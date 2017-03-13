package rinseg.asistp.com.models;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by Carlos Ramos on 28/10/2016.
 * Tipos de acto o condición sub-estándar
 * Congestión o acción restringida - Sistemas de advertencia - Peligro...
 */
public class EventItemsFix extends RealmObject {

    @SerializedName("id")
    private int Id;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }


}
