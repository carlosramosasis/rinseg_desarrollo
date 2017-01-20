package rinseg.asistp.com.models;

import io.realm.RealmObject;

/**
 * Created by Carlos Ramos on 26/10/2016.
 * Clase Realm de frecuencias
 */
public class FrecuencieRO extends RealmObject {

    private int id ;
    private String displayName;
    private int value;

    public int getId() {
        return id;
    }

    public void setId(int _id) {
        id = _id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String _displayName) {
        displayName = _displayName;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.displayName; // Value to be displayed in the Spinner
    }
}
