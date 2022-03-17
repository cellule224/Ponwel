package house.thelittlemountaindev.ponwel;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import house.thelittlemountaindev.ponwel.user.UserAddressesActivity;
import house.thelittlemountaindev.ponwel.user.UserOrdersActivity;

/**
 * Created by Charlie One on 6/23/2018.
 */

public class AccountFragment extends Fragment implements View.OnClickListener{

    private TextView tvName, tvPhone, tvOrders, tvMessages, tvAddresses, tvShopProfil,
                     tvManageProds, tvBecomeSeller, tvHelp, tvAbout, tvLogin;

    private LinearLayout llLoggedUserView;
    private CardView cvSellerOptions;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        llLoggedUserView = rootView.findViewById(R.id.ll_user_is_logged);
        cvSellerOptions = rootView.findViewById(R.id.cv_seller_only_options);
        tvLogin = rootView.findViewById(R.id.tv_log_in);
        tvName = rootView.findViewById(R.id.tv_user_name);
        tvPhone = rootView.findViewById(R.id.tv_user_phone);
        tvOrders = rootView.findViewById(R.id.tv_user_orders);
        tvMessages = rootView.findViewById(R.id.tv_user_messages);
        tvAddresses = rootView.findViewById(R.id.tv_user_addresses);
        tvShopProfil = rootView.findViewById(R.id.tv_seller_shop_profile);
        tvManageProds = rootView.findViewById(R.id.tv_seller_manage_products);
        tvBecomeSeller = rootView.findViewById(R.id.tv_become_a_seller);
        tvHelp = rootView.findViewById(R.id.tv_help);
        tvAbout = rootView.findViewById(R.id.tv_about);

        tvLogin.setOnClickListener(this);
        tvName.setOnClickListener(this);
        tvPhone.setOnClickListener(this);
        tvOrders.setOnClickListener(this);
        tvMessages.setOnClickListener(this);
        tvAddresses.setOnClickListener(this);
        tvShopProfil.setOnClickListener(this);
        tvManageProds.setOnClickListener(this);
        tvBecomeSeller.setOnClickListener(this);
        tvHelp.setOnClickListener(this);
        tvAbout.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null){
            FirebaseUser user = firebaseAuth.getCurrentUser();
            llLoggedUserView.setVisibility(View.VISIBLE);
            tvLogin.setVisibility(View.GONE);
            tvPhone.setText(user.getPhoneNumber());

            getUserData(user.getUid());
        }

        return rootView;
    }

    private void getUserData(String uid){
     mDatabase.child("Users").child(uid).addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {

            }
         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {

         }
     });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_user_orders:
                startActivity(new Intent(getActivity(), UserOrdersActivity.class));
                break;

            case R.id.tv_user_messages:
                Toast.makeText(getActivity(), "Cet option n'est pas encore disponible", Toast.LENGTH_SHORT).show();
                break;

            case R.id.tv_user_addresses:
                startActivity(new Intent(getActivity(), UserAddressesActivity.class));
                break;

            case R.id.tv_seller_shop_profile:
                startActivity(new Intent(getActivity(), BoutiqueProfileActivity.class));
                break;

            case R.id.tv_seller_manage_products:
                Toast.makeText(getActivity(), "Aucune vente", Toast.LENGTH_SHORT).show();
                break;

            case R.id.tv_become_a_seller:
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setMessage("Un representant vous contactera sur votre numero d'ici 48h")
                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create();
                alertDialog.show();

                break;

            case R.id.tv_help:
                startActivity(new Intent(getActivity(), HelpActivity.class));
                break;

            case R.id.tv_about:
                startActivity(new Intent(getActivity(), AboutActivity.class));
                break;

            case R.id.tv_log_in:
                startActivity(new Intent(getActivity(), PhoneNumberAuthentication.class));
                break;
            default:
                break;
        }
    }

}
