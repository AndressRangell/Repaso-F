package com.flota.model;

public class DosStringModel implements Item {

    private final String subTitulo;
    private String titulo;

    public DosStringModel(String titulo, String subTitulo) {
        this.titulo = titulo;
        this.subTitulo = subTitulo;
    }

    public DosStringModel(String titulo) {
        this.titulo = titulo;
        this.subTitulo = "";
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSubTitulo() {
        return subTitulo;
    }
}
