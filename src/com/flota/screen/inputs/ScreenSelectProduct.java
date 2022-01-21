package com.flota.screen.inputs;

import android.support.annotation.LayoutRes;

import com.flota.adapters.model.ProductModel;
import com.flota.screen.inputs.base.ScreenInput;
import com.wposs.flota.R;

import java.util.List;

public class ScreenSelectProduct extends ScreenInput {

    private final List<ProductModel> products;

    public ScreenSelectProduct(int timeout, List<ProductModel> products) {
        super(timeout, null);
        this.products = products;
    }

    public List<ProductModel> getProducts() {
        return products;
    }

    @Override
    @LayoutRes
    public int getLayout() {
        return R.layout.screen_select_product;
    }

    @Override
    public TypeScreen getTypeScreen() {
        return TypeScreen.SELECT_PRODUCT;
    }
}
