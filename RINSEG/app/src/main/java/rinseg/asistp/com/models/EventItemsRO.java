package rinseg.asistp.com.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by Carlos Ramos on 28/10/2016.
 * Tipos de acto o condición sub-estándar
 * Congestión o acción restringida - Sistemas de advertencia - Peligro...
 */
public class EventItemsRO  extends RealmObject {

    @SerializedName("id")
    private int Id;
    @SerializedName("code")
    private String Code;
    @SerializedName("name")
    private String Name;
    @SerializedName("checked")
    private Boolean checked;


    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public boolean isChecked() {return checked;}

    public void setChecked(Boolean checked) {this.checked = checked;}

    @Override
    public String toString() {
        return this.Name; // Value to be displayed in the Spinner
    }

}
