package rinseg.asistp.com.services;

import android.app.Service;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import rinseg.asistp.com.models.AccionPreventiva;
import rinseg.asistp.com.models.IncidenciaLevantadaRO;
import rinseg.asistp.com.models.InspeccionRO;
import rinseg.asistp.com.models.ROP;

/**
 * Created by Carlos Ramos on 20/10/2016.
 * Interface de métodos REST
 */

public interface IServices {

    @FormUrlEncoded
    @POST(Services.LOGIN)
    Call<ResponseBody> setLogin(@Field("username") String usu, @Field("password") String pass);

    @GET(Services.SYNC)
    Call<ResponseBody> getSync(@Query("api_token") String api_token);

    @POST(Services.ROP_CLOSE)
    Call<ResponseBody> setRopCerrado(@Body ROP rop, @Query("api_token") String api_token);

    @Multipart
    @POST(Services.ADD_IMAGEN)
    Call<ResponseBody> setImageRop(
                              @Part("rop_id") RequestBody rop_id,
                              @Part("description") RequestBody description,
                              @Part MultipartBody.Part file_image,
                              @Query("api_token") String api_token
    );

    @FormUrlEncoded
    @POST(Services.ROP_RECOVERY)
    Call<ResponseBody> getRopClosed(@Field("code") int id_rop, @Query("api_token") String api_token);

    @POST(Services.LOGOUT)
    Call<ResponseBody> setLogout(@Query("api_token") String api_token);


    @POST(Services.FIX_ACTONS)
    Call<ResponseBody> setFixActions(@Body AccionPreventiva accion,
                                     @Query("api_token") String api_token);

    @POST(Services.CLOSE)
    Call<ResponseBody> sendInspection(@Body InspeccionRO incidencia,
                                  @Query("api_token") String token);

    @POST(Services.FIX_INCIDENT)
    Call<ResponseBody> setFixIncident(@Body IncidenciaLevantadaRO icidencia,
                                      @Query("api_token") String api_token);

    @GET(Services.PDF)
    Call<ResponseBody> downloadInspecPDF(@Query("id") int id_ins, @Query("api_token") String token);

    @GET(Services.PDF)
    Call<ResponseBody> downloadRopPDF(@Query("id") int id_rop, @Query("api_token") String token);

    @Multipart
    @POST(Services.ADD_INS_IMAGEN)
    Call<ResponseBody> addImageIncident(@Part("inspection_item_id") RequestBody incident_id,
                                        @Part("description") RequestBody description,
                                        @Part MultipartBody.Part file_image,
                                        @Query("api_token") String api_token
    );
}
