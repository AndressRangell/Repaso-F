package com.flota.actividades;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.flota.defines_bancard.DefinesBANCARD;
import com.flota.inicializacion.configuracioncomercio.CARDS;
import com.flota.inicializacion.configuracioncomercio.COMERCIOS;
import com.flota.inicializacion.configuracioncomercio.Device;
import com.flota.inicializacion.configuracioncomercio.HOST;
import com.flota.inicializacion.configuracioncomercio.IPS;
import com.flota.inicializacion.configuracioncomercio.PLANES;
import com.flota.inicializacion.configuracioncomercio.TRANSACCIONES;
import com.flota.inicializacion.configuracioncomercio.TransActive;
import com.flota.inicializacion.tools.PolarisUtil;
import com.flota.inicializacion.trans_init.Init;
import com.flota.polaris_validation.ReadWriteFileMDM;
import com.flota.tools.BatteryStatus;
import com.newpos.libpay.LogType;
import com.newpos.libpay.Logger;
import com.newpos.libpay.PaySdk;
import com.newpos.libpay.utils.PAYUtils;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.desert.newpos.payui.base.PayApplication;


public class StartAppBANCARD extends AppCompatActivity {

    public static final String CERT = "CERTIFICACION   ";
    public static String VERSION = "6.6";
    public static COMERCIOS tablaComercios = null;
    public static Device tablaDevice = null;
    public static HOST tablaHost = null;
    public static IPS tablaIp = null;
    public static CARDS tablaCards = null;
    public static TRANSACCIONES tablaTransacciones = null;
    public static boolean isInit = false;
    public static boolean MODE_KIOSK = false;
    public static ArrayList<TRANSACCIONES> listadoTransacciones = null;
    public static ArrayList<IPS> listadoIps = null;
    public static ArrayList<PLANES> listadoPlanes = null;
    public static PolarisUtil polarisUtil = null;
    public static TransActive transActive;
    public static boolean intentoInicializacion = false;
    public static boolean initSeconIp = true;
    public static ReadWriteFileMDM readWriteFileMDM = null;
    //CREATEDATABASE = true -> Se usa la base de datos que se crea localmente
    //CREATEDATABASE = false -> Se usa la base de datos que se importo
    String clase = "StartAppBANCARD.java";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {//check system version ,if it is Android 5, load the so file
                String path = getFilesDir().getPath() + "/libPlatform.so";
                File file = new File(path);
                boolean fileExist = file.exists();
                Logger.error("TAGS ", "File " + path + "\n" + fileExist);
                if (!fileExist) {
                    Logger.error("StarpApp", "copy assets file to data");
                    PAYUtils.copyAssetsToData(getApplicationContext(), "libPlatform.so");  // copy the so file from folder assets to data folder
                }
                System.load(path);
            }
        } catch (SecurityException | UnsatisfiedLinkError | NullPointerException e) {
            Logger.exception( "Error al cargar la libreria" ,e);
            Logger.error("Error al cargar la libreria" ,e);
            e.printStackTrace();
        }

        initSDK();

        this.registerReceiver(BatteryStatus.getInstance(), new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        initApp();
    }

    public void kiosk() {
        //kioske mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (MODE_KIOSK)
                startLockTask();
            /*else
                stopLockTask();*/
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            this.unregisterReceiver(BatteryStatus.getInstance());
        } catch (final Exception exception) {
            exception.printStackTrace();
            Logger.exception(clase, exception);
            // The receiver was not registered.
            // There is nothing to do in that case.
            // Everything is fine.
        }

    }

    /**
     * inicializa el sdk
     */
    private void initSDK() {
        PaySdk.getInstance().setActivity(this);
        PayApplication.getInstance().addActivity(this);
        PayApplication.getInstance().setRunned();
    }


    /**
     * Inicio de la app
     */
    private void initApp() {

        polarisUtil = new PolarisUtil();
        polarisUtil.initObjetPSTIS(StartAppBANCARD.this);
        polarisUtil.leerBaseDatos(StartAppBANCARD.this);
        StringBuilder stb = new StringBuilder();
        stb.append("********|Verificar Trans Activas|********").append("\n");
        stb.append("venta: ").append(transActive.isVenta()).append("\n");
        stb.append("venta cuotas: ").append(transActive.isVentaCuotas()).append("\n");
        stb.append("venta zimple: ").append(transActive.isVentaZimple()).append("\n");
        stb.append("venta sin tarjeta: ").append(transActive.isVentaSinTarjeta()).append("\n");
        stb.append("venta credito: ").append(transActive.isVentaCreditoForzado()).append("\n");
        stb.append("venta debito: ").append(transActive.isVentaDebitoForzado()).append("\n");
        stb.append("venta cashback: ").append(transActive.isVentaCashBack()).append("\n");
        stb.append("venta minutos: ").append(transActive.isVentaMinutos()).append("\n");
        stb.append("consulta saldo: ").append(transActive.isConsultaSaldo()).append("\n");
        stb.append("*********+*********************").append("\n");
        Logger.debug(stb.toString());

        if (getFechaUltimoCierre() == null || getFechaUltimoCierre().trim().equals("")) {
            guardarFechaDeUltimoCierre();
        }

        if (!isInit) {
            if (!intentoInicializacion) {
                Logger.info("intentoInicializacion ++++ " + intentoInicializacion + " isInit ++++  " + isInit);
                Intent intent = new Intent();
                intent.setClass(StartAppBANCARD.this, Init.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent();
                intent.setClass(StartAppBANCARD.this, FalloConexionActivity.class);
                startActivity(intent);
            }
            return;

        }

        if (intentoInicializacion) {
            Logger.info("INFORMACION ------------  ");
            Intent intent = new Intent();
            intent.setClass(StartAppBANCARD.this, MainActivity.class);
            intent.putExtra(DefinesBANCARD.CASTEO_APLICACION, true);
            intent.putExtra(DefinesBANCARD.FECHA_INICIALIZACION, true);
            if (initSeconIp) {
                intent.putExtra("typesInitSeconIP", true);
                intent.setClass(StartAppBANCARD.this, Init.class);
                initSeconIp = false;
            }
            startActivity(intent);
        } else {
            Logger.info("intentoInicializacion2 ++++ " + intentoInicializacion + " isInit2 ++++  " + isInit);
            Intent intent = new Intent();
            intent.setClass(StartAppBANCARD.this, Init.class);
            startActivity(intent);
        }
    }


    @Override
    public void onBackPressed() {
        //
    }

    private String getFechaUltimoCierre() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("fecha-cierre", MODE_PRIVATE);
        return prefs.getString("fechaUltimoCierre", null);
    }

    private void guardarFechaDeUltimoCierre() {
        DateFormat hourdateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date fechaActual = new Date();
        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("fecha-cierre", MODE_PRIVATE).edit();
        editor.putString("fechaUltimoCierre", hourdateFormat.format(fechaActual));
        editor.apply();
    }
}
