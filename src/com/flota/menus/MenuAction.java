package com.flota.menus;

import static com.flota.actividades.StartAppBANCARD.isInit;
import static com.flota.actividades.StartAppBANCARD.tablaComercios;
import static com.flota.defines_bancard.DefinesBANCARD.ITEM_REPORTE_DETALLADO;
import static com.flota.menus.menus.idAcquirer;
import static com.newpos.libpay.trans.Trans.idLote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.widget.Toast;

import com.flota.actividades.InfoActivity;
import com.flota.defines_bancard.DefinesBANCARD;
import com.flota.inicializacion.trans_init.Init;
import com.flota.printer.PrintParameter;
import com.flota.tools.PaperStatus;
import com.flota.tools_bacth.ToolsBatch;
import com.flota.transactions.DataAdicional.DataAdicional;
import com.flota.transactions.callbacks.waitPrintReport;
import com.flota.transactions.callbacks.waitSeatleReport;
import com.newpos.libpay.device.printer.PrintRes;
import com.pos.device.printer.Printer;
import com.wposs.flota.R;

import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.master.MasterControl;
import cn.desert.newpos.payui.transrecord.HistoryTrans;


public class MenuAction {
    //Claves para cuando no esta inicializado el POS
    private static final String TERMINAL_PWD = "123456";
    public static waitPrintReport callbackPrint;
    public static waitSeatleReport callBackSeatle;
    private final Context context;
    private final String tipoDeMenu;

    MenuAction(Context context, String tipoDeMenu) {
        this.context = context;
        this.tipoDeMenu = tipoDeMenu;
        MasterControl.setMcontext(context);
    }

    void selectAction() {
        Intent intent = new Intent();
        switch (tipoDeMenu) {
            case DefinesBANCARD.ITEM_REPORTE:
                idAcquirer = idLote;
                intent = new Intent(context, HistoryTrans.class);
                intent.putExtra(HistoryTrans.EVENTS, HistoryTrans.COMMON);
                context.startActivity(intent);
                break;
            case DefinesBANCARD.ITEM_LEALTAD:
                String campo63Exitoso = "0012383130323131333337343938004232324150524F42414441202020202020202020202020202020202020202020202020202020202020202000823239202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202000033838300082323850746F732E2067656E657261646F733A20322E35363220747374202020202020202020202020202053616C646F3A203839312E3634352074737420202020202020202020202020202020202020202020000438395643001639323131303731393230313633323434";
                DataAdicional.addOrUpdate(63, campo63Exitoso);
                break;
            case DefinesBANCARD.ITEM_COBRANZAS:
                String campo63Duplicada = "001238313032313133333734393800423232413031342D5452414E53414343494F4E204455504C4943414441202D204E4F204150524F4241444F001639323131303731393230313632393036";
                DataAdicional.addOrUpdate(63, campo63Duplicada);
                break;
            case DefinesBANCARD.ITEM_ECHO_TEST:
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, MasterControl.class);
                intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[20]);
                context.startActivity(intent);
                break;
            case DefinesBANCARD.ITEM_REIMPRESION:
                idAcquirer = idLote;
                intent = new Intent(context, HistoryTrans.class);
                intent.putExtra(HistoryTrans.EVENTS, HistoryTrans.LAST);
                context.startActivity(intent);
                break;
            case DefinesBANCARD.ITEM_VENTA_TARJETA:
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, MasterControl.class);
                intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[21]);
                intent.putExtra(MasterControl.TIPO_VENTA, DefinesBANCARD.ITEM_VENTA_TARJETA);
                context.startActivity(intent);
                break;
            case DefinesBANCARD.ITEM_SIN_TARJETA_PAGO_MOVIL:
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, MasterControl.class);
                intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[21]);
                intent.putExtra(MasterControl.TIPO_VENTA, DefinesBANCARD.ITEM_SIN_TARJETA_PAGO_MOVIL);
                context.startActivity(intent);
                break;
            case DefinesBANCARD.ITEM_BILLETERAS:
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, MasterControl.class);
                intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[21]);
                intent.putExtra(MasterControl.TIPO_VENTA, DefinesBANCARD.ITEM_BILLETERAS);
                context.startActivity(intent);
                break;
            case DefinesBANCARD.ITEM_SIN_TARJETA_CUENTA_ST:
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, MasterControl.class);
                intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[21]);
                intent.putExtra(MasterControl.TIPO_VENTA, DefinesBANCARD.ITEM_SIN_TARJETA_CUENTA_ST);
                context.startActivity(intent);
                break;
            case DefinesBANCARD.ITEM_VENTA_CON_VUELTO:
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, MasterControl.class);
                intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[21]);
                intent.putExtra(MasterControl.TIPO_VENTA, DefinesBANCARD.ITEM_VENTA_CON_VUELTO);
                context.startActivity(intent);
                break;
            case DefinesBANCARD.ITEM_VENTA_SIN_CONTACTO:
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, MasterControl.class);
                intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[21]);
                intent.putExtra(MasterControl.TIPO_VENTA, DefinesBANCARD.ITEM_VENTA_SIN_CONTACTO);
                context.startActivity(intent);
                break;
            case DefinesBANCARD.ITEM_CUOTAS_TARJETA:
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, MasterControl.class);
                intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[22]);
                intent.putExtra(MasterControl.TIPO_VENTA, DefinesBANCARD.ITEM_CUOTAS_TARJETA);
                context.startActivity(intent);
                break;
            case DefinesBANCARD.ITEM_CUOTAS_SERVICIOS:
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, MasterControl.class);
                intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[22]);
                intent.putExtra(MasterControl.TIPO_VENTA, DefinesBANCARD.ITEM_CUOTAS_SERVICIOS);
                context.startActivity(intent);
                break;
            case DefinesBANCARD.ITEM_CUOTAS_SIN_CONTACTO:
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, MasterControl.class);
                intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[22]);
                intent.putExtra(MasterControl.TIPO_VENTA, DefinesBANCARD.ITEM_CUOTAS_SIN_CONTACTO);
                context.startActivity(intent);
                break;
            case DefinesBANCARD.ITEM_C_SIN_TARJETA_PAGO_MOVIL:
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, MasterControl.class);
                intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[22]);
                intent.putExtra(MasterControl.TIPO_VENTA, DefinesBANCARD.ITEM_C_SIN_TARJETA_PAGO_MOVIL);
                context.startActivity(intent);
                break;
            case DefinesBANCARD.ITEM_C_SIN_TARJETA_ZIMPLE:
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, MasterControl.class);
                intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[22]);
                intent.putExtra(MasterControl.TIPO_VENTA, DefinesBANCARD.ITEM_C_SIN_TARJETA_ZIMPLE);
                context.startActivity(intent);
                break;
            case DefinesBANCARD.ITEM_C_SIN_TARJETA_CUENTA_ST:
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, MasterControl.class);
                intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[22]);
                intent.putExtra(MasterControl.TIPO_VENTA, DefinesBANCARD.ITEM_C_SIN_TARJETA_CUENTA_ST);
                context.startActivity(intent);
                break;
            case DefinesBANCARD.ITEM_VENTA_MINUTOS:
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, MasterControl.class);
                intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[21]);
                intent.putExtra(MasterControl.TIPO_VENTA, DefinesBANCARD.VENTA_SALDO);
                context.startActivity(intent);
                break;
            case DefinesBANCARD.ITEM_DEBITO:
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, MasterControl.class);
                intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[21]);
                intent.putExtra(MasterControl.TIPO_VENTA, DefinesBANCARD.ITEM_DEBITO);
                context.startActivity(intent);
                break;
            case DefinesBANCARD.ITEM_CREDITO:
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, MasterControl.class);
                intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[21]);
                intent.putExtra(MasterControl.TIPO_VENTA, DefinesBANCARD.ITEM_CREDITO);
                context.startActivity(intent);
                break;
            case DefinesBANCARD.CONFI_INFO:
                intent.setClass(context, InfoActivity.class);
                intent.putExtra("menu", tipoDeMenu);
                context.startActivity(intent);
                break;
            case DefinesBANCARD.ITEM_DIFERIDO:
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, MasterControl.class);
                intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[26]);
                context.startActivity(intent);
                break;
            case DefinesBANCARD.ITEM_CONSULTA_SALDO:
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, MasterControl.class);
                intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[31]);
                context.startActivity(intent);
                break;
            case DefinesBANCARD.ITEM_ANNULMENT:
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context, MasterControl.class);
                intent.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[23]);
                context.startActivity(intent);
                break;
            case DefinesBANCARD.ITEM_COMUNICACION:
            case DefinesBANCARD.ITEM_BORRAR_LOTE:
            case DefinesBANCARD.ITEM_BORRAR_REVERSO:
            case DefinesBANCARD.ITEM_PARAMETROS:
            case DefinesBANCARD.ITEM_DEFERED:
            case DefinesBANCARD.MENU_REPORTE_TESTPOS:
                if (isInit)
                    maintainPwd("CLAVE DE ACCESO", tablaComercios.getClaveComercio(), tipoDeMenu, 6);
                else
                    maintainPwd("CLAVE DE ACCESO", TERMINAL_PWD, tipoDeMenu, 6);

                break;
            case DefinesBANCARD.MENU_REPORTE_REIMPRESION:
                if (PaperStatus.getInstance().getRet() == Printer.PRINTER_STATUS_PAPER_LACK) {
                    UIUtils.toast((Activity) context, R.drawable.ic_redinfonet, DefinesBANCARD.MSG_PAPER, Toast.LENGTH_SHORT);
                } else {

                    idAcquirer = idLote;
                    if (ToolsBatch.statusTrans(idAcquirer)) {
                        intent = new Intent(context, HistoryTrans.class);
                        intent.putExtra(HistoryTrans.EVENTS, HistoryTrans.COMMON);
                        context.startActivity(intent);
                    } else {
                        UIUtils.toast((Activity) context, R.drawable.ic_redinfonet, DefinesBANCARD.LOTE_VACIO, Toast.LENGTH_LONG);
                        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                        toneG.startTone(ToneGenerator.TONE_CDMA_PIP, 500);
                    }
                }
                break;
            case ITEM_REPORTE_DETALLADO:
                if (PaperStatus.getInstance().getRet() == Printer.PRINTER_STATUS_PAPER_LACK) {
                    UIUtils.toast((Activity) context, R.drawable.ic_redinfonet, DefinesBANCARD.MSG_PAPER, Toast.LENGTH_SHORT);
                } else {
                    callbackPrint = null;
                    idAcquirer = idLote;
                    if (ToolsBatch.statusTrans(idAcquirer)) {
                        PrintParameter.setPrintTotals(true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(context, PrintParameter.class);
                        intent.putExtra("typeReport", DefinesBANCARD.ITEM_REPORTE_DETALLADO);
                        context.startActivity(intent);
                    } else {
                        UIUtils.toast((Activity) context, R.drawable.ic_redinfonet, DefinesBANCARD.LOTE_VACIO, Toast.LENGTH_SHORT);
                        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                        toneG.startTone(ToneGenerator.TONE_CDMA_PIP, 500);
                    }
                }
                break;
            case DefinesBANCARD.ITEM_POLARIS:
                idAcquirer = idLote;
                if (!ToolsBatch.statusTrans(idAcquirer)) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(context, Init.class);
                    context.startActivity(intent);
                } else {
                    UIUtils.toast((Activity) context, R.drawable.ic_redinfonet, DefinesBANCARD.MSG_SETTLE, Toast.LENGTH_SHORT);
                    ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                    toneG.startTone(ToneGenerator.TONE_CDMA_PIP, 500);
                }
                break;
            default:
                intent.setClass(context, menus.class);
                intent.putExtra(DefinesBANCARD.DATO_MENU, tipoDeMenu);
                context.startActivity(intent);
                break;
        }
    }

    private void maintainPwd(String title, final String pwd, final String type_trans, int lenEdit) {
        UIUtils.centerDialog(context, R.layout.setting_home_pass, R.id.setting_pass_layout);
    }
}
