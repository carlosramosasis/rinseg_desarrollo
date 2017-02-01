package rinseg.asistp.com.ui.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import rinseg.asistp.com.models.FotoModel;
import rinseg.asistp.com.models.ImagenRO;
import rinseg.asistp.com.models.IncidenciaRO;
import rinseg.asistp.com.models.ROP;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.utils.Constants;
import rinseg.asistp.com.utils.Generic;
import rinseg.asistp.com.utils.Messages;
import rinseg.asistp.com.utils.RinsegModule;


public class ActivityFotoComentario extends AppCompatActivity {
    public Toolbar toolbarFotoComentario;
    public ImageView imageFoto;
    public EditText txtComentario;
    private ActivityFotoComentario thiss = (ActivityFotoComentario) this;
    private FotoModel fotoModel;
    Bitmap bitmap;

    ImageView imageView;
    ImageButton btnGuardar;

    ROP mRop;
    IncidenciaRO mIncidencia;
    ImagenRO imagenRO;
    String tmpIdRop = null;
    String pTitulo = null;
    String tmpIdInci = null;
    Boolean puedeEditar;
    String comentario = null;
    RealmConfiguration myConfig;

    View rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto_comentario);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                fotoModel = (FotoModel) extras.getSerializable("imagen");
                tmpIdRop = extras.getString("ROPtmpId", null);
                tmpIdInci = extras.getString("IncidenciatmpId", null);
                puedeEditar = extras.getBoolean("puedeEditar", true);
                comentario = extras.getString("comentario", null);
                pTitulo = extras.getString("titulo",getString(R.string.title_add_imagen));
            }
        }

        setUpElements();

        if (tmpIdRop != null) {
            LoadRopPendiente();
        }
        if (tmpIdInci != null) {
            LoadIncidente();
        }

        MostrarImagen();

        setUpActions();

    }


    @Override
    protected void onResume() {
        super.onResume();
        toolbarFotoComentario.setTitle(pTitulo);
        txtComentario.clearFocus();
    }

    //Proceso para cargar las vistas
    private void setUpElements() {
        rootLayout = findViewById(R.id.coordinator_foto_comentario);
        toolbarFotoComentario = (Toolbar) findViewById(R.id.toolbarFotoComentario);
        toolbarFotoComentario.setNavigationIcon(R.drawable.ic_arrow_left);
        txtComentario = (EditText) findViewById(R.id.txt_comentario);
        imageView = (ImageView) findViewById(R.id.image_foto_comentario);
        btnGuardar = (ImageButton) findViewById(R.id.btn_guardar_foto_comentario);

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
        toolbarFotoComentario.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thiss.finish();
            }
        });
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtComentario.getText().toString().trim().length() == 0) {
                    Messages.showToast(rootLayout, getString(R.string.error_comentario));
                    return;
                }

                SaveImagenComentario();
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
        transaction.replace(R.id.frame_main_content_inspeccion_detalle, fragment, tag);
        if (addToBackStack) transaction.addToBackStack(null);
        transaction.commit();
    }


    private void MostrarImagen() {

        /* if (fotoModel.bitmap != null) {
            try {
                bitmap = fotoModel.bitmap;
                imageView.setImageBitmap(bitmap);

                if (comentario != null) {
                    txtComentario.setText(comentario);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }  else*/

        if (fotoModel.uri != null ) {
            Uri uri = fotoModel.uri;
            try {
                if(puedeEditar){
                    while (bitmap == null) {
                        // if (puedeEditar) {
                        //bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        InputStream imageStream = null;
                        imageStream = getContentResolver().openInputStream(uri);
                        bitmap = BitmapFactory.decodeStream(imageStream);
                        //  } else {
//                        bitmap = BitmapFactory.decodeFile(uri.getPath());
                        //                  }
                    }
                }


               /* Picasso.with(this).load("file://" + uri.getPath())
                        .error(R.drawable.ic_imagen_no_disponible)
                        .placeholder(R.drawable.ic_image)
                        .into(imageView);*/
                imageView.setImageURI(uri);

                if (comentario != null) {
                    txtComentario.setText(comentario);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!puedeEditar) {
            btnGuardar.setVisibility(View.GONE);
            txtComentario.setFocusable(false);
        }

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

        final Realm realm = Realm.getInstance(myConfig);
        try {
            mIncidencia = realm.where(IncidenciaRO.class).equalTo("tmpId", tmpIdInci).findFirst();
            if (mIncidencia == null) {
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            realm.close();
        }
    }


    private void SaveImagenComentario() {
        String nombreImagen = Generic.randomString(Constants.CHARSET_AZ_09, Constants.LEN_IMAGEN);
        Boolean result = false;
        if (mRop != null) {
            result = Generic.GuardarImagenCarpeta(this.getApplicationContext(), Constants.PATH_IMAGE_GALERY_ROP, mRop.getTmpId(), bitmap, nombreImagen);
        } else if (mIncidencia != null) {
            result = Generic.GuardarImagenCarpeta(this.getApplicationContext(), Constants.PATH_IMAGE_GALERY_INCIDENCIA, mIncidencia.getTmpId(), bitmap, nombreImagen);
        }


        if (result) {
            Realm realm = Realm.getInstance(myConfig);
            try {
                realm.beginTransaction();

                imagenRO = realm.createObject(ImagenRO.class);
                imagenRO.setName(nombreImagen + ".jpg");
                imagenRO.setDescripcion(txtComentario.getText().toString().trim());
                if (mRop != null) {
                    mRop.listaImgComent.add(imagenRO);
                } else if (mIncidencia != null) {
                    mIncidencia.listaImgComent.add(imagenRO);
                }

                realm.commitTransaction();
                Messages.showToast(rootLayout, getString(R.string.guardo_ok));
                this.finish();
            } catch (Exception e) {
                e.printStackTrace();
                Messages.showToast(rootLayout, getString(R.string.guardo_error));
            } finally {
                realm.close();
            }
        } else {
            Messages.showToast(rootLayout, getString(R.string.guardo_error));
        }
    }


}
