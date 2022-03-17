package house.thelittlemountaindev.ponwel;


import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import house.thelittlemountaindev.ponwel.utils.CustomImageView;
import house.thelittlemountaindev.ponwel.utils.ZoomableImageView;

/**
 * Created by Charlie One on 9/21/2017.
 */

public class SlideShowFragment extends DialogFragment {
    private TextView previewCounter;
    private ViewPager viewPager;
    private ArrayList<String> images;
    private ViewPagerAdapter pagerAdapter;
    private int clickedPosition = 0;

    static SlideShowFragment newInstance(){
        SlideShowFragment fragment = new SlideShowFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_image_slider, container, false);

        previewCounter = (TextView) rootView.findViewById(R.id.preview_counter);
        viewPager = (ViewPager) rootView.findViewById(R.id.gallery_viewpager);

        images = (ArrayList<String>) getArguments().getSerializable("images");
        clickedPosition = getArguments().getInt("position");

        pagerAdapter = new ViewPagerAdapter();
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerChangerListener);

        setCurrentItem(clickedPosition);
        return rootView;
    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
        setCount(clickedPosition);
    }

    ViewPager.OnPageChangeListener viewPagerChangerListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            setCount(position);
        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void setCount(int position) {
        previewCounter.setText((position + 1) + " of " + images.size());

//       Image image = images.get(position);
        //lblTitle.setText(image.getName());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }


    public class ViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;
        public ViewPagerAdapter(){
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.fullscreen_preview, container, false);

            ZoomableImageView imagePreview = (ZoomableImageView) view.findViewById(R.id.iv_preview);

            String image = images.get(position);

            Glide.with(getActivity())
                    .load(Uri.parse(image))
                    .into(imagePreview);

            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((View) object);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

}
