package com.airmovil.profuturo.ti.retencion.model;

/**
 * Created by tecnicoairmovil on 05/04/17.
 */

public class GerenteReporteAsistenciaModel {
    public int idSucursal;
    public String nombre;
    public int numeroEmpleado;
    public String nEmpleado;

    public int getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getNumeroEmpleado() {
        return numeroEmpleado;
    }

    public void setNumeroEmpleado(int numeroEmpleado) {
        this.numeroEmpleado = numeroEmpleado;
    }

    public String getnEmpleado() {
        return nEmpleado;
    }

    public void setnEmpleado(String nEmpleado) {
        this.nEmpleado = nEmpleado;
    }
}

