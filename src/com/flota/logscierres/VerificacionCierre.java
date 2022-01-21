package com.flota.logscierres;


import static android.content.Context.MODE_PRIVATE;
import static com.flota.defines_bancard.DefinesBANCARD.EVENTO_TAREAS;
import static com.flota.defines_bancard.DefinesBANCARD.POLARIS_APP_NAME;
import static com.flota.defines_bancard.DefinesBANCARD.TAREA_REALIZA;
import static com.flota.transactions.common.CommonFunctionalities.getUltimoComercio;
import static com.newpos.libpay.trans.Trans.idLote;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.flota.basedatos.implementaciones.CierreTotalDAOImpl;
import com.flota.basedatos.interfaces.CierreTotalDAO;
import com.flota.defines_bancard.DefinesBANCARD;
import com.flota.inicializacion.configuracioncomercio.APLICACIONES;
import com.flota.inicializacion.configuracioncomercio.COMERCIOS;
import com.flota.inicializacion.trans_init.Init;
import com.flota.tools_bacth.ToolsBatch;
import com.flota.transactions.common.CommonFunctionalities;
import com.newpos.libpay.Logger;
import com.newpos.libpay.device.printer.PrintRes;
import com.newpos.libpay.trans.translog.TransLog;
import com.newpos.libpay.trans.translog.TransLogData;
import com.newpos.libpay.utils.ISOUtil;
import com.newpos.libpay.utils.PAYUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.desert.newpos.payui.master.MasterControl;
import cn.pedant.SweetAlert.SweetAlertDialog;
import interactor.utils.DateImpl;
import interactor.utils.DateUtil;

public class VerificacionCierre {

    @SuppressLint("StaticFieldLeak")
    static VerificacionCierre verificacionCierre = null;
    String mensajeCounTimer = "counterDownTimer: ";
    String mensajeTag = "Mensaje VerificacionCierre";
    String claseActual = "VerificacionCierre.java";

    CountDownTimer countDownTimer;
    Context context;

    public VerificacionCierre(Context context) {
        this.context = context;
    }

    public static VerificacionCierre getVerificacionCierre(Context context) {
        verificacionCierre = new VerificacionCierre(context);
        return verificacionCierre;
    }

    public boolean cambioComercio(Context context) {

        String comercioPolaris = COMERCIOS.getSingletonInstance(context).getMerchantDescription();
        String ultimoComercio = getUltimoComercio(context);

        List<TransLogData> list = TransLog.getInstance(idLote).getData();

        if (ultimoComercio == null && !comercioPolaris.isEmpty()) {
            guardarComercioCierre(context, comercioPolaris);

        } else if (!comercioPolaris.isEmpty() && !comercioPolaris.equals(ultimoComercio)) {
            if (!list.isEmpty()) {
                return true;
            } else {
                CierreTotalDAO sql = new CierreTotalDAOImpl(context);
                guardarComercioCierre(context, comercioPolaris);
                sql.getEliminarBaseDatos(context);
            }
        } else if (ToolsBatch.statusTrans(idLote)) {
            return cierreTransacciones(list);
        }

        return false;
    }

    private boolean cierreTransacciones(List<TransLogData> list) {
        APLICACIONES aplicaciones = APLICACIONES.getSingletonInstanceAppActual(POLARIS_APP_NAME);
        if (aplicaciones != null && !aplicaciones.getMaxNumeroTxnCierre().isEmpty()) {
            int numeroTranscionesCierre = Integer.parseInt(aplicaciones.getMaxNumeroTxnCierre());
            return numeroTranscionesCierre > 0 && list.size() >= numeroTranscionesCierre;
        } else {
            ISOUtil.showMensaje("Numero transacciones cierre \n no está  definido", context);
            return list.size() > 300;

        }
    }

    void intentTrans(boolean isInicializacion, int tipoTrans) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (isInicializacion) {
            verificacionCierre.guardarFechaIncializacion(context);
            intent.setClass(context, Init.class);
        } else {
            intent.setClass(context, MasterControl.class);
            intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[tipoTrans]);
        }

        context.startActivity(intent);
    }

    private void guardarComercioCierre(Context context, String comercio) {
        SharedPreferences.Editor editor = context.getSharedPreferences("fecha-cierre", MODE_PRIVATE).edit();
        editor.putString("Comercio", comercio);
        editor.apply();
    }

    public void verificacionCierre(Bundle bundle) {
        try {
            if (bundle != null) {
                boolean verificacion = bundle.getBoolean(DefinesBANCARD.FECHA_INICIALIZACION);
                TransLogData revesalData = TransLog.getReversal();
                if (verificacion) {
                    verificacionCierre.guardarFechaIncializacion(context);
                    if (revesalData != null) {
                        intentTrans(false, 36);
                    }
                }
            }

            if (verificacionCierre.detectNewDayToInitialize(context)) {
                if (Logger.getFilesCant() >= 3) {
                    Logger.deleteLogs();
                }
                showCustomDialog(context);
            } else if (verificacionCierre.cambioComercio(context)) {
                intentTrans(false, 25);
            }
        } catch (Exception e) {
            Logger.exception(claseActual, e);
        }


    }

    private boolean detectNewDayToInitialize(Context context) {
        String dateLastInit = CommonFunctionalities.getFechaUltimaIncializacion(context);
        String dayInit = CommonFunctionalities.getDiasCierre(context);
        String hourInit = CommonFunctionalities.getHoraCierre(context);
        String timeInit = PAYUtils.getTimeCierre();

        if (hourInit == null || hourInit.trim().equals("") || hourInit.isEmpty()) {
            CommonFunctionalities.setHoraCierre(context);
        }

        DateUtil dateUtil = new DateImpl(context);
        return dateUtil.detectNewDay(dateLastInit, dayInit, hourInit, timeInit);
    }

    public void guardarFechaIncializacion(Context context) {
        @SuppressLint("SimpleDateFormat") DateFormat hourdateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date fechaActual = new Date();
        SharedPreferences.Editor editor = context.getSharedPreferences(DefinesBANCARD.FECHA_INCIALIZACION, MODE_PRIVATE).edit();
        editor.putString(DefinesBANCARD.FECHA_INICIALIZACION, hourdateFormat.format(fechaActual));
        editor.apply();
    }

    private void showCustomDialog(final Context context) {
        counterDownTimer();
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Importante")
                .setContentText("Es necesario realizar la inicialización\ndiaria del POS en este momento.")
                .setConfirmText("Aceptar")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        deleteTimer();
                        guardarConfirmacionTarea();
                        intentTrans(true, 0);
                    }
                });
        sweetAlertDialog.show();
    }

    private void guardarConfirmacionTarea() {
        SharedPreferences.Editor editor = context.getApplicationContext().getSharedPreferences(EVENTO_TAREAS, MODE_PRIVATE).edit();
        editor.putBoolean(TAREA_REALIZA, false);
        editor.apply();
    }

    private void counterDownTimer() {
        Logger.error(mensajeTag, mensajeCounTimer + "Ingreso ");
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
            Logger.error(mensajeTag, mensajeCounTimer + "Finalizo ");
        }
        countDownTimer = new CountDownTimer(10000, 5000) {
            public void onTick(long millisUntilFinished) {
                Logger.error("onTick", "init onTick countDownTimer " + "showCustomDialog" + " " + millisUntilFinished);
            }

            public void onFinish() {
                countDownTimer.cancel();
                countDownTimer = null;
                Logger.error(mensajeTag, mensajeCounTimer + " Finalizado ");
                intentTrans(true, 0);
            }
        }.start();

    }

    private void deleteTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
            Logger.error("Mensaje ", "deleteTimer: " + " Finalizado");
        }
    }
}
