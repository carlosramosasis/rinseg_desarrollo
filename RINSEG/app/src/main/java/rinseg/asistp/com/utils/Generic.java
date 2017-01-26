package rinseg.asistp.com.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.NetworkOnMainThreadException;
import android.provider.MediaStore;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.security.SecureRandom;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Random;

import rinseg.asistp.com.models.FotoModel;

/**
 * Created by Usuario on 05/10/2016.
 */
public class Generic {
    public static SimpleDateFormat dateFormatterMySql = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    public static SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

    public static SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a", Locale.US);

    public static String Mayus(String param) {
        String resu = "";
        resu = param.substring(0, 1).toUpperCase() + param.substring(1);
        return resu;
    }

    public static String FormatTmpId(int id) {
        String resu = "";
        resu = String.format("tmp%06d", id);
        return resu;
    }


    //Validar Acceso a la red
    public static boolean IsOnRed(Activity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isAvailable() && activeNetworkInfo.isConnected();
    }

    public static boolean isOnline() {
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal == 0);
            return reachable;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static boolean IsJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }


    public static boolean CrearCarpetaImagenesPorRop(Context context, String carpertaRop) {
        boolean result = false;

        File myDir = context.getFilesDir();
        File folder = new File(myDir, Constants.PATH_IMAGE_GALERY_ROP + carpertaRop + "/");

        if (!folder.exists()) {
            result = folder.mkdirs();
        }
        Log.e("resultSaveImageGalery", String.valueOf(result));

        return result;
    }

    public static boolean CrearCarpetaImagenesPorIncidencia(Context context, String carpertaIncidencia) {
        boolean result = false;

        File myDir = context.getFilesDir();
        File folder = new File(myDir, Constants.PATH_IMAGE_GALERY_INCIDENCIA + carpertaIncidencia + "/");

        if (!folder.exists()) {
            result = folder.mkdirs();
        }
        Log.e("resultSaveImageGalery", String.valueOf(result));

        return result;
    }

    public static boolean GuardarImagenCarpeta(Context context, String pathImageGallery, String carperta, Bitmap bmp, String nombreImagen) {
        boolean result = false;

        File myDir = context.getFilesDir();
        File path = new File(myDir, pathImageGallery + carperta + "/" + nombreImagen + ".jpg");

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            bmp.compress(Bitmap.CompressFormat.JPEG, 11, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static FotoModel DevolverImagendeCarpeta(File fcarpetaPrincipalApp, String pathImageGallery, String carpertaRop, String nombreImagen) {
        FotoModel result = new FotoModel();

        try {
            File folderRop = new File(fcarpetaPrincipalApp, pathImageGallery + carpertaRop + "/");
            if (folderRop.exists()) {
                File[] files = folderRop.listFiles();
                for (int i = 0; i < files.length; i++) {
                    Log.e("getName", files[i].getName());
                    if (files[i].getName().equals(nombreImagen)) {
                        //result.bitmap = BitmapFactory.decodeFile(files[i].getAbsolutePath());
                        //result.bitmap = null;

                        // nombreImagen = nombreImagen.replaceFirst("[.][^.]+$", "");


                        result.uri = Uri.parse(folderRop + "/" + nombreImagen);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    public static boolean EliminarImagenCarpeta(Context context, String carpertaRop) {
        boolean result = false;

        File myDir = context.getFilesDir();
        File myDirGaleria = new File(myDir, Constants.PATH_IMAGE_GALERY_ROP + carpertaRop);

        try {

            if (myDirGaleria.isDirectory()) {
                myDirGaleria.delete();
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }


        return result;
    }


    public static int CantidadImagenesPorRop(Context context, String carpertaRop) {
        boolean existeCarpeta = false;
        int resu = 0;

        File myDir = context.getFilesDir();
        File folder = new File(myDir, Constants.PATH_IMAGE_GALERY_ROP + carpertaRop + "/");


        if (folder.exists()) {
            File[] files = folder.listFiles();
            for (int i = 0; i < files.length; i++) {
                Log.d("Files", "FileName:" + files[i].getName());
                resu += 1;
            }
        }
        return resu;
    }

    public static int CantidadImagenesPorIncidente(Context context, String carpertaIncidente) {
        boolean existeCarpeta = false;
        int resu = 0;

        File myDir = context.getFilesDir();
        File folder = new File(myDir, Constants.PATH_IMAGE_GALERY_INCIDENCIA + carpertaIncidente + "/");


        if (folder.exists()) {
            File[] files = folder.listFiles();
            for (int i = 0; i < files.length; i++) {
                Log.d("Files", "FileName:" + files[i].getName());
                resu += 1;
            }
        }
        return resu;
    }

    public static String randomString(char[] characterSet, int length) {
        Random random = new SecureRandom();
        char[] result = new char[length];
        for (int i = 0; i < result.length; i++) {
            // picks a random index out of character set > random character
            int randomCharIndex = random.nextInt(characterSet.length);
            result[i] = characterSet[randomCharIndex];
        }
        return new String(result);
    }

    public static String RutaPdfRop(Context context, String nombrePdf) {
        String resu = "";

        File f = new File(context.getCacheDir() + "/" + nombrePdf);
        if(f.exists()){
            f.delete();
        }

         try {

            InputStream is = context.getAssets().open(nombrePdf);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            FileOutputStream fos = new FileOutputStream(f);
            fos.write(buffer);
            fos.close();

            resu = f.getPath();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        return resu;
    }

    //target to save
    public static Target getTarget(final String url){
        Target target = new Target(){

            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        File file = new File( url);
                        try {
                            file.createNewFile();
                            FileOutputStream ostream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, ostream);
                            ostream.flush();
                            ostream.close();
                        } catch (IOException e) {
                            Log.e("IOException", e.getLocalizedMessage());
                        }
                    }
                }).start();

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Log.e("load","load ok");
            }
        };
        return target;
    }

}


