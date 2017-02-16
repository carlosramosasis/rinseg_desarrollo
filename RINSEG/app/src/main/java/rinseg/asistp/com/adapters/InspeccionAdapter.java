package rinseg.asistp.com.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import rinseg.asistp.com.listener.ListenerClick;
import rinseg.asistp.com.models.AreaRO;
import rinseg.asistp.com.models.EventRO;
import rinseg.asistp.com.models.IncidenciaRO;
import rinseg.asistp.com.models.Inspeccion;
import rinseg.asistp.com.models.InspeccionRO;
import rinseg.asistp.com.models.ManagementRO;
import rinseg.asistp.com.models.RiskRO;
import rinseg.asistp.com.models.SettingsInspectionRO;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.ui.fragments.FragmentInspeccionIncidenciaDetalle1;
import rinseg.asistp.com.utils.Generic;
import rinseg.asistp.com.utils.RinsegModule;

/**
 * Created by Carlos Ramos on 30/09/2016.
 */
public class InspeccionAdapter extends RecyclerView.Adapter<InspeccionAdapter.InspeccionViewHolder> {
    private List<InspeccionRO> ListaInspecciones;
    private final ListenerClick mListener;
    private RealmList<RiskRO> mRisks = new RealmList<>();

    public static class InspeccionViewHolder extends RecyclerView.ViewHolder {
        //Campos respectivos del item
        public TextView txtCodigo, txtArea, txtFechaCrea, txtValorVerde, txtValorAmarillo, txtValorNaranja,
                txtValorRojo, txtValorAbiertos, txtValorCerrados, txtValorTotal;

        public final LinearLayout vLayout;

        public InspeccionViewHolder(View v) {
            super(v);

            txtCodigo = (TextView) v.findViewById(R.id.txt_card_view_cod_inspeccion);
            txtArea = (TextView) v.findViewById(R.id.txt_card_view_are_inspeccion);
            txtFechaCrea = (TextView) v.findViewById(R.id.txt_card_view_fec_crea_inspeccion);

            txtValorVerde = (TextView) v.findViewById(R.id.txt_card_view_valor_verde);
            txtValorAmarillo = (TextView) v.findViewById(R.id.txt_card_view_valor_amarillo);
            txtValorNaranja = (TextView) v.findViewById(R.id.txt_card_view_valor_naranja);
            txtValorRojo = (TextView) v.findViewById(R.id.txt_card_view_valor_rojo);

            txtValorAbiertos = (TextView) v.findViewById(R.id.txt_card_view_valor_abiertos);
            txtValorCerrados = (TextView) v.findViewById(R.id.txt_card_view_valor_cerrados);
            txtValorTotal = (TextView) v.findViewById(R.id.txt_card_view_valor_total);


            vLayout = (LinearLayout) v.findViewById(R.id.linearlayout_card_inspecciones);

        }
    }

    public InspeccionAdapter(Context context, List<InspeccionRO> inspecciones, ListenerClick listener) {
        this.ListaInspecciones = inspecciones;
        mListener = listener;

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
            this.mRisks = settingsInspectionRO.risks;
        }

    }

    @Override
    public int getItemCount() {
        return ListaInspecciones.size();
    }

    @Override
    public InspeccionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cardview_inspeccion, viewGroup, false);
        return new InspeccionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final InspeccionViewHolder viewHolder, final int i) {

        InspeccionRO insp = ListaInspecciones.get(i);
        if (insp.getId() != 0) {
            viewHolder.txtCodigo.setText("" + insp.getId());
        } else {
            viewHolder.txtCodigo.setText(insp.getTmpId());
        }


        viewHolder.txtArea.setText(insp.getArea());

        viewHolder.txtFechaCrea.setText(Generic.dateFormatter.format(insp.getDate()));

        int verde_c = 0;
        int verde_t = 0;
        int amarillo_c = 0;
        int amarillo_t = 0;
        int naranja_c = 0;
        int naranja_t = 0;
        int rojo_c = 0;
        int rojo_t = 0;
        int suma_a = 0;
        int suma_c = 0;
        int total = 0;

        for (int j = 0; j < insp.listaIncidencias.size(); j++) {
            IncidenciaRO incidencia = insp.listaIncidencias.get(j);
            if (incidencia.isClosed()) {
                suma_c += 1;
            } else {
                suma_a += 1;
            }
            total += 1;

            for (int y = 0; y < mRisks.size(); y++) {
                RiskRO riesgo = mRisks.get(y);
                if (incidencia.getRiesgo() >= riesgo.getMinValue() && incidencia.getRiesgo() <= riesgo.getMaxValue()) {
                    switch (riesgo.getDisplayName()) {
                        case "Bajo":
                            verde_t += 1;
                            if (incidencia.isClosed()) {
                                verde_c += 1;
                            }
                            break;
                        case "Medio":
                            amarillo_t += 1;
                            if (incidencia.isClosed()) {
                                amarillo_c += 1;
                            }
                            break;
                        case "Alto":
                            naranja_t += 1;
                            if (incidencia.isClosed()) {
                                naranja_c += 1;
                            }
                            break;
                        case "Muy Alto":
                            rojo_t += 1;
                            if (incidencia.isClosed()) {
                                rojo_c += 1;
                            }
                            break;
                    }

                }
            }

        }

        viewHolder.txtValorVerde.setText(verde_c + "/" + verde_t);
        viewHolder.txtValorAmarillo.setText(amarillo_c + "/" + amarillo_t);
        viewHolder.txtValorNaranja.setText(naranja_c + "/" + naranja_t);
        viewHolder.txtValorRojo.setText(rojo_c + "/" + rojo_t);

        viewHolder.txtValorAbiertos.setText(String.valueOf(suma_a));
        viewHolder.txtValorCerrados.setText(String.valueOf(suma_c));

        viewHolder.txtValorTotal.setText(String.valueOf(total));

        viewHolder.vLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClicked(viewHolder, i);
            }
        });

    }

}
