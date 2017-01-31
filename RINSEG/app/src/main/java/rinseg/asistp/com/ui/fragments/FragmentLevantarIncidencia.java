package rinseg.asistp.com.ui.fragments;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;
import rinseg.asistp.com.models.FrecuencieRO;
import rinseg.asistp.com.models.IncidenciaLevantadaRO;
import rinseg.asistp.com.models.IncidenciaRO;
import rinseg.asistp.com.models.InspeccionRO;
import rinseg.asistp.com.models.RiskRO;
import rinseg.asistp.com.models.SeveritiesRO;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.ui.activities.ActivityInspeccionDetalle;
import rinseg.asistp.com.utils.Generic;
import rinseg.asistp.com.utils.RinsegModule;

public class FragmentLevantarIncidencia extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "idInspeccion";
    private static final String ARG_PARAM2 = "idIncidencia";

    private int idInspeccion;
    private int idIncidencia;

    private OnFragmentInteractionListener mListener;

    ActivityInspeccionDetalle activityMain;

    Button btnCancelar;
    Button btnGrabar;
    TextView textDisplayDate;
    ImageButton btnShowCalendar;
    EditText editDescription;
    MaterialSpinner spinnerFrequencies;
    MaterialSpinner spinnerSeverities;
    TextView textCategory;
    TextView textLevel;

    ArrayAdapter<FrecuencieRO> adapterFrequencies;
    ArrayAdapter<SeveritiesRO> adapterSeverities;
    RealmResults<FrecuencieRO> frecuencieROs;
    RealmResults<SeveritiesRO> severitiesROs;
    RealmResults<RiskRO> riesgosROs;

    IncidenciaRO incidenciaRO;
    IncidenciaLevantadaRO incLevantadaRO;

    RealmConfiguration myConfig;

    int valorNiv = 0;
    boolean isNewFrequency, isNewSeverity;

    Calendar datePicker;

    public FragmentLevantarIncidencia() { }

    public static FragmentLevantarIncidencia newInstance(int idInspeccion, int idIncidencia) {
        FragmentLevantarIncidencia fragment = new FragmentLevantarIncidencia();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, idInspeccion);
        args.putInt(ARG_PARAM2, idIncidencia);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idInspeccion = getArguments().getInt(ARG_PARAM1);
            idIncidencia = getArguments().getInt(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_levantar_incidencia, container, false);

        setUpElements(view);
        setUpData();
        setUpActions();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        activityMain.toolbarInspeccionDet.setTitle(R.string.title_levantamiento);
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

    /** Inicializamos los elementos */
    private void setUpElements(View v) {
        activityMain = ((ActivityInspeccionDetalle) getActivity());
        btnCancelar =  (Button) v.findViewById(R.id.btn_cancelar);
        btnGrabar =  (Button) v.findViewById(R.id.btn_grabar);
        textDisplayDate = (TextView) v.findViewById(R.id.text_fix_incident_display_date);
        btnShowCalendar = (ImageButton) v.findViewById(R.id.btn_fix_incident_calendar);
        editDescription = (EditText) v.findViewById(R.id.edit_fix_incident_description);
        spinnerFrequencies = (MaterialSpinner) v.findViewById(R.id.spinner_fix_incident_frequency);
        spinnerSeverities = (MaterialSpinner) v.findViewById(R.id.spinner_fix_incident_severity);
        textCategory = (TextView) v.findViewById(R.id.textview_fix_incident_category);
        textLevel = (TextView) v.findViewById(R.id.textview_fix_incident_level);

        // Configurando Realm :
        Realm.init(this.getActivity().getApplicationContext());
        myConfig = new RealmConfiguration.Builder()
                .name("rinseg.realm")
                .schemaVersion(2)
                .modules(new RinsegModule())
                .deleteRealmIfMigrationNeeded()
                .build();
    }

    /** Cargamos los datos iniciales a la interfaz */
    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private void setUpData() {

        final Realm realm = Realm.getInstance(myConfig);

        try {
            // Recuperamos desde Real todas las frecuencias :
            frecuencieROs = realm.where(FrecuencieRO.class).findAll();
            // Asignamos las frecuncias al adapter :
            adapterFrequencies = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, frecuencieROs);
            adapterFrequencies.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerFrequencies.setAdapter(adapterFrequencies);

            // Recuperamos desde Realm todas las severidades :
            severitiesROs = realm.where(SeveritiesRO.class).findAll();
            // Asignamos las severidades al adapter :
            adapterSeverities = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, severitiesROs);
            adapterSeverities.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSeverities.setAdapter(adapterSeverities);

            //Recuperamos desde Realm los riesgos :
            riesgosROs = realm.where(RiskRO.class).findAll();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            realm.close();
        }

        // Recuperando la incidencia anfitriona :
        if (idIncidencia != 0) {
            incidenciaRO = realm.where(IncidenciaRO.class).equalTo("id", idIncidencia).findFirst();

            // Seteando la frecuencia de la incidencia al spinner :
            for ( int i = 0; i < frecuencieROs.size(); i++ ) {
                if ( frecuencieROs.get(i).getId() == incidenciaRO.getFrecuenciaId() ) {
                    spinnerFrequencies.setSelectedIndex(i);
                    break;
                }
            }

            // Seteando la severidad de la incidencia al spinner :
            for ( int i = 0; i < severitiesROs.size(); i++ ) {
                if ( severitiesROs.get(i).getId() == incidenciaRO.getSeveridadId() ) {
                    spinnerSeverities.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    /** Listener de acciones */
    private void setUpActions() {
        activityMain.toolbarInspeccionDet.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityMain.replaceFragment(new FragmentInspeccionIncidenciaDetalle1(),
                        true, 0, 0, 0, 0);
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityMain.replaceFragment(new FragmentInspeccionIncidenciaDetalle1(),
                        true, 0, 0, 0, 0);
            }
        });

        // Guardamos los valores del levantamiento :
        btnGrabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validamos que los campos sean correctos :
                if ( validateForm( )) {
                    // Seteamos los nuevos valores a nuestro IncidenciaRO :
                    saveIncLevantada();
                }
            }
        });

        // Click sobre el botón del Calendar :
        btnShowCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        // Auto cálculo de categoría y nivel de riesgo :
        spinnerSeverities.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                valorNiv = severitiesROs.get(position).getValue() *
                        frecuencieROs.get(spinnerFrequencies.getSelectedIndex()).getValue();
                // Flag de nueva severidad :
                isNewSeverity = ((SeveritiesRO)item).getId() != incidenciaRO.getSeveridadId();
                // Seteamos los valores en la vista :
                calculateRisk();
            }
        });

        // Auto cálculo de categoría y nivel de riesgo :
        spinnerFrequencies.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                valorNiv = frecuencieROs.get(position).getValue() *
                        severitiesROs.get(spinnerSeverities.getSelectedIndex()).getValue();
                // Flag de nueva frecuencia :
                isNewFrequency = ((FrecuencieRO)item).getId() != incidenciaRO.getFrecuenciaId();
                // Seteamos los valores en la vista :
                calculateRisk();
            }
        });
    }

    /** Método para mostrar el DatePicker */
    private void showDatePicker() {
        Calendar newCalendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view,
                                          int year,
                                          int monthOfYear,
                                          int dayOfMonth) {
                        datePicker = Calendar.getInstance();
                        datePicker.set(year, monthOfYear, dayOfMonth);
                        textDisplayDate.setText(Generic.dateFormatter.format(datePicker.getTime()));
                        textDisplayDate.setError(null);
            }
        }, newCalendar.get(Calendar.YEAR),
                newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    /** Método para calcular los valores de categoría y nivel de riesgo */
    private void calculateRisk() {
        String categoria = "-";
        for ( RiskRO riskRO : riesgosROs ) {
            if ( valorNiv <= riskRO.getMaxValue() && valorNiv >= riskRO.getMinValue() ) {
                categoria = riskRO.getDisplayName();
                break;
            }
        }
        textCategory.setText(categoria);
        textLevel.setText(valorNiv);
    }

    /** Método para validar el formulario */
    private boolean validateForm() {
        if ( textDisplayDate.getText().toString().isEmpty() ) {
            textDisplayDate.setError(getString(R.string.error_fix_incident_display_date));
            return false;
        }
        if ( editDescription.getText().toString().isEmpty() ) {
            editDescription.setError(getString(R.string.error_fix_incident_description));
            return false;
        }
        if ( !isNewFrequency || !isNewSeverity ) {
            spinnerFrequencies.setError(getString(R.string.error_fix_incident_frequency));
            spinnerSeverities.setError(getString(R.string.error_fix_incident_severity));
            return false;
        }
        return true;
    }

    /** Metodo para asignar los nuevos valores a nuestra IncidenciaLevantadaRO */
    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private void saveIncLevantada() {
        // Guardando en Realm :
        Realm realm = Realm.getInstance(myConfig);
        try {
            incLevantadaRO = realm.createObject(IncidenciaLevantadaRO.class);
            realm.beginTransaction();
            incLevantadaRO.setIdIncidencia(incidenciaRO.getId());
            incLevantadaRO.setFechaLevantamiento(datePicker.getTime());
            incLevantadaRO.setFechaLevantamientoString(textDisplayDate.getText().toString());
            incLevantadaRO.setIdFrecuencia(
                    frecuencieROs.get(spinnerFrequencies.getSelectedIndex()).getId());
            incLevantadaRO.setIdSeveridad(
                    severitiesROs.get(spinnerSeverities.getSelectedIndex()).getId());
            incLevantadaRO.setCategoriaRiesgo(textCategory.getText().toString());
            incLevantadaRO.setNivelRiesgo(valorNiv);
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            realm.close();
        }
    }
}
