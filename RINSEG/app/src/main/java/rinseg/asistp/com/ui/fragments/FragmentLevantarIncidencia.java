package rinseg.asistp.com.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rinseg.asistp.com.models.FotoModel;
import rinseg.asistp.com.models.FrecuencieRO;
import rinseg.asistp.com.models.ImagenRO;
import rinseg.asistp.com.models.IncidenciaLevantadaRO;
import rinseg.asistp.com.models.IncidenciaRO;
import rinseg.asistp.com.models.RiskRO;
import rinseg.asistp.com.models.SettingsInspectionRO;
import rinseg.asistp.com.models.SeveritiesRO;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.services.RestClient;
import rinseg.asistp.com.services.Services;
import rinseg.asistp.com.ui.activities.ActivityFotoComentario;
import rinseg.asistp.com.ui.activities.ActivityGaleria;
import rinseg.asistp.com.ui.activities.ActivityInspeccionDetalle;
import rinseg.asistp.com.utils.Constants;
import rinseg.asistp.com.utils.DialogLoading;
import rinseg.asistp.com.utils.DialogRINSEG;
import rinseg.asistp.com.utils.Generic;
import rinseg.asistp.com.utils.Messages;
import rinseg.asistp.com.utils.RinsegModule;
import rinseg.asistp.com.utils.SharedPreferencesHelper;

import static rinseg.asistp.com.utils.Constants.MY_SHARED_PREFERENCES;
import static rinseg.asistp.com.utils.Constants.tagIncidentes;

public class FragmentLevantarIncidencia extends Fragment {

    private static final String ARG_PARAM1 = "idInspeccion";
    private static final String ARG_PARAM2 = "idIncidencia";

    private int idInspeccion;
    private String idIncidencia;

    int totalToSend, correctSend, failSend = 0;

    //import foto
    public int PICK_IMAGE_REQUEST = 1;
    // tomar foto
    public int REQUEST_IMAGE_CAPTURE = 1;

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
    ProgressBar progressRiesgo;

    DialogLoading dialog;

    FloatingActionsMenu btnFabMenu;
    FloatingActionButton btnTomarFoto, btnImportarFotos, btnGaleriaFotos;

    RealmResults<RiskRO> riesgosROs;

    List<FrecuencieRO> listF;
    List<SeveritiesRO> listS;

    IncidenciaRO incidenciaRO;
    IncidenciaLevantadaRO incLevantadaRO;

    RealmConfiguration myConfig;

    int valorNiv = 0;
    boolean isNewFrequency, isNewSeverity;

    Calendar datePicker;

    int nivelRiesgo;
    String categoriaRiesgo;

    SettingsInspectionRO sIns;

    static Uri capturedImageUri = null;

    public FragmentLevantarIncidencia() {
    }

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

        LoadSettingInspeccion();
        CalcularRiesgo();

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
        btnFabMenu.setVisibility(View.VISIBLE);
        if (incidenciaRO != null) {
            MostrarCantidadImagenesInspeccionesLevantadas(incidenciaRO.listaImgComent);
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            try {
                Uri imagen = null;
                if (data != null) {
                    if (data.getData() != null) {
                        imagen = data.getData();
                    }
                } else if (capturedImageUri != null) {
                    imagen = capturedImageUri;
                    capturedImageUri = null;
                }
                if (imagen != null) {
                    launchActivityFotoComentario(imagen);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Inicializamos los elementos
     */
    private void setUpElements(View v) {
        activityMain = ((ActivityInspeccionDetalle) getActivity());
        btnCancelar = (Button) v.findViewById(R.id.btn_cancelar);
        btnGrabar = (Button) v.findViewById(R.id.btn_grabar);
        textDisplayDate = (TextView) v.findViewById(R.id.text_fix_incident_display_date);
        btnShowCalendar = (ImageButton) v.findViewById(R.id.btn_fix_incident_calendar);
        editDescription = (EditText) v.findViewById(R.id.edit_fix_incident_description);
        spinnerFrequencies = (MaterialSpinner) v.findViewById(R.id.spinner_fix_incident_frequency);
        spinnerSeverities = (MaterialSpinner) v.findViewById(R.id.spinner_fix_incident_severity);
        textCategory = (TextView) v.findViewById(R.id.textview_fix_incident_category);
        textLevel = (TextView) v.findViewById(R.id.textview_fix_incident_level);
        progressRiesgo = (ProgressBar) v.findViewById(R.id.progress_lev_incidencia);

        btnFabMenu = (FloatingActionsMenu) v.findViewById(R.id.fab_menu_rop);
        btnTomarFoto = (FloatingActionButton) v.findViewById(R.id.fab_tomar_foto);
        btnImportarFotos = (FloatingActionButton) v.findViewById(R.id.fab_import_foto);
        btnGaleriaFotos = (FloatingActionButton) v.findViewById(R.id.fab_set_dir);

        // Configurando Realm :
        Realm.init(this.getActivity().getApplicationContext());
        myConfig = new RealmConfiguration.Builder()
                .name("rinseg.realm")
                .schemaVersion(2)
                .modules(new RinsegModule())
                .deleteRealmIfMigrationNeeded()
                .build();
    }

    /**
     * Cargamos los datos iniciales a la interfaz
     */
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

            // Recuperando la incidencia anfitriona :
            if (!idIncidencia.equals("")) {
                incidenciaRO = realm.where(IncidenciaRO.class)
                        .equalTo("id", Integer.parseInt(idIncidencia)).findFirst();

                // Seteando la frecuencia de la incidencia al spinner :
                for (int i = 0; i < listF.size(); i++) {
                    if (listF.get(i).getId() == incidenciaRO.getFrecuenciaId()) {
                        spinnerFrequencies.setSelectedIndex(i);
                        break;
                    }
                }

                // Seteando la severidad de la incidencia al spinner :
                for (int i = 0; i < listS.size(); i++) {
                    if (listS.get(i).getId() == incidenciaRO.getSeveridadId()) {
                        spinnerSeverities.setSelectedIndex(i);
                        break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            realm.close();
        } finally {
            realm.close();
        }


        //textCategory.setText(incidenciaRO.getCategoria());
        //textLevel.setText(String.valueOf(incidenciaRO.getRiesgo()));
    }

    /**
     * Listener de acciones
     */
    private void setUpActions() {
        activityMain.toolbarInspeccionDet.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment f = FragmentInspeccionIncidenciaDetalle1
                        .newInstance(idInspeccion, idIncidencia);
                activityMain.replaceFragment(f, true, 0, 0, 0, 0);
            }
        });

        btnTomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCheckCamera = ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CAMERA);
                int permissionCheckWrite = ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permissionCheckCamera != PackageManager.PERMISSION_GRANTED ||
                        permissionCheckWrite != PackageManager.PERMISSION_GRANTED) {
                    Permissions();
                }

                if (permissionCheckCamera == PackageManager.PERMISSION_GRANTED ||
                        permissionCheckWrite == PackageManager.PERMISSION_GRANTED) {
                    Calendar cal = Calendar.getInstance();
                    File file = new File(Environment.getExternalStorageDirectory(),
                            (cal.getTimeInMillis() + ".jpg"));
                    if (!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else {
                        file.delete();
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    capturedImageUri = Uri.fromFile(file);

                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
                        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            }
        });

        btnImportarFotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        btnGaleriaFotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivityGaleria();
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
                if (validateForm()) {
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
                isNewSeverity = ((SeveritiesRO) item).getId() != incidenciaRO.getSeveridadId();
                // Seteamos los valores en la vista :
                //calculateRisk();
                CalcularRiesgo();
            }
        });

        // Auto cálculo de categoría y nivel de riesgo :
        spinnerFrequencies.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                valorNiv = listF.get(position).getValue() *
                        listS.get(spinnerSeverities.getSelectedIndex()).getValue();
                // Flag de nueva frecuencia :
                isNewFrequency = ((FrecuencieRO) item).getId() != incidenciaRO.getFrecuenciaId();
                // Seteamos los valores en la vista :
                //calculateRisk();
                CalcularRiesgo();
            }
        });
    }

    /**
     * Método para mostrar el DatePicker
     */
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

    /**
     * Método para calcular los valores de categoría y nivel de riesgo
     */
    private void calculateRisk() {
        String categoria = "-";
        for (RiskRO riskRO : riesgosROs) {
            if (valorNiv <= riskRO.getMaxValue() && valorNiv >= riskRO.getMinValue()) {
                categoria = riskRO.getDisplayName();
                break;
            }
        }
        textCategory.setText(categoria);
        textLevel.setText(String.valueOf(valorNiv));
    }

    /**
     * Método para validar el formulario
     */
    private boolean validateForm() {
        if (textDisplayDate.getText().toString().isEmpty()) {
            textDisplayDate.setError(getString(R.string.error_fix_incident_display_date));
            return false;
        }
        if (editDescription.getText().toString().isEmpty()) {
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

    /**
     * Metodo para asignar los nuevos valores a nuestra IncidenciaLevantadaRO
     */
    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private void saveIncLevantada() {
        // Guardando en Realm :
        Realm realm = Realm.getInstance(myConfig);
        try {
            realm.beginTransaction();
            incLevantadaRO = realm.createObject(IncidenciaLevantadaRO.class);
            incLevantadaRO.setIdIncidencia(incidenciaRO.getId());
            incLevantadaRO.setFechaLevantamiento(datePicker.getTime());
            incLevantadaRO.setFechaLevantamientoString(Generic.dateFormatterMySql.format(datePicker.getTime()));
            incLevantadaRO.setIdFrecuencia(listF.get(spinnerFrequencies.getSelectedIndex()).getId());
            incLevantadaRO.setIdSeveridad(listS.get(spinnerSeverities.getSelectedIndex()).getId());
            incLevantadaRO.setCategoriaRiesgo(textCategory.getText().toString());
            incLevantadaRO.setNivelRiesgo(valorNiv);

            IncidenciaRO incidencia = realm.where(IncidenciaRO.class).equalTo("id", idIncidencia).findFirst();
            if (incidencia != null) {
                incidencia.setClosed(true);
            }

            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            realm.close();
            sendData();
        }
    }

    /**
     * Dialog de confirmación
     */
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

    /**
     * Módulo para enviar hacia el servidor los datos de la incidencia levantada
     */
    private void sendData() {
        totalToSend = 0;
        correctSend = 0;
        failSend  = 0;

        // Obtenemos el token :
        SharedPreferencesHelper preferencesHelper = new SharedPreferencesHelper(
                activityMain.getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE));
        final String token = preferencesHelper.getToken();

        // Mostramos dialog mientras se procese :
        dialog = new DialogLoading(activityMain);
        dialog.show();

        Realm realm = Realm.getInstance(myConfig);
        final IncidenciaLevantadaRO incidentToFix = realm.copyFromRealm(incLevantadaRO);


        RestClient restClient = new RestClient(Services.INSPECTION);
        Call<ResponseBody> call = restClient.iServices.setFixIncident(incidentToFix, token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Mostramos dialog de éxito :
                    try {
                        // Intentaremos castear la respuesta :
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        //Log.d("TAG-INCIDENT-FIX ", jsonObject.toString());
                        // dialog.dismiss();
                        ///todo Actualizar incidente cerrado
                        updateIncidenteClose();
                        // Actualizar el id
                        // showDialogSuccess();
                        // Enviamos todas las imágenes :

                        RealmResults<ImagenRO> listaImagenes = incidenciaRO.listaImgComent.where().equalTo("inspeccionLevantada", true).findAll();

                        if (listaImagenes != null && listaImagenes.size() > 0) {
                            totalToSend = listaImagenes.size();
                            for (int i = 0; i < listaImagenes.size(); i++) {
                                ImagenRO image = listaImagenes.get(i);
                                sendImage(image, incidenciaRO.getTmpId(), incidenciaRO.getId(), token);
                            }
                        } else {
                            dialog.dismiss();
                            showDialogSuccess();
                        }

                    } catch (Exception e) {
                        dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    dialog.dismiss();
                    Messages.showSB(getView(), getString(R.string.msg_error_guardar_inspeccion),
                            "ok");
                    //Log.e("TAG_OnResponse", response.errorBody() + " - " +
                    //       response.message() + "code :" + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialog.dismiss();
                t.printStackTrace();
            }
        });
    }


    /**
     * Dialog de información de éxito
     */
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
              /*  Fragment fragment = FragmentInspeccionDetalle2.newInstance(idInspeccion);
                activityMain.replaceFragment(fragment, true, 0, 0, 0, 0);*/
                Fragment fragment = FragmentTabsIncidents.newInstance(idInspeccion);
                activityMain.replaceFragment(fragment, true, 0, 0, 0, 0);
            }
        });
    }

    private void showDialogSuccess(String message) {
        final DialogRINSEG dialogConfirm = new DialogRINSEG(activityMain);
        dialogConfirm.show();
        dialogConfirm.setTitle("INSPECCIÓN FINALIZADA");
        dialogConfirm.setBody(message);
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


    void CalcularRiesgo() {
        try {
            textCategory.setText(getString(R.string.rop_c1_texto_default));
            textLevel.setText(getString(R.string.rop_c1_texto_default));
            progressRiesgo.setProgress(0);

            FrecuencieRO frecuenciaSelect = listF.get(spinnerFrequencies.getSelectedIndex());
            SeveritiesRO severitiesSelect = listS.get(spinnerSeverities.getSelectedIndex());

            int valorFrecuencia = frecuenciaSelect.getValue();
            int valorSeveridad = severitiesSelect.getValue();

            nivelRiesgo = valorSeveridad * valorFrecuencia;
            textLevel.setText(String.valueOf(nivelRiesgo));

            Double valorMaximoBase = 0.0;
            Double nivelRiesgoEquivalente = 0.0;


            for (int i = 0; i < sIns.risks.size(); i++) {
                RiskRO riesgo = sIns.risks.get(i);
                if (nivelRiesgo >= riesgo.getMinValue()
                        && nivelRiesgo <= riesgo.getMaxValue()
                        && nivelRiesgo != 0) {

                    categoriaRiesgo = riesgo.getDisplayName();
                    textCategory.setText(categoriaRiesgo);

                    Double rango = ((double) riesgo.getMaxValue() - riesgo.getMinValue());
                    Double unidad = (rango / 25);

                    nivelRiesgo = nivelRiesgo - riesgo.getMinValue();
                    nivelRiesgoEquivalente = nivelRiesgo / unidad;
                    switch (categoriaRiesgo) {
                        case "Bajo":
                            progressRiesgo.setProgressDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.progressbar_green, null));
                            valorMaximoBase = 4.0;
                            progressRiesgo.setProgress((valorMaximoBase.intValue() + nivelRiesgoEquivalente.intValue()));
                            break;
                        case "Medio":
                            progressRiesgo.setProgressDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.progressbar_yellow, null));
                            valorMaximoBase = 29.0;
                            progressRiesgo.setProgress((valorMaximoBase.intValue() + nivelRiesgoEquivalente.intValue()));
                            break;
                        case "Alto":
                            progressRiesgo.setProgressDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.progressbar_orange, null));
                            valorMaximoBase = 54.0;
                            progressRiesgo.setProgress((valorMaximoBase.intValue() + nivelRiesgoEquivalente.intValue()));
                            break;
                        case "Muy Alto":
                            progressRiesgo.setProgressDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.progressbar_red, null));
                            valorMaximoBase = 79.0;
                            progressRiesgo.setProgress((valorMaximoBase.intValue() + nivelRiesgoEquivalente.intValue()));
                            break;
                    }


                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void LoadSettingInspeccion() {
        final Realm realm = Realm.getInstance(myConfig);
        try {
            sIns = realm.where(SettingsInspectionRO.class).findFirst();
        } catch (Exception e) {
            e.printStackTrace();
            realm.close();
        } finally {
            realm.close();
        }
    }

    public void Permissions() {

        ArrayList<String> especificacionPermisos = new ArrayList<>();

        int permissionCheckCamera = ContextCompat.checkSelfPermission(
                this.getActivity(), Manifest.permission.CAMERA);
        int permissionCheckWrite = ContextCompat.checkSelfPermission(
                this.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheckCamera != PackageManager.PERMISSION_GRANTED) {
            especificacionPermisos.add(Manifest.permission.CAMERA);
        }

        if (permissionCheckWrite != PackageManager.PERMISSION_GRANTED) {
            especificacionPermisos.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        String[] permisos = new String[especificacionPermisos.size()];
        permisos = especificacionPermisos.toArray(permisos);

        if (especificacionPermisos.size() > 0) {
            this.requestPermissions(permisos, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void launchActivityGaleria() {
        Intent GaleriaIntent = new Intent().setClass(activityMain, ActivityGaleria.class);
        GaleriaIntent.putExtra("IncidentetmpId", incidenciaRO.getTmpId());
        GaleriaIntent.putExtra("esInspeccionLevantada", true);
        startActivity(GaleriaIntent);
    }

    public void launchActivityFotoComentario(Uri uriImagen) {
        FotoModel fotoMd = new FotoModel();

        Uri uri = uriImagen;
        fotoMd.uri = uri;
        //fotoMd.bitmap = null;

        Intent FotoComentarioIntent = new Intent().setClass(
                activityMain, ActivityFotoComentario.class);
        FotoComentarioIntent.putExtra("imagen", fotoMd);
        FotoComentarioIntent.putExtra("IncidenciatmpId", incidenciaRO.getTmpId());
        FotoComentarioIntent.putExtra("esInspeccionLevantada", true);
        startActivity(FotoComentarioIntent);
    }

    public void MostrarCantidadImagenesInspeccionesLevantadas(RealmList<ImagenRO> listaImagenes) {
        int cant = listaImagenes.where().equalTo("inspeccionLevantada", true).findAll().size();
        this.btnGaleriaFotos.setTitle(getString(R.string.label_fotos) + " (" + cant + ")");
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private void updateIncidenteClose() {
        Realm realm = Realm.getInstance(myConfig);
        try {
            if (!realm.isInTransaction()) {
                realm.beginTransaction();
            }
            incidenciaRO.setClosed(true);
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            realm.close();
        } finally {
            realm.close();
        }
    }


    /**
     * Módulo para enviar la imagen de incidencia
     */
    private void sendImage(ImagenRO imagenRO, String tmpId, int idIncident, String api_token) {
        imagenRO.setDescripcion("Inspección Levantada - " + imagenRO.getDescripcion());

        File myDir = getActivity().getApplicationContext().getFilesDir();
        File file = new File(myDir, Constants.PATH_IMAGE_GALERY_INCIDENCIA + tmpId + "/"
                + imagenRO.getName());

        // Asignamos la imagen como Part
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file_image", file.getName(),
                requestFile);

        // Asignamos los campos como RequestBody :
        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"),
                imagenRO.getDescripcion());
        RequestBody incident_id = RequestBody.create(MediaType.parse("multipart/form-data"),
                String.valueOf(idIncident));

        RestClient restClient = new RestClient(Services.INSPECTION);

        Call<ResponseBody> call = restClient.iServices.addImageIncident(incident_id, description,
                body, api_token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    correctSend++;
                    Realm real = Realm.getInstance(myConfig);
                    try {
                        // Seteando el id de imagen :
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONObject messageResult = jsonObject.getJSONObject("message");
                        JSONObject imageResult = messageResult.getJSONObject("inspection_image");

                        ImagenRO imagenInc = real.where(ImagenRO.class).equalTo("name",
                                imageResult.getString("name")).findFirst();
                        if (imagenInc != null) {
                            if (!real.isInTransaction()) {
                                real.beginTransaction();
                            }
                            imagenInc.setId(imageResult.getInt("id"));
                            real.commitTransaction();
                        }
                        if (correctSend == totalToSend) {
                            dialog.dismiss();
                            // showDialogSuccess(getString(R.string.msg_success_send_inspection));
                            showDialogSuccess();
                        } else {
                            if (correctSend + failSend == totalToSend) {
                                dialog.dismiss();
                                //showDialogSuccess(getString(R.string.msg_success_send_inspe_error_image));
                                showDialogSuccess();
                            }
                        }
                    } catch (Exception e) {
                        real.close();
                        e.printStackTrace();
                        if (correctSend + failSend == totalToSend) {
                            dialog.dismiss();
                            showDialogSuccess("El levantamiento de inspección ha sido enviada satisfactoriamente. Sin embargo, algunas imágenes no han sido enviadas.");
                        }
                    } finally {
                        real.close();
                    }
                } else {
                    failSend++;
                    if (correctSend + failSend == totalToSend) {
                        dialog.dismiss();
                        showDialogSuccess("El levantamiento de inspección ha sido enviada satisfactoriamente. Sin embargo, algunas imágenes no han sido enviadas.");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                failSend++;
                if (correctSend + failSend == totalToSend) {
                    dialog.dismiss();
                    showDialogSuccess("El levantamiento de inspección ha sido enviada satisfactoriamente. Sin embargo, algunas imágenes no han sido enviadas.");
                }
            }
        });
    }


}
