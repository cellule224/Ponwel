package house.thelittlemountaindev.ponwel.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import house.thelittlemountaindev.ponwel.views.CheckOutSummaryFragment;
import house.thelittlemountaindev.ponwel.views.CheckoutAddressFragment;
import house.thelittlemountaindev.ponwel.views.CheckoutCartFragment;
import house.thelittlemountaindev.ponwel.views.CheckoutPaymentFragment;

public class CheckoutPagerAdapter extends FragmentPagerAdapter {
    public CheckoutPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new CheckoutCartFragment();
            case 1: return new CheckoutAddressFragment();
            case 2: return new CheckOutSummaryFragment(); //CheckoutPaymentFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
