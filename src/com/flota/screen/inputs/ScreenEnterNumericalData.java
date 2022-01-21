package com.flota.screen.inputs;

import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;

import com.flota.screen.inputs.base.ScreenInput;
import com.flota.screen.inputs.methods.NumericalData;
import com.flota.screen.utils.ScreenToolbar;
import com.wposs.flota.R;

public class ScreenEnterNumericalData extends ScreenInput {

    private String title;
    private Drawable image;
    private String message;
    private NumericalData input;

    public ScreenEnterNumericalData(int timeout, ScreenToolbar screenToolbar) {
        super(timeout, screenToolbar);
    }

    public ScreenEnterNumericalData(int timeout) {
        super(timeout, null);
    }

    public String getTitle() {
        return title;
    }

    public ScreenEnterNumericalData setTitle(String title) {
        this.title = title;
        return this;
    }

    public Drawable getImage() {
        return image;
    }

    public ScreenEnterNumericalData setImage(Drawable image) {
        this.image = image;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ScreenEnterNumericalData setMessage(String message) {
        this.message = message;
        return this;
    }

    public NumericalData getInput() {
        return input;
    }

    public ScreenEnterNumericalData setInput(NumericalData input) {
        this.input = input;
        return this;
    }

    @Override
    @LayoutRes
    public int getLayout() {
        return R.layout.screen_enter_numerical_data;
    }

    @Override
    public TypeScreen getTypeScreen() {
        return TypeScreen.ENTER_NUMERICAL_DATA;
    }
}
