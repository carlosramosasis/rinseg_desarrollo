package rinseg.asistp.com.ui.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.AtomicFile;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import rinseg.asistp.com.adapters.IncidenciaAdapter;
import rinseg.asistp.com.adapters.RopAdapter;
import rinseg.asistp.com.listener.ListenerClick;
import rinseg.asistp.com.models.Inspeccion;
import rinseg.asistp.com.models.InspeccionRO;
import rinseg.asistp.com.models.ROP;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.ui.activities.ActivityInspeccionDetalle;
import rinseg.asistp.com.ui.activities.ActivityMain;
import rinseg.asistp.com.adapters.InspeccionAdapter;
import rinseg.asistp.com.utils.RinsegModule;

public class FragmentInspecciones extends Fragment implements ListenerClick {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private FloatingActionButton btnAgregar;
    ActivityMain activityMain;

    private RecyclerView recyclerInspecciones;
    private RecyclerView.Adapter inspeccionAdapter;
    private RecyclerView.LayoutManager lManager;
    private List<InspeccionRO> listaInspeccinoes = new ArrayList<>();

    int TAKE_PHOTO_CODE = 0;
    public static int count = 0;
    String dir;

    RealmConfiguration myConfig;

    public FragmentInspecciones() {
        // Required empty public constructor
    }

    public static FragmentInspecciones newInstance(String param1, String param2) {
        FragmentInspecciones fragment = new FragmentInspecciones();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ( getArguments() != null ) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inspecciones, container, false);

        setUpElements(view);
        setUpActions();

        Permissions();

        LoadInspeccionesCerradas();

        // Here, we are making a folder named picFolder to store
        // pics taken by the camera using this application.
        dir = Environment.getExternalStorageDirectory() + "/picFolder/";
        File newdir = new File(dir);
        newdir.mkdirs();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_PHOTO_CODE ) {
            Log.d("CameraDemo", "Pic saved");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        activityMain.toolbar.setTitle(R.string.title_inspecciones);
        activityMain.ShowButtonsBottom(false);
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
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onItemClicked(InspeccionAdapter.InspeccionViewHolder holder, int position) {
        // Si la inspección está cerrada, lanzamos Inspección detalle :
        if ( listaInspeccinoes.get(position).isCerrado() ) {
            launchActivityInspeccionDetalle(listaInspeccinoes.get(position));
        } else {
            // La inspección aún se puede editar :
            FragmentInspeccionNuevo1 fragment = FragmentInspeccionNuevo1
                    .newInstance(listaInspeccinoes.get(position).getTmpId());
            activityMain.replaceFragment(fragment, true,
                    R.anim.enter_from_left, R.anim.exit_to_left,
                    R.anim.enter_from_right, R.anim.exit_to_right);
        }
    }

    @Override
    public void onItemClicked(RopAdapter.RopViewHolder holder, int position) { }

    @Override
    public void onItemClicked(IncidenciaAdapter.IncidenciaViewHolder holder, int position) { }

    @Override
    public void onItemLongClicked(RopAdapter.RopViewHolder holder, int position) { }


    // Proceso para cargar las vistas
    private void setUpElements(View v) {
        btnAgregar = (FloatingActionButton) v.findViewById(R.id.btn_agregar_inspeccion);
        activityMain = ((ActivityMain) getActivity());

        //configuracion para el recicler
        recyclerInspecciones = (RecyclerView) v.findViewById(R.id.recycler_view_inspecciones);
        recyclerInspecciones.setHasFixedSize(true);
        // usar administrador para linearLayout
        lManager = new LinearLayoutManager(this.getActivity().getApplicationContext());
        recyclerInspecciones.setLayoutManager(lManager);
        // Crear un nuevo Adaptador
        inspeccionAdapter = new InspeccionAdapter(listaInspeccinoes,this);
        recyclerInspecciones.setAdapter(inspeccionAdapter);

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
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityMain.ShowButtonsBottom(true);
                activityMain.replaceFragment(new FragmentInspeccionNuevo1(), true, 0, 0, 0, 0);
            }
        });
    }

    public void Permissions() {

        ArrayList<String> especificacionPermisos = new ArrayList<String>();

        int permissionCheckCamera = ContextCompat.checkSelfPermission(
                this.getActivity(), Manifest.permission.CAMERA);
        if (permissionCheckCamera != PackageManager.PERMISSION_GRANTED) {
            especificacionPermisos.add(Manifest.permission.CAMERA);
        }

        String[] permisos = new String[especificacionPermisos.size()];
        permisos = especificacionPermisos.toArray(permisos);

        if (especificacionPermisos.size() > 0) {
            this.requestPermissions(permisos, TAKE_PHOTO_CODE);
        }
    }

    /**
     * Created on 28/09/16
     * Módulo encargado de lanzar la actividad
     */
    public void launchActivityInspeccionDetalle(InspeccionRO insp) {

        Intent InspeccionDetalleIntent = new Intent().setClass(
                activityMain, ActivityInspeccionDetalle.class);
        InspeccionDetalleIntent.putExtra("InspId", insp.getId());
        InspeccionDetalleIntent.putExtra("InspTmpId", insp.getTmpId());
        startActivity(InspeccionDetalleIntent );
    }

    private void LoadInspeccionesCerradas() {
        Realm realm = Realm.getInstance(myConfig);
        try {
            RealmResults<InspeccionRO> InspeccionesRealm =
                    realm.where(InspeccionRO.class).findAll().sort("dateClose", Sort.DESCENDING);

            for (int i = 0; i < InspeccionesRealm.size(); i++) {
                InspeccionRO tInsp = InspeccionesRealm.get(i);
                listaInspeccinoes.add(tInsp);
                inspeccionAdapter.notifyDataSetChanged();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            realm.close();
        }
    }
}