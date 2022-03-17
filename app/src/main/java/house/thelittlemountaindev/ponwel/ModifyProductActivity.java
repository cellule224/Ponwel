package house.thelittlemountaindev.ponwel;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;

import house.thelittlemountaindev.ponwel.models.Product;

public class ModifyProductActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private Uri outputFileUri;
    private EditText pName, pPrice, pDescription, pQuantity;
    private String mCurrentPhotoPath;
    private Uri profilePicUri;
    private ProgressBar uploadProgressBar;
    private DatabaseReference fbDatabase;
    private FirebaseStorage fbStorage;
    private HorizontalScrollView horizontalScrollView;
    private String productKey;
    private TextView addPicturesTxt;
    private FirebaseUser fbUser;

    private Button btnUpdate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_product);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        Bundle extra = getIntent().getExtras();
        productKey = extra.getString("pId");

        addPicturesTxt = findViewById(R.id.mod_tv_add_pictures);
        pName = findViewById(R.id.mod_et_product_name);
        pPrice = findViewById(R.id.mod_et_product_price);
        pDescription = findViewById(R.id.mod_et_product_desc);
        horizontalScrollView = findViewById(R.id.mod_horizontalScrollV);
        pQuantity = findViewById(R.id.mod_et_product_quantity);

        btnUpdate = findViewById(R.id.mod_btn_update_product);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateInfos();
            }
        });
        getCurrentInfos();
    }

    private void getCurrentInfos(){
        databaseReference.child("Products").child(productKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    pName.setText(product.getProduct_name());
                    pPrice.setText(product.getProduct_price());
                    pQuantity.setText(String.valueOf(product.getProduct_quantity()));
                    pDescription.setText(product.getProduct_desc());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateInfos(){
        HashMap<String, Object> map = new HashMap();
        map.put("product_name", pName.getText().toString());
        map.put("product_price", pPrice.getText().toString());
        map.put("product_quantity", Integer.parseInt(pQuantity.getText().toString()));
        map.put("product_desc", pDescription.getText().toString());

        databaseReference.child("Products").child(productKey).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                finish();
            }
        });
    }

}
