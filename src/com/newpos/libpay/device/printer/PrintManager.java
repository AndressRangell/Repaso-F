package com.newpos.libpay.device.printer;

import static android.content.Context.MODE_PRIVATE;
import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static com.flota.actividades.StartAppBANCARD.CERT;
import static com.flota.actividades.StartAppBANCARD.VERSION;
import static com.flota.actividades.StartAppBANCARD.tablaComercios;
import static com.flota.actividades.StartAppBANCARD.tablaDevice;
import static com.flota.actividades.StartAppBANCARD.tablaHost;
import static com.flota.defines_bancard.DefinesBANCARD.PREF_AMOUNT;
import static com.flota.defines_bancard.DefinesBANCARD.PREF_CARGO;
import static com.flota.defines_bancard.DefinesBANCARD.PREF_DATE;
import static com.flota.defines_bancard.DefinesBANCARD.PREF_METODO;
import static com.flota.defines_bancard.DefinesBANCARD.PREF_TIME;
import static com.flota.defines_bancard.DefinesBANCARD.PREF_TRACE;
import static com.flota.inicializacion.trans_init.Init.NAME_DB;
import static com.flota.menus.MenuAction.callBackSeatle;
import static com.flota.menus.MenuAction.callbackPrint;
import static com.flota.menus.menus.idAcquirer;
import static com.flota.tools.UtilNetwork.getMask;
import static com.newpos.libpay.presenter.TransUIImpl.getErrInfo;
import static com.newpos.libpay.trans.Trans.IVAAMOUNT;
import static com.newpos.libpay.trans.Trans.MODE_CTL;
import static com.newpos.libpay.trans.Trans.MODE_HANDLE;
import static com.newpos.libpay.trans.Trans.MODE_ICC;
import static com.newpos.libpay.trans.Trans.MODE_MAG;
import static com.newpos.libpay.trans.Trans.SERVICEAMOUNT;
import static com.newpos.libpay.trans.Trans.TIPAMOUNT;
import static com.newpos.libpay.trans.finace.FinanceTrans.LOCAL;
import static com.newpos.libpay.utils.PAYUtils.getAPN;
import static com.newpos.libpay.utils.PAYUtils.getNetype;
import static org.jpos.stis.Util.hex2byte;
import static cn.desert.newpos.payui.master.MasterControl.KEY_DESCRIPTION_PRODUCT;
import static cn.desert.newpos.payui.master.MasterControl.mcontext;
import static cn.desert.newpos.payui.transrecord.HistoryTrans.ALL_F_REDEN;
import static cn.desert.newpos.payui.transrecord.HistoryTrans.REPORTE_TOTAL;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;

import com.flota.adaptadores.ModeloCierreTrans;
import com.flota.basedatos.ModeloVoucherReimpresion;
import com.flota.basedatos.implementaciones.ReimpresionVoucherDAOImpl;
import com.flota.basedatos.interfaces.ReimpresionVoucherDAO;
import com.flota.defines_bancard.DefinesBANCARD;
import com.flota.inicializacion.configuracioncomercio.ChequeoIPs;
import com.flota.inicializacion.configuracioncomercio.Device;
import com.flota.inicializacion.init_emv.CapkRow;
import com.flota.inicializacion.init_emv.EmvAppRow;
import com.flota.inicializacion.trans_init.trans.DbHelper;
import com.flota.inicializacion.trans_init.trans.Tools;
import com.flota.logscierres.LogsCierresModelo;
import com.flota.printer.PrintParameter;
import com.flota.screen.inputs.methods.FormatInput;
import com.flota.tools.UtilNetwork;
import com.flota.transactions.DataAdicional.DAUtil;
import com.flota.transactions.DataAdicional.DataAdicional;
import com.newpos.libpay.Logger;
import com.newpos.libpay.global.TMConfig;
import com.newpos.libpay.presenter.TransUI;
import com.newpos.libpay.trans.Tcode;
import com.newpos.libpay.trans.Trans;
import com.newpos.libpay.trans.translog.TransLog;
import com.newpos.libpay.trans.translog.TransLogData;
import com.newpos.libpay.trans.translog.TransLogLastSettle;
import com.newpos.libpay.utils.ISOUtil;
import com.newpos.libpay.utils.PAYUtils;
import com.pos.device.printer.PrintCanvas;
import com.pos.device.printer.PrintTask;
import com.pos.device.printer.Printer;
import com.pos.device.printer.PrinterCallback;
import com.wposs.flota.BuildConfig;
import com.wposs.flota.R;

import org.jpos.stis.TLV_parsing;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import cn.desert.newpos.payui.master.MasterControl;
import wposs.printer.Voucher;
import wposs.printer.data.format.TextFormat;
import wposs.printer.data.format.type.Align;
import wposs.printer.data.format.type.Bold;
import wposs.printer.data.format.type.Size;
import wposs.printer.data.format.type.TypeText;
import wposs.printer.factory.TextConvert;


/**
 * Created by zhouqiang on 2017/3/14.
 *
 * @author zhouqiang
 * 打印管理类
 */
public class PrintManager {

    private static final String TAG = "PrintManager";

    /**
     * extras de impresión
     */
    public static Map<String, String> printExtras = new HashMap<>();
    private static PrintManager mInstance;
    private static TMConfig cfg;
    private static Context mContext;
    private static TransUI transUI;

    private final int S_SMALL = 15;
    private final int S_MEDIUM = 23;
    private final int S_BIG = 29;
    private final int MAX_CHAR_SMALL = 42;
    private final int MAX_CHAR_MEDIUM = 28;
    private final int MAX_CHAR_BIG = 22;

    private final Voucher voucher = new Voucher();
    private final TextFormat fSmallCenter = new TextFormat().setAlign(Align.CENTER).setBold(Bold.ON).setSize(Size.SMALL).setTypeText(TypeText.IN_A_LINE);
    private final TextFormat fMediumLeft = new TextFormat().setAlign(Align.LEFT).setBold(Bold.ON).setSize(Size.MEDIUM).setTypeText(TypeText.IN_A_LINE);
    private final TextFormat fSmallLeft = new TextFormat().setAlign(Align.LEFT).setBold(Bold.ON).setSize(Size.SMALL).setTypeText(TypeText.IN_A_LINE);

    int num = 0;
    boolean isPrinting = false;
    boolean isICC;
    boolean isNFC;
    boolean isFallback;
    private TransLogData dataTrans;
    private boolean BOLD_ON = true;
    private boolean BOLD_OFF = false;
    private Printer printer = null;
    private PrintTask printTask = null;
    private PackageInfo packageInfo;
    private String host_id;
    private String[] rspField57 = new String[17];
    private String[] identificadoresActivos = new String[25];
    private String TraceNo;
    //Reportes
    private long subTotalSubTotal = 0;
    private long ivaAmountSubTotal = 0;
    private long serviceAmountSubTotal = 0;
    private long tipAmountSubTotal = 0;
    private long montoFijoSubTotal = 0;
    private long totalTempAmount = 0;
    private long totalTempIva = 0;
    private long totalTempServiceAmount = 0;
    private long totalTempTipAmount = 0;
    private long totalTempMontoFijo = 0;
    private long granTotal = 0;
    private long granTotalIva = 0;
    private long granTotalService = 0;
    private long granTotalTip = 0;
    private long granTotalMontoFijo = 0;
    private long amount;
    private long subTotal;
    private long ivaAmount;
    private long serviceAmount;
    private long tipAmount;
    private long montoFijo = 0;
    private int contTransAcq = 0;
    private int contTotalTransAcq = 0;
    private int contTransEmisor = 0;
    private String nombreActualEmisor = "";
    private String fechaTransActual = "";
    private String nombreAdquirenteActual = "";
    private String MID_InterOper = "";
    private boolean soloUnCiclo = false;
    private String[] comercioImpreso;
    private int idxImpresionComercio = 0;
    private boolean omitir = false;
    private boolean printNameIssuer = false;
    private boolean printDateTransxIssuer = false;
    private boolean isCajas;
    private String pinOffline;

    private PrintManager() {
    }

    public static PrintManager getmInstance(Context c, TransUI tui) {
        mContext = c;
        transUI = tui;
        if (null == mInstance) {
            mInstance = new PrintManager();
        }
        cfg = TMConfig.getInstance();
        return mInstance;
    }

    public void setPinOffline(String pinOffline) {
        this.pinOffline = pinOffline;
    }

    public void setHost_id(String host_id) {
        this.host_id = host_id;
    }

    public int printCierre(ArrayList<ModeloCierreTrans> cierreTrans, List<TransLogData> list, boolean isCierre) {
        int ret = -1;
        this.printTask = new PrintTask();
        this.printTask.setGray(150);

        PrintCanvas canvas = new PrintCanvas();
        Paint paint = new Paint();

        printLogoRedInfonet(paint, canvas);

        String nombreComercio = checkNull(tablaComercios.sucursal.getDescripcion());
        String ciudadComerio = checkNull(tablaComercios.sucursal.getDireccionPrincipal());
        setTextPrint(setCenterText(nombreComercio.trim(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(setCenterText(ciudadComerio.trim(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

        if (isCierre) {
            setTextPrint(setCenterText("CIERRE DE LOTE - LOTE: " + TMConfig.getInstance().getBatchNo(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        } else {
            setTextPrint(setCenterText("REPORTE DE TOTAL - LOTE: " + TMConfig.getInstance().getBatchNo(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        }

        String fechaUltimoCierre = getFechaUltimoCierre();
        String fechaActual = PAYUtils.getLocalDateFormat("dd/MM/yyyy HH:mm");

        setTextPrint(setCenterText("Entre: " + fechaUltimoCierre, S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(setCenterText("Hasta: " + fechaActual, S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

        println(paint, canvas);
        setTextPrint("CANTIDAD       COD. COMERCIO         MONTO", paint, BOLD_ON, canvas, S_SMALL);

        int cantTotal = 0;
        int montoTotal = 0;

        for (ModeloCierreTrans trans : cierreTrans) {
            String cant = trans.getCantidadTrans();
            cantTotal += Integer.parseInt(cant);
            if (cant.length() > 6) {
                cant = cant.substring(0, 6);
            } else {
                cant = ISOUtil.padright(cant, 6, ' ');
            }
            String cod = trans.getCodNegocio().trim();
            if (cod.length() > 9) {
                cod = cod.substring(0, 9);
            } else {
                cod = ISOUtil.padleft(cod, 9, ' ');
            }

            //
            String mon = trans.getMontoTotal();
            if (mon.length() > 2) {
                mon = mon.substring(0, mon.length() - 2);
            }
            montoTotal += Integer.parseInt(mon);
            if (mon.length() > 10) {
                mon = mon.substring(0, 10);
                mon = PAYUtils.formatMontoGs(mon);
            } else {
                mon = PAYUtils.formatMontoGs(mon);
                mon = ISOUtil.padleft(mon, 16, ' ');
            }

            setTextPrint(setCenterText(cod, S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
            setTextPrint(setTextColumn(cant, mon, S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
        }

        setTextPrint("------------------------------------------", paint, BOLD_ON, canvas, S_SMALL);
        ArrayList<String> tiposDeTarjetas = new ArrayList<>();
        for (TransLogData transLogData : list) {
            if (!tiposDeTarjetas.contains(transLogData.getTipoTarjeta())) {
                tiposDeTarjetas.add(transLogData.getTipoTarjeta());
            }
        }

        ArrayList<ModeloCierreTrans> log2TipoTarjeta = new ArrayList<>();
        for (String tipoTarjeta : tiposDeTarjetas) {
            int cont = 0;
            long montoSuma = 0;
            for (TransLogData transLogData : list) {
                if (tipoTarjeta.equals(transLogData.getTipoTarjeta())) {
                    if (transLogData.getOtherAmount() != null) {
                        if (transLogData.getOtherAmount() > 0) {
                            if (transLogData.getAmount() != null) {
                                montoSuma += (transLogData.getAmount() - transLogData.getOtherAmount());
                                cont++;
                            }
                        } else {
                            if (transLogData.getAmount() != null) {
                                montoSuma += transLogData.getAmount();
                                cont++;
                            }
                        }
                    } else {
                        if (transLogData.getAmount() != null) {
                            montoSuma += transLogData.getAmount();
                            cont++;
                        }
                    }
                }
            }
            Logger.info("Monto trans " + montoSuma);
            Logger.info("Cantidad " + cont);
            ModeloCierreTrans transData = new ModeloCierreTrans();
            transData.setTipoDeTarjeta(tipoTarjeta);
            transData.setMontoTotal(String.valueOf(montoSuma));
            transData.setCantidadTrans(String.valueOf(cont));
            log2TipoTarjeta.add(transData);
        }

        totalCredito(log2TipoTarjeta, paint, canvas);
        totalDebito(log2TipoTarjeta, paint, canvas);
        totalZimple(log2TipoTarjeta, paint, canvas);
        totalAnular(log2TipoTarjeta, paint, canvas);

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

        totalVueltos(contVueltos, montoVueltos, paint, canvas);
        setTextPrint(setCenterText("TOTAL GENERAL", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
        setTextPrint(setTextColumn(ISOUtil.padleft(String.valueOf(cantTotal), 4, '0'), PAYUtils.formatMontoGs(String.valueOf(montoTotal)), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);

        totalVentaSaldo(log2TipoTarjeta, paint, canvas);

        if (isCierre) {
            println(paint, canvas);
            setTextPrint(setCenterText("Cierre completo", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        }

        println(paint, canvas);
        println(paint, canvas);

        ret = printData(canvas, "");

        if (printer != null) {
            printer = null;
        }

        return ret;
    }

    private void totalVueltos(int contVueltos, long montoVueltos, Paint paint, PrintCanvas canvas) {
        if (contVueltos > 0) {
            setTextPrint(setTextColumn("TOTAL VUELTO        " + ISOUtil.padleft(String.valueOf(contVueltos), 4, '0') + " Gs.", PAYUtils.FormatPyg(String.valueOf(montoVueltos)), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
            return;
        }
        setTextPrint(setTextColumn("TOTAL VUELTO        0000 Gs.", "0", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
    }

    private void totalAnular(ArrayList<ModeloCierreTrans> log2TipoTarjeta, Paint paint, PrintCanvas canvas) {
        setTextPrint(setTextColumn("TOTAL ANULAR        0000 Gs.", "0", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
    }

    private void totalCredito(ArrayList<ModeloCierreTrans> log2TipoTarjeta, Paint paint, PrintCanvas canvas) {
        for (ModeloCierreTrans modelo : log2TipoTarjeta) {
            if (modelo.getTipoDeTarjeta().equals("C")) {
                setTextPrint(setTextColumn("TOTAL CREDITO       " + ISOUtil.padleft(modelo.getCantidadTrans(), 4, '0') + " Gs.", PAYUtils.FormatPyg(String.valueOf(modelo.getMontoTotal())), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
                return;
            }
        }
        setTextPrint(setTextColumn("TOTAL CREDITO       0000 Gs.", "0", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
    }

    private void totalDebito(ArrayList<ModeloCierreTrans> log2TipoTarjeta, Paint paint, PrintCanvas canvas) {
        for (ModeloCierreTrans modelo : log2TipoTarjeta) {
            if (modelo.getTipoDeTarjeta().equals("D")) {
                setTextPrint(setTextColumn("TOTAL DEBITO        " + ISOUtil.padleft(modelo.getCantidadTrans(), 4, '0') + " Gs.", PAYUtils.FormatPyg(String.valueOf(modelo.getMontoTotal())), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
                return;
            }
        }
        setTextPrint(setTextColumn("TOTAL DEBITO        0000 Gs.", "0", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
    }

    private void totalVentaSaldo(ArrayList<ModeloCierreTrans> log2TipoTarjeta, Paint paint, PrintCanvas canvas) {
        for (ModeloCierreTrans modelo : log2TipoTarjeta) {
            if (modelo.getTipoDeTarjeta().equals("S")) {
                setTextPrint(setTextColumn("TOTAL VENTA SALDO   " + ISOUtil.padleft(modelo.getCantidadTrans(), 4, '0') + " Gs.", PAYUtils.FormatPyg(String.valueOf(modelo.getMontoTotal())), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
                return;
            }
        }
        setTextPrint(setTextColumn("TOTAL VENTA SALDO   0000 Gs.", "0", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
    }

    private void totalZimple(ArrayList<ModeloCierreTrans> log2TipoTarjeta, Paint paint, PrintCanvas canvas) {
        int cantidad = 0;
        long montoTotal = 0;
        for (ModeloCierreTrans modelo : log2TipoTarjeta) {
            if (modelo.getTipoDeTarjeta().equals("M")) {
                cantidad += Integer.parseInt(modelo.getCantidadTrans());
                montoTotal += Long.parseLong(modelo.getMontoTotal());
                continue;
            }
            if (modelo.getTipoDeTarjeta().equals("Z")) {
                cantidad += Integer.parseInt(modelo.getCantidadTrans());
                montoTotal += Long.parseLong(modelo.getMontoTotal());
                continue;
            }
            if (modelo.getTipoDeTarjeta().equals("T")) {
                cantidad += Integer.parseInt(modelo.getCantidadTrans());
                montoTotal += Long.parseLong(modelo.getMontoTotal());
                continue;
            }
            if (modelo.getTipoDeTarjeta().equals("2")) {
                cantidad += Integer.parseInt(modelo.getCantidadTrans());
                montoTotal += Long.parseLong(modelo.getMontoTotal());
            }
        }
        if (cantidad > 0) {
            setTextPrint(setTextColumn("TOTAL MOVIL         " + ISOUtil.padleft(String.valueOf(cantidad), 4, '0') + " Gs.", PAYUtils.FormatPyg(String.valueOf(montoTotal)), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        } else {
            setTextPrint(setTextColumn("TOTAL MOVIL         0000 Gs.", "0", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        }


    }

    private String getFechaUltimoCierre() {
        SharedPreferences prefs = mContext.getSharedPreferences("fecha-cierre", MODE_PRIVATE);
        return prefs.getString("fechaUltimoCierre", null);
    }

    /**
     * print
     *
     * @param data   dataTrans
     * @param isCopy isCopy
     * @return return
     */
    public int print(final TransLogData data, boolean isCopy, boolean duplicate) {
        int ret = -1;
        String typeTransVoid = null;
        this.printTask = new PrintTask();
        this.printTask.setGray(150);
        dataTrans = data;
        isICC = data.isICC();
        isNFC = data.isNFC();
        isFallback = data.isFallback();
        int sizeTransLog = -1;

        if (dataTrans.getTypeTransVoid() != null)
            typeTransVoid = dataTrans.getTypeTransVoid();

        try {
            packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Logger.error("Exception", e.toString());
            Logger.exception(TAG, e);
        }

        sizeTransLog = TransLog.getInstance(idAcquirer).getSize();

        if (sizeTransLog == 0) {
            ret = Tcode.T_print_no_log_err;
            Logger.print(TAG, "ERROR - sizeTransLog == 0");
        } else {
            printer = Printer.getInstance();
            if (printer == null) {
                ret = Tcode.T_sdk_err;
                Logger.print(TAG, "ERROR - printer == null");
            } else {
                if (dataTrans.isVoided()) {
                    ret = printVoidMedianet(isCopy, duplicate);
                } else if (dataTrans.getEName().equals(Trans.Type.VENTA)) {
                    try {
                        ret = printSale(isCopy, duplicate);
                    } catch (Exception e) {
                        if (transUI != null)
                            transUI.toasTrans("Error print : " + e.getMessage(), false, true);
                        else Logger.error(TAG, "print: transUI=null");
                        e.printStackTrace();
                        Logger.exception(TAG, e);
                    }
                } else if (dataTrans.getEName().equals(Trans.Type.ANULACION)) {
                    try {
                        ret = printAnnulment(isCopy, duplicate);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (dataTrans.getEName().equals(Trans.Type.SETTLE) || dataTrans.getEName().equals(Trans.Type.AUTO_SETTLE)) {
                    ret = printReportMedianet(true, false, PrintParameter.isPrintTotals(), false);
                }
            }
        }
        return ret;
    }

    public void rePrint(String nroCargo) {
        ReimpresionVoucherDAO reimpresionVoucherDAO = new ReimpresionVoucherDAOImpl(mcontext);
        final Bitmap bitmap = reimpresionVoucherDAO.obtenerVoucher(nroCargo);
        if (bitmap != null) {
            final PrintManager printManager = PrintManager.getmInstance(mContext, transUI);
            new Thread() {
                @Override
                public void run() {
                    printManager.printVoucher(bitmap);
                }
            }.start();
        } else {
            Logger.info(TAG, "Bitmap null");
        }
    }

    public void printVoucher(Bitmap bitmap) {
        final CountDownLatch latch = new CountDownLatch(1);
        if (bitmap != null) {
            Logger.info("El bitmap grafi es :" + bitmap);
            printTask = new PrintTask();
            printTask.setPrintBitmap(bitmap);
            printer = Printer.getInstance();
            printer.startPrint(printTask, new PrinterCallback() {
                @Override
                public void onResult(int i, PrintTask printTask) {
                    new CountDownTimer(150, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            latch.countDown();
                        }
                    }
                            .start();

                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            Logger.error("Exception", e.toString());
            Thread.currentThread().interrupt();
            Logger.exception(TAG, e);
        }
    }

    private void printHeaderVenta(PrintCanvas canvas, Paint paint) {
        String nombreComercio = checkNull(tablaComercios.sucursal.getDescripcion());
        String ciudadComerio = checkNull(tablaComercios.sucursal.getDireccionPrincipal());
        String codigoNegocio = checkNull(dataTrans.getCodigoDelNegocio());

        String date = checkNull(dataTrans.getLocalDate());
        String hora = checkNull(dataTrans.getLocalTime());

        String formatDate = PAYUtils.StrToDate(date, "yyyyMMdd", "dd/MM/yyyy");
        String formatHour = PAYUtils.StrToDate(hora, "HHmmss", "HH:mm:ss");

        String fecha;
        fecha = "F:" + formatDate + " H:" + formatHour;
        String aux = "C.N.: " + codigoNegocio;

        printLogoRedInfonet(paint, canvas);
        if (dataTrans.getTipoVenta() != null) {
            if (!dataTrans.getTipoVenta().equals(DefinesBANCARD.VENTA_SALDO)) {
                setTextPrint(setCenterText(nombreComercio.trim(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
                setTextPrint(setCenterText(ciudadComerio.trim(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
            }
        }
        /*setTextPrint(setCenterText(aux.trim(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);*/
        setTextPrint(setTextColumn(aux.trim(), fecha, S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
    }

    private void printHeaderVenta2(PrintCanvas canvas, Paint paint) {
        // Commerce
        String name = checkNull(tablaComercios.sucursal.getDescripcion());
        String direction = checkNull(tablaComercios.sucursal.getDireccionPrincipal());
        String phone = checkNull(tablaComercios.sucursal.getTelefono());
        String ruc = checkNull(tablaComercios.sucursal.getRuc());

        printLogoRedInfonet(paint, canvas);
        setTextPrint(setCenterText(name.trim(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(setCenterText(direction.trim(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(setCenterText(phone.trim(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        if (!ruc.trim().equals(""))
            setTextPrint(setCenterText("RUC: " + ruc.trim(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
    }

    /**
     * Obtener el código de negocio por tipo de transacción
     *
     * @return Si es QR se valida y se establece el valor de “merchant_code” proveniente de extras,
     * si no, se establece la variable codigoNegocio.
     */

    private void printSubHeader(PrintCanvas canvas, Paint paint, boolean duplicate) {
        String cardMode;
        String lecturaTarjeta = formatDetailsType(dataTrans);
        if (dataTrans.getRRN() != null) {
            setTextPrint(setCenterText("BOLETA: " + dataTrans.getRRN(), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
        }
        if (duplicate) {
            setTextPrint(setCenterText("***** DUPLICADO *****", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
        }
        setTextPrint(setTextColumn(dataTrans.getTipoVenta() + " " + lecturaTarjeta, formatSerial(Tools.getSerial()) + " " + Tools.getVersion(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        printDataCARDCHIP(paint, canvas);

        String aux1 = "";
        String aux2 = "";
        String auxPan = dataTrans.getPan();
        final String field02 = dataTrans.getField02();
        if (auxPan != null) {
            if (auxPan.length() >= 4) {
                aux1 = "T : *" + auxPan.substring((dataTrans.getPan().length() - 4));
            }
        } else if (field02 != null) {
            aux1 = "T : *" + (field02.length() > 4 ? field02.substring(field02.length() - 4) : field02);
        }

        if (dataTrans.getAuthCode() != null) {
            aux2 += "C.AUT: " + dataTrans.getAuthCode();
            setTextPrint(setTextColumn(aux1, aux2, S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        }
    }

    private void printSubHeader2(PrintCanvas canvas, Paint paint, boolean duplicate) {
        // Data Trans
        String commerceCode = checkNull(dataTrans.getCodigoDelNegocio()); // Codigo de Negocio (C.N.)
        final String numberTicket = dataTrans.getRRN(); // Numero de boleta
        final String issuerName = dataTrans.getField61(); // Nombre del emisor
        final String pan = dataTrans.getPan(); // Pan
        final String voucherNumber = checkNull(DataAdicional.getField(9)); // Numero de comprobante
        final String date = checkNull(dataTrans.getLocalDate());
        final String time = checkNull(dataTrans.getLocalTime());

        try {
            commerceCode = String.valueOf(Integer.parseInt(commerceCode));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        setTextPrint("C.N.: " + commerceCode.trim(), paint, BOLD_ON, canvas, S_MEDIUM);
        if (numberTicket != null)
            setTextPrint("BOLETA: " + numberTicket, paint, BOLD_ON, canvas, S_MEDIUM);
        if (duplicate) {
            setTextPrint(setCenterText("***** DUPLICADO *****", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
        }
        Log.e(TAG, "printSubHeader2: " + Tools.getSerial().replace("-", ""));
        Log.e(TAG, "printSubHeader2: " + Tools.getSerial());

        setTextPrint(setRightText(" " + FormatInput.formatSerial(Tools.getSerial()) + " " + Tools.getVersion(), S_SMALL, "-"), paint, BOLD_ON, canvas, S_SMALL);
        if (issuerName != null)
            setTextPrint(setCenterText(checkNull(issuerName), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

        // Number Card
        String numberCard = "";
        final String field02 = dataTrans.getField02();
        if (pan != null) {
            if (pan.length() >= 4) {
                numberCard = "T : ************" + pan.substring((dataTrans.getPan().length() - 4));
            }
        } else if (field02 != null) {
            numberCard = "T : ************" + (field02.length() > 4 ? field02.substring(field02.length() - 4) : field02);
        }

        setTextPrint(numberCard, paint, BOLD_ON, canvas, S_MEDIUM);

        setTextPrint("Comprobante Nro.:" + voucherNumber, paint, BOLD_ON, canvas, S_SMALL);

        String formatDate = PAYUtils.StrToDate(date, "yyyyMMdd", "dd/MM/yyyy");
        String formatHour = PAYUtils.StrToDate(time, "HHmmss", "HH:mm:ss");

        setTextPrint(formatDate + " H:" + formatHour, paint, BOLD_ON, canvas, S_MEDIUM);
    }

    /**
     * Obtener el payment_type_description si es null se establece el campo defaultDescription si no se agrega
     *
     * @param defaultDescription Tipo tarjeta
     *                           <p>
     *                           si no retorna campo defaultDescription.
     */
    public String getPaymentTypeDescription(String defaultDescription) {
        if (printExtras != null && printExtras.get("payment_type_description") != null) {
            return printExtras.get("payment_type_description");
        }
        return defaultDescription;
    }

    private String formatSerial(String serial) {
        int espacio = 5;
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < serial.length(); i += espacio) {
            if (i + espacio >= serial.length()) {
                result.append(serial.substring(i));
            } else {
                result.append(serial.substring(i, i + espacio)).append("-");
            }
        }
        return result.toString();
    }

    public int printSale(boolean isCopy, boolean duplicate) throws Exception {
        printHeader();
        printSubHeader(true);

        final DAUtil da = new DAUtil(dataTrans.getMsgID());
        da.setSubCampos(dataTrans.getField63());
        final String kilometers = da.getField(2); // Kilometros
        final String codeProduct1 = da.getField(3); // Codigo del producto 1
        final String quantityProduct1 = da.getField(4); // Cantidad del producto 1
        final String codeProduct2 = da.getField(5); // Codigo del producto 2
        final String quantityProduct2 = da.getField(6); // Cantidad del prodecto 2

        if (kilometers != null) voucher.putText("Km. del vehiculo: " + kilometers, fSmallLeft);
        if (codeProduct1 != null && quantityProduct1 != null) {
            voucher.putText("Cod. Prod 1     : " + codeProduct1, fSmallLeft);
            if (printExtras.get(KEY_DESCRIPTION_PRODUCT + "1") != null) {
                voucher.putText("                  " + printExtras.get(KEY_DESCRIPTION_PRODUCT + "1"), fSmallLeft);
            }
            voucher.putText("Cantidad 1      : " + quantityProduct1, fSmallLeft);
        }
        if (codeProduct2 != null && quantityProduct2 != null) {
            voucher.putText("Cod. Prod 2     : " + codeProduct2, fSmallLeft);
            if (printExtras.get(KEY_DESCRIPTION_PRODUCT + "2") != null) {
                voucher.putText("                  " + printExtras.get(KEY_DESCRIPTION_PRODUCT + "2"), fSmallLeft);
            }
            voucher.putText("Cantidad 2      : " + quantityProduct2, fSmallLeft);
        }

        String cajaNro = ISOUtil.padleft(checkNull(Device.getNumeroCajas()), 4, '0');
        String batch = ISOUtil.padleft(checkNull(dataTrans.getBatchNo()), 4, '0');
        String cargo = ISOUtil.padleft(checkNull(dataTrans.getNroCargo()), 6, '0');
        String aux = TextConvert.setTextColumn("Caja Nro: " + cajaNro + "  Lote: " + batch, "Cargo: " + cargo, Size.SMALL); // TO DO
        voucher.putText(aux, fSmallLeft);

        if (isCopy) {
            if (dataTrans.getAdditionalAmount() != null) {
                voucher.putText("Disponible: G " + PAYUtils.FormatPyg(dataTrans.getAdditionalAmount()), fSmallLeft);
            }
            voucher.putText("ESTE CUPON NO REQUIERE FIRMA", fSmallLeft.addAlign(Align.CENTER).setTypeText(TypeText.PARAGRAPH));
            voucher.putText("COMPROBANTE NO VALIDO COMO FACTURA", fSmallLeft.addAlign(Align.CENTER).setTypeText(TypeText.PARAGRAPH));
            voucher.putText("Copia Cliente", fSmallLeft.addAlign(Align.CENTER));
        } else {
            voucher.putTextFill("", "-", fSmallLeft.addAlign(Align.CENTER));
            voucher.putText("COMPROBANTE NO VALIDO COMO FACTURA", fSmallLeft.addAlign(Align.CENTER));
            voucher.putLine();
            voucher.putLine();
            // NOMBRE DEL TITULAR DE LA TARJETA Y CAMPO DE FIRMA
            String nameCard = checkNull(dataTrans.getNameCard()).trim();
            boolean isHexa;
            voucher.putTextFill("FIRMA X:", "_", fSmallLeft.addBold(Bold.OFF));
            if (!nameCard.equals("----")) {//Con esto evitamos imprimir la cadena "---" que se agrega cuando el labelCard es null
                if (nameCard.length() > 0) {
                    isHexa = nameCard.matches("^[0-9a-fA-F]+$"); // validacion de variable labelCard para evitar conversion
                    if (!isHexa) {
                        nameCard = ISOUtil.convertStringToHex(nameCard);
                    }
                    if (!nameCard.trim().isEmpty())
                        voucher.putText(ISOUtil.hex2AsciiStr(nameCard), fSmallLeft); // TO DO
                }
            }
            voucher.putTextFill("DOC IDENTIDAD NRO.:", "_", fSmallLeft.addBold(Bold.OFF));
            voucher.putText("Copia Comercio", fSmallLeft.addAlign(Align.CENTER));
        }

        voucher.putLine();

        String campo29 = DataAdicional.getField(29); // Mensaje de HOST
        if (campo29 != null && !campo29.isEmpty()) {
            if (campo29.length() >= 40) {
                String data1 = campo29.substring(0, 40);
                String data2 = campo29.substring(40);
                voucher.putText(data1, fSmallCenter);
                voucher.putText(data2, fSmallCenter);
            } else {
                voucher.putText(campo29, fSmallCenter);
            }
        }

        final String quantityTickets = DataAdicional.getField(88); // Numero de boletas
        if (!isCopy && quantityTickets.equals("1")) {
            Bitmap voucherClone = voucher.getBitmap();
            Bitmap bitmap = Bitmap.createBitmap(1, 40, Bitmap.Config.ARGB_8888);
            voucher.putBitmap(Voucher.CopyType.JUST_ORIGINAL, bitmap);
            voucher.putBitmap(Voucher.CopyType.JUST_ORIGINAL, voucherClone);
        }

        int ret = printData(voucher.getBitmap(), dataTrans.getTransEName());

        if (printer != null) {
            printer = null;
        }

        try {
            guardarimagen(voucher.getBitmapCopy(), dataTrans);
        } catch (IOException e) {
            e.printStackTrace();
            Logger.exception(TAG, e);
        }

        voucher.clear();

        Logger.print(TAG, "----------printSaleBancard - return : " + ret);
        return ret;
    }

    public void printHeader() {
        Bitmap logo = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.logo_infonet);
        voucher.putBitmap(logo);

        String name = checkNull(tablaComercios.sucursal.getDescripcion()).trim();
        String direction = checkNull(tablaComercios.sucursal.getDireccionPrincipal()).trim();
        String phone = checkNull(tablaComercios.sucursal.getTelefono()).trim();
        String ruc = checkNull(tablaComercios.sucursal.getRuc()).trim();

        // Header
        voucher.putText(name, fSmallCenter);
        voucher.putText(direction, fSmallCenter);
        voucher.putText(phone, fSmallCenter);
        if (!ruc.isEmpty() && !dataTrans.getEName().equals("ANULACION")) {
            voucher.putText("RUC: " + ruc, fSmallCenter);
        }
    }

    public int printAnnulment(boolean isCopy, boolean duplicate) throws Exception {
        printHeader();
        voucher.putText("ANULACIÓN", fSmallCenter);
        voucher.putLine();

        final DAUtil da = new DAUtil(dataTrans.getMsgID());
        da.setSubCampos(dataTrans.getField63());

        // Data Trans
        String commerceCode = checkNull(dataTrans.getCodigoDelNegocio()).trim(); // Codigo de Negocio (C.N.)
        try {
            commerceCode = String.valueOf(Integer.parseInt(commerceCode));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        final String numberTicket = dataTrans.getRRN(); // Numero de boleta

        final String serialAndVersion = " " + FormatInput.formatSerial(Tools.getSerial()) + " " + Tools.getVersion();
        final String field02 = dataTrans.getField02();
        final String amount = "TOT. " + dataTrans.getAmount();
        final String pan = dataTrans.getPan(); // Pan
        String numberCard = "";

        voucher.putText("C.N.: " + commerceCode, fMediumLeft);
        if (numberTicket != null) {
            voucher.putText("BOLETA: " + numberTicket, fMediumLeft);
        }

        voucher.putTextFill(serialAndVersion, "-", fSmallCenter.addAlign(Align.RIGHT));

        if (pan != null) {
            if (pan.length() >= 4) {
                numberCard = "T : ************" + pan.substring((dataTrans.getPan().length() - 4));
            }
        } else if (field02 != null) {
            numberCard = "T : ************" + (field02.length() > 4 ? field02.substring(field02.length() - 4) : field02);
        }

        voucher.putText(numberCard, fMediumLeft);
        voucher.putLine();

        voucher.putText(amount, fMediumLeft);
        voucher.putLine();

        final String date = checkNull(dataTrans.getLocalDate());
        final String time = checkNull(dataTrans.getLocalTime());
        String formatDate = PAYUtils.StrToDate(date, "yyyyMMdd", "dd/MM/yyyy");
        String formatHour = PAYUtils.StrToDate(time, "HHmmss", "HH:mm:ss");
        voucher.putText(formatDate + " H:" + formatHour, fMediumLeft);

        String cajaNro = ISOUtil.padleft(checkNull(Device.getNumeroCajas()), 4, '0');
        String batch = ISOUtil.padleft(checkNull(dataTrans.getBatchNo()), 4, '0');
        String cargo = ISOUtil.padleft(checkNull(dataTrans.getNroCargo()), 6, '0');
        String aux = TextConvert.setTextColumn("Caja Nro: " + cajaNro + "  Lote: " + batch, "Cargo: " + cargo, Size.SMALL); // TO DO
        voucher.putText(aux, fSmallLeft);

        voucher.putLine();
        voucher.putText("Copia Comercio", fSmallLeft.addAlign(Align.CENTER));

        int ret = printData(voucher.getBitmap(), dataTrans.getTransEName());

        if (printer != null) {
            printer = null;
        }

        try {
            guardarimagen(voucher.getBitmapCopy(), dataTrans);
        } catch (IOException e) {
            e.printStackTrace();
            Logger.exception(TAG, e);
        }

        voucher.clear();

        Logger.print(TAG, "----------printSaleBancard - return : " + ret);
        return ret;
    }

    public void printSubHeader(boolean duplicate) {
        // Data Trans
        String commerceCode = checkNull(dataTrans.getCodigoDelNegocio()).trim(); // Codigo de Negocio (C.N.)
        try {
            commerceCode = String.valueOf(Integer.parseInt(commerceCode));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        final String numberTicket = dataTrans.getRRN(); // Numero de boleta
        final String serialAndVersion = " " + FormatInput.formatSerial(Tools.getSerial()) + " " + Tools.getVersion();
        final String issuerName = checkNull(dataTrans.getField61()); // Nombre del emisor
        final String pan = dataTrans.getPan(); // Pan
        final String voucherNumber = checkNull(DataAdicional.getField(9)); // Numero de comprobante
        final String date = checkNull(dataTrans.getLocalDate());
        final String time = checkNull(dataTrans.getLocalTime());
        // Number Card
        String numberCard = "";
        final String field02 = dataTrans.getField02();
        if (pan != null) {
            if (pan.length() >= 4) {
                numberCard = "T : ************" + pan.substring((dataTrans.getPan().length() - 4));
            }
        } else if (field02 != null) {
            numberCard = "T : ************" + (field02.length() > 4 ? field02.substring(field02.length() - 4) : field02);
        }
        String formatDate = PAYUtils.StrToDate(date, "yyyyMMdd", "dd/MM/yyyy");
        String formatHour = PAYUtils.StrToDate(time, "HHmmss", "HH:mm:ss");

        voucher.putText("C.N.: " + commerceCode, fMediumLeft);
        if (numberTicket != null) {
            voucher.putText("BOLETA: " + numberTicket, fMediumLeft);
        }
        if (duplicate)
            voucher.putText(Voucher.CopyType.JUST_COPY, "***** DUPLICADO *****", fMediumLeft.addAlign(Align.CENTER));
        voucher.putTextFill(serialAndVersion, "-", fSmallCenter.addAlign(Align.RIGHT));
        if (issuerName != null) {
            voucher.putText(issuerName, fSmallCenter);
        }
        voucher.putText(numberCard, fMediumLeft);
        voucher.putText("Comprobante Nro.:" + voucherNumber, fSmallLeft);
        voucher.putText(formatDate + " H:" + formatHour, fMediumLeft);
    }

    public int printVoidMedianet(boolean isCopy, boolean duplicate) {
        Logger.debug("PrintManager>>start>>printVoidMedianet>>");

        this.printTask = new PrintTask();
        this.printTask.setGray(150);

        PrintCanvas canvas = new PrintCanvas();
        Paint paint = new Paint();

        String tid = "25000107";

        printLogoRedInfonet(paint, canvas);

        printHeaderBancard(checkNull(tablaComercios.sucursal.getDescripcion()), checkNull(tablaComercios.sucursal.getDireccionPrincipal()), checkNull(tablaComercios.sucursal.getTelefono()),
                checkNull(tablaComercios.sucursal.getCiudad()), paint, canvas);

        setTextPrint(setCenterText("- CAPTURA ELECTRONICA -", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
        println(paint, canvas);

        printComercioTID(checkNull(tablaComercios.sucursal.getCardAccpMerch()), tid, paint, canvas);

        printLoteDataCARD(checkNull(dataTrans.getIssuerName()), checkNull(dataTrans.getPan()),
                checkNull(dataTrans.getExpDate()), checkNull(dataTrans.getBatchNo()), paint, canvas);

        printTraceNoAuthNo(checkNull(dataTrans.getTraceNo()), checkNull(dataTrans.getAuthCode()), paint, canvas);

        setTextPrint(setTextColumn("RED: MEDIANET", "COMERCIO2: ", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

        print_DateAndTime(dataTrans, paint, canvas, false);


        printTipoTrans(dataTrans.getTransEName(), paint, canvas);

        setTextPrint(setCenterText("TRANSACCION ANULADA", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
        setTextPrint(setCenterText(dataTrans.getTraceNo(), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);

        printAmountVoid(dataTrans.getAmount(), dataTrans.getTypeCoin(), paint, canvas);

        println(paint, canvas);

        int ret = printData(canvas, dataTrans.getTransEName());

        if (printer != null) {
            printer = null;
        }

        return ret;
    }

    public int printReportMedianet(boolean isSettle, boolean isDeleteLote, boolean printTotals, boolean usarCallback) {

        Logger.debug("PrintManager>>start>>printLogout>>");
        this.printTask = new PrintTask();
        this.printTask.setGray(150);

        PrintCanvas canvas = new PrintCanvas();
        Paint paint = new Paint();

        if (printTotals) {

            printLogoRedInfonet(paint, canvas);

            printHeaderBancard(checkNull(tablaComercios.sucursal.getDescripcion()), checkNull(tablaComercios.sucursal.getDireccionPrincipal()), checkNull(tablaComercios.sucursal.getTelefono()),
                    checkNull(tablaComercios.sucursal.getCiudad()), paint, canvas);

            setTextPrint(setCenterText("CIERRE TERMINAL", S_BIG), paint, BOLD_ON, canvas, S_BIG);

            println(paint, canvas);
            println(paint, canvas);
            println(paint, canvas);
            setTextPrint("LOTE#   : " + ISOUtil.zeropad(TMConfig.getInstance().getBatchNo(), 6), paint, BOLD_ON, canvas, S_MEDIUM);
            setTextPrint("TERMINAL: " + checkNull(tablaDevice.getNumeroCajas()), paint, BOLD_ON, canvas, S_MEDIUM);

            print_DateAndTime(null, paint, canvas, true);

            setTextPrint("COMERCIO: " + checkNull(tablaComercios.sucursal.getCardAccpMerch()), paint, BOLD_ON, canvas, S_SMALL);


            println(paint, canvas);
            setTextPrint("==========================================", paint, BOLD_OFF, canvas, S_SMALL);

            if (TransLog.getInstance(idAcquirer).getData().size() == 0) {
                println(paint, canvas);
                setTextPrint(setCenterText("SIN TRANSACCIONES", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
                setTextPrint(setCenterText("CONVENCIONALES", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);

                println(paint, canvas);
                setTextPrint("==========================================", paint, BOLD_OFF, canvas, S_SMALL);
            } else {
                printAllData(paint, canvas, idAcquirer);
            }
        }

        printMessageSettle(paint, canvas);

        if (isSettle || isDeleteLote) {

            println(paint, canvas);

            printLogoRedInfonet(paint, canvas);

            printHeaderBancard(checkNull(tablaComercios.sucursal.getDescripcion()), checkNull(tablaComercios.sucursal.getDireccionPrincipal()), checkNull(tablaComercios.sucursal.getTelefono()),
                    checkNull(tablaComercios.sucursal.getCiudad()), paint, canvas);

            println(paint, canvas);

            setTextPrint(setCenterText("COMERCIO: " + checkNull(tablaComercios.sucursal.getCardAccpMerch()), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);

            setTextPrint(setCenterText("CIERRE TERMINAL", S_BIG), paint, BOLD_ON, canvas, S_BIG);

            println(paint, canvas);
            println(paint, canvas);

            setTextPrint("LOTE#   : " + ISOUtil.zeropad(TMConfig.getInstance().getBatchNo(), 6), paint, BOLD_ON, canvas, S_MEDIUM);
            setTextPrint("TERMINAL: " + checkNull(tablaDevice.getNumeroCajas()), paint, BOLD_ON, canvas, S_MEDIUM);

            print_DateAndTime(null, paint, canvas, true);

            println(paint, canvas);

            setTextPrint("==========================================", paint, BOLD_OFF, canvas, S_SMALL);

            if (isDeleteLote) {
                setTextPrint(setCenterText("LOTE BORRADO", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
            } else {
                setTextPrint(setCenterText("GB CIERRE COMPLETO", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
            }
            setTextPrint(setCenterText("NUM. DE LOTE: " + ISOUtil.zeropad(TMConfig.getInstance().getBatchNo(), 6), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
        }

        int ret = printData(canvas, "");

        if (printer != null) {
            printer = null;
        }

        if (usarCallback) {
            if (callbackPrint != null)
                callbackPrint.getRspPrintReport(0);

            if (callBackSeatle != null)
                callBackSeatle.getRspSeatleReport(0);
        }

        return ret;
    }

    public int printReportMedianet(boolean printTotals) {
        this.printTask = new PrintTask();
        this.printTask.setGray(150);

        PrintCanvas canvas = new PrintCanvas();
        Paint paint = new Paint();

        if (printTotals) {
            println(paint, canvas);

            printLogoRedInfonet(paint, canvas);

            printHeaderBancard(checkNull(tablaComercios.sucursal.getDescripcion()), checkNull(tablaComercios.sucursal.getDireccionPrincipal()), checkNull(tablaComercios.sucursal.getTelefono()),
                    checkNull(tablaComercios.sucursal.getCiudad()), paint, canvas);

            println(paint, canvas);

            setTextPrint(setCenterText("COMERCIO: " + checkNull(tablaComercios.sucursal.getCardAccpMerch()), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);

            setTextPrint(setCenterText("REPORTE DE TOTALES", S_BIG), paint, BOLD_ON, canvas, S_BIG);

            println(paint, canvas);
            println(paint, canvas);

            setTextPrint("LOTE#   : " + ISOUtil.zeropad(TMConfig.getInstance().getBatchNo(), 6), paint, BOLD_ON, canvas, S_MEDIUM);
            setTextPrint("TERMINAL: " + checkNull(tablaDevice.getNumeroCajas()), paint, BOLD_ON, canvas, S_MEDIUM);

            print_DateAndTime(null, paint, canvas, true);

            println(paint, canvas);

            println(paint, canvas);
            setTextPrint("==========================================", paint, BOLD_OFF, canvas, S_SMALL);

            if (TransLog.getInstance(idAcquirer).getData().size() == 0) {
                println(paint, canvas);
                setTextPrint(setCenterText("SIN TRANSACCIONES", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
                setTextPrint(setCenterText("CONVENCIONALES", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);

                println(paint, canvas);
                setTextPrint("==========================================", paint, BOLD_OFF, canvas, S_SMALL);
            } else {
                printAllData(paint, canvas, idAcquirer);
            }
        }

        int ret = printData(canvas, "");

        if (printer != null) {
            printer = null;
        }

        if (callbackPrint != null)
            callbackPrint.getRspPrintReport(0);

        return ret;
    }

    public int printParamInit() {
        String auxText;

        this.printTask = new PrintTask();
        this.printTask.setGray(150);
        String[] datosConexion = UtilNetwork.showDhcpData(mContext);
        PrintCanvas canvas = new PrintCanvas();
        Paint paint = new Paint();
        TransLogLastSettle.getInstance(true).getData();

        paint.setTextSize(20);
        setTextPrint(setCenterText("MEDIANET", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
        println(paint, canvas);
        setTextPrintREV(setCenterText("TEST DEL POS", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
        println(paint, canvas);
        auxText = PAYUtils.getDay() + "/" + PAYUtils.getMonth() + "/" + String.valueOf(PAYUtils.getYear()) + " " + formatoHora(PAYUtils.getLocalTime());
        setTextPrintREV(setCenterText(auxText, S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
        println(paint, canvas);
        setTextPrintREV(setCenterText("INFORMACION DEL COMERCIO RETAIL", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

        setTextPrint(checkNumCharacters("#HDR1:" + tablaComercios.sucursal.getDescripcion(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(checkNumCharacters("#HDR2:" + tablaComercios.sucursal.getDireccionPrincipal(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(checkNumCharacters("#HDR3:TELEFONO:" + tablaComercios.sucursal.getTelefono(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(checkNumCharacters("#HDR4:" + tablaComercios.sucursal.getDireccionPrincipal(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(checkNumCharacters("#HDR5:RUC:" + tablaComercios.sucursal.getRuc(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(checkNumCharacters("MID:" + tablaComercios.sucursal.getCardAccpMerch(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(checkNumCharacters("NUMERO CAJA:" + tablaDevice.getNumeroCajas(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(checkNumCharacters("VERSION: " + CERT.trim() + " " + VERSION, S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

        setTextPrintREV("PARAMETROS RETAIL:", paint, BOLD_ON, canvas, S_SMALL);

        //setTextPrint(checkNumCharacters("FLAG DESCARGADO ITEMS:" + "", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(checkNumCharacters("NUMERO REFERENCIA:" + TMConfig.getInstance().getTraceNo(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(checkNumCharacters("NUMERO DE LOTE:" + ISOUtil.zeropad(TMConfig.getInstance().getBatchNo(), 6), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(checkNumCharacters("TIMEOUT INGRESO A DATOS:" + TMConfig.getInstance().getTimeoutData() / 1000 + " seg.", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(checkNumCharacters("TIMEOUT TRANSACCION:" + Integer.parseInt(checkNull(tablaHost.getTiempoEsperaConexion())) + " seg.", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

        setTextPrintREV("ITEMS ADICIONALES:", paint, BOLD_ON, canvas, S_SMALL);

        setTextPrint(setCenterText("---- IVA ----", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrintREV("COMUNICACIONES:", paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(setCenterText("---- REMOTO ----", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

        //Wifi
        if (getNetype(mContext) == 1) {
            setTextPrint(checkNumCharacters("ILANIP:" + UtilNetwork.getIPAddress(true), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
            setTextPrint(checkNumCharacters("DNS1:" + datosConexion[1], S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
            setTextPrint(checkNumCharacters("DNS2:" + datosConexion[2], S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
            setTextPrint(checkNumCharacters("MASCARA:" + datosConexion[0], S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
            setTextPrint(checkNumCharacters("GATEWAY:" + datosConexion[3], S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

        }//Mobil
        else if (getNetype(mContext) == 2 || getNetype(mContext) == 3) {
            String ip = UtilNetwork.getIPAddress(true);
            setTextPrint(checkNumCharacters("APN:" + checkNull(getAPN(mContext)), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
            setTextPrint(checkNumCharacters("ILANIP:" + ip, S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
            setTextPrint(checkNumCharacters("DNS1:" + datosConexion[1], S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
            setTextPrint(checkNumCharacters("DNS2:" + datosConexion[2], S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
            setTextPrint(checkNumCharacters("MASCARA:" + getMask(ip), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
            setTextPrint(checkNumCharacters("GATEWAY:" + ip.substring(0, ip.lastIndexOf(".")) + ".1", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        }

        setTextPrint(setCenterText("---- HOST ----", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);


        if (ChequeoIPs.seleccioneIP(0).isTls())
            setTextPrint(checkNumCharacters("HOST 1:" + ChequeoIPs.seleccioneIP(0).getIp() + " PUERTO:" + ChequeoIPs.seleccioneIP(0).getPuerto() + " TLS:1", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        else
            setTextPrint(checkNumCharacters("HOST 1:" + ChequeoIPs.seleccioneIP(0).getIp() + " PUERTO:" + ChequeoIPs.seleccioneIP(0).getPuerto() + " TLS:0", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

        if (ChequeoIPs.seleccioneIP(1).isTls())
            setTextPrint(checkNumCharacters("HOST 2:" + ChequeoIPs.seleccioneIP(1).getIp() + " PUERTO:" + ChequeoIPs.seleccioneIP(1).getPuerto() + " TLS:1", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        else
            setTextPrint(checkNumCharacters("HOST 2:" + ChequeoIPs.seleccioneIP(1).getIp() + " PUERTO:" + ChequeoIPs.seleccioneIP(1).getPuerto() + " TLS:0", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

        if (ChequeoIPs.seleccioneIP(2).isTls())
            setTextPrint(checkNumCharacters("HOST 3:" + ChequeoIPs.seleccioneIP(2).getIp() + " PUERTO:" + ChequeoIPs.seleccioneIP(2).getPuerto() + " TLS:1", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        else
            setTextPrint(checkNumCharacters("HOST 3:" + ChequeoIPs.seleccioneIP(2).getIp() + " PUERTO:" + ChequeoIPs.seleccioneIP(2).getPuerto() + " TLS:0", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

        if (ChequeoIPs.seleccioneIP(3).isTls())
            setTextPrint(checkNumCharacters("HOST 4:" + ChequeoIPs.seleccioneIP(3).getIp() + " PUERTO:" + ChequeoIPs.seleccioneIP(3).getPuerto() + " TLS:1", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        else
            setTextPrint(checkNumCharacters("HOST 4:" + ChequeoIPs.seleccioneIP(3).getIp() + " PUERTO:" + ChequeoIPs.seleccioneIP(3).getPuerto() + " TLS:0", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

        println(paint, canvas);

        int ret = printData(canvas, "");

        if (printer != null) {
            printer = null;
        }

        return ret;
    }

    public void imprimirReporte(String tipoReporte, ArrayList<ModeloCierreTrans> data, List<TransLogData> list) {
        switch (tipoReporte) {
            case REPORTE_TOTAL:
                printCierre(data, list, false);
        }
    }

    public int imprimirCierre(LogsCierresModelo modelo, boolean isCierre) {
        this.printTask = new PrintTask();
        this.printTask.setGray(150);
        PrintCanvas canvas = new PrintCanvas();
        Paint paint = new Paint();
        printLogoRedInfonet(paint, canvas);

        String nombreComercio = checkNull(tablaComercios.sucursal.getDescripcion());
        String ciudadComerio = checkNull(tablaComercios.sucursal.getCiudad());
        setTextPrint(setCenterText(nombreComercio.trim(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(setCenterText(ciudadComerio.trim(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

        setTextPrint(setCenterText("CIERRE DE LOTE - LOTE: " + modelo.getNumLote(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(setCenterText("Entre: " + modelo.getFechaUltimoCierre(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(setCenterText("Hasta: " + modelo.getFechaCierre(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

        println(paint, canvas);
        setTextPrint("CANTIDAD       COD. COMERCIO         MONTO", paint, BOLD_ON, canvas, S_SMALL);

        String[] porCodigos = modelo.getDiscriminadoComercios().split("~");
        for (String dataMensaje : porCodigos) {
            String[] discriminado = dataMensaje.split("@");
            setTextPrint(setCenterText(discriminado[1], S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
            setTextPrint(setTextColumn(discriminado[0], PAYUtils.FormatPyg(discriminado[2]), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
        }

        setTextPrint("------------------------------------------", paint, BOLD_ON, canvas, S_SMALL);

        if (modelo.getCantCredito() != null && modelo.getTotalCredito() != null) {
            setTextPrint(setTextColumn("TOTAL CREDITO       " + ISOUtil.padleft(modelo.getCantCredito(), 4, '0') + " Gs.", PAYUtils.FormatPyg(modelo.getTotalCredito()), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        } else {
            setTextPrint(setTextColumn("TOTAL CREDITO       0000 Gs.", "0", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        }

        if (modelo.getCantDebito() != null && modelo.getTotalDebito() != null) {
            setTextPrint(setTextColumn("TOTAL DEBITO        " + ISOUtil.padleft(modelo.getCantDebito(), 4, '0') + " Gs.", PAYUtils.FormatPyg(modelo.getTotalDebito()), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        } else {
            setTextPrint(setTextColumn("TOTAL DEBITO        0000 Gs.", "0", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        }

        if (modelo.getCantMovil() != null && modelo.getTotalMovil() != null) {
            setTextPrint(setTextColumn("TOTAL MOVIL         " + ISOUtil.padleft(modelo.getCantMovil(), 4, '0') + " Gs.", PAYUtils.FormatPyg(modelo.getTotalMovil()), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        } else {
            setTextPrint(setTextColumn("TOTAL MOVIL         0000 Gs.", "0", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        }

        if (modelo.getCantAnular() != null && modelo.getTotalAnular() != null) {
            setTextPrint(setTextColumn("TOTAL ANULAR        " + ISOUtil.padleft(modelo.getCantAnular(), 4, '0') + " Gs.", PAYUtils.FormatPyg(modelo.getTotalAnular()), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        } else {
            setTextPrint(setTextColumn("TOTAL ANULAR        0000 Gs.", "0", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        }

        if (modelo.getCantVuelto() != null && modelo.getTotalVuelto() != null) {
            setTextPrint(setTextColumn("TOTAL VUELTO        " + ISOUtil.padleft(modelo.getCantVuelto(), 4, '0') + " Gs.", PAYUtils.FormatPyg(modelo.getTotalVuelto()), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        } else {
            setTextPrint(setTextColumn("TOTAL VUELTO        0000 Gs.", "0", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        }

        setTextPrint(setCenterText("TOTAL GENERAL", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
        setTextPrint(setTextColumn(ISOUtil.padleft(modelo.getCantGeneral(), 4, '0'), PAYUtils.FormatPyg(modelo.getTotalGeneral()), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);

        if (modelo.getCantSaldo() != null && modelo.getTotalSaldo() != null) {
            setTextPrint(setTextColumn("TOTAL VENTA SALDO   " + ISOUtil.padleft(modelo.getCantSaldo(), 4, '0') + " Gs.", PAYUtils.FormatPyg(modelo.getTotalSaldo()), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        } else {
            setTextPrint(setTextColumn("TOTAL VENTA SALDO   0000 Gs.", "0", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        }

        if (isCierre) {
            println(paint, canvas);
            setTextPrint(setCenterText("Cierre completo", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        }

        println(paint, canvas);
        println(paint, canvas);

        int ret = printData(canvas, "");

        if (printer != null) {
            printer = null;
        }

        return ret;
    }

    public void imprimirReporteDetallado(List<TransLogData> list) {
        int ret = -1;
        this.printTask = new PrintTask();
        this.printTask.setGray(150);

        PrintCanvas canvas = new PrintCanvas();
        Paint paint = new Paint();

        printLogoRedInfonet(paint, canvas);

        String nombreComercio = checkNull(tablaComercios.sucursal.getDescripcion());
        String ciudadComerio = checkNull(tablaComercios.sucursal.getDireccionPrincipal());
        setTextPrint(setCenterText(nombreComercio.trim(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(setCenterText(ciudadComerio.trim(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

        setTextPrint("FECHA             HORA            TERMINAL", paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(setTextColumn(PAYUtils.getLocalDate2() + "        " + PAYUtils.getLocalTime2(), ISOUtil.padleft(tablaDevice.getNumeroCajas(), 4, '0'), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint(setCenterText("REPORTE DE DETALLES", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
        setTextPrint("Nro de lote: " + TMConfig.getInstance().getBatchNo() + " Comercio: " + tablaComercios.sucursal.getMerchantId(), paint, BOLD_ON, canvas, S_SMALL);

        String fechaUltimoCierre = getFechaUltimoCierre();
        String fechaActual = PAYUtils.getLocalDateFormat("dd/MM/yyyy HH:mm");

        setTextPrint("Desde: " + fechaUltimoCierre, paint, BOLD_ON, canvas, S_SMALL);
        setTextPrint("Hasta: " + fechaActual, paint, BOLD_ON, canvas, S_SMALL);

        println(paint, canvas);
        setTextPrint("REF        NRO TARJETA          MONTO TIPO", paint, BOLD_ON, canvas, S_SMALL);

        int contador = 0;

        for (TransLogData data : list) {
            String cargo = data.getNroCargo().substring(2);
            String card = "****";
            if (data.getPan() != null) {
                card += data.getPan().substring(data.getPan().length() - 4);
            }
            String monto = String.valueOf(data.getAmount());
            monto = monto.substring(0, monto.length() - 2);
            monto = PAYUtils.formatMontoGs(monto);
            if (monto.equals("G. nu")) {
                monto = "G. 0";
            }

            setTextPrint(setTextColumn(cargo + "       " + card, monto + "   " + data.getTipoTarjeta(), S_SMALL), paint, BOLD_OFF, canvas, S_SMALL);

            contador++;
        }

        setTextPrint("------------------------------------------", paint, BOLD_ON, canvas, S_SMALL);

        setTextPrint("CANTIDAD DE TRANSACCIONES:   " + contador, paint, BOLD_ON, canvas, S_SMALL);


        println(paint, canvas);
        println(paint, canvas);

        ret = printData(canvas, "");

        if (printer != null) {
            printer = null;
        }
    }

    public void selectPrintReport(String typeReport) {
        switch (typeReport) {
            case ALL_F_REDEN:
                printReport(false);
                break;
            default:
                printReport(true);
                break;
        }
    }

    public int printReport(boolean title) {

        Logger.debug("PrintManager>>start>>printReport>>");
        String address;
        String phone;
        String tmp;
        this.printTask = new PrintTask();
        this.printTask.setGray(150);
        int ret = -1;

        if (TransLog.getInstance(idAcquirer).getSize() == 0) {
            ret = Tcode.T_print_no_log_err;
        } else {

            printer = Printer.getInstance();
            if (printer == null) {
                ret = Tcode.T_sdk_err;
            } else {

                PrintCanvas canvas = new PrintCanvas();
                Paint paint = new Paint();
                printDateAndTime(getFormatDateAndTime(checkNull(PAYUtils.getMonth() + " " + PAYUtils.getDay() + "," + PAYUtils.getYear()), checkNull(PAYUtils.getHMS())), S_MEDIUM, BOLD_OFF, paint, canvas);

                println(paint, canvas);
                println(paint, canvas);

                if (title)
                    setTextPrintREV(setCenterText("REPORTE DEPOSITO", S_BIG), paint, BOLD_OFF, canvas, S_BIG);

                println(paint, canvas);

                setTextPrint(setCenterText("LOTE: " + checkNull(dataTrans.getBatchNo()), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);

                println(paint, canvas);
                println(paint, canvas);

                printLine(paint, canvas);


                println(paint, canvas);

                setTextPrint(setCenterText("REF.      TARJETA       MONTO  ", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
                println(paint, canvas);

                printAllData(paint, canvas, idAcquirer);

                printLine(paint, canvas);

                setTextPrint(setCenterText("FIN  DE  REPORTE", S_BIG), paint, BOLD_OFF, canvas, S_BIG);

                println(paint, canvas);
                println(paint, canvas);
                println(paint, canvas);

                ret = printData(canvas, "");

                if (printer != null) {
                    printer = null;
                }
            }
        }

        return ret;
    }

    public int printTransreject(String value1, String value2, int rerval) {

        Logger.debug("PrintManager>>start>>printTransreject>>");
        String lote;
        String term;
        String idCom;
        this.printTask = new PrintTask();
        this.printTask.setGray(150);
        int ret = -1;

        printer = Printer.getInstance();
        if (printer == null) {
            ret = Tcode.T_sdk_err;
        } else {

            PrintCanvas canvas = new PrintCanvas();
            Paint paint = new Paint();

            lote = TMConfig.getInstance().getBatchNo();
            term = tablaDevice.getNumeroCajas();
            idCom = tablaComercios.sucursal.getCardAccpMerch();

            printSecondHeader(checkNull(lote), checkNull(term), checkNull(idCom), paint, canvas);

            setTextPrint(setCenterText(checkNull(PAYUtils.getSecurityNum(value1, 6, 3)).trim(), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
            println(paint, canvas);

            setTextPrint(setCenterText("REF : " + checkNull(value2).trim(), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
            println(paint, canvas);

            printDateAndTime(getFormatDateAndTime(checkNull(PAYUtils.getMonth() + " " + PAYUtils.getDay() + "," + PAYUtils.getYear()), checkNull(PAYUtils.getHMS())), S_MEDIUM, BOLD_OFF, paint, canvas);
            println(paint, canvas);

            String msg = getErrInfo(String.valueOf(rerval));
            setTextPrint(setCenterText(checkNull(msg).trim(), S_BIG), paint, BOLD_ON, canvas, S_MEDIUM);

            println(paint, canvas);
            println(paint, canvas);
            println(paint, canvas);

            ret = printData(canvas, "");

            if (printer != null) {
                printer = null;
            }
        }

        return ret;
    }

    public int printEMVAppCfg() {

        Logger.debug("PrintManager>>start>>printReportEmv>>");

        EmvAppRow emvappRow = EmvAppRow.getSingletonInstance();
        CapkRow capkRow = CapkRow.getSingletonInstance();

        this.printTask = new PrintTask();
        this.printTask.setGray(150);
        int ret = -1;

        printer = Printer.getInstance();
        if (printer == null) {
            ret = Tcode.T_sdk_err;
        } else {

            PrintCanvas canvas = new PrintCanvas();
            Paint paint = new Paint();
            println(paint, canvas);
            println(paint, canvas);
            println(paint, canvas);
            println(paint, canvas);

            getEMVAPP_ROW(emvappRow, paint, canvas);

            println(paint, canvas);
            println(paint, canvas);

            setTextPrint("EMV KEY INFO", paint, BOLD_ON, canvas, S_MEDIUM);
            println(paint, canvas);

            setTextPrint("Public Key ID      Eff     Exp", paint, BOLD_ON, canvas, S_MEDIUM);
            getCAPK_ROW(capkRow, paint, canvas);

            ret = printData(canvas, "");

            if (printer != null) {
                printer = null;
            }
        }

        return ret;
    }

    public int printConfigTerminal() {

        Logger.debug("PrintManager>>start>>printConfigTerminal>>");

        String term;
        this.printTask = new PrintTask();
        this.printTask.setGray(150);
        int ret = -1;

        printer = Printer.getInstance();
        if (printer == null) {
            ret = Tcode.T_sdk_err;
        } else {

            PrintCanvas canvas = new PrintCanvas();
            Paint paint = new Paint();

            setTextPrint(setCenterText("CONFIGURACION TERMINAL", S_BIG), paint, BOLD_ON, canvas, S_BIG);
            println(paint, canvas);

            term = tablaDevice.getNumeroCajas();
            setTextPrint(checkNumCharacters("TERMINAL: " + checkNull(term), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
            printDateAndTime(getFormatDateAndTime(checkNull(PAYUtils.getMonth() + " " + PAYUtils.getDay() + "," + PAYUtils.getYear()), checkNull(PAYUtils.getHMS())), S_MEDIUM, BOLD_OFF, paint, canvas);
            printLine("=", paint, canvas);

            setTextPrint(setCenterText("LISTA DE APLICACIONES", S_BIG), paint, BOLD_ON, canvas, S_BIG);
            printLine("=", paint, canvas);
            println(paint, canvas);

            setTextPrint(setTextColumn("APPLICATION_ID", "VERSION_NAME", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
            setTextPrint(setTextColumn(BuildConfig.APPLICATION_ID, BuildConfig.VERSION_NAME, S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
            //Obtenido del documento EMVCo Letter of Approval - Contact Terminal Level 2 - October 27, 2017
            setTextPrint(setTextColumn("libemv.so Version 1.0.9", "AFD80709", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
            println(paint, canvas);

            printLine("=", paint, canvas);

            setTextPrint(setCenterText("IP NETWORK ", S_BIG), paint, BOLD_ON, canvas, S_BIG);
            printLine("=", paint, canvas);
            println(paint, canvas);

            // test functions
            setTextPrint(checkNumCharacters("MAC Address: " + checkNull(UtilNetwork.getMACAddress("wlan0")), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
            setTextPrint(checkNumCharacters("IP Address: " + checkNull(UtilNetwork.getIPAddress(true)), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
            println(paint, canvas);

            setTextPrint(setCenterText("FIN  DE  REPORTE", S_BIG), paint, BOLD_ON, canvas, S_BIG);

            println(paint, canvas);
            println(paint, canvas);
            println(paint, canvas);
            println(paint, canvas);


            ret = printData(canvas, "");

            if (printer != null) {
                printer = null;
            }
        }

        return ret;
    }

    /*******
     Tools print
     *******/
    private boolean getCAPK_ROW(CapkRow capkRow, Paint paint, PrintCanvas canvas) {
        boolean ok = false;
        String eff;
        String exp;
        String tmp;
        DbHelper databaseAccess = new DbHelper(mContext, NAME_DB, null, 1);
        databaseAccess.openDb(NAME_DB);
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        int counter = 1;
        for (String s : CapkRow.fields) {
            sql.append(s);
            if (counter++ < CapkRow.fields.length) {
                sql.append(",");
            }
        }
        sql.append(" from capks");
        sql.append(";");

        try {

            Cursor cursor = databaseAccess.rawQuery(sql.toString());
            cursor.moveToFirst();
            int indexColumn;
            while (!cursor.isAfterLast()) {
                capkRow.clearCapkRow();
                indexColumn = 0;
                for (String s : CapkRow.fields) {
                    capkRow.setCapkRow(s, cursor.getString(indexColumn++));
                }

                //Effect date // PREGUNTARLE A FER
              /*  tmp = capkRow.getEffectDate();
                eff = tmp.substring(4, 6);
                eff += "/";
                eff += tmp.substring(2, 4);*/

                //Exp date
                tmp = capkRow.getKeyExpirationDate();
                exp = tmp.substring(4, 6);
                exp += "/";
                exp += tmp.substring(2, 4);

                setTextPrint(setTextColumn(capkRow.getKeyRid() + "-" + capkRow.getKeyId(), "eff" + "  " + exp, S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);

                ok = true;
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            Logger.exception(TAG, e);
            Logger.error("", e);
        }
        databaseAccess.closeDb();
        return ok;
    }

    private boolean getEMVAPP_ROW(EmvAppRow emvappRow, Paint paint, PrintCanvas canvas) {
        boolean ok = false;
        int aux;
        String tmp;
        long flr;
        DbHelper databaseAccess = new DbHelper(mContext, NAME_DB, null, 1);
        databaseAccess.openDb(NAME_DB);
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        int counter = 1;
        for (String s : EmvAppRow.fields) {
            sql.append(s);
            if (counter++ < EmvAppRow.fields.length) {
                sql.append(",");
            }
        }
        sql.append(" from emvapps");
        sql.append(";");

        try {

            Cursor cursor = databaseAccess.rawQuery(sql.toString());
            cursor.moveToFirst();
            int indexColumn;

            while (!cursor.isAfterLast()) {
                emvappRow.clearEmvAppRow();
                indexColumn = 0;
                for (String s : EmvAppRow.fields) {
                    emvappRow.setEmvAppRow(s, cursor.getString(indexColumn++));
                }

                setTextPrint("EMV APP CFG", paint, BOLD_ON, canvas, S_MEDIUM);
                println(paint, canvas);
                TLV_parsing tlvParsing = new TLV_parsing(emvappRow.geteACFG());
                setTextPrint(setTextColumn("AID: ", ISOUtil.bcd2str(tlvParsing.getValueB(0x9f06), 0, tlvParsing.getValueB(0x9f06).length), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);

                byte[] output = hex2byte(emvappRow.geteBitField());

                //AQC profile
                aux = (output[0] & 0x80);
                tmp = (aux == 0) ? "Y" : "N";
                setTextPrint(setTextColumn("Def ACQ Profile: ", tmp, S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);


                setTextPrint(setTextColumn("Type: ", (emvappRow.geteType()), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
                setTextPrint(setTextColumn("Ver: ", ISOUtil.bcd2str(tlvParsing.getValueB(0x9f09), 0, tlvParsing.getValueB(0x9f09).length), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);

                if (tlvParsing.getValueB(0x9f1b) != null) {
                    flr = Long.parseLong(ISOUtil.bcd2str(tlvParsing.getValueB(0x9f1b), 0, tlvParsing.getValueB(0x9f1b).length));
                    setTextPrint(setTextColumn("FLR LIMIT: ", PAYUtils.getStrAmount(flr), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
                }

                if (tlvParsing.getValueB(0xdf7f) != null) {
                    flr = Long.parseLong(ISOUtil.bcd2str(tlvParsing.getValueB(0xdf7f), 0, tlvParsing.getValueB(0xdf7f).length));
                    setTextPrint(setTextColumn("FLR LIMIT(0): ", PAYUtils.getStrAmount(flr), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
                }

                setTextPrint(setTextColumn("Basic Random: ", emvappRow.geteRSBThresh(), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
                setTextPrint(setTextColumn("Target %: ", emvappRow.geteRSTarget() + "-" + emvappRow.geteRSBMax(), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);

                setTextPrint(setTextColumn("Country: ", ISOUtil.bcd2str(tlvParsing.getValueB(0x9f1a), 0, tlvParsing.getValueB(0x9f1a).length), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
                setTextPrint(setTextColumn("Currency: ", ISOUtil.bcd2str(tlvParsing.getValueB(0x5f2a), 0, tlvParsing.getValueB(0x5f2a).length), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);

                aux = (output[0] & 0x01);
                tmp = (aux == 0) ? "Y" : "N";
                setTextPrint(setTextColumn("Allow Partial AID: ", tmp, S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);

                aux = (output[0] & 0x02);
                tmp = (aux == 0) ? "Y" : "N";
                setTextPrint(setTextColumn("Referral Enable: ", tmp, S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);

                aux = (output[0] & 0x04);
                tmp = (aux == 0) ? "Y" : "N";
                setTextPrint(setTextColumn("PIN Bypass Enable: ", tmp, S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);

                aux = (output[0] & 0x08);
                tmp = (aux == 0) ? "Y" : "N";
                setTextPrint(setTextColumn("Force TRM: ", tmp, S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);

                setTextPrint(setTextColumn("TAC Denial: ", (emvappRow.geteTACDenial()), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
                setTextPrint(setTextColumn("TAC Online: ", (emvappRow.geteTACOnline()), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
                setTextPrint(setTextColumn("TAC Default: ", (emvappRow.geteTACDefault()), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);

                setTextPrint(setTextColumn("Term Cap: ", ISOUtil.bcd2str(tlvParsing.getValueB(0x9f33), 0, tlvParsing.getValueB(0x9f33).length), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
                setTextPrint(setTextColumn("Add Cap: ", ISOUtil.bcd2str(tlvParsing.getValueB(0x9f40), 0, tlvParsing.getValueB(0x9f40).length), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);

                println(paint, canvas);
                println(paint, canvas);

                ok = true;
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            Logger.exception(TAG, e);
            Logger.error("", e);
        }
        databaseAccess.closeDb();
        return ok;
    }

    private void printAllData(Paint paint, PrintCanvas canvas, String batch) {
        List<TransLogData> list = new ArrayList<>(TransLog.getInstance(batch).getData());

        if (list != null) {
            List<TransLogData> listFinal = new ArrayList<>(orderList(list));
            List<TransLogData> listDetalle = new ArrayList<>(listFinal);

            Collections.sort(listDetalle, new Comparator<TransLogData>() {
                @Override
                public int compare(TransLogData transLogData, TransLogData t1) {
                    return transLogData.getTraceNo().compareTo(t1.getTraceNo());
                }
            });
            //Collections.reverse(listDetalle);

            try {

                limpiarVar();

                limpiarComerciosImpresos(listFinal);
                //List de comercios
                for (TransLogData translogAllAdquirer : listFinal) {

                    if (soloUnCiclo)
                        break;

                }

                imprimirGranTotal(paint, canvas);

            } catch (ArrayIndexOutOfBoundsException exception) {
                Logger.exception(TAG, exception);
            }
        }
    }

    private void limpiarComerciosImpresos(List<TransLogData> listFinal) {
        comercioImpreso = new String[listFinal.size()];
        for (int i = 0; i < comercioImpreso.length; i++) {
            comercioImpreso[i] = "-";
        }
    }

    private void limpiarVar() {
        contTransAcq = 0;
        contTotalTransAcq = 0;
        contTransEmisor = 0;

        subTotalSubTotal = 0;
        ivaAmountSubTotal = 0;
        serviceAmountSubTotal = 0;
        tipAmountSubTotal = 0;
        montoFijoSubTotal = 0;

        totalTempAmount = 0;
        totalTempIva = 0;
        totalTempServiceAmount = 0;
        totalTempTipAmount = 0;
        totalTempMontoFijo = 0;

        granTotal = 0;
        granTotalIva = 0;
        granTotalService = 0;
        granTotalTip = 0;
        granTotalMontoFijo = 0;

        montoFijo = 0;

        nombreActualEmisor = "";
        fechaTransActual = "";
        nombreAdquirenteActual = "";
        soloUnCiclo = false;
        idxImpresionComercio = 0;
        omitir = false;


    }

    private void imprimirGranTotal(Paint paint, PrintCanvas canvas) {
        println(paint, canvas);
        println(paint, canvas);
        setTextPrintREV("Total Generalizado", paint, BOLD_ON, canvas, S_BIG);
        println(paint, canvas);
        setTextPrint("------------------------------------------", paint, BOLD_OFF, canvas, S_SMALL);
        setTextPrint(setTextColumn("GRAN TOTAL:          " + contTotalTransAcq, "$ " + PAYUtils.getStrAmount(granTotal) + "    ", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

        setTextPrint(setTextColumn("T.Neto:" + "", "$ " + PAYUtils.getStrAmount(granTotal + granTotalIva + granTotalService + granTotalTip) + "    ", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        println(paint, canvas);
        setTextPrint("==========================================", paint, BOLD_OFF, canvas, S_SMALL);
    }

    private void imprimirTotalComercio(Paint paint, PrintCanvas canvas, long totalComercio, long totalIvaComercio, long totalServiceComercio, long totalTipComercio) {
        println(paint, canvas);
        println(paint, canvas);
        setTextPrint(setTextColumn("VENTA:          " + contTotalTransAcq, "$ " + PAYUtils.getStrAmount(totalComercio) + "    ", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        println(paint, canvas);
        setTextPrint("==========================================", paint, BOLD_OFF, canvas, S_SMALL);
    }

    private void printVersionVoid(boolean isRePrint, Paint paint, PrintCanvas canvas) {
        if (isRePrint) {
            setTextPrint(setCenterText("XXX   COPIA   XXX", S_BIG), paint, BOLD_ON, canvas, S_BIG);
            println(paint, canvas);
        }

        setTextPrint(setTextColumn(" ", "Version " + checkNull(VERSION).trim(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);

        println(paint, canvas);
        println(paint, canvas);
        println(paint, canvas);
    }

    private void limpiarMontoSubtotales() {
        subTotal = 0;
        ivaAmount = 0;
        serviceAmount = 0;
        tipAmount = 0;
        montoFijo = 0;
    }

    private void limpiarMontoSubTotales2() {
        subTotalSubTotal = 0;
        ivaAmountSubTotal = 0;
        serviceAmountSubTotal = 0;
        tipAmountSubTotal = 0;
        montoFijoSubTotal = 0;
    }

    private void limpiarMontoTotales() {
        totalTempAmount = 0;
        totalTempIva = 0;
        totalTempServiceAmount = 0;
        totalTempTipAmount = 0;
        totalTempMontoFijo = 0;
    }


    private String checkNull(String strText) {
        if (strText == null) {
            strText = "   ";
        }
        return strText;
    }

    private void printDateAndDues(Paint paint, PrintCanvas canvas) {

        String numCoutas = "0";

        if (dataTrans.getNumCuotas() > 1) {
            numCoutas = dataTrans.getNumCuotas() + "";
        }
        if (numCoutas.equals("0")) {
            setTextPrint(setTextColumn(getFormatDateAndTime(checkNull(dataTrans.getDatePrint()), checkNull(dataTrans.getLocalTime())),
                    " ", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        } else {
            setTextPrint(setTextColumn(getFormatDateAndTime(checkNull(dataTrans.getDatePrint()), checkNull(dataTrans.getLocalTime())),
                    "CUOTAS: " + numCoutas, S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        }
    }

    private void printSecondHeader(String lote, String term, String id_com, Paint paint, PrintCanvas canvas) {
        setTextPrint(checkNumCharacters("LOTE:" + lote.trim() + " TERM:" + term.trim() + " ID COM:" + id_com.trim(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        println(paint, canvas);
        printLine(paint, canvas);
        println(paint, canvas);
    }

    private void print_AP_REF(String AP, String REF, Paint paint, PrintCanvas canvas) {
        setTextPrint(setTextColumn("AP: " + AP.trim(), "REF: " + REF.trim(), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
        println(paint, canvas);
    }

    private void print_expires_RRN_tipo(String expires, String RRN, String type, Paint paint, PrintCanvas canvas) {
        setTextPrint(checkNumCharacters("VENCE:" + expires.substring(2) + "/" + expires.substring(0, 2) + " RRN:" + RRN.trim() + " TIPO:" + type.trim(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
    }

    private void print_Rnn(String RRN, Paint paint, PrintCanvas canvas) {
        setTextPrint(checkNumCharacters(" RRN: " + RRN.trim(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
    }

    private void printAID(String AID, Paint paint, PrintCanvas canvas) {
        if (isICC) {
            setTextPrint("AID: " + checkNumCharacters(AID.trim(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        } else if (isNFC) {
            if (dataTrans.getAIDName().equals("") && AID.trim().substring(0, 14).equals("A0000000031010")) {
                setTextPrint("AID NAME: VISA CREDITO", paint, BOLD_ON, canvas, S_SMALL);
            } else {
                setTextPrint("AID NAME: " + dataTrans.getAIDName(), paint, BOLD_ON, canvas, S_SMALL);
            }
            setTextPrint("AID: " + checkNumCharacters(AID.trim(), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        }

    }

    private void printDateAndTime(String data, int typeFont, boolean isBold, Paint paint, PrintCanvas canvas) {
        setTextPrint(checkNumCharacters(data, typeFont), paint, isBold, canvas, typeFont);
    }

    private String getFormatDateAndTime(String date, String time) {
        String newtime = PAYUtils.StringPattern(time.trim(), "HHmmss", "HH:mm");
        return "FECHA: " + date.trim() + "  HORA: " + newtime;
    }

    private void printAmountVoid(long total, String typeCoin, Paint paint, PrintCanvas canvas) {
        if (typeCoin.equals(LOCAL)) {
            typeCoin = "LOCAL.";
        } else {
            typeCoin = "DOLAR";
        }
        setTextPrint(setTextColumn("TOTAL      : $", formatAmountLess(total), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
        println(paint, canvas);
        println(paint, canvas);
    }

    private void printSignature(String labelCard, boolean isCopy, Paint paint, PrintCanvas canvas) {
        if (!isCopy) {
            if (TMConfig.getInstance().isBanderaMessageFirma()) {
                if (dataTrans.getEntryMode().equals(MODE_CTL + CapPinPOS())) {
                    if (MasterControl.ctlSign)
                        setTextPrint("FIRMA X........................", paint, BOLD_OFF, canvas, S_MEDIUM);
                } else {
                    setTextPrint("FIRMA X........................", paint, BOLD_OFF, canvas, S_MEDIUM);
                }
            }

            if (!labelCard.equals("---"))//Con esto evitamos imprimir la cadena "---" que se agrega cuando el labelCard es null
                if (labelCard.length() > 0) {
                    setTextPrint(setCenterText(checkNumCharacters(labelCard.trim(), S_MEDIUM), S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
                    println(paint, canvas);
                }

            if (TMConfig.getInstance().isBanderaMessageDoc())
                setTextPrint(checkNumCharacters("DOC: ", S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);

            if (TMConfig.getInstance().isBanderaMessageTel())
                setTextPrint(checkNumCharacters("TEL:", S_MEDIUM), paint, BOLD_OFF, canvas, S_MEDIUM);
            println(paint, canvas);
        }
    }

    private void printLine(String character, Paint paint, PrintCanvas canvas) {
        StringBuilder dat = new StringBuilder();
        for (int i = 0; i < 45; i++) {
            dat.append(character);
        }
        setTextPrint(dat.toString(), paint, BOLD_ON, canvas, S_SMALL);
    }

    private void printLine(Paint paint, PrintCanvas canvas) {
        setTextPrint("---------------------------------------------", paint, BOLD_ON, canvas, S_SMALL);
    }

    private void printLineResult(Paint paint, PrintCanvas canvas) {
        println(paint, canvas);
        setTextPrint("                  =======", paint, BOLD_OFF, canvas, S_BIG);
        println(paint, canvas);
    }

    private String setTextColumn(String columna1, String columna2, int size) {
        String aux = "";
        String auxText = columna2;
        auxText = setRightText(auxText, size);
        String auxText2 = columna1;

        if (auxText2.length() < auxText.length())
            aux = auxText.substring(auxText2.length());

        auxText2 += aux;

        return auxText2;
    }

    private String checkNumCharacters(String data, int size) {
        String dataPrint = "";
        int lenData = 0;

        lenData = data.length();

        switch (size) {
            case S_SMALL:
                if (lenData > MAX_CHAR_SMALL) {
                    dataPrint = data.substring(0, MAX_CHAR_SMALL);
                } else {
                    dataPrint = data;
                }
                break;

            case S_MEDIUM:
                if (lenData > MAX_CHAR_MEDIUM) {
                    dataPrint = data.substring(0, MAX_CHAR_MEDIUM);
                } else {
                    dataPrint = data;
                }
                break;

            case S_BIG:
                if (lenData > MAX_CHAR_BIG) {
                    dataPrint = data.substring(0, MAX_CHAR_BIG);
                } else {
                    dataPrint = data;
                }
                break;

        }
        return dataPrint;
    }

    private void println(Paint paint, PrintCanvas canvas) {
        setTextPrint("                                             ", paint, BOLD_ON, canvas, S_SMALL);
    }

    private void setTextPrint(String data, Paint paint, boolean bold, PrintCanvas canvas, int size) {
        Typeface typeface = (Typeface.MONOSPACE);
        data = checkNumCharacters(data, size);
        canvas.drawBitmap(drawText(data, (float) size, bold, typeface), paint);
    }

    private void setTextPrintREV(String data, Paint paint, boolean bold, PrintCanvas canvas, int size) {
        Typeface typeface = (Typeface.MONOSPACE);
        canvas.drawBitmap(drawTextREV(data, (float) size, bold, typeface), paint);
    }

    private String setCenterText(String data, int size) {
        data = padLeft(checkNumCharacters(data.trim(), size), size);
        return data;
    }

    private String setRightText(String data, int size) {
        String dataFinal = "";
        int len1 = 0;
        switch (size) {
            case S_SMALL:
                len1 = MAX_CHAR_SMALL - data.length();
                break;
            case S_MEDIUM:
                len1 = MAX_CHAR_MEDIUM - data.length();
                break;
            case S_BIG:
                len1 = MAX_CHAR_BIG - data.length();
                break;
        }

        for (int i = 0; i < len1; i++) {
            dataFinal += " ";
        }

        dataFinal += data;
        return dataFinal;
    }

    private String fill(String data, int size) {
        String dataFinal = "";
        int len1 = 0;
        switch (size) {
            case S_SMALL:
                len1 = MAX_CHAR_SMALL;
                break;
            case S_MEDIUM:
                len1 = MAX_CHAR_MEDIUM;
                break;
            case S_BIG:
                len1 = MAX_CHAR_BIG;
                break;
        }

        for (int i = 0; i < len1; i++) {
            dataFinal += data;
        }

        dataFinal += data;
        return dataFinal;
    }

    private String setLeftText(String data, int size, String fill) {
        String dataFinal = "";
        int len1 = 0;
        switch (size) {
            case S_SMALL:
                len1 = MAX_CHAR_SMALL - data.length();
                break;
            case S_MEDIUM:
                len1 = MAX_CHAR_MEDIUM - data.length();
                break;
            case S_BIG:
                len1 = MAX_CHAR_BIG - data.length();
                break;
        }

        for (int i = 0; i < len1; i++) {
            dataFinal += fill;
        }

        return data + dataFinal;
    }

    private String setRightText(String data, int size, String fill) {
        String dataFinal = "";
        int len1 = 0;
        switch (size) {
            case S_SMALL:
                len1 = MAX_CHAR_SMALL - data.length();
                break;
            case S_MEDIUM:
                len1 = MAX_CHAR_MEDIUM - data.length();
                break;
            case S_BIG:
                len1 = MAX_CHAR_BIG - data.length();
                break;
        }

        for (int i = 0; i < len1; i++) {
            dataFinal += fill;
        }

        dataFinal += data;
        return dataFinal;
    }

    private String formatAmoun(long valor) {
        // Si pongo 1 en el monto se estalla
        String result = valor + "";
        result = result.substring(0, result.length() - 2);
        Long.parseLong(result);
        return String.format("%,d", Long.parseLong(result)).replace(",", ".");
    }

    private String formatAmounCaja(long valor) {
        String result = valor + "";
        result = result.substring(0, result.length() - 2);
        return result;
    }

    private String formatAmountLess(long valor) {

        String auxText;

        if (String.valueOf(valor).length() == 1)
            auxText = ISOUtil.decimalFormat("0" + String.valueOf(valor));
        else
            auxText = ISOUtil.decimalFormat(String.valueOf(valor));

        return auxText;
    }

    private String padLeft(String data, int size) {

        String dataFinal = "";
        int len1 = 0;

        switch (size) {
            case S_SMALL:
                len1 = MAX_CHAR_SMALL - data.length();
                break;
            case S_MEDIUM:
                len1 = MAX_CHAR_MEDIUM - data.length();
                break;
            case S_BIG:
                len1 = MAX_CHAR_BIG - data.length();
                break;
        }

        for (int i = 0; i < len1 / 2; i++) {
            dataFinal += " ";
        }
        dataFinal += data;

        return dataFinal;
    }

    private Bitmap drawText(String text, float textSize, boolean bold, Typeface typeface) {

        // Get text dimensions
        TextPaint textPaint = new TextPaint(ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);

        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(textSize);
        textPaint.setTypeface(typeface);
        textPaint.setFakeBoldText(bold);

        StaticLayout mTextLayout = new StaticLayout(text, textPaint, 400, Layout.Alignment.ALIGN_NORMAL, 40.0f, 20.0f, false);

        // Create bitmap and canvas to draw to
        Bitmap b = Bitmap.createBitmap(400, mTextLayout.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(b);

        // Draw background
        Paint paint = new Paint(ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTextSize(textSize);
        c.drawPaint(paint);

        // Draw text
        c.save();
        c.translate(0, 0);
        mTextLayout.draw(c);
        c.restore();

        return b;
    }

    private Bitmap drawTextREV(String text, float textSize, boolean bold, Typeface typeface) {

        // Get text dimensions
        TextPaint textPaint = new TextPaint(ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);

        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(textSize);
        textPaint.setTypeface(typeface);
        textPaint.setFakeBoldText(bold);

        StaticLayout mTextLayout = new StaticLayout(text, textPaint, 400, Layout.Alignment.ALIGN_NORMAL, 40.0f, 20.0f, false);

        // Create bitmap and canvas to draw to
        Bitmap b = Bitmap.createBitmap(400, mTextLayout.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(b);

        // Draw background
        Paint paint = new Paint(ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTextSize(textSize);
        c.drawPaint(paint);

        // Draw text
        c.save();
        c.translate(0, 0);
        mTextLayout.draw(c);
        c.restore();

        return b;
    }

    private int printData(PrintCanvas pCanvas, String text) {
        final CountDownLatch latch = new CountDownLatch(1);
        printer = Printer.getInstance();
        int ret = printer.getStatus();
        Logger.debug("打印机状态：" + ret);
        if (Printer.PRINTER_STATUS_PAPER_LACK == ret) {
            Logger.debug("打印机缺纸，提示用户装纸");
            //transUI.handling(60 * 1000, Tcode.Status.printer_lack_paper, text);
            long start = SystemClock.uptimeMillis();
            while (true) {
                if (SystemClock.uptimeMillis() - start > 60 * 1000) {
                    ret = Printer.PRINTER_STATUS_PAPER_LACK;
                    break;
                }
                if (printer.getStatus() == Printer.PRINTER_OK) {
                    ret = Printer.PRINTER_OK;
                    break;
                } else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Logger.exception(TAG, e);
                        Thread.currentThread().interrupt();
                        Logger.debug("printer task interrupted");
                    }
                }
            }
        }
        Logger.debug("开始打印");
        if (ret == Printer.PRINTER_OK) {
            //transUI.handling(60 * 1000, Tcode.Status.printing_recept, text);
            printTask.setPrintCanvas(pCanvas);
            if (printer != null) {
                printer.startPrint(printTask, new PrinterCallback() {
                    @Override
                    public void onResult(int i, PrintTask printTask) {
                        latch.countDown();
                    }
                });
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    Logger.exception(TAG, e);
                    Logger.error("Exception", e);
                    Thread.currentThread().interrupt();
                }
            }
        }
        return ret;
    }

    private int printData(Bitmap pCanvas, String text) {
        final CountDownLatch latch = new CountDownLatch(1);
        printer = Printer.getInstance();
        int ret = printer.getStatus();
        Logger.debug("打印机状态：" + ret);
        if (Printer.PRINTER_STATUS_PAPER_LACK == ret) {
            Logger.debug("打印机缺纸，提示用户装纸");
            //transUI.handling(60 * 1000, Tcode.Status.printer_lack_paper, text);
            long start = SystemClock.uptimeMillis();
            while (true) {
                if (SystemClock.uptimeMillis() - start > 60 * 1000) {
                    ret = Printer.PRINTER_STATUS_PAPER_LACK;
                    break;
                }
                if (printer.getStatus() == Printer.PRINTER_OK) {
                    ret = Printer.PRINTER_OK;
                    break;
                } else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Logger.exception(TAG, e);
                        Thread.currentThread().interrupt();
                        Logger.debug("printer task interrupted");
                    }
                }
            }
        }
        Logger.debug("开始打印");
        if (ret == Printer.PRINTER_OK) {
            //transUI.handling(60 * 1000, Tcode.Status.printing_recept, text);
            printTask.setPrintBitmap(pCanvas);
            if (printer != null) {
                printer.startPrint(printTask, new PrinterCallback() {
                    @Override
                    public void onResult(int i, PrintTask printTask) {
                        latch.countDown();
                    }
                });
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    Logger.exception(TAG, e);
                    Logger.error("Exception", e);
                    Thread.currentThread().interrupt();
                }
            }
        }
        return ret;
    }

    private void guardarimagen(Bitmap recibo, TransLogData transLogData) throws IOException {
        ModeloVoucherReimpresion voucherReimpresion = new ModeloVoucherReimpresion();
        String pan = transLogData.getPan();
        if (pan != null && !pan.isEmpty()) {
            String panOfucado = ISOUtil.ofuscarPAN(pan);
            voucherReimpresion.setPan(panOfucado);
        }
        voucherReimpresion.setFecha(PAYUtils.printStr(transLogData.getLocalDate(), transLogData.getLocalTime()));
        if (transLogData.getRRN() != null) {
            voucherReimpresion.setNroBoleta(transLogData.getRRN());
        } else {
            voucherReimpresion.setNroBoleta("");
        }
        savePreferences(transLogData, "guardarimagen");
        voucherReimpresion.setNroCargo(transLogData.getNroCargo());
        try {
            voucherReimpresion.setMonto(transLogData.getAmount().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        voucherReimpresion.setTipoVenta(transLogData.getTipoVenta());
        voucherReimpresion.setVoucher(crearImagen(recibo));
        ReimpresionVoucherDAO guardarVoucher = new ReimpresionVoucherDAOImpl(mContext);
        guardarVoucher.ingresarRegistro(voucherReimpresion);
    }

    private void savePreferences(TransLogData transLogData, String metodo) {
        try {
            if (transLogData != null) {
                String impAmount = transLogData.getAmount().toString();
                String impTraceNro = transLogData.getTraceNo();
                String impNCargo = transLogData.getNroCargo();
                String nroBoleta = transLogData.getRRN();
                SharedPreferences.Editor transInfo = mcontext.getSharedPreferences("transInfo", MODE_PRIVATE).edit();
                transInfo.putString(PREF_AMOUNT, impAmount);
                transInfo.putString(PREF_TRACE, impTraceNro);
                transInfo.putString(PREF_CARGO, impNCargo);
                transInfo.putString(PREF_METODO, metodo);
                transInfo.putString(PREF_DATE, PAYUtils.getLocalDate());
                transInfo.putString(PREF_TIME, PAYUtils.getLocalTime());
                transInfo.putString("nroBoleta", nroBoleta);
                transInfo.apply();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.exception("savePreferences: Exception ", e);
            Logger.error("savePreferences: Exception ", e);
        }
    }

    public byte[] crearImagen(Bitmap bitmap) {
        // tamaño del baos depende del tamaño de tus imagenes en promedio
        ByteArrayOutputStream baos = new ByteArrayOutputStream(10240);
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);
        // aqui tenemos el byte[] con el imagen comprimido, ahora lo guardemos en SQLite
        return baos.toByteArray();
    }

    private int checkPrinterStatus() {
        long t0 = System.currentTimeMillis();
        int ret;
        while (true) {
            if (System.currentTimeMillis() - t0 > 30000) {
                ret = -1;
                break;
            }
            ret = printer.getStatus();
            Logger.debug("printer.getStatus() ret = " + ret);
            if (ret == Printer.PRINTER_OK) {
                Logger.debug("printer.getStatus()=Printer.PRINTER_OK");
                Logger.debug("打印机状态正常");
                break;
            } else if (ret == -3) {
                Logger.debug("printer.getStatus()=Printer.PRINTER_STATUS_PAPER_LACK");
                Logger.debug("提示用户装纸...");
                break;
            } else if (ret == Printer.PRINTER_STATUS_BUSY) {
                Logger.debug("printer.getStatus()=Printer.PRINTER_STATUS_BUSY");
                Logger.debug("打印机忙");
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Logger.exception(TAG, e);
                    Logger.error("Exception" + e);
                    Thread.currentThread().interrupt();
                }
            } else {
                break;
            }
        }
        return ret;
    }

    private String formatTranstype(String type) {
        int index = 0;
        for (int i = 0; i < PrintRes.TRANSEN.length; i++) {
            if (PrintRes.TRANSEN[i].equals(type)) {
                index = i;
            }
        }
        if (Locale.getDefault().getLanguage().equals("zh")) {
            return PrintRes.TRANSCH[index] + "(" + type + ")";
        } else {
            return type;
        }
    }

    private String formatDetailsType(TransLogData data) {
        if (data.isICC()) {
            return "(C)";
        } else if (data.isNFC()) {
            return "(CTLS)";
        } else {
            return "(B)";
        }
    }

    private String formatDetailsAuth(TransLogData data) {
        if (data.getAuthCode() == null) {
            return "000000";
        } else {
            return data.getAuthCode();
        }
    }

    private void setFontStyle(Paint paint, int size, boolean isBold) {
        if (isBold) {
            paint.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            paint.setTypeface(Typeface.SERIF);
        }
        switch (size) {
            case 0:
                break;
            case 1:
                paint.setTextSize(15F);
                break;
            case 2:
                paint.setTextSize(22F);
                break;
            case 3:
                paint.setTextSize(30F);
                break;
            default:
                break;
        }
    }

    /**
     * Bancard
     */
    private void printLogoRedInfonet(Paint paint, PrintCanvas canvas) {
        try {
            Bitmap image = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.logoinfonet02);
            canvas.setX(115);
            canvas.drawBitmap(image, paint);
            canvas.setX(0);
        } catch (Exception e) {
            Logger.exception(TAG, e);
            Logger.error("ERROR", e.toString());
        }
    }

    private void addBitmap(Bitmap bitmap, Paint paint, PrintCanvas canvas) {
        try {
            canvas.setY(bitmap.getWidth());
            canvas.drawBitmap(bitmap, paint);
        } catch (Exception e) {
            Logger.exception(TAG, e);
            Logger.error("ERROR", e.toString());
        }
    }

    /**
     * Medianet
     */
    private void printHeaderBancard(String text1, String text2, String text3, String text4, Paint paint, PrintCanvas canvas) {
        if (!text1.isEmpty() && !text1.substring(0, 1).equals("0")) {
            setTextPrint(setCenterText(text1.trim(), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
        }
        if (!text2.isEmpty() && !text2.substring(0, 1).equals("0")) {
            setTextPrint(setCenterText(text2.trim(), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
        }
        if (!text3.isEmpty() && !text3.substring(0, 1).equals("0")) {
            setTextPrint(setCenterText(text3.trim(), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
        }
        if (!text4.isEmpty() && !text4.substring(0, 1).equals("0")) {
            setTextPrint(setCenterText(text4.trim(), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
        }
    }

    /**
     * Bancard
     */
    private void printComercioTID(String comercio, String tid, Paint paint, PrintCanvas canvas) {
        setTextPrint(setTextColumn("COMERCIO: " + comercio, "TID: " + tid, S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
    }

    /**
     * Bancard
     */
    private void printPuntosBancard(Paint paint, PrintCanvas canvas) {
        String campo28 = DataAdicional.getField(28);
        if (campo28 != null) {
            if (!campo28.isEmpty()) {
                setTextPrint(setCenterText(campo28, S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
            }
        }
    }

    private void printStringLargue(String mensaje, int size, boolean isCenter, boolean bold, Paint paint, PrintCanvas canvas) {
        int tamañoMaximmoLinea;
        String[] palabras = mensaje.trim().split(" ");
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<String> lienas = new ArrayList<String>();
        switch (size) {
            case S_SMALL:
                tamañoMaximmoLinea = MAX_CHAR_SMALL;
                break;
            case S_MEDIUM:
                tamañoMaximmoLinea = MAX_CHAR_MEDIUM;
                break;
            case S_BIG:
                tamañoMaximmoLinea = MAX_CHAR_BIG;
                break;
            default:
                return;
        }
        for (String s : palabras) {
            if (stringBuilder.length() + s.length() < tamañoMaximmoLinea) {
                stringBuilder.append(s + " ");
            } else {
                lienas.add(stringBuilder.toString());
                stringBuilder = new StringBuilder();
                stringBuilder.append(s + " ");
            }
        }
        lienas.add(stringBuilder.toString());
        if (isCenter) {
            for (String linea : lienas) {
                setTextPrint(setCenterText(linea, size), paint, bold, canvas, size);
            }
        } else {
            for (String linea : lienas) {
                setTextPrint(linea, paint, bold, canvas, size);
            }
        }
    }

    private void print_DateAndTime(TransLogData datatrans, Paint paint, PrintCanvas canvas, boolean isTotalReport) {
        try {
            if (isTotalReport) {
                setTextPrint("FECHA   : " + PAYUtils.getDay() + "/" + PAYUtils.getMonth() + "/" + String.valueOf(PAYUtils.getYear()).substring(2), paint, BOLD_ON, canvas, S_MEDIUM);
                setTextPrint("HORA    : " + PAYUtils.StringPattern(PAYUtils.getLocalTime().trim(), "HHmmss", "HH:mm"), paint, BOLD_ON, canvas, S_MEDIUM);
            } else {
                if (datatrans.getLocalDate() != null && datatrans.getLocalTime() != null) {
                    setTextPrint(setTextColumn(formato2Fecha(datatrans.getLocalDate()),
                            formato2Hora(datatrans.getLocalTime()), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
                } else {
                    setTextPrint(setTextColumn(PAYUtils.getDay() + "/" + PAYUtils.getMonth() + "/" + String.valueOf(PAYUtils.getYear()).substring(2),
                            "H: " + formato2Hora(datatrans.getLocalTime()), S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
                }
            }
        } catch (Exception e) {
            Logger.exception(TAG, e);
        }
    }

    private void printLoteDataCARD(String Acquirer, String Pan, String expDate, String Lote, Paint paint, PrintCanvas canvas) {
        setTextPrint(setTextColumn(Acquirer, Pan, S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        expDate = "XX/XXXX";
        setTextPrint(setTextColumn("VENCE: " + expDate + "     " + checkNull(typeEntryPoint()), "Lote: " + Lote, S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
    }

    private void printTraceNoAuthNo(String TraceNo, String AuthNo, Paint paint, PrintCanvas canvas) {
        setTextPrint(setTextColumn("TRANSACCION # " + TraceNo, "AUTORIZACION # " + AuthNo, S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
    }

    private void printHeadDefered(String typeTrans, Paint paint, PrintCanvas canvas) {
        if (typeTrans.equals(Trans.Type.DEFERRED)) {
            if (dataTrans.getNumCuotasDeferred() != null) {
                String fee = dataTrans.getNumCuotasDeferred().substring(0, 2);
                String feeMonthGrace = dataTrans.getNumCuotasDeferred().substring(2, 4);
                setTextPrint(setCenterText("PLAZO MESES: " + fee, S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
            }
            println(paint, canvas);
        }
    }

    private void printTipoTrans(String typeTrans, Paint paint, PrintCanvas canvas) {
        println(paint, canvas);
        setTextPrint(setCenterText("- " + typeTrans + " -", S_MEDIUM), paint, BOLD_ON, canvas, S_MEDIUM);
        printHeadDefered(typeTrans, paint, canvas);
    }

    private void printDataCARDCHIP(Paint paint, PrintCanvas canvas) {
        if (isICC) {
            setTextPrint(setCenterText(checkNull(dataTrans.getTypeAccount()), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
            setTextPrint(setTextColumn(checkNull(dataTrans.getAID()), checkNull(dataTrans.getTC()) + checkNull(dataTrans.getTVR()) + checkNull(dataTrans.getTSI()), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        } else if (isNFC) {
            //TODO Validacion no concuerda. FA
            if (dataTrans.getAIDName().equals("") && dataTrans.getAID().trim().substring(0, 14).equals("A0000000031010")) {
                setTextPrint(setTextColumn("VISA CREDIT", "Ctls", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
            } else {
                setTextPrint(setTextColumn(dataTrans.getAIDName(), "Ctls", S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
            }
            setTextPrint(setTextColumn(checkNull(dataTrans.getAID().trim()), checkNull(dataTrans.getARQC()), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        }
        if (dataTrans.getField61() != null) {
            setTextPrint(setCenterText(checkNull(dataTrans.getField61()), S_SMALL), paint, BOLD_ON, canvas, S_SMALL);
        }
    }

    private String checkIfModoItem(int ucTipoMonto) {
        String tipoEntrada = null;
        switch (ucTipoMonto) {
            case SERVICEAMOUNT:
                tipoEntrada = "A";
                break;
            case TIPAMOUNT:
                tipoEntrada = "P";
                break;
            case IVAAMOUNT:
                tipoEntrada = "D";
                break;
        }
        return tipoEntrada;
    }

    private List<TransLogData> orderList(List<TransLogData> list1) {
        String getNameAcq = "";
        int cont = 0;
        List<TransLogData> auxList = new ArrayList<>();
        List<TransLogData> list = new ArrayList<>(list1);
        try {
            while (list.size() > 0) {
                int lenAuxlist = auxList.size();
                String acqActual = "";
                if (cont >= list.size()) {
                    auxList.add(list.get(0));
                    getNameAcq = list.get(0).getIssuerName().trim();
                    list.remove(0);
                    cont = 0;
                } else {
                    getNameAcq = list.get(cont).getIssuerName().trim();
                }
                if (list.size() > 0) {
                    if (lenAuxlist > 0) {
                        acqActual = auxList.get(lenAuxlist - 1).getIssuerName().trim();
                    } else {
                        auxList.add(list.get(0));
                        list.remove(0);
                        continue;
                    }
                    if (getNameAcq.equals(acqActual)) {
                        auxList.add(list.get(cont));
                        list.remove(cont);
                    } else {
                        cont++;
                    }
                }
            }
        } catch (Exception e) {
            Logger.exception(TAG, e);
        }
        return auxList;
    }

    private String formatoFecha(String fecha) {
        StringBuilder date = new StringBuilder();
        date.append(fecha.substring(4));
        date.append("/");
        date.append(PAYUtils.getMonth(fecha.substring(2, 4)));
        date.append("/");
        date.append(fecha, 0, 2);
        return date.toString();
    }

    private String formato2Fecha(String fecha) {//20190310
        StringBuilder date = new StringBuilder();
        date.append(fecha.substring(6));
        date.append("/");
        date.append(PAYUtils.getMonth(fecha.substring(4, 6)));
        date.append("/");
        date.append(fecha, 2, 4);
        return date.toString();
    }

    private String formatoHora(String hora) {
        StringBuilder date = new StringBuilder();
        date.append(hora, 0, 2);
        date.append(":");
        date.append(hora, 2, 4);
        date.append(":");
        date.append(hora.substring(4));
        return date.toString();
    }

    private String formatoHoraMin(String hora) {
        StringBuilder date = new StringBuilder();
        date.append(hora, 0, 2);
        date.append(":");
        date.append(hora, 2, 4);
        return date.toString();
    }

    private String formato2Hora(String hora) {
        StringBuilder date = new StringBuilder();
        date.append(hora, 0, 2);
        date.append(":");
        date.append(hora, 2, 4);
        date.append(":");
        date.append(hora, 4, 6);
        return date.toString();
    }

    private String typeEntryPoint() {
        String typeEntry = "";
        try {
            if (isFallback) {
                typeEntry = "FALLBACK";
            } else if (dataTrans.getEntryMode().equals(MODE_MAG + CapPinPOS())) {
                typeEntry = "BANDA";
            } else if (dataTrans.getEntryMode().equals(MODE_ICC + CapPinPOS())) {
                typeEntry = "CHIP";
            } else if (dataTrans.getEntryMode().equals(MODE_CTL + CapPinPOS())) {
                typeEntry = "CTL C";
            } else if (dataTrans.getEntryMode().equals(MODE_HANDLE + CapPinPOS())) {
                typeEntry = "MANUAL";
            }
        } catch (Exception e) {
            Logger.exception(TAG, e);
        }
        return typeEntry;
    }

    public String CapPinPOS() {
        String capPINPos = "1";
        return capPINPos;
    }


    /**
     * Maximo 4 lineas de 20 caracteres
     *
     * @param paint
     * @param canvas
     */
    private void printMessageSettle(Paint paint, PrintCanvas canvas) {
        //Cada campo maximo 40 caracteres
        String msg1 = null;// tconf.getMENSAJE_CIERRE1();
        String msg2 = null;//tconf.getMENSAJE_CIERRE2();

        boolean header = false;
        boolean footer = false;

        if (msg1 != null || msg2 != null) {
            if (!msg1.equals("")) {
                header = true;
                footer = true;
            } else if (!msg2.equals("")) {
                header = true;
                footer = true;
            } else {
                return;
            }
        }

        if (header) {
            setTextPrint("******************************************", paint, BOLD_OFF, canvas, S_SMALL);
            setTextPrint(setCenterText("MENSAJE DE MEDIANET:", S_BIG), paint, BOLD_ON, canvas, S_BIG);
            setTextPrint("******************************************", paint, BOLD_OFF, canvas, S_SMALL);
        }

        if (msg1 != null) {
            if (msg1.length() > 20) {
                setTextPrint(setCenterText(checkNull(msg1.substring(0, 20)), S_BIG), paint, BOLD_ON, canvas, S_BIG);
                setTextPrint(setCenterText(checkNull(msg1.substring(20)), S_BIG), paint, BOLD_ON, canvas, S_BIG);
            } else {
                if (!msg1.equals("")) {
                    setTextPrint(setCenterText(checkNull(msg1), S_BIG), paint, BOLD_ON, canvas, S_BIG);
                }
            }
        }

        if (msg2 != null) {
            if (msg2.length() > 20) {
                setTextPrint(setCenterText(checkNull(msg2.substring(0, 20)), S_BIG), paint, BOLD_ON, canvas, S_BIG);
                setTextPrint(setCenterText(checkNull(msg2.substring(20)), S_BIG), paint, BOLD_ON, canvas, S_BIG);
            } else {
                if (!msg2.equals("")) {
                    setTextPrint(setCenterText(checkNull(msg2), S_BIG), paint, BOLD_ON, canvas, S_BIG);
                }
            }
        }

        if (footer) {
            setTextPrint("******************************************", paint, BOLD_OFF, canvas, S_SMALL);
        }
    }

    public String getTraceNo() {
        return TraceNo;
    }

    public void setTraceNo(String traceNo) {
        if (traceNo != null) {
            TraceNo = traceNo;
        } else {
            TraceNo = " ";
        }
    }

    public void setCajas(boolean cajas) {
        isCajas = cajas;
    }
}
