package rinseg.asistp.com.models;

import io.realm.RealmObject;

/**
 * Created by Carlos Ramos on 28/10/2016.
 * Tipo de inspección:
 * Inopinada
 */

public class TypesRO extends RealmObject{
    private int id ;
    private String displayName;

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

    @Override
    public String toString() {
        return this.displayName; // Value to be displayed in the Spinner
    }
}
