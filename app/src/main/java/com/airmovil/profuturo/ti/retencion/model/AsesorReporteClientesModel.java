package com.airmovil.profuturo.ti.retencion.model;

/**
 * Created by tecnicoairmovil on 03/04/17.
 */

public class AsesorReporteClientesModel {
    public String nombreCliente;
    public String numeroCuenta;
    public String conCita;
    public String noEmitido;
    public int idTramite;
    public String curp;
    public String hora;

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getCurp() {
        return curp;
    }

    public void setCurp(String curp) {
        this.curp = curp;
    }

    public int getIdTramite() {
        return idTramite;
    }

    public void setIdTramite(int idTramite) {
        this.idTramite = idTramite;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public String getConCita() {
        return conCita;
    }

    public void setConCita(String conCita) {
        this.conCita = conCita;
    }

    public String getNoEmitido() {
        return noEmitido;
    }

    public void setNoEmitido(String noEmitido) {
        this.noEmitido = noEmitido;
    }
}
