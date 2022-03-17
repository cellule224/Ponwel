package house.thelittlemountaindev.ponwel.models;

public class Order  {
    private String order_id;
    private String buyer_id;
    private double delivery_lat;
    private double delivery_lon;
    private String order_status;
    private Long order_price;
    private Long updated;
    private Long timestamp;

    public Order() {
    }

    public Order(String order_id, String buyer_id, double delivery_lat, double delivery_lon, String order_status, Long order_price, Long updated, Long timestamp) {
        this.order_id = order_id;
        this.buyer_id = buyer_id;
        this.delivery_lat = delivery_lat;
        this.delivery_lon = delivery_lon;
        this.order_status = order_status;
        this.order_price = order_price;
        this.updated = updated;
        this.timestamp = timestamp;
    }


    public String getOrder_id() {
        return order_id;
    }

    public String getBuyer_id() {
        return buyer_id;
    }

    public double getDelivery_lat() {
        return delivery_lat;
    }

    public double getDelivery_lon() {
        return delivery_lon;
    }

    public String getOrder_status() {
        return order_status;
    }

    public Long getOrder_price() {
        return order_price;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Long getUpdated() {
        return updated;
    }
}
