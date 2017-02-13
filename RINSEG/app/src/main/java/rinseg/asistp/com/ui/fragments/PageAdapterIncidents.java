package rinseg.asistp.com.ui.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by OSequeiros on 10/02/2017.
 * Adapter para el ViewPager para mostrar incidencias pendientes y cerradas
 */

class PageAdapterIncidents extends FragmentStatePagerAdapter {

    private int countPages;
    private int idInspection;

    PageAdapterIncidents(FragmentManager fm, int countPages, int idInspection) {
        super(fm);
        this.countPages = countPages;
        this.idInspection = idInspection;
    }

    @Override
    public Fragment getItem(int position) {
        switch ( position ) {
            case 0:
                return new FragmentIncidentsPendings().newInstance(idInspection);
            case 1:
                return new FragmentIncidentsClosed().newInstance(idInspection);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return countPages;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch ( position ) {
            case 0:
                return "PENDIENTES";
            case 1:
                return "CERRADOS";
        }
        return super.getPageTitle(position);
    }
}