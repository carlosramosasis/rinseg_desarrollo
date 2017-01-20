package rinseg.asistp.com.ui.fragments;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import rinseg.asistp.com.models.EventItemsRO;
import rinseg.asistp.com.models.EventRO;
import rinseg.asistp.com.models.FrecuencieRO;
import rinseg.asistp.com.models.IncidenciaRO;
import rinseg.asistp.com.models.InspeccionRO;
import rinseg.asistp.com.models.RiskRO;
import rinseg.asistp.com.models.SecuencialRO;
import rinseg.asistp.com.models.SeveritiesRO;
import rinseg.asistp.com.models.TargetRO;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.ui.activities.ActivityGenerarIncidencia;
import rinseg.asistp.com.ui.activities.ActivityMain;
import rinseg.asistp.com.utils.Constants;
import rinseg.asistp.com.utils.Generic;
import rinseg.asistp.com.utils.RinsegModule;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentIncidenciaNuevo1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentIncidenciaNuevo1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentIncidenciaNuevo1 extends Fragment {
    /// // TODO: :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: VARIABLES :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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

    ArrayAdapter<EventRO> adapterTipoIncidencia;
    ArrayAdapter<FrecuencieRO> adapterFrecuencia;
    ArrayAdapter<SeveritiesRO> adapterTipoSeveridad;
    ArrayAdapter<TargetRO> adapterBlanco;

    Calendar calendarFechaLimite;

    Calendar newCalendar;

    IncidenciaRO mIncidencia;
    RealmConfiguration myConfig;

    Bundle bundle;

    /// // TODO: :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: CONSTRUCTORES :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    public FragmentIncidenciaNuevo1() {
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
    public static FragmentIncidenciaNuevo1 newInstance(String param1, String param2) {
        FragmentIncidenciaNuevo1 fragment = new FragmentIncidenciaNuevo1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    /// // TODO: :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: EVENTOS :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
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
        View view = inflater.inflate(R.layout.fragment_incidencia_nuevo1, container, false);


        setUpElements(view);
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
        activityMain.btnRight.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_chevron_right_black_24dp, 0);

        activityMain.actualPagina = 1;
        activityMain.totalPaginas = 2;
        activityMain.ShowNumPagina();
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

    /// // TODO: :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: METODOS :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    //Proceso para cargar las vistas
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

        //configuramos Realm
        Realm.init(this.getActivity().getApplicationContext());
        myConfig = new RealmConfiguration.Builder()
                .name("rinseg.realm")
                .schemaVersion(2)
                .modules(new RinsegModule())
                .deleteRealmIfMigrationNeeded()
                .build();

        bundle = getArguments();

    }

    //cargamos los eventos
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
                Fragment fIncidencia2 = new FragmentIncidenciaNuevo2();
                Bundle args = new Bundle();
                args.putString("InciTmpId", mIncidencia.getTmpId());
                args.putInt("InciId", mIncidencia.getId());
                fIncidencia2.setArguments(args);
                activityMain.replaceFragment(fIncidencia2, true, 0, 0, 0, 0);
            }
        });
    }

    private void ShowDatepicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendarFechaLimite = Calendar.getInstance();
                calendarFechaLimite.set(year, monthOfYear, dayOfMonth);
                txtFecha.setText(Generic.dateFormatter.format(calendarFechaLimite.getTime()));
                txtFecha.setError(null);

            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void saveIncidencia() {
        Realm realm = Realm.getInstance(myConfig);
        try {
            EventRO tipoIncidente = ((EventRO) spinnerTipoIncidencia.getSelectedItem());
            FrecuencieRO frecuencia = ((FrecuencieRO) spinnerFrecuencia.getSelectedItem());
            SeveritiesRO severidad = ((SeveritiesRO) spinnerSveridad.getSelectedItem());
            TargetRO blanco = ((TargetRO) spinnerBlanco.getSelectedItem());

            realm.beginTransaction();
            if (mIncidencia == null) {
                mIncidencia = realm.createObject(IncidenciaRO.class);

                // Obtener id Secuencial para id temporal
                int codigoSecuencial = 0;
                RealmResults<SecuencialRO> resultsSecuancial = realm.where(SecuencialRO.class).equalTo("tagTabla", Constants.tagIncidentes).findAll();
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
            mIncidencia.setFechalimiteString(Generic.dateFormatterMySql.format(mIncidencia.getFechalimite()));

            realm.commitTransaction();

            //Creamos la carpeta que contendra las imagenes
            boolean createdImageGalery = Generic.CrearCarpetaImagenesPorIncidencia(getActivity().getApplicationContext(), mIncidencia.getTmpId());



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
            adapterTipoIncidencia = new ArrayAdapter<EventRO>(getActivity(), R.layout.spinner_item, activityMain.sIns.events);
            adapterTipoIncidencia.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerTipoIncidencia.setAdapter(adapterTipoIncidencia);

            //cargar Frecuencia
            adapterFrecuencia = new ArrayAdapter<FrecuencieRO>(getActivity(), R.layout.spinner_item, activityMain.sIns.frecuencies);
            adapterFrecuencia.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerFrecuencia.setAdapter(adapterFrecuencia);

            //cargar Severidad
            adapterTipoSeveridad = new ArrayAdapter<SeveritiesRO>(getActivity(), R.layout.spinner_item, activityMain.sIns.severities);
            adapterTipoSeveridad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSveridad.setAdapter(adapterTipoSeveridad);

            //cargar Blanco (Target)
            adapterBlanco = new ArrayAdapter<TargetRO>(getActivity(), R.layout.spinner_item, activityMain.sIns.targets);
            adapterBlanco.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerBlanco.setAdapter(adapterBlanco);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void LoadIncidencia() {
        String tmpIdInsp = null;
        int id = 0;
        if (bundle != null) {
            tmpIdInsp = bundle.getString("InciTmpId", null);
            id = bundle.getInt("InciId", 0);

            final Realm realm = Realm.getInstance(myConfig);
            try {
                if (id != 0) {
                    mIncidencia = realm.where(IncidenciaRO.class).equalTo("id", id).findFirst();
                } else if (tmpIdInsp != null) {
                    mIncidencia = realm.where(IncidenciaRO.class).equalTo("tmpId", tmpIdInsp).findFirst();
                }
                if (mIncidencia == null) {
                    return;
                }

                // recuperar tipo incidencia
                for (int i = 0; i < activityMain.sIns.events.size(); i++) {
                    EventRO tmpEvent = activityMain.sIns.events.get(i);
                    if (tmpEvent.getId() == mIncidencia.getEventId()) {
                        spinnerTipoIncidencia.setSelection(i);
                        break;
                    }
                }

                // recuperar Descripcion
                txtDescripcion.setText(mIncidencia.getDescripcion());

                // recuperar frecuancia
                for (int i = 0; i < activityMain.sIns.frecuencies.size(); i++) {
                    FrecuencieRO tmpFrec = activityMain.sIns.frecuencies.get(i);
                    if (tmpFrec.getId() == mIncidencia.getFrecuenciaId()) {
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

            } catch (Exception e) {
                e.printStackTrace();
                realm.close();
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

        FrecuencieRO frecuenciaSelect = (FrecuencieRO) spinnerFrecuencia.getSelectedItem();
        SeveritiesRO severitiesSelect = (SeveritiesRO) spinnerSveridad.getSelectedItem();

        int valorFrecuencia = frecuenciaSelect.getValue();
        int valorSeveridad = severitiesSelect.getValue();

        int resultado = valorSeveridad * valorFrecuencia;
        txtNivelRiesgo.setText(String.valueOf(resultado));

        for (int i = 0; i < activityMain.sIns.risks.size(); i++) {
            RiskRO riesgo = activityMain.sIns.risks.get(i);
            if (resultado >= riesgo.getMinValue() && resultado <= riesgo.getMaxValue() && resultado != 0) {
                txtCatRiesgo.setText(riesgo.getDisplayName());
                break;
            }
        }


    }


}
