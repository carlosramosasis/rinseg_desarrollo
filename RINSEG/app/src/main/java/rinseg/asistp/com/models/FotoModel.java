package rinseg.asistp.com.models;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.Serializable;

/**
 * Created by Usuario on 06/12/2016.
 */
public class FotoModel implements Serializable {
    public static  Uri uri;
    //public static  Bitmap bitmap;

    @Override
    public String toString() {
        return "FotoModel{"+uri+"}";
    }
}
