package com.flota.tools;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.newpos.libpay.Logger;
import com.wposs.flota.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.desert.newpos.payui.UIUtils;

public class ConectarseRedWifi extends AppCompatActivity {
    private static final String TAG = "UtilNetwork.java";

    public static boolean conectarRed(final String networkSSID, String claveRed, final Context context, boolean showMsg) {
        Logger.flujo(TAG, "llegada a conectarRed-1");
        final WifiConfiguration conf = getWifiConfiguration(networkSSID, claveRed);
        boolean conecto = false;
        try {
            WifiManager wifiManager = UtilNetwork.getWifiManager(context, showMsg);
            if (wifiManager == null) {
                return false;
            }

            if (showMsg && !wifiManager.isWifiEnabled()) {
                UIUtils.toast((Activity) context, R.drawable.logoinfonet02, "Conectando a la red", Toast.LENGTH_LONG);
            }
            wifiManager.addNetwork(conf);

            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            Collections.sort(list, new CustomComparator());
            android.util.Log.e(TAG, "list: " + list.size());

            for (WifiConfiguration i : list) {
                android.util.Log.e(TAG, "SSD : " + i.SSID + "  <>  \"" + networkSSID + "\"");

                if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                    android.util.Log.e(TAG, "If ssid superado");
                    wifiManager.disconnect();
                    wifiManager.enableNetwork(i.networkId, true);
                    wifiManager.reconnect();

                    Log.e(TAG, "Se conecto");
                    if (showMsg) {
                        UIUtils.toast((Activity) context, R.drawable.logoinfonet02, "Se ha conectando a la Red", Toast.LENGTH_SHORT);
                    }
                    savePreferences(networkSSID, claveRed, context);
                    conecto = true;
                    break;
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "ConectarseRedWifi.java");
            Log.e(TAG, "ConectarseRedWifi.java");
            e.printStackTrace();
        }
        return conecto;
    }

    private static WifiConfiguration getWifiConfiguration(String networkSSID, String password) {
        WifiConfiguration conf = new WifiConfiguration();
        conf.hiddenSSID = true;
        conf.SSID = "\"" + networkSSID + "\"";
        conf.preSharedKey = "\"" + password + "\"";
        conf.status = WifiConfiguration.Status.ENABLED;
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        return conf;
    }

    private static void savePreferences(String nombreRed, String claveRed, final Context context) {
        SharedPreferences.Editor editor = context.getApplicationContext()
                .getSharedPreferences("InformacionRed", MODE_PRIVATE).edit();
        editor.putString("nombreRed", nombreRed);
        editor.putString("claveRed", claveRed);
        editor.apply();
    }

    private static class CustomComparator implements Comparator<WifiConfiguration> {
        @Override
        public int compare(WifiConfiguration wifi1, WifiConfiguration wifi2) {
            return Integer.compare(wifi1.networkId, wifi2.networkId);
        }
    }
}
