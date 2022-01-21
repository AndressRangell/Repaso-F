package com.flota.basedatos.migracion;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flota.basedatos.implementaciones.CierreDetalladoDAOImpl;
import com.flota.basedatos.implementaciones.CierreTotalDAOImpl;
import com.flota.basedatos.interfaces.CierreDetalladoDAO;
import com.flota.basedatos.interfaces.CierreTotalDAO;
import com.flota.logscierres.LogsCierreDetalladoModelo;
import com.flota.logscierres.LogsCierresModelo;
import com.flota.logscierres.SqlLogsCierres;
import com.newpos.libpay.Logger;
import com.newpos.libpay.utils.ISOUtil;
import com.wposs.flota.R;

import java.io.File;
import java.util.List;

import cn.desert.newpos.payui.UIUtils;

public class MigraccionBaseDatos extends AsyncTask<Void, Void, Void> {

    String clase = "MigraccionBaseDatos.java";
    ProgressDialog progressDialog;
    ProgressBar progressBar;
    TextView textProgressBar;
    int timerOut = 32;
    Context context;

    ProcederMigracion procederMigracion;
    String pathBaseDatos = "/data/data/com.wposs.bancard/databases/logs_cierres.db";
    int i = 0;

    public MigraccionBaseDatos(Context context, ProcederMigracion procederMigracion) {
        this.context = context;
        this.procederMigracion = procederMigracion;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try {
            progressDialog = new ProgressDialog(context);
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
            this.progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            this.progressDialog.setContentView(R.layout.proceso_migracion_inicializacion);
            progressBar = progressDialog.findViewById(R.id.progressBar);
            textProgressBar = progressDialog.findViewById(R.id.Texto);
            mostrarSerialvsVersion(progressDialog);
        } catch (Exception e) {
            e.printStackTrace();
          Logger.exception(clase, e);
            Logger.error( "onPreExecute: " ,e);
        }
    }


    private void mostrarSerialvsVersion(ProgressDialog pd) {
        TextView tvVersion = pd.findViewById(R.id.tvVersion);
        TextView tvSerial = pd.findViewById(R.id.tvSerial);
        UIUtils.mostrarSerialvsVersion(tvVersion, tvSerial);
    }

    @Override
    protected Void doInBackground(Void... voids) {

        procesoMigracion((Activity) context);
        return null;
    }

    private void procesoMigracion(Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (checkDataBase(pathBaseDatos)) {
                    visualizarProceso(textProgressBar);

                    SqlLogsCierres baseDatosAnterior = new SqlLogsCierres(context);
                    CierreTotalDAO cierreTotalDAO = new CierreTotalDAOImpl(context);

                    CierreDetalladoDAO cierreDetalladoDAO = new CierreDetalladoDAOImpl(context);


                    List<LogsCierresModelo> listadoCierreTotal = baseDatosAnterior.getAllLogsCierre();
                    List<LogsCierreDetalladoModelo> listadoCierreDetallado = baseDatosAnterior.getAllLogsCierreDetalladoMigracion();

                    recalcularTimeout(listadoCierreDetallado.size(), listadoCierreTotal.size());

                    if (!listadoCierreTotal.isEmpty()) {
                        for (LogsCierresModelo cierreTotal : listadoCierreTotal) {
                            LogsCierresModelo cierreNuevo = new LogsCierresModelo();
                            cierreNuevo.setId(checkNullZero(cierreTotal.getId()));
                            cierreNuevo.setTipoCierre(checkNullZero(cierreTotal.getTipoCierre()));
                            cierreNuevo.setNumLote(checkNullZero(cierreTotal.getNumLote()));
                            cierreNuevo.setFechaUltimoCierre(checkNullZero(cierreTotal.getFechaUltimoCierre()));
                            cierreNuevo.setFechaCierre(checkNullZero(cierreTotal.getFechaCierre()));
                            cierreNuevo.setDiscriminadoComercios(checkNullZero(cierreTotal.getDiscriminadoComercios()));
                            cierreNuevo.setCantCredito(checkNullZero(cierreTotal.getCantCredito()));
                            cierreNuevo.setTotalCredito(checkNullZero(cierreTotal.getTotalCredito()));
                            cierreNuevo.setCantDebito(checkNullZero(cierreTotal.getCantDebito()));
                            cierreNuevo.setTotalDebito(checkNullZero(cierreTotal.getTotalDebito()));
                            cierreNuevo.setCantMovil(checkNullZero(cierreTotal.getCantMovil()));
                            cierreNuevo.setTotalMovil(checkNullZero(cierreTotal.getTotalMovil()));
                            cierreNuevo.setCantAnular(checkNullZero(cierreTotal.getCantAnular()));
                            cierreNuevo.setTotalAnular(checkNullZero(cierreTotal.getTotalAnular()));
                            cierreNuevo.setCantVuelto(checkNullZero(cierreTotal.getCantVuelto()));
                            cierreNuevo.setTotalVuelto(checkNullZero(cierreTotal.getTotalVuelto()));
                            cierreNuevo.setCantSaldo(checkNullZero(cierreTotal.getCantSaldo()));
                            cierreNuevo.setTotalSaldo(checkNullZero(cierreTotal.getTotalSaldo()));
                            cierreNuevo.setCantGeneral(checkNullZero(cierreTotal.getCantGeneral()));
                            cierreNuevo.setTotalGeneral(checkNullZero(cierreTotal.getTotalGeneral()));
                            cierreTotalDAO.ingresarRegistro(cierreNuevo);
                        }

                    }


                    if (!listadoCierreDetallado.isEmpty()) {
                        for (LogsCierreDetalladoModelo cierreDetallado : listadoCierreDetallado) {

                            LogsCierreDetalladoModelo cierreDetalladoNuevo = new LogsCierreDetalladoModelo();

                            cierreDetalladoNuevo.setTarjeta(cierreDetallado.getTarjeta());
                            cierreDetalladoNuevo.setCargo(cierreDetallado.getCargo());
                            cierreDetalladoNuevo.setNumBoleta(cierreDetallado.getNumBoleta());
                            cierreDetalladoNuevo.setMonto(cierreDetallado.getMonto());
                            cierreDetalladoNuevo.setFecha(cierreDetallado.getFecha());
                            cierreDetalladoNuevo.setHora(cierreDetallado.getHora());
                            cierreDetalladoNuevo.setTrans(cierreDetallado.getTrans());
                            cierreDetalladoNuevo.setTipoTarjeta(cierreDetallado.getTipoTarjeta());
                            cierreDetalladoNuevo.setIdCierre(cierreDetallado.getIdCierre());

                            cierreDetalladoDAO.ingresarRegistro(cierreDetalladoNuevo);
                        }

                    }

                    baseDatosAnterior.getEliminarBaseDatos(context);


                } else {
                    progressDialog.dismiss();
                    procederMigracion.rspProcesoTerminadoMigracion(true);
                }

            }
        });
    }

    private void recalcularTimeout(int totalCierreDetallado, int totalCierreTotal) {
        int resultado = totalCierreDetallado + totalCierreTotal;
        timerOut = timerOut * resultado;
    }

    private void visualizarProceso(final TextView textProgressBar) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (i <= 100) {
                    progressBar.setProgress(i);
                    textProgressBar.setText("Actualizando Base de Datos... \n(" + i + " % )");
                    i++;
                    handler.postDelayed(this, timerOut);
                } else {
                    progressDialog.dismiss();
                    handler.removeCallbacks(this);
                    ISOUtil.showMensaje("Base de Datos Actualizada", context);
                    procederMigracion.rspProcesoTerminadoMigracion(true);
                }
            }
        }, timerOut);

    }

    private String checkNullZero(String data) {
        if (data == null || data.trim().equals("")) {
            return "0";
        } else {
            return data;
        }
    }

    private boolean checkDataBase(String myPath) {
        try {
            File file = new File(myPath);
            if (file.exists() && !file.isDirectory()) {
                SQLiteDatabase checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
                checkDB.close();
                return true;
            }
        } catch (SQLiteException e) {
          Logger.exception(clase, e);
            e.printStackTrace();
        }
        return false;
    }

    public interface ProcederMigracion {
        void rspProcesoTerminadoMigracion(boolean isContinuarIncializacion);
    }
}
