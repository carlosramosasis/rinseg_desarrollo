package rinseg.asistp.com.ui.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import rinseg.asistp.com.models.User;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.ui.fragments.FragmentROPsRegistradoDetalle;
import rinseg.asistp.com.utils.Constants;
import rinseg.asistp.com.utils.Generic;
import rinseg.asistp.com.utils.RinsegModule;
import rinseg.asistp.com.utils.SharedPreferencesHelper;

public class ActivityRopRegistradoDetalle extends AppCompatActivity {

    ///todo :::::::::::::::::::::::::::::::::::::::::::::::VARIABLES ::::::::::::::::::::::::::::::::::::::::::::::::::::::
    public Toolbar toolbar;
    ActivityRopRegistradoDetalle thiss = this;

    int idRop;
    String idTmpRop;

    RealmConfiguration myConfig;

    private User usuarioLogueado;



    ///todo :::::::::::::::::::::::::::::::::::::::::::::::EVENTOS ::::::::::::::::::::::::::::::::::::::::::::::::::::::
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rop_registrado_detalle);

        setUpElements();
        setUpActions();

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                idRop = extras.getInt("ROPId", 0);
                idTmpRop = extras.getString("ROPIdTmp", "");
            }
        }

        FragmentROPsRegistradoDetalle fragment = FragmentROPsRegistradoDetalle.newInstance(idRop,idTmpRop);
        replaceFragment(fragment, true, 0, 0, 0, 0);
    }

    @Override
    public void onBackPressed() {

    }


    ///todo :::::::::::::::::::::::::::::::::::::::::::::::METODOS ::::::::::::::::::::::::::::::::::::::::::::::::::::::

    void setUpElements() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_rop_registrado_detalle);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left);

        myConfig = new RealmConfiguration.Builder()
                .name("rinseg.realm")
                .schemaVersion(2)
                .modules(new RinsegModule())
                .deleteRealmIfMigrationNeeded()
                .build();
    }

    void setUpActions() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
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
        transaction.replace(R.id.frame_main_content_rop_registrado_detalle, fragment, tag);
        if (addToBackStack) transaction.addToBackStack(null);
        transaction.commit();
    }

}
