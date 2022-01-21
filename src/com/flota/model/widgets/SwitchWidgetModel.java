package com.flota.model.widgets;

import android.content.Context;
import android.view.View;
import android.widget.Switch;

import com.wposs.flota.R;

public class SwitchWidgetModel extends WidgetModel implements ItemWidget {

    private final Switch aSwitch;

    public SwitchWidgetModel(String titulo, int tipo, Context context) {
        super(titulo, tipo, context);
        aSwitch = new Switch(context);
    }

    public void setChecked(String oneOrZero) {
        oldValue = oneOrZero;
        aSwitch.setChecked(oneOrZero.equals("true"));
    }

    public void setDesign(int i) {
        if (i == 0) {
            aSwitch.setLayoutParams(lp);
            aSwitch.setPadding(0, 0, 16, 0);
            aSwitch.setSwitchMinWidth(96);
            aSwitch.setThumbDrawable(context.getDrawable(R.drawable.fondo_switch));
            aSwitch.setSwitchTextAppearance(context, R.style.SwitchTextAppearance);
            aSwitch.setShowText(true);
        }
    }

    @Override
    public Switch getWidget() {
        return aSwitch;
    }

    @Override
    public String getTituloItem() {
        return title;
    }

    @Override
    public View getWidgetItem() {
        return aSwitch;
    }

    @Override
    public String getResultado() {
        return aSwitch.isChecked() ? "true" : "false";
    }

    @Override
    public boolean isNewValor() {
        return aSwitch.isChecked() && oldValue.equals("false")
                || !aSwitch.isChecked() && oldValue.equals("true");
    }
}
