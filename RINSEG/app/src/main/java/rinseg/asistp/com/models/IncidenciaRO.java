package rinseg.asistp.com.models;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by Carlos on 10/10/2016.
 */
public class IncidenciaRO extends RealmObject{
    private int id;
    private String nombre;
    private String detalle;
    private String tmpId;

    private int eventId;
    private int eventItemId;
    private String descripcion;
    private int frecuenciaId;
    private int severidadId;
    private int blancoId;
    private Date fechalimite;
    private String fechalimiteString;
    private int racId;
    private int reportanteId;
    private String responsable;
    private String supervisor;
    private IncidenciaLevantadaRO incidenciaLevantadaRO;


    public IncidenciaRO(String nombre, String detalle) {
        this.nombre = nombre;
        this.detalle = detalle;
    }

    public IncidenciaRO() { }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }


    public String getTmpId() {
        return tmpId;
    }

    public void setTmpId(String tmpId) {
        this.tmpId = tmpId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getEventItemId() {
        return eventItemId;
    }

    public void setEventItemId(int eventItemId) {
        this.eventItemId = eventItemId;
    }

    public int getRacId() {
        return racId;
    }

    public void setRacId(int racId) {
        this.racId = racId;
    }

    public int getFrecuenciaId() {
        return frecuenciaId;
    }

    public void setFrecuenciaId(int frecuenciaId) {
        this.frecuenciaId = frecuenciaId;
    }

    public int getSeveridadId() {
        return severidadId;
    }

    public void setSeveridadId(int severidadId) {
        this.severidadId = severidadId;
    }

    public int getBlancoId() {
        return blancoId;
    }

    public void setBlancoId(int blancoId) {
        this.blancoId = blancoId;
    }

    public Date getFechalimite() {
        return fechalimite;
    }

    public void setFechalimite(Date fechalimite) {
        this.fechalimite = fechalimite;
    }

    public String getFechalimiteString() {
        return fechalimiteString;
    }

    public void setFechalimiteString(String fechalimiteString) {
        this.fechalimiteString = fechalimiteString;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getReportanteId() {
        return reportanteId;
    }

    public void setReportanteId(int reportanteId) {
        this.reportanteId = reportanteId;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    public IncidenciaLevantadaRO getIncidenciaLevantadaRO() {
        return incidenciaLevantadaRO;
    }

    public void setIncidenciaLevantadaRO(IncidenciaLevantadaRO incidenciaLevantadaRO) {
        this.incidenciaLevantadaRO = incidenciaLevantadaRO;
    }
}
