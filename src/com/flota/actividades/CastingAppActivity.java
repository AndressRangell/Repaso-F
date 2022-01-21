package com.flota.actividades;

import static com.flota.defines_bancard.DefinesBANCARD.EVENTO_TAREAS;
import static com.flota.defines_bancard.DefinesBANCARD.POLARIS_APP_NAME;
import static com.flota.defines_bancard.DefinesBANCARD.TAREA_REALIZA;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.flota.inicializacion.configuracioncomercio.Tareas;
import com.flota.inicializacion.trans_init.Init;
import com.flota.inicializacion.trans_init.trans.ISO;
import com.newpos.libpay.Logger;
import com.newpos.libpay.trans.translog.TransLog;
import com.wposs.flota.R;

import java.io.File;
import java.util.List;

import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.master.FormularioActivity;
import interactor.casting_application.CastingTransPackImpl;
import interactor.casting_application.SendRcdConfirm;

public class CastingAppActivity extends FormularioActivity {

    public static final String TAG = "CastingAppActivity.java";

    private TextView txtInfoMsg;
    private List<Tareas> tasks;

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.initView(R.layout.trans_handling);

        try {
            txtInfoMsg = findViewById(R.id.txt_handing_info_msg);
            verifyingTaskType();
        } catch (Exception exception) {
            Logger.exception(TAG, exception);
            exception.printStackTrace();
        }
    }

    private void verifyingTaskType() {
        tasks = Tareas.getInstance(false).getListadoTarea2Aplicacion(POLARIS_APP_NAME);
        if (tasks != null && !tasks.isEmpty()) {
            txtInfoMsg.setText(tasks.get(0).getDescripcion());
            if (tasks.get(0).getDescripcion().equals("BORRAR CACHE APLICACION")) {
                TransLog.clearReveral();
                TransLog.clearScriptResult();
                deleteCache(CastingAppActivity.this);
            }
        }
    }

    private void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (deleteDir(dir)) {
                Toast.makeText(context, "Cache borrado", Toast.LENGTH_SHORT).show();
                sendConfirmTrans();
            } else {
                Toast.makeText(context, "Cache no borrado", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.exception(TAG, e);
        }
    }

    private void sendConfirmTrans() {
        SendRcdConfirm send = new SendRcdConfirm(
                CastingAppActivity.this,
                new CastingTransPackImpl(this, tasks),
                getResources().getString(R.string.ipConfigWifi),
                Integer.parseInt(getResources().getString(R.string.portConfig)),
                Integer.parseInt(getResources().getString(R.string.timerConfig)),
                new SendRcdConfirm.Actions() {
                    @Override
                    public void onShowError(String msg) {
                        UIUtils.toast(CastingAppActivity.this, R.drawable.ic_redinfonet, msg, Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onShowSuccess(byte[] rsp) {
                        if (rsp != null) {
                            unpackTrama(new ISO(rsp, ISO.LENGHT_NOT_INCLUDE, ISO.TPDU_INCLUDE));
                        } else {
                            UIUtils.toast(CastingAppActivity.this, R.drawable.ic_redinfonet,
                                    "ERROR EN TRANSACCION NO SE RECIBIO RESPUESTA DEL SERVIDOR", Toast.LENGTH_LONG);
                            intentActivity(MainActivity.class);
                        }
                    }
                }
        );
        send.execute();
    }

    private void unpackTrama(ISO iso) {
        String rspCode = iso.getField(ISO.FIELD_39_RESPONSE_CODE);
        String msg = iso.getField(ISO.FIELD_60_RESERVED_PRIVATE);

        Log.d("CastingAppActivity", "unpackTrama: rspCode " + rspCode);
        Log.d("CastingAppActivity", "unpackTrama: msg " + msg);

        UIUtils.toast(CastingAppActivity.this, R.drawable.ic_redinfonet, msg, Toast.LENGTH_LONG);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Logger.exception(TAG, e);
        } finally {
            Thread.currentThread().interrupt();
        }

        if (rspCode.equals("00")) {
            saveConfirmTask();
            intentActivity(Init.class);
        } else {
            intentActivity(MainActivity.class);
        }
    }

    private void intentActivity(Class<?> cls) {
        tasks.clear();
        startActivity(new Intent(CastingAppActivity.this, cls)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        );
        overridePendingTransition(0, 0);
        finish();
    }

    private void saveConfirmTask() {
        SharedPreferences.Editor editor = getApplicationContext()
                .getSharedPreferences(EVENTO_TAREAS, MODE_PRIVATE).edit();
        editor.putBoolean(TAREA_REALIZA, true);
        editor.apply();
    }
}