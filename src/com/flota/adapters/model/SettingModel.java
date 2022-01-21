package com.flota.adapters.model;

import java.util.List;

public class SettingModel implements UAItem {

    private String id;
    private String title;
    private List<ButtonModel> buttons;

    public SettingModel(String id, String title, List<ButtonModel> buttons) {
        this.id = id;
        this.title = title;
        this.buttons = buttons;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ButtonModel> getButtons() {
        return buttons;
    }

    public void setButtons(List<ButtonModel> buttons) {
        this.buttons = buttons;
    }
}
