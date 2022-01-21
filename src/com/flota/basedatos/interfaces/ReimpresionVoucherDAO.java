package com.flota.basedatos.interfaces;

import android.graphics.Bitmap;

import com.flota.basedatos.ModeloVoucherReimpresion;

public interface ReimpresionVoucherDAO extends CRUD<ModeloVoucherReimpresion> {

     Bitmap obtenerVoucher(String nroCargo);
}
