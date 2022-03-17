package house.thelittlemountaindev.ponwel.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import java.util.List;

import house.thelittlemountaindev.ponwel.ProductDetailActivity;
import house.thelittlemountaindev.ponwel.R;
import house.thelittlemountaindev.ponwel.models.Featured;

public class FeaturedViewPagerAdapter extends PagerAdapter {

    private Context context;
    private List<Featured> featuredList;

    public FeaturedViewPagerAdapter() {
    }

    public FeaturedViewPagerAdapter(Context context, List<Featured> featuredList) {
        this.context = context;
        this.featuredList = featuredList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        Featured featured = featuredList.get(position);

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_featured_slide_page, container, false);
        ImageView pagerImage = view.findViewById(R.id.iv_featured_img);
        TextView featuredId = view.findViewById(R.id.tv_hidden_featured_id);
        final TextView featuredItemId = view.findViewById(R.id.id_hidden_featured_product_id);

        featuredId.setText(featured.getFeatured_id());
        featuredItemId.setText(featured.getFeatured_item_id());
        Glide
                .with(context)
                .load(featured.getFeatured_img_url())
                .into(pagerImage);

        container.addView(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context mContext = v.getContext();
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("pId", featuredItemId.getText().toString());
                intent.putExtra("title", "Ponwel");
                mContext.startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return featuredList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
