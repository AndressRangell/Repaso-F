package com.flota.logscierres;

public class LogsCierreDetalladoModelo {

    private String tarjeta;
    private String cargo;
    private String numBoleta;
    private String monto;
    private String fecha;
    private String hora;
    private String trans;
    private String idCierre;
    private String tipoTarjeta;

    public LogsCierreDetalladoModelo() {
        // this constructor is empty
    }

    public String getIdCierre() {
        return idCierre;
    }

    public void setIdCierre(String idCierre) {
        this.idCierre = idCierre;
    }

    public String getTarjeta() {
        return tarjeta;
    }

    public void setTarjeta(String tarjeta) {
        this.tarjeta = tarjeta;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getNumBoleta() {
        return numBoleta;
    }

    public void setNumBoleta(String numBoleta) {
        this.numBoleta = numBoleta;
    }

    public String getMonto() {
        return monto;
    }

    public void setMonto(String monto) {
        this.monto = monto;
    }

    public String getTrans() {
        return trans;
    }

    public void setTrans(String trans) {
        this.trans = trans;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getTipoTarjeta() {
        return tipoTarjeta;
    }

    public void setTipoTarjeta(String tipoTarjeta) {
        this.tipoTarjeta = tipoTarjeta;
    }
}
