package com.flota.inicializacion.configuracioncomercio;

import static com.flota.actividades.StartAppBANCARD.listadoIps;
import static com.flota.inicializacion.trans_init.Init.NAME_DB;

import android.content.Context;
import android.database.Cursor;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.flota.inicializacion.trans_init.trans.DbHelper;
import com.newpos.libpay.Logger;

import java.util.ArrayList;
import java.util.List;

public class ChequeoIPs {

    private static final String TAG = "ChequeoIPs.java";

    private ChequeoIPs() {

    }

    public static ArrayList<IPS> selectIP(Context context) {
        boolean ipNull = false;
        DbHelper databaseAccess = new DbHelper(context, NAME_DB, null, 1);
        databaseAccess.openDb(NAME_DB);

        ArrayList<IPS> aLp = new ArrayList<>();

        String sql = consultaSQL();
        try {
            Logger.info(TAG, "Consulta SQL ------ " + sql);
            Cursor cursor = databaseAccess.rawQuery(sql, null);
            cursor.moveToFirst();
            int indexColumn;
            IPS ips = null;
            while (!cursor.isAfterLast()) {
                ips = new IPS(context);
                indexColumn = 0;
                for (String s : IPS.fields) {
                    ips.addIP(s, cursor.getString(indexColumn++).trim());
                }
                ipNull = true;
                cursor.moveToNext();
                aLp.add(ips);
            }
            cursor.close();

        } catch (Exception e) {
            Logger.exception(TAG, e);
            Logger.error(TAG, e);
        }
        databaseAccess.closeDb();

        if (!ipNull)
            aLp = null;

        return aLp;
    }


    private static String consultaSQL() {
        StringBuilder sql = new StringBuilder();
        sql.append("select ");
        int counter = 1;
        for (String s : IPS.fields) {
            sql.append(s);
            if (counter++ < IPS.fields.length) {
                sql.append(",");
            }
        }
        sql.append(" FROM IPS WHERE IP_ID not like '%QR%'");

        return sql.toString();
    }

    public static IPS seleccioneIP(int posicion) {
        try {
            Logger.error(TAG, "IPS: " + listadoIps.get(posicion).getIdIp());
            return listadoIps.get(posicion);
        } catch (IndexOutOfBoundsException e) {
            Logger.exception(TAG, e);
        }
        return null;
    }

    public static int getLengIps() {
        return listadoIps.size();
    }

    public static List<Integer> getPosicionesIpsConexion3G(String name) {
        name = name.trim();
        name = name.split(" ")[0];
        name = name.toLowerCase();
        String tempo;
        List<Integer> result = new ArrayList<>();
        int cantidadIps = ChequeoIPs.getLengIps();
        for (int i = 0; i < cantidadIps; i++) {
            tempo = getIps(i);
            if (tempo != null) {
                tempo = tempo.toLowerCase();
                if (tempo.contains(name) &&
                        !tempo.equals("com_wifi") &&
                        !tempo.contains("wifi") &&
                        !tempo.equals("ip caja") &&
                        !tempo.equals("ip polaris")) {

                    result.add(i);
                }
            }
        }
        return result;
    }

    public static List<Integer> getPosicionesIps(String name) {
        name = name.trim();
        name = name.split(" ")[0];
        name = name.toLowerCase();
        String tempo;
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < ChequeoIPs.getLengIps(); i++) {
            tempo = ChequeoIPs.seleccioneIP(i).getIdIp();

            if (tempo != null) {
                tempo = tempo.toLowerCase();
                if (tempo.contains(name)) {
                    result.add(i);

                }
            }

        }

        return result;
    }

    public static List<Integer> getPosicionesWifi(String comWifi, String name, WifiManager wifiManager) {
        name = name.trim();
        name = name.split(" ")[0];
        name = name.toLowerCase();
        String tempo;
        List<Integer> result = new ArrayList<>();
        int cantidadIps = ChequeoIPs.getLengIps();
        for (int i = 0; i < cantidadIps; i++) {
            tempo = getIps(i);
            if (tempo != null) {
                tempo = tempo.toLowerCase();
                if (wifiManager.getConnectionInfo().getSSID().equals(comWifi) && tempo.contains(comWifi)) {
                    result.add(i);
                } else {
                    if (tempo.contains(name)) {
                        result.add(i);
                    }
                }
            }
        }
        return result;
    }

    public static List<Integer> getPosicionesEthernet(String name) {
        try {
            name = name.trim();
            name = name.split(" ")[0];
            name = name.toLowerCase();
            String tempo;
            List<Integer> result = new ArrayList<>();
            int cantidadIps = ChequeoIPs.getLengIps();

            for (int i = 0; i < cantidadIps; i++) {
                tempo = getIps(i);
                if (tempo != null) {
                    tempo = tempo.toLowerCase();
                    if (tempo.contains(name)) {
                        result.add(i);
                    }
                }
            }
            return result;
        } catch (Exception e) {
            Logger.exception(TAG, e);
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private static String getIps(int position) {
        Logger.error(TAG, "Legada a getIps");
        IPS ip = null;
        try {
            ip = ChequeoIPs.seleccioneIP(position);
        } catch (Exception ex) {
            Logger.error(TAG , ex);
        }
        assert ip != null;
        Logger.error(TAG, "Id Ip: " + ip.getIdIp());
        return ip.getIdIp();
    }


    public static int getPosicionIps(String name) {
        List<Integer> lista = getPosicionesIps(name);
        if (!lista.isEmpty()) {
            return lista.get(0);
        } else {
            return -1;
        }
    }
}
