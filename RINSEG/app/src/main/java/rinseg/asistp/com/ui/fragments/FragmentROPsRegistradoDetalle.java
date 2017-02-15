package rinseg.asistp.com.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import rinseg.asistp.com.adapters.AccionPrevEstadoAdapter;
import rinseg.asistp.com.models.AccionPreventiva;
import rinseg.asistp.com.rinseg.R;

/**
 * Created by OSequeiros on 14/02/2017.
 * Fragment que muestra el listado de acciones preventivas
 */

public class FragmentROPsRegistradoDetalle extends Fragment {

    private static final String ARG_PARAM = "id_rop";

    private int idRop;

    private RecyclerView recyclerAcciones;
    private TextView textTipoEvento, textArea, textDescripcion;
    private List<AccionPreventiva> listAcciones;
    private AccionPrevEstadoAdapter adapter;

    public FragmentROPsRegistradoDetalle newInstance(int id) {
        FragmentROPsRegistradoDetalle fragment = new FragmentROPsRegistradoDetalle();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ( getArguments() != null ) {
            idRop = getArguments().getInt(ARG_PARAM);
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
        recyclerAcciones = (RecyclerView) v.findViewById(R.id.recycler_preventive_actions_list);
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
    }

    /** Proceso para cargar datos */
    private void setUpData() {

    }
}
