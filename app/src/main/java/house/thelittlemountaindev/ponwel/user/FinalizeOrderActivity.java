package house.thelittlemountaindev.ponwel.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.shuhart.stepview.StepView;

import java.util.HashMap;

import house.thelittlemountaindev.ponwel.R;
import house.thelittlemountaindev.ponwel.adapter.CheckoutPagerAdapter;
import house.thelittlemountaindev.ponwel.utils.LocationPickerActivity;
import house.thelittlemountaindev.ponwel.views.OrderConfirmationActivity;

public class FinalizeOrderActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private double deliveryLat;
    private double deliveryLon;
    public String deliveryName;
    public String deliveryNote;
    public int ordreTotalPrice;

    private int dotsCount;
    private ImageView[] dots;
    public ViewPager viewPager;
    private LinearLayout stepperLayout;
    private PagerAdapter mPagerAdapter;
    private int currentStep = 0;
    private Button btnStepperNext;
    public TextView tvTotalAmount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalize_order);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        tvTotalAmount = findViewById(R.id.tv_checkout_final_price);

        final StepView stepView = findViewById(R.id.step_view);
        btnStepperNext = findViewById(R.id.btn_stepper_next);
        viewPager = findViewById(R.id.checkout_pager);
        mPagerAdapter = new CheckoutPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
               stepView.go(position, true);
               if (position == viewPager.getChildCount()) {
                   btnStepperNext.setText("Terminer");
                   btnStepperNext.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           completOrder();
                       }
                   });
               }else {
                   btnStepperNext.setText("Suivant");
               }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        btnStepperNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentStep < stepView.getStepCount() -1 ) {
                    currentStep++;
                    stepView.go(currentStep, true);
                    viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
                }else {
                    stepView.done(true);
                }
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("addr_data"));
    }

    private void setUiPageViewController() {

        dotsCount = mPagerAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(FinalizeOrderActivity.this, R.drawable.boxed_text_bg));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(16, 0, 16, 0);

            stepperLayout.addView(dots[i], params);
        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(FinalizeOrderActivity.this, R.drawable.boxed_text_bg));
    }

    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            deliveryName = intent.getStringExtra("addr_name");
            deliveryNote = intent.getStringExtra("addr_note");
            deliveryLat = Double.parseDouble(intent.getStringExtra("addr_lat"));
            deliveryLon = Double.parseDouble(intent.getStringExtra("addr_lon"));

        }
    };

    private void completOrder(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        final String key = databaseReference.child("Orders").push().getKey();

        HashMap productsMap = new HashMap<>();
        //TODO: either get orders from db again then add to this productsMap,
        //TODO:.. or get existing list of prods from CartAvtivity

        final HashMap<String, Object> orderValues = new HashMap<>();
        orderValues.put("order_id", key);
        orderValues.put("buyer_id", user.getUid());
        orderValues.put("delivery_name", deliveryName);
        orderValues.put("delivery_note", deliveryNote);
        orderValues.put("delivery_lat", deliveryLat);
        orderValues.put("delivery_lon", deliveryLon);
        orderValues.put("order_price", ordreTotalPrice);
        orderValues.put("order_status", "en attente");
        orderValues.put("timestamp", ServerValue.TIMESTAMP);
        orderValues.put("order_products", productsMap);

        //update seller db TODO: Handle server side to add into each
        databaseReference.child("Orders").child(key).updateChildren(orderValues)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

        //delete products from user's cart
        databaseReference.child("Users").child(user.getUid()).child("user_cart").removeValue();
        startActivity(new Intent(FinalizeOrderActivity.this, OrderConfirmationActivity.class));
        }
    });

    }
}
