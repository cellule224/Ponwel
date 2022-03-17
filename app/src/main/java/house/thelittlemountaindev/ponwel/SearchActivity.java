package house.thelittlemountaindev.ponwel;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import house.thelittlemountaindev.ponwel.adapter.ProductGridAdapter;
import house.thelittlemountaindev.ponwel.models.Product;

public class SearchActivity extends AppCompatActivity {

    private static final String SEARCH_FLAG = "search";
    private EditText etSearchBar;
    private DatabaseReference mDatabaseRef;
    private ProductGridAdapter productGridAdapter;
    private List<Product> productList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        setContentView(R.layout.fragment_search);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        etSearchBar = findViewById(R.id.et_search);
        productGridAdapter = new ProductGridAdapter(this, productList, SEARCH_FLAG);
        productList = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.search_recycler_view);
        productGridAdapter = new ProductGridAdapter(this, productList, SEARCH_FLAG);
        recyclerView.setAdapter(productGridAdapter);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));



        etSearchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                v = etSearchBar;
                String searchTerm = v.getText().toString();

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (!searchTerm.isEmpty()) {
                        doSearch(searchTerm);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void doSearch(String searchTerm) {
        Query resultsQuery = mDatabaseRef.child("Products").orderByChild("product_name").startAt(searchTerm).limitToFirst(3);
        resultsQuery.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                LinearLayout noResultError = findViewById(R.id.ll_no_result_error);
                if (dataSnapshot.exists() ){
                    if (productList.size() > 0){
                        productList.clear();
                    }
                    noResultError.setVisibility(View.GONE);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        fetchProducts(snapshot.getKey());
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void fetchProducts(String key) {
        mDatabaseRef.child("Products").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    productList.add(dataSnapshot.getValue(Product.class));
                    productGridAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
