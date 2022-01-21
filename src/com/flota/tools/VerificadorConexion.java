package com.flota.tools;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;

import com.newpos.libpay.Logger;
import com.wposs.flota.R;

import cn.desert.newpos.payui.UIUtils;

public class VerificadorConexion extends AsyncTask<Void, Void, Void> {


    ProgressDialog pd;
    private Context context;
    private ProcederConexion procederConexion;
    CountDownTimer countDownTimer;
    int contador = 0;
    String clase ="VerificadorConexion.java";

    String mensajeCounTimer = "counterDownTimer: ";
    String mensajeTag = "Mensaje VerificadorConexion";

    public VerificadorConexion(Context context, ProcederConexion procederIncializacion) {
        this.context = context;
        this.procederConexion = procederIncializacion;
    }

    public interface ProcederConexion {
        void rspProcesoExitosoWifi(String mensajeExitoso);

        void rspProcesoExitoso3G(String mensajeExitoso);

        void rspProcesoExitosoEthernet(String mensajeExitoso);

        void rspProcesoFallido(String mensajeError);
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd = new ProgressDialog(context);
        this.pd.setCancelable(false);
        this.pd.show();
        this.pd.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        this.pd.setContentView(R.layout.progress_inicializacion);
        TextView textView = pd.findViewById(R.id.Texto);
        textView.setText("Verificando Conexión");
        mostrarSerialvsVersion(pd);

    }

    private void mostrarSerialvsVersion(ProgressDialog pd) {
        TextView tvVersion = pd.findViewById(R.id.tvVersion);
        TextView tvSerial = pd.findViewById(R.id.tvSerial);
        UIUtils.mostrarSerialvsVersion(tvVersion, tvSerial);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        valConexion(context);
        return null;
    }

    private void valConexion(final Context context) {
        Activity activity = (Activity) context;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    ConnectivityManager cm;
                    NetworkInfo ni;
                    boolean tipoConexion1 = false;
                    boolean tipoConexion2 = false;
                    boolean tipoConexion3 = false;


                    cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    ni = cm.getActiveNetworkInfo();


                    if (ni != null) {
                        ConnectivityManager connManager1 = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo mWifi = connManager1.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                        ConnectivityManager connManager2 = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo mMobile = connManager2.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

                        ConnectivityManager connManager3 = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo mEthe = connManager3.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
                        if (mWifi.isConnected()) {
                            tipoConexion1 = true;
                        }

                        if (mMobile.isConnected()) {
                            tipoConexion2 = true;
                        }
                        if (mEthe.isConnected()) {
                            tipoConexion3 = true;
                            tipoConexion1=false;
                        }
                    }

                    if (tipoConexion1) {
                        procederConexion.rspProcesoExitosoWifi("Conexión establecida\npor Wifi");
                        detenerCountDownTimer();
                    } else if (tipoConexion2) {
                        procederConexion.rspProcesoExitoso3G("Conexión establecida\npor 3G");
                        detenerCountDownTimer();
                    } else if (tipoConexion3) {
                        procederConexion.rspProcesoExitosoEthernet("Conexión establecida\npor Ethernet");
                        detenerCountDownTimer();
                    } else if (contador == 6) {
                        procederConexion.rspProcesoFallido("Conexión no establecida");
                        detenerCountDownTimer();
                    } else {
                        contador++;
                        counterDownTimer(10000, "VerificadorConexion");
                    }

                } catch (Exception e) {
                    Logger.exception(clase, e);
                    e.printStackTrace();
                    procederConexion.rspProcesoFallido("No se encuentra conexión " + e.getMessage());
                    detenerCountDownTimer();
                }

            }
        });

    }

    private void detenerCountDownTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (pd != null) {
            pd.dismiss();
        }
    }


    private void counterDownTimer(final int timeout, final String metodo) {

        Logger.error(mensajeTag, mensajeCounTimer + "Ingreso ");
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
            Logger.error(mensajeTag, mensajeCounTimer + "Finalizo ");
        }
        countDownTimer = new CountDownTimer(timeout, 5000) {
            public void onTick(long millisUntilFinished) {
                Logger.error("onTick", "init onTick countDownTimer " + metodo + " " + millisUntilFinished);
            }

            public void onFinish() {
                countDownTimer.cancel();
                countDownTimer = null;
                Logger.error(mensajeTag, mensajeCounTimer + " Finalizado ");
                valConexion(context);
            }
        }.start();

    }
}
