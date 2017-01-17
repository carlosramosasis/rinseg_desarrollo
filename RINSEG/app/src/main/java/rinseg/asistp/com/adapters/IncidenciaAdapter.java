package rinseg.asistp.com.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import rinseg.asistp.com.listener.ListenerClick;
import rinseg.asistp.com.models.EventRO;
import rinseg.asistp.com.models.IncidenciaRO;
import rinseg.asistp.com.models.SettingsInspectionRO;
import rinseg.asistp.com.models.SettingsRopRO;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.utils.RinsegModule;

/**
 * Created by Usuario on 30/09/2016.
 */
public class IncidenciaAdapter extends RecyclerView.Adapter<IncidenciaAdapter.IncidenciaViewHolder> {
    private List<IncidenciaRO> ListaIncidencias;
    private final ListenerClick mListener;
    private RealmList<EventRO> mEvents = new RealmList<>();

    public static class IncidenciaViewHolder extends RecyclerView.ViewHolder {
        //Campos respectivos del item
        public TextView txtTipo;
        public TextView txtDesc;
        public final LinearLayout vLayout;


        public IncidenciaViewHolder(View v){
            super(v);

            txtTipo  = (TextView) v.findViewById(R.id.txt_card_view_incidencia_nombre);
            txtDesc  = (TextView) v.findViewById(R.id.txt_card_view_incidencia_desc);
            vLayout = (LinearLayout) v.findViewById(R.id.linearlayout_card_incidencia);
        }
    }

    public IncidenciaAdapter(List<IncidenciaRO> incidencias, Context context, ListenerClick listener){
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
        }
    }

    @Override
    public int getItemCount(){
        return ListaIncidencias.size();
    }

    @Override
    public IncidenciaViewHolder onCreateViewHolder(ViewGroup viewGroup,int i){
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cardview_incidencia,viewGroup,false);
        return new IncidenciaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final IncidenciaViewHolder viewHolder,final int i){

        int idEvent = ListaIncidencias.get(i).getEventId();
        for (int j = 0; j < mEvents.size(); j++) {
            EventRO evnt = mEvents.get(j);
            if (idEvent == evnt.getId()) {
                viewHolder.txtTipo.setText(evnt.getDisplayName());
            }
        }

        viewHolder.txtDesc.setText( ListaIncidencias.get(i).getDescripcion().toString() );

        viewHolder.vLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                mListener.onItemClicked(viewHolder, i);
            }
        });
    }

}
