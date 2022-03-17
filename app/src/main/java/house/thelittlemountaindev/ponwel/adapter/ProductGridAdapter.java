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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

import house.thelittlemountaindev.ponwel.PhoneNumberAuthentication;
import house.thelittlemountaindev.ponwel.ProductDetailActivity;
import house.thelittlemountaindev.ponwel.R;
import house.thelittlemountaindev.ponwel.models.Product;

/**
 * Created by Charlie One on 6/20/2018.
 */

public class ProductGridAdapter extends RecyclerView.Adapter<ProductGridAdapter.MyViewHolder> {
    private Context context;
    private List<Product> productList;
    private String mFlag;

    public ProductGridAdapter(Context context, List<Product> productList, String mFlag) {
        this.context = context;
        this.productList = productList;
        this.mFlag = mFlag;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView productId, productPrice, productName, productShop;
        private ImageView productImg;
        private Button btnAddToCart;

        private MyViewHolder(View itemView) {
            super(itemView);
            productId = itemView.findViewById(R.id.tv_hidden_product_id);
            productPrice = itemView.findViewById(R.id.tv_product_price);
            productName = itemView.findViewById(R.id.tv_product_name);
            productShop = itemView.findViewById(R.id.tv_item_shop_name);
            productImg = itemView.findViewById(R.id.iv_product_img);
            btnAddToCart = itemView.findViewById(R.id.btn_item_add_to_cart);
            
            btnAddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addToCart(productId.getText().toString(), btnAddToCart);
                }
            });
            
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context mContext = v.getContext();
                    Intent intent = new Intent(context, ProductDetailActivity.class);
                    intent.putExtra("pId", productId.getText().toString());
                    intent.putExtra("title", productName.getText().toString());
                    mContext.startActivity(intent);
                }
            });

            if (mFlag.equals("single_category")){
                btnAddToCart.setVisibility(View.GONE);
                productShop.setVisibility(View.GONE);
            }
        }
    }

    private void addToCart(String productID, final Button button) {
        String uid;
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            HashMap<String, Object> map = new HashMap<>();
            map.put(productID, true);

            DatabaseReference fbDatabase = FirebaseDatabase.getInstance().getReference();
            fbDatabase.child("Users").child(uid).child("user_cart").updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(context, "Ajouté à vos commandes", Toast.LENGTH_SHORT).show();
                    button.setEnabled(false);
                }
            });

        }else {
            context.startActivity(new Intent(context, PhoneNumberAuthentication.class));
        }

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.productId.setText(product.getProduct_id());
        holder.productPrice.setText(product.getProduct_price()+ " FG");
        holder.productName.setText(product.getProduct_name());
        holder.productShop.setText(product.getProduct_boutique());

        Glide.with(context)
                .load(product.getProduct_pic_url())
                .apply(new RequestOptions()
                    .placeholder(R.mipmap.product_holder))
                .into(holder.productImg);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}
