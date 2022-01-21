package com.flota.adapters.model;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class ButtonModel implements UAItem {

    private int id;
    private String code;
    private String name;
    private Drawable icon;

    public ButtonModel() {
    }

    public ButtonModel(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public ButtonModel(String code, String name, Drawable icon) {
        this.code = code;
        this.name = name;
        this.icon = icon;
    }

    public ButtonModel(int id, Drawable icon) {
        this.id = id;
        this.icon = icon;
    }

    public static void setImage(View view, Drawable drawable) {
        try {
            ((AppCompatImageView) ((RelativeLayout) ((LinearLayout) ((CardView) view)
                    .getChildAt(0)).getChildAt(0)).getChildAt(0)).setImageDrawable(drawable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<ButtonModel> removeRepeating(List<ButtonModel> buttons) {
        TreeMap<String, ButtonModel> map = new TreeMap<>();
        for (ButtonModel button : buttons) {
            map.put(button.getName(), button);
        }
        buttons.clear();
        buttons.addAll(map.values());
        return new ArrayList<>(buttons);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}
