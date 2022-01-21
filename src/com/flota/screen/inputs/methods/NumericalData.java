package com.flota.screen.inputs.methods;

import android.graphics.drawable.Drawable;
import android.view.Gravity;

public class NumericalData {

    // Required
    private final FormatInput formatInput;
    private final int minLength;
    private final int maxLength;

    private String text = "";
    private TextAlignment textAlignment = TextAlignment.START;
    private String hint = "";
    private String suffix;
    private String msgErrorValidation = "";
    private Drawable icon;
    private boolean isKeyboardWith000 = true;

    public NumericalData(FormatInput formatInput, int length) {
        this.formatInput = formatInput;
        this.minLength = length;
        this.maxLength = length;
    }

    public NumericalData(FormatInput formatInput, int minLength, int maxLength) {
        this.formatInput = formatInput;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    public FormatInput getFormatInput() {
        return formatInput;
    }

    public int getMinLength() {
        return minLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public String getSuffix() {
        return suffix;
    }

    public NumericalData setSuffix(String suffix) {
        this.suffix = suffix;
        return this;
    }

    public String getHint() {
        return hint;
    }

    public NumericalData setHint(String hint) {
        this.hint = hint;
        return this;
    }

    public String getText() {
        return text;
    }

    public NumericalData setText(String text) {
        this.text = text;
        return this;
    }

    public String getMsgErrorValidation() {
        return msgErrorValidation;
    }

    public NumericalData setMsgErrorValidation(String msgErrorValidation) {
        this.msgErrorValidation = msgErrorValidation;
        return this;
    }

    public Drawable getIcon() {
        return icon;
    }

    public NumericalData setIcon(Drawable icon) {
        this.icon = icon;
        return this;
    }

    public boolean isKeyboardWith000() {
        return isKeyboardWith000;
    }

    public NumericalData setKeyboardWith000(boolean enable) {
        this.isKeyboardWith000 = enable;
        return this;
    }

    public int getTextAlignment() {
        switch (textAlignment) {
            case CENTER:
                return Gravity.CENTER;
            case END:
                return Gravity.END;
            case START:
            default:
                return Gravity.START;
        }
    }

    public NumericalData setTextAlignment(TextAlignment textAlignment) {
        this.textAlignment = textAlignment;
        return this;
    }

    public enum TextAlignment {
        START,
        CENTER,
        END
    }
}
