package house.thelittlemountaindev.ponwel.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import house.thelittlemountaindev.ponwel.R;
import house.thelittlemountaindev.ponwel.models.Order;

public class OrdersAdapter extends BaseAdapter {
    private Context context;
    private List<Order> orderList;
    private ArrayList<Uri> profilePicUrls = new ArrayList<>();

    public OrdersAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }


    @Override
    public int getCount() {
        return orderList.size();
    }

    @Override
    public Object getItem(int position) {
        return orderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.orders_list_item, parent, false);

        Order order = orderList.get(position);

        ImageView ivColoredStatus = (ImageView) row.findViewById(R.id.color_coded_order_status);
        TextView timeStamp = (TextView) row.findViewById(R.id.tv_order_timestamp);
        TextView tvOrderStatus = (TextView) row.findViewById(R.id.tv_order_status);
        TextView tvOrderId = row.findViewById(R.id.hidden_order_id);

        tvOrderId.setText(order.getOrder_id());
        tvOrderStatus.setText(order.getOrder_status());
        timeStamp.setText(epochToDate(order.getTimestamp()));

        switch (order.getOrder_status()) {
            case "retourné":
                ivColoredStatus.setImageResource(R.drawable.ic_returned_order_circle_green);
                break;
            case "annulé":
                ivColoredStatus.setImageResource(R.drawable.ic_cancel_red_24dp);
                break;
            case "en attente":
                ivColoredStatus.setImageResource(R.drawable.ic_order_waiting_circle_yellow_24dp);
                break;
            case "confirmé":
                ivColoredStatus.setImageResource(R.drawable.ic_order_waiting_circle_yellow_24dp);
                break;
            case "traité":
                ivColoredStatus.setImageResource(R.drawable.ic_done_circle_yellow);
                break;
            case "en route":
                ivColoredStatus.setImageResource(R.drawable.ic_done_circle_yellow);
                break;
            case "livré":
                ivColoredStatus.setImageResource(R.drawable.ic_done_circle_green_24dp);
                break;

            default:
                ivColoredStatus.setImageResource(R.drawable.ic_order_waiting_circle_yellow_24dp);
                break;
        }

        return row;
    }

    private String epochToDate(Long timestamp){
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, HH:mm", Locale.FRANCE);
        return sdf.format(new Date(timestamp));
    }
}
