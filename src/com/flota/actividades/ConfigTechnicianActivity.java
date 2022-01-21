package com.flota.actividades;

import static com.flota.actividades.StartAppBANCARD.readWriteFileMDM;
import static com.flota.defines_bancard.DefinesBANCARD.ITEM_LIMPIAR_DATOS;
import static com.flota.inicializacion.trans_init.Init.DEFAULT_DOWNLOAD_PATH;
import static com.flota.menus.menus.idAcquirer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.flota.adapters.UAFactory;
import com.flota.adapters.UAListener;
import com.flota.adapters.model.ButtonModel;
import com.flota.polaris_validation.ReadWriteFileMDM;
import com.flota.setting.ListSetting;
import com.newpos.libpay.Logger;
import com.newpos.libpay.trans.translog.TransLog;
import com.pos.device.SDKException;
import com.pos.device.ped.KeySystem;
import com.pos.device.ped.KeyType;
import com.pos.device.ped.Ped;
import com.wposs.flota.R;

import java.io.File;
import java.util.List;

import cn.desert.newpos.payui.master.FormularioActivity;
import cn.desert.newpos.payui.master.mToolbar;
import cn.desert.newpos.payui.master.onToolbarClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class ConfigTechnicianActivity extends FormularioActivity {

    public static final String TAG = "ConfigTechnician";

    private CountDownTimer timer;

    public static void reLaunchApp(Context context) {
        context.startActivity(
                Intent.makeRestartActivityTask(context.getPackageManager()
                        .getLaunchIntentForPackage(context.getPackageName())
                        .getComponent()
                )
        );
        Runtime.getRuntime().exit(0);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.initView(new mToolbar(R.layout.activity_config, R.drawable.ic__back, 0), new onToolbarClick() {
            @Override
            public void onClick() {
                startActivity(new Intent(ConfigTechnicianActivity.this, MenusActivity.class));
            }
        }, null);

        final String title = "Configuración técnico";
        ((TextView) findViewById(R.id.tvTitulo)).setText(title);

        genButtonsTechnician();
    }

    private void genButtonsTechnician() {
        List<ButtonModel> buttons = ListSetting.getInstanceListadoTecnico(ConfigTechnicianActivity.this);
        genRcy(R.id.recycler_buttons_config, UAFactory.adapterButtons(buttons, new UAListener.ButtonListener() {
            @Override
            public void onClick(View view, ButtonModel button) {
                processButtonClick(button);
            }
        }));
    }

    private void processButtonClick(ButtonModel button) {
        if (ITEM_LIMPIAR_DATOS.equals(button.getName())) {
            restartApp(ConfigTechnicianActivity.this,
                    "¿Borrar datos?", "Atención se perderá toda la información del POS");
        }
    }

    @SuppressWarnings("deprecation")
    private void restartApp(final Context context, final String titulo, final String mensaje) {
        SweetAlertDialog dialog = new SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setCustomImage(R.drawable.ic_menu_config_clean_data)
                .setTitleText(titulo)
                .setContentText(mensaje)
                .setConfirmButtonBackgroundColor(getResources().getColor(R.color.colorPrimary))
                .setCancelButtonBackgroundColor(getResources().getColor(R.color.colorPrimary))
                .setConfirmButton("Sí, Eliminar", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        clearData(context);
                    }
                })
                .setCancelButton("No", new SweetAlertDialog.OnSweetClickListener() {
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                });
        dialog.show();
    }

    private void clearData(Context context) {
        eliminarCache(getApplicationContext(), TAG);
        String[] paths = {DEFAULT_DOWNLOAD_PATH, getApplicationInfo().dataDir};
        for (String path : paths) {
            eliminarDatos(new File(path), TAG);
        }
        clearTransLog();
        readWriteFileMDM.writeFileMDM(readWriteFileMDM.ISTRANSACCION_DEACTIVE);
        readWriteFileMDM.writeFileMDM(readWriteFileMDM.getSettle(), readWriteFileMDM.getReverse());
        showCustomDialog(context, "Importante", "El caché y los Datos fueron borrados con Éxito, Se reiniciará la aplicación.");
    }

    private void clearTransLog() {
        TransLog.clearReveral();
        TransLog.clearScriptResult();
        TransLog.getInstance(idAcquirer).clearAll(idAcquirer);
    }

    @SuppressWarnings("deprecation")
    private void showCustomDialog(final Context context, final String titulo, final String mensaje) {
        Log.e(TAG, "showCustomDialog: mensaje: " + mensaje);
        counterDownTimer(context, 3000, "showCustomDialog");
        SweetAlertDialog dialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(titulo)
                .setContentText(mensaje)
                .setConfirmButtonBackgroundColor(getResources().getColor(R.color.colorPrimary))
                .setConfirmButton("Aceptar", new SweetAlertDialog.OnSweetClickListener() {
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        deleteTimer();
                        reLaunchApp(context);
                    }
                });
        dialog.show();
    }

    private void counterDownTimer(final Context context, final int timeout, final String metodo) {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new CountDownTimer(timeout, 500) {
            public void onTick(long millisUntilFinished) {
                Log.e("onTick", "init onTick countDownTimer " + metodo + " " + millisUntilFinished);
            }

            public void onFinish() {
                timer.cancel();
                timer = null;
                reLaunchApp(context);
            }
        }.start();
    }

    private void deleteTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            Log.i(TAG, "deleteTimer: Finalizado");
        }
    }

    @SuppressWarnings("deprecation")
    private void solicitarEliminarLLaves(Context context) {
        try {
            SweetAlertDialog dialog = new SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                    .setCustomImage(R.drawable.ic_keys)
                    .setTitleText("¿Eliminar llaves?")
                    .setContentText("Cuidado. Si continúa, su POS ya no podrá realizar transacciones.")
                    .setConfirmButtonBackgroundColor(getResources().getColor(R.color.red))
                    .setConfirmButton("Sí, Eliminar", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            try {
                                Ped.getInstance().deleteKey(KeySystem.DUKPT_DES, KeyType.KEY_TYPE_DUKPTK, 1);
                                Ped.getInstance().deleteKey(KeySystem.MS_DES, KeyType.KEY_TYPE_MASTK, 0);
                            } catch (SDKException e) {
                                Logger.exception(TAG, e);
                            }
                            startActivity(new Intent(ConfigTechnicianActivity.this, StartAppBANCARD.class));
                        }
                    })
                    .setCancelButtonBackgroundColor(getResources().getColor(R.color.colorPrimary))
                    .setCancelButton("No", new SweetAlertDialog.OnSweetClickListener() {
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                        }
                    });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.exception(TAG, e);
        }
    }
}
