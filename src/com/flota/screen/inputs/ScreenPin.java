package com.flota.screen.inputs;

import android.graphics.Bitmap;

import com.newpos.libpay.device.pinpad.OfflineRSA;
import com.secure.api.PadView;

public class ScreenPin {

    private final int timeout;
    private final TypeScreen typeScreen;
    private PadView padView = new PadView();

    // Pin Online or Online DUKPT
    private String pinCardNo;

    // Pin Offline
    private int i;
    private OfflineRSA key;
    private int counts;

    private ScreenPin(TypeScreen typeScreen, int timeout) {
        this.timeout = timeout;
        this.typeScreen = typeScreen;
        padView.setTitleMsg("Red Infonet - Ingreso de PIN"); // Default
        padView.setPinTips("Ingrese PIN"); // Default
    }

    public static ScreenPin offline(int timeout, int i, OfflineRSA key, int counts) {
        return new ScreenPin(TypeScreen.OFFLINE, timeout).setI(i).setKey(key)
                .setTitleMsg("Newpos Secure Keyboard")
                .setPinTips("Please enter offline PIN\n" + "Left " + counts + " times");
    }

    public static ScreenPin online(int timeout, String amount, String cardNo) {
        return new ScreenPin(TypeScreen.ONLINE, timeout).setPinCardNo(cardNo).setAmount(amount);
    }

    public static ScreenPin onlineDUKPT(int timeout, String amount, String cardNo) {
        return new ScreenPin(TypeScreen.ONLINE_DUKPT, timeout).setPinCardNo(cardNo).setAmount(amount);
    }

    // Pin Online or Online DUKPT

    public String getPinCardNo() {
        return pinCardNo;
    }

    public ScreenPin setPinCardNo(String pinCardNo) {
        this.pinCardNo = pinCardNo;
        return this;
    }

    // Pin Offline

    public int getI() {
        return i;
    }

    public ScreenPin setI(int i) {
        this.i = i;
        return this;
    }

    public OfflineRSA getKey() {
        return key;
    }

    public ScreenPin setKey(OfflineRSA key) {
        this.key = key;
        return this;
    }

    public int getCounts() {
        return counts;
    }

    public ScreenPin setCounts(int counts) {
        this.counts = counts;
        return this;
    }

    // Pad View

    public Bitmap getTitleIcon() {
        return padView.getTitleIcon();
    }

    public ScreenPin setTitleIcon(Bitmap titleIcon) {
        padView.setTitleIcon(titleIcon);
        return this;
    }

    public String getTitleMsg() {
        return padView.getTitleMsg();
    }

    public ScreenPin setTitleMsg(String titleMsg) {
        padView.setTitleMsg(titleMsg);
        return this;
    }

    public String getAmountTitle() {
        return padView.getAmountTitle();
    }

    public ScreenPin setAmountTitle(String amountTitle) {
        padView.setAmountTitle(amountTitle);
        return this;
    }

    public String getAmount() {
        return padView.getAmount();
    }

    public ScreenPin setAmount(String amount) {
        padView.setAmount(amount);
        return this;
    }

    public String getPinTips() {
        return padView.getPinTips();
    }

    public ScreenPin setPinTips(String pinTips) {
        padView.setPinTips(pinTips);
        return this;
    }

    // Screen

    public int getTimeout() {
        return timeout;
    }

    public TypeScreen getType() {
        return typeScreen;
    }

    public PadView getPadView() {
        return padView;
    }

    public void setPadView(PadView padView) {
        this.padView = padView;
    }

    public enum TypeScreen {
        OFFLINE,
        ONLINE,
        ONLINE_DUKPT
    }
}
