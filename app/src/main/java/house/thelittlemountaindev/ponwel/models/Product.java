package house.thelittlemountaindev.ponwel.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Charlie One on 6/20/2018.
 */

public class Product {
    private String product_id;
    private String product_name;
    private String product_category;
    private String product_price;
    private String product_desc;
    private String product_pic_url;
    private String product_seller_id;
    private String product_boutique;
    private int product_quantity;

    public Product() {
        // Default constructor required for calls to DataSnapshot.getValue(Product.class)
    }

    public Product(String product_id, String product_name, String product_category, String product_price,
                   String product_desc, String product_pic_url, String product_seller_id, String product_boutique, int product_quantity) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.product_category = product_category;
        this.product_price = product_price;
        this.product_desc = product_desc;
        this.product_pic_url = product_pic_url;
        this.product_seller_id = product_seller_id;
        this.product_boutique = product_boutique;
        this.product_quantity = product_quantity;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("product_id", product_id);
        result.put("product_name", product_name);
        result.put("product_category", product_category);
        result.put("product_price", product_price);
        result.put("product_desc", product_desc);
        result.put("product_pic_url", product_pic_url);
        result.put("product_seller_id", product_seller_id);
        result.put("product_boutique", product_boutique);
        result.put("product_quantity", product_quantity);

        return result;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }


    public String getProduct_desc() {
        return product_desc;
    }

    public void setProduct_desc(String product_desc) {
        this.product_desc = product_desc;
    }

    public String getProduct_pic_url() {
        return product_pic_url;
    }

    public void setProduct_pic_url(String product_pic_url) {
        this.product_pic_url = product_pic_url;
    }


    public String getProduct_category() {
        return product_category;
    }

    public void setProduct_category(String product_category) {
        this.product_category = product_category;
    }

    public int getProduct_quantity() {
        return product_quantity;
    }

    public void setProduct_quantity(int product_quantity) {
        this.product_quantity = product_quantity;
    }

    public String getProduct_seller_id() {
        return product_seller_id;
    }

    public void setProduct_seller_id(String product_seller_id) {
        this.product_seller_id = product_seller_id;
    }

    public String getProduct_boutique() {
        return product_boutique;
    }

    public void setProduct_boutique(String product_boutique) {
        this.product_boutique = product_boutique;
    }
}

