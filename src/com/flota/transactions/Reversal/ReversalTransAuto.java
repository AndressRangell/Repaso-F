package com.flota.transactions.Reversal;

import android.content.Context;

import com.newpos.libpay.Logger;
import com.newpos.libpay.helper.iso8583.ISO8583;
import com.newpos.libpay.presenter.TransPresenter;
import com.newpos.libpay.presenter.TransUI;
import com.newpos.libpay.trans.Tcode;
import com.newpos.libpay.trans.TransInputPara;
import com.newpos.libpay.trans.manager.RevesalTrans;
import com.newpos.libpay.trans.translog.TransLog;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class ReversalTransAuto extends RevesalTrans implements TransPresenter {

    TransUI transUI;

    public ReversalTransAuto(Context ctx, String transEname, int timeOut, TransInputPara p) {
        super(ctx, transEname, timeOut);
        para = p;
        if (para != null) {
            transUI = para.getTransUI();
        }
    }

    @Override
    public void start() throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, NoSuchProviderException, InvalidKeySpecException {
        int respuestaReverso = analizarReversoAntesDe(transUI, "ReversalTransAuto-start");
        Logger.reversal("ReversalTransAuto", "respuestaReverso " + respuestaReverso, "start");
        if (respuestaReverso != Tcode.T_success) {
            if (respuestaReverso != Tcode.T_NO_REVERSE)
                transUI.showError(timeout, "REVERSA PENDIENTE", Tcode.T_envio_fallido_reverso_fail, false, false);
        } else {
            TransLog.clearReveral();
            transUI.showError(timeout, "REVERSA APROBADA", Tcode.T_envio_fallido_reverso_ok, false, true);
        }
    }

    @Override
    public ISO8583 getISO8583() {
        return null;
    }
}
