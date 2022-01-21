package com.flota.actividades;

import static com.flota.actividades.StartAppBANCARD.isInit;
import static com.flota.actividades.StartAppBANCARD.readWriteFileMDM;
import static com.flota.defines_bancard.DefinesBANCARD.EVENTO_TAREAS;
import static com.flota.defines_bancard.DefinesBANCARD.POLARIS_APP_NAME;
import static com.flota.defines_bancard.DefinesBANCARD.TAREA_REALIZA;
import static com.flota.defines_bancard.DefinesBANCARD.TYPE_3G;
import static com.flota.defines_bancard.DefinesBANCARD.TYPE_ETHERNET;
import static com.flota.inicializacion.trans_init.Init.DEFAULT_DOWNLOAD_PATH;
import static com.flota.keys.InjectMasterKey.MASTERKEYIDX;
import static com.flota.keys.InjectMasterKey.TRACK2KEYIDX;
import static com.flota.keys.InjectMasterKey.threreIsKey;
import static com.flota.keys.InjectMasterKey.threreIsKeyWK;
import static com.newpos.libpay.trans.Trans.idLote;
import static interactor.utils.MoreAppsImpl.PACKAGE_NAME_POLARIS;
import static interactor.utils.SecurityImpl.isNamedProcessRunning;
import static interactor.utils.SecurityImpl.isSafeMode;

import android.annotation.SuppressLint;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.flota.adapters.UAFactory;
import com.flota.adapters.UAListener;
import com.flota.adapters.model.AppModel;
import com.flota.defines_bancard.DefinesBANCARD;
import com.flota.inicializacion.configuracioncomercio.APLICACIONES;
import com.flota.inicializacion.configuracioncomercio.Device;
import com.flota.inicializacion.configuracioncomercio.Tareas;
import com.flota.inicializacion.trans_init.trans.Tools;
import com.flota.keys.DUKPT;
import com.flota.logscierres.VerificacionCierre;
import com.flota.polaris_validation.ReadWriteFileMDM;
import com.flota.screen.utils.Animations;
import com.flota.timertransacciones.TimerTrans;
import com.flota.tools.PaperStatus;
import com.flota.tools.RebootServiceClass;
import com.flota.tools.UtilNetwork;
import com.flota.tools_bacth.ToolsBatch;
import com.newpos.libpay.Logger;
import com.newpos.libpay.device.printer.PrintRes;
import com.newpos.libpay.trans.manager.RevesalTrans;
import com.newpos.libpay.utils.ISOUtil;
import com.pos.device.net.eth.EthernetManager;
import com.pos.device.printer.Printer;
import com.wposs.flota.R;

import java.io.File;
import java.util.List;

import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.master.FormularioActivity;
import cn.desert.newpos.payui.master.MasterControl;
import cn.desert.newpos.payui.master.mToolbar;
import cn.desert.newpos.payui.master.onToolbarClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import interactor.utils.MoreAppsImpl;
import interactor.utils.MoreAppsUtil;

public class MainActivity extends FormularioActivity {

    public static final String TAG = "MainActivity.java";

    // Mode Caja
    public static boolean modoCaja = false;

    // More Apps
    private CardView cardMoreApps;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.initView(new mToolbar(R.layout.activity_main, R.drawable.btn_menu, R.drawable.ic_more_apps),
                new onToolbarClick() {
                    @Override
                    public void onClick() {
                        startActivity(new Intent(MainActivity.this, MenusActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                    }
                }, new onToolbarClick() {
                    @Override
                    public void onClick() {
                        if (MasterControl.ANIM_ACTIVE) {
                            Animations.moreApps(cardMoreApps, MainActivity.this);
                        } else {
                            cardMoreApps.setVisibility(cardMoreApps.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                        }
                    }
                }
        );

        eliminarDatos(new File(DEFAULT_DOWNLOAD_PATH), TAG);
        genMoreApps();

        Bundle bundle = getIntent().getExtras();
        verificacionCasteoAplicacion(bundle);
        VerificacionCierre.getVerificacionCierre(MainActivity.this).verificacionCierre(bundle);
        verificadorActualizacionAgente();
        if (validadServicioActivoPolaris()) {
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            EthernetManager manager;
            if (!Device.getConexion().equals(TYPE_ETHERNET)) {
                if (Tools.isEthernetFirmware()) {
                    manager = EthernetManager.getInstance();
                    manager.setEtherentEnabled(false);
                }
                if (Device.getConexion().equals(TYPE_3G)) {
                    if (wifiManager != null && wifiManager.isWifiEnabled()) {
                        wifiManager.setWifiEnabled(false);
                    }
                } else {
                    forzarConexionWifi();
                }
            } else {
                if (wifiManager != null)
                    wifiManager.setWifiEnabled(false);
                Log.e(TAG, "onCreate: Alarma " + RebootServiceClass.isAlarma());
                if (UtilNetwork.canProccesNet() && Tools.isEthernetFirmware()) {
                    manager = EthernetManager.getInstance();
                    manager.setEtherentEnabled(true);
                }
            }
            componentsInit();
        } else {
            showPolarisLockView();
        }

        // Habilita instalacion

        if (!RevesalTrans.isReversalPending("MainActivity-OnCreate")) {
            ReadWriteFileMDM readWriteFileMDM = ReadWriteFileMDM.getInstance();
            readWriteFileMDM.writeFileMDM(readWriteFileMDM.getReverse(), readWriteFileMDM.getSettle());
            readWriteFileMDM.writeFileMDM(readWriteFileMDM.ISTRANSACCION_DEACTIVE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!validadServicioActivoPolaris()) {
            showPolarisLockView();
        }
    }

    @Override
    public void onBackPressed() {
        // Don't push back
    }

    private void verificacionCasteoAplicacion(Bundle bundle) {
        if (bundle != null) {
            boolean casteoAplicacion = bundle.getBoolean(DefinesBANCARD.CASTEO_APLICACION, false);
            if (casteoAplicacion && !getConfirmacionTareaRealizadas()) {
                List<Tareas> tareasList = Tareas.getInstance(false).getListadoTarea2Aplicacion(POLARIS_APP_NAME);
                if (tareasList != null && !tareasList.isEmpty()) {
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(MainActivity.this, CastingAppActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }
            }
        }
    }

    private boolean getConfirmacionTareaRealizadas() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(EVENTO_TAREAS, MODE_PRIVATE);
        return prefs.getBoolean(TAREA_REALIZA, false);
    }

    private void verificadorActualizacionAgente() {
        ReadWriteFileMDM readWriteFileMDM = ReadWriteFileMDM.getInstance();
        try {
            if (readWriteFileMDM != null) {
                readWriteFileMDM.readFileMDM();
                String validActualizacion = readWriteFileMDM.getInitAuto();
                Logger.info("PROCESO ACTUALIZACION " + validActualizacion);
                if (validActualizacion != null && !validActualizacion.equals("")
                        && validActualizacion.equals("1") && ToolsBatch.statusTrans(idLote)) {
                    final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#0E3E8A"));
                    pDialog.setCancelable(false);
                    pDialog.show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pDialog.dismiss();

                            Intent intent = new Intent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setClass(MainActivity.this, MasterControl.class);
                            intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[25]);
                            intent.putExtra(MasterControl.CAJAS, true);
                            startActivity(intent);
                        }
                    }, 2500);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public boolean validadServicioActivoPolaris() {
        if (!isSafeMode(MainActivity.this)
                && isNamedProcessRunning(MainActivity.this, PACKAGE_NAME_POLARIS)) {
            DevicePolicyManager manager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
            List<ComponentName> activeAdmins = manager.getActiveAdmins();
            if (activeAdmins != null) {
                for (ComponentName admin : activeAdmins) {
                    if (admin.getPackageName().contains(PACKAGE_NAME_POLARIS)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void forzarConexionWifi() {
        if (UtilNetwork.canProccesNet()) {
            UtilNetwork.activarWifi(this, false);
        }
    }

    private void componentsInit() {
        showVersionSerial();
        try {
            final String nameCommerce = StartAppBANCARD.tablaComercios.sucursal.getDescripcion();
            if (nameCommerce != null) {
                ((TextView) findViewById(R.id.tvNombreComercio)).setText(nameCommerce);
            }
            TimerTrans.deleteTimer();
        } catch (Exception e) {
            Logger.exception(TAG, e);
            ISOUtil.AlertExcepciones("Error informacion Tabla comerio : " + e.getMessage(), MainActivity.this);
            Logger.error("ERROR  ---- " + " onCreate: ", e);
        }

        MasterControl.setMcontext(MainActivity.this);
        new Handler().postAtTime(new Runnable() {
            @Override
            public void run() {
                transaccionInyeccionLLaves();
            }
        }, 6000);
    }

    private void transaccionInyeccionLLaves() {
        if (!verificarLlaves()) {
            intentSetting("Debe inyectar las llaves");
        }
    }

    private boolean verificarLlaves() {
        return DUKPT.checkIPEK() == 0
                && threreIsKey(MASTERKEYIDX, "Debe cargar Master Key", MainActivity.this)
                && threreIsKeyWK(TRACK2KEYIDX, "Debe cargar Work key", MainActivity.this);
    }

    public void intentSetting(String mensaje) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(MainActivity.this, MenusActivity.class);
        intent.putExtra(DefinesBANCARD.MENSAJE_ERROR_INYECCION_LLAVES, mensaje);
        startActivity(intent);
    }

    private void showPolarisLockView() {
        setContentView(R.layout.activity_agente);
        showVersionSerial();
    }

    private void genMoreApps() {
        cardMoreApps = findViewById(R.id.cardMoreApps);
        final MoreAppsUtil moreAppsUtil = new MoreAppsImpl(this);
        List<AppModel> listApps = moreAppsUtil.getListApps(APLICACIONES.getSingletonInstance());

        if (!listApps.isEmpty()) {
            RecyclerView recycler = findViewById(R.id.recyclerMoreApps);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recycler.setLayoutManager(layoutManager);
            recycler.setAdapter(UAFactory.adapterApps(listApps, new UAListener.AppListener() {
                @Override
                public void onClick(View view, AppModel app) {
                    String packageName = "";
                    packageName += app.getPackageName();
                    if (packageName != null && moreAppsUtil.appIsInstalled(packageName)) {
                        startActivity(getApplicationContext()
                                .getPackageManager().getLaunchIntentForPackage(packageName));
                        cardMoreApps.setVisibility(View.GONE);
                    } else UIUtils.toast(MainActivity.this, R.drawable.ic_redinfonet,
                            "Aplicación no instalada", Toast.LENGTH_SHORT);
                }
            }));
        } else {
            setButtonVisibility(rightButton, false);
        }
    }

    @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        if (isInit) {
            // DESACTIVA INSTALACION de APP por Polaris Agente, se juega con variable reverso ACTIVATE
            ReadWriteFileMDM.getInstance().writeFileMDM(readWriteFileMDM.ISTRANSACCION_ACTIVE);

            switch (view.getId()) {
                case R.id.optionSaldo:
                    intentTrans(31, true);
                    break;
                case R.id.optionVenta:
                    intentTrans(21, true);
                    break;
                case R.id.optionVentaManual:
                    intentTrans(22, true);
                    break;
                default:
                    Toast.makeText(this, "" + view.getId(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Requiere inicialización", Toast.LENGTH_SHORT).show();
        }
    }

    private void intentTrans(int tipoTrans, boolean isVenta) {
        if (PaperStatus.getInstance().getRet() == Printer.PRINTER_STATUS_PAPER_LACK) {
            toast(DefinesBANCARD.MSG_PAPER);
        } else {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(MainActivity.this, MasterControl.class);
            intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[tipoTrans]);
            if (isVenta) {
                intent.putExtra(MasterControl.CAJAS, false);
            }
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        }
    }
}
