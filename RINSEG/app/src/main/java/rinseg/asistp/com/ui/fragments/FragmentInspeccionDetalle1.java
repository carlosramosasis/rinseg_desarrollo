package rinseg.asistp.com.ui.fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rinseg.asistp.com.models.CompanyRO;
import rinseg.asistp.com.models.InspeccionRO;
import rinseg.asistp.com.models.InspectorRO;
import rinseg.asistp.com.models.TypesRO;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.services.RestClient;
import rinseg.asistp.com.services.Services;
import rinseg.asistp.com.ui.activities.ActivityInspeccionDetalle;
import rinseg.asistp.com.utils.DialogLoading;
import rinseg.asistp.com.utils.Messages;
import rinseg.asistp.com.utils.RinsegModule;
import rinseg.asistp.com.utils.SharedPreferencesHelper;

import static rinseg.asistp.com.utils.Constants.MY_SHARED_PREFERENCES;

public class FragmentInspeccionDetalle1 extends Fragment {

    private static final String ARG_PARAM1 = "InspId";
    private static final String ARG_PARAM2 = "InspTmpId";

    private InspeccionRO inspeccion;

    private OnFragmentInteractionListener mListener;

    int idInspeccion;
    String idInspeccionTem;

    ActivityInspeccionDetalle activityMain;
    private TextView textArea, textFecha, textTipo, textEmpresa, textInspectores, textResponsables;
    private CoordinatorLayout coordinatorLayout;

    private FloatingActionButton btnVerIncidentes, btnGenerarPDF;
    private FloatingActionsMenu btnMenu;

    RealmConfiguration myConfig;

    public FragmentInspeccionDetalle1() { }

    public static FragmentInspeccionDetalle1 newInstance(int id, String idTemporal) {
        FragmentInspeccionDetalle1 fragment = new FragmentInspeccionDetalle1();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, id);
        args.putString(ARG_PARAM2, idTemporal);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idInspeccion = getArguments().getInt(ARG_PARAM1);
            idInspeccionTem = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inspeccion_detalle1, container, false);

        setUpElements(view);
        setUpData();
        setUpActions();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        activityMain.toolbarInspeccionDet.setTitle(R.string.title_incident_detail);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if ( mListener != null ) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    //Proceso para cargar las vistas
    private void setUpElements(View v) {
        activityMain = ((ActivityInspeccionDetalle) getActivity());

        textArea = (TextView) v.findViewById(R.id.text_inspection_detail_area);
        textFecha = (TextView) v.findViewById(R.id.text_inspection_detail_fecha);
        textTipo = (TextView) v.findViewById(R.id.text_inspection_detail_tipo);
        textEmpresa = (TextView) v.findViewById(R.id.text_inspection_detail_empresa);
        textInspectores = (TextView) v.findViewById(R.id.text_inspection_detail_inspectores);
        textResponsables = (TextView) v.findViewById(R.id.text_inspection_detail_responsables);

        btnVerIncidentes = (FloatingActionButton) v.findViewById(R.id.btn_ver_incidente);
        btnGenerarPDF = (FloatingActionButton)
                v.findViewById(R.id.btn_inspection_detail_generate_pdf);
        btnMenu = (FloatingActionsMenu) v.findViewById(R.id.fab_menu_incidencias);
        coordinatorLayout = (CoordinatorLayout) v.findViewById(R.id.coordinator_inspection_detail);

        // Configuramos Realm :
        Realm.init(this.getActivity().getApplicationContext());
        myConfig = new RealmConfiguration.Builder()
                .name("rinseg.realm")
                .schemaVersion(2)
                .modules(new RinsegModule())
                .deleteRealmIfMigrationNeeded()
                .build();
    }

    /** Escuchadores de eventos */
    private void setUpActions() {
        btnVerIncidentes.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Fragment fragment = FragmentInspeccionDetalle2.newInstance(inspeccion.getId());
                activityMain.replaceFragment(fragment, true, 0, 0, 0, 0);
            }
        });

        activityMain.toolbarInspeccionDet.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityMain.finish();
            }
        });

        btnGenerarPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtenemos el token :
                SharedPreferencesHelper preferencesHelper = new SharedPreferencesHelper(
                        activityMain.getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE));
                String token = preferencesHelper.getToken();

                final DialogLoading loading = new DialogLoading(activityMain);
                loading.show();

                RestClient restClient = new RestClient(Services.INSPECTION);
                Call<ResponseBody> call = restClient.iServices.downloadInspecPDF(
                        idInspeccion, token);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if ( response.isSuccessful() ) {
                            try {
                                final File file = Environment.getExternalStoragePublicDirectory(
                                        Environment.DIRECTORY_DOWNLOADS);
                                final String nameFile = "Inspeccion_" + String.valueOf(idInspeccion)
                                        + ".pdf";
                                String path = file.getPath() + "/" + nameFile;
                                OutputStream out = new FileOutputStream(path);
                                out.write(response.body().bytes());
                                out.close();

                                loading.dismiss();
                                btnMenu.collapse();

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


    /** Módulo para cargar los datos */
    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private void setUpData() {
        if ( idInspeccionTem != null ) {
            final Realm realm = Realm.getInstance(myConfig);
            try {
                inspeccion = realm.where(InspeccionRO.class).equalTo("id", idInspeccion).findFirst();
                // Seteamos los datos en la vista :
                textArea.setText(inspeccion.getArea());
                textFecha.setText(inspeccion.getDateString());

                // Recuperamos el tipo de inspección por código :
                TypesRO type = realm.where(TypesRO.class)
                        .equalTo("id", inspeccion.getTypeInspectionId()).findFirst();
                if ( type != null ) {
                    textTipo.setText(type.getDisplayName());
                }

                // Recuperamos la compañía por código :
                CompanyRO company = realm.where(CompanyRO.class)
                        .equalTo("id", inspeccion.getCompanyId()).findFirst();
                if ( company != null ) {
                    textEmpresa.setText(company.getDisplayName());
                }

                // Concatenamos datos de inspectores :
                String inspectores = "";
                for (InspectorRO i : inspeccion.listaInspectores ) {
                    inspectores = inspectores + i.getName() + "\n";
                }
                textInspectores.setText(inspectores);

                // Concatenamos datos de responsables :
                String responsables = "";
                for (InspectorRO i : inspeccion.listaResponsables ) {
                    responsables = responsables + i.getName() + "\n";
                }
                textResponsables.setText(responsables);
            } catch ( Exception e ) {
                e.printStackTrace();
            } finally {
                realm.close();
            }
        }
    }
}
