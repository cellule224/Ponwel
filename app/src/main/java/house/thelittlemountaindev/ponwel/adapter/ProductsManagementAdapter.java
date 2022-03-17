package house.thelittlemountaindev.ponwel.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

import house.thelittlemountaindev.ponwel.ModifyProductActivity;
import house.thelittlemountaindev.ponwel.R;
import house.thelittlemountaindev.ponwel.models.Product;

public class ProductsManagementAdapter extends RecyclerView.Adapter<ProductsManagementAdapter.MyViewHolder> {
    private Context context;
    private List<Product> productList;

    public ProductsManagementAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView productId, productQuantity, productPrice;
        private ImageView productImg;

        private MyViewHolder(final View itemView) {
            super(itemView);
            productId = itemView.findViewById(R.id.tv_hidden_id);
            productQuantity = itemView.findViewById(R.id.tv_prd_qty_left);
            productPrice = itemView.findViewById(R.id.tv_price_product);
            productImg = itemView.findViewById(R.id.iv_product_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showModifyDialog(productId.getText().toString(), Integer.parseInt(productQuantity.getText().toString()));
                }
            });
        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.product_item_management, parent, false);
        return new ProductsManagementAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.productId.setText(product.getProduct_id());
        holder.productPrice.setText(product.getProduct_price()+ " $");
        holder.productQuantity.setText(String.valueOf(product.getProduct_quantity()));

        if (product.getProduct_quantity() < 1) {
            holder.productQuantity.setTextColor(Color.RED);
        }

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


    private void deleteItem(String prodKey){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Products").child(prodKey).removeValue();
    }


    private void showModifyDialog(final String pId, final int qtty){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_modify_qtty);

        final TextView tvQtty = dialog.findViewById(R.id.tv_dialog_qtty);
        final Button btnMinus = dialog.findViewById(R.id.btn_dialog_minus);
        Button btnPlus = dialog.findViewById(R.id.btn_dialog_plus);

        tvQtty.setText(String.valueOf(qtty));

        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvQtty.setText(String.valueOf(qtty - 1));
            }
        });

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvQtty.setText(String.valueOf(qtty + 1));
            }
        });



        Button btnMore = dialog.findViewById(R.id.btn_dialog_more);
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ModifyProductActivity.class);
                intent.putExtra("pId", pId);
                context.startActivity(intent);
            }
        });

        Button btnOK = (Button) dialog.findViewById(R.id.btn_dialog_ok);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                HashMap<String, Object> updatedQtty = new HashMap<>();

                updatedQtty.put("product_quantity", Integer.parseInt(tvQtty.getText().toString()));
                databaseReference.child("Products").child(pId).updateChildren(updatedQtty);
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}
