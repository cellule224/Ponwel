package house.thelittlemountaindev.ponwel.models;

public class Addresse {
    private String ad_id;
    private String name;
    private String note;
    private double lat;
    private double lon;

    public Addresse() {
    }

    public Addresse(String ad_id, String name, String note, double lat, double lon) {
        this.ad_id = ad_id;
        this.name = name;
        this.note = note;
        this.lat = lat;
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getAd_id() {
        return ad_id;
    }

    public void setAd_id(String ad_id) {
        this.ad_id = ad_id;
    }
}
