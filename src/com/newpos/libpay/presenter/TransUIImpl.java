package com.newpos.libpay.presenter;

import static com.flota.defines_bancard.DefinesBANCARD.COD_BILLETERAS;
import static com.flota.defines_bancard.DefinesBANCARD.COD_CUENTA_SIN_TARJETA;
import static com.flota.defines_bancard.DefinesBANCARD.COD_VENTA_CUOTA;
import static com.flota.defines_bancard.DefinesBANCARD.COD_VENTA_DEBITO_CREDITO;
import static com.flota.defines_bancard.DefinesBANCARD.COD_VENTA_SALDO;
import static com.flota.screen.inputs.ScreenCard.TypeScreen.CARD_PROCESSING;

import android.app.Activity;
import android.media.ToneGenerator;
import android.util.Log;

import com.android.desert.keyboard.InputInfo;
import com.android.desert.keyboard.InputManager;
import com.flota.adaptadores.ModeloMensajeConfirmacion;
import com.flota.adapters.model.ButtonModel;
import com.flota.defines_bancard.DefinesBANCARD;
import com.flota.keys.DUKPT;
import com.flota.logscierres.LogsCierresModelo;
import com.flota.screen.inputs.ScreenCard;
import com.flota.screen.inputs.ScreenEnterNumericalData;
import com.flota.screen.inputs.ScreenPin;
import com.flota.screen.inputs.ScreenSelectFromTwoOptions;
import com.flota.screen.inputs.ScreenSelectProduct;
import com.flota.screen.inputs.base.ScreenInput;
import com.flota.screen.result.ScreenAccountBalance;
import com.flota.screen.result.base.ScreenResult;
import com.flota.transactions.Billeteras.Billeteras;
import com.newpos.libpay.Logger;
import com.newpos.libpay.PaySdk;
import com.newpos.libpay.PaySdkException;
import com.newpos.libpay.device.card.CardInfo;
import com.newpos.libpay.device.card.CardListener;
import com.newpos.libpay.device.card.CardManager;
import com.newpos.libpay.device.pinpad.PinInfo;
import com.newpos.libpay.device.pinpad.PinpadListener;
import com.newpos.libpay.device.pinpad.PinpadManager;
import com.newpos.libpay.device.user.OnUserResultListener;
import com.newpos.libpay.global.TMConstants;
import com.newpos.libpay.trans.Tcode;
import com.newpos.libpay.trans.translog.TransLogData;
import com.newpos.libpay.utils.PAYUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import cn.desert.newpos.payui.UIUtils;

/**
 * Created by zhouqiang on 2017/4/25.
 *
 * @author zhouqiang
 * 交易UI接口实现类
 * MVP架构中的P层 ，处理复杂的逻辑及数据
 */

public class TransUIImpl implements TransUI {

    public static final String SCREEN_TAG = "Screen";

    String clase = "TransUllmpl.java";
    private TransView transView;
    private Activity mActivity;
    private CardInfo cInfo;
    private CardManager cardManager = null;
    private CountDownLatch mLatch;
    private int mRet = 0;
    private InputManager.Style payStyle;
    private final OnUserResultListener listener = new OnUserResultListener() {
        @Override
        public void confirm(InputManager.Style style) {
            Log.d(SCREEN_TAG, "listener: confirm");
            try {
                mRet = 0;
                payStyle = style;
                mLatch.countDown();
            } catch (NullPointerException e) {
                Logger.exception(clase, e);
                Logger.error("Exception ", e.toString());
            }
        }

        @Override
        public void cancel() {
            Log.d(SCREEN_TAG, "listener: cancel");
            try {
                mRet = 1;
                mLatch.countDown();
            } catch (NullPointerException e) {
                Logger.error("Exception ", e.toString());
                Logger.exception(clase, e);
            }
        }

        @Override
        public void confirm(int applistselect) {
            Log.d(SCREEN_TAG, "listener: confirm 2");
            try {
                mRet = applistselect;
                mLatch.countDown();
            } catch (NullPointerException e) {
                Logger.error("Exception ", e.toString());
                Logger.exception(clase, e);
            }
        }
    };

    public TransUIImpl(Activity activity, TransView tv) {
        this.transView = tv;
        this.mActivity = activity;
    }

    public static String getErrInfo(String status) {
        try {
            String[] errs = Locale.getDefault().getLanguage().equals("zh") ?
                    PAYUtils.getProps(PaySdk.getInstance().getContext(), TMConstants.ERRNO, status) :
                    PAYUtils.getProps(PaySdk.getInstance().getContext(), TMConstants.ERRNO_EN, status);
            if (errs != null) {
                return errs[0];
            }
        } catch (PaySdkException pse) {
            Logger.exception("TransUllmpl.java", pse);
            Logger.error("Exception" + pse.toString());
            Thread.currentThread().interrupt();
        }
        if (Locale.getDefault().getLanguage().equals("zh")) {
            return "未知错误";
        } else {
            return "Código de Error Desconocido";
        }
    }

    @Override
    public CardInfo showScreenCard(ScreenCard screen) {
        switch (screen.getTypeScreen()) {
            case CARD_PROCESSING:
            case CARD_FALLBACK:
                transView.screenCardProcessing(screen, listener);
                break;
            case CARD_HANDLING:
                transView.showMsgInfo(screen.getTimeout(), screen.getMessage(), false, false);
                break;
        }

        final CardManager manager = CardManager.getInstance(screen.getMode());
        final CardInfo info = new CardInfo();
        manager.getCard(screen.getTimeout(), new CardListener() {
            @Override
            public void callback(CardInfo cardInfo) {
                info.setResultFalg(cardInfo.isResultFalg());
                info.setNfcType(cardInfo.getNfcType());
                info.setCardType(cardInfo.getCardType());
                info.setTrackNo(cardInfo.getTrackNo());
                info.setCardAtr(cardInfo.getCardAtr());
                info.setErrno(cardInfo.getErrno());
                mLatch.countDown();
            }
        });

        await();

        // [!] Necessary?
        if (screen.getTypeScreen() == CARD_PROCESSING)
            switch (transView.getInput(InputManager.Mode.REFERENCE)) {
                case DefinesBANCARD.OPC_VENTA_CUOTA:
                    info.setResultFalg(true);
                    info.setCardType(COD_VENTA_CUOTA);
                    mLatch.countDown();
                    break;
                case DefinesBANCARD.OPC_VENTA_SALDO:
                    info.setResultFalg(true);
                    info.setCardType(COD_VENTA_SALDO);
                    mLatch.countDown();
                    break;
                case DefinesBANCARD.ITEM_BILLETERAS:
                    info.setResultFalg(true);
                    info.setCardType(COD_BILLETERAS);
                    mLatch.countDown();
                    break;
                case DefinesBANCARD.OPC_VENTA_DC:
                    mLatch.countDown();
                    info.setResultFalg(true);
                    info.setCardType(COD_VENTA_DEBITO_CREDITO);
                    mLatch.countDown();
                    break;
                case DefinesBANCARD.OPC_VENTA_ST:
                    info.setResultFalg(true);
                    info.setCardType(COD_CUENTA_SIN_TARJETA);
                    mLatch.countDown();
                    break;
                default:
                    break;
            }

        return info;
    }

    @Override
    public PinInfo showScreenPin(ScreenPin screen) {
        final PinInfo infoResp = new PinInfo();
        switch (screen.getType()) {
            case OFFLINE:
                PinpadManager.getInstance().getOfflinePin(screen, new PinpadListener() {
                    @Override
                    public void callback(PinInfo info) {
                        infoResp.setResultFlag(info.isResultFlag());
                        infoResp.setErrno(info.getErrno());
                        infoResp.setNoPin(info.isNoPin());
                        infoResp.setPinblock(info.getPinblock());
                        mLatch.countDown();
                    }
                });
                break;
            case ONLINE:
                PinpadManager.getInstance().getPin(screen, new PinpadListener() {
                    @Override
                    public void callback(PinInfo info) {
                        infoResp.setResultFlag(info.isResultFlag());
                        infoResp.setErrno(info.getErrno());
                        infoResp.setNoPin(info.isNoPin());
                        infoResp.setPinblock(info.getPinblock());
                        mLatch.countDown();
                    }
                });
                break;
            case ONLINE_DUKPT:
                DUKPT.getInstance().getPinDUKPT(screen, new PinpadListener() {
                    @Override
                    public void callback(PinInfo info) {
                        infoResp.setResultFlag(info.isResultFlag());
                        infoResp.setErrno(info.getErrno());
                        infoResp.setNoPin(info.isNoPin());
                        infoResp.setPinblock(info.getPinblock());
                        infoResp.setKsnString(info.getKsnString());
                        mLatch.countDown();
                    }
                });
                break;
            default:
                return null;
        }

        await();

        transView.showMsgInfo(screen.getTimeout(),
                getStatusInfo(String.valueOf(Tcode.Status.handling)), false, false);
        return infoResp;
    }

    @Override
    public InputInfo showScreenInput(ScreenInput screen) {
        switch (screen.getTypeScreen()) {
            case ENTER_NUMERICAL_DATA:
                if (screen instanceof ScreenEnterNumericalData)
                    transView.screenEnterNumericalData((ScreenEnterNumericalData) screen, listener);
                break;
            case SELECT_PRODUCT:
                if (screen instanceof ScreenSelectProduct)
                    transView.screenSelectProduct((ScreenSelectProduct) screen, listener);
                break;
            case SELECT_FROM_TWO_OPTIONS:
                if (screen instanceof ScreenSelectFromTwoOptions)
                    transView.screenSelectFromTwoOptions((ScreenSelectFromTwoOptions) screen, listener);
                break;
            default:
                break;
        }

        await();

        InputInfo info = new InputInfo();
        if (mRet == 1) {
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        } else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public void showScreenResult(ScreenResult screen) {
        switch (screen.getTypeScreen()) {
            case ACCOUNT_BALANCE:
                if (screen instanceof ScreenAccountBalance)
                    transView.screenAccountBalance((ScreenAccountBalance) screen);
                break;
            case TRANS:
            default:
                break;
        }

        await();
    }

    private void await() {
        this.mLatch = new CountDownLatch(1);
        try {
            mLatch.await();
        } catch (InterruptedException e) {
            Logger.exception(clase, e);
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public InputInfo getOutsideInput(int timeout, InputManager.Mode type, String title, String trx, long amount) {
        String tipoIngreso;
        int longitudMaxima = 0;
        if (type.equals(InputManager.Mode.AMOUNT)) {
            tipoIngreso = DefinesBANCARD.INGRESO_MONTO;
            longitudMaxima = 10;
        } else {
            tipoIngreso = DefinesBANCARD.INGRESO_TELEFONO;
        }
        transView.showIngresoDataNumericoView(timeout, tipoIngreso, "", title, longitudMaxima, trx, amount, listener);
        //transView.showInputView(timeout, type, listener, title);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            Logger.exception(clase, e);
            Thread.currentThread().interrupt();
            Logger.error("Exception" + e.toString());
        }
        InputInfo info = new InputInfo();
        if (mRet == 1) {
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        } else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(type));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public int showCardConfirm(int timeout, String cn) {
        this.mLatch = new CountDownLatch(1);
        transView.showCardNo(timeout, cn, listener);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            Logger.exception(clase, e);
            Thread.currentThread().interrupt();
            Logger.error("Exception" + e.toString());
        }
        return mRet;
    }

    @Override
    public InputInfo showMessageInfo(String title, String msg, String btnCancel, String btnConfirm, int timeout) {
        this.mLatch = new CountDownLatch(1);
        transView.showMessageInfo(title, msg, btnCancel, btnConfirm, timeout, listener);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            Logger.exception(clase, e);
            Thread.currentThread().interrupt();
            Logger.error("Exception" + e.toString());
        }
        InputInfo info = new InputInfo();
        if (mRet == 1) {
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        } else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.REFERENCE));
        }
        return info;
    }

    @Override
    public InputInfo showMessageImpresion(String title, String msg, String btnCancel, String btnConfirm, int timeout) {
        this.mLatch = new CountDownLatch(1);
        transView.showMessageImpresion(title, msg, btnCancel, btnConfirm, timeout, listener);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            Logger.exception(clase, e);
            Thread.currentThread().interrupt();
            Logger.error("Exception" + e.toString());
        }
        InputInfo info = new InputInfo();
        if (mRet == 1) {
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        } else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.REFERENCE));
        }
        return info;
    }

    @Override
    public int showCardApplist(int timeout, String[] list) {
        this.mLatch = new CountDownLatch(1);
        transView.showCardAppListView(timeout, list, listener);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            Logger.exception(clase, e);
            Thread.currentThread().interrupt();
            Logger.error("Exception" + e.toString());
        }
        return mRet;
    }

    @Override
    public int showMultiLangs(int timeout, String[] langs) {
        this.mLatch = new CountDownLatch(1);
        transView.showMultiLangView(timeout, langs, listener);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            Logger.exception(clase, e);
            Thread.currentThread().interrupt();
            Logger.error("Exception" + e.toString());
        }
        return mRet;
    }

    @Override
    public void handling(int timeout, int status) {
        boolean withClose = (status == Tcode.Status.process_trans);
        Logger.flujo("Showing Handling: " + status + " WhitClose: " + withClose);
        transView.showMsgInfo(timeout, getStatusInfo(String.valueOf(status)), false, withClose);
    }

    @Override
    public void handling(int timeout, int status, String title) {
        Logger.flujo("Showing Handling: " + status + " Title: " + title);
        transView.showMsgInfo(timeout, getStatusInfo(String.valueOf(status)), title, false);
    }

    @Override
    public void handling(int timeout, String mensaje, String title) {
        Logger.flujo("Showing Handling: " + mensaje + " Title: " + title);
        transView.showMsgInfo(timeout, mensaje, title, false);
    }

    @Override
    public int showTransInfo(int timeout, TransLogData logData) {
        this.mLatch = new CountDownLatch(1);
        transView.showTransInfoView(timeout, logData, listener);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            Logger.exception(clase, e);
            Thread.currentThread().interrupt();
            Logger.error("Exception" + e.toString());
        }
        return mRet;
    }

    @Override
    public void trannSuccess(int timeout, int code, String... args) {
        String info = getStatusInfo(String.valueOf(code));
        if (args.length != 0) {
            info += "\n" + args[0];
        }
        transView.showResultView(timeout, true, false, false, info, listener);
        UIUtils.beep(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD);
    }

    @Override
    public void showError(int timeout, int errcode, boolean isIconoWfi) {
        String descripcion = getErrInfo(String.valueOf(errcode));
        Logger.flujo(clase, " Codigo : " + errcode + " Descripcion : " + descripcion);
        Logger.debug(clase, "showError: " + " Codigo : " + errcode + " Descripcion : " + descripcion);
        transView.showResultView(timeout, false, isIconoWfi, false, getErrInfo(String.valueOf(errcode)), listener);
        UIUtils.beep(ToneGenerator.TONE_PROP_BEEP2);
    }

    @Override
    public void showError(int timeout, String encabezado, int errcode, boolean isIconoWfi, boolean aprobado) {
        String descripcion = getErrInfo(String.valueOf(errcode));
        Logger.flujo(clase, "Header : " + encabezado + " Codigo : " + errcode + " Descripcion : " + descripcion);
        Logger.debug(clase, "showError: " + "Header : " + encabezado + " Codigo : " + errcode + " Descripcion : " + descripcion);
        transView.showResultView(timeout, encabezado, aprobado, isIconoWfi, false, descripcion, listener);
        UIUtils.beep(ToneGenerator.TONE_PROP_BEEP2);
    }

    @Override
    public void showError(int timeout, String encabezado, String errcode, boolean isIconoWfi, boolean aprobado) {
        Logger.flujo(clase, "Header : " + encabezado + " Codigo : " + errcode + " Descripcion : " + errcode);
        Logger.debug(clase, "showError: " + "Header : " + encabezado + " Codigo : " + errcode + " Descripcion : " + errcode);
        transView.showResultView(timeout, encabezado, aprobado, isIconoWfi, false, errcode, listener);
        UIUtils.beep(ToneGenerator.TONE_PROP_BEEP2);
    }

    @Override
    public InputInfo showTypeCoin(int timeout, final String title) {
        transView.showTypeCoinView(timeout, title, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            Logger.exception(clase, e);
            Thread.currentThread().interrupt();
            Logger.error("Exception" + e.toString());
        }
        InputInfo info = new InputInfo();
        if (mRet == 1) {
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        } else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public InputInfo showInputUser(int timeout, String title, String label, int min, int max) {
        transView.showInputUser(timeout, title, label, min, max, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            Logger.exception(clase, e);
            Thread.currentThread().interrupt();
            Logger.error("Exception" + e.toString());
        }
        InputInfo info = new InputInfo();
        if (mRet == 1) {
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        } else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.REFERENCE));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public void toasTrans(int errcode, boolean sound, boolean isErr) {
        if (isErr)
            transView.toasTransView(getErrInfo(String.valueOf(errcode)), sound);
        else
            transView.toasTransView(getStatusInfo(String.valueOf(errcode)), sound);
    }

    public void toasTrans(String errcode, boolean sound, boolean isErr) {
        if (isErr)
            transView.toasTransView(errcode, sound);
        else
            transView.toasTransView(errcode, sound);
    }

    @Override
    public InputInfo showConfirmAmount(int timeout, String title, String label, String amnt, boolean isHTML) {
        transView.showConfirmAmountView(timeout, title, label, amnt, isHTML, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            Logger.exception(clase, e);
            Thread.currentThread().interrupt();
            Logger.error("Exception" + e.toString());
        }
        InputInfo info = new InputInfo();
        if (mRet == 1) {
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        } else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.REFERENCE));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public void showMessage(String message, boolean transaccion) {
        switch (message) {
            case "Retire la tarjeta":
                transView.showRetireTarjeta();
                break;
            default:
                transView.showMsgInfo(60 * 1000, message, transaccion, false);
                break;
        }

    }

    /**
     * =============================================
     */

    private String getStatusInfo(String status) {
        try {
            String[] infos = Locale.getDefault().getLanguage().equals("zh") ?
                    PAYUtils.getProps(PaySdk.getInstance().getContext(), TMConstants.STATUS, status) :
                    PAYUtils.getProps(PaySdk.getInstance().getContext(), TMConstants.STATUS_EN, status);
            if (infos != null) {
                return infos[0];
            }
        } catch (PaySdkException pse) {
            Logger.exception(clase, pse);
            Logger.error("Exception" + pse.toString());
            Thread.currentThread().interrupt();
        }
        if (Locale.getDefault().getLanguage().equals("zh")) {
            return "未知信息";
        } else {
            return "Error Desconocido";
        }
    }

    @Override
    public void showCardImg(String img) {
        this.mLatch = new CountDownLatch(1);
        transView.showCardViewImg(img, listener);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            Logger.exception(clase, e);
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if (mRet == 1) {
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        } else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
    }

    @Override
    public InputInfo showSignature(int timeout, String title, String transType) {
        transView.showSignatureView(timeout, listener, title, transType);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            Logger.exception(clase, e);
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if (mRet == 1) {
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        } else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public InputInfo showList(int timeout, String title, String transType, final ArrayList<String> listMenu, int id) {
        transView.showListView(timeout, listener, title, transType, listMenu, id);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            Logger.exception(clase, e);
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if (mRet == 1) {
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        } else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public InputInfo showVentaCuotas(int timeout) {
        transView.showVentaCuotasView(timeout, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            Logger.exception(clase, e);
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if (mRet == 1) {
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        } else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public InputInfo showIngresoDataNumerico(int timeout, String tipoIngreso, String title,
                                             int longitudMaxima, String trx, long amount) {
        return showIngresoDataNumerico(timeout, tipoIngreso, "", title, longitudMaxima, trx, amount);
    }

    @Override
    public InputInfo showIngresoDataNumerico(int timeout, String tipoIngreso, String mensaje, String title, int longitudMaxima, String trx, long amount) {
        transView.showIngresoDataNumericoView(timeout, tipoIngreso, mensaje, title, longitudMaxima, trx, amount, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            Logger.exception(clase, e);
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if (mRet == 1) {
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        } else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public InputInfo showSeleccionTipoDeCuenta(int timeout) {
        transView.showSeleccionTipoDeCuentaView(timeout, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            Logger.exception(clase, e);
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if (mRet == 1) {
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        } else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public void showImprimiendo(int timeout) {
        transView.showImprimiendoView(timeout);
    }

    @Override
    public InputInfo showResult(int timeout, boolean aprobada, boolean isIconoWifi, boolean opciones, String mensajeHost) {
        transView.showResultView(timeout, aprobada, isIconoWifi, opciones, mensajeHost, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            Logger.exception(clase, e);
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if (mRet == 1) {
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        } else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public InputInfo showPlanVentaButtonView(int timeout, ButtonModel botones[], String monto) {
        transView.showPlanVentaButtonView(timeout, botones, monto, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            Logger.exception(clase, e);
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if (mRet == 1) {
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        } else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public InputInfo showIngresoCuota(int timeout, String valor, String titulo, String subtitulo) {
        transView.showIngresoCuota(timeout, valor, titulo, subtitulo, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            Logger.exception(clase, e);
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if (mRet == 1) {
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        } else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }


    @Override
    public void showFinish() {
        transView.showFinishView();
    }

    @Override
    public InputInfo showBotones(int timeout, String titulo, ArrayList<ButtonModel> botones) {
        transView.showBotonesView(timeout, titulo, botones, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            Logger.exception(clase, e);
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if (mRet == 1) {
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        } else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public InputInfo showMensajeConfirmacion(int timeout, ModeloMensajeConfirmacion modelo) {
        transView.showMensajeConfirmacionView(timeout, modelo, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            Logger.exception(clase, e);
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if (mRet == 1) {
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        } else {
            if (transView.getInput(InputManager.Mode.AMOUNT).equals("no")) {
                info.setResultFlag(false);
                info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
                info.setErrno(Tcode.T_user_cancel);
            } else {
                info.setResultFlag(true);
                info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
                info.setNextStyle(payStyle);
            }
        }
        return info;
    }

    @Override
    public void showVerSaldoCuenta(int timeout, long saldo) {
        transView.showVerSaldoCuentaView(timeout, saldo);
    }

    @Override
    public InputInfo showResultCierre(int timeout, LogsCierresModelo cierresModelo) {
        transView.showResultCierreView(timeout, cierresModelo, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            Logger.exception(clase, e);
            e.printStackTrace();
        }
        InputInfo info = new InputInfo();
        if (mRet == 1) {
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        } else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;
    }

    @Override
    public void showContacLessInfo(boolean finish) {
        transView.showContacLessInfoView(finish);
    }

    @Override
    public InputInfo showListBilleteras(int timeout, String titulo, List<Billeteras> billeterasList) {
        transView.showListBilleteras(timeout, titulo, billeterasList, listener);
        this.mLatch = new CountDownLatch(1);
        try {
            this.mLatch.await();
        } catch (InterruptedException e) {
            Logger.exception(clase, e);
            e.printStackTrace();
        }

        InputInfo info = new InputInfo();
        if (mRet == 1) {
            info.setResultFlag(false);
            info.setErrno(Tcode.T_user_cancel);
        } else {
            info.setResultFlag(true);
            info.setResult(transView.getInput(InputManager.Mode.AMOUNT));
            info.setNextStyle(payStyle);
        }
        return info;

    }

    @Override
    public void showMsgInfo(int timeout, String title, String message, int ico, String metodo) {
        Logger.flujo(clase, "Metodo: " + metodo + " Header : " + title + " Mensaje : " + message);
        Logger.debug(clase, "showMsgInfo: " + "Metodo: " + metodo + "Header : " + title + " Mensaje : " + message);
        transView.showInfoMessage(title, message, timeout, ico, listener);
        UIUtils.beep(ToneGenerator.TONE_PROP_BEEP2);
    }
}
