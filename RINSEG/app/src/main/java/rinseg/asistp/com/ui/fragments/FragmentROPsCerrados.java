package rinseg.asistp.com.ui.fragments;

import android.app.Dialog;
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
import android.widget.Button;
import android.widget.EditText;

import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rinseg.asistp.com.adapters.IncidenciaAdapter;
import rinseg.asistp.com.adapters.InspeccionAdapter;
import rinseg.asistp.com.listener.ListenerClick;
import rinseg.asistp.com.models.CompanyRO;
import rinseg.asistp.com.models.FrecuencieRO;
import rinseg.asistp.com.models.Inspeccion;
import rinseg.asistp.com.models.ROP;
import rinseg.asistp.com.models.SettingsInspectionRO;
import rinseg.asistp.com.models.SettingsRopRO;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.services.RestClient;
import rinseg.asistp.com.services.Services;
import rinseg.asistp.com.ui.activities.ActivityInspeccionDetalle;
import rinseg.asistp.com.ui.activities.ActivityMain;
import rinseg.asistp.com.adapters.RopAdapter;
import rinseg.asistp.com.ui.activities.ActivityRopCerradoDetalle;
import rinseg.asistp.com.utils.DialogLoading;
import rinseg.asistp.com.utils.Generic;
import rinseg.asistp.com.utils.Messages;
import rinseg.asistp.com.utils.RinsegModule;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentROPsCerrados.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentROPsCerrados#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentROPsCerrados extends Fragment implements ListenerClick {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private OnFragmentInteractionListener mListener;


    private FloatingActionButton fab;
    ActivityMain activityMain ;

    private DialogLoading dialogLoading;

    private RecyclerView recyclerRops;
    private RecyclerView.Adapter ropAdapter;
    private RecyclerView.LayoutManager lManager;
    private List<ROP> listaRops = new ArrayList<>();

    Dialog recuperaRopDialog;
    Button btnRecuperaRop;
    Button btnCancelarRecuperaRop;
    EditText txtCodigoRecuperar;

    RealmConfiguration myConfig;

    public FragmentROPsCerrados() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentROPsPendientes.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentROPsCerrados newInstance(String param1, String param2) {
        FragmentROPsCerrados fragment = new FragmentROPsCerrados();
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
        View view =  inflater.inflate(R.layout.fragment_rops_cerrados, container, false);

        setUpElements(view);
        setUpActions();

        LoadRopCerrados();

        return view;

    }

    //Proceso para cargar las vistas
    private void setUpElements(View v) {
        fab = (FloatingActionButton) v.findViewById(R.id.btn_recupera_rop);
        activityMain = ((ActivityMain) getActivity());

        //instanciamos el dialog para recuperar rop
        recuperaRopDialog = new Dialog(this.getContext(),R.style.CustomDialogTheme);

        //configuracion para el recicler
        recyclerRops = (RecyclerView) v.findViewById(R.id.recycler_view_rops_cerrados);
        recyclerRops.setHasFixedSize(true);
        // usar administrador para linearLayout
        lManager =  new LinearLayoutManager(this.getActivity().getApplicationContext());
        recyclerRops.setLayoutManager(lManager);
        // Crear un nuevo Adaptador
        ropAdapter = new RopAdapter(listaRops,activityMain.getApplicationContext(),this);
        recyclerRops.setAdapter(ropAdapter);

        //configuramos Realm
        Realm.init(this.getActivity().getApplicationContext());
        myConfig = new RealmConfiguration.Builder()
                .name("rinseg.realm")
                .schemaVersion(2)
                .modules(new RinsegModule())
                .deleteRealmIfMigrationNeeded()
                .build();
    }

    private void setUpActions() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recuperaRopDialog.show();
                recuperaRopDialog.setContentView(R.layout.dialog_recupera_rop);

                btnRecuperaRop = (Button) recuperaRopDialog.findViewById(R.id.btn_dialog_recupera_recuperar);
                btnCancelarRecuperaRop = (Button) recuperaRopDialog.findViewById(R.id.btn_dialog_recupera_cancelar);
                txtCodigoRecuperar =(EditText) recuperaRopDialog.findViewById(R.id.txt_dialog_codigo_recuperar);

                btnRecuperaRop.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        txtCodigoRecuperar.setText("REcuperado");
                    }
                });

                btnCancelarRecuperaRop.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        recuperaRopDialog.dismiss();
                    }
                });

            }
        });
    }



    @Override
    public void onResume(){
        super.onResume();
        activityMain.ShowButtonsBottom(false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

/*    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClicked(RopAdapter.RopViewHolder holder, int position) {

        launchActivityRopDetalle(listaRops.get(position));
    }

    @Override
    public void onItemClicked(IncidenciaAdapter.IncidenciaViewHolder holder, int position) {

    }
    @Override
    public void onItemClicked(InspeccionAdapter.InspeccionViewHolder holder, int position) {

    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    ///TODO ::::::::::::::::::::::::::::::::::::::::::: METODOS ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    private void LoadRopCerrados() {
        Realm realm = Realm.getInstance(myConfig);
        try {
            RealmResults<ROP> RopsRealm = realm.where(ROP.class).equalTo("cerrado",true).findAll().sort("dateClose", Sort.DESCENDING);

            for (int i = 0; i < RopsRealm.size(); i++) {
                ROP tRop = RopsRealm.get(i);
                listaRops.add(tRop);
                ropAdapter.notifyDataSetChanged();
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            realm.close();
        }
    }

    /**
     * Created on 21/12/16
     * Módulo encargado de lanzar la actividad
     */
    public void launchActivityRopDetalle(ROP rop) {

        Intent RopDetalleIntent = new Intent().setClass(activityMain, ActivityRopCerradoDetalle.class);
        RopDetalleIntent.putExtra("ROPtmpId", rop.getTmpId());
        RopDetalleIntent.putExtra("ROPId",rop.getId());
        startActivity(RopDetalleIntent );
    }

  /*  public void RecuperarRopCerrado(int idRop) {
        View parentLAyout = getView().findViewById(R.id.frame_rop_cerrados_content);

        if (!Generic.IsOnRed(activityMain)) {
            Messages.showSB(parentLAyout, getString(R.string.no_internet), getString(R.string.ok));
            return;
        }

        dialogLoading.show();


        RestClient restClient = new RestClient(Services.URL_ROPS);
        Call<ResponseBody> call = restClient.iServices.getRopClosed(Integer.parseInt(txtCodigoRecuperar.getText().toString().trim()), activityMain.usuarioLogueado.getApi_token());

        call.enqueue(new Callback<ResponseBody>() {
            View rootLayout = activityMain.findViewById(R.id.coordinator_activity_main);

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Realm realm = Realm.getInstance(myConfig);
                    try {
                        View parentLAyout = findViewById(R.id.layout_content_main);

                        int code = response.code();
                        String body = response.body().string();

                        if (body.charAt(0) != '{') {
                            Messages.showSB(parentLAyout, getString(R.string.sincronizando_error), getString(R.string.ok));
                            return;
                        }

                        JSONObject jsonObject = new JSONObject(body);

                        ////////////////////////////Inspections//////////////////////////////////////////////
                        // recuperamos la configuracion inspeccions del resultado recibido del WS
                        JSONObject inspectionsJSON = jsonObject.getJSONObject("inspections");
                        JSONArray iCompaniesJSON = inspectionsJSON.getJSONArray("companies");
                        JSONArray iFecuenciesJSON = inspectionsJSON.getJSONArray("frequencies");
                        JSONArray iSeveritiesJSON = inspectionsJSON.getJSONArray("severities");
                        JSONArray iRisksJSON = inspectionsJSON.getJSONArray("risks");
                        JSONArray iEventsJSON = inspectionsJSON.getJSONArray("event_items");
                        JSONArray iManagementsJSON = inspectionsJSON.getJSONArray("managements");
                        JSONArray iTargentsJSON = inspectionsJSON.getJSONArray("targets");
                        JSONArray iTypesJSON = inspectionsJSON.getJSONArray("types");
                        JSONArray iRacsJSON = inspectionsJSON.getJSONArray("racs");

                        // creamos una SettingsInspectionRO para almacenar los valores para seleccion en los formularios en nuestra BD local
                        realm.beginTransaction();
                        SettingsInspectionRO inspections = realm.createObject(SettingsInspectionRO.class);
                        // poblamos las tablas para la inspeccion
                        PopulateCompaniesForInspection(inspections, iCompaniesJSON, realm);
                        PopulateFrecuenciesForInspection(inspections, iFecuenciesJSON, realm);
                        PopulateSeveritiesForInspection(inspections, iSeveritiesJSON, realm);
                        PopulateRisksForInspection(inspections, iRisksJSON, realm);
                        PopulateEventsForInspection(inspections, iEventsJSON, realm);
                        PopulateManagementsForInspection(inspections, iManagementsJSON, realm);
                        PopulateTargetsForInspection(inspections, iTargentsJSON, realm);
                        PopulateTypesForInspection(inspections, iTypesJSON, realm);
                        PopulateRacsForInspection(inspections, iRacsJSON, realm);

                        ////////////////////////////ROPS//////////////////////////////////////////////
                        // recuperamos el la configuracion ROP del resultado recibido del WS
                        JSONObject ropJSON = jsonObject.getJSONObject("rops");
                        JSONArray rCompaniesJSON = ropJSON.getJSONArray("companies");
                        JSONArray rRisksJSON = ropJSON.getJSONArray("risks");
                        JSONArray rEventsJSON = ropJSON.getJSONArray("event_items");
                        JSONArray rTargetsJSON = ropJSON.getJSONArray("targets");
                        JSONArray rAreasJSON = ropJSON.getJSONArray("areas");
                        // creamos un SettingsRopRO para almacenar los valores para seleccion en los formularios en nuestra BD local
                        SettingsRopRO rops = realm.createObject(SettingsRopRO.class);
                        // poblamos las tablas para el ROP
                        PopulateCompaniesForROP(rops, rCompaniesJSON, realm);
                        PopulateRisksForROP(rops, rRisksJSON, realm);
                        PopulateEventsForROP(rops, rEventsJSON, realm);
                        PopulateTargetsForROP(rops, rTargetsJSON, realm);
                        PopulateAreasForROP(rops, rAreasJSON, realm);

                        //////////////////////////////////Settings///////////////////////////////////////
                        JSONObject settingsJSON = jsonObject.getJSONObject("settings");
                        PopulateSettings(rops, settingsJSON);


                        realm.commitTransaction();

                        *//*settingInsp = realm.where(SettingsInspectionRO.class).findFirst();
                        settingRop = realm.where(SettingsRopRO.class).findFirst();*//*
                        settingInsp = realm.copyFromRealm(inspections);
                        settingRop = realm.copyFromRealm(rops);

                        syncDialog.dismiss();
                        Messages.showToast(rootLayout, getString(R.string.sincronizando_ok));

                        replaceFragment(new FragmentTabRops(), true, 0, 0, 0, 0);

                        //Pintamos como seleccionado el primer item de nuestro NavigationView
                        navigationView.getMenu().getItem(0).setChecked(true);

                        sincronizar = false;
                        thiss.getIntent().putExtra("Sincronizar", sincronizar);

                        //// TODO: 28/10/2016  Eliminar esta ultima parte
                        SettingsInspectionRO insp = realm.where(SettingsInspectionRO.class).findFirst();
                        SettingsRopRO rop = realm.where(SettingsRopRO.class).findFirst();
                        RealmResults<FrecuencieRO> f = realm.where(FrecuencieRO.class).findAll();
                        RealmResults<CompanyRO> c = realm.where(CompanyRO.class).findAll();
                        RealmList<CompanyRO> comp = insp.companies;
                        Log.e("INSP", insp.toString());
                        Log.e("ROP", rop.toString());
                        for (CompanyRO cm : comp) {
                            Log.e("INSP_COMP", cm.getId() + " " + cm.getDisplayName());
                        }
                        Log.e("FREC", f.toString());
                        Log.e("COMP", c.toString());

                    } catch (Exception e) {
                        syncDialog.dismiss();
                        e.printStackTrace();
                        Messages.showSB(rootLayout, e.getMessage(), "ok");
                    } finally {
                        realm.close();
                        syncDialog.dismiss();
                    }

                } else {
                    syncDialog.dismiss();
                    Messages.showSB(rootLayout, getString(R.string.sincronizando_error), "ok");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                syncDialog.dismiss();
                Messages.showSB(rootLayout, getString(R.string.sincronizando_error), "ok");
            }
        });


    }*/

}
