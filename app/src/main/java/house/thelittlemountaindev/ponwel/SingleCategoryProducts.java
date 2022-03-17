package house.thelittlemountaindev.ponwel;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import house.thelittlemountaindev.ponwel.adapter.ProductGridAdapter;
import house.thelittlemountaindev.ponwel.models.Product;

/**
 * Created by Charlie One on 7/12/2018.
 */

public class SingleCategoryProducts extends AppCompatActivity {

    private static final String FLAG = "single_category";
    DatabaseReference mDatabase;
    private String category;
    private ArrayList<Product> productList;
    private ProductGridAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_category_product);

        mDatabase = FirebaseDatabase.getInstance().getReference("Products");

        Intent i = getIntent();
        category = i.getStringExtra("category");

/*
        Toolbar toolbar = findViewById(R.id.toolbar_prd);
        setSupportActionBar(toolbar);
        toolbar.setTitle("All in "+category);
*/

        productList = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.recycler_single_prd);
        adapter = new ProductGridAdapter(this, productList, FLAG);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        //recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));

        getProducts();
    }

    private void getProducts(){
        mDatabase.orderByChild("product_category").equalTo(category).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        productList.add(snapshot.getValue(Product.class));
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
