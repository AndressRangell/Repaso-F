package com.flota.logscierres;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.newpos.libpay.Logger;
import com.newpos.libpay.trans.translog.TransLogData;
import com.newpos.libpay.utils.ISOUtil;

import java.util.ArrayList;
import java.util.List;

public class SqlLogsCierres extends SQLiteOpenHelper {

    private SQLiteDatabase db;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "logs_cierres.db";
    String clase ="SqlLogsCierres.java";

    public SqlLogsCierres(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private final String TABLE_Logs_Cierres = "logs_cierres";
    private final String COLUMN_Logs_id = "logs_id";
    private final String COLUMN_Logs_tipoCierre = "logs_tipoCierre";
    private final String COLUMN_Logs_numLote = "logs_numLote";
    private final String COLUMN_Logs_fechaUltimoCierre = "logs_fechaUltimoCierre";
    private final String COLUMN_Logs_fechaCierre = "logs_fechaCierre";
    private final String COLUMN_Logs_discriminadoComercios = "logs_discriminadoComercios";
    private final String COLUMN_Logs_cantCredito = "logs_cantCredito";
    private final String COLUMN_Logs_totalCredito = "logs_totalCredito";
    private final String COLUMN_Logs_cantDebito = "logs_cantDebito";
    private final String COLUMN_Logs_totalDebito = "logs_totalDebito";
    private final String COLUMN_Logs_cantMovil = "logs_cantMovil";
    private final String COLUMN_Logs_totalMovil = "logs_totalMovil";
    private final String COLUMN_Logs_cantAnular = "logs_cantAnular";
    private final String COLUMN_Logs_totalAnular = "logs_totalAnular";
    private final String COLUMN_Logs_cantVuelto = "logs_cantVuelto";
    private final String COLUMN_Logs_totalVuelto = "logs_totalVuelto";
    private final String COLUMN_Logs_cantSaldo = "logs_cantSaldo";
    private final String COLUMN_Logs_totalSaldo = "logs_totalSaldo";
    private final String COLUMN_Logs_cantGeneral = "logs_cantGeneral";
    private final String COLUMN_Logs_totalGeneral = "logs_totalGeneral";

    private String CreateTableLogsCierres =
            "CREATE TABLE " + TABLE_Logs_Cierres + " (" +
                    COLUMN_Logs_id + " TEXT NOT NULL, " +
                    COLUMN_Logs_tipoCierre + " TEXT NOT NULL, " +
                    COLUMN_Logs_numLote + " TEXT, " +
                    COLUMN_Logs_fechaUltimoCierre + " TEXT, " +
                    COLUMN_Logs_fechaCierre + " TEXT, " +
                    COLUMN_Logs_discriminadoComercios + " TEXT, " +
                    COLUMN_Logs_cantCredito + " TEXT, " +
                    COLUMN_Logs_totalCredito + " TEXT, " +
                    COLUMN_Logs_cantDebito + " TEXT, " +
                    COLUMN_Logs_totalDebito + " TEXT, " +
                    COLUMN_Logs_cantMovil + " TEXT, " +
                    COLUMN_Logs_totalMovil + " TEXT, " +
                    COLUMN_Logs_cantAnular + " TEXT, " +
                    COLUMN_Logs_totalAnular + " TEXT, " +
                    COLUMN_Logs_cantVuelto + " TEXT, " +
                    COLUMN_Logs_totalVuelto + " TEXT, " +
                    COLUMN_Logs_cantSaldo + " TEXT, " +
                    COLUMN_Logs_totalSaldo + " TEXT, " +
                    COLUMN_Logs_cantGeneral + " TEXT, " +
                    COLUMN_Logs_totalGeneral + " TEXT" +
                    ")";

    private String TABLE_Logs_Cierres_Detallado = "logs_cierres_detallado";
    private String COLUMN_Logs_cargo = "logs_cargo";
    private String COLUMN_Logs_num_boleta = "logs_boleta";
    private String COLUMN_Logs_monto = "logs_monto";
    private String COLUMN_Logs_fecha = "logs_fecha";
    private String COLUMN_Logs_hora = "logs_hora";
    private String COLUMN_Logs_trans = "logs_trans";
    private String COLUMN_Logs_tarjeta = "logs_tarjeta";
    private String COLUMN_logs_tipoTarjeta = "logs_tipoTarjeta";
    private String COLUMN_Logs_idCierre = "logs_idCierre";

    private String CreateTableCierreDetallado =
            "CREATE TABLE " + TABLE_Logs_Cierres_Detallado + " (" +
                    COLUMN_Logs_tarjeta + " TEXT, " +
                    COLUMN_Logs_cargo + " TEXT, " +
                    COLUMN_Logs_num_boleta + " TEXT, " +
                    COLUMN_Logs_monto + " TEXT, " +
                    COLUMN_Logs_fecha + " TEXT, " +
                    COLUMN_Logs_hora + " TEXT, " +
                    COLUMN_Logs_trans + " TEXT, " +
                    COLUMN_logs_tipoTarjeta+ " TEXT, " +
                    COLUMN_Logs_idCierre + " TEXT NOT NULL " +
                    ")";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CreateTableLogsCierres);
        db.execSQL(CreateTableCierreDetallado);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean ingresarRegistroLogs(LogsCierreDetalladoModelo logsCierreDetalladoModelo) {
        boolean ret = false;
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (logsCierreDetalladoModelo.getTarjeta() != null) {
            values.put(COLUMN_Logs_tarjeta, logsCierreDetalladoModelo.getTarjeta());
        }
        if (logsCierreDetalladoModelo.getCargo() != null) {
            values.put(COLUMN_Logs_cargo, logsCierreDetalladoModelo.getCargo());
        }
        if (logsCierreDetalladoModelo.getNumBoleta() != null) {
            values.put(COLUMN_Logs_num_boleta, logsCierreDetalladoModelo.getNumBoleta());
        }
        if (logsCierreDetalladoModelo.getMonto() != null) {
            values.put(COLUMN_Logs_monto, logsCierreDetalladoModelo.getMonto());
        }
        if (logsCierreDetalladoModelo.getFecha() != null) {
            values.put(COLUMN_Logs_fecha, logsCierreDetalladoModelo.getFecha());
        }
        if (logsCierreDetalladoModelo.getHora() != null) {
            values.put(COLUMN_Logs_hora, logsCierreDetalladoModelo.getHora());
        }
        if (logsCierreDetalladoModelo.getTrans() != null) {
            values.put(COLUMN_Logs_trans, logsCierreDetalladoModelo.getTrans());
        }
        if (logsCierreDetalladoModelo.getTipoTarjeta() != null) {
            values.put(COLUMN_logs_tipoTarjeta, logsCierreDetalladoModelo.getTipoTarjeta());
        }
        if (logsCierreDetalladoModelo.getIdCierre() != null) {
            values.put(COLUMN_Logs_idCierre, logsCierreDetalladoModelo.getIdCierre());
        }

        try {
            db.insert(TABLE_Logs_Cierres_Detallado, null, values);
            db.close();
            ret = true;
        } catch (SQLException e) {
          Logger.exception(clase, e);
            e.getCause();
        }
        return ret;
    }

    public boolean ingresarRegistroLogs(LogsCierresModelo logsCierresModelo) {
        boolean ret = false;
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_Logs_id, logsCierresModelo.getId());
        if (logsCierresModelo.getTipoCierre() != null) {
            values.put(COLUMN_Logs_tipoCierre, logsCierresModelo.getTipoCierre());
        }
        if (logsCierresModelo.getNumLote() != null) {
            values.put(COLUMN_Logs_numLote, logsCierresModelo.getNumLote());
        }
        if (logsCierresModelo.getFechaUltimoCierre() != null) {
            values.put(COLUMN_Logs_fechaUltimoCierre, logsCierresModelo.getFechaUltimoCierre());
        }
        if (logsCierresModelo.getFechaCierre() != null) {
            values.put(COLUMN_Logs_fechaCierre, logsCierresModelo.getFechaCierre());
        }
        if (logsCierresModelo.getDiscriminadoComercios() != null) {
            values.put(COLUMN_Logs_discriminadoComercios, logsCierresModelo.getDiscriminadoComercios());
        }
        if (logsCierresModelo.getCantCredito() != null) {
            values.put(COLUMN_Logs_cantCredito, logsCierresModelo.getCantCredito());
        }
        if (logsCierresModelo.getTotalCredito() != null) {
            values.put(COLUMN_Logs_totalCredito, logsCierresModelo.getTotalCredito());
        }
        if (logsCierresModelo.getCantDebito() != null) {
            values.put(COLUMN_Logs_cantDebito, logsCierresModelo.getCantDebito());
        }
        if (logsCierresModelo.getTotalDebito() != null) {
            values.put(COLUMN_Logs_totalDebito, logsCierresModelo.getTotalDebito());
        }
        if (logsCierresModelo.getCantMovil() != null) {
            values.put(COLUMN_Logs_cantMovil, logsCierresModelo.getCantMovil());
        }
        if (logsCierresModelo.getTotalMovil() != null) {
            values.put(COLUMN_Logs_totalMovil, logsCierresModelo.getTotalMovil());
        }
        if (logsCierresModelo.getCantAnular() != null) {
            values.put(COLUMN_Logs_cantAnular, logsCierresModelo.getCantAnular());
        }
        if (logsCierresModelo.getTotalAnular() != null) {
            values.put(COLUMN_Logs_totalAnular, logsCierresModelo.getTotalAnular());
        }
        if (logsCierresModelo.getCantVuelto() != null) {
            values.put(COLUMN_Logs_cantVuelto, logsCierresModelo.getCantVuelto());
        }
        if (logsCierresModelo.getTotalVuelto() != null) {
            values.put(COLUMN_Logs_totalVuelto, logsCierresModelo.getTotalVuelto());
        }
        if (logsCierresModelo.getCantSaldo() != null) {
            values.put(COLUMN_Logs_cantSaldo, logsCierresModelo.getCantSaldo());
        }
        if (logsCierresModelo.getTotalSaldo() != null) {
            values.put(COLUMN_Logs_totalSaldo, logsCierresModelo.getTotalSaldo());
        }
        if (logsCierresModelo.getCantGeneral() != null) {
            values.put(COLUMN_Logs_cantGeneral, logsCierresModelo.getCantGeneral());
        }
        if (logsCierresModelo.getTotalGeneral() != null) {
            values.put(COLUMN_Logs_totalGeneral, logsCierresModelo.getTotalGeneral());
        }
        try {
            db.insert(TABLE_Logs_Cierres, null, values);
            db.close();
            ret = true;
        } catch (SQLException e) {
          Logger.exception(clase, e);
            e.getCause();
        }
        return ret;
    }

    public boolean actualizarLogCierre(LogsCierresModelo logsCierresModelo) {
        boolean ret = false;
        db = this.getWritableDatabase();

        System.out.printf("Paramtero id =  %s%n ", logsCierresModelo.getId());
        String[] parametros = {logsCierresModelo.getId()};

        ContentValues values = new ContentValues();
        if (logsCierresModelo.getTipoCierre() != null) {
            values.put(COLUMN_Logs_tipoCierre, logsCierresModelo.getTipoCierre());
        }
        if (logsCierresModelo.getId() != null) {
            values.put(COLUMN_Logs_fechaCierre, logsCierresModelo.getNuevaFecha());
        }

        try {
            db.update(TABLE_Logs_Cierres, values, COLUMN_Logs_id + "=?", parametros);
            db.close();
            ret = true;
        } catch (SQLException e) {
          Logger.exception(clase, e);
            e.printStackTrace();
        }
        return ret;
    }

    public List<LogsCierresModelo> getAllLogsCierre() {
        db = this.getWritableDatabase();
        List<LogsCierresModelo> logsCierres = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_Logs_Cierres;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToLast()) {
            do {
                LogsCierresModelo logsCierre = new LogsCierresModelo();
                logsCierre.setId(cursor.getString(cursor.getColumnIndex(COLUMN_Logs_id)));
                logsCierre.setTipoCierre(cursor.getString(cursor.getColumnIndex(COLUMN_Logs_tipoCierre)));
                logsCierre.setNumLote(cursor.getString(cursor.getColumnIndex(COLUMN_Logs_numLote)));
                logsCierre.setFechaUltimoCierre(cursor.getString(cursor.getColumnIndex(COLUMN_Logs_fechaUltimoCierre)));
                logsCierre.setFechaCierre(cursor.getString(cursor.getColumnIndex(COLUMN_Logs_fechaCierre)));
                logsCierre.setDiscriminadoComercios(cursor.getString(cursor.getColumnIndex(COLUMN_Logs_discriminadoComercios)));
                logsCierre.setCantCredito(checkNullZero(cursor.getString(cursor.getColumnIndex(COLUMN_Logs_cantCredito))));
                logsCierre.setTotalCredito(checkNullZero(cursor.getString(cursor.getColumnIndex(COLUMN_Logs_totalCredito))));
                logsCierre.setCantDebito(checkNullZero(cursor.getString(cursor.getColumnIndex(COLUMN_Logs_cantDebito))));
                logsCierre.setTotalDebito(checkNullZero(cursor.getString(cursor.getColumnIndex(COLUMN_Logs_totalDebito))));
                logsCierre.setCantMovil(checkNullZero(cursor.getString(cursor.getColumnIndex(COLUMN_Logs_cantMovil))));
                logsCierre.setTotalMovil(checkNullZero(cursor.getString(cursor.getColumnIndex(COLUMN_Logs_totalMovil))));
                logsCierre.setCantAnular(checkNullZero(cursor.getString(cursor.getColumnIndex(COLUMN_Logs_cantAnular))));
                logsCierre.setTotalAnular(checkNullZero(cursor.getString(cursor.getColumnIndex(COLUMN_Logs_totalAnular))));
                logsCierre.setCantVuelto(checkNullZero(cursor.getString(cursor.getColumnIndex(COLUMN_Logs_cantVuelto))));
                logsCierre.setTotalVuelto(checkNullZero(cursor.getString(cursor.getColumnIndex(COLUMN_Logs_totalVuelto))));
                logsCierre.setCantSaldo(checkNullZero(cursor.getString(cursor.getColumnIndex(COLUMN_Logs_cantSaldo))));
                logsCierre.setTotalSaldo(checkNullZero(cursor.getString(cursor.getColumnIndex(COLUMN_Logs_totalSaldo))));
                logsCierre.setCantGeneral(checkNullZero(cursor.getString(cursor.getColumnIndex(COLUMN_Logs_cantGeneral))));
                logsCierre.setTotalGeneral(checkNullZero(cursor.getString(cursor.getColumnIndex(COLUMN_Logs_totalGeneral))));

                logsCierres.add(logsCierre);
            } while (cursor.moveToPrevious());
        }
        cursor.close();
        db.close();

        return logsCierres;
    }


    public List<TransLogData> getAllLogsCierreDetallado(Activity context, String numLote) {
        List<TransLogData> logsCierredetallado = null;
        try {
            db = this.getWritableDatabase();
            logsCierredetallado = new ArrayList<>();
            String query = "SELECT * FROM " + TABLE_Logs_Cierres_Detallado + " WHERE " + COLUMN_Logs_idCierre + " = " + "'" + numLote + "'";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    TransLogData transLogData = new TransLogData();
                    transLogData.setPan(cursor.getString(cursor.getColumnIndex(COLUMN_Logs_tarjeta)));
                    transLogData.setNroCargo(cursor.getString(cursor.getColumnIndex(COLUMN_Logs_cargo)));
                    transLogData.setRRN(cursor.getString(cursor.getColumnIndex(COLUMN_Logs_num_boleta)));
                    transLogData.setAmount(Long.parseLong(cursor.getString(cursor.getColumnIndex(COLUMN_Logs_monto))));
                    transLogData.setLocalDate(cursor.getString(cursor.getColumnIndex(COLUMN_Logs_fecha)));
                    transLogData.setLocalTime(cursor.getString(cursor.getColumnIndex(COLUMN_Logs_hora)));
                    transLogData.setEName(cursor.getString(cursor.getColumnIndex(COLUMN_Logs_trans)));
                    transLogData.setTipoTarjeta(cursor.getString(cursor.getColumnIndex(COLUMN_logs_tipoTarjeta)));
                    logsCierredetallado.add(transLogData);
                } while (cursor.moveToPrevious());

            }
            cursor.close();
            db.close();
        } catch (Exception e) {
          Logger.exception(clase, e);
            e.printStackTrace();
            ISOUtil.AlertExcepciones("Error cierre detallado \n" + e.getMessage(), context);
        }


        return logsCierredetallado;
    }


    public List<LogsCierreDetalladoModelo> getAllLogsCierreDetalladoMigracion() {
        db = this.getWritableDatabase();
        List<LogsCierreDetalladoModelo> logDetallado = new ArrayList<>();
        String query = "SELECT logs_tarjeta, logs_cargo, logs_boleta, logs_monto, logs_fecha, logs_hora, logs_trans, logs_tipoTarjeta, logs_idCierre FROM logs_cierres_detallado";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToLast()) {
            do {
                LogsCierreDetalladoModelo transLogData = new LogsCierreDetalladoModelo();

                transLogData.setTarjeta(cursor.getString(cursor.getColumnIndex(COLUMN_Logs_tarjeta)));
                transLogData.setCargo(cursor.getString(cursor.getColumnIndex(COLUMN_Logs_cargo)));
                transLogData.setNumBoleta(cursor.getString(cursor.getColumnIndex(COLUMN_Logs_num_boleta)));
                transLogData.setMonto(cursor.getString(cursor.getColumnIndex(COLUMN_Logs_monto)));
                transLogData.setFecha(cursor.getString(cursor.getColumnIndex(COLUMN_Logs_fecha)));
                transLogData.setHora(cursor.getString(cursor.getColumnIndex(COLUMN_Logs_hora)));
                transLogData.setTrans(cursor.getString(cursor.getColumnIndex(COLUMN_Logs_trans)));
                transLogData.setTipoTarjeta(cursor.getString(cursor.getColumnIndex(COLUMN_logs_tipoTarjeta)));
                transLogData.setIdCierre(cursor.getString(cursor.getColumnIndex(COLUMN_Logs_idCierre)));
                logDetallado.add(transLogData);
            } while (cursor.moveToPrevious());
        }
        cursor.close();
        db.close();

        return logDetallado;
    }

    public void getEliminarBaseDatos(Context context) {
        try {
            context.deleteDatabase(DATABASE_NAME);
        } catch (Exception e) {
          Logger.exception(clase, e);
            ISOUtil.AlertExcepciones("Error Eliminacion Base Datos Cierre \n" + e.getMessage(), context);
            e.printStackTrace();
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
