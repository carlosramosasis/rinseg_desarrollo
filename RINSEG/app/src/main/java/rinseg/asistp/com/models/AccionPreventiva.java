package rinseg.asistp.com.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmObject;
import rinseg.asistp.com.utils.Generic;

/**
 * Created by Carlos on 30/09/2016.
 */
public class AccionPreventiva extends RealmObject {
    private int id;
    @SerializedName("responsible")
    private String responsable;
    @SerializedName("action")
    private String accion;

    private Date fecha;

    @SerializedName("deadline")
    private String fechaString;


    private Date fechaFinalizacion;

    @SerializedName("date_done")
    private String fechaFinalizacionString;

    @SerializedName("action_done")
    private boolean accionHecha;


    public AccionPreventiva() {
    }

    public AccionPreventiva(String responsable, Date fecha, String accion) {
        this.responsable = responsable;
        this.fecha = fecha;
        this.accion = accion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }


    public String getFechaString() {
        return this.fechaString;
    }

    public void setFechaString() {
        if (this.fecha != null) {
            this.fechaString = Generic.dateFormatterMySql.format(this.fecha);
        } else {
            this.fechaString = new String();
        }
    }

    public void setFechaString(String fecha) {
        this.fechaString = fecha;
    }

    public Date getFechaFinalizacion() {
        return fechaFinalizacion;
    }

    public void setFechaFinalizacion(Date fechaFinalizacion) {
        this.fechaFinalizacion = fechaFinalizacion;
    }

    public String getFechaFinalizacionString() {
        return fechaFinalizacionString;
    }

    public void setFechaFinalizacionString(String fechaFinalizacionString) {
        this.fechaFinalizacionString = fechaFinalizacionString;
    }

    public boolean isAccionHecha() {
        return accionHecha;
    }

    public void setAccionHecha(boolean accionHecha) {
        this.accionHecha = accionHecha;
    }
}
