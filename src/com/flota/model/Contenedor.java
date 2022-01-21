package com.flota.model;

import java.util.List;

public interface Contenedor extends Item {

    String getTitulo();

    List<Item> getList();
}
