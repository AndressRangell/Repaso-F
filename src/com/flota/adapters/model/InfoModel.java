package com.flota.adapters.model;

public class InfoModel implements UAItem {

    private String subtitle;
    private String content;

    public InfoModel(String subtitle, String content) {
        this.subtitle = subtitle;
        this.content = content;
    }

    public static InfoModel justSubtitle(String subtitle) {
        return new InfoModel(subtitle, null);
    }

    public static InfoModel justContent(String content) {
        return new InfoModel(content, null);
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getContent() {
        return content;
    }
}
