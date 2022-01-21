package com.flota.screen.inputs.base;

import com.flota.screen.Screen;
import com.flota.screen.utils.ScreenToolbar;

public abstract class ScreenInput extends Screen {

    protected ScreenInput(int timeout, ScreenToolbar sToolbar) {
        super(timeout, sToolbar);
    }

    public abstract TypeScreen getTypeScreen();

    public enum TypeScreen {
        ENTER_NUMERICAL_DATA,
        SELECT_PRODUCT,
        SELECT_FROM_TWO_OPTIONS
    }
}
