package com.flota.transactions.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.SystemClock;

import com.android.desert.keyboard.InputInfo;
import com.android.newpos.libemv.PBOCUtil;
import com.flota.adaptadores.ModeloMensajeConfirmacion;
import com.flota.defines_bancard.DefinesBANCARD;
import com.flota.inicializacion.configuracioncomercio.TRANSACCIONES;
import com.flota.menus.menus;
import com.flota.screen.inputs.ScreenPin;
import com.newpos.libpay.Logger;
import com.newpos.libpay.device.pinpad.PinInfo;
import com.newpos.libpay.presenter.TransUI;
import com.newpos.libpay.trans.Tcode;
import com.newpos.libpay.trans.finace.FinanceTrans;
import com.newpos.libpay.trans.translog.TransLog;
import com.newpos.libpay.utils.ISOUtil;
import com.newpos.libpay.utils.PAYUtils;
import com.pos.device.icc.IccReader;
import com.pos.device.icc.SlotType;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static com.flota.actividades.StartAppBANCARD.listadoTransacciones;
import static com.flota.actividades.StartAppBANCARD.tablaCards;
import static com.flota.defines_bancard.DefinesBANCARD.INGRESO_VUELTO;
import static com.flota.menus.menus.NO_FALLBACK;
import static com.newpos.libpay.trans.Trans.ENTRY_MODE_FALLBACK;
import static com.newpos.libpay.trans.Trans.idLote;
import static java.lang.Thread.sleep;

public class CommonFunctionalities {

    static  String clase = "CommonFunctionalities.java";
    private static String Pan;
    private static String codOTT;
    private static String ExpDate;
    private static String Cvv2;
    private static boolean isPinExist;
    private static String PIN;

    public static String getKsn() {
        return ksn;
    }

    private static String ksn;
    private static String numReferencia;
    private static String proCode;
    public static StringBuilder Fld58Prompts;
    public static StringBuilder Fld58PromptsPrinter;
    public static StringBuilder Fld58PromptsAmountPrinter;
    public static boolean multicomercio = false;
    public static String idComercio;
    public static long sumarTotales;
    public static boolean isSumarTotales = false;

    public static String getPan() {
        return Pan;
    }

    public static String getCodOTT() {
        return codOTT;
    }

    public static String getExpDate() {
        return ExpDate;
    }

    public static String getCvv2() {
        return Cvv2;
    }

    public static boolean isIsPinExist() {
        return isPinExist;
    }

    public static String getPIN() {
        return PIN;
    }

    public static String getNumReferencia() {
        return numReferencia;
    }

    public static String getProCode() {
        return proCode;
    }

    public static String getFld58Prompts() {
        if (Fld58Prompts == null || Fld58Prompts.toString().equals("")) {
            return null;
        }
        return Fld58Prompts.toString();
    }

    public static String getFld58PromptsPrinter() {
        return Fld58PromptsPrinter.toString();
    }

    public static String getFld58PromptsAmountPrinter() {
        return Fld58PromptsAmountPrinter.toString();
    }

    public static boolean isMulticomercio() {
        return multicomercio;
    }

    public static String getIdComercio() {
        return idComercio;
    }

    public static long getSumarTotales() {
        return sumarTotales;
    }

    public static boolean isSumarTotales() {
        return isSumarTotales;
    }

    private static boolean getImg(String img) {
        boolean rta = false;
        switch (img.trim()) {
            case "0"://visa
            case "1"://Master
            case "2"://Amex
            case "3"://Diners
            case "4"://Visa Electron
            case "5"://Maestro
            case "6"://
                rta = true;
                break;

            default:
                break;
        }
        return rta;
    }

    public static void showCardImage(TransUI transUI) {

        String id_label = "";//rango.getIMAGEN_MOSTRAR();//rango.getNOMBRE_EMISOR();

        if (getImg(id_label)) {
            transUI.showCardImg(id_label);
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    Logger.exception(clase,  e);
                }
                break;
            }
        }
    }

    public static String[] tipoMoneda() {
        String moneda[] = new String[2];
        moneda[0] = "$";
        moneda[1] = FinanceTrans.LOCAL;
        return moneda;
    }

    public static boolean checkCierre(Context context) {
        DateFormat hourdateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date fechaActual = new Date();
        String fechaFormat = hourdateFormat.format(fechaActual);
        SharedPreferences prefs = context.getSharedPreferences("fecha-cierre", MODE_PRIVATE);
        String fechaCierre = prefs.getString("fechaSigCierre", null);
        if (fechaCierre != null) {
            Date dateCierre;
            Date dateActual;
            try {
                dateCierre = hourdateFormat.parse(fechaCierre);
                dateActual = hourdateFormat.parse(fechaFormat);
                int rta = dateActual.compareTo(dateCierre);
                if (rta >= 0 && (TransLog.getInstance(idLote).getSize() > 0)) {
                    return false;
                } else if (rta > 0 && (TransLog.getInstance(idLote).getSize() == 0)) {
                    saveDateSettle(context);
                }
            } catch (ParseException e) {
                e.printStackTrace();
                Logger.exception(clase, e);
            }
        } else {
            saveDateSettle(context);
        }
        return true;
    }

    /*public static int tipoEntrada(String tipoDatEnt) {
        int ret = 0;
        switch (tipoDatEnt) {
            case Prompt.NUMERICO:
            case Prompt.MONTO:
                ret = InputType.TYPE_CLASS_NUMBER;
                break;
            case Prompt.ALFA_NUMERICO:
            case Prompt.FECHA:
            case Prompt.CLAVE:
                ret = InputType.TYPE_CLASS_TEXT;
                break;
            default:
                ret = 1;
                break;
        }
        return ret;
    }*/

    public static int setPanManual(int timeout, String TransEName, TransUI transUI) {

        int ret = 1;

        while (true) {

            InputInfo inputInfo = transUI.showInputUser(timeout, TransEName, "DIGITE TARJETA", 0,19);

            if (inputInfo.isResultFlag()) {
                Pan = inputInfo.getResult();
                //Falta agregar funcionalidad para verificar el digito de chequeo de la tarjeta
                ret = 0;
                break;
            } else {
                ret = Tcode.T_user_cancel_input;
                transUI.showError(timeout, ret, false);
                break;
            }
        }

        return ret;
    }

    public static int setOTT_Token(int timeout, String TransEName, String title, String TipoPE, int min, int max,TransUI transUI) {

        int ret = 1;

        while (true) {

            InputInfo inputInfo = null;

            if (inputInfo.isResultFlag()) {
                codOTT = inputInfo.getResult();
                ret = 0;
                break;
            } else {
                ret = Tcode.T_user_cancel_input;
                transUI.showError(timeout, ret,false);
                break;
            }
        }

        return ret;
    }

    public static int setFechaExp(int timeout, String TransEName, TransUI transUI, boolean mostrarPantalla) {

        int ret = 1;
        String tmp;

        if (!mostrarPantalla) {
            return 0;
        }

        while (true) {
            InputInfo inputInfo = transUI.showInputUser(timeout, TransEName, "FECHA EXPIRACION MM/YY", 0,4);

            if (inputInfo.isResultFlag()) {
                tmp = inputInfo.getResult();
                ExpDate = "";
                try {
                    ExpDate += tmp.substring(2, 4);
                    ExpDate += tmp.substring(0, 2);
                    ret = 0;
                }catch (IndexOutOfBoundsException e){
                    ret = Tcode.T_err_invalid_len;
                    Logger.exception(clase,  e);
                    transUI.toasTrans(Tcode.T_err_invalid_len, true, true);
                    continue;
                }
                break;
            } else {
                ret = Tcode.T_user_cancel_input;
                transUI.showError(timeout, ret,false);
                break;
            }
        }

        return ret;
    }

    public static int setCVV2(int timeout, String TransEName, TransUI transUI, boolean mostrarPantalla) {

        int ret = 1;

        if (!mostrarPantalla) {
            return 0;
        }

        while (true) {
            InputInfo inputInfo = transUI.showInputUser(timeout, TransEName, "CODIGO SEGURIDAD CVV2", 0,3);

            if (inputInfo.isResultFlag()) {
                if (inputInfo.getResult().length()==3) {
                    Cvv2 = inputInfo.getResult();
                    ret = 0;
                }else{
                    ret = Tcode.T_err_invalid_len;
                    transUI.toasTrans(Tcode.T_err_invalid_len, true, true);
                    continue;
                }
                break;
            } else {
                ret = Tcode.T_user_cancel_input;
                transUI.showError(timeout, ret, false);
                break;
            }
        }

        return ret;
    }

    public static int ctlPIN(String pan, int timeout, long amount, TransUI transUI) {
        int ret = 1;
        PinInfo info = transUI.showScreenPin(ScreenPin.onlineDUKPT(timeout, String.valueOf(amount), pan));
        if (info.isResultFlag()) {
            if (info.isNoPin()) {
                isPinExist = false;
            } else {
                if (null == info.getPinblock()) {
                    isPinExist = false;
                } else {
                    isPinExist = true;
                }
                PIN = ISOUtil.hexString(Objects.requireNonNull(info.getPinblock()));
                ksn = info.getKsnString();
                ret = 0;
            }
            if (!isPinExist) {
                ret = Tcode.T_user_cancel_pin_err;
                //transUI.showError(timeout, ret);
                return ret;
            }
        } else {
            ret = Tcode.T_user_cancel_pin_err;
            //transUI.showError(timeout, ret);
            return ret;
        }
        return ret;
    }

    /*public static int setPrompt(int timeout, String TransEName, ArrayList<Prompt> prompt, TransUI transUI) {

        int ret = 1;
        Fld58Prompts = new StringBuilder();
        Fld58PromptsPrinter = null;
        Fld58PromptsAmountPrinter = null;
        Fld58PromptsPrinter = new StringBuilder();
        Fld58PromptsAmountPrinter = new StringBuilder();
        sumarTotales = 0;
        isSumarTotales = false;

        //No requiere ningun prompt
        if (prompt == null || prompt.isEmpty()) {
            Fld58Prompts = null;
            return 0;
        }

        Iterator<Prompt> itrPrompts = prompt.iterator();

        while (itrPrompts.hasNext()) {

            int len = 0;
            String datoPrompt = "";
            String datoPromptPrinter = "";
            StringBuilder data = new StringBuilder();
            Prompt promptActual = itrPrompts.next();

            //Transacciones Permitidas
            switch (TransEName) {
                case Trans.Type.VENTA:
                    if (!ISOUtil.stringToBoolean(promptActual.getVENTA())) {
                        ret =  0;
                        continue;
                    }
                    if (ISOUtil.stringToBoolean(tconf.getHABILITA_MONTO_FIJO())) {
                        if (!ISOUtil.stringToBoolean(promptActual.getVENTA_GASOLINERA())) {
                            ret =  0;
                            continue;
                        }
                    }
                    break;
                case Trans.Type.DEFERRED:
                    if (!ISOUtil.stringToBoolean(promptActual.getDIFERIDO())) {
                        ret =  0;
                        continue;
                    }
                    break;
                default:
                    Fld58Prompts = null;
                    return 0;
            }

            InputInfo inputPrompt = transUI.showInputPrompt(timeout, TransEName, "", promptActual);

            if (inputPrompt.isResultFlag()) {

                datoPromptPrinter = inputPrompt.getResult();
                datoPrompt = rellenarPrompt(inputPrompt.getResult(), promptActual);

                len = datoPrompt.length() + 2; //2 de la longitud del codigo

                data.append(ISOUtil.padleft(len + "", 4, '0'));
                data.append(ISOUtil.convertStringToHex(promptActual.getCODIGO_PROMPTS()));
                data.append(ISOUtil.convertStringToHex(datoPrompt));
                Fld58Prompts.append(data.toString());

                if (ISOUtil.stringToBoolean(promptActual.getSUMAR_TOTALES()) &&
                        promptActual.getTIPO_DATO().equals(Prompt.MONTO)){
                    sumarTotales += Long.valueOf(datoPromptPrinter);
                    isSumarTotales = true;

                    datoPromptPrinter = "$"+PAYUtils.getStrAmount(sumarTotales);
                }

                if (promptActual.getCODIGO_PROMPTS().equals("12")) {
                    multicomercio = true;
                    idComercio = datoPromptPrinter;
                }

                setFld58PromptsPrinter(promptActual, datoPromptPrinter);

                ret = 0;
            } else {
                ret = Tcode.T_user_cancel_input;
                transUI.showError(timeout, ret);
                break;
            }
        }

        return ret;
    }*/

    /*public static int setPrompt(int timeout, String TransEName, String title, ArrayList<Prompt> prompt, TransUI transUI) {

        int ret = 1;
        Fld58Prompts = new StringBuilder();
        Fld58PromptsPrinter = null;
        Fld58PromptsAmountPrinter = null;
        Fld58PromptsPrinter = new StringBuilder();
        Fld58PromptsAmountPrinter = new StringBuilder();
        sumarTotales = 0;
        isSumarTotales = false;

        //No requiere ningun prompt
        if (prompt == null || prompt.isEmpty()) {
            Fld58Prompts = null;
            return 0;
        }

        Iterator<Prompt> itrPrompts = prompt.iterator();

        while (itrPrompts.hasNext()) {

            int len = 0;
            String datoPrompt = "";
            String datoPromptPrinter = "";
            StringBuilder data = new StringBuilder();
            Prompt promptActual = itrPrompts.next();

            //Transacciones Permitidas
            switch (TransEName) {
                case Trans.Type.VENTA:
                    if (!ISOUtil.stringToBoolean(promptActual.getVENTA())) {
                        ret =  0;
                        continue;
                    }
                    if (ISOUtil.stringToBoolean(tconf.getHABILITA_MONTO_FIJO())) {
                        if (!ISOUtil.stringToBoolean(promptActual.getVENTA_GASOLINERA())) {
                            ret =  0;
                            continue;
                        }
                    }
                    break;
                case Trans.Type.DEFERRED:
                    if (!ISOUtil.stringToBoolean(promptActual.getDIFERIDO())) {
                        ret =  0;
                        continue;
                    }
                    break;
                default:
                    Fld58Prompts = null;
                    return 0;
            }

            InputInfo inputPrompt = transUI.showInputPrompt(timeout, title, "", promptActual);

            if (inputPrompt.isResultFlag()) {

                datoPromptPrinter = inputPrompt.getResult();
                datoPrompt = rellenarPrompt(inputPrompt.getResult(), promptActual);

                len = datoPrompt.length() + 2; //2 de la longitud del codigo

                data.append(ISOUtil.padleft(len + "", 4, '0'));
                data.append(ISOUtil.convertStringToHex(promptActual.getCODIGO_PROMPTS()));
                data.append(ISOUtil.convertStringToHex(datoPrompt));
                Fld58Prompts.append(data.toString());

                if (ISOUtil.stringToBoolean(promptActual.getSUMAR_TOTALES()) &&
                        promptActual.getTIPO_DATO().equals(Prompt.MONTO)){
                    sumarTotales += Long.valueOf(datoPromptPrinter);
                    isSumarTotales = true;

                    datoPromptPrinter = "$"+PAYUtils.getStrAmount(sumarTotales);
                }

                if (promptActual.getCODIGO_PROMPTS().equals("12")) {
                    multicomercio = true;
                    idComercio = datoPromptPrinter;
                }

                setFld58PromptsPrinter(promptActual, datoPromptPrinter);

                ret = 0;
            } else {
                ret = Tcode.T_user_cancel_input;
                transUI.showError(timeout, ret);
                break;
            }
        }

        return ret;
    }*/

    /*private static String rellenarPrompt(String datoPrompt, Prompt promptActual){
        String prompt;
        switch (promptActual.getTIPO_DATO()) {
            case Prompt.NUMERICO:
            case Prompt.MONTO:
                prompt = ISOUtil.padleft(datoPrompt + "", Integer.parseInt(promptActual.getLONGITUD_MAXIMA()), '0');
                break;
            case Prompt.ALFA_NUMERICO:
            case Prompt.FECHA:
            case Prompt.CLAVE:
                prompt = ISOUtil.padleft(datoPrompt + "", Integer.parseInt(promptActual.getLONGITUD_MAXIMA()), ' ');
                break;
            default:
                prompt = datoPrompt;
                break;
        }
        return prompt;
    }*/

    /*private static void setFld58PromptsPrinter(Prompt prompt, String value){

        if (prompt.getTIPO_DATO().equals(Prompt.MONTO)){
            Fld58PromptsAmountPrinter.append(prompt.getNOMBRE_PROMPTS());
            Fld58PromptsAmountPrinter.append(" : ");
            Fld58PromptsAmountPrinter.append(value);
            Fld58PromptsAmountPrinter.append("|");
        }else{
            Fld58PromptsPrinter.append(prompt.getNOMBRE_PROMPTS());
            Fld58PromptsPrinter.append(" : ");
            Fld58PromptsPrinter.append(value);
            Fld58PromptsPrinter.append("|");
        }
    }*/

    public static int last4card(int timeout, String TransEName, String pan, TransUI transUI, boolean mostrarPantalla) {

        int ret = 1;
        int intRest = 1;
        String intento = "";

        if (!mostrarPantalla)
            return 0;

        while (true) {
            InputInfo inputInfo = transUI.showInputUser(timeout, TransEName + intento, "ULTIMOS 4 DIGITOS", 0,4);

            if (inputInfo.isResultFlag()) {
                String last4Pan = pan.substring((pan.length() - 4), pan.length());
                if (last4Pan.equals(inputInfo.getResult())) {
                    ret = 0;
                    break;
                } else {
                    ret = Tcode.T_err_last_4;
                    transUI.toasTrans(Tcode.T_err_last_4, true, true);
                }
                intRest--;
                intento = "\nIntento Restante " + intRest;
                if (intRest == 0){
                    ret = Tcode.T_err_last_4;
                    break;
                }
            } else {
                ret = Tcode.T_user_cancel_input;
                transUI.showError(timeout, ret,false);
                break;
            }
        }

        return ret;
    }


    public static int setNumReferencia(int timeout, String TransEName, TransUI transUI) {

        int ret = 1;

        while (true) {
            InputInfo inputInfo = transUI.showInputUser(timeout, TransEName, "NO. REFERENCIA", 0,6);

            if (inputInfo.isResultFlag()) {
                numReferencia = inputInfo.getResult();
                ret = 0;
                break;
            } else {
                ret = Tcode.T_user_cancel_input;
                transUI.showError(timeout, ret, false);
                break;
            }
        }

        return ret;
    }

    /*public static int confirmAmount(int timeout, String transEname, TransUI transUI, long[] montos) {

        int ret = 1;
        StringBuilder msgLabel = new StringBuilder();
        StringBuilder msgAmnt = new StringBuilder();

        long IvaAmount = montos[0];
        long ServiceAmount = montos[1];
        long TipAmount = montos[2];
        long AmountXX = montos[3];
        long AmountBase0 = montos[4];
        long AmountCashOver = montos[5];
        long montoFijo = montos[6];

        String dataIva = "<br/>" + "<b>" + tconf.getLABEL_IMPUESTO() + "        :   $ </b>" + PAYUtils.getStrAmount(IvaAmount);
        String dataService = "<br/>" + "<b>"+tconf.getLABEL_SERVICIO()+"   :   $ </b>" + PAYUtils.getStrAmount(ServiceAmount);
        String dataTip = "<br/>" + "<b>"+tconf.getLABEL_PROPINA()+"    :   $ </b>" + PAYUtils.getStrAmount(TipAmount);
        String dataCashOver = "<br/>" + "<b>CASH OVER  :   $ </b>" + PAYUtils.getStrAmount(AmountCashOver);
        String dataMontoFijo = "<br/>" + "<b>MONTO FIJO  :   $ </b>" + PAYUtils.getStrAmount(montoFijo);

        if (!GetAmount.checkIVA())
            dataIva = "";
        if (!GetAmount.checkService())
            dataService = "";
        if (!GetAmount.checkTip())
            dataTip = "";
        dataCashOver = "";
        if (tconf.getVALOR_MONTO_FIJO()!=null) {
            if (!ISOUtil.stringToBoolean(tconf.getHABILITA_MONTO_FIJO()))
                dataMontoFijo = "";
        }else
            dataMontoFijo = "";

        msgLabel.append("<b>MONTO      :   $ </b>");
        msgLabel.append(PAYUtils.getStrAmount(AmountXX + AmountBase0) + dataIva + dataService + dataTip + dataCashOver + dataMontoFijo);
        msgLabel.append("<br/>");
        msgLabel.append("<br/>");
        msgAmnt.append("<b>TOTAL        :   $ </b>");
        msgAmnt.append(PAYUtils.getStrAmount(AmountXX + AmountBase0 + IvaAmount + ServiceAmount + TipAmount + AmountCashOver + montoFijo));

        InputInfo inputInfo = transUI.showConfirmAmount(timeout, "CONFIRMAR DATOS DE " + transEname, msgLabel.toString(), msgAmnt.toString(), true);

        if (inputInfo.isResultFlag()) {
            ret = 0;
        } else {
            ret = Tcode.T_user_cancel_operation;
            transUI.showError(timeout, ret);
        }

        return ret;
    }*/

    /*public static int confirmAmount(int timeout, String transEname, String title, TransUI transUI, long[] montos) {

        int ret = 1;
        StringBuilder msgLabel = new StringBuilder();
        StringBuilder msgAmnt = new StringBuilder();

        long IvaAmount = montos[0];
        long ServiceAmount = montos[1];
        long TipAmount = montos[2];
        long AmountXX = montos[3];
        long AmountBase0 = montos[4];
        long AmountCashOver = montos[5];
        long montoFijo = montos[6];

        String dataIva = "<br/>" + "<b>" + tconf.getLABEL_IMPUESTO() + "        :   $ </b>" + PAYUtils.getStrAmount(IvaAmount);
        String dataService = "<br/>" + "<b>"+tconf.getLABEL_SERVICIO()+"   :   $ </b>" + PAYUtils.getStrAmount(ServiceAmount);
        String dataTip = "<br/>" + "<b>"+tconf.getLABEL_PROPINA()+"    :   $ </b>" + PAYUtils.getStrAmount(TipAmount);
        String dataCashOver = "<br/>" + "<b>CASH OVER  :   $ </b>" + PAYUtils.getStrAmount(AmountCashOver);
        String dataMontoFijo = "<br/>" + "<b>MONTO FIJO  :   $ </b>" + PAYUtils.getStrAmount(montoFijo);

        if (!GetAmount.checkIVA())
            dataIva = "";
        if (!GetAmount.checkService())
            dataService = "";
        if (!GetAmount.checkTip())
            dataTip = "";
        if (!transEname.equals(Trans.Type.CASH_OVER))
            dataCashOver = "";
        if (tconf.getVALOR_MONTO_FIJO()!=null) {
            if (!ISOUtil.stringToBoolean(tconf.getHABILITA_MONTO_FIJO()))
                dataMontoFijo = "";
        }else
            dataMontoFijo = "";

        msgLabel.append("<b>MONTO      :   $ </b>");
        msgLabel.append(PAYUtils.getStrAmount(AmountXX + AmountBase0) + dataIva + dataService + dataTip + dataCashOver + dataMontoFijo);
        msgLabel.append("<br/>");
        msgLabel.append("<br/>");
        msgAmnt.append("<b>TOTAL        :   $ </b>");
        msgAmnt.append(PAYUtils.getStrAmount(AmountXX + AmountBase0 + IvaAmount + ServiceAmount + TipAmount + AmountCashOver + montoFijo));

        InputInfo inputInfo = transUI.showConfirmAmount(timeout, "CONFIRMAR DATOS DE " + title, msgLabel.toString(), msgAmnt.toString(), true);

        if (inputInfo.isResultFlag()) {
            ret = 0;
        } else {
            ret = Tcode.T_user_cancel_operation;
            transUI.showError(timeout, ret);
        }

        return ret;
    }*/

    public static int setTipoCuenta(int timeout, String fld3, TransUI transUI, boolean mostrarPantalla) {

        int ret = 1;

        if (!mostrarPantalla) {
            proCode = fld3;
            return 0;
        }

        InputInfo inputInfo = transUI.showTypeCoin(timeout, "TIPO DE CUENTA");
        if (inputInfo.isResultFlag()) {
            if (inputInfo.getResult().equals("1")) {
                proCode = fld3.replaceFirst("30", "10");
            } else if (inputInfo.getResult().equals("2")) {
                proCode = fld3.replace("30", "20");
            } else {
                proCode = fld3;
            }
            ret = 0;
        } else {
            ret = Tcode.T_user_cancel_input;
            transUI.showError(timeout, ret, false);
        }
        return ret;
    }

    public static boolean checkExpDate(String Track2, boolean checkExpDate) {
        String track2, dateCard, dateLocal;
        int yearCard, monCard, yearLocal, monLocal;

        //boolean checkExpDate = Chkoptn.stringToBoolean(Chkoptn.CheckExpDate(issuerRow));

        track2 = Track2.replace('D', '=');
        String tmp2 = track2.substring(track2.indexOf('=') + 1, track2.length());
        dateCard = tmp2.substring(0, 4);
        ExpDate = dateCard;

        if (!checkExpDate)
            return false;


        dateLocal = PAYUtils.getExpDate();
        monLocal = Integer.parseInt(dateLocal.substring(2));
        yearLocal = Integer.parseInt(dateLocal.substring(0, 2));
        yearCard = Integer.parseInt(dateCard.substring(0, 2));
        monCard = Integer.parseInt(dateCard.substring(2));

        if (yearCard > yearLocal) {
            return false;
        } else if (yearCard == yearLocal) {
            if (monCard > monLocal) {
                return false;
            } else return monCard != monLocal;
        } else {
            return true;
        }
    }

    public static void saveSettle(Context context) {
        if (TransLog.getInstance().getSize() == 1 && !getFirtsTrans(context)) {
            saveDateSettle(context);
            saveFirtsTrans(context, true);
        }
    }

    public static void saveFirtsTrans(Context context, boolean flag) {
        SharedPreferences.Editor editor = context.getSharedPreferences("firtsTrans", MODE_PRIVATE).edit();
        editor.putBoolean("firtsTrans", flag);
        editor.apply();
    }

    private static boolean getFirtsTrans(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("firtsTrans", MODE_PRIVATE);
        return prefs.getBoolean("firtsTrans", false);
    }

    public static void saveDateSettle(Context context) {
        DateFormat hourdateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date fechaActual = new Date();
        String diasCierre = "1";
        Date fechaCierre = sumarRestarDiasFecha(fechaActual, Integer.valueOf(diasCierre));
        String horasEchoTest = "12";
        horasEchoTest = horasEchoTest.replaceAll("[^\\d]", "");
        //Si el comercio no tienen configurada la hora de cierre tambien se estalliria
        String horaCierre = "0000";
        if (horaCierre == null || horaCierre.trim().equals("")) {
            horaCierre = "0000";
        } else {
            horaCierre = horaCierre.replaceAll("[^\\d]", "");
        }
        Date fechaEchoTest = sumarHorasFecha(fechaActual, Integer.parseInt(horasEchoTest));
        SharedPreferences.Editor editor = context.getSharedPreferences("fecha-cierre", MODE_PRIVATE).edit();
        editor.putString("fechaSigCierre", hourdateFormat.format(fechaCierre));
        editor.putString("fechaUltAct", hourdateFormat.format(fechaActual));
        editor.putString("fechaSigEchoTest", hourdateFormat.format(fechaEchoTest));
        //Si no esta en configurada en polaris, se guardaria null
        editor.putString("horaCierre", horaCierre);
        editor.putString("diasProximoCierre", diasCierre);
        editor.apply();
    }

    public static void setHoraCierre(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences("fecha-cierre", MODE_PRIVATE).edit();
        String horaCierre = "0000";
        if (horaCierre == null || horaCierre.trim().equals("")) {
            horaCierre = "0000";
        } else {
            horaCierre = horaCierre.replaceAll("[^\\d]", "");
        }
        editor.putString("horaCierre", horaCierre);
    }

    public static String getHoraCierre(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("fecha-cierre", MODE_PRIVATE);
        return prefs.getString("horaCierre", null);
    }

    public static String getDiasCierre(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("fecha-cierre", MODE_PRIVATE);
        return prefs.getString("diasProximoCierre", null);
    }

    private static Date sumarRestarDiasFecha(Date fecha, int dias) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha); // Configuramos la fecha que se recibe
        calendar.add(Calendar.DAY_OF_YEAR, dias);  // numero de días a añadir, o restar en caso de días<0
        return calendar.getTime(); // Devuelve el objeto Date con los nuevos días añadidos
    }

    private static Date sumarHorasFecha(Date fecha, int horas) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha); // Configuramos la fecha que se recibe
        calendar.add(Calendar.HOUR_OF_DAY, horas);  // numero de días a añadir, o restar en caso de días<0
        return calendar.getTime(); // Devuelve el objeto Date con los nuevos días añadidos
    }

    public static String getFechaUltimoCierre(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("fecha-cierre", MODE_PRIVATE);
        return prefs.getString("fechaUltimoCierre", null);
    }

    public static String getIdentificadorUltimoCierre(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("fecha-cierre", MODE_PRIVATE);
        return prefs.getString("identificadorCierre", null);
    }

    public static String getUltimoComercio(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("fecha-cierre", MODE_PRIVATE);
        return prefs.getString("Comercio", null);
    }

    public static String getFechaUltimaIncializacion(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(DefinesBANCARD.FECHA_INCIALIZACION, MODE_PRIVATE);
        return prefs.getString(DefinesBANCARD.FECHA_INICIALIZACION, null);
    }

    public static int fallback(int retVal) {
        int ret = retVal;
        if (ret > 1) {

            if (ret == 124) {//NO AID
                menus.contFallback = ENTRY_MODE_FALLBACK;
                ret = Tcode.T_err_fallback;
            } else {
                menus.contFallback = NO_FALLBACK;
            }
        }
        return ret;
    }

    /*public static boolean permitirTransGasolinera(String pan){
        if (ISOUtil.stringToBoolean(tconf.getHABILITA_MONTO_FIJO())){

            if (ISOUtil.stringToBoolean(tconf.getNO_PERMITIR_2_TRANS_MISMO_TARJ())){

                //String bin = pan.substring(0,6);
                if (TMConfig.getInstance().getTransMaxGasolinera().equals(pan)){
                    return false;
                }
                else
                    return true;
            }else
                return true;
        }
        return true;
    }*/

    /*public static void obtenerBin(String pan){
        if (ISOUtil.stringToBoolean(tconf.getHABILITA_MONTO_FIJO())){

            if (ISOUtil.stringToBoolean(tconf.getNO_PERMITIR_2_TRANS_MISMO_TARJ())){

                //String bin = pan.substring(0,6);
                TMConfig.getInstance().setTransMaxGasolinera(pan).save();
            }

        }
    }*/

    /*public static void limpiarPanTarjGasolinera(String pan){
        if (ISOUtil.stringToBoolean(tconf.getHABILITA_MONTO_FIJO())){
            if (ISOUtil.stringToBoolean(tconf.getNO_PERMITIR_2_TRANS_MISMO_TARJ())){
                TMConfig.getInstance().setTransMaxGasolinera(pan).save();
            }
        }
    }*/

    public static boolean validateCard(int timeout, TransUI transUI){
        boolean ret;
        final int TIMEOUT_REMOVE_CARD = timeout * 1000;

        IccReader iccReader0;
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);

        iccReader0 = IccReader.getInstance(SlotType.USER_CARD);
        long start = SystemClock.uptimeMillis() ;

        while (true){
            try {
                if (iccReader0.isCardPresent()) {
                    transUI.showMessage("Retire la tarjeta",true);
                    toneG.startTone(ToneGenerator.TONE_PROP_BEEP2, 2000);

                    try {
                        sleep(2000);
                    }catch (InterruptedException e) {
                        Logger.exception(clase,  e);
                        Logger.error("Exception" , e.toString());
                        Thread.currentThread().interrupt();
                    }

                    if (SystemClock.uptimeMillis() - start > TIMEOUT_REMOVE_CARD) {
                        toneG.stopTone();
                        ret = false;
                        break;
                    }
                }else {
                    ret = true;
                    break;
                }
            }catch (Exception e){
                Logger.exception(clase,  e);
                ret = true;
                break;
            }

        }
        return ret;
    }


    /* validaPedirVuelto : Verifica y solicita vuelto, si el vuleto (CASHBACK) se encuentra habilitado
                        segun configuracion de Comercio y rango de bines
       Retorna :
                     -1 : Operacion Cancelada por usario
                      0 : Usuario No desea vuelto
             Otro valor : Valor digitado por el usuario*/
    public static long validaPedirVuelto(Context context, int timeout, TransUI transUI, String pan, long montoVenta ) {

        long rtn = 0;

        /*
        - Para habilitar cashBack, debe estar habilitado en transacciones y cards
        - El monto como sugerencia de cashback, es tomado de la tabla transacciones
         */
        int montoCashback = validarCashBackPorTransaccion(DefinesBANCARD.POLARIS_NAME_TX_VUELTO);

        if (montoCashback > -1) {
            if (validarCashBackPorBines()) {
                int tempo = preguntarCahBack(timeout, transUI, montoVenta);
                switch (tempo) {
                    case 1:
                        rtn = pedirCashback(context, timeout, "Venta con vuelto", transUI, montoCashback);
                        break;
                    default:
                        rtn = tempo;
                }

            }
        }
        return rtn;
    }

    /**
     *
     * @param nombre
     * @return :
     *  -1 Cashback Deshabilitado
     *  > Mayor a -1 : CashBack habilitado
     */
    private static int validarCashBackPorTransaccion(String nombre) {
        boolean habilitado = false;
        int montoCashback = -1; // -1 Cashback Deshabilitado

        try{
            for (TRANSACCIONES transacciones : listadoTransacciones) {
                if (transacciones.getNombre().contains(nombre) && transacciones.getHabilitar()){
                    if(transacciones.getCashback()){
                        if(transacciones.getCashbackMonto().isEmpty()){
                            montoCashback = 0;
                        }else{// Monto valido
                            montoCashback = Integer.parseInt(transacciones.getCashbackMonto());
                        }
                    }
                    break;
                }
            }
        }catch (Exception e){
            Logger.exception(clase,  e);
            Logger.error("Fallo al obtener CASHBACK por TRANSACCION :" ,e);
        }


        return montoCashback;
    }

    private static boolean validarCashBackPorBines() {
        return tablaCards.isCashback();
    }

    /*
    return > true/false
     */
    public static int preguntarCahBack(int timeout, TransUI transUI, long MontoVenta) {
        String format = "";

        ModeloMensajeConfirmacion mensajeConfirmacion = new ModeloMensajeConfirmacion();
        mensajeConfirmacion.setBanner("VUELTO");

        format = String.valueOf(MontoVenta);
        if (!format.equals("")) {
            mensajeConfirmacion.setTitulo("Monto compra : Gs. " + PAYUtils.FormatPyg(format));
        }

        mensajeConfirmacion.setMensaje("¿Desea vuelto?");
        mensajeConfirmacion.setMsgBtnAceptar("Si");
        mensajeConfirmacion.setMsgBtnCancelar("No");

      /*  String strMonto = card.getCASHBACK_MONTO();
        if(!strMonto.equals("")){
            mensajeConfirmacion.setSubMensaje("Monto máximo : Gs. " + PAYUtils.FormatPygNoCentimos(strMonto));
        }*/

        InputInfo info = transUI.showMensajeConfirmacion(timeout, mensajeConfirmacion);
        if (info.isResultFlag()) {
            return 1;
        } else {
            if (info.getResult() != null) {
                if (info.getResult().equals("no")) {
                    return 0;
                }
            }
            return -1;

        }

    }

    /* Solicita el ingreso de CashBack - Vuelto
    -1 : Operacion Cancelada por usario
    Otro valor : Valor digitado por el usuario
     */
    public static long pedirCashback(Context context, int timeout, String TransEName, TransUI transUI, int montoCashBack) {
        long rtnMonto = -1;

        Logger.debug("Solicitud Cash Back - Vuelto");

        try{
            String strMonto;
            strMonto = String.valueOf(montoCashBack);
            InputInfo info = transUI.showIngresoDataNumerico(
                    timeout,
                    INGRESO_VUELTO, "Monto máximo : Gs. " + PAYUtils.FormatPygNoCentimos(strMonto),
                    "Vuelto: ", 7,
                    TransEName,
                    0);
            if (info.isResultFlag()) {
                rtnMonto = Long.parseLong(info.getResult()) * 100; // * 100, adiciona centavos 00
                Logger.debug("Vuelto ingresado" + rtnMonto);
            }
        }catch (Exception e){
            Logger.exception(clase,  e);
            Logger.error("FALLO Solicitud Cash Back - Vuelto :", e);
        }

        return rtnMonto;
    }

    /**
     * Metodo que dado una cadena de String con tags edita un nuevo mensaje
     *
     * @param originalData data original
     * @param tag          T
     * @param length       L
     * @param newValue     V
     * @return newData
     * <p>
     * Nota:
     * El tamaño del nuevo valor tiene que ser igual al tamaño del tag que se encuentra en la data original
     * <p>
     * En caso de que el tamaño exceda el tamaño real de newValue se retorna null
     */
    public static String setTag(byte[] originalData, int tag, int length, String newValue) {


        if (originalData == null)
            return null;

        int lengNewVal = newValue.length() / 2;

        if (length != lengNewVal)
            return null;

        byte[] oriDatB = new byte[originalData.length - 1];

        System.arraycopy(originalData, 1, oriDatB, 0, oriDatB.length);

        int offset = 0;
        int totalLen = oriDatB.length;

        while (offset < totalLen) {
            int tagLocal;
            if ((oriDatB[offset] & 31) == 31) {
                tagLocal = PBOCUtil.byte2int(oriDatB, offset, 2);
                offset += 2;
            } else {
                tagLocal = PBOCUtil.byte2int(new byte[]{oriDatB[offset++]});
            }

            int len = PBOCUtil.byte2int(new byte[]{oriDatB[offset++]});
            if ((len & -128) != 0) {
                int lenL = len & 3;
                len = PBOCUtil.byte2int(oriDatB, offset, lenL);
                offset += lenL;
            }

            byte[] temp;

            if (tag == tagLocal) {
                temp = ISOUtil.hex2byte(newValue);
                System.arraycopy(temp, 0, oriDatB, offset, len);
                Logger.debug("Se encontro el tag: 0x" + Integer.toHexString(tag));
            }
            offset += len;

        }

        byte[] resp = new byte[originalData.length];
        resp[0] = originalData[0];

        System.arraycopy(oriDatB, 0, resp, 1, resp.length - 1);

        return ISOUtil.byte2hex(resp);
    }
}
