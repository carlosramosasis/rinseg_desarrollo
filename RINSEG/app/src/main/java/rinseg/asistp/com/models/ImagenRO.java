package rinseg.asistp.com.models;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import io.realm.RealmObject;

/**
 * Created by Carlos Ramos on 28/10/2016.
 */
public class ImagenRO extends RealmObject {

    private int id;

    private String name;

    private String descripcion;

    private String path;

    private int idParent;

    private boolean accionLevantada;

    private boolean inspeccionLevantada;


    public String getName() {
        return name;
    }

    public void setName(String _name) {
        name = _name;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getIdParent() {
        return idParent;
    }

    public void setIdParent(int idParent) {
        this.idParent = idParent;
    }

    public boolean isInspeccionLevantada() {return inspeccionLevantada;}

    public void setInspeccionLevantada(boolean inspeccionLevantada) {
        this.inspeccionLevantada = inspeccionLevantada;
    }

    public boolean isAccionLevantada() {return accionLevantada;}

    public void setAccionLevantada(boolean accionLevantada) {this.accionLevantada = accionLevantada;}

    public void setValues(JSONObject json) {
        try {
            setId(json.getInt("id"));
            setName(json.getString("name"));
            setDescripcion(json.getString("description"));
            setPath(json.getString("path"));
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}
