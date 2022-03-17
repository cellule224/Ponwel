package house.thelittlemountaindev.ponwel;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

/**
 * Created by Charlie One on 6/23/2018.
 */

public class CategoriesFragment extends Fragment implements View.OnClickListener{

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_categs, container, false);

        CardView categoryOne = rootView.findViewById(R.id.cv_category_one);
        CardView categoryTwo = rootView.findViewById(R.id.cv_category_two);
        CardView categoryThree = rootView.findViewById(R.id.cv_category_three);
        CardView categoryFour = rootView.findViewById(R.id.cv_category_four);
        CardView categoryFive = rootView.findViewById(R.id.cv_category_five);
        CardView categorySix = rootView.findViewById(R.id.cv_category_six);

        categoryOne.setOnClickListener(this);
        categoryTwo.setOnClickListener(this);
        categoryThree.setOnClickListener(this);
        categoryFour.setOnClickListener(this);
        categoryFive.setOnClickListener(this);
        categorySix.setOnClickListener(this);

        return rootView;

    }

    @Override
    public void onClick(View view) {
        String category = null;

        switch (view.getId()) {
            case R.id.cv_category_one:
                category = "Pantalons et Bas";
                break;

            case R.id.cv_category_two:
                category = "Chaussures et Sandals";
                break;

            case R.id.cv_category_three:
                category = "Chemises et Tops";
                break;

            case R.id.cv_category_four:
                category = "Accesoires et autres";
                break;

            case R.id.cv_category_five:
                category = "Electroniques et Tech";
                break;

            case R.id.cv_category_six:
                category = "Blings et Bijous";
                break;

        }

        Intent intent =  new Intent(getActivity(), SingleCategoryProducts.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }
}
