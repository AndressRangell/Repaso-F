package com.flota.model.widgets;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.flota.model.Item;

public abstract class WidgetModel implements Item {

    protected String title;
    protected int type;
    protected String oldValue;
    Context context;
    LinearLayout.LayoutParams lp;

    protected WidgetModel(String tytle, int type, Context context) {
        this.title = tytle;
        this.type = type;
        this.context = context;
        this.oldValue = "";
        lp = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
    }

    public int getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public abstract View getWidget();

}
