package rinseg.asistp.com.listener;

import android.net.Uri;

import rinseg.asistp.com.adapters.ImageAdapter;
import rinseg.asistp.com.adapters.IncidenciaAdapter;
import rinseg.asistp.com.adapters.InspeccionAdapter;
import rinseg.asistp.com.adapters.RopAdapter;
import rinseg.asistp.com.models.FotoModel;

/**
 * @author Carlos Ramos
 * @version 1.0
 * Created on 14/10/2016
 * Interface para el onCLickListener del Recycler
 */
public interface ListenerClickImage {
    /**
     * @param holder The ViewHolder for the clicked kitten
     * @param position The position in the grid of the kitten that was clicked
     */
    void onItemClicked(ImageAdapter.ImageViewHolder holder, int position, FotoModel fotmoModel);
}