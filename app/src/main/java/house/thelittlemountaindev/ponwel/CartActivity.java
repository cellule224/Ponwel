package house.thelittlemountaindev.ponwel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import house.thelittlemountaindev.ponwel.adapter.CartItemsAdapater;
import house.thelittlemountaindev.ponwel.models.Product;
import house.thelittlemountaindev.ponwel.user.FinalizeOrderActivity;

/**
 * Created by Charlie One on 6/28/2018.
 */

public class CartActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private CartItemsAdapater adapater;
    private List<Product> productList;

    private String userId;
    private Button btnSubmitOrder;
    private TextView tvFinalPrice;
    private int totalPriceInt = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        productList = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.cart_recycler);
        adapater = new CartItemsAdapater(this, productList);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapater);


        tvFinalPrice = findViewById(R.id.tv_final_price);


        if (firebaseAuth.getCurrentUser() != null) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            getItemIdsFromDb(user.getUid());
        }else {
            //For Emulator tests only
            //getProductsFromDbx();
        }

        btnSubmitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (totalPriceInt < 1000) {
                    Toast.makeText(CartActivity.this, "Montant non valide!", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(CartActivity.this, FinalizeOrderActivity.class);
                    intent.putExtra("total_price", totalPriceInt);
                    startActivity(intent);
                }
            }
        });
    }

    private void getItemIdsFromDb(String uid){
        databaseReference.child("Users").child(uid).child("user_cart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                productList.clear();
                totalPriceInt = 0;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        getProductsFromDb(snapshot.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void getProductsFromDb(String key){
        databaseReference.child("Products").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        String count = dataSnapshot.child("product_price").getValue().toString();

                        totalPriceInt += Integer.parseInt(count);
                        tvFinalPrice.setText(String.valueOf(totalPriceInt) + " GNF");

                        productList.add(dataSnapshot.getValue(Product.class));
                        adapater.notifyDataSetChanged();
                    }
        }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void getProductsFromDbx(){
        databaseReference.child("Products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        productList.add(snapshot.getValue(Product.class));
                        adapater.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String calculateTotal(int itemPrice){
        String sumTotal = String.valueOf(totalPriceInt + itemPrice);
        return sumTotal;
    }
}
