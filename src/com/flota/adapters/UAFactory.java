package com.flota.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;

import com.flota.adapters.UAListener.AppListener;
import com.flota.adapters.UAListener.ButtonListener;
import com.flota.adapters.UAListener.ProductListener;
import com.flota.adapters.model.AppModel;
import com.flota.adapters.model.ButtonModel;
import com.flota.adapters.model.CategoryModel;
import com.flota.adapters.model.ProductModel;
import com.flota.adapters.model.SettingModel;
import com.flota.adapters.model.UAItem;
import com.wposs.flota.R;

import java.util.ArrayList;
import java.util.List;

public class UAFactory {

    private UAFactory() {
    }

    public static UniversalAdapter adapterApps(List<AppModel> apps, AppListener appListener) {
        List<UAItem> items = new ArrayList<>();
        items.addAll(apps);
        return new UniversalAdapter(null, R.layout.item_app, false, -1, items, appListener);
    }

    public static UniversalAdapter adapterButtons(List<ButtonModel> buttons, ButtonListener buttonListener) {
        List<UAItem> items = new ArrayList<>();
        items.addAll(buttons);
        return new UniversalAdapter(null, R.layout.item_button, false, -1, items, buttonListener);
    }

    public static UniversalAdapter adapterSettings(Context context, List<SettingModel> settings, ButtonListener buttonListener) {
        List<UAItem> items = new ArrayList<>();
        items.addAll(settings);
        return new UniversalAdapter(context, R.layout.item_setting, false, -1, items, buttonListener);
    }

    public static UniversalAdapter adapterProducts(List<ProductModel> products, String codeSelectProduct, ProductListener productListener) {
        List<UAItem> items = new ArrayList<>();
        items.addAll(products);
        return new UniversalAdapter(null, R.layout.item_product, false, getIndexOfCodeProduct(products, codeSelectProduct), items, productListener);
    }

    public static String getDescriptionOfCodeProduct(final List<ProductModel> products, final String codeSelectProduct) {
        try {
            return products.get(getIndexOfCodeProduct(products, codeSelectProduct)).getDescription();
        } catch (Exception e) {
            return null;
        }
    }

    private static int getIndexOfCodeProduct(final List<ProductModel> products, final String codeSelectProduct) {
        for (ProductModel product : products)
            if (product.getCode().equals(codeSelectProduct)) return products.indexOf(product);
        return -1;
    }

    public static UniversalAdapter adapterCategory(Context context, @LayoutRes int layout, List<CategoryModel> categoryList) {
        List<UAItem> items = new ArrayList<>();
        items.addAll(categoryList);
        return new UniversalAdapter(context, layout, true, -1, items, null);
    }

    public static UniversalAdapter adapterItem(Context context, @LayoutRes int layout, List<UAItem> items) {
        return new UniversalAdapter(context, layout, false, -1, items, null);
    }
}
