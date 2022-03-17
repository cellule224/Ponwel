package house.thelittlemountaindev.ponwel.user;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;

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
import house.thelittlemountaindev.ponwel.adapter.OrdersAdapter;
import house.thelittlemountaindev.ponwel.models.Order;

/**
 * Created by Charlie One on 7/14/2018.
 */

public class UserOrdersActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private OrdersAdapter adapter;
    private ListView listView;
    private List<Order> orderList;
    private ContentLoadingProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_orders);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        listView = findViewById(R.id.orders_listview);
        progressBar = findViewById(R.id.progress_bar_loading);

        orderList = new ArrayList<>();
        adapter = new OrdersAdapter(getApplicationContext(), orderList);
        listView.setAdapter(adapter);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            getOrdersFromDb(user.getUid());
        }
    }

    private void getOrdersFromDb(String userId) {
        progressBar.show();
        databaseReference.child("Users").child(userId).child("orders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    orderList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        orderList.add(snapshot.getValue(Order.class));
                        adapter.notifyDataSetChanged();
                        progressBar.hide();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static class ActiveOrderFragment extends Fragment {

    }
}
