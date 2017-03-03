package rinseg.asistp.com.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
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
import io.realm.RealmResults;
import io.realm.Sort;
import rinseg.asistp.com.adapters.IncidenciaAdapter;
import rinseg.asistp.com.adapters.InspeccionAdapter;
import rinseg.asistp.com.adapters.RopAdapter;
import rinseg.asistp.com.listener.ListenerClick;
import rinseg.asistp.com.models.ROP;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.ui.activities.ActivityMain;
import rinseg.asistp.com.ui.interfaces.IChangeViewPager;
import rinseg.asistp.com.ui.activities.ActivityRopRegistradoDetalle;

import rinseg.asistp.com.utils.RinsegModule;

public class FragmentROPsRegistrados extends Fragment
        implements ListenerClick, IChangeViewPager {

    private OnFragmentInteractionListener mListener;

    ActivityMain activityMain;

    private RecyclerView.Adapter ropAdapter;
    private List<ROP> listaRops = new ArrayList<>();

    TabLayout tabLayout;

    RealmConfiguration myConfig;

    public FragmentROPsRegistrados() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rops_registrado, container, false);

        setUpElements(view);
        //LoadRopCerrados();

        return view;
    }

    //Proceso para cargar las vistas
    private void setUpElements(View v) {
        activityMain = ((ActivityMain) getActivity());

        tabLayout = (TabLayout) getActivity().findViewById(R.id.tab_rops);

        //configuracion para el recicler
        RecyclerView recyclerRops = (RecyclerView) v.findViewById(R.id.recycler_view_rops_registrados);
        recyclerRops.setHasFixedSize(true);
        // usar administrador para linearLayout
        RecyclerView.LayoutManager lManager = new LinearLayoutManager(
                this.getActivity().getApplicationContext());
        recyclerRops.setLayoutManager(lManager);
        // Crear un nuevo Adaptador
        ropAdapter = new RopAdapter(listaRops, activityMain.getApplicationContext(), this);
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

    @Override
    public void onResume() {
        super.onResume();
        activityMain.ShowButtonsBottom(false);
        LoadRopCerrados();

        tabLayout.getTabAt(1).setText(getString(R.string.tab_registrados) + " (" + listaRops.size() + ")");
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

    @Override
    public void onItemClicked(RopAdapter.RopViewHolder holder, int position) {
        launchActivityRopDetalle(listaRops.get(position));
        //replaceFragmentWithAccionesRegistradas(listaRops.get(position));
    }

    @Override
    public void onItemClicked(IncidenciaAdapter.IncidenciaViewHolder holder, int position) { }

    @Override
    public void onItemClicked(InspeccionAdapter.InspeccionViewHolder holder, int position) { }

    @Override
    public void onItemLongClicked(RopAdapter.RopViewHolder holder, int position) { }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private void LoadRopCerrados() {
        listaRops.clear();
        ropAdapter.notifyDataSetChanged();

        Realm realm = Realm.getInstance(myConfig);
        try {
            RealmResults<ROP> RopsRealm = realm.where(ROP.class).equalTo("estadoRop", 1).findAll()
                    .sort("dateClose", Sort.DESCENDING);

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
     * Módulo encargado de lanzar la actividad
     */
    public void launchActivityRopDetalle(ROP rop) {
       // Intent RopDetalleIntent = new Intent().setClass(activityMain, ActivityRopCerradoDetalle.class);
        Intent RopRegistradoDetalleIntent = new Intent().setClass(activityMain, ActivityRopRegistradoDetalle.class);
        RopRegistradoDetalleIntent.putExtra("ROPId", rop.getId());
        RopRegistradoDetalleIntent.putExtra("ROPIdTmp", rop.getTmpId());

        startActivity(RopRegistradoDetalleIntent);
    }

    private void replaceFragmentWithAccionesRegistradas(ROP rop){
        FragmentROPsRegistradoDetalle fragment = FragmentROPsRegistradoDetalle.newInstance(
                rop.getId(),rop.getTmpId());
        activityMain.replaceFragment(fragment,false,0,0,0,0);

    }

    @Override
    public void notifyChanged(ROP rop, boolean isClosed) {
        if ( !isClosed ) {
            for (int i = 0; i < listaRops.size(); i++) {
                if ( listaRops.get(i).getId() == rop.getId() ) {
                    listaRops.remove(i);
                    break;
                }
            }
            listaRops.add(0, rop);
            ropAdapter.notifyDataSetChanged();
        }
    }
}
