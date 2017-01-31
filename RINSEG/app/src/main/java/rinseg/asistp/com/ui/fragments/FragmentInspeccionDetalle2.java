package rinseg.asistp.com.ui.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import rinseg.asistp.com.adapters.IncidenciaAdapter;
import rinseg.asistp.com.adapters.InspeccionAdapter;
import rinseg.asistp.com.adapters.RopAdapter;
import rinseg.asistp.com.listener.ListenerClick;
import rinseg.asistp.com.models.IncidenciaRO;
import rinseg.asistp.com.models.InspeccionRO;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.ui.activities.ActivityInspeccionDetalle;
import rinseg.asistp.com.utils.Generic;
import rinseg.asistp.com.utils.RinsegModule;

public class FragmentInspeccionDetalle2 extends Fragment implements ListenerClick {

    private static final String ARG_PARAM1 = "idInspection";

    private int idInspeccion;

    private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerIncidencias;
    private RecyclerView.Adapter incidenciaAdapter;
    private RecyclerView.LayoutManager lManager;
    private List<IncidenciaRO> listaIncidenciasHost = new ArrayList<>();

    RealmConfiguration myConfig;

    ActivityInspeccionDetalle activityMain;

    public FragmentInspeccionDetalle2() { }

    public static FragmentInspeccionDetalle2 newInstance(int id) {
        FragmentInspeccionDetalle2 fragment = new FragmentInspeccionDetalle2();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idInspeccion = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inspeccion_detalle2_incidencias,
                container, false);

        setUpElements(view);
        setUpData();
        setUpActions();

        return view;
    }

    //Proceso para cargar las vistas
    private void setUpElements(View v) {
        activityMain = ((ActivityInspeccionDetalle) getActivity());

        //configuracion para el recicler
        recyclerIncidencias = (RecyclerView) v.findViewById(
                R.id.recycler_view_inspeccion_detalle_incidencias);
        recyclerIncidencias.setHasFixedSize(true);
        // usar administrador para linearLayout
        lManager =  new LinearLayoutManager(this.getActivity().getApplicationContext());
        recyclerIncidencias.setLayoutManager(lManager);
        // Crear un nuevo Adaptador
        incidenciaAdapter = new IncidenciaAdapter(
                listaIncidenciasHost, activityMain.getApplicationContext(), this);
        recyclerIncidencias.setAdapter(incidenciaAdapter);

        myConfig = new RealmConfiguration.Builder()
                .name("rinseg.realm")
                .schemaVersion(2)
                .modules(new RinsegModule())
                .deleteRealmIfMigrationNeeded()
                .build();
    }

    //cargamos los eventos
    private void setUpActions() {
        activityMain.toolbarInspeccionDet.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment f = FragmentInspeccionDetalle1.newInstance(idInspeccion, "");
                activityMain.replaceFragment(f, true, 0, 0, 0, 0);
            }
        });
    }

    // Proceso para cargar los incidentes :
    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private void setUpData() {
        final Realm realm = Realm.getInstance(myConfig);
        try {
            if (idInspeccion != 0) {
                InspeccionRO inspeccionRO = realm.where(InspeccionRO.class)
                        .equalTo("id", idInspeccion).findFirst();
                listaIncidenciasHost.addAll(inspeccionRO.listaIncidencias);
                incidenciaAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
            realm.close();
        } finally {
            realm.close();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        activityMain.toolbarInspeccionDet.setTitle("INCIDENTES");
    }

    // TODO: Rename method, update argument and hook method into UI event
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onItemClicked(IncidenciaAdapter.IncidenciaViewHolder holder, int position) {
        Fragment f = FragmentInspeccionIncidenciaDetalle1.newInstance(idInspeccion,
                listaIncidenciasHost.get(position).getTmpId());
        activityMain.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_main_content_inspeccion_detalle, f)
                .addToBackStack(null)
                .commit();
    }
    @Override
    public void onItemClicked(InspeccionAdapter.InspeccionViewHolder holder, int position) { }

    @Override
    public void onItemClicked(RopAdapter.RopViewHolder holder, int position) { }

    @Override
    public void onItemLongClicked(RopAdapter.RopViewHolder holder, int position) { }

}
