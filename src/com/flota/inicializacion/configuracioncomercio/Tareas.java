package com.flota.inicializacion.configuracioncomercio;

import static com.flota.inicializacion.trans_init.Init.NAME_DB;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.flota.inicializacion.trans_init.trans.DbHelper;
import com.newpos.libpay.Logger;

import java.util.ArrayList;
import java.util.List;

public class Tareas {

    private static final String TAG = "Tareas.java";
    private static final String TABLA_BASE_DATOS_TAREA = "tareas";
    private static final String COLUMNA_TAREA = "tarea";
    private static final String COLUMNA_APLICACION_ID = "aplicacionid";
    private static final String COLUMNA_APLICACION = "aplicacion";
    private static final String COLUMNA_CODIGO_TAREA = "codigotarea";
    private static final String COLUMNA_DESCRIPCION = "descripcion";

    static Tareas tablaTareas;
    List<Tareas> listadoTareas;
    String tarea;
    String aplicacionid;
    String aplicacion;
    String codigotarea;
    String descripcion;
    String[] listadoColumnasSQL = new String[]{
            COLUMNA_TAREA,
            COLUMNA_APLICACION_ID,
            COLUMNA_APLICACION,
            COLUMNA_CODIGO_TAREA,
            COLUMNA_DESCRIPCION
    };

    public static Tareas getInstance(boolean isBorrarInfo) {
        if (isBorrarInfo) {
            tablaTareas = null;
        }
        if (tablaTareas == null) {
            tablaTareas = new Tareas();
        }
        return tablaTareas;
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
        sql.append(TABLA_BASE_DATOS_TAREA);

        return sql.toString();
    }

    public boolean inicializandoComponentes(Context context) {

        DbHelper databaseAccess = new DbHelper(context, NAME_DB, null, 1);
        databaseAccess.openDb(NAME_DB);


        String sql = consultaSQL(listadoColumnasSQL);


        try {
            Cursor cursor = databaseAccess.rawQuery(sql, null);
            listadoTareas = new ArrayList<>();
            cursor.moveToFirst();
            int indexColumn;

            while (!cursor.isAfterLast()) {
                Tareas tareas = new Tareas();
                tareas.clearAPP();
                indexColumn = 0;
                for (String s : listadoColumnasSQL) {
                    tareas.setAPP(s, cursor.getString(indexColumn++).trim());
                }
                cursor.moveToNext();
                listadoTareas.add(tareas);
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
        switch (column) {
            case COLUMNA_TAREA:
                setTarea(value);
                break;
            case COLUMNA_APLICACION_ID:
                setAplicacionid(value);
                break;
            case COLUMNA_APLICACION:
                setAplicacion(value);
                break;
            case COLUMNA_CODIGO_TAREA:
                setCodigotarea(value);
                break;
            case COLUMNA_DESCRIPCION:
                setDescripcion(value);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + column);
        }
    }

    public List<Tareas> getListadoTarea2Aplicacion(String nombreAplicacion) {
        List<Tareas> tareasList = new ArrayList<>();
        if (listadoTareas != null) {
            for (Tareas tareas : listadoTareas) {
                if (tareas != null && !tareas.getAplicacion().isEmpty() && tareas.getAplicacion().contains(nombreAplicacion)) {
                    tareasList.add(tareas);
                }
            }
        }
        return tareasList;
    }

    public String getTarea() {
        return tarea;
    }

    private void setTarea(String tarea) {
        this.tarea = tarea;
    }

    public String getAplicacionid() {
        return aplicacionid;
    }

    private void setAplicacionid(String aplicacionid) {
        this.aplicacionid = aplicacionid;
    }

    public String getAplicacion() {
        return aplicacion;
    }

    private void setAplicacion(String aplicacion) {
        this.aplicacion = aplicacion;
    }

    public String getCodigotarea() {
        return codigotarea;
    }

    private void setCodigotarea(String codigotarea) {
        this.codigotarea = codigotarea;
    }

    public String getDescripcion() {
        return descripcion;
    }

    private void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
