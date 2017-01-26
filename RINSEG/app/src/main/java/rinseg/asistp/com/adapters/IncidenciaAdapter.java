package rinseg.asistp.com.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import rinseg.asistp.com.listener.ListenerClick;
import rinseg.asistp.com.models.EventRO;
import rinseg.asistp.com.models.FrecuencieRO;
import rinseg.asistp.com.models.IncidenciaRO;
import rinseg.asistp.com.models.RiskRO;
import rinseg.asistp.com.models.SettingsInspectionRO;
import rinseg.asistp.com.models.SettingsRopRO;
import rinseg.asistp.com.models.SeveritiesRO;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.utils.RinsegModule;

/**
 * Created by Usuario on 30/09/2016.
 * Adapter para el recycler de incidentes
 */

public class IncidenciaAdapter extends RecyclerView.Adapter<IncidenciaAdapter.IncidenciaViewHolder> {

    private List<IncidenciaRO> ListaIncidencias;
    private final ListenerClick mListener;
    private RealmList<EventRO> mEvents = new RealmList<>();
    private RealmList<RiskRO> mRisks = new RealmList<>();
    private RealmList<FrecuencieRO> mFrecuencies = new RealmList<>();
    private RealmList<SeveritiesRO> mSeverities = new RealmList<>();

    public static class IncidenciaViewHolder extends RecyclerView.ViewHolder {
        //Campos respectivos del item
        public TextView txtTipo;
        public TextView txtDesc;
        public ImageView imgRiesgo;
        public final LinearLayout vLayout;


        public IncidenciaViewHolder(View v) {
            super(v);

            txtTipo = (TextView) v.findViewById(R.id.txt_card_view_incidencia_nombre);
            txtDesc = (TextView) v.findViewById(R.id.txt_card_view_incidencia_desc);
            imgRiesgo = (ImageView) v.findViewById(R.id.img_card_view_incidencia_riesgo);
            vLayout = (LinearLayout) v.findViewById(R.id.linearlayout_card_incidencia);
        }
    }

    public IncidenciaAdapter(List<IncidenciaRO> incidencias, Context context, ListenerClick listener) {
        this.ListaIncidencias = incidencias;
        mListener = listener;

        Realm.init(context);
        RealmConfiguration myConfig = new RealmConfiguration.Builder()
                .name("rinseg.realm")
                .schemaVersion(2)
                .modules(new RinsegModule())
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm realm = Realm.getInstance(myConfig);
        SettingsInspectionRO settingsInsp = realm.where(SettingsInspectionRO.class).findFirst();
        if (settingsInsp != null) {
            this.mEvents = settingsInsp.events;
            this.mRisks = settingsInsp.risks;
            this.mFrecuencies = settingsInsp.frecuencies;
            this.mSeverities = settingsInsp.severities;
        }
    }

    @Override
    public int getItemCount() {
        return ListaIncidencias.size();
    }

    @Override
    public IncidenciaViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cardview_incidencia, viewGroup, false);
        return new IncidenciaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final IncidenciaViewHolder viewHolder, final int i) {

        int idEvent = ListaIncidencias.get(i).getEventId();
        for (int j = 0; j < mEvents.size(); j++) {
            EventRO evnt = mEvents.get(j);
            if (idEvent == evnt.getId()) {
                viewHolder.txtTipo.setText(evnt.getDisplayName());
                break;
            }
        }

        viewHolder.txtDesc.setText(ListaIncidencias.get(i).getDescripcion());

        FrecuencieRO frecuencia = new FrecuencieRO();
        int idFrec = ListaIncidencias.get(i).getFrecuenciaId();
        for (int j = 0; j < mFrecuencies.size(); j++) {
            FrecuencieRO frcuenciaTmp = mFrecuencies.get(j);
            if (idFrec == frcuenciaTmp.getId()) {
                frecuencia = frcuenciaTmp;
                break;
            }
        }

        SeveritiesRO severidad = new SeveritiesRO();
        int idSev = ListaIncidencias.get(i).getSeveridadId();
        for (int j = 0; j < mSeverities.size(); j++) {
            SeveritiesRO severidadTmp = mSeverities.get(j);
            if (idSev == severidadTmp.getId()) {
                severidad = severidadTmp;
                break;
            }
        }

        int resultado = severidad.getValue() * frecuencia.getValue();

        for (int j = 0; j < mRisks.size(); j++) {
            RiskRO riesgo = mRisks.get(j);
            if (resultado >= riesgo.getMinValue()
                    && resultado <= riesgo.getMaxValue()
                    && resultado != 0) {
                if (j == 1) {
                    viewHolder.imgRiesgo.setBackgroundResource(
                            R.drawable.backgorund_circle_gradient_green);
                }
                else if(j  == 2){
                    viewHolder.imgRiesgo.setBackgroundResource(
                            R.drawable.backgorund_circle_gradient_amarillo);
                }
                else if(j== 3){
                    viewHolder.imgRiesgo.setBackgroundResource(
                            R.drawable.backgorund_circle_gradient_naranja);
                }
                else if(j  == 4){
                    viewHolder.imgRiesgo.setBackgroundResource(
                            R.drawable.backgorund_circle_gradient);
                }
                break;
            }
        }

        viewHolder.vLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClicked(viewHolder, i);
            }
        });
    }

}
