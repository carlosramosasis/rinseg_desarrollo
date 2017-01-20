package rinseg.asistp.com.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by OSequeiros  on 19/01/2017.
 * Clase Realm que almacena los datos de una incidencia levantada
 */

public class IncidenciaLevantadaRO extends RealmObject {

    @SerializedName("inspection_item_id")
    private int idIncidencia;

    @SerializedName("description")
    private int descripcion;

    @SerializedName("date_close")
    private String fechaLevantamientoString;

    private Date fechaLevantamiento;

    @SerializedName("frequency_id")
    private int idFrecuencia;

    @SerializedName("severity_id")
    private int idSeveridad;

    @SerializedName("risk_category")
    private String categoriaRiesgo;

    @SerializedName("risk_level")
    private int nivelRiesgo;

    public int getIdIncidencia() {
        return idIncidencia;
    }

    public void setIdIncidencia(int idIncidencia) {
        this.idIncidencia = idIncidencia;
    }

    public int getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(int descripcion) {
        this.descripcion = descripcion;
    }

    public String getFechaLevantamientoString() {
        return fechaLevantamientoString;
    }

    public void setFechaLevantamientoString(String fechaLevantamientoString) {
        this.fechaLevantamientoString = fechaLevantamientoString;
    }

    public Date getFechaLevantamiento() {
        return fechaLevantamiento;
    }

    public void setFechaLevantamiento(Date fechaLevantamiento) {
        this.fechaLevantamiento = fechaLevantamiento;
    }

    public int getIdFrecuencia() {
        return idFrecuencia;
    }

    public void setIdFrecuencia(int idFrecuencia) {
        this.idFrecuencia = idFrecuencia;
    }

    public int getIdSeveridad() {
        return idSeveridad;
    }

    public void setIdSeveridad(int idSeveridad) {
        this.idSeveridad = idSeveridad;
    }

    public String getCategoriaRiesgo() {
        return categoriaRiesgo;
    }

    public void setCategoriaRiesgo(String categoriaRiesgo) {
        this.categoriaRiesgo = categoriaRiesgo;
    }

    public int getNivelRiesgo() {
        return nivelRiesgo;
    }

    public void setNivelRiesgo(int nivelRiesgo) {
        this.nivelRiesgo = nivelRiesgo;
    }
}
