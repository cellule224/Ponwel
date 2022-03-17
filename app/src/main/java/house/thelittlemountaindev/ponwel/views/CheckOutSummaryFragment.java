package house.thelittlemountaindev.ponwel.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import house.thelittlemountaindev.ponwel.R;
import house.thelittlemountaindev.ponwel.user.FinalizeOrderActivity;

public class CheckOutSummaryFragment extends Fragment {

    private TextView totalOfCommande, addrName,addrNote, prodsPrice,delivFee,delivDiscount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_checkout_summary, container, false);


        addrName = rootView.findViewById(R.id.tv_sum_deliv_name);
        addrNote = rootView.findViewById(R.id.tv_sum_deliv_note);
        prodsPrice = rootView.findViewById(R.id.tv_sum_order_total);
        delivFee = rootView.findViewById(R.id.tv_sum_deliv_fee);
        delivDiscount = rootView.findViewById(R.id.tv_sum_deliv_discount);
        totalOfCommande = rootView.findViewById(R.id.tv_sum_deliv_total_fee);


        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, new IntentFilter("addr_data"));

        return rootView;
    }

    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String deliveryName = intent.getStringExtra("addr_name");
            String deliveryNote = intent.getStringExtra("addr_note");

            addrName.setText(deliveryName);
            addrNote.setText(deliveryNote);
            prodsPrice.setText("55,000 FG");
            delivFee.setText("15,000 FG");
            delivDiscount.setText("0 FG");
            totalOfCommande.setText("");
        }
    };
}
