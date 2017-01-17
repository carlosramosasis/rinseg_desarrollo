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
import rinseg.asistp.com.models.Inspeccion;
import rinseg.asistp.com.models.InspeccionRO;
import rinseg.asistp.com.models.InspectorRO;
import rinseg.asistp.com.models.SettingsInspectionRO;
import rinseg.asistp.com.models.TypesRO;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.ui.activities.ActivityMain;
import rinseg.asistp.com.utils.Generic;
import rinseg.asistp.com.utils.RinsegModule;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentInspeccionNuevo3.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentInspeccionNuevo3#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentInspeccionNuevo3 extends Fragment {
    ///Todo ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: VARIABLES ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
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
    EditText txtEmpresa;

    ArrayAdapter<TypesRO> adapterTipoInspeccion;

    InspeccionRO mInspc;
    private SettingsInspectionRO settingInsp;

    RealmConfiguration myConfig;
    Bundle bundle;


    ///Todo ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: CONSTRUCTORES ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    public FragmentInspeccionNuevo3() {
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
    public static FragmentInspeccionNuevo3 newInstance(String param1, String param2) {
        FragmentInspeccionNuevo3 fragment = new FragmentInspeccionNuevo3();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    ///Todo ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: EVENTOS ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
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

    @Override
    public void onDestroyView() {
        activityMain.HideNumPagina();
        super.onDestroyView();
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


    ///Todo ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: METODOS ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    //Proceso para cargar las vistas
    private void setUpElements(View v) {
        activityMain = ((ActivityMain) getActivity());

        newCalendar = Calendar.getInstance();
        calendarFechaLimite = Calendar.getInstance();

        txtArea = (EditText) v.findViewById(R.id.txt_inpeccion_3_area);
        txtFecha = (TextView) v.findViewById(R.id.txt_inpeccion_3_fecha);
        btnFecha = (ImageButton) v.findViewById(R.id.btn_inpeccion_3_fecha);
        spinnerTipoInspeccion = (Spinner) v.findViewById(R.id.spinner_inspeccion_3_tipo);
        txtEmpresa = (EditText) v.findViewById(R.id.txt_inpeccion_3_empresa);

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
                activityMain.replaceFragment(fInspPendiente2, true, R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_left, R.anim.exit_to_left);
            }
        });
        activityMain.btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ValidarFormulario()){
                    return;
                }

                SaveInspeccion();


                Fragment fInspPendiente4 = new FragmentInspeccionNuevo4();
                Bundle args = new Bundle();
                args.putString("InspTmpId", mInspc.getTmpId());
                args.putInt("InspId", mInspc.getId());
                fInspPendiente4.setArguments(args);
                activityMain.replaceFragment(fInspPendiente4, true, R.anim.enter_from_left, R.anim.exit_to_left, R.anim.enter_from_right, R.anim.exit_to_right);
            }
        });
    }

    private void LoadFormDefault() {
        final Realm realm = Realm.getInstance(myConfig);
        try {
            settingInsp = realm.where(SettingsInspectionRO.class).findFirst();

            //cargar Tipo de incidencia  (Types)
            adapterTipoInspeccion = new ArrayAdapter<TypesRO>(getActivity(), R.layout.spinner_item, settingInsp.types);
            adapterTipoInspeccion.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerTipoInspeccion.setAdapter(adapterTipoInspeccion);

        } catch (Exception e) {
            e.printStackTrace();
            realm.close();
        }finally {
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

                txtEmpresa.setText(mInspc.getCompanyString());



            } catch (Exception e) {
                e.printStackTrace();
                realm.close();
            } finally {
                realm.close();
            }


        }
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

        TypesRO tipoSelect;
        tipoSelect = ((TypesRO) spinnerTipoInspeccion.getSelectedItem());
        if (tipoSelect.getId() == 0) {
            TextView txtGerencia = (TextView) spinnerTipoInspeccion.getSelectedView();
            txtGerencia.setError("");
            resu = false;
        }

        if (txtEmpresa.getText().length() == 0) {
            txtEmpresa.setError(getString(R.string.error_empresa_insp3));
            resu = false;
        }

        return resu;
    }

    private void SaveInspeccion() {
        Realm realm = Realm.getInstance(myConfig);
        try {
            TypesRO tipoSelect = ((TypesRO) spinnerTipoInspeccion.getSelectedItem());

            realm.beginTransaction();
            mInspc.setArea(txtArea.getText().toString().trim());
            mInspc.setDate(calendarFechaLimite.getTime());
            mInspc.setDateString(Generic.dateFormatterMySql.format(mInspc.getDate()));
            mInspc.setTypeInspectionId(tipoSelect.getId());
            mInspc.setCompanyString(txtEmpresa.getText().toString().trim());
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            realm.close();
        } finally {
            realm.close();
        }
    }


}
