package com.flota.transactions.consulta;

import android.content.Context;
import android.util.Log;

import com.flota.screen.result.ScreenAccountBalance;
import com.flota.transactions.DataAdicional.DataAdicional;
import com.newpos.libpay.Logger;
import com.newpos.libpay.helper.iso8583.ISO8583;
import com.newpos.libpay.presenter.TransPresenter;
import com.newpos.libpay.trans.Tcode;
import com.newpos.libpay.trans.TransInputPara;
import com.newpos.libpay.trans.finace.FinanceTrans;
import com.newpos.libpay.utils.ISOUtil;

public class ConsultaDeSaldo extends FinanceTrans implements TransPresenter {

    public static final String TAG = "ConsultaDeSaldo";

    public ConsultaDeSaldo(Context ctx, String transEname, TransInputPara p) {
        super(ctx, transEname);
        para = p;
        isReversal = false;
        isProcPreTrans = true;
        isSaveLog = false;
        TransEName = transEname;
        para.setNeedAmount(false);
        para.setNeedPrint(false);
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

        int cardProcess = cardProcess(INMODE_MAG, "CONSULTA SALDO",
                "Deslice tarjeta cliente", false);
        Log.d(TAG, "start: cardProcess=" + cardProcess);
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

        final String playeroPassword = getPlayeroPassword("Consultar saldo", TAG);
        if (playeroPassword != null) {
            DataAdicional.addOrUpdate(1, playeroPassword);
        }else{
            return;
        }

        transUI.handling(timeout, Tcode.Status.connecting_center, TransEName);
        setDatas(inputMode);
        if (inputMode == ENTRY_MODE_MAG) {
            retVal = OnlineTrans(emv);
        } else {
            retVal = OnlineTrans(null);
        }
        Logger.debug(TAG + " prepareOnline: retVal=" + retVal);
        clearPan();
        if (retVal == 0) {
            if (iso8583.getfield(54) != null) {
                long accountBalance = Long.parseLong(ISOUtil.hex2AsciiStr(iso8583.getfield(54)));
                transUI.showScreenResult(new ScreenAccountBalance(15000)
                        .setAccountBalance(accountBalance)
                );
            } else {
                // TO DO: Si el campo 54 es nulo, ¿Qué hacer?
            }
        } else {
            transUI.showError(timeout, retVal, true);
        }
    }
}
