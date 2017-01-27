package rinseg.asistp.com.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.github.clans.fab.FloatingActionButton;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rinseg.asistp.com.adapters.IncidenciaAdapter;
import rinseg.asistp.com.adapters.InspeccionAdapter;
import rinseg.asistp.com.adapters.RopAdapter;
import rinseg.asistp.com.listener.ListenerClick;
import rinseg.asistp.com.models.IncidenciaRO;
import rinseg.asistp.com.models.InspeccionRO;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.services.RestClient;
import rinseg.asistp.com.services.Services;
import rinseg.asistp.com.ui.activities.ActivityGenerarIncidencia;
import rinseg.asistp.com.ui.activities.ActivityMain;
import rinseg.asistp.com.utils.DialogLoading;
import rinseg.asistp.com.utils.DialogRINSEG;
import rinseg.asistp.com.utils.Generic;
import rinseg.asistp.com.utils.Messages;
import rinseg.asistp.com.utils.RinsegModule;

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

    public FragmentInspeccionNuevo4() { }

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

        listaIncidencias.add(new IncidenciaRO("nombre 1", "desc"));
        listaIncidencias.add(new IncidenciaRO("nombre 2", "descripcion"));
        listaIncidencias.add(new IncidenciaRO("nombre 3", "descripcion"));
        listaIncidencias.add(new IncidenciaRO("nombre 4", "desc"));

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
                showDialogConfirm();
            }
        });
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private void LoadInspeccion() {
        String tmpIdInsp = null;
        int id = 0;
        if (bundle != null) {
            tmpIdInsp = bundle.getString("InspTmpId", null);
            id = bundle.getInt("InspId", 0);

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
        listaIncidencias.addAll(mInspc.listaIncidencias);
        incidenciaAdapter.notifyDataSetChanged();
    }

    private void showDialogConfirm() {
        dialogConfirm = new DialogRINSEG(activityMain);
        //dialogConfirm.setContentView(R.layout.dialog_terminar_inspeccion);
        dialogConfirm.show();
        dialogConfirm.setTitle("INSPECCIÓN FINALIZADA");
        dialogConfirm.setBody("¿Está seguro de finalizar la inspección? \n" +
                "Una vez finalizada se enviará hacia el servidor y no podrá modificarse");
        dialogConfirm.setTextBtnAceptar("FINALIZAR");

        dialogConfirm.btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveInspection();
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
            //mInspc.setDateCloseString(Generic.dateFormatterMySql.format(mInspc.getDateClose()));
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            realm.close();
        } finally {
            realm.close();
            sendInspection();
        }
    }

    /** Módulo para enviar la inspección al servidor */
    private void sendInspection() {
        // Obtenemos el token :
        //String token = activityMain.usuarioLogueado.getApi_token();
        String token = "ugeDBS95yQGxLIdGw9lX30g02BGkew3chqj4MlbVE554ruIkJrz33BaPDpds";

        // Mostramos dialog mientras se procese :
        final DialogLoading dialog = new DialogLoading(activityMain);
        dialog.show();

        /*Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
                String json = gson.toJson(mInspc);
                Log.d("object", json);
            }
        });*/

        Realm realm = Realm.getInstance(myConfig);
        final InspeccionRO inspectionToSend = realm.copyFromRealm(mInspc);

        /*Gson gson = new Gson();
        String json = gson.toJson(mInspc);

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);*/

        RestClient restClient = new RestClient(Services.INSPECTION);
        Call<ResponseBody> call = restClient.iServices.sendInspection(inspectionToSend, token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if ( response.isSuccessful() ) {
                    // Intentaremos castear la respuesta :
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        String status = jsonObject.getString("status");

                        Log.e("jsonObject", jsonObject.toString());
                        dialog.dismiss();

                    } catch (Exception e) {
                        dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    dialog.dismiss();
                    Messages.showSB(
                            getView(), getString(R.string.msg_error_guardar_inspeccion), "ok");
                }
                Log.e("TAG_OnResponse", response.errorBody() + " - " +
                        response.message() + "code :" + response.code());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialog.dismiss();
                t.printStackTrace();
            }
        });
    }
}
