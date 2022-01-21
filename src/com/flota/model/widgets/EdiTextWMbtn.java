package com.flota.model.widgets;

import android.content.Context;
import android.widget.Button;

public class EdiTextWMbtn extends EdiTextWidgetModel{

    Button button;

    public EdiTextWMbtn(String title, int typeWidget, int lengthMax, int typeInput, Boolean enabled, Context context) {
        super(title, typeWidget, lengthMax, typeInput, enabled, context);
    }

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }
}
