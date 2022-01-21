package com.flota.inicializacion.trans_init.trans;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.google.common.base.Strings;
import com.newpos.libpay.Logger;
import com.pos.device.config.DevConfig;
import com.wposs.flota.BuildConfig;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * Created by Julian on 7/06/2018.
 */

public class Tools {

    private static Context ctx;
    private static final String TAG = "Tools.java";
    private static final String ETHERNET_FIRMWARE = "3.2.32";//MINIMUN ETHERNET FIRMWARE VERSION

    private Tools(){

    }

    public static Context getCurrentContext() {
        return ctx;
    }

    public static void setCurrentContext(Context context) {
        ctx = context;
    }

    public static String timeStr() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int hr = cal.get(Calendar.HOUR);
        int mi = cal.get(Calendar.MINUTE);
        int se = cal.get(Calendar.SECOND);
        String hrC;
        String miC;
        String seC;

        ///Hora
        if (cal.get(Calendar.AM_PM) == Calendar.AM) {
            if (hr == 0)
                hr = 12;  ///Noon

            if (hr < 10) {
                hrC = "0" + hr;
            } else {
                hrC = String.valueOf(hr);
            }
        } else {
            hrC = String.valueOf(hr + 12); ///Se agregó para pointpay un + 1
        }

        ///Minuto
        if (mi < 10) {
            miC = "0" + mi;
        } else {
            miC = String.valueOf(mi);
        }

        ///Segundo
        if (se < 10) {
            seC = "0" + se;
        } else {
            seC = String.valueOf(se);
        }

        return hrC + miC + seC;
    }

    public static String dateStr() {
        Calendar cal = Calendar.getInstance();
        int mm = cal.get(Calendar.MONTH) + 1;
        int dd = cal.get(Calendar.DAY_OF_MONTH);
        String mmC;
        String ddC;

        ///Mes
        if (mm < 10) {
            mmC = "0" + mm;
        } else {
            mmC = String.valueOf(mm);
        }

        ///Dia
        if (dd < 10) {
            ddC = "0" + dd;
        } else {
            ddC = String.valueOf(dd);
        }
        return mmC + ddC;
    }

    public static String convert2YYYYMMDDStr(String date) {
        String mm;
        String dd;
        String yyyy;

        StringTokenizer tokens = new StringTokenizer(date, "/");
        dd = tokens.nextElement().toString();
        mm = tokens.nextElement().toString();
        yyyy = tokens.nextElement().toString();
        return yyyy + mm + dd;

    }

    public static String dateYYYYMMDDStr() {
        Calendar cal = Calendar.getInstance();
        int mm = cal.get(Calendar.MONTH) + 1;
        int dd = cal.get(Calendar.DAY_OF_MONTH);
        int yyyy = cal.get(Calendar.YEAR);
        String mmC;
        String ddC;
        String yyyyC;

        ///Mes
        if (mm < 10) {
            mmC = "0" + mm;
        } else {
            mmC = String.valueOf(mm);
        }

        ///Dia
        if (dd < 10) {
            ddC = "0" + dd;
        } else {
            ddC = String.valueOf(dd);
        }

        //Año
        if (yyyy < 200)
            yyyy += 1900;

        yyyyC = String.valueOf(yyyy);

        return yyyyC + mmC + ddC;
    }

    public static int bcdToInt(byte bcdValue) {
        StringBuilder sb = new StringBuilder();

        byte high = (byte) (bcdValue & 0xf0);
        high >>>= (byte) 4;
        high = (byte) (high & 0x0f);
        byte low = (byte) (bcdValue & 0x0f);

        sb.append(high);
        sb.append(low);

        return Integer.parseInt(sb.toString());
    }

    public static String padLeft(String text, char pad, int len) {
        String aux;
        aux = Strings.padStart(text, len, pad);
        return aux;
    }

    public static String padRight(String text, char pad, int len) {
        String aux;
        aux = Strings.padEnd(text, len, pad);
        return aux;
    }

    public static String centerString(String s, int size, char pad) {
        if (s == null || size <= s.length())
            return s;

        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < (size - s.length()) / 2; i++) {
            sb.append(pad);
        }
        sb.append(s);
        while (sb.length() < size) {
            sb.append(pad);
        }
        return sb.toString();
    }

    public static String getVersion() {
        return BuildConfig.VERSION_NAME;
    }

    public static String mesAlphaHaiti(int mes) {

        switch (mes) {


            case 1:
                return ("Jan");
            case 2:
                return ("Fev");
            case 3:
                return ("Mar");
            case 4:
                return ("Avr");
            case 5:
                return ("Mai");
            case 6:
                return ("Jui");
            case 7:
                return ("Jul");
            case 8:
                return ("Aou");
            case 9:
                return ("Sep");
            case 10:
                return ("Oct");
            case 11:
                return ("Nov");
            case 12:
                return ("Dec");
            default:
                return null;


        }
    }

    /**
     * Convert a array of byte to hex String. <br/>
     * Each byte is covert a two character of hex String. That is <br/>
     * if byte of int is less than 16, then the hex String will append <br/>
     * a character of '0'.
     *
     * @param bytes array of byte
     * @return hex String represent the array of byte
     */
    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            int value = b & 0xFF;
            if (value < 16) {
                // if value less than 16, then it's hex String will be only
                // one character, so we need to append a character of '0'
                sb.append("0");
            }
            sb.append(Integer.toHexString(value).toUpperCase());
        }
        return sb.toString();
    }

    /**
     * Compute the SHA-1 hash of the given byte array
     *
     * @param hashThis byte[]
     * @return byte[]
     */
    public static String hashSha1(byte[] hashThis) {
        try {
            byte[] hash = new byte[20];
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            hash = md.digest(hashThis);
            return bytesToHexString(hash);
        } catch (NoSuchAlgorithmException e) {
            Logger.exception(TAG, e);
            Logger.error(TAG, "SHA-1 algorithm is not available...");
            System.exit(2);
        }
        return null;
    }

    public static void saveHash(String hash, Context context) {
        DbHelper db = new DbHelper(context, "hash", null, 1);
        db.openDb("hash");
        db.execSql("DELETE FROM HASH;");
        db.execSql("INSERT INTO HASH VALUES ('" + hash + "');");
        db.closeDb();
    }

    public static String readHash(Context context) {
        String sha1 = "";
        DbHelper db = new DbHelper(context, "hash", null, 1);
        db.openDb("hash");

        Cursor c = db.rawQuery("SELECT SHA1 FROM HASH");

        if (c.moveToFirst()) {
            do {
                sha1 = c.getString(0);
            } while (c.moveToNext());
        }
        db.closeDb();

        return sha1;
    }

    public static String insertPeriodically(String text, String insert, int period) {
        StringBuilder builder = new StringBuilder(text.length() + insert.length() * (text.length() / period) + 1);
        int index = 0;
        String prefix = "";
        while (index < text.length()) {
            builder.append(prefix);
            prefix = insert;
            builder.append(text.substring(index, Math.min(index + period, text.length())));
            index += period;
        }
        return builder.toString();
    }

    public static boolean isEthernetFirmware() {
        boolean res = false;
        String[] firmware = DevConfig.getFirmwareVersion().split("\\.");
        String[] validFirmware = ETHERNET_FIRMWARE.split("\\.");
        Logger.error(TAG, "isEthernetFirmware: " + Arrays.toString(firmware) + " " + Arrays.toString(validFirmware));
        try {
            for (int i = 0; i < 3; i++) {
                int actualFirware = Integer.parseInt(firmware[i]);
                int etheFirware = Integer.parseInt(validFirmware[i]);
                Logger.error(TAG, "actualFrm " + actualFirware + " etheFirmware " + etheFirware);
                if (actualFirware > etheFirware) {
                    res = true;
                    break;
                }
            }
        } catch (NumberFormatException e) {
            Logger.error("isEthernetFirmware: ", e);
            Logger.exception(TAG, e);
            res = false;
        }
        Logger.error(TAG, "res " + res);
        return res;
    }

    /**
     * @return Serial Of Terminal
     */
    public static String getSerial() {
        //To Simulate serial- Change the return by the serial to simulate
        return DevConfig.getSN();
    }
}
