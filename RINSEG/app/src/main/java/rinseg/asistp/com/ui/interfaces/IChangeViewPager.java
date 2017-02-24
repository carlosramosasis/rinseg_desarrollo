package rinseg.asistp.com.ui.interfaces;

import rinseg.asistp.com.models.ROP;

/**
 * Created by OSequeiros on 17/02/2017.
 * Interface destinada a actualizar un pager desde otro
 */

public interface IChangeViewPager {

    void notifyChanged(ROP rop, boolean fromClosed);
}
