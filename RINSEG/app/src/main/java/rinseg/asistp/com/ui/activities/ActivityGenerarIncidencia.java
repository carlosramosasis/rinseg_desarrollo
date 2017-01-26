package rinseg.asistp.com.ui.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import rinseg.asistp.com.models.InspeccionRO;
import rinseg.asistp.com.models.ManagementRO;
import rinseg.asistp.com.models.SettingsInspectionRO;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.ui.fragments.FragmentIncidenciaNuevo1;
import rinseg.asistp.com.ui.fragments.FragmentInspeccionDetalle1;
import rinseg.asistp.com.utils.Messages;
import rinseg.asistp.com.utils.RinsegModule;

public class ActivityGenerarIncidencia extends AppCompatActivity {

    private final static String ARG_INCIDENT = "idIncident";

    public Toolbar toolbarGenerarIncidencia;
    private ActivityGenerarIncidencia thiss = (ActivityGenerarIncidencia) this;
    public Button btnLeft;
    public Button btnRight;

    public LinearLayout linearButtonsBottom;
    TextView txtNumPagina ;

    String idIncidencia;

    RealmConfiguration myConfig;

    public static SettingsInspectionRO sIns;

    public static InspeccionRO mInspeccion;

    public short totalPaginas = 0;
    public short actualPagina = 0;

    Bundle bundle;

    //import foto
    public int PICK_IMAGE_REQUEST = 1;
    //tomar foto
    public int REQUEST_IMAGE_CAPTURE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generar_incidencia);

        setUpElements();
        setUpActions();

        LoadSettingInspeccion();

        LoadInspeccion();

<<<<<<< Updated upstream
        FragmentIncidenciaNuevo1 fragment = FragmentIncidenciaNuevo1.newInstance(idIncidencia);
        replaceFragment(fragment, true, 0, 0, 0, 0);
=======
        replaceFragment(new FragmentIncidenciaNuevo1(), true, 0, 0, 0, 0);
>>>>>>> Stashed changes
    }

    @Override
    public void onBackPressed() { }

    @Override
    protected void onResume() {
        super.onResume();
        linearButtonsBottom.setVisibility(View.VISIBLE);
    }

    //Proceso para cargar las vistas
    private void setUpElements() {
        toolbarGenerarIncidencia = (Toolbar) findViewById(R.id.toolbar_generar_incidencia);
        toolbarGenerarIncidencia.setNavigationIcon(R.drawable.ic_arrow_left);

        bundle = getIntent().getExtras();

        linearButtonsBottom = (LinearLayout) this.findViewById(R.id.linear_buttons_bottom);
        txtNumPagina  = (TextView) findViewById(R.id.txt_numero_pagina_incidente);

        btnLeft = (Button) this.findViewById(R.id.btn_left);
        btnRight = (Button) this.findViewById(R.id.btn_right);

        //configuramos Realm
        Realm.init(this.getApplicationContext());
        myConfig = new RealmConfiguration.Builder()
                .name("rinseg.realm")
                .schemaVersion(2)
                .modules(new RinsegModule())
                .deleteRealmIfMigrationNeeded()
                .build();
    }

    //cargamos los eventos
    private void setUpActions() {
        toolbarGenerarIncidencia.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thiss.finish();
            }
        });
    }

    public void replaceFragment(Fragment fragment, boolean addToBackStack,
                                int animIdIn1,
                                int animIdOut1,
                                int animIdIn2,
                                int animIdOut2) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        String tag = fragment.getClass().getSimpleName();
        tag = "Tag_" + tag;
        transaction.setCustomAnimations(animIdIn1, animIdOut1, animIdIn2, animIdOut2);
        transaction.replace(R.id.frame_main_content_generar_inspeccion, fragment, tag);
        if (addToBackStack) transaction.addToBackStack(null);
        transaction.commit();
    }

    private void LoadSettingInspeccion() {
        final Realm realm = Realm.getInstance(myConfig);
        try {
            sIns = realm.where(SettingsInspectionRO.class).findFirst();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            realm.close();
        }
    }

    private void LoadInspeccion() {
        if ( bundle != null ) {
            String tmpIdInsp = bundle.getString("InspTmpId", null);
            int id = bundle.getInt("InspId", 0);
            idIncidencia = bundle.getString(ARG_INCIDENT, "");

            final Realm realm = Realm.getInstance(myConfig);
            try {
                if ( id != 0 ) {
                    mInspeccion = realm.where(InspeccionRO.class).equalTo("id", id ).findFirst();
                } else if ( tmpIdInsp != null ) {
                    mInspeccion = realm.where(InspeccionRO.class).equalTo("tmpId", tmpIdInsp).findFirst();
                }
                if ( mInspeccion == null ) {
                    return;
                }
            } catch ( Exception e ) {
                e.printStackTrace();
                realm.close();
            } finally {
                realm.close();
            }
        }
    }

    public void ShowNumPagina() {
        txtNumPagina.setText("" + actualPagina + "/" + totalPaginas);
        txtNumPagina.setVisibility(View.VISIBLE);
    }
}
