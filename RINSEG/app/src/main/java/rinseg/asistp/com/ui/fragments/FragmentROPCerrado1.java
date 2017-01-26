package rinseg.asistp.com.ui.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import rinseg.asistp.com.adapters.AccionPreventivaAdapter;
import rinseg.asistp.com.adapters.AccionPreventivaDetalleAdapter;
import rinseg.asistp.com.adapters.RopAdapter;
import rinseg.asistp.com.models.AccionPreventiva;
import rinseg.asistp.com.models.AreaRO;
import rinseg.asistp.com.models.CompanyRO;
import rinseg.asistp.com.models.EventItemsRO;
import rinseg.asistp.com.models.EventRO;
import rinseg.asistp.com.models.ROP;
import rinseg.asistp.com.models.RiskRO;
import rinseg.asistp.com.models.SettingsRopRO;
import rinseg.asistp.com.models.TargetRO;
import rinseg.asistp.com.models.User;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.ui.activities.ActivityMain;
import rinseg.asistp.com.ui.activities.ActivityRopCerradoDetalle;
import rinseg.asistp.com.utils.Generic;
import rinseg.asistp.com.utils.RinsegModule;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentROPCerrado1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentROPCerrado1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentROPCerrado1 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    ActivityRopCerradoDetalle activityMain;

    Bundle bundle;

    ROP mRop;
    RealmConfiguration myConfig;

    private User usuario;
    SettingsRopRO sRop;

    TextView txtCodigo;
    TextView txtAprobado;
    TextView txtRevision;
    TextView txtPotencialPerdida;
    TextView txtTipoEvento;
    TextView txtBlanco;
    TextView txtAreaResopnsable;
    TextView txtLugarExacto;
    TextView txtEmpresa;
    TextView txtFecha;
    TextView txtHora;
    TextView txtDescripcion;
    TextView txtActoSubestandar;
    RecyclerView recyclerAccionesInmediatasRealizadas;
    private RecyclerView.Adapter accionPreventivaAdapter;
    private RecyclerView.LayoutManager lManager;
    private List<AccionPreventiva> listaAccionesPreventivas = new ArrayList<>();
    TextView txtCompromisoTrabajador;
    TextView txtReportante;
    TextView txtEmpresaReportante;
    TextView txtSupervisor;
    TextView txtEmpresaSupervisor;
    TextView txtRequiereInvestigacion;

    Calendar newDateForROP;

    public FragmentROPCerrado1() {
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
    public static FragmentROPCerrado1 newInstance(String param1, String param2) {
        FragmentROPCerrado1 fragment = new FragmentROPCerrado1();
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
        View view = inflater.inflate(R.layout.fragment_rop_cerrado1, container, false);


        setUpElements(view);
        setUpActions();

        LoadSettingRop();
        LoadUser();

        LoadRop();

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        activityMain.toolbar.setTitle(R.string.title_rop);

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

//// TODO: ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: METODOS ::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    //Proceso para cargar las vistas
    private void setUpElements(View v) {
        activityMain = ((ActivityRopCerradoDetalle) getActivity());

        bundle = getArguments();

        newDateForROP = Calendar.getInstance();

        txtCodigo = (TextView) v.findViewById(R.id.rd_txt_codigo);
        txtAprobado = (TextView) v.findViewById(R.id.rd_txt_aprobado);
        txtRevision = (TextView) v.findViewById(R.id.rd_txt_revision);
        txtPotencialPerdida = (TextView) v.findViewById(R.id.rd_txt_potencial_perdida);
        txtTipoEvento = (TextView) v.findViewById(R.id.rd_txt_tipo_evento);
        txtBlanco = (TextView) v.findViewById(R.id.rd_txt_blanco);
        txtAreaResopnsable = (TextView) v.findViewById(R.id.rd_txt_area_responsable);
        txtLugarExacto = (TextView) v.findViewById(R.id.rd_txt_lugar_exacto);
        txtEmpresa = (TextView) v.findViewById(R.id.rd_txt_empresa);
        txtFecha = (TextView) v.findViewById(R.id.rd_txt_fecha);
        txtHora = (TextView) v.findViewById(R.id.rd_txt_hora);
        txtDescripcion = (TextView) v.findViewById(R.id.rd_txt_descripcion);
        txtActoSubestandar = (TextView) v.findViewById(R.id.rd_txt_acto_subestandar);

        //configuracion para el recicler
        recyclerAccionesInmediatasRealizadas = (RecyclerView) v.findViewById(R.id.rd_recycler_acciones_realizadas);
        recyclerAccionesInmediatasRealizadas.setHasFixedSize(true);
        // usar administrador para linearLayout
        lManager = new LinearLayoutManager(this.getActivity().getApplicationContext());
        recyclerAccionesInmediatasRealizadas.setLayoutManager(lManager);
        // Crear un nuevo Adaptador
        accionPreventivaAdapter = new AccionPreventivaDetalleAdapter(listaAccionesPreventivas);
        recyclerAccionesInmediatasRealizadas.setAdapter(accionPreventivaAdapter);


        txtCompromisoTrabajador = (TextView) v.findViewById(R.id.rd_txt_compromiso);
        txtReportante = (TextView) v.findViewById(R.id.rd_txt_reportante);
        txtEmpresaReportante = (TextView) v.findViewById(R.id.rd_txt_empresa_de_reportante);
        txtSupervisor = (TextView) v.findViewById(R.id.rd_txt_supervisor);
        txtEmpresaSupervisor = (TextView) v.findViewById(R.id.rd_txt_empresa_de_supervisor);
        txtRequiereInvestigacion = (TextView) v.findViewById(R.id.rd_txt_requiere_investigacion);


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

    }

    private void LoadSettingRop() {
        final Realm realm = Realm.getInstance(myConfig);
        try {
            sRop = realm.where(SettingsRopRO.class).findFirst();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            realm.close();
        }

    }

    private void LoadUser() {
        final Realm realm = Realm.getInstance(myConfig);
        try {
            usuario = realm.where(User.class).findFirst();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            realm.close();
        }

    }


    private void LoadRop() {

        int idRop = 0;
        if (bundle != null) {
            idRop = bundle.getInt("ROPId", 0);

            //ROP tmpRop = new ROP();
            if (idRop != 0) {
                final Realm realm = Realm.getInstance(myConfig);
                try {
                    mRop = realm.where(ROP.class).equalTo("id", idRop).findFirst();
                    if (mRop == null) {
                        return;
                    }


                    txtCodigo.setText(String.valueOf(mRop.getId()));


                    // recuperar potencial de perdida
                    for (int i = 0; i < sRop.risks.size(); i++) {
                        RiskRO tmpRisk = sRop.risks.get(i);
                        if (tmpRisk.getId() == mRop.getRiskId()) {
                            txtPotencialPerdida.setText(tmpRisk.getDisplayName());
                            break;
                        }
                    }

                    // recuperar tipo de evento
                    for (int i = 0; i < sRop.events.size(); i++) {
                        EventRO tmpEvent = sRop.events.get(i);
                        if (tmpEvent.getId() == mRop.getEventId()) {
                            txtTipoEvento.setText(tmpEvent.getDisplayName());
                            break;
                        }
                    }

                    // recuperar Blanco
                    for (int i = 0; i < sRop.targets.size(); i++) {
                        TargetRO tmpTarget = sRop.targets.get(i);
                        if (tmpTarget.getId() == mRop.getTargetId()) {
                            txtBlanco.setText(tmpTarget.getDisplayName());
                            break;
                        }
                    }

                    // recuperar area responsable
                    for (int i = 0; i < sRop.areas.size(); i++) {
                        AreaRO tmpArea = sRop.areas.get(i);
                        if (tmpArea.getId() == mRop.getAreaId()) {
                            txtAreaResopnsable.setText(tmpArea.getDisplayName());
                            break;
                        }
                    }

                    // recuperar lugar exacto
                    txtLugarExacto.setText(mRop.getEventPlace());

                    // recuperar Empresa
                    for (int i = 0; i < sRop.companies.size(); i++) {
                        CompanyRO tmpCompany = sRop.companies.get(i);
                        if (tmpCompany.getId() == mRop.getCompanyId()) {
                            txtEmpresa.setText(tmpCompany.getDisplayName());
                            break;
                        }
                    }

                    // recuperar Fecha y Hora
                    newDateForROP.setTime(mRop.getEventDate());
                    txtFecha.setText(Generic.dateFormatter.format(newDateForROP.getTime()));
                    txtHora.setText(Generic.timeFormatter.format(newDateForROP.getTime()));

                    // recuperar descripcion
                    txtDescripcion.setText(mRop.getEventDescription());

                    // recuperar acto subestandar
                    txtActoSubestandar.setText("");
                    for (int i = 0; i < mRop.listaEventItems.size(); i++) {
                        String actosSubestandars = txtActoSubestandar.getText().toString();
                        if (i > 0) {
                            actosSubestandars += "\n";
                        }

                        txtActoSubestandar.setText(actosSubestandars + "- " + mRop.listaEventItems.get(i).getName() + ".");
                    }


                    // recuperar accion inmediata realizada
                    for (int i = 0; i < mRop.listaAccionPreventiva.size(); i++) {
                        AccionPreventiva tAccionPrev = mRop.listaAccionPreventiva.get(i);
                        listaAccionesPreventivas.add(tAccionPrev);
                        accionPreventivaAdapter.notifyDataSetChanged();
                    }


                    // recuperar Reportante
                    if (usuario != null) {
                        txtReportante.setText(usuario.getName() + " " + usuario.getLastname());

                        // recuperar Empresa reposrtante
                        for (int i = 0; i < sRop.companies.size(); i++) {
                            CompanyRO tmpCompany = sRop.companies.get(i);
                            if (tmpCompany.getId() == usuario.getCompany_id()) {
                                txtEmpresaReportante.setText(tmpCompany.getDisplayName());
                                break;
                            }
                        }
                    }

                    //recuperar datos de supervisor
                    txtSupervisor.setText(mRop.getSupervisorName());
                    // recuperar Empresa supervisor
                    for (int i = 0; i < sRop.companies.size(); i++) {
                        CompanyRO tmpCompany = sRop.companies.get(i);
                        if (tmpCompany.getId() == mRop.getSupervisorIdCompany()) {
                            txtEmpresaSupervisor.setText(tmpCompany.getDisplayName());
                            break;
                        }
                    }

                    //recuperar requiere investigacion
                    if (mRop.isResearch_required()) {
                        txtRequiereInvestigacion.setText("Si");
                    } else {
                        txtRequiereInvestigacion.setText("No");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    realm.close();
                }
            }
        }
    }


}
