package com.flota.basedatos.implementaciones;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import com.flota.basedatos.ConexionSQLite;
import com.flota.basedatos.interfaces.CierreTotalDAO;
import com.flota.logscierres.LogsCierresModelo;
import com.newpos.libpay.Logger;
import com.newpos.libpay.utils.ISOUtil;

import java.util.ArrayList;
import java.util.List;

public class CierreTotalDAOImpl extends ConexionSQLite implements CierreTotalDAO {

    public CierreTotalDAOImpl(Context context) {
        super(context);
    }
    String clase ="CierreTotalDAOImpl.java";

    @Override
    public boolean ingresarRegistro(LogsCierresModelo logsCierresModelo) {
        boolean ret = false;
        conexion = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGS_ID, logsCierresModelo.getId());
        if (logsCierresModelo.getTipoCierre() != null) {
            values.put(COLUMN_LOGS_TIPO_CIERRE, logsCierresModelo.getTipoCierre());
        }
        if (logsCierresModelo.getNumLote() != null) {
            values.put(COLUMN_LOGS_NUM_LOTE, logsCierresModelo.getNumLote());
        }
        if (logsCierresModelo.getFechaUltimoCierre() != null) {
            values.put(COLUMN_LOGS_FECHA_ULTIMO_CIERRE, logsCierresModelo.getFechaUltimoCierre());
        }
        if (logsCierresModelo.getFechaCierre() != null) {
            values.put(COLUMN_LOGS_FECHA_CIERRE, logsCierresModelo.getFechaCierre());
        }
        if (logsCierresModelo.getDiscriminadoComercios() != null) {
            values.put(COLUMN_LOGS_DISCRIMINADO_COMERCIOS, logsCierresModelo.getDiscriminadoComercios());
        }
        if (logsCierresModelo.getCantCredito() != null) {
            values.put(COLUMN_LOGS_CANT_CREDITO, logsCierresModelo.getCantCredito());
        }
        if (logsCierresModelo.getTotalCredito() != null) {
            values.put(COLUMN_LOGS_TOTAL_CREDITO, logsCierresModelo.getTotalCredito());
        }
        if (logsCierresModelo.getCantDebito() != null) {
            values.put(COLUMN_LOGS_CANT_DEBITO, logsCierresModelo.getCantDebito());
        }
        if (logsCierresModelo.getTotalDebito() != null) {
            values.put(COLUMN_LOGS_TOTAL_DEBITO, logsCierresModelo.getTotalDebito());
        }
        if (logsCierresModelo.getCantMovil() != null) {
            values.put(COLUMN_LOGS_CANT_MOVIL, logsCierresModelo.getCantMovil());
        }
        if (logsCierresModelo.getTotalMovil() != null) {
            values.put(COLUMN_LOGS_TOTAL_MOVIL, logsCierresModelo.getTotalMovil());
        }
        if (logsCierresModelo.getCantAnular() != null) {
            values.put(COLUMN_LOGS_CANT_ANULAR, logsCierresModelo.getCantAnular());
        }
        if (logsCierresModelo.getTotalAnular() != null) {
            values.put(COLUMN_LOGS_TOTAL_ANULAR, logsCierresModelo.getTotalAnular());
        }
        if (logsCierresModelo.getCantVuelto() != null) {
            values.put(COLUMN_LOGS_CANT_VUELTO, logsCierresModelo.getCantVuelto());
        }
        if (logsCierresModelo.getTotalVuelto() != null) {
            values.put(COLUMN_LOGS_TOTAL_VUELTO, logsCierresModelo.getTotalVuelto());
        }
        if (logsCierresModelo.getCantSaldo() != null) {
            values.put(COLUMN_LOGS_CANT_SALDO, logsCierresModelo.getCantSaldo());
        }
        if (logsCierresModelo.getTotalSaldo() != null) {
            values.put(COLUMN_LOGS_TOTAL_SALDO, logsCierresModelo.getTotalSaldo());
        }
        if (logsCierresModelo.getCantGeneral() != null) {
            values.put(COLUMN_LOGS_CANT_GENERAL, logsCierresModelo.getCantGeneral());
        }
        if (logsCierresModelo.getTotalGeneral() != null) {
            values.put(COLUMN_LOGS_TOTAL_GENERAL, logsCierresModelo.getTotalGeneral());
        }
        try {
            conexion.insert(TABLE_LOGS_CIERRES, null, values);
            conexion.close();
            ret = true;
        } catch (SQLException e) {
            e.printStackTrace();
          Logger.exception(clase, e);
            e.getCause();
        }
        return ret;
    }

    @Override
    public List<LogsCierresModelo> getAllLogsCierre() {
        conexion = this.getWritableDatabase();
        List<LogsCierresModelo> logsCierres = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_LOGS_CIERRES;
        Cursor cursor = conexion.rawQuery(query, null);
        if (cursor.moveToLast()) {
            do {
                LogsCierresModelo logsCierre = new LogsCierresModelo();
                logsCierre.setId(cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_ID)));
                logsCierre.setTipoCierre(cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_TIPO_CIERRE)));
                logsCierre.setNumLote(cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_NUM_LOTE)));
                logsCierre.setFechaUltimoCierre(cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_FECHA_ULTIMO_CIERRE)));
                logsCierre.setFechaCierre(cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_FECHA_CIERRE)));
                logsCierre.setDiscriminadoComercios(cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_DISCRIMINADO_COMERCIOS)));
                logsCierre.setCantCredito(checkNullZero(cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_CANT_CREDITO))));
                logsCierre.setTotalCredito(checkNullZero(cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_TOTAL_CREDITO))));
                logsCierre.setCantDebito(checkNullZero(cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_CANT_DEBITO))));
                logsCierre.setTotalDebito(checkNullZero(cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_TOTAL_DEBITO))));
                logsCierre.setCantMovil(checkNullZero(cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_CANT_MOVIL))));
                logsCierre.setTotalMovil(checkNullZero(cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_TOTAL_MOVIL))));
                logsCierre.setCantAnular(checkNullZero(cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_CANT_ANULAR))));
                logsCierre.setTotalAnular(checkNullZero(cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_TOTAL_ANULAR))));
                logsCierre.setCantVuelto(checkNullZero(cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_CANT_VUELTO))));
                logsCierre.setTotalVuelto(checkNullZero(cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_TOTAL_VUELTO))));
                logsCierre.setCantSaldo(checkNullZero(cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_CANT_SALDO))));
                logsCierre.setTotalSaldo(checkNullZero(cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_TOTAL_SALDO))));
                logsCierre.setCantGeneral(checkNullZero(cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_CANT_GENERAL))));
                logsCierre.setTotalGeneral(checkNullZero(cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_TOTAL_GENERAL))));

                logsCierres.add(logsCierre);
            } while (cursor.moveToPrevious());
        }
        cursor.close();
        conexion.close();

        return logsCierres;
    }

    @Override
    public void getEliminarBaseDatos(Context context) {
        try {
            context.deleteDatabase(DATABASE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
          Logger.exception(clase, e);
            ISOUtil.AlertExcepciones("Error Eliminacion Base Datos Cierre \n" + e.getMessage(), context);
        }
    }

    private String checkNullZero(String data) {
        if (data == null || data.trim().equals("")) {
            return "0";
        } else {
            return data;
        }
    }
}
