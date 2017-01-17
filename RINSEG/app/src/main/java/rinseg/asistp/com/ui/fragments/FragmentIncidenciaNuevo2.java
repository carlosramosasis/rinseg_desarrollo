package rinseg.asistp.com.ui.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import rinseg.asistp.com.models.EventItemsRO;
import rinseg.asistp.com.models.EventRO;
import rinseg.asistp.com.models.IncidenciaRO;
import rinseg.asistp.com.models.InspectorRO;
import rinseg.asistp.com.models.RacRO;
import rinseg.asistp.com.models.SecuencialRO;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.ui.activities.ActivityGenerarIncidencia;
import rinseg.asistp.com.ui.activities.ActivityMain;
import rinseg.asistp.com.utils.Constants;
import rinseg.asistp.com.utils.DialogRINSEG;
import rinseg.asistp.com.utils.Generic;
import rinseg.asistp.com.utils.RinsegModule;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentIncidenciaNuevo2.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentIncidenciaNuevo2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentIncidenciaNuevo2 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    ActivityGenerarIncidencia activityMain;

    ArrayAdapter<EventItemsRO> adapterActoCondicionSubStndr;
    ArrayAdapter<RacRO> adapterRac;
    ArrayAdapter<InspectorRO> adapterReportante;

    Spinner spinnerActoCondicionSubStndr;
    Spinner spinnerRac;
    Spinner spinnerReportante;

    EditText txtResponsables;
    EditText txtSupervisor;

    IncidenciaRO mIncidencia;
    RealmConfiguration myConfig;

    Bundle bundle;


    public FragmentIncidenciaNuevo2() {
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
    public static FragmentIncidenciaNuevo2 newInstance(String param1, String param2) {
        FragmentIncidenciaNuevo2 fragment = new FragmentIncidenciaNuevo2();
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
        View view = inflater.inflate(R.layout.fragment_incidencia_nuevo2, container, false);


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
        activityMain.btnLeft.setText(R.string.btn_atras);
        activityMain.btnRight.setText(R.string.btn_agreagar);
        activityMain.btnRight.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_plus_circle, 0);

        activityMain.actualPagina = 2;
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


    @Override
    public void onDestroyView() {
        //activityMain.ButtonBottomSetDefault();
        super.onDestroyView();
    }


    //Proceso para cargar las vistas
    private void setUpElements(View v) {
        activityMain = ((ActivityGenerarIncidencia) getActivity());

        spinnerActoCondicionSubStndr = (Spinner) v.findViewById(R.id.spinner_incidencia_2_tipo_acto_condicion);
        spinnerRac = (Spinner) v.findViewById(R.id.spinner_incidencia_2_rac);
        spinnerReportante = (Spinner) v.findViewById(R.id.spinner_incidencia_2_reportante);

        txtResponsables = (EditText) v.findViewById(R.id.txt_incidencia_2_responsable);
        txtSupervisor = (EditText) v.findViewById(R.id.txt_incidencia_2_supervisor);


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
        activityMain.btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveIncidencia(false);
                Fragment fIncidencia1 = new FragmentIncidenciaNuevo1();
                Bundle args = new Bundle();
                args.putString("InciTmpId", mIncidencia.getTmpId());
                args.putInt("InciId", mIncidencia.getId());
                fIncidencia1.setArguments(args);
                activityMain.replaceFragment(fIncidencia1, true, 0, 0, 0, 0);
            }
        });
        activityMain.btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ValidarFormulario()) {
                    return;
                }
                ConfirmarAgregarIncidente(getString(R.string.confirmar_agregar_inccidente));
            }
        });
    }


    private boolean ValidarFormulario() {
        boolean resu = true;

        EventItemsRO eventSelect;
        eventSelect = ((EventItemsRO) spinnerActoCondicionSubStndr.getSelectedItem());
        if (eventSelect.getId() == 0) {
            TextView txtActoCondicion = (TextView) spinnerActoCondicionSubStndr.getSelectedView();
            txtActoCondicion.setError("");
            resu = false;
        }


        RacRO racSelect;
        racSelect = ((RacRO) spinnerRac.getSelectedItem());
        if (racSelect.getId() == 0) {
            TextView txtRac = (TextView) spinnerRac.getSelectedView();
            txtRac.setError("");
            resu = false;
        }

        if (txtResponsables.getText().length() == 0) {
            txtResponsables.setError(getString(R.string.error_resp_inci2));
            resu = false;
        }

        if (txtSupervisor.getText().length() == 0) {
            txtSupervisor.setError(getString(R.string.error_supervisor_inci2));
            resu = false;
        }

        return resu;
    }

    private void saveIncidencia(boolean vincularAInspeccion) {
        Realm realm = Realm.getInstance(myConfig);
        try {

            EventItemsRO tipoActoCondicion = ((EventItemsRO) spinnerActoCondicionSubStndr.getSelectedItem());
            RacRO racSelect = (RacRO) spinnerRac.getSelectedItem();
            InspectorRO reportanteSelect = (InspectorRO) spinnerReportante.getSelectedItem();

            realm.beginTransaction();
            mIncidencia.setEventItemId(tipoActoCondicion.getId());
            mIncidencia.setRacId(racSelect.getId());
            mIncidencia.setReportanteId(reportanteSelect.getId());
            mIncidencia.setResponsable(txtResponsables.getText().toString().trim());
            mIncidencia.setSupervisor(txtSupervisor.getText().toString().trim());

            if (vincularAInspeccion) {
                activityMain.mInspeccion.listaIncidencias.add(mIncidencia);
            }

            realm.commitTransaction();


        } catch (Exception e) {
            e.printStackTrace();
            realm.close();
        } finally {
            realm.close();
        }
    }

    private void LoadFormDefault() {
        try {

            //cargar rac
            adapterRac = new ArrayAdapter<RacRO>(getActivity(), R.layout.spinner_item, activityMain.sIns.racs);
            adapterRac.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerRac.setAdapter(adapterRac);

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

                //cargar los items segun acto inseguro , condicion insegura.
                for (int i = 0; i < activityMain.sIns.events.size(); i++) {
                    EventRO event = activityMain.sIns.events.get(i);
                    if (mIncidencia.getEventId() == event.getId()) {
                        adapterActoCondicionSubStndr = new ArrayAdapter<EventItemsRO>(getActivity(), R.layout.spinner_item, event.eventItems);
                        adapterActoCondicionSubStndr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerActoCondicionSubStndr.setAdapter(adapterActoCondicionSubStndr);

                        // recuperar Acto o condicion SubStandar
                        for (int j = 0; j < event.eventItems.size(); j++) {
                            EventItemsRO tmpEventItem = event.eventItems.get(j);
                            if (tmpEventItem.getId() == mIncidencia.getEventItemId()) {
                                spinnerActoCondicionSubStndr.setSelection(j);
                                break;
                            }
                        }

                    }
                }


                // recuperar Rac
                for (int i = 0; i < activityMain.sIns.racs.size(); i++) {
                    RacRO tmpRac = activityMain.sIns.racs.get(i);
                    if (tmpRac.getId() == mIncidencia.getRacId()) {
                        spinnerRac.setSelection(i);
                        break;
                    }
                }


                // cargar reportante
                adapterReportante = new ArrayAdapter<InspectorRO>(getActivity(), R.layout.spinner_item, activityMain.mInspeccion.listaInspectores);
                adapterReportante.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerReportante.setAdapter(adapterReportante);


                // recuperar reportante
                for (int i = 0; i < activityMain.mInspeccion.listaInspectores.size(); i++) {
                    InspectorRO tmpInspector = activityMain.mInspeccion.listaInspectores.get(i);
                    if (tmpInspector.getId() == mIncidencia.getReportanteId()) {
                        spinnerReportante.setSelection(i);
                        break;
                    }
                }


                txtResponsables.setText(mIncidencia.getResponsable());
                txtSupervisor.setText(mIncidencia.getSupervisor());


            } catch (Exception e) {
                e.printStackTrace();
                realm.close();
            } finally {
                realm.close();
            }


        }
    }

    private void ConfirmarAgregarIncidente(String msg) {
        final DialogRINSEG dialogConfirmarCierre = new DialogRINSEG(getActivity());
        dialogConfirmarCierre.show();
        dialogConfirmarCierre.setBody(msg);
        dialogConfirmarCierre.btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogConfirmarCierre.dismiss();
            }
        });
        dialogConfirmarCierre.btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogConfirmarCierre.dismiss();
                saveIncidencia(true);
                activityMain.finish();
            }
        });

    }

}
