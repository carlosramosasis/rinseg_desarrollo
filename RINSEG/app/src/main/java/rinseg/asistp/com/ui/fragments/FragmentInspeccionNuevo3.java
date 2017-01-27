package rinseg.asistp.com.ui.fragments;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import rinseg.asistp.com.models.AreaRO;
import rinseg.asistp.com.models.CompanyRO;
import rinseg.asistp.com.models.Inspeccion;
import rinseg.asistp.com.models.InspeccionRO;
import rinseg.asistp.com.models.InspectorRO;
import rinseg.asistp.com.models.SettingsInspectionRO;
import rinseg.asistp.com.models.TypesRO;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.ui.activities.ActivityMain;
import rinseg.asistp.com.utils.Generic;
import rinseg.asistp.com.utils.RinsegModule;

public class FragmentInspeccionNuevo3 extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    ActivityMain activityMain;

    Calendar calendarFechaLimite;
    Calendar newCalendar;

    EditText txtArea;
    TextView txtFecha;
    ImageButton btnFecha;
    Spinner spinnerTipoInspeccion;
    Spinner spinnerEmpresa;

    ArrayAdapter<TypesRO> adapterTipoInspeccion;
    ArrayAdapter<CompanyRO> adapterCompanies;

    InspeccionRO mInspc;
    private SettingsInspectionRO settingInsp;

    RealmConfiguration myConfig;
    Bundle bundle;

    public FragmentInspeccionNuevo3() {
        // Required empty public constructor
    }

    public static FragmentInspeccionNuevo3 newInstance(String param1, String param2) {
        FragmentInspeccionNuevo3 fragment = new FragmentInspeccionNuevo3();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

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
        View view = inflater.inflate(R.layout.fragment_inspeccion_nuevo3, container, false);

        setUpElements(view);
        setUpActions();

        LoadFormDefault();
        LoadInspeccion();

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        activityMain.toolbar.setTitle(R.string.title_nueva_inspeccion);
        activityMain.ShowButtonsBottom(true);

        activityMain.actualPaginaRop = 3;
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

    @Override
    public void onDestroyView() {
        activityMain.HideNumPagina();
        super.onDestroyView();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    //Proceso para cargar las vistas
    private void setUpElements(View v) {
        activityMain = ((ActivityMain) getActivity());

        newCalendar = Calendar.getInstance();
        calendarFechaLimite = Calendar.getInstance();

        txtArea = (EditText) v.findViewById(R.id.txt_inpeccion_3_area);
        txtFecha = (TextView) v.findViewById(R.id.txt_inpeccion_3_fecha);
        btnFecha = (ImageButton) v.findViewById(R.id.btn_inpeccion_3_fecha);
        spinnerTipoInspeccion = (Spinner) v.findViewById(R.id.spinner_inspeccion_3_tipo);
        spinnerEmpresa = (Spinner) v.findViewById(R.id.spinner_inspeccion_3_company);

        bundle = getArguments();

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
        activityMain.btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fInspPendiente2 = new FragmentInspeccionNuevo2();
                Bundle args = new Bundle();
                args.putString("InspTmpId", mInspc.getTmpId());
                args.putInt("InspId", mInspc.getId());
                fInspPendiente2.setArguments(args);
                activityMain.replaceFragment(fInspPendiente2, true,
                        R.anim.enter_from_right, R.anim.exit_to_right,
                        R.anim.enter_from_left, R.anim.exit_to_left);
            }
        });

        activityMain.btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( !ValidarFormulario() ) {
                    return;
                }
                SaveInspeccion();

                Fragment fInspPendiente4 = new FragmentInspeccionNuevo4();
                Bundle args = new Bundle();
                args.putString("InspTmpId", mInspc.getTmpId());
                args.putInt("InspId", mInspc.getId());
                fInspPendiente4.setArguments(args);
                activityMain.replaceFragment(fInspPendiente4, true,
                        R.anim.enter_from_left, R.anim.exit_to_left,
                        R.anim.enter_from_right, R.anim.exit_to_right);
            }
        });
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private void LoadFormDefault() {
        final Realm realm = Realm.getInstance(myConfig);
        try {
            settingInsp = realm.where(SettingsInspectionRO.class).findFirst();

            // Cargar Tipo de incidencia (Types)
            adapterTipoInspeccion = new ArrayAdapter<>(
                    getActivity(), R.layout.spinner_item, settingInsp.types);
            adapterTipoInspeccion.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerTipoInspeccion.setAdapter(adapterTipoInspeccion);

            // Cargar empresas :
            adapterCompanies = new ArrayAdapter<>(
                    getActivity(), R.layout.spinner_item, settingInsp.companies);
            adapterCompanies.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerEmpresa.setAdapter(adapterCompanies);
        } catch (Exception e) {
            e.printStackTrace();
            realm.close();
        } finally {
            realm.close();
        }
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private void LoadInspeccion() {
        if (bundle != null) {
            String tmpIdInsp = bundle.getString("InspTmpId", null);
            int id = bundle.getInt("InspId", 0);

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
                txtArea.setText(mInspc.getArea());

                newCalendar.setTime(mInspc.getDate());
                calendarFechaLimite.setTime(mInspc.getDate());
                txtFecha.setText(Generic.dateFormatter.format(calendarFechaLimite.getTime()));

                // recuperar tipo inspeccion
                for (int i = 0; i < settingInsp.types.size(); i++) {
                    TypesRO tmpType = settingInsp.types.get(i);
                    if (tmpType.getId() == mInspc.getTypeInspectionId()) {
                        spinnerTipoInspeccion.setSelection(i);
                        break;
                    }
                }

                // Recuperar empresa :
                for (int i = 0; i < settingInsp.companies.size(); i++) {
                    CompanyRO tmpCompany = settingInsp.companies.get(i);
                    if (tmpCompany.getId() == mInspc.getCompanyId()) {
                        spinnerEmpresa.setSelection(i);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                realm.close();
            } finally {
                realm.close();
            }
        }
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

    private boolean ValidarFormulario() {
        boolean resu = true;

        if (txtArea.getText().length() == 0) {
            txtArea.setError(getString(R.string.error_area_insp3));
            resu = false;
        }

        if (txtFecha.getText().length() == 0) {
            txtFecha.setError("");
            resu = false;
        }

        TypesRO tipoSelect = ((TypesRO) spinnerTipoInspeccion.getSelectedItem());
        if (tipoSelect.getId() == 0) {
            TextView txtGerencia = (TextView) spinnerTipoInspeccion.getSelectedView();
            txtGerencia.setError("");
            resu = false;
        }

        CompanyRO companySelect = ((CompanyRO) spinnerEmpresa.getSelectedItem());
        if ( companySelect.getId() == 0 ) {
            TextView txtCompany = (TextView) spinnerEmpresa.getSelectedView();
            txtCompany.setError("");
            resu = false;
        }
        return resu;
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private void SaveInspeccion() {
        Realm realm = Realm.getInstance(myConfig);
        try {
            TypesRO tipoSelect = ((TypesRO) spinnerTipoInspeccion.getSelectedItem());
            CompanyRO companySelect = ((CompanyRO) spinnerEmpresa.getSelectedItem());

            realm.beginTransaction();

            mInspc.setArea(txtArea.getText().toString().trim());
            mInspc.setDate(calendarFechaLimite.getTime());
            mInspc.setDateString(Generic.dateFormatterMySql.format(mInspc.getDate()));
            mInspc.setTypeInspectionId(tipoSelect.getId());
            mInspc.setCompanyId(companySelect.getId());

            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            realm.close();
        } finally {
            realm.close();
        }
    }
}