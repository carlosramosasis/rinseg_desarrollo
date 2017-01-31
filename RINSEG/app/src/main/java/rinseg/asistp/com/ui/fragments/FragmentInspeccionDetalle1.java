package rinseg.asistp.com.ui.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.GregorianCalendar;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import rinseg.asistp.com.models.AreaRO;
import rinseg.asistp.com.models.CompanyRO;
import rinseg.asistp.com.models.EventRO;
import rinseg.asistp.com.models.Inspeccion;
import rinseg.asistp.com.models.InspeccionRO;
import rinseg.asistp.com.models.InspectorRO;
import rinseg.asistp.com.models.ROP;
import rinseg.asistp.com.models.RiskRO;
import rinseg.asistp.com.models.TargetRO;
import rinseg.asistp.com.models.TypeInspection;
import rinseg.asistp.com.models.TypesRO;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.ui.activities.ActivityInspeccionDetalle;
import rinseg.asistp.com.ui.activities.ActivityMain;
import rinseg.asistp.com.utils.Constants;
import rinseg.asistp.com.utils.Generic;
import rinseg.asistp.com.utils.RinsegModule;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentInspeccionDetalle1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class FragmentInspeccionDetalle1 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "InspId";
    private static final String ARG_PARAM2 = "InspTmpId";

    // TODO: Rename and change types of parameters
    private InspeccionRO inspeccion;

    private OnFragmentInteractionListener mListener;

    int idInspeccion;
    String idInspeccionTem;


    ActivityInspeccionDetalle activityMain;
    private TextView textArea, textFecha, textTipo, textEmpresa, textInspectores, textResponsables;
    FloatingActionButton btnVerIncidentes;

    RealmConfiguration myConfig;

    public FragmentInspeccionDetalle1() {
        // Required empty public constructor
    }

    public static FragmentInspeccionDetalle1 newInstance(int id, String idTemporal) {
        FragmentInspeccionDetalle1 fragment = new FragmentInspeccionDetalle1();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, id);
        args.putString(ARG_PARAM2, idTemporal);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idInspeccion = getArguments().getInt(ARG_PARAM1);
            idInspeccionTem = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inspeccion_detalle1, container, false);

        setUpElements(view);
        setUpData();
        setUpActions();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        activityMain.toolbarInspeccionDet.setTitle(R.string.title_incident_detail);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if ( mListener != null ) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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

    //Proceso para cargar las vistas
    private void setUpElements(View v) {
        activityMain = ((ActivityInspeccionDetalle) getActivity());

        textArea = (TextView) v.findViewById(R.id.text_inspection_detail_area);
        textFecha = (TextView) v.findViewById(R.id.text_inspection_detail_fecha);
        textTipo = (TextView) v.findViewById(R.id.text_inspection_detail_tipo);
        textEmpresa = (TextView) v.findViewById(R.id.text_inspection_detail_empresa);
        textInspectores = (TextView) v.findViewById(R.id.text_inspection_detail_inspectores);
        textResponsables = (TextView) v.findViewById(R.id.text_inspection_detail_responsables);

        btnVerIncidentes = (FloatingActionButton) v.findViewById(R.id.btn_ver_incidente);

        // Configuramos Realm :
        Realm.init(this.getActivity().getApplicationContext());
        myConfig = new RealmConfiguration.Builder()
                .name("rinseg.realm")
                .schemaVersion(2)
                .modules(new RinsegModule())
                .deleteRealmIfMigrationNeeded()
                .build();
    }

    /** Escuchadores de eventos */
    private void setUpActions() {
        btnVerIncidentes.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Fragment fragment = FragmentInspeccionDetalle2.newInstance(inspeccion.getId());
                activityMain.replaceFragment(fragment, true, 0, 0, 0, 0);
            }
        });

        activityMain.toolbarInspeccionDet.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityMain.finish();
            }
        });
    }

    /** Módulo para cargar los datos */
    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private void setUpData() {
        if ( idInspeccionTem != null ) {
            final Realm realm = Realm.getInstance(myConfig);
            try {
                inspeccion = realm.where(InspeccionRO.class).equalTo("id", idInspeccion).findFirst();
                // Seteamos los datos en la vista :
                textArea.setText(inspeccion.getArea());
                textFecha.setText(inspeccion.getDateString());

                // Recuperamos el tipo de inspección por código :
                TypesRO type = realm.where(TypesRO.class)
                        .equalTo("id", inspeccion.getTypeInspectionId()).findFirst();
                if ( type != null ) {
                    textTipo.setText(type.getDisplayName());
                }

                // Recuperamos la compañía por código :
                CompanyRO company = realm.where(CompanyRO.class)
                        .equalTo("id", inspeccion.getCompanyId()).findFirst();
                if ( company != null ) {
                    textEmpresa.setText(company.getDisplayName());
                }

                // Concatenamos datos de inspectores :
                String inspectores = "";
                for (InspectorRO i : inspeccion.listaInspectores ) {
                    inspectores = inspectores + i.getName() + "\n";
                }
                textInspectores.setText(inspectores);

                // Concatenamos datos de responsables :
                String responsables = "";
                for (InspectorRO i : inspeccion.listaResponsables ) {
                    responsables = responsables + i.getName() + "\n";
                }
                textResponsables.setText(responsables);
            } catch ( Exception e ) {
                e.printStackTrace();
            } finally {
                realm.close();
            }
        }
    }
}
