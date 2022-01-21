package com.flota.screen.result.base;

import com.flota.screen.Screen;
import com.flota.screen.utils.ScreenToolbar;

public abstract class ScreenResult extends Screen {

    protected ScreenResult(int timeout, ScreenToolbar sToolbar) {
        super(timeout, sToolbar);
    }

    public abstract TypeScreen getTypeScreen();

    public enum TypeScreen {
        ACCOUNT_BALANCE,
        TRANS
    }
}
