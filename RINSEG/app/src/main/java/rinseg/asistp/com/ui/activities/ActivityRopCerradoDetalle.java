package rinseg.asistp.com.ui.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import io.realm.RealmList;
import rinseg.asistp.com.models.ImagenRO;
import rinseg.asistp.com.models.Inspeccion;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.ui.fragments.FragmentIncidenciaNuevo1;
import rinseg.asistp.com.ui.fragments.FragmentROPCerrado1;

public class ActivityRopCerradoDetalle extends AppCompatActivity {

    ///todo :::::::::::::::::::::::::::::::::::::::::::::::VARIABLES ::::::::::::::::::::::::::::::::::::::::::::::::::::::
    public Toolbar toolbar;
    ActivityRopCerradoDetalle thiss = this;

    int idRop;


    ///todo :::::::::::::::::::::::::::::::::::::::::::::::EVENTOS ::::::::::::::::::::::::::::::::::::::::::::::::::::::
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rop_cerrado_detalle);

        setUpElements();
        setUpActions();

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                idRop = extras.getInt("ROPId", 0);
            }
        }


        Fragment fRop1 = new FragmentROPCerrado1();
        Bundle args = new Bundle();
        args.putInt("ROPId", idRop);
        fRop1.setArguments(args);
        replaceFragment(fRop1, true, 0, 0, 0, 0);
    }

    @Override
    public void onBackPressed() {

    }


    ///todo :::::::::::::::::::::::::::::::::::::::::::::::METODOS ::::::::::::::::::::::::::::::::::::::::::::::::::::::

    void setUpElements() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_rop_detalle);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left);


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
        transaction.replace(R.id.frame_main_content_rop_detalle, fragment, tag);
        if (addToBackStack) transaction.addToBackStack(null);
        transaction.commit();
    }



}
