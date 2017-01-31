package rinseg.asistp.com.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import rinseg.asistp.com.adapters.ImageAdapter;
import rinseg.asistp.com.adapters.RopAdapter;
import rinseg.asistp.com.listener.ListenerClickImage;
import rinseg.asistp.com.models.FotoModel;
import rinseg.asistp.com.models.ImagenRO;
import rinseg.asistp.com.models.IncidenciaRO;
import rinseg.asistp.com.models.ROP;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.utils.Constants;
import rinseg.asistp.com.utils.RinsegModule;

public class ActivityGaleria extends AppCompatActivity implements ListenerClickImage {

    ///// TODO: :::::::::::::::::::::::::::::::::::::::::::::::::: VARIABLES ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    ActivityGaleria thiss = this;

    Toolbar toolbar;

    String tmpIdRop = null;
    String tmpIdIncidente = null;

    private RecyclerView recyclerImage;
    private ImageAdapter imageAdapter;
    private RecyclerView.LayoutManager lManager;
    private List<ImagenRO> listaImagenes = new ArrayList<>();

    ROP mRop;
    IncidenciaRO mIncidente;

    private File fileApp;

    RealmConfiguration myConfig;

    ///// TODO: :::::::::::::::::::::::::::::::::::::::::::::::::: EVENTOS ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeria);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                tmpIdRop = extras.getString("ROPtmpId", null);
                tmpIdIncidente = extras.getString("IncidentetmpId", null);
            }
        }


        setUpElements();
        setUpActions();

        if (mRop != null) {
            LoadImagenesDeRop();
        } else if (mIncidente != null) {
            LoadImagenesDeIncidente();
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.setTitle("Galería de imágenes");
    }

    @Override
    public void onItemClicked(ImageAdapter.ImageViewHolder holder, int position, FotoModel fotoModel) {
        if (fotoModel.uri != null) {
            Intent FotoComentarioIntent = new Intent().setClass(this, ActivityFotoComentario.class);
            FotoComentarioIntent.putExtra("imagen", fotoModel);
            if (mRop != null) {
                FotoComentarioIntent.putExtra("ROPtmpId", mRop.getTmpId());
            } else if (mIncidente != null) {
                FotoComentarioIntent.putExtra("IncidentetmpId", mIncidente.getTmpId());
            }

            FotoComentarioIntent.putExtra("puedeEditar", false);
            FotoComentarioIntent.putExtra("comentario", listaImagenes.get(position).getDescripcion());
            startActivity(FotoComentarioIntent);
        }
    }


    ///// TODO: :::::::::::::::::::::::::::::::::::::::::::::::::: METODOS ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    private void setUpElements() {

        toolbar = (Toolbar) findViewById(R.id.toolbarGaleria);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left);

        //configuramos Realm
        Realm.init(this.getApplicationContext());
        myConfig = new RealmConfiguration.Builder()
                .name("rinseg.realm")
                .schemaVersion(2)
                .modules(new RinsegModule())
                .deleteRealmIfMigrationNeeded()
                .build();

        //configuracion para el recicler
        recyclerImage = (RecyclerView) findViewById(R.id.recycler_galeria);
        recyclerImage.setHasFixedSize(true);
        recyclerImage.setItemViewCacheSize(10);
        recyclerImage.setDrawingCacheEnabled(true);
        recyclerImage.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        // usar administrador para linearLayout
        //lManager = new LinearLayoutManager(this.getApplicationContext());
        RecyclerView.LayoutManager lm = new GridLayoutManager(this, 2);
        recyclerImage.setLayoutManager(lm);
        // Crear un nuevo Adaptador
        fileApp = getApplicationContext().getFilesDir();

        if(tmpIdRop!= null){
            LoadRopPendiente();
            imageAdapter = new ImageAdapter(this,listaImagenes, fileApp, Constants.PATH_IMAGE_GALERY_ROP, mRop.getTmpId(), this);
        }else if(tmpIdIncidente != null){
            LoadIncidente();
            imageAdapter = new ImageAdapter(this,listaImagenes, fileApp, Constants.PATH_IMAGE_GALERY_INCIDENCIA,mIncidente.getTmpId(), this);
        }

        recyclerImage.setAdapter(imageAdapter);
    }

    private void setUpActions() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thiss.finish();
            }
        });
    }

    private void LoadRopPendiente() {

        //ROP tmpRop = new ROP();
        if (tmpIdRop != null) {
            final Realm realm = Realm.getInstance(myConfig);
            try {
                mRop = realm.where(ROP.class).equalTo("tmpId", tmpIdRop).findFirst();
                if (mRop == null) {
                    return;
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                realm.close();
            }
        }
    }

    private void LoadIncidente() {
        if (tmpIdIncidente != null) {
            final Realm realm = Realm.getInstance(myConfig);
            try {
                mIncidente = realm.where(IncidenciaRO.class).equalTo("tmpId", tmpIdIncidente).findFirst();
                if (mIncidente == null) {
                    return;
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                realm.close();
            }
        }
    }

    private void LoadImagenesDeRop() {
        Realm realm = Realm.getInstance(myConfig);
        try {
            ROP RopRealm = realm.where(ROP.class).equalTo("tmpId", tmpIdRop).findFirst();

            if (RopRealm == null) {
                return;
            }

            if (RopRealm.listaImgComent.size() > 0) {

                for (int i = 0; i < RopRealm.listaImgComent.size(); i++) {
                    ImagenRO img = RopRealm.listaImgComent.get(i);
                    listaImagenes.add(img);
                    imageAdapter.notifyDataSetChanged();
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            realm.close();
        }
    }

    private void LoadImagenesDeIncidente() {
        Realm realm = Realm.getInstance(myConfig);
        try {
            IncidenciaRO incidenteRealm = realm.where(IncidenciaRO.class).equalTo("tmpId", tmpIdIncidente).findFirst();

            if (incidenteRealm == null) {
                return;
            }

            if (incidenteRealm.listaImgComent.size() > 0) {

                for (int i = 0; i < incidenteRealm.listaImgComent.size(); i++) {
                    ImagenRO img = incidenteRealm.listaImgComent.get(i);
                    listaImagenes.add(img);
                    imageAdapter.notifyDataSetChanged();
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            realm.close();
        }
    }


}
