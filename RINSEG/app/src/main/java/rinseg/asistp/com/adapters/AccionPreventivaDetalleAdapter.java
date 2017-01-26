package rinseg.asistp.com.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import rinseg.asistp.com.listener.ListenerClickAccionPreventiva;
import rinseg.asistp.com.models.AccionPreventiva;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.utils.Generic;

/**
 * Created by Carlos Ramos on 30/09/2016.
 */
public class AccionPreventivaDetalleAdapter extends RecyclerView.Adapter<AccionPreventivaDetalleAdapter.AccionViewHolder> {
    private List<AccionPreventiva> ListaAcciones;

    public static class AccionViewHolder extends RecyclerView.ViewHolder {
        //Campos respectivos del item
        public TextView txtResponsable;
        public TextView txtAccion;
        public TextView txtFecha;


        public AccionViewHolder(View v) {
            super(v);

            txtResponsable = (TextView) v.findViewById(R.id.txt_card_view_accion_responsable_detalle);
            txtAccion = (TextView) v.findViewById(R.id.txt_card_view_accion_accion_detalle);
            txtFecha = (TextView) v.findViewById(R.id.txt_card_view_accion_fecha_detalle);

        }
    }

    public AccionPreventivaDetalleAdapter(List<AccionPreventiva> accionesPreventivas ) {
        this.ListaAcciones = accionesPreventivas;
    }

    @Override
    public int getItemCount() {
        return ListaAcciones.size();
    }

    @Override
    public AccionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cardview_acciones_preventivas_detalle, viewGroup, false);
        return new AccionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AccionViewHolder viewHolder, final int i) {
        viewHolder.txtResponsable.setText(ListaAcciones.get(i).getResponsable());
        viewHolder.txtAccion.setText(ListaAcciones.get(i).getAccion());
        viewHolder.txtFecha.setText(Generic.dateFormatter.format(ListaAcciones.get(i).getFecha()));

    }


}
