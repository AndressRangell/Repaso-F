package com.flota.basedatos.implementaciones;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import com.flota.basedatos.ConexionSQLite;
import com.flota.basedatos.interfaces.CierreDetalladoDAO;
import com.flota.logscierres.LogsCierreDetalladoModelo;
import com.newpos.libpay.Logger;
import com.newpos.libpay.trans.translog.TransLogData;
import com.newpos.libpay.utils.ISOUtil;

import java.util.ArrayList;
import java.util.List;

public class CierreDetalladoDAOImpl extends ConexionSQLite implements CierreDetalladoDAO {

    String clase = "CierreDetalladoDAOlmpl.java";

    public CierreDetalladoDAOImpl(Context context) {
        super(context);
    }

    @Override
    public boolean ingresarRegistro(LogsCierreDetalladoModelo logsCierreDetalladoModelo) {
        boolean ret = false;
        conexion = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (logsCierreDetalladoModelo.getTarjeta() != null) {
            values.put(COLUMN_LOGS_TARJETA, logsCierreDetalladoModelo.getTarjeta());
        }
        if (logsCierreDetalladoModelo.getCargo() != null) {
            values.put(COLUMN_LOGS_CARGO, logsCierreDetalladoModelo.getCargo());
        }
        if (logsCierreDetalladoModelo.getNumBoleta() != null) {
            values.put(COLUMN_LOGS_NUM_BOLETA, logsCierreDetalladoModelo.getNumBoleta());
        }
        if (logsCierreDetalladoModelo.getMonto() != null) {
            values.put(COLUMN_LOGS_MONTO, logsCierreDetalladoModelo.getMonto());
        }
        if (logsCierreDetalladoModelo.getFecha() != null) {
            values.put(COLUMN_LOGS_FECHA, logsCierreDetalladoModelo.getFecha());
        }
        if (logsCierreDetalladoModelo.getHora() != null) {
            values.put(COLUMN_LOGS_HORA, logsCierreDetalladoModelo.getHora());
        }
        if (logsCierreDetalladoModelo.getTrans() != null) {
            values.put(COLUMN_LOGS_TRANS, logsCierreDetalladoModelo.getTrans());
        }
        if (logsCierreDetalladoModelo.getTipoTarjeta() != null) {
            values.put(COLUMN_LOGS_TIPO_TARJETA, logsCierreDetalladoModelo.getTipoTarjeta());
        }
        if (logsCierreDetalladoModelo.getIdCierre() != null) {
            values.put(COLUMN_LOGS_ID_CIERRE, logsCierreDetalladoModelo.getIdCierre());
        }

        try {
            conexion.insert(TABLE_LOGS_CIERRES_DETALLADO, null, values);
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
    public List<TransLogData> getLogsCierreDetallado(Activity context, String numLote) {
        List<TransLogData> logsCierredetallado = null;
        try {
            conexion = this.getWritableDatabase();
            logsCierredetallado = new ArrayList<>();
            String query = "SELECT * FROM " + TABLE_LOGS_CIERRES_DETALLADO + " WHERE " + COLUMN_LOGS_ID_CIERRE + " = " + "'" + numLote + "'";
            Cursor cursor = conexion.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    TransLogData transLogData = new TransLogData();
                    transLogData.setPan(cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_TARJETA)));
                    transLogData.setNroCargo(cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_CARGO)));
                    transLogData.setRRN(cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_NUM_BOLETA)));
                    if (cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_MONTO)) != null) {
                        transLogData.setAmount(Long.parseLong(cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_MONTO))));
                    }
                    transLogData.setLocalDate(cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_FECHA)));
                    transLogData.setLocalTime(cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_HORA)));
                    transLogData.setEName(cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_TRANS)));
                    transLogData.setTipoTarjeta(cursor.getString(cursor.getColumnIndex(COLUMN_LOGS_TIPO_TARJETA)));
                    logsCierredetallado.add(transLogData);
                } while (cursor.moveToNext());
                cursor.close();
            }

            conexion.close();
        } catch (Exception e) {
            e.printStackTrace();
          Logger.exception(clase, e);
            ISOUtil.AlertExcepciones("Error cierre detallado \n" + e.getMessage(), context);
        }


        return logsCierredetallado;
    }
}
