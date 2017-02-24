package rinseg.asistp.com.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import rinseg.asistp.com.adapters.AccionPrevEstadoAdapter;
import rinseg.asistp.com.models.AccionPreventiva;
import rinseg.asistp.com.models.AreaRO;
import rinseg.asistp.com.models.EventRO;

import rinseg.asistp.com.models.ROP;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.utils.RinsegModule;

/**
 * Created by OSequeiros on 14/02/2017.
 * Fragment que muestra el listado de acciones preventivas
 */

public class FragmentROPsRegistradoDetalle extends Fragment {

    private static final String ARG_ID_ROP = "id_rop";
    private static final String ARG_ID_TMP_ROP = "id_tmp_rop";

    private int idRop = 0;
    String idTmpRop = "";

    private ROP rop;

    private TextView textTipoEvento, textArea, textDescripcion;
    private List<AccionPreventiva> listAcciones;
    private AccionPrevEstadoAdapter adapter;

    Toolbar toolbar;

    RealmConfiguration myConfig;

    public static FragmentROPsRegistradoDetalle newInstance(int id,String idTmp) {
        FragmentROPsRegistradoDetalle fragment = new FragmentROPsRegistradoDetalle();
        Bundle args = new Bundle();
        args.putInt(ARG_ID_ROP, id);
        args.putString(ARG_ID_TMP_ROP, idTmp);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idRop = getArguments().getInt(ARG_ID_ROP,0);
            idTmpRop =  getArguments().getString(ARG_ID_TMP_ROP,"");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rops_registrado_acciones, container, false);

        setUpElements(view);
        setUpData();

        return view;
    }

    /** Proceso para cargar vistas */
    private void setUpElements(View v) {
        RecyclerView recyclerAcciones = (RecyclerView) v.findViewById(R.id.recycler_preventive_actions_list);
        textTipoEvento = (TextView) v.findViewById(R.id.text_acciones_prev_tipo_evento);
        textArea = (TextView) v.findViewById(R.id.text_acciones_prev_area_resp);
        textDescripcion = (TextView) v.findViewById(R.id.text_acciones_prev_area_descr);

        recyclerAcciones.setHasFixedSize(true);
        adapter = new AccionPrevEstadoAdapter(listAcciones, getContext());

        LinearLayoutManager lManager = new LinearLayoutManager(this.getActivity()
                .getApplicationContext());
        recyclerAcciones.setLayoutManager(lManager);
        recyclerAcciones.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerAcciones.setItemAnimator(new DefaultItemAnimator());

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        //configuramos Realm
        Realm.init(this.getActivity().getApplicationContext());
        myConfig = new RealmConfiguration.Builder()
                .name("rinseg.realm")
                .schemaVersion(2)
                .modules(new RinsegModule())
                .deleteRealmIfMigrationNeeded()
                .build();
    }

    /** Proceso para cargar datos */
    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private void setUpData() {
        Realm realm = Realm.getInstance(myConfig);
        try {
            if (idRop != 0) {
                rop = realm.where(ROP.class).equalTo("id", idRop).findFirst();
            } else if (!idTmpRop.equals("")) {
                rop = realm.where(ROP.class).equalTo("tmpId", idTmpRop).findFirst();
            }
            if (rop != null) {
                // Recuperamos el tipo de evento :
                String eventType = realm.where(EventRO.class).equalTo("id", rop.getEventId())
                        .findFirst().getDisplayName();
                textTipoEvento.setText(eventType);
                // Recuperamos el área :
                String area = realm.where(AreaRO.class).equalTo("id", rop.getAreaId())
                        .findFirst().getDisplayName();
                textArea.setText(area);
                textDescripcion.setText(rop.getEventDescription());

                listAcciones.addAll(rop.listaAccionPreventiva);
                adapter.notifyDataSetChanged();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            realm.close();
        }
    }
}