package com.airmovil.profuturo.ti.retencion.model;

/**
 * Created by cesarriver on 31/03/17.
 */

public class DirectorReporteGerenciasModel {
    public int idGerencia;
    public int conCita;
    public int sinCita;
    public int emitidas;
    public int noEmitidas;
    public String saldoRetenido;
    public String saldoNoRetenido;

    public int getIdGerencia() {
        return idGerencia;
    }

    public void setIdGerencia(int idGerencia) {
        this.idGerencia = idGerencia;
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

    public int getEmitidas() {
        return emitidas;
    }

    public void setEmitidas(int emitidas) {
        this.emitidas = emitidas;
    }

    public int getNoEmitidas() {
        return noEmitidas;
    }

    public void setNoEmitidas(int noEmitidas) {
        this.noEmitidas = noEmitidas;
    }




    public String getSaldoRetenido() {
        return saldoRetenido;
    }

    public void setSaldoRetenido(String saldoRetenido) {
        this.saldoRetenido = saldoRetenido;
    }

    public String getSaldoNoRetenido() {
        return saldoNoRetenido;
    }

    public void setSaldoNoRetenido(String saldoNoRetenido) {
        this.saldoNoRetenido = saldoNoRetenido;
    }
}
