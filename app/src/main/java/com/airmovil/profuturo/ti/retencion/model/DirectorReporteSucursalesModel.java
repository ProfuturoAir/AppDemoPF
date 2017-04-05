package com.airmovil.profuturo.ti.retencion.model;

/**
 * Created by tecnicoairmovil on 04/04/17.
 */

public class DirectorReporteSucursalesModel {
    public int idSucursal;
    public int conCita;
    public int sinCita;
    public int emitido;
    public int noEmitido;
    public int saldoEmitido;
    public int saldoNoEmetido;

    public int getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }

    public int getConCita() {
        return conCita;
    }

    public void setConCita(int conCita) {
        this.conCita = conCita;
    }

    public int getSinCita() {
        return sinCita;
    }

    public void setSinCita(int sinCita) {
        this.sinCita = sinCita;
    }

    public int getEmitido() {
        return emitido;
    }

    public void setEmitido(int emitido) {
        this.emitido = emitido;
    }

    public int getNoEmitido() {
        return noEmitido;
    }

    public void setNoEmitido(int noEmitido) {
        this.noEmitido = noEmitido;
    }

    public int getSaldoEmitido() {
        return saldoEmitido;
    }

    public void setSaldoEmitido(int saldoEmitido) {
        this.saldoEmitido = saldoEmitido;
    }

    public int getSaldoNoEmetido() {
        return saldoNoEmetido;
    }

    public void setSaldoNoEmetido(int saldoNoEmetido) {
        this.saldoNoEmetido = saldoNoEmetido;
    }
}
