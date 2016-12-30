package rinseg.asistp.com.models;

import android.graphics.Bitmap;
import android.net.Uri;

import io.realm.RealmObject;

/**
 * Created by Carlos Ramos on 28/10/2016.
 */
public class ImagenRO extends RealmObject{

    private int id;

    private String name;

    private String descripcion;

    private String path;




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


}
