package com.flota.model;

import java.util.List;

public class ContenedorRcyModel implements Contenedor, Item {
    protected String tituloContenedor;
    protected List<Item> list;

    public ContenedorRcyModel(String tituloContenedor, List<Item> list) {
        this.tituloContenedor = tituloContenedor;
        this.list = list;
    }

    @Override
    public String getTitulo() {
        return tituloContenedor;
    }

    @Override
    public List<Item> getList() {
        return list;
    }
}
