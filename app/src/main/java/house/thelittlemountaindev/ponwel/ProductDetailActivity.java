package house.thelittlemountaindev.ponwel;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
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
import java.util.Map;

import house.thelittlemountaindev.ponwel.models.Featured;

/**
 * Created by Charlie One on 6/21/2018.
 */

public class ProductDetailActivity extends AppCompatActivity {


    private String productId;
    private DatabaseReference fbDatabase, fbGalleryDatabase;
    private TextView prName, prPrice, prDescr;
    private ImageView profilImg;
    private ArrayList<String> imagesUrls;
    private Button btnAddToCart;
    private boolean isColorAvailable = false;
    private boolean isSizeAvailable = false;
    private String selectedSize;
    private String selectedColor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        setContentView(R.layout.activity_product_detail);

        fbDatabase = FirebaseDatabase.getInstance().getReference();
        fbGalleryDatabase = FirebaseDatabase.getInstance().getReference("Galleries");

        //Get extras
        Bundle extras = getIntent().getExtras();
        String productTitle = extras.getString("title");
        productId = extras.getString("pId");

        Toolbar toolbar = findViewById(R.id.prod_detail_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(productTitle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prName = findViewById(R.id.tv_product_name);
        prPrice = findViewById(R.id.tv_product_price);
        prDescr = findViewById(R.id.tv_product_desc);
        profilImg = findViewById(R.id.app_bar_image);
        btnAddToCart = findViewById(R.id.btn_details_add_cart);

        prDescr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prDescr.setMaxLines(prDescr.getLineCount());
            }
        });

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    addToCart(uid);
                }else {
                    startActivity(new Intent(ProductDetailActivity.this, PhoneNumberAuthentication.class));
                }
            }
        });

        fecthProductDetails();
    }

    private void addToCart(String uid) {
        if (isSizeAvailable && selectedSize == null) {
            Toast.makeText(this, "Selectionnez mesures", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isColorAvailable && selectedColor == null) {
            Toast.makeText(this, "Selectionnez couleures", Toast.LENGTH_SHORT).show();
            return;
        }


            HashMap<String, Object> map = new HashMap<>();
            map.put("item_id", productId);
            map.put("item_size", selectedSize);
            map.put("item_color", selectedColor);

            fbDatabase.child("Users").child(uid).child("user_cart").child(productId).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    btnAddToCart.setText("Ajouté");
                    btnAddToCart.setCompoundDrawables(getResources().getDrawable(R.drawable.fui_done_check_mark, null), null, null, null);
                }
            });

    }

    private void fecthProductDetails() {
        fbDatabase.child("Products").child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue(Object.class);
                    prName.setText(map.get("product_name").toString());
                    prPrice.setText(map.get("product_price").toString());
                    prDescr.setText(map.get("product_desc").toString());

                    String url = map.get("product_pic_url").toString();
                    Glide.with(ProductDetailActivity.this).load(url).into(profilImg);

                    loadGallery();

                    if (map.get("sizes/sizes") != null) {
                        loadSizes();
                    }

                    if (map.get("colors/colors") != null) {
                        loadColors();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadGallery(){
        final ImageView noPhotosImg = findViewById(R.id.iv_no_photos);

        fbGalleryDatabase.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() > 0) {
                    imagesUrls = new ArrayList<String>();
                    for (DataSnapshot imgUrl : dataSnapshot.getChildren()) {
                        imagesUrls.add(imgUrl.getValue().toString());

                        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.show_gallery_LL);
                        LinearLayout.LayoutParams layoutParams;

                        ImageView gallImage = new ImageView(ProductDetailActivity.this);
                        gallImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        gallImage.setPadding(8,8,8,8);
                        gallImage.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        gallImage.setAdjustViewBounds(true);

                        Glide
                                .with(ProductDetailActivity.this)
                                .load(imgUrl.getValue().toString())
                                .into(gallImage);

                        linearLayout.addView(gallImage);
                        gallImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("images", imagesUrls);
                                bundle.putInt("position", 0);

                                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                SlideShowFragment fragment = SlideShowFragment.newInstance();
                                fragment.setArguments(bundle);
                                fragment.show(fragmentTransaction, "preview");
                            }
                        });
                    }
            }else{
                    noPhotosImg.setVisibility(View.VISIBLE);
                    noPhotosImg.setImageResource(R.mipmap.product_holder);
            }
                fbGalleryDatabase.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadSizes(){
        final ArrayList<String> sizeArray = new ArrayList();

        fbDatabase.child("Products").child(productId).child("sizes/").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    sizeArray.add(snapshot.getValue(String.class));

                    RadioGroup radioGroup = findViewById(R.id.radiogroup);
                    RadioGroup.LayoutParams layoutParams;

                    for (int i=0;i< sizeArray.size() ;i++){
                        RadioButton radioButton = new RadioButton(ProductDetailActivity.this);
                        radioButton.setPadding(12,0,12,0);
                        radioButton.setBackgroundResource(R.drawable.boxed_text_bg);
                        radioButton.setText(sizeArray.get(i));
                        radioButton.setId(i);
                        layoutParams = new RadioGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(0, 0, 8, 0);
                        radioGroup.addView(radioButton, layoutParams);

                        isSizeAvailable = true;


                        int id = radioGroup.getCheckedRadioButtonId();
                        RadioButton rbtn = radioButton.findViewById(id);
                        selectedSize = rbtn.getText().toString();
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadColors(){
        final ArrayList<String> colorArray = new ArrayList<>();

        fbDatabase.child("Products").child(productId).child("colors/").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    colorArray.add(snapshot.getValue(String.class));
                    LinearLayout linearLayout = findViewById(R.id.ll_colors);
                    LinearLayout.LayoutParams layoutParams;

                    for (int i = 0; i < colorArray.size(); i++) {
                        TextView colorBox = new TextView(ProductDetailActivity.this);
                        colorBox.setBackgroundResource(R.drawable.rounded_add_btn);
                        colorBox.setId(i);
//                        colorBox.setText(colorArray.get(i));
                        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(0,16,16,16);
                        layoutParams.height = 46;
                        layoutParams.width = 46;
                        colorBox.setBackgroundColor(Color.parseColor(colorArray.get(i)));
                        linearLayout.addView(colorBox, layoutParams);

                        isColorAvailable = true;

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}






