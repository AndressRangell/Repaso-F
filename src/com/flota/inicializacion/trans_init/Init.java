package com.flota.inicializacion.trans_init;

import static com.flota.actividades.StartAppBANCARD.isInit;
import static com.flota.actividades.StartAppBANCARD.listadoIps;
import static com.flota.actividades.StartAppBANCARD.tablaComercios;
import static com.flota.defines_bancard.DefinesBANCARD.CAKEY;
import static com.flota.defines_bancard.DefinesBANCARD.ENTRY_POINT;
import static com.flota.defines_bancard.DefinesBANCARD.NAME_FOLDER_CTL_FILES;
import static com.flota.defines_bancard.DefinesBANCARD.PROCESSING;
import static com.flota.defines_bancard.DefinesBANCARD.REVOK;
import static com.flota.defines_bancard.DefinesBANCARD.TERMINAL;
import static com.flota.menus.MenuAction.callBackSeatle;
import static com.flota.transactions.common.CommonFunctionalities.saveDateSettle;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.flota.PermissionActivity;
import com.flota.actividades.FalloConexionActivity;
import com.flota.actividades.MainActivity;
import com.flota.actividades.StartAppBANCARD;
import com.flota.basedatos.migracion.MigraccionBaseDatos;
import com.flota.inicializacion.configuracioncomercio.ChequeoIPs;
import com.flota.inicializacion.configuracioncomercio.Device;
import com.flota.inicializacion.configuracioncomercio.IPS;
import com.flota.inicializacion.tools.PolarisUtil;
import com.flota.inicializacion.trans_init.trans.DbHelper;
import com.flota.inicializacion.trans_init.trans.ISO;
import com.flota.inicializacion.trans_init.trans.SendRcvd;
import com.flota.inicializacion.trans_init.trans.Tools;
import com.flota.inicializacion.trans_init.trans.UnpackFile;
import com.flota.tools.PermissionStatus;
import com.flota.tools.VerificadorConexion;
import com.flota.transactions.callbacks.waitInitCallback;
import com.newpos.libpay.Logger;
import com.newpos.libpay.global.TMConfig;
import com.newpos.libpay.utils.ISOUtil;
import com.pos.device.SDKException;
import com.pos.device.beeper.Beeper;
import com.wposs.flota.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.master.FormularioActivity;

public class Init extends FormularioActivity {

    public static final int INIT_TOTAL = 1;
    public static final int INIT_PARCIAL = 2;
    public static final String DEFAULT_DOWNLOAD_PATH = Environment.getExternalStorageDirectory() + File.separator + "bancard" + File.separator + "DB_CR";
    public static final String NAME_DB = "init";
    public static final String APLICACIONES = "APLICACIONES";
    public static final String CAPKS = "capks";
    public static final String CARDS = "CARDS";
    public static final String COMERCIOS = "COMERCIOS";
    public static final String DEVICE = "DEVICE";
    public static final String HOST = "HOST";
    public static final String IPS = "IPS";
    public static final String PLANES = "PLANES";
    public static final String RED = "RED";
    public static final String SUCURSAL = "SUCURSAL";
    public static final String TRANSACCIONES = "TRANSACCIONES";
    public static final String BILLETERAS = "BILLETERAS";
    public static final String EMVAPPS = "emvapps";
    public static final String emvappsdebug = "emvappsdebug";
    public static final String tareas = "tareas";
    private static final String TAG = "Init.java";
    public static waitInitCallback callBackInit;
    public static String gFileName;
    public static String gTID;
    public static String gOffset;
    private static String nii;
    public boolean isParcial = false;
    public int tipoInit;
    TextView txt;
    TextView tvTitle;
    PermissionStatus permissionStatus;
    boolean type = false;
    private String ip;
    private String puerto;
    private int espera;
    private String tid;

    private static String getNameFileCTL(int id) {
        String ret = "";
        switch (id) {
            case 1:
                ret = ENTRY_POINT;
                break;
            case 2:
                ret = PROCESSING;
                break;
            case 3:
                ret = REVOK;
                break;
            case 4:
                ret = TERMINAL;
                break;
            case 5:
                ret = CAKEY;
                break;
        }
        return ret;
    }

    private static String getNameTableById(int id) {
        String ret = "";
        switch (id) {
            case 1:
                ret = APLICACIONES;
                break;
            case 2:
                ret = CAPKS;
                break;
            case 3:
                ret = CARDS;
                break;
            case 4:
                ret = COMERCIOS;
                break;
            case 5:
                ret = DEVICE;
                break;
            case 6:
                ret = HOST;
                break;
            case 7:
                ret = IPS;
                break;
            case 8:
                ret = PLANES;
                break;
            case 9:
                ret = RED;
                break;
            case 10:
                ret = SUCURSAL;
                break;
            case 11:
                ret = TRANSACCIONES;
                break;
            case 12:
                ret = EMVAPPS;
                break;
            case 13:
                ret = emvappsdebug;
                break;
            case 14:
                ret = BILLETERAS;
                break;
            case 15:
                ret = tareas;
                break;

            default:
                break;
        }
        return ret;
    }

    public String getTid() {
        return tid;
    }

    public void setTID() {
        String serial = "";
        try {
            serial = Tools.getSerial();
        } catch (Exception e) {
            Logger.exception(TAG, e);
            Logger.debug("Error serial -", e.getMessage());
        }
        this.tid = serial;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        eliminarCache(getApplicationContext(), TAG);
        Logger.comunicacion(TAG, "Eliminando Directorio: " + new File(DEFAULT_DOWNLOAD_PATH).getAbsolutePath());
        eliminarDatos(new File(DEFAULT_DOWNLOAD_PATH), TAG);
        Logger.comunicacion(TAG, "Directorio Eliminado >>" + !new File(DEFAULT_DOWNLOAD_PATH).exists());

        permissionStatus = new PermissionStatus(Init.this, this);
        if (!permissionStatus.validatePermissions()) {
            Intent intent = new Intent();
            intent.setClass(Init.this, PermissionActivity.class);
            startActivity(intent);
        } else {
            if (getIntent().hasExtra("typesInitSeconIP")) {
                type = getIntent().getBooleanExtra("typesInitSeconIP", false);
            }
            txt = findViewById(R.id.output);

            isParcial = getIntent().getBooleanExtra("PARCIAL", false);
            tipoInit = isParcial ? INIT_PARCIAL : INIT_TOTAL;

            tvTitle = findViewById(R.id.textView_titleToolbar);
            tvTitle.setText(Html.fromHtml("<h4> INICIALIZACIÓN POLARIS </h4>"));
            callBackSeatle = null;

            download();
            mostrarSerialvsVersion();
        }
    }

    private void mostrarSerialvsVersion() {
        TextView tvVersion = findViewById(R.id.tvVersion);
        TextView tvSerial = findViewById(R.id.tvSerial);
        showVersionSerial(tvVersion, tvSerial);
    }


    @Override
    public void onBackPressed() {
        //Método no implementado
    }


    private void init(String tipoIP, String port) {
        setTID();
        gFileName = tid + ".zip";
        gTID = tid;
        gOffset = "0";
        this.ip = tipoIP;
        this.puerto = port;
        nii = getResources().getString(R.string.niiConfig);
        this.espera = Integer.parseInt(getResources().getString(R.string.timerConfig));
    }

    private void download() {
        new MigraccionBaseDatos(this, new MigraccionBaseDatos.ProcederMigracion() {
            @Override
            public void rspProcesoTerminadoMigracion(boolean isContinuarIncializacion) {
                new VerificadorConexion(Init.this, new VerificadorConexion.ProcederConexion() {
                    @Override
                    public void rspProcesoExitosoWifi(String mensajeExitoso) {
                        ip = getResources().getString(R.string.ipConfigWifi);
                        puerto = getResources().getString(R.string.portConfig);

                        ISOUtil.showMensaje(mensajeExitoso, Init.this);
                        if (type) {
                            int posicionRed = obtenerPolaris("polarisDNS");
                            com.flota.inicializacion.configuracioncomercio.IPS selectIp = ChequeoIPs.seleccioneIP(posicionRed);
                            if (selectIp != null) {
                                ip = selectIp.getIp();
                                puerto = selectIp.getPuerto();
                            }
                        }

                        init(ip, puerto);
                        onlineTrans();
                    }

                    @Override
                    public void rspProcesoExitoso3G(String mensajeExitoso) {
                        ISOUtil.showMensaje(mensajeExitoso, Init.this);
                        ip = getResources().getString(R.string.ipConfig3G);
                        puerto = getResources().getString(R.string.portConfig);

                        if (type) {
                            int posicionRed = obtenerPolaris("polaris3G");
                            com.flota.inicializacion.configuracioncomercio.IPS selectIp = ChequeoIPs.seleccioneIP(posicionRed);
                            if (selectIp != null) {
                                ip = selectIp.getIp();
                                puerto = selectIp.getPuerto();
                            }

                        }

                        init(ip, puerto);
                        onlineTrans();
                    }

                    @Override
                    public void rspProcesoExitosoEthernet(String mensajeExitoso) {
                        ISOUtil.showMensaje(mensajeExitoso, Init.this);
                        ip = getResources().getString(R.string.ipConfigWifi);
                        puerto = getResources().getString(R.string.portConfig);

                        if (type) {
                            int posicionRed = obtenerPolaris("polarisDNS");
                            com.flota.inicializacion.configuracioncomercio.IPS selectIp = ChequeoIPs.seleccioneIP(posicionRed);
                            if (selectIp != null) {
                                ip = selectIp.getIp();
                                puerto = selectIp.getPuerto();
                            }
                        }

                        init(ip, puerto);
                        onlineTrans();

                    }

                    @Override
                    public void rspProcesoFallido(String mensajeError) {
                        //Validar que la bd tenga info -true >main -false > FalloConexionActivity
                        ISOUtil.showMensaje(mensajeError, Init.this);
                        Intent intent = new Intent();
                        String msg;
                        if (PolarisUtil.isInitPolaris(Init.this)) {
                            intent.setClass(Init.this, MainActivity.class);
                            msg = "Base de datos correcta";
                        } else {
                            msg = "Base de datos incorrecta";
                            intent.setClass(Init.this, FalloConexionActivity.class);
                        }
                        Logger.error(TAG, "Fallo en la inicializacion: " + msg);
                        startActivity(intent);
                    }
                }).execute();
            }
        }).execute();

    }

    private int obtenerPolaris(String tipoConexion) {
        int posicion = -1;
        posicion = ChequeoIPs.getPosicionIps(tipoConexion);
        if (posicion != -1) {
            IPS ips = ChequeoIPs.seleccioneIP(posicion);
            if (!ips.getIp().equals(TMConfig.getInstance().getIp()) || !ips.getPuerto().equals(TMConfig.getInstance().getPort())) {
                return posicion;
            }
        }
        return posicion;
    }

    private int onlineTrans() {

        final byte[] dataVacia = new byte[]{};

        SendRcvd sendTrans = new SendRcvd(ip, Integer.parseInt(puerto), espera, Init.this);

        sendTrans.setFileName(gFileName);
        sendTrans.setNii(nii);
        sendTrans.setTid(gTID);
        sendTrans.setOffset(gOffset);
        sendTrans.setPathDefault(DEFAULT_DOWNLOAD_PATH);
        sendTrans.setTramaQueEnvia(tipoInit);

        if (!type) {
            sendTrans.setWithMensaje(false);
        }

        sendTrans.callbackResponse(new SendRcvd.TcpCallback() {

            @Override
            public void rspHost(byte[] rxBuf, String resultOk) {
                if (rxBuf == null || Arrays.equals(rxBuf, dataVacia)) {
                    showMensaje("ERROR, INICIALIZACIÓN FALLIDA", false);
                    finalizarActividad();
                    return;
                }

                if (!resultOk.equals("OK")) {
                    if (!resultOk.contains("ERROR DESCONOCIDO")) {
                        UIUtils.toast(Init.this, R.drawable.redinfonet, resultOk, Toast.LENGTH_SHORT);
                    }
                    finalizarActividad();
                    return;
                }

                ISO rspIso = new ISO(rxBuf, ISO.LENGHT_NOT_INCLUDE, ISO.TPDU_INCLUDE);

                if (rspIso.getField(ISO.FIELD_03_PROCESSING_CODE).equals("310100")) {
                    showMensaje(rspIso.getField(ISO.FIELD_60_RESERVED_PRIVATE), false);

                    callBackInit = null;

                    txt.setText(R.string.label_init_process);

                    if (processFile()) {

                        callBackInit = new waitInitCallback() {
                            @Override
                            public void getRspInitCallback(int status) {
                                try {
                                    Logger.flujo(TAG, "********|Verificar " +
                                            "Inicializacion en Init |********");
                                    isInit = PolarisUtil.isInitPolaris(Init.this);
                                    if (isInit) {
                                        if (Device.selectTConfig(Init.this) &&
                                                tablaComercios.selectComercios(Init.this) &&
                                                StartAppBANCARD.tablaHost.selectHostConfi(Init.this) &&
                                                cargarListadoIps()) {
                                            saveDateSettle(Init.this);
                                            Beeper.getInstance().beep();
                                            showMensaje("INICIALIZACIÓN EXITOSA", true);

                                            TMConfig.getInstance().setIp(ip);
                                            TMConfig.getInstance().setPort(puerto);
                                            finalizarActividad();
                                        }
                                    } else {
                                        showMensaje("INICIALIZACIÓN FALLIDA", false);
                                        finalizarActividad();
                                    }

                                } catch (SDKException e) {
                                    Logger.exception(TAG, e);
                                }
                            }
                        };
                    } else {
                        finalizarActividad();
                    }
                } else if (rspIso.getField(ISO.FIELD_03_PROCESSING_CODE).equals("960080") &&
                        processFile()) {
                    callBackInit = null;
                    callBackInit = new waitInitCallback() {
                        @Override
                        public void getRspInitCallback(int status) {
                            finalizarActividad();
                        }
                    };
                }
            }
        });

        sendTrans.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return 0;
    }


    private boolean cargarListadoIps() {
        listadoIps = ChequeoIPs.selectIP(Init.this);
        if (listadoIps == null) {
            listadoIps = new ArrayList<>();
            listadoIps.clear();
            isInit = false;
            showMensaje("Error al leer tabla, Por favor Inicialice nuevamente", false);
            finalizarActividad();
            return false;
        } else if (listadoIps.isEmpty()) {
            isInit = false;
            showMensaje("Error al leer tabla, Por favor Inicialice nuevamente", false);
            finalizarActividad();
            return false;
        }
        return true;
    }


    private void showMensaje(String mensaje, boolean isok) {
        if (isok) {
            StartAppBANCARD.initSeconIp = false;
        }
        if (!type) {
            UIUtils.toast(Init.this, R.drawable.redinfonet, mensaje, Toast.LENGTH_SHORT);
        }
    }

    private void finalizarActividad() {
        StartAppBANCARD.intentoInicializacion = true;
        startActivity(new Intent(this, StartAppBANCARD.class));
        finish();
    }

    public boolean processFile() {
        int readBlockSize = 65000 * 2;
        File file = new File(DEFAULT_DOWNLOAD_PATH + File.separator + gFileName + "T");
        if (!file.exists()) {
            file = new File(DEFAULT_DOWNLOAD_PATH + File.separator + gFileName);
        }
        if (file.exists()) {
            file.renameTo(new File(DEFAULT_DOWNLOAD_PATH + File.separator + gFileName));
            if (gFileName.endsWith(".txt")) {
                try {
                    FileInputStream fileIn = new FileInputStream(new File(DEFAULT_DOWNLOAD_PATH + File.separator + gFileName));
                    InputStreamReader inputRead = new InputStreamReader(fileIn, StandardCharsets.ISO_8859_1);

                    char[] inputBuffer = new char[readBlockSize];
                    StringBuilder s = new StringBuilder();
                    int charRead;

                    while ((charRead = inputRead.read(inputBuffer)) > 0) {
                        // char to string conversion
                        String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                        s.append(readstring);
                    }
                    inputRead.close();
                    String[] fieldSplit = s.toString().split(";");
                    DbHelper db = new DbHelper(getApplicationContext(), "init", null, 1);
                    db.openDb("init");
                    for (String str : fieldSplit) {
                        if (!str.equals("\n")) {
                            if (str.contains("DROP TABLE")) {
                                try {
                                    Log.v("Init", "DROP query " + str);
                                    db.execSql(str);
                                } catch (Exception e) {
                                    Logger.exception(TAG, e);
                                }
                            } else {
                                Log.v("Init", "Query " + str);
                                db.execSql(str);
                            }
                        }
                    }

                    db.closeDb();
                    String rename = DEFAULT_DOWNLOAD_PATH + File.separator + gFileName + "T";
                    new File(DEFAULT_DOWNLOAD_PATH + File.separator + gFileName).renameTo(new File(rename));

                } catch (Exception e) {
                    UIUtils.toast(Init.this, R.drawable.redinfonet, "INICIALIZACIÓN FALLIDA",
                            Toast.LENGTH_SHORT);
                    Logger.exception(TAG, e);
                    new File(DEFAULT_DOWNLOAD_PATH + File.separator + gFileName).delete();
                    return false;
                }

                //Tools.saveHash(gHashTotal, getApplicationContext()); //guarda hash
            }
            if (gFileName.endsWith(".zip")) {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            unzip(gFileName, DEFAULT_DOWNLOAD_PATH + File.separator, Init.this);
                        }
                    });
                } catch (Exception e) {
                    Logger.exception(TAG, e);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Lee los archivos de configuracion de CTL y los copia en una ruta interna dentro del
     * package de la aplicacion (acceso solo desde la app), posterior a esto se eliminan del
     * SD
     *
     * @param aFileName
     * @param aFileWithOutExt
     * @return
     */
    public boolean processFilesCTL(String aFileName, String aFileWithOutExt) {
        File fileLocation = new File(DEFAULT_DOWNLOAD_PATH + File.separator + aFileName);

        ContextWrapper cw = new ContextWrapper(Init.this);
        File directory = cw.getDir(NAME_FOLDER_CTL_FILES, Context.MODE_PRIVATE);
        File file = new File(directory + File.separator + aFileWithOutExt);

        if (fileLocation.exists() && (gFileName.endsWith(".bin") || gFileName.endsWith(".BIN"))) {
            try {
                FileInputStream inputRead = new FileInputStream(fileLocation);
                FileOutputStream outWrite = new FileOutputStream(file);

                byte[] inputBuffer = new byte[1024];
                int charRead;

                while ((charRead = inputRead.read(inputBuffer)) > 0) {
                    outWrite.write(inputBuffer, 0, charRead);
                    outWrite.flush();
                }
                inputRead.close();
                outWrite.close();

            } catch (Exception e) {
                Logger.exception(TAG, e);
            }
        }
        return true;
    }

    public void unzip(final String zipFile, String location, Context context) {

        UnpackFile unpackFile;
        boolean ponerLaT = true;

        unpackFile = new UnpackFile(context, zipFile, location, ponerLaT, false, new UnpackFile.FileCallback() {
            @Override
            public void rspUnpack(boolean okUnpack) {

                if (okUnpack) {
                    String nameAux;
                    String nameTbl;

                    int i;
                    new File(DEFAULT_DOWNLOAD_PATH + File.separator + gFileName).delete();

                    nameAux = zipFile.replace(".zip", "");
                    i = 1;
                    nameTbl = getNameTableById(i);
                    while (!nameTbl.equals("")) {
                        gFileName = nameAux + "_" + getNameTableById(i) + ".txt";
                        processFile();
                        i++;
                        nameTbl = getNameTableById(i);
                    }

                    i = 1;
                    nameTbl = getNameFileCTL(i);
                    while (!nameTbl.equals("")) {
                        gFileName = nameAux + "_" + getNameFileCTL(i) + ".bin";
                        processFilesCTL(gFileName, getNameFileCTL(i));
                        i++;
                        nameTbl = getNameFileCTL(i);
                    }
                    if (callBackInit != null)
                        callBackInit.getRspInitCallback(0);
                }
            }
        });
        unpackFile.execute();
    }
}
