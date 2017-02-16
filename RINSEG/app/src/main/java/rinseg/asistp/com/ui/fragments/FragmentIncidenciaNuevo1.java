package rinseg.asistp.com.ui.fragments;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;
import rinseg.asistp.com.models.EventRO;
import rinseg.asistp.com.models.FrecuencieRO;
import rinseg.asistp.com.models.IncidenciaRO;
import rinseg.asistp.com.models.RiskRO;
import rinseg.asistp.com.models.SecuencialRO;
import rinseg.asistp.com.models.SeveritiesRO;
import rinseg.asistp.com.models.TargetRO;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.ui.activities.ActivityGenerarIncidencia;
import rinseg.asistp.com.utils.Constants;
import rinseg.asistp.com.utils.Generic;
import rinseg.asistp.com.utils.RinsegModule;

public class FragmentIncidenciaNuevo1 extends Fragment {

    private static final String ARG_ID = "idIncidencia";

    private String idIncidencia = "";

    private OnFragmentInteractionListener mListener;

    ActivityGenerarIncidencia activityMain;

    Spinner spinnerTipoIncidencia;
    EditText txtDescripcion;
    Spinner spinnerFrecuencia;
    Spinner spinnerSveridad;
    Spinner spinnerBlanco;
    TextView txtFecha;
    ImageButton btnFecha;

    TextView txtCatRiesgo;
    TextView txtNivelRiesgo;
    ProgressBar progressRiesgo;

    ArrayAdapter<EventRO> adapterTipoIncidencia;
    ArrayAdapter<FrecuencieRO> adapterFrecuencia;
    ArrayAdapter<SeveritiesRO> adapterTipoSeveridad;
    ArrayAdapter<TargetRO> adapterBlanco;

    Calendar calendarFechaLimite;

    Calendar newCalendar;

    IncidenciaRO mIncidencia;
    RealmConfiguration myConfig;

    Boolean isNewInc = false;

    int nivelRiesgo;
    String categoriaRiesgo;

    int progresMax = 0;


    public FragmentIncidenciaNuevo1() {
    }

    public static FragmentIncidenciaNuevo1 newInstance(String idIncidencia) {
        FragmentIncidenciaNuevo1 fragment = new FragmentIncidenciaNuevo1();
        Bundle args = new Bundle();
        args.putString(ARG_ID, idIncidencia);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idIncidencia = getArguments().getString(ARG_ID, "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_incidencia_nuevo1, container, false);

        setUpElements(view);
        inicializarProgressRiesgo();
        setUpActions();
        LoadFormDefault();
        LoadIncidencia();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        activityMain.toolbarGenerarIncidencia.setTitle(R.string.title_nuevo_incidente);
        activityMain.btnLeft.setText(R.string.btn_cancelar);
        activityMain.btnRight.setText(R.string.btn_ccontinuar);
        activityMain.btnRight.setCompoundDrawablesWithIntrinsicBounds(
                0, 0, R.drawable.ic_chevron_right_black_24dp, 0);

        activityMain.actualPagina = 1;
        activityMain.totalPaginas = 2;
        activityMain.ShowNumPagina();
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

    /**
     * Proceso para cargar las vistas
     */
    private void setUpElements(View v) {
        activityMain = ((ActivityGenerarIncidencia) getActivity());

        newCalendar = Calendar.getInstance();
        calendarFechaLimite = Calendar.getInstance();

        spinnerTipoIncidencia = (Spinner) v.findViewById(R.id.spinner_incidencia_1_tipo_incidencia);
        txtDescripcion = (EditText) v.findViewById(R.id.text_incidencia_1_descripcion);
        spinnerFrecuencia = (Spinner) v.findViewById(R.id.spinner_incidencia_1_frecuencia);
        spinnerSveridad = (Spinner) v.findViewById(R.id.spinner_incidencia_1_severidad);
        spinnerBlanco = (Spinner) v.findViewById(R.id.spinner_incidencia_1_blanco);
        txtFecha = (TextView) v.findViewById(R.id.txt_incidencia_1_fecha);
        btnFecha = (ImageButton) v.findViewById(R.id.btn_incidencia_1_calendar);

        txtCatRiesgo = (TextView) v.findViewById(R.id.txt_incidencia_1_categoria_riesgo);
        txtNivelRiesgo = (TextView) v.findViewById(R.id.txt_incidencia_1_nivel_riesgo);
        progressRiesgo = (ProgressBar) v.findViewById(R.id.progress_incidencia_1_riesgo);

        // Configuramos Realm
        Realm.init(this.getActivity().getApplicationContext());
        myConfig = new RealmConfiguration.Builder()
                .name("rinseg.realm")
                .schemaVersion(2)
                .modules(new RinsegModule())
                .deleteRealmIfMigrationNeeded()
                .build();
    }

    private void inicializarProgressRiesgo() {
        Realm realm = Realm.getInstance(myConfig);
        try {
            int maxFrecuencia = activityMain.sIns.frecuencies.where().max("value").intValue();
            int maxSeveridad = activityMain.sIns.severities.where().max("value").intValue();

            int resu = maxFrecuencia * maxSeveridad;

            progressRiesgo.setMax(100);


        } catch (Exception e) {
            e.printStackTrace();
            realm.close();
        } finally {
            realm.close();
        }
    }

    /**
     * Múdulo para escuchar las accoiones
     */
    private void setUpActions() {
        txtFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDatepicker();
            }
        });
        btnFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDatepicker();
            }
        });

        spinnerFrecuencia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CalcularRiesgo();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinnerSveridad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CalcularRiesgo();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        activityMain.btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityMain.finish();
            }
        });

        activityMain.btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ValidarFormulario()) {
                    return;
                }
                saveIncidencia();

                Fragment f = FragmentIncidenciaNuevo2.newInstance(mIncidencia.getTmpId(), isNewInc);
                activityMain.replaceFragment(f, true, 0, 0, 0, 0);

                /*Fragment fIncidencia2 = new FragmentIncidenciaNuevo2();
                Bundle args = new Bundle();
                args.putString("InciTmpId", mIncidencia.getTmpId());
                args.putInt("InciId", mIncidencia.getId());
                fIncidencia2.setArguments(args);
                activityMain.replaceFragment(fIncidencia2, true, 0, 0, 0, 0);*/
            }
        });
    }

    private void ShowDatepicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendarFechaLimite = Calendar.getInstance();
                        calendarFechaLimite.set(year, monthOfYear, dayOfMonth);
                        txtFecha.setText(Generic.dateFormatter.format(calendarFechaLimite.getTime()));
                        txtFecha.setError(null);
                    }
                }, newCalendar.get(Calendar.YEAR),
                newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private void saveIncidencia() {
        Realm realm = Realm.getInstance(myConfig);
        try {
            EventRO tipoIncidente = ((EventRO) spinnerTipoIncidencia.getSelectedItem());
            FrecuencieRO frecuencia = ((FrecuencieRO) spinnerFrecuencia.getSelectedItem());
            SeveritiesRO severidad = ((SeveritiesRO) spinnerSveridad.getSelectedItem());
            TargetRO blanco = ((TargetRO) spinnerBlanco.getSelectedItem());

            realm.beginTransaction();

            if (mIncidencia == null) {
                isNewInc = true;
                mIncidencia = realm.createObject(IncidenciaRO.class);

                // Obtener id Secuencial para id temporal
                int codigoSecuencial = 0;
                RealmResults<SecuencialRO> resultsSecuancial = realm.where(SecuencialRO.class)
                        .equalTo("tagTabla", Constants.tagIncidentes).findAll();
                if (resultsSecuancial.isEmpty() && resultsSecuancial.size() < 1) {
                    codigoSecuencial += 1;
                    mIncidencia.setTmpId(Generic.FormatTmpId(codigoSecuencial));
                } else {
                    codigoSecuencial = (resultsSecuancial.max("codigo").intValue() + 1);
                    mIncidencia.setTmpId(Generic.FormatTmpId(codigoSecuencial));
                }
                SecuencialRO mSecuencial = realm.createObject(SecuencialRO.class);
                mSecuencial.setCodigo(codigoSecuencial);
                mSecuencial.setTagTabla(Constants.tagIncidentes);
            }
            mIncidencia.setEventId(tipoIncidente.getId());
            mIncidencia.setDescripcion(txtDescripcion.getText().toString().trim());
            mIncidencia.setFrecuenciaId(frecuencia.getId());
            mIncidencia.setSeveridadId(severidad.getId());
            mIncidencia.setBlancoId(blanco.getId());
            mIncidencia.setFechalimite(calendarFechaLimite.getTime());
            mIncidencia.setFechalimiteString(Generic.dateFormatterMySql.format(
                    mIncidencia.getFechalimite()));
            mIncidencia.setRiesgo(nivelRiesgo);
            mIncidencia.setCategoria(categoriaRiesgo);

            if (isNewInc) {
                activityMain.mInspeccion.listaIncidencias.add(mIncidencia);
            }
            realm.commitTransaction();

            //Creamos la carpeta que contendra las imagenes
            boolean createdImageGalery = Generic.CrearCarpetaImagenesPorIncidencia(getActivity()
                    .getApplicationContext(), mIncidencia.getTmpId());
        } catch (Exception e) {
            e.printStackTrace();
            realm.close();
        } finally {
            realm.close();
        }
    }

    private void LoadFormDefault() {
        try {
            //cargar Tipo de incidencia  (Events)
            adapterTipoIncidencia = new ArrayAdapter<>(
                    getActivity(), R.layout.spinner_item, activityMain.sIns.events);
            adapterTipoIncidencia.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerTipoIncidencia.setAdapter(adapterTipoIncidencia);

            //cargar Frecuencia
            adapterFrecuencia = new ArrayAdapter<>(
                    getActivity(), R.layout.spinner_item, activityMain.sIns.frecuencies);
            adapterFrecuencia.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerFrecuencia.setAdapter(adapterFrecuencia);

            //cargar Severidad
            adapterTipoSeveridad = new ArrayAdapter<>(
                    getActivity(), R.layout.spinner_item, activityMain.sIns.severities);
            adapterTipoSeveridad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSveridad.setAdapter(adapterTipoSeveridad);

            //cargar Blanco (Target)
            adapterBlanco = new ArrayAdapter<>(
                    getActivity(), R.layout.spinner_item, activityMain.sIns.targets);
            adapterBlanco.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerBlanco.setAdapter(adapterBlanco);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Módulo para recuperar la incidencia seleccionada
     */
    private void LoadIncidencia() {
        if (!idIncidencia.equals("")) {
            Realm realm = Realm.getInstance(myConfig);
            try {
                mIncidencia = realm.where(IncidenciaRO.class)
                        .equalTo("tmpId", idIncidencia).findFirst();
                if (mIncidencia != null) {
                    // Recuperamos el tipo de incidencia :
                    for (int i = 0; i < activityMain.sIns.events.size(); i++) {
                        if (activityMain.sIns.events.get(i).getId() == mIncidencia.getEventId()) {
                            spinnerTipoIncidencia.setSelection(i);
                            break;
                        }
                    }
                    // Recuperamos descripción :
                    txtDescripcion.setText(mIncidencia.getDescripcion());
                    // Recuperamos el tipo de frecuencia :
                    for (int i = 0; i < activityMain.sIns.frecuencies.size(); i++) {
                        if (activityMain.sIns.frecuencies.get(i).getId() ==
                                mIncidencia.getFrecuenciaId()) {
                            spinnerFrecuencia.setSelection(i);
                            break;
                        }
                    }
                    // recuperar severidad
                    for (int i = 0; i < activityMain.sIns.severities.size(); i++) {
                        SeveritiesRO tmpSev = activityMain.sIns.severities.get(i);
                        if (tmpSev.getId() == mIncidencia.getSeveridadId()) {
                            spinnerSveridad.setSelection(i);
                            break;
                        }
                    }
                    // recuperar Blanco
                    for (int i = 0; i < activityMain.sIns.targets.size(); i++) {
                        TargetRO tmpBlanco = activityMain.sIns.targets.get(i);
                        if (tmpBlanco.getId() == mIncidencia.getBlancoId()) {
                            spinnerBlanco.setSelection(i);
                            break;
                        }
                    }
                    newCalendar.setTime(mIncidencia.getFechalimite());
                    calendarFechaLimite.setTime(mIncidencia.getFechalimite());
                    txtFecha.setText(Generic.dateFormatter.format(calendarFechaLimite.getTime()));

                    CalcularRiesgo();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                realm.close();
            }
        }
    }

    private boolean ValidarFormulario() {
        boolean resu = true;

        EventRO eventSelect;
        eventSelect = ((EventRO) spinnerTipoIncidencia.getSelectedItem());
        if (eventSelect.getId() == 0) {
            TextView txtEmpresa = (TextView) spinnerTipoIncidencia.getSelectedView();
            txtEmpresa.setError("");
            resu = false;
        }

        if (txtDescripcion.getText().length() == 0) {
            txtDescripcion.setError(getString(R.string.error_desc_inci1));
            resu = false;
        }

        FrecuencieRO frecSelect;
        frecSelect = ((FrecuencieRO) spinnerFrecuencia.getSelectedItem());
        if (frecSelect.getId() == 0) {
            TextView txtFrec = (TextView) spinnerFrecuencia.getSelectedView();
            txtFrec.setError("");
            resu = false;
        }

        SeveritiesRO severitiesSelect;
        severitiesSelect = ((SeveritiesRO) spinnerSveridad.getSelectedItem());
        if (severitiesSelect.getId() == 0) {
            TextView txtSev = (TextView) spinnerSveridad.getSelectedView();
            txtSev.setError("");
            resu = false;
        }

        TargetRO blancoSelect;
        blancoSelect = ((TargetRO) spinnerBlanco.getSelectedItem());
        if (blancoSelect.getId() == 0) {
            TextView txtBlanco = (TextView) spinnerBlanco.getSelectedView();
            txtBlanco.setError("");
            resu = false;
        }

        if (txtFecha.getText().length() == 0) {
            txtFecha.setError(getString(R.string.error_fecha_inci11));
            resu = false;
        }
        return resu;
    }

    void CalcularRiesgo() {
        txtCatRiesgo.setText(getString(R.string.rop_c1_texto_default));
        txtNivelRiesgo.setText(getString(R.string.rop_c1_texto_default));
        progressRiesgo.setProgress(0);

        FrecuencieRO frecuenciaSelect = (FrecuencieRO) spinnerFrecuencia.getSelectedItem();
        SeveritiesRO severitiesSelect = (SeveritiesRO) spinnerSveridad.getSelectedItem();

        int valorFrecuencia = frecuenciaSelect.getValue();
        int valorSeveridad = severitiesSelect.getValue();

        nivelRiesgo = valorSeveridad * valorFrecuencia;
        txtNivelRiesgo.setText(String.valueOf(nivelRiesgo));

        Double valorMaximoBase = 0.0;
        int nivelRiesgoTermometro = 0;
        Double nivelRiesgoEquivalente = 0.0;


        for (int i = 0; i < activityMain.sIns.risks.size(); i++) {
            RiskRO riesgo = activityMain.sIns.risks.get(i);
            if (nivelRiesgo >= riesgo.getMinValue()
                    && nivelRiesgo <= riesgo.getMaxValue()
                    && nivelRiesgo != 0) {

                categoriaRiesgo = riesgo.getDisplayName();
                txtCatRiesgo.setText(categoriaRiesgo);

                Double rango = ((double)riesgo.getMaxValue() - riesgo.getMinValue());
                Double unidad = (rango / 25);

                nivelRiesgoTermometro = nivelRiesgo - riesgo.getMinValue();
                nivelRiesgoEquivalente = nivelRiesgoTermometro / unidad;
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


       // progressRiesgo.setProgress(nivelRiesgo);
    }
}
