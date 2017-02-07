package rinseg.asistp.com.intent_services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rinseg.asistp.com.models.ImagenRO;
import rinseg.asistp.com.models.IncidenciaRO;
import rinseg.asistp.com.models.InspeccionRO;
import rinseg.asistp.com.models.ROP;
import rinseg.asistp.com.models.User;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.services.RestClient;
import rinseg.asistp.com.services.Services;
import rinseg.asistp.com.utils.Constants;
import rinseg.asistp.com.utils.Messages;
import rinseg.asistp.com.utils.RinsegModule;

/**
 * Created by Carlos Ramos on 16/12/2016.
 */
public class InspeccionIntentServices extends IntentService {
    InspeccionIntentServices thiss = this;
    RealmConfiguration myConfig;
    User usuParaToken;


    public InspeccionIntentServices() {
        super("inspeccion_intent_services");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            Boolean intentActivo = preferences.getBoolean(Constants.KEY_INTENT_SERV_ACTIVO_INSPECCION, false);
            if (!intentActivo) {
                preferences.edit().putBoolean(Constants.KEY_INTENT_SERV_ACTIVO_INSPECCION, true).commit();
                Log.e("intent", "esto viene del intent INSPECCION");

                //configuramos Realm
                Realm.init(this.getApplicationContext());
                myConfig = new RealmConfiguration.Builder()
                        .name("rinseg.realm")
                        .schemaVersion(2)
                        .modules(new RinsegModule())
                        .deleteRealmIfMigrationNeeded()
                        .build();

                Realm realm = Realm.getInstance(myConfig);

                User user = realm.where(User.class).findFirst();
                if (user != null) {
                    usuParaToken = realm.copyFromRealm(user);
                } else {
                    TerminaProceso();
                    return;
                }

                RealmResults<InspeccionRO> inspeccionesCerradasNoEnviadas = realm.where(InspeccionRO.class).equalTo("cerrado", true).equalTo("id", 0).findAll();

                if (inspeccionesCerradasNoEnviadas.size() > 0) {
                    for (int y = 0; y < inspeccionesCerradasNoEnviadas.size(); y++) {
                        InspeccionRO inspeccion = inspeccionesCerradasNoEnviadas.get(y);
                        InspeccionRO inspeccionCopy = realm.copyFromRealm(inspeccion);
                        EnviarInspeccion(realm, inspeccionCopy, inspeccion);
                        Log.e("Inspeccion en INTENT", inspeccionCopy.toString());
                    }
                } else {
                    TerminaProceso();
                    return;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            TerminaProceso();
        } finally {

        }
    }


    void TerminaProceso() {
        Log.e("intent", "acabo el intent inspeccion");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().putBoolean(Constants.KEY_INTENT_SERV_ACTIVO_INSPECCION, false).commit();
    }

    void EnviarInspeccion(final Realm realm, final InspeccionRO inspeccionCopy, final InspeccionRO inspeccionOriginal) {

        final String token = usuParaToken.getApi_token();

        //Asignar ids temporal segun orden a Incidente(inspection_items),
        // (asi lo requiere el sevicio web)
        int cantImagenesTotal = 0;
        for (int i = 0; i < inspeccionCopy.listaIncidencias.size(); i++) {
            inspeccionCopy.listaIncidencias.get(i).setId(i);
            // asignamos el id temporal del incidente a sus respectivas imagenes
            cantImagenesTotal += inspeccionCopy.listaIncidencias.get(i).listaImgComent.size();
            for (int j = 0; j < inspeccionCopy.listaIncidencias.get(i).listaImgComent.size(); j++) {
                inspeccionCopy.listaIncidencias.get(i).listaImgComent.get(j).setIdParent(i);
            }
        }

        RestClient restClient = new RestClient(Services.INSPECTION);

        Call<ResponseBody> call = restClient.iServices.sendInspection(inspeccionCopy, token);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                if (response.isSuccessful()) {
                    try {
                        // Intentaremos castear la respuesta :
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONObject inspeccionResult = jsonObject.getJSONObject("message")
                                .getJSONObject("inspection");
                        JSONArray listaIncidentes = jsonObject.getJSONObject("message")
                                .getJSONArray("inspection_items");

                        // Actualizar el registro en Realm :
                        updateLocalInspection(inspeccionOriginal, inspeccionResult.getInt("id"));

                        // Actualizar incidentes en Realm :
                        updateLocalIncidentes(inspeccionOriginal, listaIncidentes);

                        InspeccionRO currentInspe = realm.copyFromRealm(inspeccionOriginal);

                        //Enviamos todas las imágenes :
                        for (IncidenciaRO incident : currentInspe.listaIncidencias) {
                            if (incident.listaImgComent.size() > 0) {
                                for (ImagenRO image : incident.listaImgComent) {
                                    sendImage(image, incident.getTmpId(), incident.getId(), token);
                                }
                                TerminaProceso();
                            } else {
                                TerminaProceso();
                            }
                        }

                        Log.e("jsonObject", jsonObject.toString());

                    } catch (Exception e) {
                        e.printStackTrace();
                        TerminaProceso();
                    }
                } else {
                    TerminaProceso();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                TerminaProceso();
            }

        });
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private void updateLocalInspection(InspeccionRO inspeccionOriginal, int id) {
        Realm realm = Realm.getInstance(myConfig);
        try {
            realm.beginTransaction();
            inspeccionOriginal.setId(id);
            inspeccionOriginal.setTmpId(String.valueOf(id));
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            realm.close();
        } finally {
            realm.close();
        }
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private void updateLocalIncidentes(InspeccionRO inspeccionOriginal, JSONArray arrayIncidentes) {
        Realm realm = Realm.getInstance(myConfig);
        try {
            realm.beginTransaction();
            for (int i = 0; i < arrayIncidentes.length(); i++) {
                JSONObject incidenteJson = arrayIncidentes.getJSONObject(i);
                inspeccionOriginal.listaIncidencias.get(i).setId(incidenteJson.getInt("id"));
            }
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            realm.close();
        } finally {
            realm.close();
        }
    }


    public class EnviarImagenRop extends AsyncTask<String, Integer, Integer> {
        private ImagenRO imagenRop;
        private String tmpIdRop;
        private int idRop;
        private String apitoken;


        EnviarImagenRop(ImagenRO imagen, String pTmpIdRop, int pIdRop, String mApiToken) {
            imagenRop = imagen;
            tmpIdRop = pTmpIdRop;
            idRop = pIdRop;
            apitoken = mApiToken;
        }

        @Override
        protected Integer doInBackground(String... strings) {
            int errorValue = 0;
            try {

                RestClient restClient = new RestClient(Services.URL_ROPS);

                File myDir = getApplicationContext().getFilesDir();
                File file = new File(myDir, Constants.PATH_IMAGE_GALERY_ROP + tmpIdRop + "/" + imagenRop.getName());

                // create RequestBody instance from file
                RequestBody requestFile =
                        RequestBody.create(MediaType.parse("multipart/form-data"), file);

                // MultipartBody.Part is used to send also the actual file name
                MultipartBody.Part body =
                        MultipartBody.Part.createFormData("file_image", file.getName(), requestFile);

                // add another part within the multipart request

                RequestBody description =
                        RequestBody.create(
                                MediaType.parse("multipart/form-data"), imagenRop.getDescripcion());
                RequestBody rop_id =
                        RequestBody.create(
                                MediaType.parse("multipart/form-data"), String.valueOf(idRop));


                Call<ResponseBody> call = restClient.iServices.setImageRop(rop_id, description, body, apitoken);

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        int code = response.code();
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                String status = jsonObject.getString("status");
                                if (status.equals(Constants.SUCCESS)) {
                                    JSONObject messageResult = jsonObject.getJSONObject("message");
                                    JSONObject ropImageResult = messageResult.getJSONObject("rop_image");

                                    Realm real = Realm.getInstance(myConfig);

                                    imagenRop = real.where(ImagenRO.class).equalTo("name", ropImageResult.getString("name")).findFirst();
                                    if (imagenRop != null) {
                                        real.beginTransaction();
                                        imagenRop.setId(ropImageResult.getInt("id"));
                                        real.commitTransaction();
                                    }

                                }

                                Log.e("jsonObject", jsonObject.toString());


                            } catch (Exception e) {
                                // dialogLoading.dismiss();
                                e.printStackTrace();
                                // Messages.showSB(getView(), getString(R.string.msg_error_guardar), "ok");
                            }


                        } else {
                            Log.e("imagen", response.message());
                            Log.e("imagen error", response.errorBody().toString());
                            //dialogLoading.dismiss();
                            //Messages.showSB(getView(), getString(R.string.msg_login_fail), "ok");
                        }

                        //cantImagenesEnviadas += 1;
                        //postExecute();

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        //cantImagenesEnviadas += 1;
                        //postExecute();
                        //dialogLoading.dismiss();
                        //Messages.showSB(getView(), getString(R.string.msg_servidor_inaccesible), "ok");
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
                //cantImagenesEnviadas += 1;
                //postExecute();
            }


            return errorValue;
        }


    }

    private void sendImage(ImagenRO imagenRO, String tmpId, int idIncident, String api_token) {

        File myDir = getApplicationContext().getFilesDir();
        File file = new File(myDir, Constants.PATH_IMAGE_GALERY_INCIDENCIA + tmpId + "/"
                + imagenRO.getName());

        // Asignamos la imagen como Part
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file_image", file.getName(),
                requestFile);

        // Asignamos los campos como RequestBody :
        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"),
                imagenRO.getDescripcion());
        RequestBody incident_id = RequestBody.create(MediaType.parse("multipart/form-data"),
                String.valueOf(idIncident));

        RestClient restClient = new RestClient(Services.INSPECTION);

        Call<ResponseBody> call = restClient.iServices.addImageIncident(incident_id, description,
                body, api_token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try{
                        // Seteando el id de imagen :
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONObject messageResult = jsonObject.getJSONObject("message");
                        JSONObject imageResult = messageResult.getJSONObject("inspection_image");

                        Realm real = Realm.getInstance(myConfig);

                        ImagenRO imagenInc = real.where(ImagenRO.class).equalTo("name",
                                imageResult.getString("name")).findFirst();
                        if (imagenInc != null) {
                            real.beginTransaction();
                            imagenInc.setId(imageResult.getInt("id"));
                            real.commitTransaction();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
