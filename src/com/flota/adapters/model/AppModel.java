package com.flota.adapters.model;

public class AppModel implements UAItem {

    private String name;
    private String packageName;

    public AppModel(String name, String packageName) {
        this.name = name;
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
