package rinseg.asistp.com.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import rinseg.asistp.com.listener.ListenerClickActoCondicion;
import rinseg.asistp.com.models.EventItemsRO;
import rinseg.asistp.com.rinseg.R;

/**
 * Created by Carlos Ramos on 30/09/2016.
 * Adaptador del Recycler del Dialog Acción o Condición
 */

public class AccionCondicionSubestandarAdapter
        extends RecyclerView.Adapter<AccionCondicionSubestandarAdapter.AccionViewHolder> {

    public List<EventItemsRO> ListaActosCondiciones;
    public List<Boolean> listCheckedActos;

    private int selectedPosition = -1;

    public static class AccionViewHolder extends RecyclerView.ViewHolder {
        // Campos respectivos del item
        public CheckBox mCheckActoCondicion;
        public TextView mTextActoCondicion;

        public AccionViewHolder(View v) {
            super(v);
            mCheckActoCondicion = (CheckBox) v.findViewById(R.id.chk_card_view_accion_condicion);
            mTextActoCondicion = (TextView) v.findViewById(R.id.txt_card_view_accion_condicion);
        }
    }

    public AccionCondicionSubestandarAdapter(List<EventItemsRO> actosCondiciones,
                                             Object[] currentItems) {
        this.ListaActosCondiciones = actosCondiciones;
        listCheckedActos = new ArrayList<>();

        // Recorremos ambas listas para identificar qué ítems han sido seleccionado
        for ( int i = 0; i < actosCondiciones.size(); i++ ) {
            for ( Object c : currentItems ) {
                if ( actosCondiciones.get(i).getName().equals(((EventItemsRO)c).getName()) ) {
                    listCheckedActos.add(true);
                    break;
                }
            }
            if ( listCheckedActos.size() == i ) {
                listCheckedActos.add(false);
            }
        }
    }

    @Override
    public int getItemCount() {
        return ListaActosCondiciones.size();
    }

    @Override
    public AccionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cardview_accion_condicion_subestandar, viewGroup, false);
        return new AccionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AccionViewHolder holder, int i) {

        holder.mTextActoCondicion.setText(ListaActosCondiciones.get(i).getName());

        holder.mCheckActoCondicion.setOnCheckedChangeListener(null);

        holder.mCheckActoCondicion.setChecked(listCheckedActos.get(i));

        holder.mCheckActoCondicion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                listCheckedActos.set(holder.getAdapterPosition(), isChecked);
            }
        });
    }
}