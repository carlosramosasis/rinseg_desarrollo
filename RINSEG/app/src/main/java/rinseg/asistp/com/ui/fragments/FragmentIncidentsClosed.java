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
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.Sort;
import rinseg.asistp.com.adapters.IncidenciaAdapter;
import rinseg.asistp.com.adapters.InspeccionAdapter;
import rinseg.asistp.com.adapters.RopAdapter;
import rinseg.asistp.com.listener.ListenerClick;
import rinseg.asistp.com.models.IncidenciaRO;
import rinseg.asistp.com.models.InspeccionRO;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.utils.RinsegModule;

/**
 * Created by OSequeiros on 10/02/2017.
 * Fragment de incidentes cerrados
 */

public class FragmentIncidentsClosed extends Fragment implements ListenerClick {

    private static final String ARG_PARAM_ID = "id_inspection";

    private int idInspeccion;

    private RecyclerView.Adapter adapterList;
    private List<IncidenciaRO> listIncidents = new ArrayList<>();

    RealmConfiguration myConfig;

    /** Constructor de fragment */
    public FragmentIncidentsClosed newInstance(int idIspeccion) {
        FragmentIncidentsClosed fragment = new FragmentIncidentsClosed();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_ID, idIspeccion);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idInspeccion = getArguments().getInt(ARG_PARAM_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_incidents_closed, container, false);

        setUpElements(view);
        //setUpData();

        return view;
    }

    /** Proceso para cargar las vistas */
    private void setUpElements(View v) {
        RecyclerView recyclerIncidents = (RecyclerView) v.findViewById(
                R.id.recycler_incidents_closed);
        recyclerIncidents.setHasFixedSize(true);
        RecyclerView.LayoutManager lManager =  new LinearLayoutManager(
                getActivity().getApplicationContext());
        recyclerIncidents.setLayoutManager(lManager);

        // Crear un nuevo Adaptador
        adapterList = new IncidenciaAdapter(listIncidents, getActivity().getApplicationContext(),
                this);
        recyclerIncidents.setAdapter(adapterList);

        myConfig = new RealmConfiguration.Builder()
                .name("rinseg.realm")
                .schemaVersion(2)
                .modules(new RinsegModule())
                .deleteRealmIfMigrationNeeded()
                .build();
    }

    /** Proceso para cargar datos */
    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private void setUpData() {
        final Realm realm = Realm.getInstance(myConfig);
        try {
            if (idInspeccion != 0) {
                InspeccionRO inspeccionRO = realm.where(InspeccionRO.class)
                        .equalTo("id", idInspeccion).findFirst();
                List<IncidenciaRO> listInci = inspeccionRO.listaIncidencias.where()
                        .equalTo("closed", true).findAll().sort("riesgo", Sort.DESCENDING);
                listIncidents.addAll(listInci);
                adapterList.notifyDataSetChanged();
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
        setUpData();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onItemClicked(IncidenciaAdapter.IncidenciaViewHolder holder, int position) {
        /*Fragment f = FragmentInspeccionIncidenciaDetalle1.newInstance(idInspeccion,
                String.valueOf(listIncidents.get(position).getId()));
        ((ActivityInspeccionDetalle)getActivity()).replaceFragment(f, false, 0, 0, 0, 0);*/
    }

    @Override
    public void onItemClicked(InspeccionAdapter.InspeccionViewHolder holder, int position) { }

    @Override
    public void onItemClicked(RopAdapter.RopViewHolder holder, int position) { }

    @Override
    public void onItemLongClicked(RopAdapter.RopViewHolder holder, int position) { }
}