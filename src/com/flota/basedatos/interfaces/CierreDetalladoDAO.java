package com.flota.basedatos.interfaces;

import android.app.Activity;

import com.flota.logscierres.LogsCierreDetalladoModelo;
import com.newpos.libpay.trans.translog.TransLogData;

import java.util.List;

public interface CierreDetalladoDAO extends CRUD<LogsCierreDetalladoModelo> {

    List<TransLogData> getLogsCierreDetallado(Activity context, String numLote);

}
