package rinseg.asistp.com.ui.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import rinseg.asistp.com.models.CompanyRO;
import rinseg.asistp.com.models.EventItemsRO;
import rinseg.asistp.com.models.EventRO;
import rinseg.asistp.com.models.FrecuencieRO;
import rinseg.asistp.com.models.IncidenciaRO;
import rinseg.asistp.com.models.InspeccionRO;
import rinseg.asistp.com.models.InspectorRO;
import rinseg.asistp.com.models.ManagementRO;
import rinseg.asistp.com.models.RacRO;
import rinseg.asistp.com.models.SeveritiesRO;
import rinseg.asistp.com.models.TargetRO;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.ui.activities.ActivityInspeccionDetalle;
import rinseg.asistp.com.utils.RinsegModule;

public class FragmentInspeccionIncidenciaDetalle1 extends Fragment {

    private static final String ARG_PARAM1 = "idInspeccion";
    private static final String ARG_PARAM2 = "idIncidencia";

    private int idInspeccion;
    private String idIncidencia;

    private OnFragmentInteractionListener mListener;

    ActivityInspeccionDetalle activityMain;
    FloatingActionButton btnLevantarIncidente;

    TextView txtDescription, txtFrequency, txtSeverity, txtCategory, txtLevel, txtTarget,
            txtDeadLine, txtType, txtRAC, txtManegementRep, txtCompany, txtCMMM, txtReportant,
            txtTitle;

    IncidenciaRO incidenciaRO;
    InspeccionRO inspeccionRO;

    RealmConfiguration myConfig;

    public FragmentInspeccionIncidenciaDetalle1() { }

    public static FragmentInspeccionIncidenciaDetalle1 newInstance(int idInsp, String idInci) {
        FragmentInspeccionIncidenciaDetalle1 fragment = new FragmentInspeccionIncidenciaDetalle1();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, idInsp);
        args.putString(ARG_PARAM2, idInci);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idInspeccion = getArguments().getInt(ARG_PARAM1);
            idIncidencia = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_inspeccion_detalle2_incidente_detalle1, container, false);

        setUpElements(view);
        setUpData();
        setUpActions();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        activityMain.toolbarInspeccionDet.setTitle("INCIDENCIA ");
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

    //Proceso para cargar las vistas
    private void setUpElements(View v) {
        activityMain = ((ActivityInspeccionDetalle) getActivity());

        btnLevantarIncidente =  (FloatingActionButton) v.findViewById(R.id.btn_levantar_incidente);
        txtDescription = (TextView) v.findViewById(R.id.text_incident_detail_descr);
        txtCategory = (TextView) v.findViewById(R.id.text_incident_detail_category);
        txtCMMM = (TextView) v.findViewById(R.id.text_incident_detail_CMMM);
        txtCompany = (TextView) v.findViewById(R.id.text_incident_detail_company_devio);
        txtDeadLine = (TextView) v.findViewById(R.id.text_incident_detail_deadline);
        txtFrequency = (TextView) v.findViewById(R.id.text_incident_detail_frequency);
        txtLevel = (TextView) v.findViewById(R.id.text_incident_detail_level);
        txtManegementRep = (TextView) v.findViewById(R.id.text_incident_detail_management);
        txtRAC = (TextView) v.findViewById(R.id.text_incident_detail_rac);
        txtReportant = (TextView) v.findViewById(R.id.text_incident_detail_reportant);
        txtSeverity = (TextView) v.findViewById(R.id.text_incident_detail_severity);
        txtType = (TextView) v.findViewById(R.id.text_incident_detail_type);
        txtTarget = (TextView) v.findViewById(R.id.text_incident_detail_target);
        txtTitle = (TextView) v.findViewById(R.id.text_incident_detail_title);

        myConfig = new RealmConfiguration.Builder()
                .name("rinseg.realm")
                .schemaVersion(2)
                .modules(new RinsegModule())
                .deleteRealmIfMigrationNeeded()
                .build();
    }

    // Proceso para cargar los datos :
    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private void setUpData() {
        final Realm realm = Realm.getInstance(myConfig);
        try {
            if ( !idIncidencia.equals("") && idInspeccion != 0 ) {
                incidenciaRO = realm.where(IncidenciaRO.class)
                        .equalTo("tmpId", idIncidencia).findFirst();
                inspeccionRO = realm.where(InspeccionRO.class)
                        .equalTo("id", idInspeccion).findFirst();

                // Seteando los campos con sus respectivos valores :
                txtDescription.setText(incidenciaRO.getDescripcion());
                txtCategory.setText(incidenciaRO.getCategoria());
                txtCMMM.setText(incidenciaRO.getSupervisor());
                txtDeadLine.setText(incidenciaRO.getFechalimiteString().substring(0, 10));

                // Hallanado el tipo acto o condición :
                EventRO eventRO = realm.where(EventRO.class)
                        .equalTo("id", incidenciaRO.getEventId()).findFirst();
                if ( eventRO != null ) {
                    txtTitle.setText(eventRO.getDisplayName());
                }

                // Hallando compañía
                CompanyRO comRO = realm.where(CompanyRO.class)
                        .equalTo("id", inspeccionRO.getCompanyId()).findFirst();
                if ( comRO != null ) {
                    txtCompany.setText(comRO.getDisplayName());
                }

                // Hallando la frecuencia por id :
                FrecuencieRO freRO = realm.where(FrecuencieRO.class)
                        .equalTo("id", incidenciaRO.getFrecuenciaId()).findFirst();
                if ( freRO != null ) {
                    txtFrequency.setText(freRO.getDisplayName());
                }

                txtLevel.setText(String.valueOf(incidenciaRO.getRiesgo()));

                // Hallando el inspector y su gerencia :
                for (InspectorRO inspRO : inspeccionRO.listaInspectores) {
                    if ( inspRO.getId() == incidenciaRO.getReportanteId() ) {
                        txtReportant.setText(inspRO.getName());
                        ManagementRO compRO = realm.where(ManagementRO.class)
                                .equalTo("id", inspRO.getManagementId()).findFirst();
                        if ( compRO != null ) {
                            txtManegementRep.setText(compRO.getDisplayName());
                        }
                        break;
                    }
                }

                // Hallando RAC asociado :
                RacRO racRO = realm.where(RacRO.class)
                        .equalTo("id", incidenciaRO.getRacId()).findFirst();
                if ( racRO != null) {
                    txtRAC.setText(racRO.getDisplayName());
                }

                // Hallando la severidad :
                SeveritiesRO seveRO = realm.where(SeveritiesRO.class)
                        .equalTo("id", incidenciaRO.getSeveridadId()).findFirst();
                if ( seveRO != null ) {
                    txtSeverity.setText(seveRO.getDisplayName());
                }

                // Hallando el tipo de incidencia :
                EventItemsRO eItemRO = realm.where(EventItemsRO.class)
                        .equalTo("Id", incidenciaRO.getEventItemId()).findFirst();
                if ( eItemRO != null ) {
                    txtType.setText(eItemRO.getName());
                }

                // Hallando del blanco u objetivo :
                TargetRO targRO = realm.where(TargetRO.class)
                        .equalTo("id", incidenciaRO.getBlancoId()).findFirst();
                if ( targRO != null ) {
                    txtTarget.setText(targRO.getDisplayName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            realm.close();
        } finally {
            realm.close();
        }
    }

    //cargamos los eventos
    private void setUpActions() {
        btnLevantarIncidente.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View v){
                Fragment f = FragmentInspeccionDetalle2.newInstance(idInspeccion);
                activityMain.replaceFragment(f, true ,0, 0, 0, 0);
            }

        });
        activityMain.toolbarInspeccionDet.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment f = FragmentInspeccionDetalle2.newInstance(idInspeccion);
                activityMain.replaceFragment(f, true, 0, 0, 0, 0);
            }
        });
    }
}
