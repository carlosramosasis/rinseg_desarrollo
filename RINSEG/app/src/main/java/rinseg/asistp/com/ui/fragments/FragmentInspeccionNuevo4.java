package rinseg.asistp.com.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.Sort;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rinseg.asistp.com.adapters.IncidenciaAdapter;
import rinseg.asistp.com.adapters.InspeccionAdapter;
import rinseg.asistp.com.adapters.RopAdapter;
import rinseg.asistp.com.listener.ListenerClick;
import rinseg.asistp.com.models.ImagenRO;
import rinseg.asistp.com.models.IncidenciaRO;
import rinseg.asistp.com.models.InspeccionRO;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.services.RestClient;
import rinseg.asistp.com.services.Services;
import rinseg.asistp.com.ui.activities.ActivityGenerarIncidencia;
import rinseg.asistp.com.ui.activities.ActivityMain;
import rinseg.asistp.com.utils.Constants;
import rinseg.asistp.com.utils.DialogLoading;
import rinseg.asistp.com.utils.DialogRINSEG;
import rinseg.asistp.com.utils.Generic;
import rinseg.asistp.com.utils.Messages;
import rinseg.asistp.com.utils.RinsegModule;
import rinseg.asistp.com.utils.SharedPreferencesHelper;

import static rinseg.asistp.com.utils.Constants.MY_SHARED_PREFERENCES;

public class FragmentInspeccionNuevo4 extends Fragment implements ListenerClick {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_INCIDENT = "idIncident";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerIncidencias;
    private RecyclerView.Adapter incidenciaAdapter;
    private List<IncidenciaRO> listaIncidencias = new ArrayList<>();

    private FloatingActionButton btnAgregarIncidente;

    DialogRINSEG dialogConfirm;

    ActivityMain activityMain;

    InspeccionRO mInspc;

    RealmConfiguration myConfig;
    Bundle bundle;

    int totalToSend, correctSend, failSend = 0;

    DialogLoading dialog;

    public FragmentInspeccionNuevo4() {
    }

    public static FragmentInspeccionNuevo4 newInstance(String param1, String param2) {
        FragmentInspeccionNuevo4 fragment = new FragmentInspeccionNuevo4();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inspeccion_nuevo4, container, false);
        setUpElements(view);
        setUpActions();
        LoadInspeccion();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        activityMain.toolbar.setTitle(R.string.title_incidentes);
        activityMain.ShowButtonsBottom(true);
        activityMain.actualPaginaRop = 4;
        activityMain.ShowNumPagina();
        LoadIncidencias();
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    /** Método sobre escrito para manejar el click sobre una incidencia */
    @Override
    public void onItemClicked(IncidenciaAdapter.IncidenciaViewHolder holder, int position) {
        launchActivityGenerarIncidencia(mInspc, listaIncidencias.get(position).getTmpId());
    }

    @Override
    public void onItemClicked(InspeccionAdapter.InspeccionViewHolder holder, int position) { }

    @Override
    public void onItemClicked(RopAdapter.RopViewHolder holder, int position) { }

    @Override
    public void onItemLongClicked(RopAdapter.RopViewHolder holder, int position) { }

    @Override
    public void onDestroyView() {
        activityMain.ButtonBottomSetDefault();
        activityMain.HideNumPagina();
        super.onDestroyView();
    }

    //Proceso para cargar las vistas
    private void setUpElements(View v) {
        activityMain = ((ActivityMain) getActivity());
        activityMain.btnRight.setText(R.string.btn_terminar);
        activityMain.btnRight.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);

        bundle = getArguments();

        btnAgregarIncidente = (FloatingActionButton) v.findViewById(R.id.btn_agregar_incidente);

        //configuracion para el recicler
        recyclerIncidencias = (RecyclerView) v.findViewById(R.id.recycler_view_i4_incidencias);
        recyclerIncidencias.setHasFixedSize(true);
        // usar administrador para linearLayout
        RecyclerView.LayoutManager lManager = new LinearLayoutManager(
                this.getActivity().getApplicationContext());
        recyclerIncidencias.setLayoutManager(lManager);
        // Crear un nuevo Adaptador
        incidenciaAdapter = new IncidenciaAdapter(listaIncidencias,
                activityMain.getApplicationContext(), this);
        recyclerIncidencias.setAdapter(incidenciaAdapter);

        //configuramos Realm
        Realm.init(this.getActivity().getApplicationContext());
        myConfig = new RealmConfiguration.Builder()
                .name("rinseg.realm")
                .schemaVersion(2)
                .modules(new RinsegModule())
                .deleteRealmIfMigrationNeeded()
                .build();
    }

    //cargamos los eventos
    private void setUpActions() {
        btnAgregarIncidente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivityGenerarIncidencia(mInspc, "");
            }
        });

        activityMain.btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fInspPendiente3 = new FragmentInspeccionNuevo3();
                Bundle args = new Bundle();
                args.putString("InspTmpId", mInspc.getTmpId());
                args.putInt("InspId", mInspc.getId());
                fInspPendiente3.setArguments(args);
                activityMain.replaceFragment(fInspPendiente3, true,
                        R.anim.enter_from_right, R.anim.exit_to_right,
                        R.anim.enter_from_left, R.anim.exit_to_left);
            }
        });

        // Evento para finalizar la inspección :
        activityMain.btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( mInspc.listaIncidencias != null && mInspc.listaIncidencias.size() > 0 ) {
                    showDialogConfirm();
                } else {
                    Messages.showSB(getView(), getString(R.string.msg_error_empty_incidents));
                }
            }
        });
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private void LoadInspeccion() {
        if (bundle != null) {
            String tmpIdInsp = bundle.getString("InspTmpId", null);
            int id = bundle.getInt("InspId", 0);

            final Realm realm = Realm.getInstance(myConfig);
            try {
                if (id != 0) {
                    mInspc = realm.where(InspeccionRO.class).equalTo("id", id).findFirst();
                } else if (tmpIdInsp != null) {
                    mInspc = realm.where(InspeccionRO.class).equalTo("tmpId", tmpIdInsp).findFirst();
                }
                if (mInspc == null) {
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                realm.close();
            } finally {
                realm.close();
            }
        }
    }

    public void launchActivityGenerarIncidencia(InspeccionRO insp, String idIncident) {
        Intent GenerarIncidenciaIntent = new Intent().setClass(
                activityMain, ActivityGenerarIncidencia.class);
        GenerarIncidenciaIntent.putExtra("InspId", insp.getId());
        GenerarIncidenciaIntent.putExtra("InspTmpId", insp.getTmpId());
        GenerarIncidenciaIntent.putExtra(ARG_INCIDENT, idIncident);
        startActivity(GenerarIncidenciaIntent);
    }

    private void LoadIncidencias() {
        listaIncidencias.clear();
        listaIncidencias.addAll(mInspc.listaIncidencias.sort("riesgo", Sort.DESCENDING));
        incidenciaAdapter.notifyDataSetChanged();
    }

    private void showDialogConfirm() {
        dialogConfirm = new DialogRINSEG(activityMain);
        dialogConfirm.show();
        dialogConfirm.setTitle("INSPECCIÓN FINALIZADA");
        dialogConfirm.setBody("¿Está seguro de finalizar la inspección? \n" +
                "Una vez finalizada se enviará hacia el servidor y no podrá modificarse.");
        dialogConfirm.setTextBtnAceptar("FINALIZAR");

        dialogConfirm.btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveInspection();
                sendInspection();
                dialogConfirm.dismiss();
            }
        });
    }

    /** Módulo para almacenar la inspección */
    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private void saveInspection() {
        Realm realm = Realm.getInstance(myConfig);
        try {
            realm.beginTransaction();
            Calendar currentDate = Calendar.getInstance();
            mInspc.setDateClose(currentDate.getTime());
            mInspc.setDateCloseString(Generic.dateFormatterMySql.format(mInspc.getDateClose()));
            mInspc.setCerrado(true);
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            realm.close();
        } finally {
            realm.close();
        }
    }

    /** Módulo para enviar la inspección al servidor */
    private void sendInspection() {
        // Obtenemos el token :
        SharedPreferencesHelper preferencesHelper = new SharedPreferencesHelper(
                activityMain.getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE));
        final String token = preferencesHelper.getToken();

        // Mostramos dialog mientras se procese :
        dialog = new DialogLoading(activityMain);
        dialog.show();

        final Realm realm = Realm.getInstance(myConfig);
        InspeccionRO inspectionToSend = realm.copyFromRealm(mInspc);

        // Asignar ids temporal segun orden a Incidente(inspection_items),
        // (asi lo requiere el sevicio web)
        for (int i = 0; i < inspectionToSend.listaIncidencias.size(); i++) {
            inspectionToSend.listaIncidencias.get(i).setId(i);
            // asignamos el id temporal del incidente a sus respectivas imagenes
            totalToSend += inspectionToSend.listaIncidencias.get(i).listaImgComent.size();
            for (int j = 0; j < inspectionToSend.listaIncidencias.get(i).listaImgComent.size(); j++ ) {
                inspectionToSend.listaIncidencias.get(i).listaImgComent.get(j).setIdParent(i);
            }
        }

        RestClient restClient = new RestClient(Services.INSPECTION);

        Call<ResponseBody> call = restClient.iServices.sendInspection(inspectionToSend, token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if ( response.isSuccessful() ) {
                    // Mostramos dialog de éxito :
                    try {
                        // Intentaremos castear la respuesta :
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONObject inspeccionResult = jsonObject.getJSONObject("message")
                                .getJSONObject("inspection");
                        JSONArray listaIncidentes = jsonObject.getJSONObject("message")
                                .getJSONArray("inspection_items");

                        // Actualizar el registro en Realm :
                        updateLocalInspection(inspeccionResult.getInt("id"));

                        // Actualizar incidentes en Realm :
                        updateLocalIncidentes(listaIncidentes);

                        InspeccionRO currentInspe = realm.copyFromRealm(mInspc);

                        // Enviamos todas las imágenes :
                        for ( IncidenciaRO incident : currentInspe.listaIncidencias ) {
                            if ( incident.listaImgComent != null &&
                                    incident.listaImgComent.size() > 0 ) {
                                for ( ImagenRO image : incident.listaImgComent ) {
                                    sendImage(image, incident.getTmpId(), incident.getId(), token);
                                }
                            } else {
                                dialog.dismiss();
                                showDialogSuccess(getString(R.string.msg_success_send_inspection));
                            }
                        }
                    } catch (Exception e) {
                        dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    dialog.dismiss();
                    Messages.showSB(getView(), getString(R.string.msg_error_guardar_inspeccion));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialog.dismiss();
                t.printStackTrace();
            }
        });
    }

    private void showDialogSuccess(String message) {
        dialogConfirm = new DialogRINSEG(activityMain);
        dialogConfirm.show();
        dialogConfirm.setTitle("INSPECCIÓN FINALIZADA");
        dialogConfirm.setBody(message);
        dialogConfirm.setTextBtnAceptar("DE ACUERDO");
        dialogConfirm.btnCancelar.setVisibility(View.INVISIBLE);
        dialogConfirm.btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogConfirm.dismiss();
                activityMain.replaceFragment(new FragmentInspecciones(), true, 0, 0, 0, 0);
            }
        });
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private void updateLocalInspection(int id) {
        Realm realm = Realm.getInstance(myConfig);
        try {
            realm.beginTransaction();
            mInspc.setId(id);
            mInspc.setTmpId(String.valueOf(id));
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            realm.close();
        } finally {
            realm.close();
        }
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private void updateLocalIncidentes(JSONArray arrayIncidentes) {
        Realm realm = Realm.getInstance(myConfig);
        try {
            realm.beginTransaction();
            for ( int i = 0; i < arrayIncidentes.length(); i++ ) {
                JSONObject incidenteJson = arrayIncidentes.getJSONObject(i);

                String oldFolder = String.valueOf(mInspc.listaIncidencias.get(i).getTmpId());
                mInspc.listaIncidencias.get(i).setId(incidenteJson.getInt("id"));
                mInspc.listaIncidencias.get(i).setTmpId(String.valueOf(incidenteJson.getInt("id")));
                Generic.CambiarNombreCarpetaImageens(activityMain, Constants.PATH_IMAGE_GALERY_INCIDENCIA, oldFolder, mInspc.listaIncidencias.get(i).getTmpId());

            }
            realm.commitTransaction();
        } catch ( Exception e ) {
            e.printStackTrace();
            realm.close();
        } finally {
            realm.close();
        }
    }

    /** Módulo para enviar la imagen de incidencia */
    private void sendImage(ImagenRO imagenRO, String tmpId, int idIncident, String api_token) {

        File myDir = getActivity().getApplicationContext().getFilesDir();
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
                if ( response.isSuccessful() ) {
                    correctSend++;
                    try {
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
                        if ( correctSend == totalToSend ) {
                            dialog.dismiss();
                            showDialogSuccess(getString(R.string.msg_success_send_inspection));
                        } else {
                            if ( correctSend + failSend == totalToSend ) {
                                dialog.dismiss();
                                showDialogSuccess(getString(
                                        R.string.msg_success_send_inspe_error_image));
                            }
                        }
                    }
                    catch ( Exception e ) {
                        e.printStackTrace();
                        if ( correctSend + failSend == totalToSend ) {
                            dialog.dismiss();
                            showDialogSuccess(getString(R.string.msg_success_send_inspe_error_image));
                        }
                    }
                } else {
                    failSend++;
                    if ( correctSend + failSend == totalToSend ) {
                        dialog.dismiss();
                        showDialogSuccess(getString(R.string.msg_success_send_inspe_error_image));
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                failSend++;
                if ( correctSend + failSend == totalToSend ) {
                    dialog.dismiss();
                    showDialogSuccess(getString(R.string.msg_success_send_inspe_error_image));
                }
            }
        });
    }
}