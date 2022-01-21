package com.flota.transactions.inyeccion;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.flota.defines_bancard.DefinesBANCARD;
import com.flota.encrypt_data.TripleDES;
import com.flota.keys.DUKPT;
import com.flota.keys.InjectMasterKey;
import com.flota.actividades.MenusActivity;
import com.flota.transactions.RSA.RSATransport;
import com.newpos.libpay.Logger;
import com.newpos.libpay.helper.iso8583.ISO8583;
import com.newpos.libpay.presenter.TransPresenter;
import com.newpos.libpay.trans.Tcode;
import com.newpos.libpay.trans.Trans;
import com.newpos.libpay.trans.TransInputPara;
import com.newpos.libpay.utils.ISOUtil;
import com.newpos.libpay.utils.PAYUtils;
import com.pos.device.SDKException;

import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static com.flota.keys.InjectMasterKey.MASTERKEYIDX;
import static com.flota.keys.InjectMasterKey.TRACK2KEYIDX;
import static com.flota.keys.InjectMasterKey.threreIsKey;
import static com.flota.keys.InjectMasterKey.threreIsKeyWK;

public class InyeccionLlave extends Trans implements TransPresenter {


    int contador;
    boolean activarSetting = false;
    Context ctx;
    int maximo = 3;
    Activity activity;
    RSATransport rsaTransport = new RSATransport();
    private String tagClaseInyeccion = "InyeccionLlave.java";


    public InyeccionLlave(Context ctx, String transEname, TransInputPara p) {
        super(ctx, transEname);
        TransEName = transEname;
        this.ctx = ctx;
        para = p;
        if (para != null) {
            transUI = para.getTransUI();
        }
    }

    @Override
    public void start() {
        procesoTransaccionLLaves();
    }

    private void procesoTransaccionLLaves() {
        do {
            transUI.handling(timeout, Tcode.Status.inyeccion_llaves);
            if (armarTrama2Llaves() && implementacionLlaves()) {
                activarSetting = false;
                transUI.trannSuccess(timeout, Tcode.Status.inyeccion_llaves_exitoso);
                break;
            } else {
                activarSetting = true;
                contador++;
            }
        } while (contador == maximo);

        if (activarSetting) {
            intentSetting("No se pudo inyectar la llaves");
        }
    }

    private boolean implementacionLlaves() {
        return inyectarLLaves() && verificarLlaves(activity);
    }

    private boolean inyectarLLaves() {
        String campo61 = iso8583.getfield(61);

        Logger.flujo(tagClaseInyeccion, "inyectarLLaves:" + campo61);
        try {
            campo61 = rsaTransport.Decrypt(campo61);
            Logger.flujo(tagClaseInyeccion, "KeyDecryp: " + campo61);
        } catch (InvalidKeyException e) {
            Logger.debug(tagClaseInyeccion, "InvalidKeyException: ", e.getMessage());
            e.printStackTrace();
            Logger.exception(tagClaseInyeccion, e);
        } catch (IllegalBlockSizeException e) {
            Logger.debug(tagClaseInyeccion, "IllegalBlockSizeException: ", e.getMessage());
            e.printStackTrace();
            Logger.exception(tagClaseInyeccion, e);
        } catch (BadPaddingException | NoSuchPaddingException | NoSuchAlgorithmException e) {
            Logger.debug(tagClaseInyeccion, "BadPaddingException | NoSuchPaddingException | NoSuchAlgorithmException: ", e.getMessage());
            e.printStackTrace();
            Logger.exception(tagClaseInyeccion, e);
        }

        if (campo61 != null && !campo61.isEmpty()) {
            String[] llaves = campo61.split("\\|");

            if (llaves.length > 1) {

                String llaveIPEK = llaves[0];
                Logger.flujo(tagClaseInyeccion, "IPEK:" + llaveIPEK);
                String llaveTMK = llaves[1];
                Logger.flujo(tagClaseInyeccion, "TMK:" + llaveTMK);
                if (!llaveIPEK.isEmpty() && !llaveTMK.isEmpty()) {
                    try {
                        Logger.flujo(tagClaseInyeccion, "----------IPEK:" + llaveIPEK);
                        DUKPT.injectIPEK(llaveIPEK);

                        Logger.flujo(tagClaseInyeccion, "----------MASTER KEY: " + llaveTMK);

                        byte[] encrypted2 = TripleDES.xor(ISOUtil.hex2byte(llaveTMK), ISOUtil.hex2byte(llaveTMK));
                        byte[] data_encrypted = TripleDES.cryptBytes(encrypted2, 0, encrypted2);

                        if (InjectMasterKey.injectMk(ISOUtil.byte2hex(encrypted2)) == 0) {
                            InjectMasterKey.injectWorkingKey(ISOUtil.byte2hex(data_encrypted));
                            Logger.flujo(tagClaseInyeccion, "----------SALE DE INYECCION MASTER KEY: " + llaveTMK);
                            return true;
                        }

                    } catch (SDKException e) {
                        e.printStackTrace();
                        Logger.exception(tagClaseInyeccion, e);
                    }
                }

            }

        }
        return false;
    }


    private boolean verificarLlaves(Activity activity) {
        Logger.flujo(tagClaseInyeccion, "------------verificarLlaves");

        Logger.flujo(tagClaseInyeccion, "activity ++++++ " + activity);
        return DUKPT.checkIPEK() == 0 && (threreIsKey(MASTERKEYIDX, "Debe cargar Master Key", activity) &&
                threreIsKeyWK(TRACK2KEYIDX, "Debe cargar Work key", activity));
    }

    private boolean armarTrama2Llaves() {
        try {
            retVal = sendInyeccionLlaves();
            if (retVal == 0) {
                return true;
            }
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            String message=e.getClass().getSimpleName();
            Logger.exception(message, e);
        }
        return false;
    }

    private int sendInyeccionLlaves() throws InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
        setFixedDatas();
        setFieldInyeccionLlaves();

        return sendRcvdInit();
    }

    private void setFieldInyeccionLlaves() throws
            IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {

        ProcCode = "920000";

        iso8583.setTpdu("6000030000");

        iso8583.setHasMac(false);
        iso8583.clearData();

        LocalTime = PAYUtils.getLocalTime();
        LocalDate = PAYUtils.getLocalDate();

        if (MsgID != null) {
            iso8583.setField(0, MsgID);
        }
        if (ProcCode != null) {
            iso8583.setField(3, ProcCode);
        }
        if (TraceNo != null) {
            iso8583.setField(11, TraceNo);
        }
        if (TermID != null) {
            iso8583.setField(41, TermID);
        }

        Field60 = TripleDES.getKSNInicial2Hsm();
        if (Field60 != null) {
            iso8583.setField(60, Field60);
        }

        rsaTransport.genKeyPair(4096);
        Field61 = rsaTransport.getPublicKeyString();
        if (Field61 != null) {
            iso8583.setField(61, Field61);
        }
    }

    @Override
    public ISO8583 getISO8583() {
        return null;
    }

    private int sendRcvdInit() {
        int rta;
        Logger.flujo(tagClaseInyeccion, "sendRcvdInit : Pendiente");
        rta = retriesConnect(0, true, false);
        if (rta == Tcode.T_socket_err) {
            Logger.flujo(tagClaseInyeccion, "return T_socket_err");
            return rta;
        }
        transUI.handling(timeout, Tcode.Status.send_data_2_server);
        if (send() == -1) {
            Logger.flujo(tagClaseInyeccion, "Error enviando InyeccionLlave");
            return Tcode.T_send_err;
        }
        transUI.handling(timeout, Tcode.Status.send_over_2_recv);
        byte[] respData = recive();
        netWork.close();
        if (respData == null || respData.length <= 0) {
            Logger.flujo(tagClaseInyeccion, "T_receive_err despues de closer");
            return Tcode.T_receive_err;
        } else {
            int rtn = iso8583.unPacketISO8583(respData);
            if (rtn != 0) {
                return Tcode.T_receive_err;
            } else {
                return Tcode.T_success;
            }
        }
    }

    public void intentSetting(String mensaje) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, MenusActivity.class);
        intent.putExtra(DefinesBANCARD.MENSAJE_ERROR_INYECCION_LLAVES, mensaje);
        context.startActivity(intent);
    }
}
