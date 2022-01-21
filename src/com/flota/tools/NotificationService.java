package com.flota.tools;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.flota.actividades.MainActivity;
import com.flota.defines_bancard.DefinesBANCARD;
import com.flota.inicializacion.configuracioncomercio.ChequeoIPs;
import com.flota.inicializacion.configuracioncomercio.Device;
import com.flota.inicializacion.trans_init.trans.Tools;
import com.newpos.libpay.Logger;
import com.pos.device.net.eth.EthernetManager;
import com.wposs.flota.R;

import java.util.Arrays;
import java.util.List;

public class NotificationService extends IntentService {

    private static final int NOTIFICATION_ID = 1;
    Notification notification;
    private static final String TAG = "NotificationService.java";

    public NotificationService(String name) {
        super(name);
    }

    public NotificationService() {
        super("SERVICE");
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onHandleIntent(Intent intent2) {
        String notificationChannelId = getApplicationContext().getString(R.string.app_name);
        Context context = this.getApplicationContext();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent mIntent = new Intent(this, MainActivity.class);
        Resources res = this.getResources();
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        String message = getString(R.string.app_name);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final int NOTIFY_ID = 0; // ID of notification
            PendingIntent pending;
            NotificationCompat.Builder builder;
            NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notifManager == null) {
                notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            }
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(notificationChannelId);
            if (mChannel == null) {
                mChannel = new NotificationChannel(notificationChannelId, notificationChannelId, importance);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(context, notificationChannelId);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pending = PendingIntent.getActivity(context, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentTitle("Wifi Activado").setCategory(Notification.CATEGORY_SERVICE)
                    .setSmallIcon(R.drawable.ic_senal_wifi)  // required
                    .setContentText(message)
                    .setColorized(true)
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_icono_bancard))
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setSound(soundUri)
                    .setContentIntent(pending)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            Notification notific = builder.build();
            notifManager.notify(NOTIFY_ID, notific);

            startForeground(1, notific);

        } else {
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            notification = new NotificationCompat.Builder(this)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_senal_wifi)
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_menu_wifi_on))
                    .setColorized(true)
                    .setAutoCancel(true)
                    .setContentTitle("Wifi Activado").setCategory(Notification.CATEGORY_SERVICE)
                    .setContentText(message).build();
            notificationManager.notify(NOTIFICATION_ID, notification);
            SharedPreferences sp = getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
            String conexion = sp.getString("tipoConexion", null);
            if (conexion != null) {
                validarTipoAlarma(context, conexion);
            }
        }
    }

    private void validarTipoAlarma(Context context, String conexion) {
        Log.e(TAG, "Notificación " + "Tipo Conexion " + conexion);
        Log.e(TAG, "Llegada a validar notificacion tipo: " + conexion + " Device: " + Device.getConexion());
        switch (Device.getConexion()) {
            case DefinesBANCARD.TYPE_WIFI:
                if (conexion.equals("Wifi") && !verificacionConexion(context, ConnectivityManager.TYPE_WIFI)) {
                    UtilNetwork.removeAlarm(context);
                    if (UtilNetwork.canProccesNet()) {
                        UtilNetwork.activarWifi(context, false);
                    }
                }
                break;
            case DefinesBANCARD.TYPE_ETHERNET:
                if (conexion.equals("Lan") && !verificacionConexion(context, ConnectivityManager.TYPE_ETHERNET)) {
                    UtilNetwork.removeAlarm(context);
                    if (Tools.isEthernetFirmware() && UtilNetwork.canProccesNet()) {
                        EthernetManager.getInstance().setEtherentEnabled(true);
                    }
                }
                break;
            default:
                Log.e(TAG, "Default NotificationSrvc");
                WifiManager wifiManager = UtilNetwork.getWifiManager(context, false);
                assert wifiManager != null;
                if (wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(false);
                }
                if (Tools.isEthernetFirmware())
                    EthernetManager.getInstance().setEtherentEnabled(false);
                break;
        }
        Log.e(TAG, "salida switch ");
    }

    private boolean verificacionConexion(Context context, int tipoConexion) {
        ConnectivityManager connManager1 = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mConexion = connManager1.getNetworkInfo(tipoConexion);
        Logger.error("TAg", "Tipo De Conexion: " + tipoConexion);
        Logger.error("TAG", "Conectado por: " + mConexion.getTypeName());
        return mConexion.isConnected();
    }

    private void activarWifi() {
        WifiManager wifiManager;
        if (getApplicationContext() != null) {
            wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            wifiManager.setWifiEnabled(true);
            String tempo;
            String ssid = null;
            String clave = null;
            for (int i = 0; i < ChequeoIPs.getLengIps(); i++) {
                tempo = ChequeoIPs.seleccioneIP(i).getIdIp();
                if (tempo != null) {
                    tempo = tempo.toLowerCase();
                    Logger.info("El IP que llego " + tempo);
                    if (tempo.contains("com_wifi")) {
                        ssid = tempo;
                        clave = ChequeoIPs.seleccioneIP(i).getClaveWifi();
                    }
                }

            }
            if (ssid != null) {
                Logger.info("El SSID a probar es " + ssid);
                if (connectToNetworkWPA(ssid, clave)) {
                    Logger.info("Funciono");
                } else {
                    Logger.info("No funciono");
                }
            }
        } else {
            Logger.info("Error en el contexto");
        }

    }

    public boolean connectToNetworkWPA(String networkSSID, String clave) {
        boolean conecto = false;
        try {
            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = "\"" + networkSSID + "\""; // Please note the quotes. String should contain SSID in quotes
            conf.preSharedKey = "\"" + clave + "\"";
            conf.status = WifiConfiguration.Status.ENABLED;
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            wifiManager.addNetwork(conf);
            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            for (WifiConfiguration i : list) {
                Logger.info("SSD : " + i.SSID);
                if (i.SSID != null && i.SSID.equalsIgnoreCase("\"" + networkSSID + "\"")) {
                    wifiManager.disconnect();
                    wifiManager.enableNetwork(i.networkId, true);
                    wifiManager.reconnect();
                    Logger.info("Se conecto");
                    conecto = true;
                    break;
                }
            }
            //WiFi Connection success, return true

        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.info(Arrays.toString(ex.getStackTrace()));
            return false;
        }

        return conecto;
    }

}