package rinseg.asistp.com.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rinseg.asistp.com.models.AccionPreventiva;
import rinseg.asistp.com.models.FotoModel;
import rinseg.asistp.com.models.ImagenRO;
import rinseg.asistp.com.models.ROP;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.services.RestClient;
import rinseg.asistp.com.services.Services;
import rinseg.asistp.com.ui.activities.ActivityFotoComentario;
import rinseg.asistp.com.ui.activities.ActivityGaleria;
import rinseg.asistp.com.ui.activities.ActivityRopRegistradoDetalle;
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
 * {@link FragmentLevantarAccionPreventiva.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentLevantarAccionPreventiva#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentLevantarAccionPreventiva extends Fragment {

    ActivityRopRegistradoDetalle activity;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ID_ACCION = "idAccion";
    private static final String ARG_ID_ROP = "idRop";

    // TODO: Rename and change types of parameters
    private int mIdAccion;
    private int mIdRop;

    private AccionPreventiva accionPreventivaRO;
    private ROP ropRO;

    TextView txtResponsable, txtAccion, txtFechaLimite;
    CheckBox chkSeValido;
    Button btnGuardar;

    FloatingActionsMenu fabMenuRop;
    com.getbase.floatingactionbutton.FloatingActionButton btnTomarFoto, btnImportarFotos, btnGaleriaFotos;

    RealmConfiguration myConfig;

    DialogLoading dialog;
    DialogRINSEG dialogConfirm;

    Calendar mCalendar;

    //import foto
    public int PICK_IMAGE_REQUEST = 1;
    //tomar foto
    public int REQUEST_IMAGE_CAPTURE = 1;

    static Uri capturedImageUri = null;

    int totalToSend, correctSend, failSend = 0;

    private OnFragmentInteractionListener mListener;

    public FragmentLevantarAccionPreventiva() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentLevantarAccionPreventiva.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentLevantarAccionPreventiva newInstance(int idAccion, int idRop) {
        FragmentLevantarAccionPreventiva fragment = new FragmentLevantarAccionPreventiva();
        Bundle args = new Bundle();
        args.putInt(ARG_ID_ACCION, idAccion);
        args.putInt(ARG_ID_ROP, idRop);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIdAccion = getArguments().getInt(ARG_ID_ACCION);
            mIdRop = getArguments().getInt(ARG_ID_ROP);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_levantar_accion_preventiva, container, false);

        setUpElements(view);
        setUpActions();
        setUpData();

        return view;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
       /* if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
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

    @Override
    public void onResume() {
        super.onResume();
        if (ropRO != null) {
            MostrarCantidadImagenesAccionesLevantadas(ropRO.listaImgComent);
        }
    }


    private void setUpElements(View v) {
        activity = (ActivityRopRegistradoDetalle) getActivity();
        txtResponsable = (TextView) v.findViewById(R.id.rrd_txt_responsable);
        txtAccion = (TextView) v.findViewById(R.id.rrd_txt_accion);
        txtFechaLimite = (TextView) v.findViewById(R.id.rrd_txt_fecha_limite);
        chkSeValido = (CheckBox) v.findViewById(R.id.rrd_chk_se_valido);
        btnGuardar = (Button) v.findViewById(R.id.rrd_btn_guardar);

        fabMenuRop = (FloatingActionsMenu) v.findViewById(R.id.fab_menu_rop);
        fabMenuRop.setVisibility(View.VISIBLE);

        btnGaleriaFotos = (com.getbase.floatingactionbutton.FloatingActionButton) v.findViewById(R.id.fab_set_dir);
        btnImportarFotos = (com.getbase.floatingactionbutton.FloatingActionButton) v.findViewById(R.id.fab_import_foto);
        btnTomarFoto = (com.getbase.floatingactionbutton.FloatingActionButton) v.findViewById(R.id.fab_tomar_foto);

        myConfig = new RealmConfiguration.Builder()
                .name("rinseg.realm")
                .schemaVersion(2)
                .modules(new RinsegModule())
                .deleteRealmIfMigrationNeeded()
                .build();
    }

    private void setUpActions() {
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogConfirm();
            }
        });

        btnTomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCheckCamera = ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CAMERA);
                int permissionCheckWrite = ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permissionCheckCamera != PackageManager.PERMISSION_GRANTED ||
                        permissionCheckWrite != PackageManager.PERMISSION_GRANTED) {
                    Permissions();
                }

                if (permissionCheckCamera == PackageManager.PERMISSION_GRANTED ||
                        permissionCheckWrite == PackageManager.PERMISSION_GRANTED) {
                    Calendar cal = Calendar.getInstance();
                    File file = new File(Environment.getExternalStorageDirectory(),
                            (cal.getTimeInMillis() + ".jpg"));
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
    }


    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private void setUpData() {
        Realm realm = Realm.getInstance(myConfig);
        try {
            if (mIdAccion != 0) {
                accionPreventivaRO = realm.where(AccionPreventiva.class)
                        .equalTo("id", mIdAccion).findFirst();
                if (accionPreventivaRO != null) {
                    txtResponsable.setText(accionPreventivaRO.getAccion());
                    txtAccion.setText(accionPreventivaRO.getAccion());
                    txtFechaLimite.setText(Generic.dateFormatter.format(accionPreventivaRO.getFecha()));
                }
            }
            if (mIdRop != 0) {
                ropRO = realm.where(ROP.class)
                        .equalTo("id", mIdRop).findFirst();
            }

        } catch (Exception e) {
            e.printStackTrace();
            realm.close();
        } finally {
            realm.close();
        }
    }

    private void sendData() {
        mCalendar = Calendar.getInstance();

        // Obtenemos el token :
        SharedPreferencesHelper preferencesHelper = new SharedPreferencesHelper(
                activity.getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE));
        final String token = preferencesHelper.getToken();

        // Mostramos dialog mientras se procese :
        dialog = new DialogLoading(activity);
        dialog.show();

        final Realm realm = Realm.getInstance(myConfig);
        final AccionPreventiva accionPreventivaToFix = realm.copyFromRealm(accionPreventivaRO);
        accionPreventivaToFix.setFechaFinalizacionString(Generic.dateFormatterMySql.format(mCalendar.getTime()));
        accionPreventivaToFix.setAccionHecha((chkSeValido.isChecked()) ? true : false);
        accionPreventivaToFix.setClosed(true);

        RestClient restClient = new RestClient(Services.URL_ROPS);
        Call<ResponseBody> call = restClient.iServices.setFixActions(accionPreventivaToFix, token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Mostramos dialog de éxito :
                    try {
                        // Intentaremos castear la respuesta :
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        //Log.d("TAG-ACTIONS-FIX ", jsonObject.toString());
                        // dialog.dismiss();
                        ///todo Actualizar accion prevntiva cerrado
                        updateAccion();
                        // Actualizar el id
                        // showDialogSuccess();

                        // Enviamos todas las imágenes :

                        RealmResults<ImagenRO> listaImagenes = ropRO.listaImgComent.where().equalTo("accionLevantada", true).findAll();

                        if (listaImagenes != null && listaImagenes.size() > 0) {
                            totalToSend = listaImagenes.size();
                            for (int i = 0; i < listaImagenes.size(); i++) {
                                ImagenRO image = realm.copyFromRealm(listaImagenes.get(i));
                                sendImage(image, ropRO.getTmpId(), ropRO.getId(), token);
                            }
                        } else {
                            dialog.dismiss();
                            showDialogSuccess();
                        }

                        dialog.dismiss();
                    } catch (Exception e) {
                        dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    dialog.dismiss();
                    Messages.showSB(getView(), getString(R.string.msg_error_levantar_accion_preventiva),
                            "ok");
                    //Log.e("TAG_OnResponse", response.errorBody() + " - " +
                    //       response.message() + "code :" + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialog.dismiss();
                t.printStackTrace();
            }
        });
    }

    /**
     * Módulo para enviar la imagen de incidencia
     */
    private void sendImage(ImagenRO imagenRO, String tmpId, int idRop, String api_token) {
        imagenRO.setDescripcion("Accion Levantada - " + imagenRO.getDescripcion());

        File myDir = getActivity().getApplicationContext().getFilesDir();
        File file = new File(myDir, Constants.PATH_IMAGE_GALERY_ROP + tmpId + "/"
                + imagenRO.getName());

        // Asignamos la imagen como Part
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file_image", file.getName(),
                requestFile);

        // Asignamos los campos como RequestBody :
        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"),
                imagenRO.getDescripcion());

        RequestBody rop_id =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), String.valueOf(idRop));

        RestClient restClient = new RestClient(Services.URL_ROPS);

        Call<ResponseBody> call = restClient.iServices.setImageRop(rop_id, description,
                body, api_token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    correctSend++;
                    Realm real = Realm.getInstance(myConfig);
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONObject messageResult = jsonObject.getJSONObject("message");
                        JSONObject ropImageResult = messageResult.getJSONObject("rop_image");


                        ImagenRO imagenRop = real.where(ImagenRO.class).equalTo("name", ropImageResult.getString("name")).findFirst();
                        if (imagenRop != null) {
                            if (!real.isInTransaction()) {
                                real.beginTransaction();
                            }
                            imagenRop.setId(ropImageResult.getInt("id"));
                            real.commitTransaction();
                        }
                        if (correctSend == totalToSend) {
                            dialog.dismiss();
                            // showDialogSuccess(getString(R.string.msg_success_send_inspection));
                            showDialogSuccess();
                        } else {
                            if (correctSend + failSend == totalToSend) {
                                dialog.dismiss();
                                //showDialogSuccess(getString(R.string.msg_success_send_inspe_error_image));
                                showDialogSuccess();
                            }
                        }

                    } catch (Exception e) {
                        real.close();
                        e.printStackTrace();
                        if (correctSend + failSend == totalToSend) {
                            dialog.dismiss();
                            showDialogSuccess("La accón preventiva ha sido dada como levantada. Sin embargo, algunas imágenes no han sido enviadas.");
                        }
                    } finally {
                        real.close();
                    }
                } else {
                    failSend++;
                    if (correctSend + failSend == totalToSend) {
                        dialog.dismiss();
                        showDialogSuccess("La accón preventiva ha sido dada como levantada . Sin embargo, algunas imágenes no han sido enviadas.");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                failSend++;
                if (correctSend + failSend == totalToSend) {
                    dialog.dismiss();
                    showDialogSuccess("La accón preventiva ha sido dada como levantada . Sin embargo, algunas imágenes no han sido enviadas.");
                }
            }
        });
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private void updateAccion() {
        Realm realm = Realm.getInstance(myConfig);
        try {
            if (!realm.isInTransaction()) {
                realm.beginTransaction();
            }
            //Log.e("accionclosed", "" + accionPreventivaRO.isClosed());


            AccionPreventiva accion = ropRO.listaAccionPreventiva.where().equalTo("id", accionPreventivaRO.getId()).findFirst();
            accion.setClosed(true);

            for (int i = 0; i < ropRO.listaAccionPreventiva.size(); i++) {
                if (ropRO.listaAccionPreventiva.get(i).getId() == accionPreventivaRO.getId()) {
                    ropRO.listaAccionPreventiva.get(i).setClosed(true);
                }

            }
            //Log.e("id accion levantada", "" + accionPreventivaRO.getId());
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            realm.close();
        } finally {
            realm.close();
        }
    }

    public void launchActivityGaleria() {
        Intent GaleriaIntent = new Intent().setClass(activity, ActivityGaleria.class);
        GaleriaIntent.putExtra("ROPtmpId", ropRO.getTmpId());
        GaleriaIntent.putExtra("esAccionLevantada", true);
        startActivity(GaleriaIntent);
    }


    public void Permissions() {

        ArrayList<String> especificacionPermisos = new ArrayList<>();

        int permissionCheckCamera = ContextCompat.checkSelfPermission(
                this.getActivity(), Manifest.permission.CAMERA);
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

    public void launchActivityFotoComentario(Uri uriImagen) {
        FotoModel fotoMd = new FotoModel();

        fotoMd.uri = uriImagen;
        //fotoMd.bitmap = null;

        Intent FotoComentarioIntent =
                new Intent().setClass(activity, ActivityFotoComentario.class);
        FotoComentarioIntent.putExtra("imagen", fotoMd);
        FotoComentarioIntent.putExtra("ROPtmpId", ropRO.getTmpId());
        FotoComentarioIntent.putExtra("esAccionLevantada", true);
        startActivity(FotoComentarioIntent);
    }

    public void MostrarCantidadImagenesAccionesLevantadas(RealmList<ImagenRO> listaImagenes) {
        int cant = listaImagenes.where().equalTo("accionLevantada", true).findAll().size();
        this.btnGaleriaFotos.setTitle(getString(R.string.label_fotos) + " (" + cant + ")");
    }

    private void showDialogConfirm() {
        dialogConfirm = new DialogRINSEG(activity);
        dialogConfirm.show();
        dialogConfirm.setTitle("Levantar Accion Preventiva");
        dialogConfirm.setBody("¿Está seguro de dar por levantada la acción preventiva? \n" +
                "Una vez finalizada se enviará hacia el servidor y no podrá modificarse.");
        dialogConfirm.setTextBtnAceptar("ACEPTAR");

        dialogConfirm.btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogConfirm.dismiss();
                sendData();

            }
        });
    }

    /**
     * Dialog de información de éxito
     */
    private void showDialogSuccess() {
        updateRopIfIsCloseAllActions();

        final DialogRINSEG dialogConfirm = new DialogRINSEG(activity);
        dialogConfirm.show();
        dialogConfirm.setTitle("ACCIÓN PREVENTIVA LEVANTADA");
        dialogConfirm.setBody("La accón preventiva ha sido dada como levantada los datos han sido enviados al servidor");
        dialogConfirm.setTextBtnAceptar("DE ACUERDO");
        dialogConfirm.btnCancelar.setVisibility(View.INVISIBLE);
        dialogConfirm.btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogConfirm.dismiss();
              /*  Fragment fragment = FragmentInspeccionDetalle2.newInstance(idInspeccion);
                activityMain.replaceFragment(fragment, true, 0, 0, 0, 0);*/
                Fragment fragment = FragmentROPsRegistradoDetalle.newInstance(ropRO.getId(), ropRO.getTmpId());
                activity.replaceFragment(fragment, true, 0, 0, 0, 0);
            }
        });
    }

    private void showDialogSuccess(String msg) {
        updateRopIfIsCloseAllActions();

        final DialogRINSEG dialogConfirm = new DialogRINSEG(activity);
        dialogConfirm.show();
        dialogConfirm.setTitle("ACCIÓN PREVENTIVA LEVANTADA");
        dialogConfirm.setBody(msg);
        dialogConfirm.setTextBtnAceptar("DE ACUERDO");
        dialogConfirm.btnCancelar.setVisibility(View.INVISIBLE);
        dialogConfirm.btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogConfirm.dismiss();
              /*  Fragment fragment = FragmentInspeccionDetalle2.newInstance(idInspeccion);
                activityMain.replaceFragment(fragment, true, 0, 0, 0, 0);*/
                Fragment fragment = FragmentROPsRegistradoDetalle.newInstance(ropRO.getId(), ropRO.getTmpId());
                activity.replaceFragment(fragment, true, 0, 0, 0, 0);
            }
        });
    }


    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private void updateRopIfIsCloseAllActions() {
        Realm realm = Realm.getInstance(myConfig);
        try {

            //Log.e("accionclosed", "" + accionPreventivaRO.isClosed());

            int totalAcciones = ropRO.listaAccionPreventiva.size();
            int totalAccionesLevantadas = 0;

            for (int i = 0; i < ropRO.listaAccionPreventiva.size(); i++) {
                if (ropRO.listaAccionPreventiva.get(i).isClosed()) {
                    totalAccionesLevantadas += 1;
                }
            }
            if(totalAcciones == totalAccionesLevantadas){
                if (!realm.isInTransaction()) {
                    realm.beginTransaction();
                }

                ropRO.setEstadoRop(2);
                realm.commitTransaction();
            }


        } catch (Exception e) {
            e.printStackTrace();
            realm.close();
        } finally {
            realm.close();
        }
    }

}
