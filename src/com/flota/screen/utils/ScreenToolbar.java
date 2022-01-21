package com.flota.screen.utils;

import android.support.annotation.DrawableRes;
import android.view.View;

public class ScreenToolbar {

    private String title;

    @DrawableRes
    private int iconLeft = 0;
    private View.OnClickListener listenerLeft;

    @DrawableRes
    private int iconRight = 0;
    private View.OnClickListener listenerRight;

    public String getTitle() {
        return title;
    }

    public ScreenToolbar setTitle(String title) {
        this.title = title;
        return this;
    }

    public ScreenToolbar setButtonLeft(int icon, View.OnClickListener listener) {
        this.iconLeft = icon;
        this.listenerLeft = listener;
        return this;
    }

    public ScreenToolbar setButtonRight(int icon, View.OnClickListener listener) {
        this.iconRight = icon;
        this.listenerRight = listener;
        return this;
    }

    public int getIconLeft() {
        return iconLeft;
    }

    public ScreenToolbar setIconLeft(int iconLeft) {
        this.iconLeft = iconLeft;
        return this;
    }

    public View.OnClickListener getListenerLeft() {
        return listenerLeft;
    }

    public ScreenToolbar setListenerLeft(View.OnClickListener listenerLeft) {
        this.listenerLeft = listenerLeft;
        return this;
    }

    public int getIconRight() {
        return iconRight;
    }

    public ScreenToolbar setIconRight(int iconRight) {
        this.iconRight = iconRight;
        return this;
    }

    public View.OnClickListener getListenerRight() {
        return listenerRight;
    }

    public ScreenToolbar setListenerRight(View.OnClickListener listenerRight) {
        this.listenerRight = listenerRight;
        return this;
    }
}
