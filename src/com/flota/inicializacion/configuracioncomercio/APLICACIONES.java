package com.flota.inicializacion.configuracioncomercio;

import static com.flota.inicializacion.trans_init.Init.NAME_DB;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.flota.inicializacion.trans_init.trans.DbHelper;
import com.newpos.libpay.Logger;
import com.wposs.flota.R;

import java.util.ArrayList;

import cn.desert.newpos.payui.UIUtils;

public class APLICACIONES {

    public static final String NAME_TABLE = "APLICACIONES";
    private static final String NAME_PLANTILLA_ID = "PLANTILLA_ID";
    private static final String NAME_APLICACION_ID = "APLICACION_ID";
    private static final String NAME_NAME_APP = "NAME_APP";
    private static final String NAME_ACTIVE = "ACTIVE";
    private static final String NAME_IMPRIMIR_COPIA_COMERCIO = "IMPRIMIR_COPIA_COMERCIO";
    private static final String NAME_GRUPO_TRANSACCIONES = "GRUPO_TRANSACCIONES";
    private static final String NAME_MAX_NUMERO_TXN_CIERRE = "MAX_NUMERO_TXN_CIERRE";
    private static final String NAME_NII_TRANSACCIONES = "NII_TRANSACCIONES";
    private static final String NAME_LOG_EXCEPCIONES = "logExcepciones";
    private static final String NAME_LOG_TIMER = "logTimer";
    private static final String NAME_LOG_FLUJO = "logFlujo";
    private static final String NAME_LOG_COMUNICACION = "logComunicacion";
    private static final String NAME_LOG_IMPRESION = "logImpresion";
    private static final String NAME_REVERSO = "REVERSO";
    protected static final String[] fields = new String[]{
            NAME_PLANTILLA_ID,
            NAME_APLICACION_ID,
            NAME_NAME_APP,
            NAME_ACTIVE,
            NAME_IMPRIMIR_COPIA_COMERCIO,
            NAME_GRUPO_TRANSACCIONES,
            NAME_MAX_NUMERO_TXN_CIERRE,
            NAME_NII_TRANSACCIONES,
            NAME_LOG_EXCEPCIONES,
            NAME_LOG_TIMER,
            NAME_LOG_FLUJO,
            NAME_LOG_COMUNICACION,
            NAME_LOG_IMPRESION,
            NAME_REVERSO
    };
    private static final String TAG = "Aplicaciones";
    static ArrayList<APLICACIONES> listadoAplicaciones = null;
    private static Context context;
    private static APLICACIONES aplicacionActual = null;
    private String plantillaId;
    private String aplicacionId;
    private String nameApp;
    private String active;
    private String imprimirCopiaComercio;
    private String grupoTransacciones;
    private String maxNumeroTxnCierre;
    private String niiTransacciones;
    private boolean logExcepciones;
    private boolean logTimer;
    private boolean logFlujo;
    private boolean logComunicacion;
    private boolean logImpresion;
    private boolean reverso;

    private APLICACIONES() {
    }

    public static boolean checksAppsActive(Context context) {
        APLICACIONES.context = context;
        DbHelper databaseAccess = new DbHelper(context, NAME_DB, null, 1);
        databaseAccess.openDb(NAME_DB);


        String sql = consultaSQL();

        try {
            Cursor cursor = databaseAccess.rawQuery(sql, null);
            cursor.moveToFirst();
            int indexColumn;
            APLICACIONES aplicaciones;
            listadoAplicaciones.clear();
            while (!cursor.isAfterLast()) {
                aplicaciones = new APLICACIONES();
                aplicaciones.clearAPP();
                indexColumn = 0;
                for (String s : fields) {
                    aplicaciones.setAPP(s, cursor.getString(indexColumn++).trim());
                }
                cursor.moveToNext();
                listadoAplicaciones.add(aplicaciones);
            }
            cursor.close();
            return true;
        } catch (Exception ex) {
            Logger.error(TAG, ex);
            Logger.exception( "APLICACIONES.java", ex);
        }
        databaseAccess.closeDb();

        return false;
    }

    private static String consultaSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM " + NAME_TABLE);
        return sb.toString();
    }

    public static ArrayList<APLICACIONES> getSingletonInstance() {
        if (listadoAplicaciones == null) {
            listadoAplicaciones = new ArrayList<>();
        }
        return listadoAplicaciones;
    }

    public static APLICACIONES getSingletonInstanceAppActual(String nameApp) {
        if (aplicacionActual == null) {
            aplicacionActual = appActual(nameApp);
        }
        return aplicacionActual;
    }

    /**
     * @param nameApp
     * @return
     */
    public static APLICACIONES appActual(String nameApp) {
        if (aplicacionActual == null && listadoAplicaciones != null) {
            for (APLICACIONES app : listadoAplicaciones) {
                if (app.getNameApp().contains(nameApp)) {
                    aplicacionActual = app;
                    break;
                }
            }
        }
        return aplicacionActual;
    }

    public static void setAplicacionesNull() {
        APLICACIONES.listadoAplicaciones = null;
        APLICACIONES.aplicacionActual = null;
    }

    private void setAPP(String column, String value) {
        switch (column) {
            case NAME_PLANTILLA_ID:
                setPlantillaId(value);
                break;
            case NAME_APLICACION_ID:
                setAplicacionId(value);
                break;
            case NAME_NAME_APP:
                setNameApp(value);
                break;
            case NAME_ACTIVE:
                setActive(value);
                break;
            case NAME_IMPRIMIR_COPIA_COMERCIO:
                setImprimirCopiaComercio(value);
                break;
            case NAME_GRUPO_TRANSACCIONES:
                setGrupoTransacciones(value);
                break;
            case NAME_MAX_NUMERO_TXN_CIERRE:
                setMaxNumeroTxnCierre(value);
                break;
            case NAME_NII_TRANSACCIONES:
                setNiiTransacciones(value);
                break;
            case NAME_LOG_EXCEPCIONES:
                setLogExcepciones(value);
                break;
            case NAME_LOG_TIMER:
                setLogTimer(value);
                break;
            case NAME_LOG_FLUJO:
                setLogFlujo(value);
                break;
            case NAME_LOG_COMUNICACION:
                setLogComunicacion(value);
                break;
            case NAME_LOG_IMPRESION:
                setLogImpresion(value);
                break;
            case NAME_REVERSO:
                setReverso(value);
                break;

            default:
                break;
        }
    }

    public void clearAPP() {
        for (String s : APLICACIONES.fields) {
            setAPP(s, "");
        }
    }

    public String getAplicacionId() {
        return aplicacionId;
    }

    public void setAplicacionId(String aplicacionId) {
        this.aplicacionId = aplicacionId;
    }

    public String getNameApp() {
        return nameApp;
    }

    public void setNameApp(String nameApp) {
        this.nameApp = nameApp;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getPlantillaId() {
        return plantillaId;
    }

    public void setPlantillaId(String plantillaId) {
        this.plantillaId = plantillaId;
    }

    public String getImprimirCopiaComercio() {
        return imprimirCopiaComercio;
    }

    public void setImprimirCopiaComercio(String imprimirCopiaComercio) {
        this.imprimirCopiaComercio = imprimirCopiaComercio;
    }

    public String getGrupoTransacciones() {
        return grupoTransacciones;
    }

    public void setGrupoTransacciones(String grupoTransacciones) {
        this.grupoTransacciones = grupoTransacciones;
    }

    public String getMaxNumeroTxnCierre() {
        return maxNumeroTxnCierre;
    }

    public void setMaxNumeroTxnCierre(String maxNumeroTxnCierre) {
        this.maxNumeroTxnCierre = maxNumeroTxnCierre;
    }

    public String getNiiTransacciones() {
        return niiTransacciones;
    }

    public void setNiiTransacciones(String niiTransacciones) {
        this.niiTransacciones = niiTransacciones;
    }

    public boolean isLogExcepciones() {
        return logExcepciones;
    }

    public void setLogExcepciones(String logExcepciones) {
        this.logExcepciones = UIUtils.verificacionBoolean(context, logExcepciones, context.getString(R.string.application_table));
    }

    public boolean isLogTimer() {
        return logTimer;
    }

    public void setLogTimer(String logTimer) {
        this.logTimer = UIUtils.verificacionBoolean(context, logTimer, context.getString(R.string.application_table));
    }

    public boolean isLogFlujo() {
        return logFlujo;
    }

    public void setLogFlujo(String logFlujo) {
        this.logFlujo = UIUtils.verificacionBoolean(context, logFlujo, context.getString(R.string.application_table));
    }

    public boolean isLogComunicacion() {
        return logComunicacion;
    }

    public void setLogComunicacion(String logComunicacion) {
        this.logComunicacion = UIUtils.verificacionBoolean(context, logComunicacion, context.getString(R.string.application_table));
    }

    public boolean isLogImpresion() {
        return logImpresion;
    }

    public void setLogImpresion(String logImpresion) {
        this.logImpresion = UIUtils.verificacionBoolean(context, logImpresion, context.getString(R.string.application_table));
    }

    public boolean isReverso() {
        return reverso;
    }

    public void setReverso(String reverso) {
        this.reverso = UIUtils.verificacionBoolean(context, reverso, context.getString(R.string.application_table));
    }


}
