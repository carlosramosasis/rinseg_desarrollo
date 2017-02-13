package rinseg.asistp.com.ui.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.ui.activities.ActivityInspeccionDetalle;

/**
 * Created by OSequeiros on 10/02/2017.
 * Clase que contiene los tabs de incidentes pendientes y cerrados
 */

public class FragmentTabsIncidents extends Fragment {

    private static String ARG_PARAM_ID = "id_inspection";

    private TabLayout tabIncidents;
    private ViewPager viewPagerIncidents;
    private int idInspeccion;
    private Toolbar toolbar;

    /** Constructor de fragment */
    public static FragmentTabsIncidents newInstance(int idIspeccion) {
        FragmentTabsIncidents fragment = new FragmentTabsIncidents();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_ID, idIspeccion);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idInspeccion = getArguments().getInt(ARG_PARAM_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_tab_incidents, container, false);

        setUpElements(view);
        setUpData();
        setUpActions();

        return view;
    }

    /** Proceso para cargar las vistas : */
    private void setUpElements(View v) {
        tabIncidents = (TabLayout) v.findViewById(R.id.tab_incidents);
        viewPagerIncidents = (ViewPager) v.findViewById(R.id.view_pager_incidents);
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbarInspeccionDetalle);
    }

    /** Proceso para asignar la data inicial */
    private void setUpData() {
        toolbar.setTitle(getString(R.string.title_incidentes));

        // Agregamos dos pestañas :
        tabIncidents.addTab(tabIncidents.newTab()
                .setText(getString(R.string.tab_incidents_title_pending)));
        tabIncidents.addTab(tabIncidents.newTab()
                .setText(getString(R.string.tab_incidents_title_closed)));
        tabIncidents.setTabGravity(TabLayout.GRAVITY_FILL);

        // Asignamos el adapter a nuestro ViewPager :
        PageAdapterIncidents adapterIncidents = new PageAdapterIncidents(
                getActivity().getSupportFragmentManager(), tabIncidents.getTabCount(), idInspeccion);
        viewPagerIncidents.setAdapter(adapterIncidents);
        tabIncidents.setupWithViewPager(viewPagerIncidents);

        viewPagerIncidents.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(
                tabIncidents));
    }

    /** Proceso para escuchar acciones */
    private void setUpActions() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment f = FragmentInspeccionDetalle1.newInstance(idInspeccion, "");
                ((ActivityInspeccionDetalle)getActivity()).replaceFragment(f, true, 0, 0, 0, 0);
            }
        });
    }
}