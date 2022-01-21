package com.flota.transactions.anulacion;

import android.content.Context;

import com.android.desert.keyboard.InputInfo;
import com.flota.screen.inputs.ScreenEnterNumericalData;
import com.flota.screen.inputs.methods.FormatInput;
import com.flota.screen.inputs.methods.NumericalData;
import com.flota.transactions.DataAdicional.DataAdicional;
import com.newpos.libpay.Logger;
import com.newpos.libpay.helper.iso8583.ISO8583;
import com.newpos.libpay.presenter.TransPresenter;
import com.newpos.libpay.trans.Tcode;
import com.newpos.libpay.trans.TransInputPara;
import com.newpos.libpay.trans.finace.FinanceTrans;

public class Anulacion extends FinanceTrans implements TransPresenter {


    public static final String TAG = "ANULACION";

    public Anulacion(Context ctx, String transEname, TransInputPara p) {
        super(ctx, transEname);
        para = p;
        // Esta transaccion es reversable.
        isReversal = false;
        isProcPreTrans = true;
        isSaveLog = true;
        TransEName = transEname;
        transUI = para.getTransUI();
    }

    @Override
    public void start() {
        /*
         *  1. Pide ingresar la tarjeta - lee la tarjeta
         *  2. Arma el iso8583
         *  3. Envia el iso8583
         *  4. Valida el campo 39, RespCode. Si es igual a 00 o diferente.
         *  5.1 Si es 00, muestra la transaccion aprovada y da la opcion de mostrar el monto en pantalla.
         *  5.2 Si es diferente, valida si tiene que procesar algo, de lo contrario muestra el motivo del porque fue fallida.
         */

        int cardProcess = cardProcess(INMODE_MAG, "ANULACIÓN", "Deslice tarjeta supervidor", false);
        if (cardProcess == 1) {
            // Aqui lectura de la tarjeta exitosa
            prepareOnline();
        }

    }

    @Override
    public ISO8583 getISO8583() {
        return null;
    }

    private void prepareOnline() {

        ProcCode = "830000";

        final String supervisedPassword = getSupervisedPassword("Venta Flota", TAG);
        if (supervisedPassword != null) {
            DataAdicional.addOrUpdate(7, supervisedPassword);
        } else {
            return;
        }

        RRN = getNumberTicket();

        if (RRN != null) {
            DataAdicional.addOrUpdate(9, RRN);
        }
        setDatas(inputMode);

        if (inputMode == ENTRY_MODE_MAG) {
            retVal = OnlineTrans(emv);
        } else {
            retVal = OnlineTrans(null);
        }
        Logger.debug("SaleTrans>>OnlineTrans=" + retVal);
        if (retVal == 0) {
            transLog.removeLogDataForRnn(RRN);
            transUI.showResult(timeout, true, false, true, DataAdicional.getField(22));
        } else {
            transUI.showError(timeout, retVal, true);
        }
    }

    private String getNumberTicket() {
        InputInfo info = transUI.showScreenInput(new ScreenEnterNumericalData(timeout)
                .setTitle("Anulación")
                .setMessage("Ingresar el <b>N° de boleta</b> y presiona <b>OK</b> para continuar")
                .setInput(new NumericalData(FormatInput.CODE, 12)
                        .setHint("N° de boleta")
                        .setMsgErrorValidation("Ingrese el número de boleta."))
        );

        if (info.isResultFlag()) {
            String numberTicket = info.getResult();
            Logger.debug(TAG, "start: numberTicket=" + numberTicket);
            return numberTicket;
        } else {
            transUI.showFinish();
        }
        return null;
    }
}
