package com.flota.model.widgets;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class LayoutManagerRcy {

    private final Context ctx;

    int type;


    public LayoutManagerRcy(Context ctx, int type) {
        this.type = type;
        this.ctx = ctx;
    }

    public LayoutManagerRcy(Context ctx) {
        this.ctx = ctx;
        this.type = 0;
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        if (type == 1) {
            return new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false);
        }
        return new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);
    }
}
