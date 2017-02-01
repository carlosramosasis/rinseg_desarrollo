package rinseg.asistp.com.ui.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rinseg.asistp.com.models.FrecuencieRO;
import rinseg.asistp.com.models.IncidenciaLevantadaRO;
import rinseg.asistp.com.models.IncidenciaRO;
import rinseg.asistp.com.models.RiskRO;
import rinseg.asistp.com.models.SeveritiesRO;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.services.RestClient;
import rinseg.asistp.com.services.Services;
import rinseg.asistp.com.ui.activities.ActivityInspeccionDetalle;
import rinseg.asistp.com.utils.DialogLoading;
import rinseg.asistp.com.utils.DialogRINSEG;
import rinseg.asistp.com.utils.Generic;
import rinseg.asistp.com.utils.Messages;
import rinseg.asistp.com.utils.RinsegModule;
import rinseg.asistp.com.utils.SharedPreferencesHelper;

import static rinseg.asistp.com.utils.Constants.MY_SHARED_PREFERENCES;

public class FragmentLevantarIncidencia extends Fragment {

    private static final String ARG_PARAM1 = "idInspeccion";
    private static final String ARG_PARAM2 = "idIncidencia";

    private int idInspeccion;
    private String idIncidencia;

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

    RealmResults<RiskRO> riesgosROs;

    List<FrecuencieRO> listF;
    List<SeveritiesRO> listS;

    IncidenciaRO incidenciaRO;
    IncidenciaLevantadaRO incLevantadaRO;

    RealmConfiguration myConfig;

    int valorNiv = 0;
    boolean isNewFrequency, isNewSeverity;

    Calendar datePicker;

    public FragmentLevantarIncidencia() { }

    public static FragmentLevantarIncidencia newInstance(int idInspeccion, String idIncidencia) {
        FragmentLevantarIncidencia fragment = new FragmentLevantarIncidencia();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, idInspeccion);
        args.putString(ARG_PARAM2, idIncidencia);
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

    public void onButtonPressed(Uri uri) {
        if ( mListener != null ) {
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
            RealmResults<FrecuencieRO> frecuencieROs = realm.where(FrecuencieRO.class).findAll();
            listF = realm.copyFromRealm(frecuencieROs);
            listF.remove(0);
            spinnerFrequencies.setItems(listF);

            // Recuperamos desde Realm todas las severidades :
            RealmResults<SeveritiesRO> severitiesROs = realm.where(SeveritiesRO.class).findAll();
            listS = realm.copyFromRealm(severitiesROs);
            listS.remove(0);
            spinnerSeverities.setItems(listS);

            //Recuperamos desde Realm los riesgos :
            riesgosROs = realm.where(RiskRO.class).findAll();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            realm.close();
        }

        // Recuperando la incidencia anfitriona :
        if (!idIncidencia.equals("")) {
            incidenciaRO = realm.where(IncidenciaRO.class)
                    .equalTo("id", Integer.parseInt(idIncidencia)).findFirst();

            // Seteando la frecuencia de la incidencia al spinner :
            for ( int i = 0; i < listF.size(); i++ ) {
                if ( listF.get(i).getId() == incidenciaRO.getFrecuenciaId() ) {
                    spinnerFrequencies.setSelectedIndex(i);
                    break;
                }
            }

            // Seteando la severidad de la incidencia al spinner :
            for ( int i = 0; i < listS.size(); i++ ) {
                if ( listS.get(i).getId() == incidenciaRO.getSeveridadId() ) {
                    spinnerSeverities.setSelectedIndex(i);
                    break;
                }
            }
        }
        textCategory.setText(incidenciaRO.getCategoria());
        textLevel.setText(String.valueOf(incidenciaRO.getRiesgo()));
    }

    /** Listener de acciones */
    private void setUpActions() {
        activityMain.toolbarInspeccionDet.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment f = FragmentInspeccionIncidenciaDetalle1
                        .newInstance(idInspeccion, idIncidencia);
                activityMain.replaceFragment(f, true, 0, 0, 0, 0);
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment f = FragmentInspeccionIncidenciaDetalle1
                        .newInstance(idInspeccion, idIncidencia);
                activityMain.replaceFragment(f, true, 0, 0, 0, 0);
            }
        });

        // Guardamos los valores del levantamiento :
        btnGrabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validamos que los campos sean correctos :
                if ( validateForm( )) {
                    // Seteamos los nuevos valores a nuestro IncidenciaRO :
                    showDialogConfirm();
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

        // Presión sobre el campo fecha :
        textDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        // Auto cálculo de categoría y nivel de riesgo :
        spinnerSeverities.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                valorNiv = listS.get(position).getValue() *
                        listF.get(spinnerFrequencies.getSelectedIndex()).getValue();
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
                valorNiv = listF.get(position).getValue() *
                        listS.get(spinnerSeverities.getSelectedIndex()).getValue();
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
        textLevel.setText(String.valueOf(valorNiv));
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
        /*if ( !isNewFrequency || !isNewSeverity ) {
            spinnerFrequencies.setError(getString(R.string.error_fix_incident_frequency));
            spinnerSeverities.setError(getString(R.string.error_fix_incident_severity));
            return false;
        }*/
        return true;
    }

    /** Metodo para asignar los nuevos valores a nuestra IncidenciaLevantadaRO */
    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private void saveIncLevantada() {
        // Guardando en Realm :
        Realm realm = Realm.getInstance(myConfig);
        try {
            realm.beginTransaction();
            incLevantadaRO = realm.createObject(IncidenciaLevantadaRO.class);
            incLevantadaRO.setIdIncidencia(incidenciaRO.getId());
            incLevantadaRO.setFechaLevantamiento(datePicker.getTime());
            incLevantadaRO.setFechaLevantamientoString(
                    Generic.dateFormatterMySql.format(datePicker.getTime()));
            incLevantadaRO.setIdFrecuencia(
                    listF.get(spinnerFrequencies.getSelectedIndex()).getId());
            incLevantadaRO.setIdSeveridad(
                    listS.get(spinnerSeverities.getSelectedIndex()).getId());
            incLevantadaRO.setCategoriaRiesgo(textCategory.getText().toString());
            incLevantadaRO.setNivelRiesgo(valorNiv);
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            realm.close();
            sendData();
        }
    }

    /** Dialog de confirmación */
    private void showDialogConfirm() {
        final DialogRINSEG dialogConfirm = new DialogRINSEG(activityMain);
        dialogConfirm.show();
        dialogConfirm.setTitle("LEVANTAMIENTO DE INCIDENCIA");
        dialogConfirm.setBody("¿Está seguro de enviar los nuevos datos de la incidencia?");
        dialogConfirm.setTextBtnAceptar("ENVIAR");

        dialogConfirm.btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveIncLevantada();
                dialogConfirm.dismiss();
            }
        });
    }

    /** Módulo para enviar hacia el servidor los datos de la incidencia levantada */
    private void sendData() {
        // Obtenemos el token :
        SharedPreferencesHelper preferencesHelper = new SharedPreferencesHelper(
                activityMain.getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE));
        String token = preferencesHelper.getToken();

        // Mostramos dialog mientras se procese :
        final DialogLoading dialog = new DialogLoading(activityMain);
        dialog.show();

        Realm realm = Realm.getInstance(myConfig);
        final IncidenciaLevantadaRO incidentToFix = realm.copyFromRealm(incLevantadaRO);

        RestClient restClient = new RestClient(Services.INSPECTION);
        Call<ResponseBody> call = restClient.iServices.setFixIncident(incidentToFix, token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if ( response.isSuccessful() ) {
                    // Mostramos dialog de éxito :
                    try {
                        // Intentaremos castear la respuesta :
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        Log.d("TAG-INCIDENT-FIX ", jsonObject.toString());
                        dialog.dismiss();
                        // Actualizar el id
                        showDialogSuccess();
                    } catch (Exception e) {
                        dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    dialog.dismiss();
                    Messages.showSB(getView(), getString(R.string.msg_error_guardar_inspeccion),
                            "ok");
                    Log.e("TAG_OnResponse", response.errorBody() + " - " +
                            response.message() + "code :" + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialog.dismiss();
                t.printStackTrace();
            }
        });
    }

    /** Dialog de información de éxito */
    private void showDialogSuccess() {
        final DialogRINSEG dialogConfirm = new DialogRINSEG(activityMain);
        dialogConfirm.show();
        dialogConfirm.setTitle("INCIDENCIA LEVANTADA");
        dialogConfirm.setBody("Los nuevos datos de la inspección han sido enviados al servidor");
        dialogConfirm.setTextBtnAceptar("DE ACUERDO");
        dialogConfirm.btnCancelar.setVisibility(View.INVISIBLE);
        dialogConfirm.btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogConfirm.dismiss();
                Fragment fragment = FragmentInspeccionDetalle2.newInstance(idInspeccion);
                activityMain.replaceFragment(fragment, true, 0, 0, 0, 0);
            }
        });
    }
}
