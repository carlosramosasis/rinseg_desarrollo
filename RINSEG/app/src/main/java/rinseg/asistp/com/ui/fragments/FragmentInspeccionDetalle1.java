package rinseg.asistp.com.ui.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.GregorianCalendar;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import rinseg.asistp.com.models.AreaRO;
import rinseg.asistp.com.models.CompanyRO;
import rinseg.asistp.com.models.EventRO;
import rinseg.asistp.com.models.Inspeccion;
import rinseg.asistp.com.models.InspeccionRO;
import rinseg.asistp.com.models.ROP;
import rinseg.asistp.com.models.RiskRO;
import rinseg.asistp.com.models.TargetRO;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.ui.activities.ActivityInspeccionDetalle;
import rinseg.asistp.com.ui.activities.ActivityMain;
import rinseg.asistp.com.utils.Constants;
import rinseg.asistp.com.utils.Generic;
import rinseg.asistp.com.utils.RinsegModule;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentInspeccionDetalle1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class FragmentInspeccionDetalle1 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private InspeccionRO inspeccion;

    private OnFragmentInteractionListener mListener;


    ActivityInspeccionDetalle activityMain;
    private TextView txtFecha;
    FloatingActionButton btnVerIncidentes;

    Bundle bundle;

    RealmConfiguration myConfig;

    public FragmentInspeccionDetalle1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentROPPendiente1.
     */
    // TODO: Rename and change types and number of parameters


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            /*mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);*/
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inspeccion_detalle1, container, false);


        setUpElements(view);
        setUpActions();

        LoadInspeccionPendiente();
        return view;
    }



    @Override
    public void onResume() {
        super.onResume();
        activityMain.toolbarInspeccionDet.setTitle(R.string.title_ssma);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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

    //Proceso para cargar las vistas
    private void setUpElements(View v) {
        bundle = getArguments();

        activityMain = ((ActivityInspeccionDetalle) getActivity());

        //instanciamos las elementos de la vista
        txtFecha = (TextView) v.findViewById(R.id.insp_det_txt_fecha);
        //seteamos los valores a mostrar
        //txtFecha.setText(Generic.dateFormatter.format(inspeccion.getFechaCreacion()));

        btnVerIncidentes = (FloatingActionButton) v.findViewById(R.id.btn_ver_incidente);



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
        btnVerIncidentes.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                activityMain.replaceFragment(new FragmentInspeccionDetalle2(),true,0,0,0,0);
            }
        });

        activityMain.toolbarInspeccionDet.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityMain.finish();
            }
        });
    }

    private void LoadInspeccionPendiente() {

        String tmpIdInsp= null;
        if (bundle != null) {
            tmpIdInsp = bundle.getString("InspeccionId", null);
            //ROP tmpRop = new ROP();
            if (tmpIdInsp != null) {
                final Realm realm = Realm.getInstance(myConfig);
                try {
                    inspeccion = realm.where(InspeccionRO.class).equalTo("id", tmpIdInsp).findFirst();
                    if (inspeccion == null) {
                        return;
                    }

                    // recuperar potencial de perdida
                   /* for (int i = 0; i < sRop.risks.size(); i++) {
                        RiskRO tmpRisk = sRop.risks.get(i);
                        if (tmpRisk.getId() == mRop.getRiskId()) {
                            spinnerPotencialPerdida.setSelection(i);
                            break;
                        }
                    }*/



                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    realm.close();
                }
            }

        }
    }

}
