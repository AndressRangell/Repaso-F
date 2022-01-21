package com.flota.adaptadores;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.flota.inicializacion.configuracioncomercio.TRANSACCIONES;
import com.newpos.libpay.Logger;

import static com.flota.actividades.StartAppBANCARD.listadoTransacciones;

public class ModeloMenusOpciones {

    private String ID;
    private String NOMBRE_TRANSACCION;
    private boolean HABILITAR_TRANSACCION;
    private Drawable icono;
    private String inputContent;

    public ModeloMenusOpciones() {
    }

    public ModeloMenusOpciones(String ID, String NOMBRE_TRANSACCION, boolean HABILITAR_TRANSACCION) {
        this.ID = ID;
        this.NOMBRE_TRANSACCION = NOMBRE_TRANSACCION;
        this.HABILITAR_TRANSACCION = HABILITAR_TRANSACCION;
    }

    public ModeloMenusOpciones(String ID, Drawable icono) {
        this.ID = ID;
        this.icono = icono;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getNOMBRE_TRANSACCION() {
        return NOMBRE_TRANSACCION;
    }

    public void setNOMBRE_TRANSACCION(String NOMBRE_TRANSACCION) {
        this.NOMBRE_TRANSACCION = NOMBRE_TRANSACCION;
    }

    public boolean isHABILITAR_TRANSACCION() {
        return HABILITAR_TRANSACCION;
    }

    public void setHABILITAR_TRANSACCION(boolean HABILITAR_TRANSACCION) {
        this.HABILITAR_TRANSACCION = HABILITAR_TRANSACCION;
    }

    public Drawable getIcono() {
        return icono;
    }

    public void setIcono(Drawable icono) {
        this.icono = icono;
    }

    public static ModeloMenusOpciones getTransaccion(Context context, String nameTrans) {
        ModeloMenusOpciones list = new ModeloMenusOpciones();
        try {
            for (TRANSACCIONES transaccion : listadoTransacciones) {
                if ((transaccion.getNombre().equals(nameTrans)) && (transaccion.getHabilitar())) {
                    list.setID(transaccion.getTransaccionId());
                    list.setNOMBRE_TRANSACCION(transaccion.getNombre());
                    list.setHABILITAR_TRANSACCION(transaccion.getHabilitar());
                    break;
                }
            }
        } catch (Exception e) {
            Logger.exception("ModeloMenusOpciones.java", e);
            e.printStackTrace();
            Logger.error("ModeloMenusOpciones"+ "getTransaccion: " ,e);
        }

        return list;
    }

    public String getInputContent() {
        return inputContent;
    }

    public void setInputContent(String inputContent) {
        this.inputContent = inputContent;
    }
}
