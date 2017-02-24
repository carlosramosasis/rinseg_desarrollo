package rinseg.asistp.com.ui.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
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

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import rinseg.asistp.com.adapters.IncidenciaAdapter;
import rinseg.asistp.com.adapters.InspeccionAdapter;
import rinseg.asistp.com.listener.ListenerClick;
import rinseg.asistp.com.models.ROP;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.ui.activities.ActivityMain;
import rinseg.asistp.com.adapters.RopAdapter;
import rinseg.asistp.com.utils.DialogRINSEG;
import rinseg.asistp.com.utils.Generic;
import rinseg.asistp.com.utils.Messages;
import rinseg.asistp.com.utils.RinsegModule;

public class FragmentROPsPendientes extends Fragment implements ListenerClick {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FloatingActionButton btnAgregar;
    ActivityMain activityMain;

    private RecyclerView recyclerRops;
    private RopAdapter ropAdapter;
    private RecyclerView.LayoutManager lManager;
    private List<ROP> listaRops = new ArrayList<>();

    RealmConfiguration myConfig;

    public int REQUEST_IMAGE_CAPTURE = 1;

    public FragmentROPsPendientes() { }

    public static FragmentROPsPendientes newInstance(String param1, String param2) {
        FragmentROPsPendientes fragment = new FragmentROPsPendientes();
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
        View view = inflater.inflate(R.layout.fragment_rops_pendientes, container, false);

        setUpElements(view);
        setUpActions();
        LoadRopPendientes();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        activityMain.ShowButtonsBottom(false);
    }

    @Override
    public void onItemClicked(RopAdapter.RopViewHolder holder, int position) {

        Fragment fRopPendiente1 = new FragmentROPPendiente1();
        Bundle args = new Bundle();
        args.putBoolean("puedeEliminarse", false);
        args.putBoolean("vieneDeListado", true);
        args.putString("ROPtmpId", listaRops.get(position).getTmpId());
        fRopPendiente1.setArguments(args);
        activityMain.replaceFragment(fRopPendiente1, true, 0, 0, 0, 0);
    }

    @Override
    public void onItemLongClicked(RopAdapter.RopViewHolder holder, int position) {
        MostrarEliminarRop(position);
    }

    @Override
    public void onItemClicked(InspeccionAdapter.InspeccionViewHolder holder, int position) { }

    @Override
    public void onItemClicked(IncidenciaAdapter.IncidenciaViewHolder holder, int position) { }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    //Proceso para cargar las vistas
    private void setUpElements(View v) {

        activityMain = ((ActivityMain) getActivity());

        btnAgregar = (FloatingActionButton) v.findViewById(R.id.btn_agregar_rop);

        //configuramos Realm
        Realm.init(this.getActivity().getApplicationContext());
        myConfig = new RealmConfiguration.Builder()
                .name("rinseg.realm")
                .schemaVersion(2)
                .modules(new RinsegModule())
                .deleteRealmIfMigrationNeeded()
                .build();

        //configuracion para el recicler
        recyclerRops = (RecyclerView) v.findViewById(R.id.recycler_view_rops_pendientes);
        recyclerRops.setHasFixedSize(true);
        // usar administrador para linearLayout
        lManager = new LinearLayoutManager(this.getActivity().getApplicationContext());
        recyclerRops.setLayoutManager(lManager);
        // Crear un nuevo Adaptador
        ropAdapter = new RopAdapter(listaRops, activityMain.getApplicationContext(), this);
        recyclerRops.setAdapter(ropAdapter);
    }

    private void setUpActions() {

        /*btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fRopPendiente1 = new FragmentROPPendiente1();
                Bundle args = new Bundle();
                args.putBoolean("puedeEliminarse", true);
                fRopPendiente1.setArguments(args);
                activityMain.replaceFragment(fRopPendiente1, true, 0, 0, 0, 0);
            }
        });*/
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private void LoadRopPendientes() {
        Realm realm = Realm.getInstance(myConfig);
        try {
            RealmResults<ROP> RopsRealm = realm.where(ROP.class).equalTo("estadoRop", 0).findAll()
                    .sort("tmpId", Sort.DESCENDING);

            for (int i = 0; i < RopsRealm.size(); i++) {
                ROP tRop = RopsRealm.get(i);
                listaRops.add(tRop);
                ropAdapter.notifyDataSetChanged();
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        } finally {
            realm.close();
        }
    }

    public void onButtonPressed(Uri uri) {
        if ( mListener != null ) {
            mListener.onFragmentInteraction(uri);
        }
    }


    public void MostrarEliminarRop(int position){
        final int mPosition = position;
        final View v = getView();

        final ROP ropEliminar = listaRops.get(mPosition);
        final DialogRINSEG dialogEliminarRop = new DialogRINSEG(activityMain);
        dialogEliminarRop.show();
        dialogEliminarRop.setTitle(getString(R.string.dialog_title_rop_pendiente_eliminar));
        dialogEliminarRop.setBody(getString(R.string.dialog_body_rop_pendiente_eliminar) + " " + ropEliminar.getTmpId());
        dialogEliminarRop.btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Realm realm = Realm.getInstance(myConfig);
                try {
                    ROP ropRealm =  realm.where(ROP.class).equalTo("tmpId",ropEliminar.getTmpId())
                            .equalTo("cerrado",false).findFirst();

                    if( ropRealm != null ) {
                        boolean eliminoCarpeta = Generic.EliminarImagenCarpeta(activityMain
                                .getApplicationContext(),ropRealm.getTmpId());

                        realm.beginTransaction();
                        ropRealm.deleteFromRealm();
                        realm.commitTransaction();
                        listaRops.remove(mPosition);
                        ropAdapter.notifyDataSetChanged();

                        Messages.showToast(v,getString(R.string.dialog_body_rop_pendiente_eliminar_ok));
                    } else {
                        Messages.showToast(v,getString(R.string.dialog_body_rop_pendiente_eliminar_fail_cerrado));
                    }
                    dialogEliminarRop.dismiss();

                } catch ( Exception e ) {
                    Messages.showToast(v,getString(R.string.dialog_body_rop_pendiente_eliminar_error));
                    dialogEliminarRop.dismiss();
                } finally {
                    realm.close();
                }
            }
        });
    }
}