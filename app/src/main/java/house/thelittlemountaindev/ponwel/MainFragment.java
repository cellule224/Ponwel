package house.thelittlemountaindev.ponwel;

import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import house.thelittlemountaindev.ponwel.adapter.FeaturedViewPagerAdapter;
import house.thelittlemountaindev.ponwel.adapter.ProductGridAdapter;
import house.thelittlemountaindev.ponwel.models.Featured;
import house.thelittlemountaindev.ponwel.models.Product;

/**
 * Created by Charlie One on 6/21/2018.
 */

public class MainFragment extends Fragment {


    private static final String MAIN_FRAGMENT = "main_frag";
    private DatabaseReference databaseReference;
    private ProductGridAdapter productGridAdapter;
    private List<Product> productList;

    private Handler handler;
    private int page = 0;
    private ViewPager viewPager;
    private FeaturedViewPagerAdapter pagerAdapter;
    private List<Featured> featuredList;

    Runnable runnable = new Runnable() {
        public void run() {
            if (pagerAdapter.getCount() == page) {
                page = 0;
            } else {
                page++;
            }
            viewPager.setCurrentItem(page, true);
            handler.postDelayed(this, 4000);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        productList = new ArrayList<>();
        featuredList = new ArrayList<>();

        handler = new Handler();

        //RecyclerView and Adapter setup
        RecyclerView recyclerView = rootView.findViewById(R.id.grid_recycler_view);
        productGridAdapter = new ProductGridAdapter(getActivity(), productList, MAIN_FRAGMENT);
        recyclerView.setAdapter(productGridAdapter);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        //Viewpager and adapter setup
        viewPager = rootView.findViewById(R.id.featured_pager);
        viewPager.setClipToPadding(false);
        viewPager.setPadding(8,8,8,0);
        viewPager.setPageMargin(dpToPx(8));
        pagerAdapter = new FeaturedViewPagerAdapter(getActivity(), featuredList);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                page = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setAdapter(pagerAdapter);

        getFirstPageProductsFromDb();
        getFeaturedProductsFromDb();

        return rootView;
    }

    private void getFeaturedProductsFromDb() {
        databaseReference.child("Featured").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        featuredList.add(snapshot.getValue(Featured.class));
                        pagerAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getFirstPageProductsFromDb(){
        databaseReference.child("Products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                if (dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        productList.add(snapshot.getValue(Product.class));
                        productGridAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private int dpToPx(int dp){
        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 3000);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    private class FeaturedProductspagerAdapter extends FragmentStatePagerAdapter {
        public FeaturedProductspagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new FeaturedPagerFragment();
        }

        @Override
        public int getCount() {
            return featuredList.size();
        }
    }
}
