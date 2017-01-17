package rinseg.asistp.com.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import rinseg.asistp.com.listener.ListenerClickAccionPreventiva;
import rinseg.asistp.com.listener.ListenerClickInspector;
import rinseg.asistp.com.models.AccionPreventiva;
import rinseg.asistp.com.models.CompanyRO;
import rinseg.asistp.com.models.InspectorRO;
import rinseg.asistp.com.models.ManagementRO;
import rinseg.asistp.com.models.SettingsInspectionRO;
import rinseg.asistp.com.models.SettingsRopRO;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.utils.Generic;
import rinseg.asistp.com.utils.RinsegModule;

/**
 * Created by Carlos Ramos on 30/09/2016.
 */
public class InspectorAdapter extends RecyclerView.Adapter<InspectorAdapter.InspectorViewHolder> {
    private List<InspectorRO> ListaInspectores;
    private final ListenerClickInspector mListener;
    private RealmList<ManagementRO> mGerencia = new RealmList<>();


    public static class InspectorViewHolder extends RecyclerView.ViewHolder {
        //Campos respectivos del item
        public TextView txtDNI;
        public TextView txtNombre;
        public TextView txtGerencia;
        public final ImageButton btnEliminar;

        public InspectorViewHolder(View v) {
            super(v);

            txtDNI = (TextView) v.findViewById(R.id.txt_card_view_dni);
            txtNombre = (TextView) v.findViewById(R.id.txt_card_view_inspector);
            txtGerencia = (TextView) v.findViewById(R.id.txt_card_view_gerencia);

            btnEliminar = (ImageButton) v.findViewById(R.id.btn_card_view_inspector_delete);

        }
    }

    public InspectorAdapter(Context context, List<InspectorRO> inspectores, ListenerClickInspector listener) {
        this.ListaInspectores = inspectores;
        this.mListener = listener;

        Realm.init(context);
        RealmConfiguration myConfig = new RealmConfiguration.Builder()
                .name("rinseg.realm")
                .schemaVersion(2)
                .modules(new RinsegModule())
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm realm = Realm.getInstance(myConfig);
        SettingsInspectionRO settingsInspectionRO = realm.where(SettingsInspectionRO.class).findFirst();
        if (settingsInspectionRO != null) {
            this.mGerencia = settingsInspectionRO.managements;
        }

    }

    @Override
    public int getItemCount() {
        return ListaInspectores.size();
    }

    @Override
    public InspectorViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cardview_inspectores, viewGroup, false);
        return new InspectorViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final InspectorViewHolder viewHolder, final int i) {
        viewHolder.txtDNI.setText(ListaInspectores.get(i).getDni());
        viewHolder.txtNombre.setText(ListaInspectores.get(i).getName());

        int idGerencia = ListaInspectores.get(i).getManagementId();

        for (int j = 0; j < mGerencia.size(); j++) {
            ManagementRO ger = mGerencia.get(j);
            if (idGerencia == ger.getId()) {
                viewHolder.txtGerencia.setText(ger.getDisplayName());
            }
        }

        viewHolder.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onDeleteClicked(viewHolder, i);
            }
        });

    }

}
