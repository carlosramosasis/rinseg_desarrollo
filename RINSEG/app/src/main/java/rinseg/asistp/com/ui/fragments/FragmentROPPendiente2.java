package rinseg.asistp.com.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import rinseg.asistp.com.adapters.ActoCondicionSubestandarSeleccionadosAdapter;
import rinseg.asistp.com.listener.ListenerClickAccionPreventiva;
import rinseg.asistp.com.listener.ListenerClickActoCondicion;
import rinseg.asistp.com.models.AccionPreventiva;
import rinseg.asistp.com.models.AreaRO;
import rinseg.asistp.com.models.CompanyRO;
import rinseg.asistp.com.models.EventItemsRO;
import rinseg.asistp.com.models.EventRO;
import rinseg.asistp.com.models.FotoModel;
import rinseg.asistp.com.models.Inspeccion;
import rinseg.asistp.com.models.ROP;
import rinseg.asistp.com.models.RiskRO;
import rinseg.asistp.com.models.TargetRO;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.ui.activities.ActivityFotoComentario;
import rinseg.asistp.com.ui.activities.ActivityGaleria;
import rinseg.asistp.com.ui.activities.ActivityInspeccionDetalle;
import rinseg.asistp.com.ui.activities.ActivityMain;
import rinseg.asistp.com.adapters.AccionPreventivaAdapter;
import rinseg.asistp.com.utils.Constants;
import rinseg.asistp.com.utils.DialogLoading;
import rinseg.asistp.com.utils.DialogRINSEG;
import rinseg.asistp.com.utils.Generic;
import rinseg.asistp.com.utils.Messages;
import rinseg.asistp.com.utils.RinsegModule;


public class FragmentROPPendiente2 extends Fragment
        implements ListenerClickAccionPreventiva, ListenerClickActoCondicion {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    static Uri capturedImageUri = null;

    ActivityMain activityMain;
    ImageButton btnActoCondicion;
    EditText mEditResponsable;
    EditText mEditAccion;
    TextView txtFecha;
    TextView textActoCondicion;
    ImageButton btnCalendar;
    Button btnAgregar;
    ImageButton btnDelete;
    Calendar newCalendar;

    private List<EventItemsRO> listaActoCondicion = new ArrayList<>();
    RecyclerView recyclerActoCondicion;
    private RecyclerView.LayoutManager lManagerActoCondicion;
    private RecyclerView.Adapter actoCondicionAdapter;

    private List<AccionPreventiva> listaAccionPreventiva = new ArrayList<>();
    RecyclerView recyclerAcciones;
    private RecyclerView.LayoutManager lManager;
    private RecyclerView.Adapter accionAdapter;

    ROP mRop;

    RealmConfiguration myConfig;

    private DialogLoading dialogLoading;

    public static int count = 0;

    private String nameEvent;

    Bundle bundle;

    //import foto
    public int PICK_IMAGE_REQUEST = 1;
    //tomar foto
    public int REQUEST_IMAGE_CAPTURE = 1;

    public FragmentROPPendiente2() { }

    public static FragmentROPPendiente2 newInstance(String param1, String param2) {
        FragmentROPPendiente2 fragment = new FragmentROPPendiente2();
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
        View view = inflater.inflate(R.layout.fragment_rop_pendiente2, container, false);

        setUpElements(view);
        setUpActions();
        Permissions();
        LoadRopPendiente();

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        activityMain.toolbar.setTitle(R.string.title_rop);
        activityMain.ShowButtonsBottom(true);
        activityMain.ShowNumPagina();
        activityMain.MostrarCantidadImagenesRop(mRop.listaImgComent);
        activityMain.btnFabMenu.setVisibility(View.VISIBLE);
        activityMain.btnFabMenu.collapseImmediately();
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
    public void onDestroyView() {
        activityMain.HideNumPagina();
        activityMain.btnFabMenu.setVisibility(View.GONE);
        super.onDestroyView();
    }

    /**
     * Evento para eliminar acción preventiva agregada
     */
    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    @Override
    public void onItemClicked(AccionPreventivaAdapter.AccionViewHolder holder, int position) {

        Realm realm = Realm.getInstance(myConfig);

        try {
            realm.beginTransaction();
            mRop.listaAccionPreventiva.get(position).deleteFromRealm();
            realm.commitTransaction();
            listaAccionPreventiva.remove(position);
            accionAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            realm.close();
        }
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


    //Proceso para cargar las vistas
    private void setUpElements(View v) {
        bundle = getArguments();

        activityMain = ((ActivityMain) getActivity());

        newCalendar = Calendar.getInstance();

        btnActoCondicion = (ImageButton) v.findViewById(R.id.btn_rop2_acto_condicion_subestandar);

        mEditResponsable = (EditText) v.findViewById(R.id.txt_rop2_responsable);
        mEditAccion = (EditText) v.findViewById(R.id.txt_rop2_accion);
        txtFecha = (TextView) v.findViewById(R.id.txt_rop2_fecha);
        textActoCondicion = (TextView) v.findViewById(R.id.txt_rop2_acto_condicion_subestandar);
        btnCalendar = (ImageButton) v.findViewById(R.id.btn_rop2_calendar);

        btnAgregar = (Button) v.findViewById(R.id.btn_rop2_agregar);
        btnDelete = (ImageButton) v.findViewById(R.id.btn_card_view_delete);

        //configuracion para el recicler Acciones preventivas
        recyclerActoCondicion= (RecyclerView) v.findViewById(R.id.recycler_rop2_acto_condicion);
        recyclerActoCondicion.setHasFixedSize(true);
        // usar administrador para linearLayout
        lManager = new LinearLayoutManager(this.getActivity().getApplicationContext());
        recyclerActoCondicion.setLayoutManager(lManagerActoCondicion);
        // Crear un nuevo Adaptador
        actoCondicionAdapter = new ActoCondicionSubestandarSeleccionadosAdapter(listaActoCondicion);
        recyclerActoCondicion.setAdapter(actoCondicionAdapter);

        //configuracion para el recicler Acciones preventivas
        recyclerAcciones = (RecyclerView) v.findViewById(R.id.recycler_rop2_acciones);
        recyclerAcciones.setHasFixedSize(true);
        // usar administrador para linearLayout
        lManager = new LinearLayoutManager(this.getActivity().getApplicationContext());
        recyclerAcciones.setLayoutManager(lManager);
        // Crear un nuevo Adaptador
        accionAdapter = new AccionPreventivaAdapter(listaAccionPreventiva, this);
        recyclerAcciones.setAdapter(accionAdapter);

        dialogLoading = new DialogLoading(getActivity());

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
        btnActoCondicion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MostrarAccionCondicionSubestandar();
            }
        });
        textActoCondicion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MostrarAccionCondicionSubestandar();
            }
        });


        txtFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDatepicker();
            }
        });

        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDatepicker();
            }
        });

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AgregarAccion();
            }
        });

        activityMain.btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dialogLoading.show();
                    //SaveRop();
                    activityMain.actualPaginaRop -= 1;
                    Fragment fRopPendiente1 = new FragmentROPPendiente1();
                    Bundle args = new Bundle();
                    args.putString("ROPtmpId", mRop.getTmpId());
                    fRopPendiente1.setArguments(args);
                    activityMain.replaceFragment(fRopPendiente1, true,
                            R.anim.enter_from_right, R.anim.exit_to_right,
                            R.anim.enter_from_left, R.anim.exit_to_left);
                    dialogLoading.hide();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        activityMain.btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRop.listaEventItems.size() == 0) {
                    Messages.showSB(v, getString(R.string.msg_lista_acto_subestandar), "ok");
                    return;
                }

                if (!listaAccionesValida()) {
                    Validarformulario();
                    Messages.showSB(v, getString(R.string.msg_lista_acciones), "ok");
                    return;
                }

                activityMain.actualPaginaRop += 1;
                Fragment fRopPendiente3 = new FragmentROPPendiente3();
                Bundle args = new Bundle();
                args.putString("ROPtmpId", mRop.getTmpId());
                fRopPendiente3.setArguments(args);
                activityMain.replaceFragment(fRopPendiente3, true,
                        R.anim.enter_from_left, R.anim.exit_to_left,
                        R.anim.enter_from_right, R.anim.exit_to_right);
            }
        });

        activityMain.btnGaleriaFotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivityGaleria();
            }
        });

        activityMain.btnImportarFotos.setOnClickListener(new View.OnClickListener() {
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

        activityMain.btnTomarFoto.setOnClickListener(new View.OnClickListener() {
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

    }

    private boolean Validarformulario() {
        boolean resu = true;
        if (mEditAccion.getText().length() == 0) {
            mEditAccion.setError(getString(R.string.error_accion_rp2));
            resu = false;
        }

        if (mEditResponsable.getText().length() == 0) {
            mEditResponsable.setError(getString(R.string.error_resp_rp2));
            resu = false;
        }

        if (txtFecha.getText().length() == 0) {
            txtFecha.setError(getString(R.string.error_fecha_rp2));
            resu = false;
        }
        return resu;
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private void AgregarAccion() {

        if (!Validarformulario()) {
            return;
        }

        Realm realm = Realm.getInstance(myConfig);
        try {
            realm.beginTransaction();
            AccionPreventiva mAccion = realm.createObject(AccionPreventiva.class);
            mAccion.setResponsable(mEditResponsable.getText().toString().trim());
            mAccion.setAccion(mEditAccion.getText().toString().trim());
            mAccion.setFecha(newCalendar.getTime());
            mAccion.setFechaString();

            mRop.listaAccionPreventiva.add(mAccion);
            realm.commitTransaction();

            listaAccionPreventiva.add(0, mAccion);
            accionAdapter.notifyDataSetChanged();
            mEditResponsable.setText("");
            mEditAccion.setText("");
            txtFecha.setText("");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            realm.close();
        }
    }

    public void ShowDatepicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        txtFecha.setText(Generic.dateFormatter.format(newDate.getTime()));
                        txtFecha.setError(null);
                    }
                }, newCalendar.get(Calendar.YEAR),
                newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void CargarActoCondicionSubestandarSeleccionados(){
        
    }


    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private void LoadRopPendiente() {
        if (bundle != null) {
            String tmpIdRop = bundle.getString("ROPtmpId", null);
            nameEvent = bundle.getString("nameEvent", null);

            //ROP tmpRop = new ROP();
            if (tmpIdRop != null) {
                final Realm realm = Realm.getInstance(myConfig);
                try {
                    mRop = realm.where(ROP.class).equalTo("tmpId", tmpIdRop).findFirst();
                    if (mRop == null) {
                        return;
                    }

                    // recuperar accion preventiva
                    for (int i = 0; i < mRop.listaAccionPreventiva.size(); i++) {
                        AccionPreventiva ap = mRop.listaAccionPreventiva.get(i);
                        listaAccionPreventiva.add(ap);
                        accionAdapter.notifyDataSetChanged();
                    }

                    // Recuperando la lista de actos o condiciones :
                    int cantEventItems = mRop.listaEventItems.size();
                    if (cantEventItems > 0) {
                        if (cantEventItems == 1) {
                            textActoCondicion.setText(cantEventItems + " " + getString(R.string.acto_condicion_sub_elemento));
                        } else if (cantEventItems > 1) {
                            textActoCondicion.setText(cantEventItems + " " + getString(R.string.acto_condicion_sub_elementos));
                        }

                    } else {
                        textActoCondicion.setText("");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    realm.close();
                }
            }
        }
    }

    public void launchActivityFotoComentario(Uri uriImagen) {
        FotoModel fotoMd = new FotoModel();

        fotoMd.uri = uriImagen;
        //fotoMd.bitmap = null;

        Intent FotoComentarioIntent =
                new Intent().setClass(activityMain, ActivityFotoComentario.class);
        FotoComentarioIntent.putExtra("imagen", fotoMd);
        FotoComentarioIntent.putExtra("ROPtmpId", mRop.getTmpId());
        startActivity(FotoComentarioIntent);
    }

    public void launchActivityGaleria() {

        Intent GaleriaIntent = new Intent().setClass(activityMain, ActivityGaleria.class);
        GaleriaIntent.putExtra("ROPtmpId", mRop.getTmpId());
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

    private boolean listaAccionesValida() {
        return listaAccionPreventiva.size() > 0;
    }

    private void MostrarAccionCondicionSubestandar() {
        final DialogActoCondicionSubestandar dialogActoCondicion =
                new DialogActoCondicionSubestandar(getActivity(), nameEvent, mRop.getTmpId(), this);
        dialogActoCondicion.show();
    }

    /**
     * Implementación de método para setear texto al campo de acto o condición sub estándar
     */
    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    @Override
    public void onAcceptItems() {
        final Realm realm = Realm.getInstance(myConfig);
        try {
            mRop = realm.where(ROP.class).equalTo("tmpId", mRop.getTmpId()).findFirst();
            if (mRop != null) {
                int cantEventItems = mRop.listaEventItems.size();
                if (cantEventItems > 0) {

                    if (cantEventItems == 1) {
                        textActoCondicion.setText(cantEventItems + " " + getString(R.string.acto_condicion_sub_elemento));
                    } else if (cantEventItems > 1) {
                        textActoCondicion.setText(cantEventItems + " " + getString(R.string.acto_condicion_sub_elementos));
                    }
                } else {
                    textActoCondicion.setText("");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            realm.close();
        }
    }
}