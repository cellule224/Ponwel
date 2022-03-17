package house.thelittlemountaindev.ponwel.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

import house.thelittlemountaindev.ponwel.R;
import house.thelittlemountaindev.ponwel.adapter.AddresseAdapter;
import house.thelittlemountaindev.ponwel.models.Addresse;
import house.thelittlemountaindev.ponwel.utils.LocationPickerActivity;

import static android.app.Activity.RESULT_OK;

public class CheckoutAddressFragment extends Fragment implements View.OnClickListener{

    private Button btnAddressOther;
    private static final int HOME_LOCATION_PICKER_REQUEST_CODE = 1;
    private static final int WORK_LOCATION_PICKER_REQUEST_CODE = 2;
    private static final int OTHER_LOCATION_PICKER_REQUEST_CODE = 3;

    public double deliveryLat, deliveryLon;

    private DatabaseReference databaseReference;
    private List<Addresse> addresseList;
    private AddresseAdapter addresseAdapter;
    private LinearLayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_checkout_address, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        btnAddressOther = rootView.findViewById(R.id.btn_address_other);

        btnAddressOther.setOnClickListener(this);

        addresseList = new ArrayList<>();

        RecyclerView recyclerView = rootView.findViewById(R.id.checkout_addrress_recycler);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        addresseAdapter = new AddresseAdapter(getActivity(), addresseList, "checkout");
        recyclerView.setAdapter(addresseAdapter);

        getAddresses(user.getUid());

        return rootView;
    }

    private void getAddresses(String uid){
        databaseReference.child("Users").child(uid).child("addresses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                addresseList.clear();
                if (dataSnapshot.getChildrenCount() > 0){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        addresseList.add(snapshot.getValue(Addresse.class));
                        addresseAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), LocationPickerActivity.class);
        switch (v.getId()) {
            case 1:
                startActivityForResult(intent, HOME_LOCATION_PICKER_REQUEST_CODE);
                break;

            case R.id.btn_address_other:
                startActivityForResult(intent, OTHER_LOCATION_PICKER_REQUEST_CODE);
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case HOME_LOCATION_PICKER_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    deliveryLat = data.getDoubleExtra("lat", 0.00);
                    deliveryLon = data.getDoubleExtra("lon", 0.00);
                }

                break;

            case WORK_LOCATION_PICKER_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    deliveryLat = data.getDoubleExtra("lat", 0.00);
                    deliveryLon = data.getDoubleExtra("lon", 0.00);
                }

                break;


            case OTHER_LOCATION_PICKER_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    deliveryLat = data.getDoubleExtra("lat", 0.00);
                    deliveryLon = data.getDoubleExtra("lon", 0.00);
                }

                break;


            default:
                break;

        }
    }

}
