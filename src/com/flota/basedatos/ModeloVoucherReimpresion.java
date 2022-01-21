package com.flota.basedatos;

public class ModeloVoucherReimpresion {

    String pan;
    String nroBoleta;
    String nroCargo;
    String monto;
    String tipoVenta;
    String fecha;
    byte[] voucher;

    public ModeloVoucherReimpresion() {
        // Contructor vacío
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getNroBoleta() {
        return nroBoleta;
    }

    public void setNroBoleta(String nroBoleta) {
        this.nroBoleta = nroBoleta;
    }

    public String getNroCargo() {
        return nroCargo;
    }

    public void setNroCargo(String nroCargo) {
        this.nroCargo = nroCargo;
    }

    public String getMonto() {
        return monto;
    }

    public void setMonto(String monto) {
        this.monto = monto;
    }

    public String getTipoVenta() {
        return tipoVenta;
    }

    public void setTipoVenta(String tipoVenta) {
        this.tipoVenta = tipoVenta;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public byte[] getVoucher() {
        return voucher;
    }

    public void setVoucher(byte[] voucher) {
        this.voucher = voucher;
    }
}
