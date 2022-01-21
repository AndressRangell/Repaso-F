package com.flota.model.widgets;

import android.view.View;

public interface ItemWidget  {

    String getTituloItem();
    View getWidgetItem();
    String getResultado();
    boolean isNewValor();
}
