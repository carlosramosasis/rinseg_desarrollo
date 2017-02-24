package rinseg.asistp.com.ui.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import rinseg.asistp.com.models.ROP;
import rinseg.asistp.com.ui.interfaces.IChangeViewPager;

/**
 * Created by Carlos Ramos on 28/10/2016.
 * Adaptador del ViewPager de ROPs
 */
public class PageAdapterRops extends FragmentStatePagerAdapter {

    private int mNumOfTabs;
    private ROP rop;
    private boolean closedRop;

    public PageAdapterRops(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                FragmentROPsPendientes ropsPndt = new FragmentROPsPendientes();
                return ropsPndt;
            case 1:
                FragmentROPsRegistrados ropsRegistrados = new FragmentROPsRegistrados();
                return ropsRegistrados;
            case 2:
                FragmentROPsCerrados ropsCerrados = new FragmentROPsCerrados();
                return ropsCerrados;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    @Override
    public int getItemPosition(Object object) {
        if (object instanceof IChangeViewPager) {
            //sent to FirstFragment and SecondFragment
            ((IChangeViewPager) object).notifyChanged(rop, closedRop);
        }
        return super.getItemPosition(object);
    }

    public void notifyAddRop(ROP rop, boolean closed) {
        this.rop = rop;
        closedRop = closed;
        notifyDataSetChanged();
    }
}
