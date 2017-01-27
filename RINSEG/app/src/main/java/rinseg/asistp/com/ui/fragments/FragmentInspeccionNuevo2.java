package rinseg.asistp.com.ui.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import rinseg.asistp.com.adapters.InspectorAdapter;
import rinseg.asistp.com.listener.ListenerClickInspector;
import rinseg.asistp.com.models.InspeccionRO;
import rinseg.asistp.com.models.InspectorRO;
import rinseg.asistp.com.models.ManagementRO;
import rinseg.asistp.com.models.SecuencialRO;
import rinseg.asistp.com.models.SettingsInspectionRO;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.ui.activities.ActivityMain;
import rinseg.asistp.com.utils.Constants;
import rinseg.asistp.com.utils.Generic;
import rinseg.asistp.com.utils.Messages;
import rinseg.asistp.com.utils.RinsegModule;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentInspeccionNuevo2.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentInspeccionNuevo2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentInspeccionNuevo2 extends Fragment implements ListenerClickInspector {

    ///// TODO: :::::::::::::::::::::::: VARIABLES :::::::::::::::::::::::::::::::::::::::::::::::::
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    ActivityMain activityMain;

    InspeccionRO mInspc;

    private List<InspectorRO> listaResponsables = new ArrayList<>();
    RecyclerView recyclerResponsables;
    private RecyclerView.LayoutManager lManager;
    private RecyclerView.Adapter inspectorAdapter;

    RealmConfiguration myConfig;

    private SettingsInspectionRO sIns;

    Bundle bundle;


    ArrayAdapter<ManagementRO> adapterGerencia;

    EditText txtDni;
    EditText txtNombreResponsable;
    Spinner spinnerGerencia;
    Button btnAgregar;

    public FragmentInspeccionNuevo2() {
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
    public static FragmentInspeccionNuevo2 newInstance(String param1, String param2) {
        FragmentInspeccionNuevo2 fragment = new FragmentInspeccionNuevo2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    ///// TODO: :::::::::::::::::::::::::::::::::: EVENTOS :::::::::::::::::::::::::::::::::::::::::
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
        View view = inflater.inflate(R.layout.fragment_inspeccion_nuevo2, container, false);


        setUpElements(view);
        setUpActions();

        LoadFormDefault(view);
        LoadInspeccion();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        activityMain.toolbar.setTitle(R.string.title_responsables);
        activityMain.ShowButtonsBottom(true);
        activityMain.actualPaginaRop = 2;
        activityMain.ShowNumPagina();
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

    @Override
    public void onDestroyView() {
        activityMain.HideNumPagina();
        super.onDestroyView();
    }

    @Override
    public void onDeleteClicked(InspectorAdapter.InspectorViewHolder holder, int position) {
        Realm realm = Realm.getInstance(myConfig);

        try {
            realm.beginTransaction();
            mInspc.listaResponsables.get(position).deleteFromRealm();
            listaResponsables.remove(position);
            inspectorAdapter.notifyDataSetChanged();
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            realm.close();
        } finally {
            realm.close();
        }
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

    ///// TODO: :::::::::::::::::::::::::::::::: METODOS ::::::::::::::::::::::::::::::::::::::::::
    //Proceso para cargar las vistas
    private void setUpElements(View v) {
        activityMain = ((ActivityMain) getActivity());

        bundle = getArguments();

        spinnerGerencia = (Spinner) v.findViewById(R.id.spinner_inpeccion_2_gerencia);
        txtDni = (EditText) v.findViewById(R.id.txt_inpeccion_2_dni);
        txtNombreResponsable= (EditText) v.findViewById(R.id.txt_inpeccion_2_nombre);
        btnAgregar = (Button) v.findViewById(R.id.btn_incpeccion_2_agregar_responsable);


        //configuracion para el recicler
        recyclerResponsables = (RecyclerView) v.findViewById(R.id.recycler_inpeccion_2_responsable);
        recyclerResponsables.setHasFixedSize(true);
        // usar administrador para linearLayout
        lManager = new LinearLayoutManager(this.getActivity().getApplicationContext());
        recyclerResponsables.setLayoutManager(lManager);
        // Crear un nuevo Adaptador
        inspectorAdapter = new InspectorAdapter(activityMain.getApplicationContext(),
                listaResponsables, this);
        recyclerResponsables.setAdapter(inspectorAdapter);

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
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AgregarResponsable();
            }
        });

        activityMain.btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentInspeccionNuevo1 fragment = FragmentInspeccionNuevo1
                        .newInstance(mInspc.getTmpId());
                activityMain.replaceFragment(fragment, true,
                        R.anim.enter_from_right, R.anim.exit_to_right,
                        R.anim.enter_from_left, R.anim.exit_to_left);
            }
        });

        activityMain.btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listaResponsables.size() <= 0) {
                    ValidarFormulario();
                    Messages.showSB(v, getString(R.string.msg_lista_responsable_area), "ok");
                    return;
                }
                Fragment fInspPendiente3 = new FragmentInspeccionNuevo3();
                Bundle args = new Bundle();
                args.putString("InspTmpId", mInspc.getTmpId());
                args.putInt("InspId", mInspc.getId());
                fInspPendiente3.setArguments(args);
                activityMain.replaceFragment(fInspPendiente3, true,
                        R.anim.enter_from_left, R.anim.exit_to_left,
                        R.anim.enter_from_right, R.anim.exit_to_right);
            }
        });
    }

    private void LoadFormDefault(View v) {
        final Realm realm = Realm.getInstance(myConfig);
        try {
            sIns = realm.where(SettingsInspectionRO.class).findFirst();
            //cargar Empresa (Companies)
            adapterGerencia = new ArrayAdapter<ManagementRO>(getActivity(), R.layout.spinner_item, sIns.managements);
            adapterGerencia.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerGerencia.setAdapter(adapterGerencia);

        } catch (Exception e) {
            Messages.showSB(v, e.getMessage(), "ok");
        } finally {
            realm.close();
        }
    }

    private boolean ValidarFormulario() {

        boolean resu = true;

        if ( txtDni.getText().length() == 0 ) {
            txtDni.setError(getString(R.string.error_dni_insp1));
            resu = false;
        } else {
            if ( txtDni.getText().length() != 8 ) {
                txtDni.setError(getString(R.string.error_dni_invalid_insp1));
                resu = false;
            }
        }

        if (txtNombreResponsable.getText().length() == 0) {
            txtNombreResponsable.setError(getString(R.string.error_nombre_resp_insp2));
            resu = false;
        }

        ManagementRO gerenciaSelect;
        gerenciaSelect = ((ManagementRO) spinnerGerencia.getSelectedItem());
        if (gerenciaSelect.getId() == 0) {
            TextView txtGerencia = (TextView) spinnerGerencia.getSelectedView();
            txtGerencia.setError("");
            resu = false;
        }
        return resu;
    }

    private void AgregarResponsable() {
        if (!ValidarFormulario()) {
            return;
        }

        Realm realm = Realm.getInstance(myConfig);
        try {
            ManagementRO empresa = ((ManagementRO) spinnerGerencia.getSelectedItem());

            realm.beginTransaction();
            InspectorRO mInspector = realm.createObject(InspectorRO.class);
            mInspector.setDni(txtDni.getText().toString().trim());
            mInspector.setName(txtNombreResponsable.getText().toString().trim());
            mInspector.setManagementId(empresa.getId());

            mInspc.listaResponsables.add(0,mInspector);
            realm.commitTransaction();
            listaResponsables.add(0, mInspector);
            inspectorAdapter.notifyDataSetChanged();
            txtDni.setText("");
            txtNombreResponsable.setText("");
            spinnerGerencia.setSelection(0);

        } catch (Exception e) {
            e.printStackTrace();
            realm.close();
        } finally {
            realm.close();
        }

    }

    private void LoadInspeccion() {
        String tmpIdInsp = null;
        int id = 0;
        if (bundle != null) {
            tmpIdInsp = bundle.getString("InspTmpId", null);
            id = bundle.getInt("InspId", 0);

            final Realm realm = Realm.getInstance(myConfig);
            try {
                if(id != 0){
                    mInspc = realm.where(InspeccionRO.class).equalTo("id", id).findFirst();
                }else if(tmpIdInsp != null){
                    mInspc = realm.where(InspeccionRO.class).equalTo("tmpId", tmpIdInsp).findFirst();
                }

                if (mInspc == null) {
                    return;
                }

                // recuperar Responsables
                for (int i = 0; i < mInspc.listaResponsables.size(); i++) {
                    InspectorRO insp = mInspc.listaResponsables.get(i);
                    listaResponsables.add(insp);
                    inspectorAdapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                realm.close();
            }
        }
    }
}