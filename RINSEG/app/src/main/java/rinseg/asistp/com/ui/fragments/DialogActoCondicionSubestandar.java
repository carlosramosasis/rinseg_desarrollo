package rinseg.asistp.com.ui.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyboardShortcutGroup;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import rinseg.asistp.com.adapters.AccionCondicionSubestandarAdapter;
import rinseg.asistp.com.adapters.RopAdapter;
import rinseg.asistp.com.listener.ListenerClickActoCondicion;
import rinseg.asistp.com.models.EventItemsRO;
import rinseg.asistp.com.models.EventRO;
import rinseg.asistp.com.models.ROP;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.utils.RinsegModule;

/**
 * Created by Carlos Ramos  on 25/11/2016.
 * Dialog de multi-selección de acto o condición
 */

public class DialogActoCondicionSubestandar extends Dialog
        implements View.OnClickListener, ListenerClickActoCondicion {

    ///// TODO: :::::::::::::::::::::::::::::::: VARIABLES :::::::::::::::::::::::::::::::::::::::::
    public Activity activity;
    public Dialog d;
    public Button btnAceptar, btnCancelar;
    private TextView mTextTitle;
    private AccionCondicionSubestandarAdapter itemsAdapter;

    private String nameActoCondicion;
    private List<EventItemsRO> listaItems = new ArrayList<>();
    private Object[] currentItems;

    private String idROP;
    RealmConfiguration myConfig;
    private ROP currentROP;

    private static String keyIdRop = "tmpId";

    ///// TODO: ::::::::::::::::::::::::::::::: CONSTRUCTOR ::::::::::::::::::::::::::::::::::::::::
    DialogActoCondicionSubestandar(Activity a, String pNameActoCondicion, String idROP) {
        super(a, R.style.CustomDialogTheme);
        this.activity = a;
        this.nameActoCondicion = pNameActoCondicion;
        this.idROP = idROP;
    }

    ///// TODO: ::::::::::::::::::::::::::::::::: EVENTOS ::::::::::::::::::::::::::::::::::::::::::
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_accion_condicion_subestandar);

        btnAceptar = (Button) findViewById(R.id.btn_dialog_actocondi_aceptar);
        btnCancelar = (Button) findViewById(R.id.btn_dialog_cancelar);
        mTextTitle = (TextView) findViewById(R.id.txt_title_dialog);
        RecyclerView recyclerViewItems = (RecyclerView) findViewById(
                R.id.recycler_view_acto_condicion_subestandar);

        btnCancelar.setOnClickListener(this);
        btnAceptar.setOnClickListener(this);

        // Propiedades del Recycler
        recyclerViewItems.setHasFixedSize(true);
        // usar administrador para linearLayout
        RecyclerView.LayoutManager lManager = new LinearLayoutManager(this.getContext());
        recyclerViewItems.setLayoutManager(lManager);

        // Configurando Realm
        Realm.init(getContext());
        myConfig = new RealmConfiguration.Builder()
                .name("rinseg.realm")
                .schemaVersion(2)
                .modules(new RinsegModule())
                .deleteRealmIfMigrationNeeded()
                .build();

        setUpData();

        // Creamos el adapter:
        itemsAdapter = new AccionCondicionSubestandarAdapter(listaItems, this, currentItems);
        recyclerViewItems.setAdapter(itemsAdapter);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_dialog_actocondi_aceptar:
                // Recorremos la lista de ítems checkeados:
                RealmList<EventItemsRO> itemsChecked = new RealmList<>();
                for ( int i = 0; i < listaItems.size(); i++ ){
                    if ( itemsAdapter.listCheckedActos.get(i) ) {
                        itemsChecked.add(listaItems.get(i));
                    }
                }
                // Guardamos en Realm
                Realm realm = Realm.getInstance(myConfig);
                realm.beginTransaction();
                currentROP.setListaEventItems(itemsChecked);
                realm.commitTransaction();
                dismiss();
                break;
            case R.id.btn_dialog_cancelar:
                dismiss();
                break;
            default:
                break;
        }
    }


    @Override
    public void onProvideKeyboardShortcuts(List<KeyboardShortcutGroup> data, Menu menu,
                                           int deviceId) { }


    ///// TODO: :::::::::::::::::::::::::::::::: METODOS :::::::::::::::::::::::::::::::::::::::::::

    public void setTitle(String pTitle) {
        mTextTitle.setText(pTitle);
    }

    private void setUpData() {
        Realm realm = Realm.getInstance(myConfig);

        try {
            // Recuperar los ítems de eventos:
            EventRO event = realm.where(EventRO.class).equalTo("name", nameActoCondicion).findFirst();

            for (int i = 0; i < event.eventItems.size(); i++) {
                EventItemsRO eventItem = event.eventItems.get(i);
                listaItems.add(eventItem);
            }

            // Recuperamos los ítems checkeados:
            currentROP = realm.where(ROP.class).equalTo(keyIdRop, idROP).findFirst();
            currentItems = currentROP.getListaEventItems().toArray();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            realm.close();
        }
    }

    @Override
    public void onItemClicked(AccionCondicionSubestandarAdapter.AccionViewHolder holder,
                              int position) { }
}
