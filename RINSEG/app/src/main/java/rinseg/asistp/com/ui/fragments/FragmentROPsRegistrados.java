package rinseg.asistp.com.ui.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.github.clans.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rinseg.asistp.com.adapters.IncidenciaAdapter;
import rinseg.asistp.com.adapters.InspeccionAdapter;
import rinseg.asistp.com.listener.ListenerClick;
import rinseg.asistp.com.models.AccionPreventiva;
import rinseg.asistp.com.models.CompanyRO;
import rinseg.asistp.com.models.ImagenRO;
import rinseg.asistp.com.models.ROP;
import rinseg.asistp.com.models.User;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.services.RestClient;
import rinseg.asistp.com.services.Services;
import rinseg.asistp.com.ui.activities.ActivityMain;
import rinseg.asistp.com.adapters.RopAdapter;
import rinseg.asistp.com.ui.activities.ActivityRopCerradoDetalle;
import rinseg.asistp.com.utils.Constants;
import rinseg.asistp.com.utils.DialogLoading;
import rinseg.asistp.com.utils.Generic;
import rinseg.asistp.com.utils.Messages;
import rinseg.asistp.com.utils.RinsegModule;
import rinseg.asistp.com.utils.SharedPreferencesHelper;

import static rinseg.asistp.com.utils.Constants.MY_SHARED_PREFERENCES;

public class FragmentROPsRegistrados extends Fragment implements ListenerClick {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private FloatingActionButton fab;
    ActivityMain activityMain;

    private DialogLoading dialogLoading;

    private RecyclerView recyclerRops;
    private RecyclerView.Adapter ropAdapter;
    private RecyclerView.LayoutManager lManager;
    private List<ROP> listaRops = new ArrayList<>();

    Dialog recuperaRopDialog;
    Button btnRecuperaRop;
    Button btnCancelarRecuperaRop;
    EditText txtCodigoRecuperar;

    RealmConfiguration myConfig;

    int cantImagenesTotal = 0;
    int cantImagenesRecibidos = 0;

    public FragmentROPsRegistrados() {
        // Required empty public constructor
    }

    public static FragmentROPsRegistrados newInstance(String param1, String param2) {
        FragmentROPsRegistrados fragment = new FragmentROPsRegistrados();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rops_registrado, container, false);

        setUpElements(view);
        setUpActions();

        LoadRopCerrados();

        return view;
    }

    //Proceso para cargar las vistas
    private void setUpElements(View v) {
        fab = (FloatingActionButton) v.findViewById(R.id.btn_recupera_rop_registrado);
        activityMain = ((ActivityMain) getActivity());

        dialogLoading = new DialogLoading(activityMain);

        //instanciamos el dialog para recuperar rop
        recuperaRopDialog = new Dialog(this.getContext(), R.style.CustomDialogTheme);

        //configuracion para el recicler
        recyclerRops = (RecyclerView) v.findViewById(R.id.recycler_view_rops_registrados);
        recyclerRops.setHasFixedSize(true);
        // usar administrador para linearLayout
        lManager = new LinearLayoutManager(this.getActivity().getApplicationContext());
        recyclerRops.setLayoutManager(lManager);
        // Crear un nuevo Adaptador
        ropAdapter = new RopAdapter(listaRops, activityMain.getApplicationContext(), this);
        recyclerRops.setAdapter(ropAdapter);

        //configuramos Realm
        Realm.init(this.getActivity().getApplicationContext());
        myConfig = new RealmConfiguration.Builder()
                .name("rinseg.realm")
                .schemaVersion(2)
                .modules(new RinsegModule())
                .deleteRealmIfMigrationNeeded()
                .build();
    }

    private void setUpActions() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recuperaRopDialog.show();
                recuperaRopDialog.setContentView(R.layout.dialog_recupera_rop);

                btnRecuperaRop = (Button) recuperaRopDialog.findViewById(
                        R.id.btn_dialog_recupera_recuperar);
                btnCancelarRecuperaRop = (Button) recuperaRopDialog.findViewById(
                        R.id.btn_dialog_recupera_cancelar);
                txtCodigoRecuperar = (EditText) recuperaRopDialog.findViewById(
                        R.id.txt_dialog_codigo_recuperar);

                btnRecuperaRop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!txtCodigoRecuperar.getText().toString().trim().equals("")) {
                            try {
                                final int codRop = Integer.parseInt(
                                        txtCodigoRecuperar.getText().toString().trim());
                                RecuperarRopCerrado(codRop);
                            } catch (Exception e) {
                                Messages.showSB(getView(), "Asegúrese de ingresar un código válido");
                            }
                        } else {
                            Messages.showSB(getView(), "Ingrese el código de ROP", "");
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
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        activityMain.ShowButtonsBottom(false);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClicked(RopAdapter.RopViewHolder holder, int position) {
        //launchActivityRopDetalle(listaRops.get(position));
        replaceFragmentWithAccionesRegistradas(listaRops.get(position));
    }

    @Override
    public void onItemClicked(IncidenciaAdapter.IncidenciaViewHolder holder, int position) {
    }

    @Override
    public void onItemClicked(InspeccionAdapter.InspeccionViewHolder holder, int position) {
    }

    @Override
    public void onItemLongClicked(RopAdapter.RopViewHolder holder, int position) {
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private void LoadRopCerrados() {
        Realm realm = Realm.getInstance(myConfig);
        try {
            RealmResults<ROP> RopsRealm = realm.where(ROP.class).equalTo("estadoRop", 1).findAll()
                    .sort("dateClose", Sort.DESCENDING);

            for (int i = 0; i < RopsRealm.size(); i++) {
                ROP tRop = RopsRealm.get(i);
                listaRops.add(tRop);
                ropAdapter.notifyDataSetChanged();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            realm.close();
        }
    }

    /**
     * Módulo encargado de lanzar la actividad
     */
    public void launchActivityRopDetalle(ROP rop) {
        Intent RopDetalleIntent = new Intent().setClass(activityMain, ActivityRopCerradoDetalle.class);
        RopDetalleIntent.putExtra("ROPId", rop.getId());
        RopDetalleIntent.putExtra("ROPIdTmp", rop.getTmpId());

        startActivity(RopDetalleIntent);
    }

    private void replaceFragmentWithAccionesRegistradas(ROP rop){
        FragmentROPsRegistradoDetalle fragment = FragmentROPsRegistradoDetalle.newInstance(rop.getId(),rop.getTmpId());
        activityMain.replaceFragment(fragment,false,0,0,0,0);

    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    public void RecuperarRopCerrado(int codeRop) {
        View parentLAyout = getView().findViewById(R.id.frame_rop_cerrados_content);

        if (!Generic.IsOnRed(activityMain)) {
            Messages.showSB(parentLAyout, getString(R.string.no_internet), getString(R.string.ok));
            return;
        }

        dialogLoading.show();
        recuperaRopDialog.hide();

        // Obtenemos el token :
        SharedPreferencesHelper preferencesHelper = new SharedPreferencesHelper(
                activityMain.getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE));
        String token = preferencesHelper.getToken();

        RestClient restClient = new RestClient(Services.URL_ROPS);
        Call<ResponseBody> call = restClient.iServices.getRopClosed(codeRop, token);

        call.enqueue(new Callback<ResponseBody>() {
            View rootLayout = activityMain.findViewById(R.id.coordinator_activity_main);

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Realm realm = Realm.getInstance(myConfig);
                    try {
                        View parentLAyout = activityMain.findViewById(R.id.layout_content_main);

                        String body = response.body().string();

                        if (body.charAt(0) != '{') {
                            Messages.showSB(parentLAyout,
                                    getString(R.string.sincronizando_error), getString(R.string.ok));
                            return;
                        }
                        JSONObject jsonObject = new JSONObject(body);

                        JSONObject ropJSON = jsonObject.getJSONObject("rop");
                        JSONObject rCompanyJSON = ropJSON.getJSONObject("company");
                        JSONArray rImagesJSON = ropJSON.getJSONArray("images");
                        JSONArray rRopItemsJSON = ropJSON.getJSONArray("rop_items");
                        JSONObject usuario = ropJSON.getJSONObject("user");

                        ROP ropRecuperado = realm.where(ROP.class)
                                .equalTo("id", ropJSON.getInt("id")).findFirst();

                        if (ropRecuperado != null) {
                            realm.beginTransaction();
                            PopulateImagesForRopExisting(ropRecuperado, rImagesJSON, realm);
                            PopulateUserForRop(ropRecuperado, usuario, realm);
                            realm.commitTransaction();

                            ROP ropCopy = realm.copyFromRealm(ropRecuperado);
                            for (int i = 0; i < listaRops.size(); i++) {
                                int idRop = listaRops.get(i).getId();
                                if (idRop == ropCopy.getId()) {
                                    listaRops.remove(i);
                                    ropAdapter.notifyDataSetChanged();
                                }
                            }
                            listaRops.add(0, ropCopy);
                            ropAdapter.notifyDataSetChanged();
                            dialogLoading.dismiss();

                        } else {
                            realm.beginTransaction();
                            ropRecuperado = realm.createObject(ROP.class);
                            //Poblamos las tablas para el ROP
                            PopulateROP(ropRecuperado, ropJSON);
                            //PopulateCompanieForRop(ropRecuperado, rCompanyJSON, realm);
                            PopulateImagesForRopNew(ropRecuperado, rImagesJSON, realm);
                            PopulateRopItemsForRop(ropRecuperado, rRopItemsJSON, realm);
                            PopulateUserForRop(ropRecuperado, usuario, realm);
                            realm.commitTransaction();

                            ROP ropCopy = realm.copyFromRealm(ropRecuperado);

                            String path = activityMain.getFilesDir().getPath();
                            path = path + "/" + Constants.PATH_IMAGE_GALERY_ROP + ropCopy.getTmpId() + "/";

                            GuardarImagenesEnLocal(ropCopy.listaImgComent, path);

                            listaRops.add(0, ropCopy);
                            ropAdapter.notifyDataSetChanged();

                            if (ropCopy.listaImgComent.size() == 0) {
                                dialogLoading.dismiss();
                                Messages.showToast(rootLayout, getString(R.string.msg_rop_recuperado_ok));
                            }
                        }


                        //Messages.showToast(rootLayout, getString(R.string.msg_rop_recuperado_ok));
                    } catch (Exception e) {
                        dialogLoading.dismiss();
                        e.printStackTrace();
                        Messages.showSB(rootLayout, getString(R.string.msg_rop_recuperado_fail), "ok");
                    } finally {
                        realm.close();
                    }
                } else {
                    if (response.code() == 422) {
                        // Mostramos mensaje del servidor :
                        try {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            String message = jsonObject.getString("message");
                            Messages.showSB(rootLayout, message, "ok");
                            dialogLoading.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                            dialogLoading.dismiss();
                            Messages.showSB(rootLayout,
                                    getString(R.string.msg_rop_recuperado_fail), "ok");
                        }
                    } else {
                        dialogLoading.dismiss();
                        Messages.showSB(rootLayout, getString(R.string.msg_rop_recuperado_fail), "ok");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogLoading.dismiss();
                Messages.showSB(rootLayout, getString(R.string.msg_rop_recuperado_fail), "ok");
            }
        });
    }

    private void PopulateROP(ROP rop, JSONObject ropJson) {
        try {
            rop.setEstadoRop(1);
            rop.setId(ropJson.getInt("id"));
            rop.setCode(ropJson.getInt("code"));
            rop.setRiskId(ropJson.getInt("risk_id"));
            rop.setEventId(ropJson.getInt("event_id"));
            rop.setTargetId(ropJson.getInt("target_id"));
            rop.setAreaId(ropJson.getInt("area_id"));
            rop.setArea(ropJson.getString("area_name"));
            rop.setEventPlace(ropJson.getString("event_place"));
            rop.setCompanyId(ropJson.getInt("company_id"));
            rop.setEventDateString(ropJson.getString("event_date"));
            rop.setEventDescription(ropJson.getString("event_description"));
            //rop.set(ropJson.getString("worker_commitment"));
            rop.setReporterName(ropJson.getString("reporter_name"));
            rop.setReporterCompany(ropJson.getString("reporter_company"));
            rop.setSupervisorName(ropJson.getString("supervisor_name"));
            rop.setSupervisorCompany(ropJson.getString("supervisor_company"));
            //rop.setResearch_required(ropJson.getBoolean("research_required"));
            rop.setDateCloseString(ropJson.getString("date_close"));
            rop.setUserId(ropJson.getInt("user_id"));

            rop.setTmpId(String.valueOf(ropJson.getInt("id")));

            int research_required = ropJson.getInt("research_required");
            if (research_required == 1) {
                rop.setResearch_required(true);
            } else {
                rop.setResearch_required(false);
            }
            Date eventDate = Generic.dateFormatterMySql.parse(rop.getEventDateString());
            rop.setEventDate(eventDate);

            //Creamos la carpeta que contendra las imagenes
            boolean createdImageGalery = Generic.CrearCarpetaImagenesPorRop(getActivity()
                    .getApplicationContext(), rop.getTmpId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void PopulateCompanieForRop(ROP rop, JSONObject companyJson, Realm realm) {
        try {
            CompanyRO company = realm.createObject(CompanyRO.class);
            company.setId(companyJson.getInt("id"));
            company.setDisplayName(companyJson.getString("display_name"));
            rop.setCompany(company);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void PopulateImagesForRopNew(ROP rop, JSONArray imagesArray, Realm realm) {
        for (int i = 0; i < imagesArray.length(); i++) {
            try {
                JSONObject imgJson = imagesArray.getJSONObject(i);
                ImagenRO imagen = realm.createObject(ImagenRO.class);
                imagen.setId(imgJson.getInt("id"));
                imagen.setName(imgJson.getString("name"));
                imagen.setDescripcion(imgJson.getString("description"));
                imagen.setPath(imgJson.getString("path"));
                rop.listaImgComent.add(imagen);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void PopulateRopItemsForRop(ROP rop, JSONArray RopItems, Realm realm) {
        for (int i = 0; i < RopItems.length(); i++) {
            try {
                JSONObject item = RopItems.getJSONObject(i);
                // CompanyRO companie = realm.createObjectFromJson (CompanyRO.class,c);
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
            }
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
                                        Log.e("onResourceReady", "onResourceReady");
                                        File file = new File(path);
                                        try {
                                            Log.e("empeso", "empezo");
                                            file.createNewFile();
                                            FileOutputStream ostream = new FileOutputStream(file);
                                            resource.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                                            ostream.flush();
                                            ostream.close();
                                            Log.e("termino", "termino");
                                            cantImagenesRecibidos += 1;
                                            if (validarDescargaTotalImagenes()) {
                                                Looper.prepare();
                                                Message msg = new Message();
                                                msg.obj = getString(R.string.msg_rop_recuperado_ok);
                                                _handler.sendMessage(msg);
                                                Looper.loop();
                                            }
                                        } catch (IOException e) {
                                            Log.e("IOException", e.getLocalizedMessage());
                                            e.printStackTrace();
                                            cantImagenesRecibidos += 1;
                                            if (validarDescargaTotalImagenes()) {
                                                Looper.prepare();
                                                Message msg = new Message();
                                                msg.obj = getString(R.string.msg_rop_recuperado_fail);
                                                _handler.sendMessage(msg);
                                                Looper.loop();
                                            }
                                        }
                                    }
                                }).start();
                            }

                            @Override
                            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                super.onLoadFailed(e, errorDrawable);
                                cantImagenesRecibidos += 1;
                                if (validarDescargaTotalImagenes()) {
                                    Looper.prepare();
                                    Message msg = new Message();
                                    msg.obj = getString(R.string.msg_rop_recuperado_fail);
                                    _handler.sendMessage(msg);
                                    Looper.loop();
                                }
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Handler _handler = new Handler() {
        public void handleMessage(Message m) {
            Messages.showToast(getView(), m.obj.toString());
            super.handleMessage(m);
        }
    };


    private void PopulateImagesForRopExisting(ROP rop, JSONArray imagesArray, Realm realm) {
        for (int i = 0; i < imagesArray.length(); i++) {
            try {
                JSONObject imgJson = imagesArray.getJSONObject(i);
                ImagenRO img = rop.listaImgComent.where().equalTo("name",
                        imgJson.getString("name")).findFirst();

                if (img == null) {
                    ImagenRO imagen = realm.createObject(ImagenRO.class);
                    imagen.setId(imgJson.getInt("id"));
                    imagen.setName(imgJson.getString("name"));
                    imagen.setDescripcion(imgJson.getString("description"));
                    imagen.setPath(imgJson.getString("path"));
                    rop.listaImgComent.add(imagen);

                    String path = activityMain.getFilesDir().getPath();
                    path = path + "/" + Constants.PATH_IMAGE_GALERY_ROP + rop.getTmpId() + "/";

                    GuardarImagenesEnLocal(imagen, path);
                }

            } catch (Exception e) {
                e.printStackTrace();
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

    private void GuardarImagenesEnLocal(ImagenRO img, String pathBase) {

        final String path = pathBase + img.getName();

        Glide.with(getActivity())
                .load(img.getPath()).asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        new Thread(new Runnable() {
                            public void run() {
                                Log.e("onResourceReady", "onResourceReady");
                                File file = new File(path);
                                try {
                                    Log.e("empeso", "empezo");
                                    file.createNewFile();
                                    FileOutputStream ostream = new FileOutputStream(file);
                                    resource.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                                    ostream.flush();
                                    ostream.close();
                                    Log.e("termino", "termino");

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
}
