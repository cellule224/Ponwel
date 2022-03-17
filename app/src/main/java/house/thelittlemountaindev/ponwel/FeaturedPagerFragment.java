package house.thelittlemountaindev.ponwel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import house.thelittlemountaindev.ponwel.models.Featured;

public class FeaturedPagerFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_featured_slide_page, container, false);

        TextView id = rootView.findViewById(R.id.tv_hidden_featured_id);
        Featured featured = new Featured();
        id.setText(featured.getFeatured_item_id());

        return rootView;
    }
}
