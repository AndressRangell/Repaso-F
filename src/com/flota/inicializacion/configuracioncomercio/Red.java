package com.flota.inicializacion.configuracioncomercio;

import static com.flota.inicializacion.trans_init.Init.NAME_DB;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.flota.inicializacion.trans_init.trans.DbHelper;
import com.newpos.libpay.Logger;

public class Red {

    private static final String TAG = "Red.java";
    private static final String TABLA_BASE_DATOS_RED = "RED";
    private static final String COLUMNA_CLAVE_TECNICO = "clave_tecnico";
    static Red tablaRed;
    String claveTecnico;
    String[] listadoColumnasSQL = new String[]{
            COLUMNA_CLAVE_TECNICO
    };

    public static Red getInstance(boolean isBorrarInfo) {
        if (isBorrarInfo) {
            tablaRed = null;
        }
        if (tablaRed == null) {
            tablaRed = new Red();
        }
        return tablaRed;
    }

    private String consultaSQL(String[] listadoColumnasSQL) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");

        for (int i = 0; i < listadoColumnasSQL.length; i++) {
            sql.append(listadoColumnasSQL[i]);
            if (i < (listadoColumnasSQL.length - 1)) {
                sql.append(", ");
            }
        }
        sql.append(" FROM ");
        sql.append(TABLA_BASE_DATOS_RED);

        return sql.toString();
    }

    public boolean inicializandoComponentes(Context context) {

        DbHelper databaseAccess = new DbHelper(context, NAME_DB, null, 1);
        databaseAccess.openDb(NAME_DB);


        String sql = consultaSQL(listadoColumnasSQL);

        try {
            Cursor cursor = databaseAccess.rawQuery(sql, null);
            cursor.moveToFirst();
            int indexColumn;

            while (!cursor.isAfterLast()) {
                tablaRed = new Red();
                tablaRed.clearAPP();
                indexColumn = 0;
                for (String s : listadoColumnasSQL) {
                    tablaRed.setAPP(s, cursor.getString(indexColumn++).trim());
                }
                cursor.moveToNext();
            }
            cursor.close();
            return true;
        } catch (Exception ex) {
          Logger.exception(TAG, ex);
            ex.printStackTrace();
            Logger.error(TAG, ex);

        }
        databaseAccess.closeDb();
        return false;
    }

    public void clearAPP() {
        for (String s : APLICACIONES.fields) {
            setAPP(s, "");
        }
    }

    private void setAPP(String column, String value) {
        if (COLUMNA_CLAVE_TECNICO.equals(column)) {
            setClaveTecnico(value);
        }
    }

    public String getClaveTecnico() {
        return claveTecnico;
    }

    public void setClaveTecnico(String claveTecnico) {
        this.claveTecnico = claveTecnico;
    }
}
