package rinseg.asistp.com.utils;

/**
 * Created by OSequeiros on 01/02/2017.
 * Interface helper para las shared preferences
 */

public interface ISharedPreferencesHelper {

    void saveToken(String token);

    String getToken();

    void clear();
}
