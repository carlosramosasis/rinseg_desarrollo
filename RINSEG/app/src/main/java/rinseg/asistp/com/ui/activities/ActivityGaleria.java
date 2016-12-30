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
import rinseg.asistp.com.models.ROP;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.utils.RinsegModule;

public class ActivityGaleria extends AppCompatActivity implements ListenerClickImage {

    ///// TODO: :::::::::::::::::::::::::::::::::::::::::::::::::: VARIABLES ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    ActivityGaleria thiss = this;

    Toolbar toolbar;

    String tmpIdRop = null;

    private RecyclerView recyclerImage;
    private ImageAdapter imageAdapter;
    private RecyclerView.LayoutManager lManager;
    private List<ImagenRO> listaImagenes = new ArrayList<>();

    ROP mRop;

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
            }
        }


        setUpElements();
        setUpActions();

        LoadImagenesDeRop();

    }

    @Override
    public void  onResume(){
        super.onResume();
        toolbar.setTitle("Galería de imágenes");
    }

    @Override
    public void onItemClicked(ImageAdapter.ImageViewHolder holder, int position, FotoModel fotoModel) {
        if(fotoModel.uri != null){
            Intent FotoComentarioIntent = new Intent().setClass(this, ActivityFotoComentario.class);
            FotoComentarioIntent.putExtra("imagen_rop", fotoModel);
            FotoComentarioIntent.putExtra("ROPtmpId", mRop.getTmpId());
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
        // usar administrador para linearLayout
        //lManager = new LinearLayoutManager(this.getApplicationContext());
        RecyclerView.LayoutManager lm = new GridLayoutManager(this,2);
        recyclerImage.setLayoutManager(lm);
        // Crear un nuevo Adaptador
        fileApp = getApplicationContext().getFilesDir();

        LoadRopPendiente();
        imageAdapter = new ImageAdapter(listaImagenes,fileApp, mRop.getTmpId(),this);
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


}
