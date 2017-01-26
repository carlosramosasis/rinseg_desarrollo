package rinseg.asistp.com.models;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by Carlos Ramos on 09/01/2017.
 * Clase Inpsector
 */

public class InspectorRO extends RealmObject {

    private int id;

    private String name;

    private String dni;

    @SerializedName("management_id")
    private int managementId;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public int getManagementId() {
        return managementId;
    }

    public void setManagementId(int managementId) {
        this.managementId = managementId;
    }

    @Override
    public String toString() {
        return this.name; // Value to be displayed in the Spinner
    }

}
