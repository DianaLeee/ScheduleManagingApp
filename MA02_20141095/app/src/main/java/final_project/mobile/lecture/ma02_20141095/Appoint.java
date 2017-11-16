package final_project.mobile.lecture.ma02_20141095;

import java.io.Serializable;

/**
 * Created by LeeDaYeon on 2016-12-18.
 */

public class Appoint implements Serializable {

    private int _id;
    private String title;
    private String content;
    private long time;
    private String sName;
    private String sNumber;
    private long sId;

    private double latitude;
    private double longitude;
    private String place_name;
    private String place_phone;
    private String place_address;

    public Appoint() {

    }

    public long getsId() {
        return sId;
    }

    public void setsId(long sId) {
        this.sId = sId;
    }

    public Appoint(String content, String sName, long time, String title, String place_name, int _id) {
        this.content = content;
        this.sName = sName;
        this.time = time;
        this.title = title;
        this.place_name = place_name;
        this._id = _id;
    }

    public Appoint(int _id, String title, String content, long time,  String sName, String sNumber, long sId, double latitude, double longitude, String place_name, String place_address) {
        this._id = _id;
        this.content = content;
        this.latitude = latitude;
        this.longitude = longitude;
        this.place_address = place_address;
        this.place_name = place_name;
        this.sName = sName;
        this.sNumber = sNumber;
        this.title = title;
        this.time = time;
        this.sId = sId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPlace_address() {
        return place_address;
    }

    public void setPlace_address(String place_address) {
        this.place_address = place_address;
    }

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public String getPlace_phone() {
        return place_phone;
    }

    public void setPlace_phone(String place_phone) {
        this.place_phone = place_phone;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getsNumber() {
        return sNumber;
    }

    public void setsNumber(String sNumber) {
        this.sNumber = sNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
