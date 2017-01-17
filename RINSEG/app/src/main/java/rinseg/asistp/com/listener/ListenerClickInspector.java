package rinseg.asistp.com.listener;

import rinseg.asistp.com.adapters.AccionPreventivaAdapter;
import rinseg.asistp.com.adapters.InspectorAdapter;

/**
 * @author Carlos Ramos
 * @version 1.0
 * Created on 14/10/2016
 * Interface para el onCLickListener del Recycler
 */
public interface ListenerClickInspector {
    /**
     * @param holder The ViewHolder for the clicked kitten
     * @param position The position in the grid of the kitten that was clicked
     */
    void onDeleteClicked(InspectorAdapter.InspectorViewHolder holder, int position);

}