package com.flota.adaptadores;

public class ModeloCierreTrans {
    private String codNegocio;
    private String montoTotal;
    private String cantidadTrans;
    private String tipoDeTarjeta;

    public ModeloCierreTrans() {
    }

    public String getCodNegocio() {
        return codNegocio;
    }

    public void setCodNegocio(String codNegocio) {
        this.codNegocio = codNegocio;
    }

    public String getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(String montoTotal) {
        this.montoTotal = montoTotal;
    }

    public String getCantidadTrans() {
        return cantidadTrans;
    }

    public void setCantidadTrans(String cantidadTrans) {
        this.cantidadTrans = cantidadTrans;
    }

    public String getTipoDeTarjeta() {
        return tipoDeTarjeta;
    }

    public void setTipoDeTarjeta(String tipoDeTarjeta) {
        this.tipoDeTarjeta = tipoDeTarjeta;
    }
}
