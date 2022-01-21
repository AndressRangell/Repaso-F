package com.flota.tools;

import static android.content.Context.MODE_MULTI_PROCESS;
import static com.flota.actividades.StartAppBANCARD.tablaHost;
import static java.util.Calendar.getInstance;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.flota.actividades.MenusActivity;
import com.flota.inicializacion.configuracioncomercio.ChequeoIPs;
import com.flota.inicializacion.configuracioncomercio.IPS;
import com.newpos.libpay.Logger;
import com.newpos.libpay.utils.ISOUtil;
import com.wposs.flota.R;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import cn.desert.newpos.payui.UIUtils;

/**
 * https://stackoverflow.com/questions/6064510/how-to-get-ip-address-of-the-device-from-code
 */

public class UtilNetwork {

    private static final String TAG = "UtilNetwork.java";

    private UtilNetwork() {

    }

    /**
     * Convert byte array to hex string
     *
     * @param bytes toConvert
     * @return hexValue
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sbuf = new StringBuilder();
        for (byte aByte : bytes) {
            int intVal = aByte & 0xff;
            if (intVal < 0x10) sbuf.append("0");
            sbuf.append(Integer.toHexString(intVal).toUpperCase());
        }
        return sbuf.toString();
    }

    /**
     * Load UTF8withBOM or any ansi text file.
     *
     * @param filename which to be converted to string
     * @return String value of File
     * @throws java.io.IOException if error occurs
     */
    public static String loadFileAsString(String filename) throws java.io.IOException {
        final int BUFLEN = 1024;
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(filename), BUFLEN);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(BUFLEN);
            byte[] bytes = new byte[BUFLEN];
            boolean isUTF8 = false;
            int read;
            int count = 0;
            while ((read = is.read(bytes)) != -1) {
                if (count == 0 && bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF) {
                    isUTF8 = true;
                    baos.write(bytes, 3, read - 3); // drop UTF8 bom marker
                } else {
                    baos.write(bytes, 0, read);
                }
                count += read;
            }
            return isUTF8 ? new String(baos.toByteArray(), StandardCharsets.UTF_8) : baos.toString();
        } finally {
            try {
                is.close();
            } catch (Exception e) {
                Logger.exception(TAG, e);
            }
        }
    }

    /**
     * Returns MAC address of the given interface name.
     *
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * @return mac address or empty string
     */
    public static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null && !intf.getName().equalsIgnoreCase(interfaceName)) {
                    continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac == null) return "";
                StringBuilder buf = new StringBuilder();
                for (byte aMac : mac) buf.append(String.format("%02X:", aMac));
                if (buf.length() > 0) buf.deleteCharAt(buf.length() - 1);
                return buf.toString();
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
            Logger.exception(TAG, ignored);
        } // for now eat exceptions
        return "";
    }

    /**
     * Get IP address from first non-localhost interface
     *
     * @param useIPv4 true=return ipv4, false=return ipv6
     * @return address or empty string
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
            Logger.exception(TAG, ignored);
        } // for now eat exceptions
        return "";
    }

    public static String[] showDhcpData(Context context) {

        String[] datos = new String[4];

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();

        datos[0] = intToIp(dhcpInfo.netmask);
        datos[1] = intToIp(dhcpInfo.dns1);
        datos[2] = intToIp(dhcpInfo.dns2);
        datos[3] = intToIp(dhcpInfo.gateway);

        return datos;

    }

    public static String intToIp(int addr) {
        return ((addr & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF));
    }

    public static String getMask(String ip) {

        String mask = "";

        int type = Integer.parseInt(ip.substring(0, ip.indexOf(".")));

        if (type >= 0 && type <= 127) {//class A
            mask = "255.0.0.0";
        } else if (type >= 128 && type <= 191) {//class B
            mask = "255.255.0.0";
        } else if (type >= 192 && type <= 223) { //class C
            mask = "255.255.255.0";
        } else {
            mask = "0.0.0.0";
        }

        return mask;

    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getImei(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager)
                    context.getSystemService(Context.TELEPHONY_SERVICE);

            return telephonyManager.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.exception(TAG, e);
            return "NA";
        }
    }

    public static String getIpFull(Context context) {
        String result = "";
        String[] s = IPS.getIPAddress(true).replace(".", "-").split("-");
        if (s.length != 4) {
            return "Erro en ip";
        }
        for (int i = 0; i < s.length; i++) {
            s[i] = ISOUtil.padleft(s[i], 3, '0');
        }
        for (String r : s) {
            result += r;
        }
        return result;
    }

    /**
     * @param context  contexto de la que invoca el metodo
     * @param showMsg  boolean, indicando si se muestra alerta tipo Toast.
     * @return en caso que no se pueda instanciar el WifiManager o ya este activa la
     * conexion por wifi retornara null,
     * en caso contrario activara el Wifi y retornara este mismo
     */
    public static WifiManager getWifiManager(Context context, final boolean showMsg) {
        WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (manager == null) {
            android.util.Log.e(TAG, "Fail to get WifiManager");
            return null;
        } else if (manager.isWifiEnabled()) {
            android.util.Log.e(TAG, "Wifi already On");
            return manager;
        }

        if (showMsg) {
            UIUtils.toast((Activity) context, R.drawable.logoinfonet02, "Activando Wifi..", Toast.LENGTH_SHORT);
        }
        manager.setWifiEnabled(true);
        boolean isActive = false;
        int cont = 1;
        long t = System.currentTimeMillis();
        long totalTime = t + 3000;
        while (System.currentTimeMillis() < totalTime) {
            android.util.Log.e(TAG, "waiting for wifi-system to provide network-data " + (cont++) + " " + WifiManager.WIFI_STATE_ENABLED + " " + manager.getWifiState());
            if (manager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
                isActive = true;
                Log.e(TAG, "Done");
                break;
            }
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
                isActive=false;
                break;
            }
        }

        return isActive ? manager : null;
    }

    /**
     * @param tipoConexion String con el nombre del medio principal (wifi/Lan)
     * @param context contexto de la que invoca el metodo
     *                crea un SharedPreferences para almacenar la alarma y
     *                en caso reiciciar el Pos, si no se ha culminao continuarla
     */
    public static void activarAlarma(String tipoConexion, Context context) {
        if (context == null) {
            return;
        }
        Log.e(TAG, "add-alm");
        SharedPreferences settings;
        settings = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        Calendar mcurrentTime = getInstance();
        long horaMilisecSistema = mcurrentTime.getTimeInMillis();
        int minutosPolaris = Integer.parseInt(tablaHost.getTiempoConexion3G());
        long tiempoConexion = (minutosPolaris * 60000);
        horaMilisecSistema = horaMilisecSistema + tiempoConexion;

        //SAVE ALARM TIME TO USE IT IN CASE OF REBOOT
        int alarmID = 1;
        SharedPreferences.Editor edit = settings.edit();
        edit.putInt("alarmID", alarmID);
        edit.putLong("alarmTime", horaMilisecSistema);
        edit.putString("tipoConexion", tipoConexion);
        edit.apply();
        ISOUtil.setAlarm(alarmID, horaMilisecSistema, context);
    }

    /**
     * @param context contexto de la que invoca el metodo
     * Limpia el SharedPreferences de la alarma, cuando esta se complete
     */
    public static void removeAlarm(Context context) {
        Log.e(TAG, "remove-alm");
        RebootServiceClass.setAlarma(false);
        SharedPreferences settings = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        settings.edit().clear().apply();
    }

    /**
     * @return Boolean indicando si se puede proceder a activar el medio principal
     *          Para esto valida:
     *          1- Si el usuario desactivo manualmete el medio
     *          2- Si la alarma se encuentra activa
     */
    public static boolean canProccesNet() {
        Log.e(TAG, "canProccesNet: mOff- " + MenusActivity.isManualOff());
        if (MenusActivity.isManualOff()) {
            return false;
        }
        Log.e(TAG, "canProccesNet: alm- " + RebootServiceClass.isAlarma());
        return !RebootServiceClass.isAlarma();
    }

    /**
     * @param context contexto de la que invoca el metodo
     * @param showMsg boolean, indicando si se muestra alerta tipo Toast.
     *                1- Procede a activar la conexion Wifi.
     *                2-Se recorre la lista de ips, almacenadas en el Pos en busca de la ip COM_WIFI
     *                Si la encuentra, procedera a conectarse a esta.
     */
    public static void activarWifi(Context context, boolean showMsg) {
        Log.e(TAG, "Llegada a activar wifi - showmsg " + showMsg);
        if (context == null) {
            return;
        }
        String temp;
        String nombreRed = null;
        String claveRed = null;

        for (int i = 0; i < ChequeoIPs.getLengIps(); i++) {
            IPS ips = ChequeoIPs.seleccioneIP(i);
            if (ips != null) {
                temp = ips.getIdIp();
                if (temp != null) {
                    if (temp.contains("com_wifi")) {
                        nombreRed = temp;
                        claveRed = ips.getClaveWifi();
                        validatePreferences(temp, claveRed, context);
                    }
                }
            }
        }
        Log.e(TAG, "Check-Dt - netName: " + nombreRed + " netPss: " + claveRed);
        boolean connected = false;
        if (nombreRed != null && !nombreRed.equals("") && claveRed != null && !claveRed.equals("")) {
            connected = ConectarseRedWifi.conectarRed(nombreRed, claveRed, context, showMsg);
        }else{
            UtilNetwork.getWifiManager(context,showMsg);
        }
        Log.e(TAG, "Can connect to Network?= " + connected);
    }


    /**
     * @param nombreRed SSID de la red (COM_WIFI)
     * @param claveRed Password traida desde plataforma (COM_WIFI)
     * @param context contexto de la que invoca el metodo
     *     Se valida el nombre y password de la red almacenados, para en caso los datos
     *     sean diferentes, olvidar dicha red, y agregarla nuevamente con la nueva password
     */
    private static void validatePreferences(String nombreRed, String claveRed, Context context) {
        SharedPreferences prefs = context.getSharedPreferences("InformacionRed", MODE_MULTI_PROCESS);
        String nameRed = prefs.getString("nombreRed", "NA");
        String passRed = prefs.getString("claveRed", "NA");
        Log.e(TAG, "AllData\nnameRed: " + nameRed + "  passRes: " + passRed + "\nnombreRed: " + nombreRed + "  claveRed: " + claveRed);
        if (nameRed.equals("") && passRed.equals("")) {
            return;
        }

        //Borrar y volver a guardar red.
        if (!nameRed.equalsIgnoreCase(nombreRed) || !passRed.equals(claveRed)) {
            Log.e(TAG, "No coinciden los datos con las preferencias");
            Log.e(TAG, "Contraseña de polaris y local no coinciden");
            removeNetwork(nombreRed, context);
        }
    }

    /**
     * @param ssid SSID de la red (COM_WIFI)
     * @param context contexto de la que invoca el metodo
     *     Metodo encargado de eliminar un SSID especifico de la lista de redes configuradas
     */
    public static void removeNetwork(final String ssid, Context context) {
        WifiManager manager = UtilNetwork.getWifiManager(context, false);
        if (manager != null && ssid != null) {
            List<WifiConfiguration> list = manager.getConfiguredNetworks();
            for (WifiConfiguration net : list) {
                if (net.SSID.contains(ssid)) {
                    manager.disconnect();
                    Log.e(TAG, "removeNetwork: ssid:" + ssid);
                    manager.removeNetwork(net.networkId);
                    return;
                }
            }
        } else {
            Log.e(TAG, "removeNetwork: ssid:" + ssid + " no encontrado o vacío");
        }
    }
}
