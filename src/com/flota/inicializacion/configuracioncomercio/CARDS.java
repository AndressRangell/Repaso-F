/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.flota.inicializacion.configuracioncomercio;


import static com.flota.actividades.StartAppBANCARD.tablaCards;
import static com.flota.inicializacion.trans_init.Init.NAME_DB;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.flota.inicializacion.trans_init.trans.DbHelper;
import com.newpos.libpay.Logger;
import com.wposs.flota.R;

import cn.desert.newpos.payui.UIUtils;

public class CARDS {
    private static final String NAME_PLANTILLA_ID = "PLANTILLA_ID";
    private static final String NAME_RANGO_ID = "RANGO_ID";
    private static final String NAME_LIMITE_INFERIOR = "LIMITE_INFERIOR";
    private static final String NAME_LIMITE_SUPERIOR = "LIMITE_SUPERIOR";
    private static final String NAME_ESTADO = "ESTADO";
    private static final String NAME_DESCRIPCION = "DESCRIPCION";
    private static final String NAME_REQUIERE_PIN = "REQUIERE_PIN";
    private static final String NAME_PIN_BYPASS = "PIN_BYPASS";
    private static final String NAME_CASHBACK = "CASHBACK";
    private static final String NAME_CASHBACK_MONTO = "CASHBACK_MONTO";
    private static final String NAME_TIPO = "TIPO";

    private static final String TRUE = "true";
    private static final String FALSE = "false";

    private static final String[] fields = new String[]{
            NAME_PLANTILLA_ID,
            NAME_RANGO_ID,
            NAME_LIMITE_INFERIOR,
            NAME_LIMITE_SUPERIOR,
            NAME_ESTADO,
            NAME_DESCRIPCION,
            NAME_REQUIERE_PIN,
            NAME_PIN_BYPASS,
            NAME_CASHBACK,
            NAME_CASHBACK_MONTO,
            NAME_TIPO
    };
    private static final String TAG = "CARDS.java";
    private String plantillaId;
    private String rangoId;
    private String limiteInferior;
    private String limiteSuperior;
    private boolean estado;
    private String descripcion;
    private boolean requierePin;
    private boolean pinBypass;
    private boolean cashback;
    private String cashbackMonto;
    private String tipo;
    private final Context context;

    public CARDS(Context context) {
        this.context = context;
    }

    public static CARDS getSingletonInstance(Context context) {
        if (tablaCards == null) {
            tablaCards = new CARDS(context);
        } else {
            Logger.debug("Card", "No se puede crear otro objeto, ya existe");
        }
        return tablaCards;
    }

    public static boolean inCardTable(String pan, Context context) {
        boolean ok = false;
        try {
            if (tablaCards.selectCardRow(pan, context)) {
                ok = true;
            }
        } catch (Exception e) {
            Logger.exception(TAG, e);
        }
        return ok;
    }

    public void setCard(String column, String value) {
        switch (column) {
            case NAME_PLANTILLA_ID:
                setPlantillaId(value);
                break;
            case NAME_RANGO_ID:
                setRangoId(value);
                break;
            case NAME_LIMITE_INFERIOR:
                setLimiteInferior(value);
                break;
            case NAME_LIMITE_SUPERIOR:
                setLimiteSuperior(value);
                break;
            case NAME_ESTADO:
                setEstado(value);
                break;
            case NAME_DESCRIPCION:
                setDescripcion(value);
                break;
            case NAME_REQUIERE_PIN:
                setRequierePin(value);
                break;
            case NAME_PIN_BYPASS:
                setPinBypass(value);
                break;
            case NAME_CASHBACK:
                setCashback(value);
                break;
            case NAME_CASHBACK_MONTO:
                setCashbackMonto(value);
                break;
            case NAME_TIPO:
                setTipo(value);
                break;
            default:
                break;
        }
    }

    public void clearCard() {
        for (String s : fields) {
            setCard(s, "");
        }
    }

    public boolean selectCardRow(String pan, Context context) {
        boolean ok = false;
        DbHelper databaseAccess = new DbHelper(context, NAME_DB, null, 1);
        databaseAccess.openDb(NAME_DB);

        String sql = consultaSQL(pan);

        Logger.info(TAG, "Consutla SQL -- " + sql);
        try {
            Cursor cursor = databaseAccess.rawQuery(sql);
            cursor.moveToFirst();
            int indexColumn;

            while (!cursor.isAfterLast()) {
                clearCard();
                indexColumn = 0;
                for (String s : CARDS.fields) {
                    setCard(s, cursor.getString(indexColumn++).trim());
                }
                ok = true;
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            Logger.exception(TAG, e);
            e.printStackTrace();
            Logger.error(TAG, e);
        }
        databaseAccess.closeDb();
        return ok;
    }

    private String consultaSQL(String pan) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        int counter = 1;
        for (String s : fields) {
            sql.append(s);
            if (counter++ < fields.length) {
                sql.append(",");
            }
        }

        sql.append(" from CARDS where ");
        sql.append("(cast(LIMITE_INFERIOR as integer) <= cast(substr('");
        sql.append(pan);
        sql.append("',0,LENGTH(TRIM(LIMITE_INFERIOR))+1) as integer) and cast(LIMITE_SUPERIOR as integer) >= cast(substr('");  // se valida solo el bin de la tarjeta MO
        sql.append(pan);
        sql.append("',0,LENGTH(TRIM(LIMITE_SUPERIOR))+1) as integer)) ");
        sql.append("order by cast(LIMITE_SUPERIOR as integer) - cast(LIMITE_INFERIOR as integer) asc limit 1; ");

        return sql.toString();
    }

    public String getPlantillaId() {
        return plantillaId;
    }

    public void setPlantillaId(String plantillaId) {
        this.plantillaId = plantillaId;
    }

    public String getRangoId() {
        return rangoId;
    }

    public void setRangoId(String rangoId) {
        this.rangoId = rangoId;
    }

    public String getLimiteInferior() {
        return limiteInferior;
    }

    public void setLimiteInferior(String limiteInferior) {
        this.limiteInferior = limiteInferior;
    }

    public String getLimiteSuperior() {
        return limiteSuperior;
    }

    public void setLimiteSuperior(String limiteSuperior) {
        this.limiteSuperior = limiteSuperior;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isEstado() {
        return estado;
    }


    /**
     * Si esta vacio no realizaria ningun proceso
     *
     * @param estado
     */
    public void setEstado(String estado) {
        if (!estado.isEmpty()) {
            if (estado.equals(TRUE) || estado.equals(FALSE)) {
                this.estado = Boolean.parseBoolean(estado);
            } else {
                showMensaje(" Error en la obtención de las Cards ");
                this.estado = false;
            }
        }

    }

    public boolean isRequierePin() {
        return requierePin;
    }

    public void setRequierePin(String requierePin) {
        if (!requierePin.isEmpty()) {
            if ((requierePin.equals(TRUE) || requierePin.equals(FALSE))) {
                this.requierePin = Boolean.parseBoolean(requierePin);
            } else {
                showMensaje(" Error en la obtención de las Cards (Requiere PIN)");
                this.requierePin = false;
            }
        }

    }

    public boolean isPinBypass() {
        return pinBypass;
    }

    public void setPinBypass(String pinBypass) {
        if (!pinBypass.isEmpty()) {
            if (pinBypass.equals(TRUE) || pinBypass.equals(FALSE)) {
                this.pinBypass = Boolean.parseBoolean(pinBypass);
            } else {
                showMensaje(" Error en la obtención de las Cards");
                this.pinBypass = false;
            }
        }


    }

    public boolean isCashback() {
        return cashback;
    }

    public void setCashback(String cashback) {
        if (!cashback.isEmpty()) {
            if (cashback.equals(TRUE) || cashback.equals(FALSE)) {
                this.cashback = Boolean.parseBoolean(cashback);
            } else {
                showMensaje(" Error en la obtención de las Cards");
                this.cashback = false;
            }
        }

    }

    public String getCashbackMonto() {
        return cashbackMonto;
    }

    public void setCashbackMonto(String cashbackMonto) {
        this.cashbackMonto = cashbackMonto;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }


    private void showMensaje(String mensaje) {
        if (context != null)
            UIUtils.toast((Activity) context, R.drawable.ic_redinfonet, mensaje, Toast.LENGTH_LONG);
        else
            Logger.debug("Info", "showMensaje: " + "context == null");
    }
}

