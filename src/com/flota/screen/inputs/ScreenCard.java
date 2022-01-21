package com.flota.screen.inputs;

import com.flota.screen.Screen;
import com.flota.screen.utils.ScreenToolbar;
import com.wposs.flota.R;

public class ScreenCard extends Screen {

    private final int mode;
    private final TypeScreen typeScreen;
    private String title;
    private String message;
    private long amount;

    public ScreenCard(int timeout, int mode, TypeScreen typeScreen) {
        super(timeout, null);
        this.mode = mode;
        this.typeScreen = typeScreen;
    }

    public ScreenCard(int timeout, int mode, TypeScreen typeScreen, ScreenToolbar sToolbar) {
        super(timeout, sToolbar);
        this.mode = mode;
        this.typeScreen = typeScreen;
    }

    @Override
    public int getLayout() {
        return R.layout.screen_card_processing;
    }

    public int getMode() {
        return mode;
    }

    public String getTitle() {
        return title;
    }

    public ScreenCard setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ScreenCard setMessage(String message) {
        this.message = message;
        return this;
    }

    public TypeScreen getTypeScreen() {
        return typeScreen;
    }

    public long getAmount() {
        return amount;
    }

    public ScreenCard setAmount(long amount) {
        this.amount = amount;
        return this;
    }

    @Override
    public String toString() {
        return "ScreenCard{" +
                "mode=" + mode +
                ", typeScreen=" + typeScreen +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", amount=" + amount +
                '}';
    }

    public enum TypeScreen {
        CARD_PROCESSING,
        CARD_HANDLING,
        CARD_FALLBACK
    }
}
