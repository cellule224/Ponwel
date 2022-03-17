package house.thelittlemountaindev.ponwel.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import house.thelittlemountaindev.ponwel.PhoneNumberAuthentication;
import house.thelittlemountaindev.ponwel.R;
import house.thelittlemountaindev.ponwel.models.Addresse;

public class AddresseAdapter extends RecyclerView.Adapter<AddresseAdapter.MyViewHolder> {
    private Context context;
    private List<Addresse> addresseList;
    private String mFlag;
    private int meh = 0;
    private String addNote;

    public AddresseAdapter(Context context, List<Addresse> addresseList, String mFlag) {
        this.context = context;
        this.addresseList = addresseList;
        this.mFlag = mFlag;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView addrId, addrName, addrNote, addrLat, addrLon;
        private Button btnRemove;
        private ImageView ivCheck;

        private MyViewHolder(final View itemView) {
            super(itemView);
            addrId = itemView.findViewById(R.id.tv_hidden_addr_id);
            addrName = itemView.findViewById(R.id.tv_saved_addr_name);
            addrNote = itemView.findViewById(R.id.tv_saved_addr_note);
            addrLat = itemView.findViewById(R.id.tv_hidden_addr_lat);
            addrLon = itemView.findViewById(R.id.tv_hidden_addr_lon);
            ivCheck = itemView.findViewById(R.id.iv_check_xxx);
            btnRemove = itemView.findViewById(R.id.btn_remove_addr);

            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeAdress(addrId.getText().toString());
                }
            });

            if (mFlag.equals("checkout")){
                btnRemove.setVisibility(View.GONE);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ivCheck.getVisibility() == View.VISIBLE) {
                            ivCheck.setVisibility(View.GONE);
                            meh = 0;
                        }else {
                            ivCheck.setVisibility(View.VISIBLE);
                            meh = 1;
                        }

                        Intent intent = new Intent("addr_data");
                        intent.putExtra("addr_name", addrName.getText().toString());
                        intent.putExtra("addr_note", addrNote.getText().toString());
                        intent.putExtra("addr_lat", addrLat.getText().toString());
                        intent.putExtra("addr_lon", addrLon.getText().toString());
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }
                });
            }else if (mFlag.equals("addr")){

            }
        }
    }

    private void removeAdress(String itemID) {
        String uid;
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DatabaseReference fbDatabase = FirebaseDatabase.getInstance().getReference();
            fbDatabase.child("Users").child(uid).child("addresses").child(itemID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(context, "Retiré de vos adresses", Toast.LENGTH_SHORT).show();
                }
            });

        }else {
            context.startActivity(new Intent(context, PhoneNumberAuthentication.class));
        }

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.addresse_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Addresse addresse = addresseList.get(position);

        holder.addrId.setText(addresse.getAd_id());
        holder.addrName.setText(addresse.getName());
        holder.addrNote.setText(addresse.getNote()); addNote = addresse.getNote();
        holder.addrLat.setText(String.valueOf(addresse.getLat()));
        holder.addrLon.setText(String.valueOf(addresse.getLon()));
    }

    @Override
    public int getItemCount() {
        return addresseList.size();
    }

}
