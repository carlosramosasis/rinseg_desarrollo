package rinseg.asistp.com.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rinseg.asistp.com.adapters.AccionPreventivaAdapter;
import rinseg.asistp.com.adapters.AccionPreventivaDetalleAdapter;
import rinseg.asistp.com.adapters.RopAdapter;
import rinseg.asistp.com.models.AccionPreventiva;
import rinseg.asistp.com.models.AreaRO;
import rinseg.asistp.com.models.CompanyRO;
import rinseg.asistp.com.models.EventItemsRO;
import rinseg.asistp.com.models.EventRO;
import rinseg.asistp.com.models.FotoModel;
import rinseg.asistp.com.models.ImagenRO;
import rinseg.asistp.com.models.ROP;
import rinseg.asistp.com.models.RiskRO;
import rinseg.asistp.com.models.SettingsRopRO;
import rinseg.asistp.com.models.TargetRO;
import rinseg.asistp.com.models.User;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.services.RestClient;
import rinseg.asistp.com.services.Services;
import rinseg.asistp.com.ui.activities.ActivityFotoComentario;
import rinseg.asistp.com.ui.activities.ActivityGaleria;
import rinseg.asistp.com.ui.activities.ActivityMain;
import rinseg.asistp.com.ui.activities.ActivityRopCerradoDetalle;
import rinseg.asistp.com.utils.Constants;
import rinseg.asistp.com.utils.DialogLoading;
import rinseg.asistp.com.utils.DialogRINSEG;
import rinseg.asistp.com.utils.Generic;
import rinseg.asistp.com.utils.Messages;
import rinseg.asistp.com.utils.RinsegModule;
import rinseg.asistp.com.utils.SharedPreferencesHelper;

import static rinseg.asistp.com.utils.Constants.MY_SHARED_PREFERENCES;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentROPCerrado1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentROPCerrado1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentROPCerrado1 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    ActivityRopCerradoDetalle activityMain;

    public FloatingActionButton btnGaleriaFotos, btnImportarFotos, btnTomarFoto, btnEnviarImg,
            btnGeneratePDF;
    private FloatingActionsMenu fabMenu;

    Bundle bundle;

    ROP mRop;
    RealmConfiguration myConfig;

    private User usuario;
    SettingsRopRO sRop;

    TextView txtCodigo;
    TextView txtAprobado;
    TextView txtRevision;
    TextView txtPotencialPerdida;
    TextView txtTipoEvento;
    TextView txtBlanco;
    TextView txtAreaResopnsable;
    TextView txtLugarExacto;
    TextView txtEmpresa;
    TextView txtFecha;
    TextView txtHora;
    TextView txtDescripcion;
    TextView txtActoSubestandar;
    RecyclerView recyclerAccionesInmediatasRealizadas;
    private RecyclerView.Adapter accionPreventivaAdapter;
    private RecyclerView.LayoutManager lManager;
    private List<AccionPreventiva> listaAccionesPreventivas = new ArrayList<>();
    TextView txtCompromisoTrabajador;
    TextView txtReportante;
    TextView txtEmpresaReportante;
    TextView txtSupervisor;
    TextView txtEmpresaSupervisor;
    TextView txtRequiereInvestigacion;

    Calendar newDateForROP;

    //import foto
    public int PICK_IMAGE_REQUEST = 1;
    //tomar foto
    public int REQUEST_IMAGE_CAPTURE = 1;

    static Uri capturedImageUri = null;

    int cantImagenesTotal = 0;
    int cantImagenesEnviadas = 0;
    int cantImagenesEnviadasYrecibidos = 0;

    private DialogLoading dialogLoading;

    public FragmentROPCerrado1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentROPPendiente1.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentROPCerrado1 newInstance(String param1, String param2) {
        FragmentROPCerrado1 fragment = new FragmentROPCerrado1();
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
        View view = inflater.inflate(R.layout.fragment_rop_cerrado1, container, false);


        setUpElements(view);
        setUpActions();

        LoadSettingRop();
        LoadUser();

        LoadRop();

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        activityMain.toolbar.setTitle(R.string.title_rop);
        MostrarCantidadImagenesRop(mRop.listaImgComent);

    }

    // TODO: Rename method, update argument and hook method into UI event
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            try {
                Uri imagen = null;
                if (data != null) {
                    if (data.getData() != null) {
                        imagen = data.getData();
                    }
                } else if (capturedImageUri != null) {
                    imagen = capturedImageUri;
                    capturedImageUri = null;
                }

                if (imagen != null) {
                    launchActivityFotoComentario(imagen);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//// TODO: ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: METODOS ::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    //Proceso para cargar las vistas
    private void setUpElements(View v) {
        activityMain = ((ActivityRopCerradoDetalle) getActivity());

        bundle = getArguments();

        dialogLoading = new DialogLoading(activityMain);

        newDateForROP = Calendar.getInstance();

        btnGaleriaFotos = (FloatingActionButton) v.findViewById(R.id.rd_fab_set_dir);
        btnImportarFotos = (FloatingActionButton) v.findViewById(R.id.rd_fab_import_foto);
        btnTomarFoto = (FloatingActionButton) v.findViewById(R.id.rd_fab_tomar_foto);
        btnEnviarImg = (FloatingActionButton) v.findViewById(R.id.rd_fab_set_enviar);
        btnGeneratePDF = (FloatingActionButton) v.findViewById(R.id.fab_rop_detail_generate_pdf);
        fabMenu = (FloatingActionsMenu) v.findViewById(R.id.rd_fab_menu_rop);

        txtCodigo = (TextView) v.findViewById(R.id.rd_txt_codigo);
        txtAprobado = (TextView) v.findViewById(R.id.rd_txt_aprobado);
        txtRevision = (TextView) v.findViewById(R.id.rd_txt_revision);
        txtPotencialPerdida = (TextView) v.findViewById(R.id.rd_txt_potencial_perdida);
        txtTipoEvento = (TextView) v.findViewById(R.id.rd_txt_tipo_evento);
        txtBlanco = (TextView) v.findViewById(R.id.rd_txt_blanco);
        txtAreaResopnsable = (TextView) v.findViewById(R.id.rd_txt_area_responsable);
        txtLugarExacto = (TextView) v.findViewById(R.id.rd_txt_lugar_exacto);
        txtEmpresa = (TextView) v.findViewById(R.id.rd_txt_empresa);
        txtFecha = (TextView) v.findViewById(R.id.rd_txt_fecha);
        txtHora = (TextView) v.findViewById(R.id.rd_txt_hora);
        txtDescripcion = (TextView) v.findViewById(R.id.rd_txt_descripcion);
        txtActoSubestandar = (TextView) v.findViewById(R.id.rd_txt_acto_subestandar);

        //configuracion para el recicler
        recyclerAccionesInmediatasRealizadas = (RecyclerView) v.findViewById(R.id.rd_recycler_acciones_realizadas);
        recyclerAccionesInmediatasRealizadas.setHasFixedSize(true);
        // usar administrador para linearLayout
        lManager = new LinearLayoutManager(this.getActivity().getApplicationContext());
        recyclerAccionesInmediatasRealizadas.setLayoutManager(lManager);
        // Crear un nuevo Adaptador
        accionPreventivaAdapter = new AccionPreventivaDetalleAdapter(listaAccionesPreventivas);
        recyclerAccionesInmediatasRealizadas.setAdapter(accionPreventivaAdapter);


        txtCompromisoTrabajador = (TextView) v.findViewById(R.id.rd_txt_compromiso);
        txtReportante = (TextView) v.findViewById(R.id.rd_txt_reportante);
        txtEmpresaReportante = (TextView) v.findViewById(R.id.rd_txt_empresa_de_reportante);
        txtSupervisor = (TextView) v.findViewById(R.id.rd_txt_supervisor);
        txtEmpresaSupervisor = (TextView) v.findViewById(R.id.rd_txt_empresa_de_supervisor);
        txtRequiereInvestigacion = (TextView) v.findViewById(R.id.rd_txt_requiere_investigacion);


        //configuramos Realm
        Realm.init(this.getActivity().getApplicationContext());
        myConfig = new RealmConfiguration.Builder()
                .name("rinseg.realm")
                .schemaVersion(2)
                .modules(new RinsegModule())
                .deleteRealmIfMigrationNeeded()
                .build();

    }

    //cargamos los eventos
    private void setUpActions() {
        btnTomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int permissionCheckCamera = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
                int permissionCheckWrite = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permissionCheckCamera != PackageManager.PERMISSION_GRANTED || permissionCheckWrite != PackageManager.PERMISSION_GRANTED) {
                    Permissions();
                }

                if (permissionCheckCamera == PackageManager.PERMISSION_GRANTED || permissionCheckWrite == PackageManager.PERMISSION_GRANTED) {
                    Calendar cal = Calendar.getInstance();
                    File file = new File(Environment.getExternalStorageDirectory(), (cal.getTimeInMillis() + ".jpg"));
                    if (!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else {
                        file.delete();
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    capturedImageUri = Uri.fromFile(file);

                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
                        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            }
        });

        btnImportarFotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        btnGaleriaFotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivityGaleria();
            }
        });

        btnEnviarImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Realm realm = Realm.getInstance(myConfig);
                try {
                    final ROP ropCopy = realm.copyFromRealm(mRop);
                    cantImagenesTotal = 0;
                    List<ImagenRO> listaImagen = new ArrayList<ImagenRO>();
                    for (int i = 0; i < ropCopy.listaImgComent.size(); i++) {
                        ImagenRO img = ropCopy.listaImgComent.get(i);
                        if (img.getId() == 0) {
                            cantImagenesTotal += 1;
                            listaImagen.add(img);
                        }
                    }

                    if (cantImagenesTotal > 0){
                        dialogLoading.show();
                        for (ImagenRO image : listaImagen){
                            new EnviarImagenRop(image, ropCopy.getTmpId(), ropCopy.getId(), usuario.getApi_token()).execute("", "", "");
                        }
                    }else{
                        Messages.showToast(getView(),getString(R.string.msg_error_no_nuevas_imagenes));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    realm.close();
                } finally {
                    realm.close();
                }
            }
        });

        btnGeneratePDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Obtenemos el token :
                SharedPreferencesHelper preferencesHelper = new SharedPreferencesHelper(
                        activityMain.getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE));
                String token = preferencesHelper.getToken();

                final DialogLoading loading = new DialogLoading(activityMain);
                loading.show();

                RestClient restClient = new RestClient(Services.URL_ROPS);
                Call<ResponseBody> call = restClient.iServices.downloadRopPDF(mRop.getId(), token);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if ( response.isSuccessful() ) {
                            // Mostramos dialog de éxito :
                            try {
                                final File file = Environment.getExternalStoragePublicDirectory(
                                        Environment.DIRECTORY_DOWNLOADS);
                                final String nameFile = "ROP_" + String.valueOf(mRop.getId())
                                        + ".pdf";
                                String path = file.getPath() + "/" + nameFile;
                                OutputStream out = new FileOutputStream(path);
                                out.write(response.body().bytes());
                                out.close();

                                loading.dismiss();
                                fabMenu.collapse();

                                // Mostramos SnackBar y acción para abrir el documento :
                                assert getView() != null;
                                Snackbar snackbar = Snackbar
                                        .make(getView(), R.string.msg_succeess_generate_pdf,
                                                Snackbar.LENGTH_LONG)
                                        .setAction("ABRIR", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                openPDF(file, nameFile);
                                            }
                                        });
                                snackbar.show();

                            } catch (Exception e) {
                                loading.dismiss();
                                e.printStackTrace();
                            }
                        } else {
                            loading.dismiss();
                            Messages.showSB(
                                    getView(), getString(R.string.msg_error_guardar_inspeccion), "ok");
                            Log.e("TAG_OnResponse", response.errorBody() + " - " +
                                    response.message() + "code :" + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        loading.dismiss();
                        t.printStackTrace();
                    }
                });
            }
        });
    }

    /** Módulo para abrir el archivo PDF */
    private void openPDF(File path, String nameFile) {
        File file = new File(path, nameFile);
        Uri pathUri = Uri.fromFile(file);
        Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
        pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pdfOpenintent.setDataAndType(pathUri, "application/pdf");
        try {
            startActivity(pdfOpenintent);
        }
        catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void Permissions() {

        ArrayList<String> especificacionPermisos = new ArrayList<String>();

        int permissionCheckCamera = ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.CAMERA);
        int permissionCheckWrite = ContextCompat.checkSelfPermission(
                this.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheckCamera != PackageManager.PERMISSION_GRANTED) {
            especificacionPermisos.add(Manifest.permission.CAMERA);
        }

        if (permissionCheckWrite != PackageManager.PERMISSION_GRANTED) {
            especificacionPermisos.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        String[] permisos = new String[especificacionPermisos.size()];
        permisos = especificacionPermisos.toArray(permisos);

        if (especificacionPermisos.size() > 0) {
            this.requestPermissions(permisos, REQUEST_IMAGE_CAPTURE);
        }

    }

    private void LoadSettingRop() {
        final Realm realm = Realm.getInstance(myConfig);
        try {
            sRop = realm.where(SettingsRopRO.class).findFirst();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            realm.close();
        }

    }

    private void LoadUser() {
        final Realm realm = Realm.getInstance(myConfig);
        try {
            usuario = realm.where(User.class).findFirst();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            realm.close();
        }

    }


    private void LoadRop() {

        int idRop = 0;
        if (bundle != null) {
            idRop = bundle.getInt("ROPId", 0);

            //ROP tmpRop = new ROP();
            if (idRop != 0) {
                final Realm realm = Realm.getInstance(myConfig);
                try {
                    mRop = realm.where(ROP.class).equalTo("id", idRop).findFirst();
                    if (mRop == null) {
                        return;
                    }


                    txtCodigo.setText(String.valueOf(mRop.getId()));


                    // recuperar potencial de perdida
                    for (int i = 0; i < sRop.risks.size(); i++) {
                        RiskRO tmpRisk = sRop.risks.get(i);
                        if (tmpRisk.getId() == mRop.getRiskId()) {
                            txtPotencialPerdida.setText(tmpRisk.getDisplayName());
                            break;
                        }
                    }

                    // recuperar tipo de evento
                    for (int i = 0; i < sRop.events.size(); i++) {
                        EventRO tmpEvent = sRop.events.get(i);
                        if (tmpEvent.getId() == mRop.getEventId()) {
                            txtTipoEvento.setText(tmpEvent.getDisplayName());
                            break;
                        }
                    }

                    // recuperar Blanco
                    for (int i = 0; i < sRop.targets.size(); i++) {
                        TargetRO tmpTarget = sRop.targets.get(i);
                        if (tmpTarget.getId() == mRop.getTargetId()) {
                            txtBlanco.setText(tmpTarget.getDisplayName());
                            break;
                        }
                    }

                    // recuperar area responsable
                    for (int i = 0; i < sRop.areas.size(); i++) {
                        AreaRO tmpArea = sRop.areas.get(i);
                        if (tmpArea.getId() == mRop.getAreaId()) {
                            txtAreaResopnsable.setText(tmpArea.getDisplayName());
                            break;
                        }
                    }

                    // recuperar lugar exacto
                    txtLugarExacto.setText(mRop.getEventPlace());

                    // recuperar Empresa
                    for (int i = 0; i < sRop.companies.size(); i++) {
                        CompanyRO tmpCompany = sRop.companies.get(i);
                        if (tmpCompany.getId() == mRop.getCompanyId()) {
                            txtEmpresa.setText(tmpCompany.getDisplayName());
                            break;
                        }
                    }

                    // recuperar Fecha y Hora
                    newDateForROP.setTime(mRop.getEventDate());
                    txtFecha.setText(Generic.dateFormatter.format(newDateForROP.getTime()));
                    txtHora.setText(Generic.timeFormatter.format(newDateForROP.getTime()));

                    // recuperar descripcion
                    txtDescripcion.setText(mRop.getEventDescription());

                    // recuperar acto subestandar
                    txtActoSubestandar.setText("");
                    for (int i = 0; i < mRop.listaEventItems.size(); i++) {
                        String actosSubestandars = txtActoSubestandar.getText().toString();
                        if (i > 0) {
                            actosSubestandars += "\n";
                        }

                        txtActoSubestandar.setText(actosSubestandars + "- " + mRop.listaEventItems.get(i).getName() + ".");
                    }


                    // recuperar accion inmediata realizada
                    for (int i = 0; i < mRop.listaAccionPreventiva.size(); i++) {
                        AccionPreventiva tAccionPrev = mRop.listaAccionPreventiva.get(i);
                        listaAccionesPreventivas.add(tAccionPrev);
                        accionPreventivaAdapter.notifyDataSetChanged();
                    }


                    // recuperar Reportante
                    if (usuario != null) {
                        txtReportante.setText(usuario.getName() + " " + usuario.getLastname());

                        // recuperar Empresa reposrtante
                        for (int i = 0; i < sRop.companies.size(); i++) {
                            CompanyRO tmpCompany = sRop.companies.get(i);
                            if (tmpCompany.getId() == usuario.getCompany_id()) {
                                txtEmpresaReportante.setText(tmpCompany.getDisplayName());
                                break;
                            }
                        }
                    }

                    //recuperar datos de supervisor
                    txtSupervisor.setText(mRop.getSupervisorName());
                    // recuperar Empresa supervisor
                    for (int i = 0; i < sRop.companies.size(); i++) {
                        CompanyRO tmpCompany = sRop.companies.get(i);
                        if (tmpCompany.getId() == mRop.getSupervisorIdCompany()) {
                            txtEmpresaSupervisor.setText(tmpCompany.getDisplayName());
                            break;
                        }
                    }

                    //recuperar requiere investigacion
                    if (mRop.isResearch_required()) {
                        txtRequiereInvestigacion.setText("Si");
                    } else {
                        txtRequiereInvestigacion.setText("No");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    realm.close();
                }
            }
        }
    }

    public void launchActivityGaleria() {

        Intent GaleriaIntent = new Intent().setClass(activityMain, ActivityGaleria.class);
        GaleriaIntent.putExtra("ROPtmpId", mRop.getTmpId());
        startActivity(GaleriaIntent);
    }

    public void launchActivityFotoComentario(Uri uriImagen) {
        FotoModel fotoMd = new FotoModel();

        Uri uri = uriImagen;
        fotoMd.uri = uri;
        //fotoMd.bitmap = null;

        Intent FotoComentarioIntent = new Intent().setClass(activityMain, ActivityFotoComentario.class);
        FotoComentarioIntent.putExtra("imagen", fotoMd);
        FotoComentarioIntent.putExtra("ROPtmpId", mRop.getTmpId());
        startActivity(FotoComentarioIntent);
    }

    public void MostrarCantidadImagenesRop(RealmList<ImagenRO> listaImagenes) {
        int cant = listaImagenes.size();
        this.btnGaleriaFotos.setTitle(getString(R.string.label_fotos) + " (" + cant + ")");
    }

    public class EnviarImagenRop extends AsyncTask<String, Integer, Integer> {
        private ImagenRO imagenRop;
        private String tmpIdRop;
        private int idRop;
        private String apitoken;


        EnviarImagenRop(ImagenRO imagen, String pTmpIdRop, int pIdRop, String mApiToken) {
            imagenRop = imagen;
            tmpIdRop = pTmpIdRop;
            idRop = pIdRop;
            apitoken = mApiToken;
        }

        @Override
        protected Integer doInBackground(String... strings) {
            int errorValue = 0;
            try {

                RestClient restClient = new RestClient(Services.URL_ROPS);

                File myDir = getActivity().getApplicationContext().getFilesDir();
                File file = new File(myDir, Constants.PATH_IMAGE_GALERY_ROP + tmpIdRop + "/" + imagenRop.getName());

                // create RequestBody instance from file
                RequestBody requestFile =
                        RequestBody.create(MediaType.parse("multipart/form-data"), file);

                // MultipartBody.Part is used to send also the actual file name
                MultipartBody.Part body =
                        MultipartBody.Part.createFormData("file_image", file.getName(), requestFile);

                // add another part within the multipart request

                RequestBody description =
                        RequestBody.create(
                                MediaType.parse("multipart/form-data"), imagenRop.getDescripcion());
                RequestBody rop_id =
                        RequestBody.create(
                                MediaType.parse("multipart/form-data"), String.valueOf(idRop));


                Call<ResponseBody> call = restClient.iServices.setImageRop(rop_id, description, body, apitoken);

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        int code = response.code();
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                String status = jsonObject.getString("status");
                                if (status.equals(Constants.SUCCESS)) {
                                    JSONObject messageResult = jsonObject.getJSONObject("message");
                                    JSONObject ropImageResult = messageResult.getJSONObject("rop_image");

                                    Realm real = Realm.getInstance(myConfig);

                                    imagenRop = real.where(ImagenRO.class).equalTo("name", ropImageResult.getString("name")).findFirst();
                                    if (imagenRop != null) {
                                        real.beginTransaction();
                                        imagenRop.setId(ropImageResult.getInt("id"));
                                        real.commitTransaction();
                                        cantImagenesEnviadasYrecibidos += 1;
                                    }

                                }

                                Log.e("jsonObject", jsonObject.toString());


                            } catch (Exception e) {
                                // dialogLoading.dismiss();
                                e.printStackTrace();
                                // Messages.showSB(getView(), getString(R.string.msg_error_guardar), "ok");
                            }


                        } else {
                            Log.e("imagen", response.message());
                            Log.e("imagen error", response.errorBody().toString());
                            //dialogLoading.dismiss();
                            //Messages.showSB(getView(), getString(R.string.msg_login_fail), "ok");
                        }

                        cantImagenesEnviadas += 1;
                        postExecute();

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        cantImagenesEnviadas += 1;
                        postExecute();
                        Log.e("failure", t.getMessage());
                        //dialogLoading.dismiss();
                        //Messages.showSB(getView(), getString(R.string.msg_servidor_inaccesible), "ok");
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
                cantImagenesEnviadas += 1;
                postExecute();
            }

            return errorValue;
        }

        @Override
        protected void onPostExecute(Integer errorValue) {

        }


    }

    public void postExecute() {
        if (cantImagenesTotal == cantImagenesEnviadas) {
            dialogLoading.hide();

            mostrarDialogRop(getString(R.string.msg_envio_imagen_ok) );

            cantImagenesTotal = 0;
            cantImagenesEnviadas = 0;
            cantImagenesEnviadasYrecibidos = 0;
        }
    }

    private void mostrarDialogRop(String msg) {
        final DialogRINSEG dialogRINSEG = new DialogRINSEG(getActivity());
        dialogRINSEG.show();
        dialogRINSEG.setBody(msg);
        dialogRINSEG.btnCancelar.setVisibility(View.GONE);
        dialogRINSEG.btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogRINSEG.dismiss();
            }
        });
    }


}
