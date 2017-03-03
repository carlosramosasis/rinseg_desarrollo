package rinseg.asistp.com.ui.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rinseg.asistp.com.models.AccionPreventiva;
import rinseg.asistp.com.models.ImagenRO;
import rinseg.asistp.com.models.ROP;
import rinseg.asistp.com.models.User;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.services.RestClient;
import rinseg.asistp.com.services.Services;
import rinseg.asistp.com.ui.activities.ActivityMain;
import rinseg.asistp.com.utils.Animations;
import rinseg.asistp.com.utils.Constants;
import rinseg.asistp.com.utils.DialogLoading;
import rinseg.asistp.com.utils.Generic;
import rinseg.asistp.com.utils.Messages;
import rinseg.asistp.com.utils.RinsegModule;
import rinseg.asistp.com.utils.SharedPreferencesHelper;

import static rinseg.asistp.com.utils.Constants.MY_SHARED_PREFERENCES;

/**
 * Created by Carlos Ramos on 28/10/2016.
 * Fragment que contiene el ViewPager de ROPs
 */
public class FragmentTabRops extends Fragment {

    ActivityMain activityMain;

    private FloatingActionButton fabTabRops;
    private Dialog recuperaRopDialog;
    private DialogLoading dialogLoading;
    PageAdapterRops adapter;

    private int positionTab;
    private RealmConfiguration myConfig;
    private int cantImagenesTotal = 0;
    int cantImagenesRecibidos = 0;

    private int[] iconIntArray = {R.drawable.ic_add,
            R.drawable.ic_cloud_download,
            R.drawable.ic_cloud_download};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tabs_rops, container, false);

        setUpElements(view);
        setUpActions();

        return view;
    }

    @Override
    public void onResume() {
        activityMain.toolbar.setTitle(R.string.title_rops);
        super.onResume();
    }

    /**
     * Proceso para cargar las vistas
     */
    private void setUpElements(View v) {

        // Configuración inicial de Realm :
        Realm.init(this.getActivity().getApplicationContext());
        myConfig = new RealmConfiguration.Builder()
                .name("rinseg.realm")
                .schemaVersion(2)
                .modules(new RinsegModule())
                .deleteRealmIfMigrationNeeded()
                .build();

        activityMain = ((ActivityMain) getActivity());
        fabTabRops = (FloatingActionButton) v.findViewById(R.id.fab_tab_rops);

        recuperaRopDialog = new Dialog(this.getContext(), R.style.CustomDialogTheme);
        dialogLoading = new DialogLoading(activityMain);

        final TabLayout tabLayout = (TabLayout) v.findViewById(R.id.tab_rops);
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_pendientes)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_registrados)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_cerrados)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) v.findViewById(R.id.tab_view_pager_rops);
        adapter = new PageAdapterRops(getActivity().getSupportFragmentManager(),
                tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                positionTab = tab.getPosition();
                Animations.animatedFabOnViewPager(fabTabRops, iconIntArray, getContext(), positionTab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    /**
     * Proceso para escuchar las acciones
     */
    private void setUpActions() {
        fabTabRops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (positionTab) {
                    case 0: // Crear nuevo ROP
                        launchNewRop();
                        break;
                    case 1: // Descargar ROP
                        showRetrieveDialog();
                        break;
                    case 2: // Descargar ROP
                        showRetrieveDialog();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * Proceso para reemplazar el fragment por el Pendiente1
     */
    private void launchNewRop() {
        Fragment fRopPendiente1 = new FragmentROPPendiente1();
        Bundle args = new Bundle();
        args.putBoolean("puedeEliminarse", true);
        fRopPendiente1.setArguments(args);
        activityMain.replaceFragment(fRopPendiente1, true, 0, 0, 0, 0);
    }

    /**
     * Proceso para mostrar Dialog de descarga de ROP
     */
    private void showRetrieveDialog() {
        recuperaRopDialog.show();
        recuperaRopDialog.setContentView(R.layout.dialog_recupera_rop);

        // Recuperamos las vistas del dialog :
        Button btnRecuperaRop = (Button) recuperaRopDialog.findViewById(
                R.id.btn_dialog_recupera_recuperar);
        Button btnCancelarRecuperaRop = (Button) recuperaRopDialog.findViewById(
                R.id.btn_dialog_recupera_cancelar);
        final EditText txtCodigoRecuperar = (EditText) recuperaRopDialog.findViewById(
                R.id.txt_dialog_codigo_recuperar);

        btnRecuperaRop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!txtCodigoRecuperar.getText().toString().trim().equals("")) {
                    try {
                        final int codRop = Integer.parseInt(txtCodigoRecuperar.getText()
                                .toString().trim());
                        recuperarRopCerrado(codRop);
                    } catch (Exception e) {
                        Messages.showSB(getView(),
                                getString(R.string.fragment_tab_rops_retrieve_rop_cod_invalid));
                    }
                } else {
                    Messages.showSB(getView(),
                            getString(R.string.fragment_tab_rops_retrieve_rop_cod_empty));
                }
            }
        });

        btnCancelarRecuperaRop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recuperaRopDialog.dismiss();
            }
        });
    }

    /**
     * Llamada Retrofit para traer el ROP
     */
    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    public void recuperarRopCerrado(int codeRop) {
        if (!Generic.IsOnRed(activityMain)) {
            Messages.showSB(getView(), getString(R.string.no_internet), getString(R.string.ok));
            return;
        }
        // Ocultamos el dialog de recuperación y mostramos el procesando :
        recuperaRopDialog.hide();
        dialogLoading.show();

        // Obtenemos el token :
        SharedPreferencesHelper preferencesHelper = new SharedPreferencesHelper(
                activityMain.getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE));
        String token = preferencesHelper.getToken();

        // Recuperamos el coordinator :
        final View vLayout = activityMain.findViewById(R.id.coordinator_activity_main);

        RestClient restClient = new RestClient(Services.URL_ROPS);
        Call<ResponseBody> call = restClient.iServices.getRopClosed(codeRop, token);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Realm realm = Realm.getInstance(myConfig);
                    try {
                        String body = response.body().string();

                        if (body.charAt(0) != '{') {
                            Messages.showSB(vLayout,
                                    getString(R.string.sincronizando_error), getString(R.string.ok));
                            return;
                        }
                        JSONObject jsonObject = new JSONObject(body);

                        JSONObject ropJSON = jsonObject.getJSONObject("rop");
                        JSONArray imagesJSON = ropJSON.getJSONArray("images");
                        JSONArray itemsJSON = ropJSON.getJSONArray("rop_items");
                        JSONObject usuario = ropJSON.getJSONObject("user");

                        // Recuperamos ROP desde Real, puede ser que ya exista :
                        ROP ropRecuperado = realm.where(ROP.class)
                                .equalTo("id", ropJSON.getInt("id")).findFirst();

                        if (ropRecuperado != null) { // El ROP ya existía
                            realm.beginTransaction();

                            PopulateUserForRop(ropRecuperado, usuario, realm);
                            // Guardando imágenes :
                            populateImagesForRopExisting(ropRecuperado, imagesJSON, realm);
                            realm.commitTransaction();

                            ROP ropCopy = realm.copyFromRealm(ropRecuperado);

                            if (ropCopy.getEstadoRop() == 1) {
                                // Notificamos al Pager de registrados :
                                notifyDowloandROP(ropCopy, false);
                                if (ropCopy.listaImgComent.size() == 0) {
                                    dialogLoading.dismiss();
                                    Messages.showToast(vLayout,
                                            getString(R.string.msg_rop_recuperado_ok));
                                }
                            } else {
                                if (ropCopy.getEstadoRop() == 2) {
                                    // Notificamos al Pager de cerrados :
                                    notifyDowloandROP(ropCopy, true);
                                    if (ropCopy.listaImgComent.size() == 0) {
                                        dialogLoading.dismiss();
                                        Messages.showToast(vLayout,
                                                getString(R.string.msg_rop_recuperado_ok));
                                    }
                                }
                            }
                            dialogLoading.dismiss();
                        } else { // El ROP no existía
                            realm.beginTransaction();
                            ropRecuperado = realm.createObject(ROP.class);
                            // Asignamos los valores al ROP :
                            ropRecuperado.setValues(ropJSON);
                            populateImagesForRopNew(ropRecuperado, imagesJSON, realm);
                            populateRopItemsForRop(ropRecuperado, itemsJSON, realm);
                            PopulateUserForRop(ropRecuperado, usuario, realm);
                            realm.commitTransaction();

                            ROP ropCopy = realm.copyFromRealm(ropRecuperado);

                            String message;
                            // Creamos la carpeta que contendrá las imágenes :
                            boolean createdImageGalery = Generic.CrearCarpetaImagenesPorRop(
                                    getActivity().getApplicationContext(), ropRecuperado.getTmpId());
                            if (createdImageGalery) {
                                String path = activityMain.getFilesDir().getPath() + "/" +
                                        Constants.PATH_IMAGE_GALERY_ROP + ropCopy.getTmpId() + "/";

                                GuardarImagenesEnLocal(ropCopy.listaImgComent, path);
                                message = "El ROP ha sido recuperado";
                            } else {
                                message = "No se ha podido descargar las imágenes";
                            }


                            if (ropCopy.getEstadoRop() == 1) {
                                // Notificamos al Pager de registrados :
                                notifyDowloandROP(ropCopy, false);
                                if (ropCopy.listaImgComent.size() == 0 || !createdImageGalery) {
                                    dialogLoading.dismiss();
                                    Messages.showToast(vLayout, message);
                                }
                            } else {
                                if (ropCopy.getEstadoRop() == 2) {
                                    // Notificamos al Pager de cerrados :
                                    notifyDowloandROP(ropCopy, true);
                                    if (ropCopy.listaImgComent.size() == 0 || !createdImageGalery) {
                                        dialogLoading.dismiss();
                                        Messages.showToast(vLayout, message);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        dialogLoading.dismiss();
                        e.printStackTrace();
                        Messages.showSB(vLayout, getString(R.string.msg_rop_recuperado_fail), "ok");
                    } finally {
                        realm.close();
                    }
                } else {
                    if (response.code() == 422) {
                        // Mostramos mensaje del servidor :
                        try {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            String message = jsonObject.getString("message");
                            Messages.showSB(vLayout, message, "ok");
                            dialogLoading.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                            dialogLoading.dismiss();
                            Messages.showSB(vLayout, getString(R.string.msg_rop_recuperado_fail), "ok");
                        }
                    } else {
                        dialogLoading.dismiss();
                        Messages.showSB(vLayout, getString(R.string.msg_rop_recuperado_fail), "ok");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogLoading.dismiss();
                Messages.showSB(vLayout, getString(R.string.msg_rop_recuperado_fail), "ok");
            }
        });
    }

    private void populateImagesForRopNew(ROP rop, JSONArray imagesArray, Realm realm) {
        for (int i = 0; i < imagesArray.length(); i++) {
            try {
                JSONObject imgJson = imagesArray.getJSONObject(i);
                ImagenRO imagen = realm.createObject(ImagenRO.class);
                imagen.setValues(imgJson);
                rop.listaImgComent.add(imagen);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void populateRopItemsForRop(ROP rop, JSONArray RopItems, Realm realm) {
        for (int i = 0; i < RopItems.length(); i++) {
            try {
                JSONObject item = RopItems.getJSONObject(i);
                AccionPreventiva accionPreventiva = realm.createObject(AccionPreventiva.class);
                accionPreventiva.setId(item.getInt("id"));
                accionPreventiva.setAccion(item.getString("action"));
                accionPreventiva.setResponsable(item.getString("responsible"));
                accionPreventiva.setFechaString(item.getString("deadline"));
                accionPreventiva.setFecha(Generic.dateFormatterMySql.parse(
                        accionPreventiva.getFechaString()));
                rop.listaAccionPreventiva.add(accionPreventiva);
            } catch (Exception e) {
                e.printStackTrace();
                dialogLoading.dismiss();
            }
        }
    }

    private void PopulateUserForRop(ROP rop, JSONObject usuarioJson, Realm realm) {
        try {
            rop.usuarioCreador = realm.createObject(User.class);
            rop.usuarioCreador.setId(usuarioJson.getInt("id"));
            rop.usuarioCreador.setName(usuarioJson.getString("name"));
            rop.usuarioCreador.setLastname(usuarioJson.getString("lastname"));
            rop.usuarioCreador.setUsername(usuarioJson.getString("username"));
            rop.usuarioCreador.setDni(usuarioJson.getString("dni"));
            rop.usuarioCreador.setEmail(usuarioJson.getString("email"));
            rop.usuarioCreador.setPhoto(usuarioJson.getString("photo"));
            rop.usuarioCreador.setCompany_id(usuarioJson.getInt("company_id"));
            rop.usuarioCreador.setManagement_id(usuarioJson.getInt("management_id"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void GuardarImagenesEnLocal(final RealmList<ImagenRO> listaImagenes, final String pathBase) {
        cantImagenesTotal = listaImagenes.size();
        for (int i = 0; i < listaImagenes.size(); i++) {
            ImagenRO img = listaImagenes.get(i);
            final String path = pathBase + img.getName();
            try {
                Glide.with(getActivity())
                        .load(img.getPath()).asBitmap()
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(final Bitmap resource,
                                                        GlideAnimation<? super Bitmap> glideAnimation) {
                                new Thread(new Runnable() {
                                    public void run() {
                                        File file = new File(path);
                                        try {
                                            // if (file.createNewFile()) {
                                            file.createNewFile();
                                            FileOutputStream ostream = new FileOutputStream(file);
                                            resource.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                                            ostream.flush();
                                            ostream.close();
                                            cantImagenesRecibidos += 1;
                                            if (validarDescargaTotalImagenes()) {
                                                if (Looper.myLooper() == null) {
                                                    Looper.prepare();
                                                    Message msg = new Message();
                                                    msg.obj = getString(R.string.msg_rop_recuperado_ok);
                                                    _handler.sendMessage(msg);
                                                    Looper.loop();
                                                }
                                            }
                                           /* }
                                            else {
                                                Log.e(getClass().getCanonicalName(), "Error al crear "
                                                        + file.getPath());
                                                cantImagenesRecibidos += 1;
                                                if (validarDescargaTotalImagenes()) {
                                                    if (Looper.myLooper() == null) {
                                                        Looper.prepare();
                                                        Message msg = new Message();
                                                        msg.obj = getString(R.string.msg_rop_recuperado_fail);
                                                        _handler.sendMessage(msg);
                                                        Looper.loop();
                                                    }
                                                }
                                            }*/
                                        } catch (IOException e) {
                                            Log.e("IOException", e.getMessage());
                                            e.printStackTrace();
                                            cantImagenesRecibidos += 1;
                                            if (validarDescargaTotalImagenes()) {
                                                if (Looper.myLooper() == null) {
                                                    Looper.prepare();
                                                    Message msg = new Message();
                                                    msg.obj = getString(R.string.msg_rop_recuperado_fail);
                                                    _handler.sendMessage(msg);
                                                    Looper.loop();
                                                }
                                            }
                                        }
                                    }
                                }).start();
                            }

                            @Override
                            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                super.onLoadFailed(e, errorDrawable);
                                Log.e("Glide_Error", e.getMessage());
                                cantImagenesRecibidos += 1;
                                if (validarDescargaTotalImagenes()) {
                                    if (Looper.myLooper() == null) {
                                        Looper.prepare();
                                        Message msg = new Message();
                                        msg.obj = getString(R.string.msg_rop_recuperado_fail);
                                        _handler.sendMessage(msg);
                                        Looper.loop();
                                    }
                                }
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
                cantImagenesRecibidos += 1;
                if (validarDescargaTotalImagenes()) {
                    if (Looper.myLooper() == null) {
                        Looper.prepare();
                        Message msg = new Message();
                        msg.obj = getString(R.string.msg_rop_recuperado_fail);
                        _handler.sendMessage(msg);
                        Looper.loop();
                    }
                }
            }
        }
    }

    private Handler _handler = new Handler() {
        public void handleMessage(Message m) {
            Messages.showToast(getView(), m.obj.toString());
            super.handleMessage(m);
        }
    };

    private void populateImagesForRopExisting(ROP rop, JSONArray imagesArray, Realm realm) {
        for (int i = 0; i < imagesArray.length(); i++) {
            try {
                JSONObject imgJson = imagesArray.getJSONObject(i);
                ImagenRO img = rop.listaImgComent.where().equalTo("name",
                        imgJson.getString("name")).findFirst();

                if (img == null) { // No se posee la imagen
                    ImagenRO imagen = realm.createObject(ImagenRO.class);
                    imagen.setValues(imgJson);
                    rop.listaImgComent.add(imagen);

                    String path = activityMain.getFilesDir().getPath() + "/" +
                            Constants.PATH_IMAGE_GALERY_ROP + rop.getTmpId() + "/";
                    GuardarImagenesEnLocal(imagen, path);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void GuardarImagenesEnLocal(ImagenRO img, String pathBase) {

        final String path = pathBase + img.getName();

        Glide.with(getActivity())
                .load(img.getPath()).asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        new Thread(new Runnable() {
                            public void run() {
                                File file = new File(path);
                                try {
                                    if (file.createNewFile()) {
                                        FileOutputStream ostream = new FileOutputStream(file);
                                        resource.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                                        ostream.flush();
                                        ostream.close();
                                    } else {
                                        Log.e(getClass().getCanonicalName(), "Error al crear " +
                                                file.getPath());
                                    }
                                } catch (IOException e) {
                                    Log.e("IOException", e.getLocalizedMessage());
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                });
    }

    private Boolean validarDescargaTotalImagenes() {
        boolean resu = false;
        if (cantImagenesTotal == cantImagenesRecibidos) {
            cantImagenesTotal = 0;
            cantImagenesRecibidos = 0;
            resu = true;
            dialogLoading.dismiss();
        }
        return resu;
    }

    private void notifyDowloandROP(ROP rop, boolean closed) {
        adapter.notifyAddRop(rop, closed);
    }
}