package house.thelittlemountaindev.ponwel.user;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import house.thelittlemountaindev.ponwel.PhoneNumberAuthentication;
import house.thelittlemountaindev.ponwel.R;
import house.thelittlemountaindev.ponwel.adapter.AddresseAdapter;
import house.thelittlemountaindev.ponwel.adapter.ProductGridAdapter;
import house.thelittlemountaindev.ponwel.models.Addresse;
import house.thelittlemountaindev.ponwel.utils.LocationPickerActivity;

/**
 * Created by Charlie One on 7/14/2018.
 */

public class UserAddressesActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION = 1;
    private String addressLocation;
    private EditText addreName, addressNote;
    private TextView tvPickLocation;
    private Button btnAddNewAdress;
    private LinearLayout llNewAddress;
    private DatabaseReference databaseReference;
    private double picked_lat = 0.0, picked_lon = 0.0;
    private List<Addresse> addresseList;
    private AddresseAdapter addresseAdapter;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addresses);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        addreName = findViewById(R.id.et_locationName);
        addressNote = findViewById(R.id.et_address_note);
        tvPickLocation = findViewById(R.id.tv_open_location_pick);
        btnAddNewAdress = findViewById(R.id.btn_new_addresse);
        llNewAddress = findViewById(R.id.ll_add_new_adress);

        addresseList = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.addrress_recycler);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        addresseAdapter = new AddresseAdapter(this, addresseList, "addr");
        recyclerView.setAdapter(addresseAdapter);

        if (user.getUid() != null) {
            getAddresses(user.getUid());
        }

        tvPickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserAddressesActivity.this, LocationPickerActivity.class);
                startActivityForResult(intent, REQUEST_LOCATION);
            }
        });
        btnAddNewAdress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llNewAddress.setVisibility(View.VISIBLE);
                btnAddNewAdress.setText("Enregistrer");

                btnAddNewAdress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (user == null){
                            startActivity(new Intent(UserAddressesActivity.this, PhoneNumberAuthentication.class));
                        }
                        if (TextUtils.isEmpty(addreName.getText())){
                            addreName.setError("Vide!");
                            return;
                        }
                        if (TextUtils.isEmpty(addressNote.getText())){
                            addressNote.setError("Vide!");
                            return;
                        }
                        if (picked_lat == 0.0){
                            tvPickLocation.setTextColor(Color.RED);
                            return;
                        }

                        addAdressToDb(user.getUid()
                                ,addreName.getText().toString()
                                ,addressNote.getText().toString()
                                ,picked_lat, picked_lon);

                    }
                });
            }
        });

    }

    private void addAdressToDb(String user, String name, String note, final double lat, final double lon) {
        String key = databaseReference.child(user).child("addresses").push().getKey();
        HashMap<String, Object> addressData = new HashMap<>();
        addressData.put("ad_id", key);
        addressData.put("name", name);
        addressData.put("note", note);
        addressData.put("lat", lat);
        addressData.put("lon", lon);

        databaseReference.child(user).child("addresses").child(key).updateChildren(addressData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                addreName.setText("");
                addressNote.setText("");
                picked_lat = 0.0;
                picked_lon = 0.0;
                llNewAddress.setVisibility(View.GONE);
                btnAddNewAdress.setText("Nouvelle Addresse");


            }
        });
    }

    private void getAddresses(String uid){
        databaseReference.child(uid).child("addresses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                addresseList.clear();
                if (dataSnapshot.getChildrenCount() > 0){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        addresseList.add(snapshot.getValue(Addresse.class));
                        addresseAdapter.notifyDataSetChanged();
                    }
                }else {
                    TextView emptyTV = findViewById(R.id.tv_no_addr_err);
                    emptyTV.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOCATION) {
            if (resultCode == RESULT_OK) {
                picked_lat = data.getDoubleExtra("lat", 0.00);
                picked_lon = data.getDoubleExtra("lon", 0.00);

                if (picked_lat != 0.0) {
                    tvPickLocation.setTextColor(Color.GREEN);
                }
            }
        }
    }
}
