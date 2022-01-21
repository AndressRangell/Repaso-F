package com.flota.screen;

import android.support.annotation.LayoutRes;

import com.flota.screen.utils.ScreenToolbar;

public abstract class Screen {

    private final int timeout;
    private final ScreenToolbar screenToolbar;

    protected Screen(int timeout, ScreenToolbar screenToolbar) {
        this.timeout = timeout;
        this.screenToolbar = screenToolbar;
    }

    public int getTimeout() {
        return timeout;
    }

    public ScreenToolbar getToolbar() {
        return screenToolbar;
    }

    @LayoutRes
    public abstract int getLayout();
}
