package com.flota.adapters;

import android.view.View;

import com.flota.adapters.model.AppModel;
import com.flota.adapters.model.ButtonModel;
import com.flota.adapters.model.ProductModel;
import com.flota.logscierres.LogsCierresModelo;

public class UAListener {

    private UAListener() {
    }

    public static interface Listener {
    }

    public static interface UAL {
        void updateLastItem(int adapterPosition);
    }

    public static interface AppListener extends Listener {
        void onClick(View view, AppModel app);
    }

    public static interface ButtonListener extends Listener {
        void onClick(View view, ButtonModel button);
    }

    public static interface ProductListener extends Listener {
        void onClick(View view, ProductModel product);
    }

    public static interface ClosureListener extends Listener {
        void onClickGeneral(View view, LogsCierresModelo closure);

        void onClickDetails(View view, LogsCierresModelo closure);
    }
}
