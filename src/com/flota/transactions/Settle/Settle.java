package com.flota.transactions.Settle;

import static android.content.Context.MODE_PRIVATE;
import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;
import static com.flota.actividades.StartAppBANCARD.readWriteFileMDM;
import static com.flota.menus.menus.idAcquirer;
import static com.flota.transactions.common.CommonFunctionalities.getUltimoComercio;
import static com.newpos.libpay.trans.Trans.Type.VENTA;
import static com.newpos.libpay.trans.Trans.Type.VENTAMANUAL;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;

import com.android.desert.keyboard.InputInfo;
import com.flota.adaptadores.ModeloCierreTrans;
import com.flota.basedatos.implementaciones.CierreTotalDAOImpl;
import com.flota.basedatos.interfaces.CierreTotalDAO;
import com.flota.inicializacion.configuracioncomercio.COMERCIOS;
import com.flota.logscierres.LogsCierresModelo;
import com.flota.menus.menus;
import com.flota.screen.inputs.ScreenSelectFromTwoOptions;
import com.flota.tools_bacth.ToolsBatch;
import com.flota.transactions.common.CommonFunctionalities;
import com.newpos.libpay.Logger;
import com.newpos.libpay.device.printer.PrintManager;
import com.newpos.libpay.global.TMConfig;
import com.newpos.libpay.helper.iso8583.ISO8583;
import com.newpos.libpay.presenter.TransPresenter;
import com.newpos.libpay.trans.Tcode;
import com.newpos.libpay.trans.TransInputPara;
import com.newpos.libpay.trans.finace.FinanceTrans;
import com.newpos.libpay.trans.translog.TransLog;
import com.newpos.libpay.trans.translog.TransLogData;
import com.newpos.libpay.utils.PAYUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.desert.newpos.payui.master.MasterControl;

public class Settle extends FinanceTrans implements TransPresenter {

    private static final String TAG = "Settle.java";
    private LogsCierresModelo cierresModelo;
    private String identificadorCierre;
    private boolean isRestart;

    public Settle(Context ctx, String transEn, TransInputPara p) {
        super(ctx, transEn);
        para = p;
        transUI = para.getTransUI();
        isReversal = false;
        isSaveLog = false;
        isDebit = false;
        isProcPreTrans = false;
    }

    @Override
    public ISO8583 getISO8583() {
        return iso8583;
    }

    @Override
    public void start() {

        if (!ToolsBatch.statusTrans(idAcquirer)) {
            transUI.showError(timeout, Tcode.T_err_no_trans, false);
            return;
        }

        try {
            realizacionCierre();
        } catch (Exception e) {
            transUI.toasTrans("Error realizacion  Cierre \n" + e.getMessage(), false, true);
            e.printStackTrace();
            Logger.exception(TAG, e);
        }
    }

    private void realizacionCierre() throws Exception {
        cierresModelo = new LogsCierresModelo();

        identificadorCierre = PAYUtils.getLocalDateTime();
        cierresModelo.setId(identificadorCierre);

        if (TransEName.equals(Type.AUTO_SETTLE)) {
            cierresModelo.setTipoCierre("AUTOMATICO");
        } else {
            cierresModelo.setTipoCierre("MANUAL");
        }
        cierresModelo.setNumLote(TMConfig.getInstance().getBatchNo());
        cierresModelo.setFechaUltimoCierre(getFechaUltimoCierre());
        cierresModelo.setFechaCierre(PAYUtils.getLocalDateFormat("dd/MM/yyyy HH:mm"));

        List<TransLogData> list = TransLog.getInstance(menus.idAcquirer).getData();
        long montoTotalTrans = 0;
        int contTrans = 0;
        int countSaleFlota = 0;
        long valueSaleFlota = 0;
        int countSaleManual = 0;
        long valueSaleManual = 0;


        ArrayList<String> codigosDeComercios = new ArrayList<>();
        ArrayList<String> tiposDeTarjetas = new ArrayList<>();
        for (TransLogData transLogData : list) {
            if (!transLogData.getIsVoided()) {
                if (transLogData.getAmount() != null) {
                    if (transLogData.getEName().equals(VENTA)) {
                        countSaleFlota++;
                        valueSaleFlota += transLogData.getAmount();
                    }
                    if (transLogData.getEName().equals(VENTAMANUAL)) {
                        countSaleManual++;
                        valueSaleManual += transLogData.getAmount();
                    }
                    montoTotalTrans += transLogData.getAmount();
                    contTrans++;
                }
            }
            if (!codigosDeComercios.contains(transLogData.getCodigoDelNegocio())) {
                codigosDeComercios.add(transLogData.getCodigoDelNegocio());
            }
            if (!tiposDeTarjetas.contains(transLogData.getTipoTarjeta())) {
                tiposDeTarjetas.add(transLogData.getTipoTarjeta());
            }
        }

        ArrayList<ModeloCierreTrans> log2CodigosNegocios = new ArrayList<>();
        StringBuilder discriminadoPorCodigos = new StringBuilder();

        for (String codComercio : codigosDeComercios) {
            int cont = 0;
            long montoSuma = 0;
            for (TransLogData transLogData : list) {
                if (codComercio.equals(transLogData.getCodigoDelNegocio())) {
                    if (transLogData.getAmount() != null) {
                        montoSuma += transLogData.getAmount();
                        cont++;
                    }
                }
            }

            String registro = cont + "@" + codComercio + "@" + montoSuma + "~";
            discriminadoPorCodigos.append(registro);

            ModeloCierreTrans transData = new ModeloCierreTrans();
            transData.setCodNegocio(codComercio);
            transData.setMontoTotal(String.valueOf(montoSuma));
            transData.setCantidadTrans(String.valueOf(cont));
            log2CodigosNegocios.add(transData);
        }

        cierresModelo.setDiscriminadoComercios(discriminadoPorCodigos.toString());

        ArrayList<ModeloCierreTrans> log2TipoTarjeta = new ArrayList<>();
        for (String tipoTarjeta : tiposDeTarjetas) {
            int cont = 0;
            long montoSuma = 0;
            for (TransLogData transLogData : list) {
                if (tipoTarjeta.equals(transLogData.getTipoTarjeta())) {
                    if (transLogData.getAmount() != null) {
                        montoSuma += transLogData.getAmount();
                        cont++;
                    }
                }
            }
            Logger.info("Monto trans " + montoSuma);
            Logger.info("Cantidad " + cont);
            ModeloCierreTrans transData = new ModeloCierreTrans();
            transData.setMontoTotal(String.valueOf(montoSuma));
            transData.setCantidadTrans(String.valueOf(cont));
            transData.setTipoDeTarjeta(tipoTarjeta);
            log2TipoTarjeta.add(transData);
        }

        totalesCredito(log2TipoTarjeta);
        totalesDebito(log2TipoTarjeta);
        totalesMovil(log2TipoTarjeta);
        totalesVuelto(list);
        totalesSaldo(log2TipoTarjeta);

        cierresModelo.setCantGeneral(String.valueOf(contTrans));
        cierresModelo.setTotalGeneral(String.valueOf(montoTotalTrans));
        cierresModelo.setCantAnular("0");
        cierresModelo.setTotalAnular("0");

        if (TransEName.equals(Type.AUTO_SETTLE)) {
            realizarCierre();
        } else {
            String line1 = "Venta Flota: " + countSaleFlota;
            String line2 = "\nGs: " + PAYUtils.FormatPyg(String.valueOf(valueSaleFlota));
            String line3 = "\n\nVenta Manual: " + countSaleManual;
            String line4 = "\nGs: " + PAYUtils.FormatPyg(String.valueOf(valueSaleManual));
            String line5 = "\n\nTotales: " + cierresModelo.getCantGeneral();
            String line6 = "\nGs: " + PAYUtils.FormatPyg(cierresModelo.getTotalGeneral());

            SpannableString span1 = new SpannableString(line1);
            span1.setSpan(new RelativeSizeSpan(0.75f), 0, line1.length(), SPAN_INCLUSIVE_INCLUSIVE);
            SpannableString span3 = new SpannableString(line3);
            span3.setSpan(new RelativeSizeSpan(0.75f), 0, line3.length(), SPAN_INCLUSIVE_INCLUSIVE);
            SpannableString span5 = new SpannableString(line5);
            span5.setSpan(new RelativeSizeSpan(0.75f), 0, line5.length(), SPAN_INCLUSIVE_INCLUSIVE);

            InputInfo info = transUI.showScreenInput(new ScreenSelectFromTwoOptions(timeout)
                    .setTitle("Cierre")
                    .setData(TextUtils.concat(span1, line2, span3, line4, span5, line6))
                    .setQuestion("¿Realizar cierre?")
                    .setTextOption1("Confirmar")
                    .setTextOption2("Cancelar")
            );
            if (info.isResultFlag()) {
                final String optionSelect = info.getResult();
                Logger.debug(TAG, "start: realizacionCierre: optionSelect=" + optionSelect);
                if (optionSelect.equals(MasterControl.SELECT_OPTION_1)) {
                    realizarCierre();
                } else {
                    transUI.showFinish();
                }
            } else {
                transUI.showFinish();
            }
        }
    }

    private void realizarCierre() {
        CierreTotalDAO cierresModeloCRUD = new CierreTotalDAOImpl(context);

        if (cierresModeloCRUD.ingresarRegistro(cierresModelo)) {
            verificacionComercio(cierresModeloCRUD);
            Logger.error("SQL", "Log del cierre guardado correctamente");
        } else {
            Logger.error("SQL", "Log del cierre no se guardo correctamente");
        }

        int val = Integer.parseInt(TMConfig.getInstance().getBatchNo());
        TMConfig.getInstance().incBatcheNo();
        TransLog.getInstance(idAcquirer).clearAll(idAcquirer);
        CommonFunctionalities.saveSettle(context);
        guardarFechaDeUltimoCierre();
        try {
            readWriteFileMDM.writeFileMDM(readWriteFileMDM.getReverse(), readWriteFileMDM.getSettle());
        } catch (Exception e) {
            e.printStackTrace();
            Logger.exception(TAG, e);
        }

        InputInfo inputInfo = transUI.showResultCierre(timeout, cierresModelo);
        if (inputInfo.isResultFlag()) {
            imprimirCierre();
        } else {
            try {
                readWriteFileMDM.writeFileMDM(readWriteFileMDM.getReverse(), readWriteFileMDM.getSettle());
            } catch (Exception e) {
                e.printStackTrace();
                Logger.exception(TAG, e);
            }
            String msg;
            if (isRestart) {
                cierreAgente();
                msg = "Se reinicia el POS";
            } else {
                msg = "No se reinicia...";
            }
            Logger.info("Mensaje: " + msg);
            transUI.showFinish();
        }
        savePreferences();
    }

    private void verificacionComercio(CierreTotalDAO cierresModeloCRUD) {
        String comercioPolaris = COMERCIOS.getSingletonInstance(context).getMerchantDescription();
        String UltimoComercio = getUltimoComercio(context);

        if (!comercioPolaris.isEmpty() && !comercioPolaris.equals(UltimoComercio)) {
            guardarComercioCierre(context, comercioPolaris);
            cierresModeloCRUD.getEliminarBaseDatos(context);
        }
    }

    private void imprimirCierre() {
        transUI.showImprimiendo(timeout);
        PrintManager printManager = PrintManager.getmInstance(context, transUI);
        printManager.setHost_id(host_id);
        retVal = printManager.imprimirCierre(cierresModelo, true);
        if (retVal == 0) {
            String msg;
            if (isRestart) {
                cierreAgente();
                msg = "Se reinicia el POS";
            } else {
                msg = "No se reinicia...";
            }
            Logger.info("Mensaje: " + msg);
            transUI.showFinish();
        }
    }

    private void cierreAgente() {
        Logger.flujo(TAG, "Entrada a cierreAgente...");
        String nombrePackage = "com.downloadmanager";
        if (nombrePackage != null && isAppInstalada(nombrePackage)) {
            Intent intentapk = context.getApplicationContext().getPackageManager().getLaunchIntentForPackage(nombrePackage);
            intentapk.putExtra("APLICACION", "BANCARD");
            context.startActivity(intentapk);
        } else {
            transUI.toasTrans("Aplicación no instalada", true, true);
            transUI.showFinish();
        }
    }

    public boolean isAppInstalada(String packageFaceId) {
        boolean ret = false;
        List<PackageInfo> packageList = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packageList.size(); i++) {
            PackageInfo packageInfo = packageList.get(i);
            Logger.info("Name: " + packageInfo.packageName);
            if (packageInfo.packageName.equals(packageFaceId)) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    private void guardarFechaDeUltimoCierre() {
        DateFormat hourdateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date fechaActual = new Date();
        SharedPreferences.Editor editor = context.getSharedPreferences("fecha-cierre", MODE_PRIVATE).edit();
        editor.putString("fechaUltimoCierre", hourdateFormat.format(fechaActual));
        editor.putString("identificadorCierre", identificadorCierre);
        editor.apply();
    }

    private String getFechaUltimoCierre() {
        SharedPreferences prefs = context.getSharedPreferences("fecha-cierre", MODE_PRIVATE);
        return prefs.getString("fechaUltimoCierre", null);
    }

    private void totalesCredito(ArrayList<ModeloCierreTrans> log2TipoTarjeta) {
        for (ModeloCierreTrans mensajeModelos : log2TipoTarjeta) {
            //TOTAL Debito
            if (mensajeModelos.getTipoDeTarjeta().equals("D")) {
                cierresModelo.setCantDebito(mensajeModelos.getCantidadTrans());
                cierresModelo.setTotalDebito(mensajeModelos.getMontoTotal());
            }
        }
    }

    private void totalesDebito(ArrayList<ModeloCierreTrans> log2TipoTarjeta) {
        for (ModeloCierreTrans mensajeModelos : log2TipoTarjeta) {
            //TOTAL CREDITO
            if (mensajeModelos.getTipoDeTarjeta().equals("C")) {
                cierresModelo.setCantCredito(mensajeModelos.getCantidadTrans());
                cierresModelo.setTotalCredito(mensajeModelos.getMontoTotal());
            }
        }
    }

    private void totalesVuelto(List<TransLogData> list) {
        int contVueltos = 0;
        long montoVueltos = 0;
        for (TransLogData transLogData : list) {
            if (transLogData.getOtherAmount() != null) {
                if (transLogData.getOtherAmount() > 0) {
                    contVueltos++;
                    montoVueltos += transLogData.getOtherAmount();
                }
            }
        }

        cierresModelo.setCantVuelto(String.valueOf(contVueltos));
        cierresModelo.setTotalVuelto(String.valueOf(montoVueltos));
    }

    private void totalesMovil(ArrayList<ModeloCierreTrans> log2TipoTrans) {
        int cantidad = 0;
        long montoTotal = 0;
        for (ModeloCierreTrans cierreTransa : log2TipoTrans) {
            if (cierreTransa.getTipoDeTarjeta().equals("M")) {
                cantidad += Integer.parseInt(cierreTransa.getCantidadTrans());
                montoTotal += Long.parseLong(cierreTransa.getMontoTotal());
                continue;
            }
            if (cierreTransa.getTipoDeTarjeta().equals("Z")) {
                cantidad += Integer.parseInt(cierreTransa.getCantidadTrans());
                montoTotal += Long.parseLong(cierreTransa.getMontoTotal());
                continue;
            }
            if (cierreTransa.getTipoDeTarjeta().equals("T")) {
                cantidad += Integer.parseInt(cierreTransa.getCantidadTrans());
                montoTotal += Long.parseLong(cierreTransa.getMontoTotal());
                continue;
            }
            if (cierreTransa.getTipoDeTarjeta().equals("2")) {
                cantidad += Integer.parseInt(cierreTransa.getCantidadTrans());
                montoTotal += Long.parseLong(cierreTransa.getMontoTotal());
            }
        }
        if (cantidad > 0) {
            cierresModelo.setCantMovil(String.valueOf(cantidad));
            cierresModelo.setTotalMovil(String.valueOf(montoTotal));
        }
    }

    private void totalesSaldo(ArrayList<ModeloCierreTrans> log2TipoTarjeta) {
        for (ModeloCierreTrans mensajeModelos : log2TipoTarjeta) {
            //TOTAL CREDITO
            if (mensajeModelos.getTipoDeTarjeta().equals("S")) {
                cierresModelo.setCantSaldo(mensajeModelos.getCantidadTrans());
                cierresModelo.setTotalSaldo(mensajeModelos.getMontoTotal());
            }
        }
    }

    private void guardarComercioCierre(Context context, String comercio) {
        SharedPreferences.Editor editor = context.getSharedPreferences("fecha-cierre", MODE_PRIVATE).edit();
        editor.putString("Comercio", comercio);
        editor.apply();
    }
}
