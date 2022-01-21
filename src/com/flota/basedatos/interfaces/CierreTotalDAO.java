package com.flota.basedatos.interfaces;

import android.content.Context;

import com.flota.logscierres.LogsCierresModelo;

import java.util.List;

public interface CierreTotalDAO extends CRUD<LogsCierresModelo> {

    List<LogsCierresModelo> getAllLogsCierre();

    void getEliminarBaseDatos(Context context);
}
