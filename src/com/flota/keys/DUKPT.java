package com.flota.keys;

import com.flota.encrypt_data.TripleDES;
import com.flota.screen.inputs.ScreenPin;
import com.newpos.libpay.Logger;
import com.newpos.libpay.device.pinpad.PinInfo;
import com.newpos.libpay.device.pinpad.PinpadListener;
import com.newpos.libpay.trans.Tcode;
import com.newpos.libpay.utils.ISOUtil;
import com.pos.device.SDKException;
import com.pos.device.ped.KeySystem;
import com.pos.device.ped.KeyType;
import com.pos.device.ped.Ped;
import com.pos.device.ped.PedRetCode;
import com.pos.device.ped.PinBlockCallback;
import com.pos.device.ped.PinBlockFormat;

public class DUKPT {

    public static final int DUKPT_KEY_INDEX = 1;
    //dukpt key
    private static final byte[] dukptIpek = {0x77, 0x0A, 0x1A, (byte) 0xA7, 0x62, (byte) 0xC0, 0x23, (byte) 0xC3, 0x5F, (byte) 0xF8, 0x6E, 0x54, 0x4A, (byte) 0xAC, 0x37, 0x69};
    private static final byte[] ksn = {0x00, 0x00, 0x12, 0x20, 0x01, 0x66, 0x25, 0x00, 0x00, 0x00};

    static String clase = "DUKPT.java";
    private static DUKPT instance = null;
    private PinpadListener listener;
    private String pinCardNo;
    private String ksnStr;
    private int timeout;

    public static DUKPT getInstance() {
        if (instance == null)
            instance = new DUKPT();
        return instance;
    }

    public static String getCCKSN() {
        byte[] ccksn = Ped.getInstance().getDukptKsn(DUKPT_KEY_INDEX);
        Logger.debug("current ksn: " + ISOUtil.byte2hex(ccksn));

        if (ccksn != null) {
            return ISOUtil.byte2hex(ccksn);
        }
        return "";
    }

    public static int injectIPEK(String ipek) throws SDKException {
        if (Ped.getInstance().checkKey(KeySystem.DUKPT_DES, KeyType.KEY_TYPE_DUKPTK, DUKPT_KEY_INDEX, 0) == 0) {
            try {
                Ped.getInstance().deleteKey(KeySystem.DUKPT_DES, KeyType.KEY_TYPE_DUKPTK, DUKPT_KEY_INDEX);
            } catch (SDKException e) {
                Logger.exception(clase, e);
            }
        }

        int ret = Ped.getInstance().createDukptKey(DUKPT_KEY_INDEX, ISOUtil.str2bcd(ipek, false), ISOUtil.str2bcd(TripleDES.getKSNInicial(), false));
        if (ret != 0) {
            Logger.debug("ped create dukpk key failed:" + ret);
            Logger.flujo(clase, "ped create dukpk key failed: " + ret);
        } else {
            Logger.debug("ped create dukpt key success\n" + "ipek:" + ISOUtil.byte2hex(dukptIpek)
                    + "\nksn:" + ISOUtil.byte2hex(ksn));
            Logger.flujo(clase, "ped create dukpt key success ");
        }

        return ret;
    }

    public static int checkIPEK() {
        int ret;

        Logger.flujo(clase, "checkIPEK:");

        ret = Ped.getInstance().checkKey(KeySystem.DUKPT_DES, KeyType.KEY_TYPE_DUKPTK, DUKPT_KEY_INDEX, 0);

        Logger.flujo(clase, "RET : " + ret);
        if (ret == 0) {
            Logger.debug("ped dukpk key already exist" + ret);
        } else {
            Logger.debug("ped dukpk key not exist" + ret);
        }
        return ret;
    }

    public void getPinDUKPT(ScreenPin screen, PinpadListener l) {
        this.listener = l;
        this.timeout = screen.getTimeout();
        this.pinCardNo = screen.getPinCardNo();

        final PinInfo info = new PinInfo();
        if (null == l || (pinCardNo == null || pinCardNo.equals(""))) {
            info.setResultFlag(false);
            info.setErrno(Tcode.T_invoke_para_err);
            if (listener != null)
                listener.callback(info);
        } else {
            Logger.debug("Pin pad Manager >> getPin >>");
            pinCardNo = pinCardNo.substring(pinCardNo.length() - 13, pinCardNo.length() - 1);
            pinCardNo = ISOUtil.padleft(pinCardNo, pinCardNo.length() + 4, '0');
            ksnStr = getCCKSN();

            final Ped ped = Ped.getInstance();
            ped.setPinPadView(screen.getPadView());
            new Thread() {
                @Override
                public void run() {
                    ped.getPinBlock(KeySystem.DUKPT_DES,
                            DUKPT_KEY_INDEX,
                            PinBlockFormat.PIN_BLOCK_FORMAT_0,
                            "4,5,6,7,8,9,10,11,12",
                            pinCardNo,
                            new PinBlockCallback() {
                                @Override
                                public void onPinBlock(int i, byte[] bytes) {
                                    switch (i) {
                                        case PedRetCode.NO_PIN:
                                            info.setResultFlag(true);
                                            info.setNoPin(true);
                                            break;
                                        case PedRetCode.TIMEOUT:
                                            info.setResultFlag(false);
                                            info.setErrno(Tcode.T_wait_timeout);
                                            break;
                                        case PedRetCode.ENTER_CANCEL:
                                            info.setResultFlag(false);
                                            info.setErrno(Tcode.T_user_cancel_pin_err);
                                            break;
                                        case 0:
                                            info.setResultFlag(true);
                                            info.setPinblock(bytes);
                                            info.setKsnString(ksnStr); // increment KSN
                                            break;
                                        default:
                                            info.setResultFlag(false);
                                            info.setErrno(i);
                                            break;
                                    }
                                    listener.callback(info);
                                }
                            });
                }
            }.start();
        }
    }
}
