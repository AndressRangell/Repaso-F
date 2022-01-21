package com.flota.inicializacion.configuracioncomercio;

import static com.flota.actividades.StartAppBANCARD.tablaTransacciones;
import static com.flota.defines_bancard.DefinesBANCARD.POLARIS_APP_NAME;
import static com.flota.inicializacion.trans_init.Init.NAME_DB;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.flota.inicializacion.trans_init.trans.DbHelper;
import com.newpos.libpay.Logger;
import com.wposs.flota.R;

import java.util.ArrayList;

import cn.desert.newpos.payui.UIUtils;

public class TRANSACCIONES {

    public static final String NAME_TABLE = "TRANSACCIONES";
    private static final String TAG = "TRANSACCIONES.java";
    private static final String NAME_PLANTILLA_ID = "PLANTILLA_ID";
    private static final String NAME_TRANSACCION_ID = "TRANSACCION_ID";
    private static final String NAME_NOMBRE = "NOMBRE";
    private static final String NAME_HABILITAR = "HABILITAR";
    private static final String NAME_CASHBACK = "CASHBACK";
    private static final String NAME_CASHBACK_MONTO = "CASHBACK_MONTO";
    private static final String NAME_MONTO_MAXIMO_TRANSACCION = "MONTO_MAXIMO_TRANSACCION";
    private static final String NAME_MONTO_MINIMO_TRANSACCION = "MONTO_MINIMO_TRANSACCION";
    private static final String NAME_CAJA_POS = "CAJA_POS";
    private static final String[] fields = new String[]{
            NAME_PLANTILLA_ID,
            NAME_TRANSACCION_ID,
            NAME_NOMBRE,
            NAME_HABILITAR,
            NAME_CASHBACK,
            NAME_CASHBACK_MONTO,
            NAME_MONTO_MAXIMO_TRANSACCION,
            NAME_MONTO_MINIMO_TRANSACCION,
            NAME_MONTO_MINIMO_TRANSACCION,
            NAME_CAJA_POS
    };

    private String plantillaId;
    private String transaccionId;
    private String nombre;
    private boolean habilitar;
    private boolean cashback;
    private String cashbackMonto;
    private String montoMaximoTransaccion;
    private String montoMinimoTransaccion;
    private String cajaPos;
    private Context context;


    public TRANSACCIONES() {
    }

    public TRANSACCIONES(Context context) {
        this.context = context;
    }

    public static TRANSACCIONES getSingletonInstance(Context context) {
        if (tablaTransacciones == null) {
            tablaTransacciones = new TRANSACCIONES(context);
        } else {
            Logger.debug(TAG, "No se puede crear otro objeto, ya existe");
        }
        return tablaTransacciones;
    }

    public String getPlantillaId() {
        return plantillaId;
    }

    public void setPlantillaId(String plantillaId) {
        this.plantillaId = plantillaId;
    }

    public String getTransaccionId() {
        return transaccionId;
    }

    public void setTransaccionId(String transaccionId) {
        this.transaccionId = transaccionId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean getHabilitar() {
        return habilitar;
    }

    public void setHabilitar(String habilitar) {
        if (!habilitar.isEmpty()) {
            if (habilitar.equals("true") || habilitar.equals("false")) {
                this.habilitar = Boolean.parseBoolean(habilitar);
            } else {
                showMensaje(" Error en la obtención de las transacciones ");
                this.habilitar = false;
            }
        }
    }

    public boolean getCashback() {
        return cashback;
    }

    public void setCashback(String cashback) {
        if (!cashback.isEmpty()) {
            if (cashback.equals("true") || cashback.equals("false")) {
                this.cashback = Boolean.parseBoolean(cashback);
            } else {
                this.cashback = false;
                showMensaje("Error en la obtención del CASHBACK");
            }
        }

    }

    public String getCashbackMonto() {
        return cashbackMonto;
    }

    public void setCashbackMonto(String cashbackMonto) {
        this.cashbackMonto = cashbackMonto;
    }

    public String getMontoMaximoTransaccion() {
        return montoMaximoTransaccion;
    }

    public void setMontoMaximoTransaccion(String montoMaximoTransaccion) {
        this.montoMaximoTransaccion = montoMaximoTransaccion;
    }

    public String getMontoMinimoTransaccion() {
        return montoMinimoTransaccion;
    }

    public void setMontoMinimoTransaccion(String montoMinimoTransaccion) {
        this.montoMinimoTransaccion = montoMinimoTransaccion;
    }

    public String getCajaPos() {
        return cajaPos;
    }

    public void setCajaPos(String cajaPos) {
        this.cajaPos = cajaPos;
    }

    private void setTRANSACCIONES(String column, String value) {
        switch (column) {
            case NAME_PLANTILLA_ID:
                setPlantillaId(value);
                break;
            case NAME_TRANSACCION_ID:
                setTransaccionId(value);
                break;
            case NAME_NOMBRE:
                setNombre(value);
                break;
            case NAME_HABILITAR:
                setHabilitar(value);
                break;
            case NAME_CASHBACK:
                setCashback(value);
                break;
            case NAME_CASHBACK_MONTO:
                setCashbackMonto(value);
                break;
            case NAME_MONTO_MAXIMO_TRANSACCION:
                setMontoMaximoTransaccion(value);
                break;
            case NAME_MONTO_MINIMO_TRANSACCION:
                setMontoMinimoTransaccion(value);
                break;
            case NAME_CAJA_POS:
                setCajaPos(value);
                break;


            default:
                break;
        }
    }

    public void clearTRANSACCIONES() {
        for (String s : TRANSACCIONES.fields) {
            setTRANSACCIONES(s, "");
        }
    }

    /**
     * Consulta en la base de datos UNICAMENTE las tranascciones correspondientes a este aplicativo
     *
     * @param context
     * @return
     */
    public ArrayList<TRANSACCIONES> selectTRANSACCIONES(Context context) {
        boolean apps = false;
        DbHelper databaseAccess = new DbHelper(context, NAME_DB, null, 1);
        databaseAccess.openDb(NAME_DB);

        ArrayList<TRANSACCIONES> aLp = new ArrayList<>();

        String sql = consultaSQL();
        try {
            Cursor cursor = databaseAccess.rawQuery(sql, null);
            cursor.moveToFirst();
            int indexColumn;
            TRANSACCIONES transacciones;
            while (!cursor.isAfterLast()) {
                transacciones = new TRANSACCIONES(context);
                transacciones.clearTransAllow();
                indexColumn = 0;
                for (String s : fields) {
                    transacciones.setTRANSACCIONES(s, cursor.getString(indexColumn++).trim());
                }
                apps = true;
                cursor.moveToNext();
                aLp.add(transacciones);
            }
            cursor.close();
        } catch (Exception ex) {
          Logger.exception(TAG, ex);
            Logger.error(TAG, ex);
        }
        databaseAccess.closeDb();

        if (!apps)
            aLp = null;
        return aLp;
    }

    /**
     * Obtiene la cadena sql para consultar en la base de datos
     * OBSERVACION : trae unicamente las transaccion correspondientes al grupo de transacciones de este aplicativo debito/credito
     *
     * @return
     */
    private String consultaSQL() {
        StringBuilder sql = new StringBuilder();
        StringBuilder auxCampos = new StringBuilder();
        int counter = 1;
        for (String s : TRANSACCIONES.fields) {
            auxCampos.append(s);
            if (counter++ < TRANSACCIONES.fields.length) {
                auxCampos.append(", ");
            }
        }

        sql.append("SELECT ");
        sql.append(auxCampos.toString());
        sql.append(" FROM TRANSACCIONES");
        sql.append(" WHERE PLANTILLA_ID IN (");
        sql.append(" SELECT GRUPO_TRANSACCIONES FROM APLICACIONES");
        sql.append(" WHERE NAME_APP = '" + POLARIS_APP_NAME + "')");

        return sql.toString();
    }

    public void clearTransAllow() {
        for (String s : fields) {
            setTRANSACCIONES(s, "");
        }
    }

    private void showMensaje(String mensaje) {
        if (context != null)
            UIUtils.toast((Activity) context, R.drawable.ic_redinfonet, mensaje, Toast.LENGTH_LONG);
        else
            Logger.debug("Info", "showMensaje: " + "context == null");
    }

}
