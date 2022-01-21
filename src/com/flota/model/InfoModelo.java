package com.flota.model;


import java.util.ArrayList;
import java.util.List;

public class InfoModelo {
    private String titulo, subtitulo;
    private List<InfoModelo> list = null;

    public int getSizeLis() {
        if (list == null) {
            return 0;
        } else {
            return list.size();
        }
    }

    public boolean isContenedor() {
        boolean result = false;
        if (list == null) {
            return false;
        } else {
            for (InfoModelo item : list) {
                if (item.getSizeLis() == 0) {
                    return false;
                } else {
                    result = true;
                }
            }
        }
        return result;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSubtitulo() {
        return subtitulo;
    }

    public List<InfoModelo> getList() {
        if (list == null) {
            return new ArrayList<>();
        } else {
            return list;
        }
    }

    public void setList(List<InfoModelo> list) {
        this.list = list;
    }
}
