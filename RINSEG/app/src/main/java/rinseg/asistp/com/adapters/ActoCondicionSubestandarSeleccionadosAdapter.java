package rinseg.asistp.com.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import rinseg.asistp.com.listener.ListenerClickAccionPreventiva;
import rinseg.asistp.com.models.AccionPreventiva;
import rinseg.asistp.com.models.EventItemsRO;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.utils.Generic;

/**
 * Created by Carlos Ramos on 30/09/2016.
 */
public class ActoCondicionSubestandarSeleccionadosAdapter extends RecyclerView.Adapter<ActoCondicionSubestandarSeleccionadosAdapter.AccionViewHolder> {
    public List<EventItemsRO> ListaActosCondiciones;
    public Context mContext;


    public static class AccionViewHolder extends RecyclerView.ViewHolder {
        //Campos respectivos del item
        public CheckBox mCheckActoCondicion;
        public TextView mTextActoCondicion;



        public AccionViewHolder(View v) {
            super(v);
            mTextActoCondicion = (TextView) v.findViewById(R.id.txt_card_view_accion_condicion_seleccionado);
        }
    }

    public ActoCondicionSubestandarSeleccionadosAdapter(List<EventItemsRO> actoCondicionSub,Context context) {
        this.ListaActosCondiciones = actoCondicionSub;
        mContext = context;
    }

    @Override
    public int getItemCount() {
        return ListaActosCondiciones.size();
    }

    @Override
    public AccionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_accion_condicion_subestandar_seleccionado, viewGroup, false);
        return new AccionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AccionViewHolder viewHolder, final int i) {
        Typeface type = Typeface.createFromAsset(mContext.getAssets(),"fonts/arial_narrow.ttf");
        viewHolder.mTextActoCondicion.setText(ListaActosCondiciones.get(i).getName());
        viewHolder.mTextActoCondicion.setTypeface(type);
    }


}
