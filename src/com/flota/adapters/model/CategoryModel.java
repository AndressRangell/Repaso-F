package com.flota.adapters.model;

import java.util.ArrayList;
import java.util.List;

public class CategoryModel implements UAItem {

    private String title;
    private List<InfoModel> infoList = new ArrayList<>();

    public CategoryModel(String title, List<InfoModel> infoList) {
        this.title = title;
        this.infoList = infoList;
    }

    public CategoryModel(String title, InfoModel info) {
        this.title = title;
        this.infoList.add(info);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<InfoModel> getInfoList() {
        return infoList;
    }

    public void setInfoList(List<InfoModel> infoList) {
        this.infoList = infoList;
    }

    public List<UAItem> getInfoListItems() {
        List<UAItem> items = new ArrayList<>();
        items.addAll(infoList);
        return items;
    }
}
