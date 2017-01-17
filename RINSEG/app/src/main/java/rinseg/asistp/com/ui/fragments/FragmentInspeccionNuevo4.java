package rinseg.asistp.com.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.getbase.floatingactionbutton.FloatingActionButton;

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
import rinseg.asistp.com.models.IncidenciaRO;
import rinseg.asistp.com.models.InspeccionRO;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.ui.activities.ActivityGenerarIncidencia;
import rinseg.asistp.com.ui.activities.ActivityMain;
import rinseg.asistp.com.utils.RinsegModule;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentInspeccionNuevo4.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentInspeccionNuevo4#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentInspeccionNuevo4 extends Fragment implements ListenerClick {

    /// todo ::::::::::::::::::::::::::::::::::::::::::::::::::::::::: VARIABLES :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerIncidencias;
    private RecyclerView.Adapter incidenciaAdapter;
    private RecyclerView.LayoutManager lManager;
    private List<IncidenciaRO> listaIncidencias = new ArrayList<>();

    private FloatingActionButton btnAgregarIncidente;


    ActivityMain activityMain;

    InspeccionRO mInspc;

    RealmConfiguration myConfig;
    Bundle bundle;


    /// todo ::::::::::::::::::::::::::::::::::::::::::::::::::::::::: CONSTRUCTOR :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    public FragmentInspeccionNuevo4() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentROPPendiente1.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentInspeccionNuevo4 newInstance(String param1, String param2) {
        FragmentInspeccionNuevo4 fragment = new FragmentInspeccionNuevo4();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /// todo ::::::::::::::::::::::::::::::::::::::::::::::::::::::::: EVENTOS :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
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

    @Override
    public void onItemClicked(IncidenciaAdapter.IncidenciaViewHolder holder, int position) {
    }

    @Override
    public void onItemClicked(InspeccionAdapter.InspeccionViewHolder holder, int position) {
    }

    @Override
    public void onItemClicked(RopAdapter.RopViewHolder holder, int position) {
    }

    @Override
    public void onItemLongClicked(RopAdapter.RopViewHolder holder, int position) {
    }


    @Override
    public void onDestroyView() {
        activityMain.ButtonBottomSetDefault();
        activityMain.HideNumPagina();
        super.onDestroyView();
    }

/// todo ::::::::::::::::::::::::::::::::::::::::::::::::::::::::: METODOS :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::


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
        lManager = new LinearLayoutManager(this.getActivity().getApplicationContext());
        recyclerIncidencias.setLayoutManager(lManager);
        // Crear un nuevo Adaptador
        incidenciaAdapter = new IncidenciaAdapter(listaIncidencias, activityMain.getApplicationContext(), this);
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
                //activityMain.replaceFragment(new FragmentIncidenciaNuevo1(), true, 0, 0, 0, 0);
                launchActivityGenerarIncidencia(mInspc);
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
                activityMain.replaceFragment(fInspPendiente3, true, R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_left, R.anim.exit_to_left);
            }
        });
        activityMain.btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

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

    public void launchActivityGenerarIncidencia(InspeccionRO insp) {

        Intent GenerarIncidenciaIntent = new Intent().setClass(activityMain, ActivityGenerarIncidencia.class);
        GenerarIncidenciaIntent.putExtra("InspId", insp.getId());
        GenerarIncidenciaIntent.putExtra("InspTmpId", insp.getTmpId());
        startActivity(GenerarIncidenciaIntent);
    }

    private void LoadIncidencias() {

        Realm realm = Realm.getInstance(myConfig);
        try {
            listaIncidencias.clear();

            for (int i = 0; i < mInspc.listaIncidencias.size(); i++) {
                IncidenciaRO tIncidencia = mInspc.listaIncidencias.get(i);
                listaIncidencias.add(tIncidencia);
                incidenciaAdapter.notifyDataSetChanged();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            realm.close();
        }
    }


}
