package com.flota.setting;

import android.content.Context;

import com.flota.adaptadores.ModeloMenusOpciones;
import com.flota.defines_bancard.DefinesBANCARD;
import com.wposs.flota.R;

import java.util.ArrayList;
import java.util.List;

public class ListTransacciones {

    static ArrayList<ModeloMenusOpciones> modeloMenusOpciones;

    private ListTransacciones() {
        //  this constructor is empty|
    }

    public static List<ModeloMenusOpciones> obtenerOpcionesMenu(Context context) {


        if (modeloMenusOpciones == null) {
            modeloMenusOpciones = new ArrayList<>();

            Object[][] objects = {
                    {DefinesBANCARD.POLARIS_NAME_TX_VTA_CUOTA, DefinesBANCARD.OPC_VENTA_CUOTA, R.drawable.ic_venta_cuota},
                    {DefinesBANCARD.POLARIS_NAME_TX_VTA_SALDO, DefinesBANCARD.OPC_VENTA_SALDO, R.drawable.ic_venta_saldo},
                    {DefinesBANCARD.POLARIS_NAME_TX_BILLETERAS, DefinesBANCARD.ITEM_BILLETERAS, R.drawable.logo_billeteras},
                    {DefinesBANCARD.POLARIS_NAME_TX_VTA_DEBITO_FORZADO, DefinesBANCARD.OPC_VENTA_DC, R.drawable.ic_debito_credito_extranjeros},
                    {DefinesBANCARD.POLARIS_NAME_TX_SIN_TARJETA, DefinesBANCARD.OPC_VENTA_ST, R.drawable.ic_operaciones_sin_tarjeta}
            };


            for (Object[] object : objects) {
                ModeloMenusOpciones options = ModeloMenusOpciones.getTransaccion(context, (String) object[0]);
                if (options.getID() != null && !options.getID().isEmpty() &&
                        options.getNOMBRE_TRANSACCION() != null && !options.getNOMBRE_TRANSACCION().isEmpty()) {
                    options.setInputContent((String) object[1]);
                    options.setIcono(context.getDrawable((int) object[2]));
                    modeloMenusOpciones.add(options);
                }
            }
        }

        return modeloMenusOpciones;
    }

    public static void setModeloMenusOpcionesNull() {
        ListTransacciones.modeloMenusOpciones = null;
    }
}
