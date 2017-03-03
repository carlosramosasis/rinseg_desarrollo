package rinseg.asistp.com.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import rinseg.asistp.com.listener.ListenerClickAccionPreventivaDetalle;
import rinseg.asistp.com.models.AccionPreventiva;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.utils.Generic;

/**
 * Created by OSequeiros on 14/02/2017.
 * Adaptador encargado de administrar la lista de acciones preventivas ()
 */

public class AccionPrevEstadoAdapter
        extends RecyclerView.Adapter<AccionPrevEstadoAdapter.AccionViewHolder> {

    private List<AccionPreventiva> list;
    private Context context;
    private ListenerClickAccionPreventivaDetalle mListener;

    public static class AccionViewHolder extends RecyclerView.ViewHolder {

        TextView txtEstado;
        TextView txtResponsable;
        TextView txtDescripciion;
        TextView txtFechaLimite;

        LinearLayout linearRoot;

        AccionViewHolder(View v) {
            super(v);
            txtEstado = (TextView) v.findViewById(R.id.text_card_acciones_status);
            txtResponsable = (TextView) v.findViewById(R.id.text_card_acciones_responsible);
            txtDescripciion = (TextView) v.findViewById(R.id.text_card_acciones_description);
            txtFechaLimite = (TextView) v.findViewById(R.id.text_card_acciones_deadline);
            linearRoot =  (LinearLayout) v.findViewById(R.id.linearlayout_card_acciones_registradas);
        }
    }

    public AccionPrevEstadoAdapter(List<AccionPreventiva> list, Context context,ListenerClickAccionPreventivaDetalle listener) {
        this.list = list;
        this.context = context;
        this.mListener =  listener;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public AccionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cardview_rop_registrado_acciones, viewGroup, false);
        return new AccionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AccionViewHolder v, final int i) {
        if ( list.get(i).isClosed() ) {
            v.txtEstado.setText(context.getString(R.string.card_prev_actions_realizado));
            v.txtEstado.setTextColor(ContextCompat.getColor(context, R.color.colorGreen));
        } else {
            v.txtEstado.setText(context.getString(R.string.card_prev_actions_pendiente));
            v.txtEstado.setTextColor(ContextCompat.getColor(context, R.color.colorAccentDark));
        }
        String responsable = v.txtResponsable.getText() + list.get(i).getResponsable();
        v.txtResponsable.setText(responsable);
        v.txtDescripciion.setText(list.get(i).getAccion());
        String fechaLimite = v.txtFechaLimite.getText() + list.get(i).getFechaString();
        v.txtFechaLimite.setText(Generic.dateFormatter.format(list.get(i).getFecha()));

        v.linearRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClicked(v, i);
            }
        });

    }
}