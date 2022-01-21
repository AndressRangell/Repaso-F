package com.flota.inicializacion.configuracioncomercio;

import com.newpos.libpay.Logger;

import java.util.ArrayList;
import java.util.List;

public class TransActive {

    private boolean venta = false;
    private boolean ventaCuotas = false;
    private boolean ventaZimple = false;
    private boolean ventaSinTarjeta = false;
    private boolean ventaCreditoForzado = false;
    private boolean ventaDebitoForzado = false;
    private boolean ventaCashBack = false;
    private boolean ventaMinutos = false;
    private boolean consultaSaldo = false;

    String clase ="TransActive.java";

    protected static final String[] listTransAllow = new String[]{
            "venta",
            "venta cuotas",
            "venta zimple",
            "venta sin tarjeta",
            "venta credito forzado",
            "venta debito forzado",
            "venta casback",
            "venta minutos",
            "consulta saldo"//total = 8
    };

    private void setTransAllow(int pos, boolean value){
        switch (pos){
            case 0:
                setVenta(value);
            break;
            case 1:
                setVentaCuotas(value);
                break;
            case 2:
                setVentaZimple(value);
                break;
            case 3:
                setVentaSinTarjeta(value);
                break;
            case 4:
                setVentaCreditoForzado(value);
                break;
            case 5:
                setVentaDebitoForzado(value);
                break;
            case 6:
                setVentaCashBack(value);
                break;
            case 7:
                setVentaMinutos(value);
                break;
            case 8:
                setConsultaSaldo(value);
                break;
            default:
                break;
        }
    }

    public void verificateTransActive(List<TRANSACCIONES> transaccionesList) {

        TRANSACCIONES currentTrans = null;

        try {
            for (int i=0;i<listTransAllow.length;i++){
                currentTrans = transaccionesList.get(i);
                if (listTransAllow[i].equals(currentTrans.getNombre())){
                    setTransAllow(i, currentTrans.getHabilitar());
                }
            }

        } catch (IndexOutOfBoundsException ignored){
          Logger.exception(clase, ignored);
        }

    }

    public boolean isVenta() {
        return venta;
    }

    public void setVenta(boolean venta) {
        this.venta = venta;
    }

    public boolean isVentaCuotas() {
        return ventaCuotas;
    }

    public void setVentaCuotas(boolean ventaCuotas) {
        this.ventaCuotas = ventaCuotas;
    }

    public boolean isVentaZimple() {
        return ventaZimple;
    }

    public void setVentaZimple(boolean ventaZimple) {
        this.ventaZimple = ventaZimple;
    }

    public boolean isVentaSinTarjeta() {
        return ventaSinTarjeta;
    }

    public void setVentaSinTarjeta(boolean ventaSinTarjeta) {
        this.ventaSinTarjeta = ventaSinTarjeta;
    }

    public boolean isVentaCreditoForzado() {
        return ventaCreditoForzado;
    }

    public void setVentaCreditoForzado(boolean ventaCreditoForzado) {
        this.ventaCreditoForzado = ventaCreditoForzado;
    }

    public boolean isVentaDebitoForzado() {
        return ventaDebitoForzado;
    }

    public void setVentaDebitoForzado(boolean ventaDebitoForzado) {
        this.ventaDebitoForzado = ventaDebitoForzado;
    }

    public boolean isVentaCashBack() {
        return ventaCashBack;
    }

    public void setVentaCashBack(boolean ventaCashBack) {
        this.ventaCashBack = ventaCashBack;
    }

    public boolean isVentaMinutos() {
        return ventaMinutos;
    }

    public void setVentaMinutos(boolean ventaMinutos) {
        this.ventaMinutos = ventaMinutos;
    }

    public boolean isConsultaSaldo() {
        return consultaSaldo;
    }

    public void setConsultaSaldo(boolean consultaSaldo) {
        this.consultaSaldo = consultaSaldo;
    }
}
