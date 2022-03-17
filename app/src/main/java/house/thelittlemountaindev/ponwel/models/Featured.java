package house.thelittlemountaindev.ponwel.models;

public class Featured {
    private String featured_id;
    private String featured_img_url;
    private String featured_is;
    private String featured_item_id;

    public Featured(String featured_id, String featured_img_url, String featured_is, String featured_item_id) {
        this.featured_id = featured_id;
        this.featured_img_url = featured_img_url;
        this.featured_is = featured_is;
        this.featured_item_id = featured_item_id;
    }

    public Featured() {
    }

    public String getFeatured_id() {
        return featured_id;
    }

    public String getFeatured_img_url() {
        return featured_img_url;
    }

    public String getFeatured_is() {
        return featured_is;
    }

    public String getFeatured_item_id() {
        return featured_item_id;
    }
}
