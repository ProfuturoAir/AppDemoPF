package com.airmovil.profuturo.ti.retencion.model;

/**
 * Created by tecnicoairmovil on 04/04/17.
 */

public class DirectorReporteClientesModel {
    public String cita;
    public int idSucursal;
    public int idTramite;
    public String nombreCliente;
    public String numeroCuenta;
    public String numeroEmpleado;
    public String retenido;

    public String getCita() {
        return cita;
    }

    public void setCita(String cita) {
        this.cita = cita;
    }

    public int getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
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

    public String getNumeroEmpleado() {
        return numeroEmpleado;
    }

    public void setNumeroEmpleado(String numeroEmpleado) {
        this.numeroEmpleado = numeroEmpleado;
    }

    public String getRetenido() {
        return retenido;
    }

    public void setRetenido(String retenido) {
        this.retenido = retenido;
    }
}
