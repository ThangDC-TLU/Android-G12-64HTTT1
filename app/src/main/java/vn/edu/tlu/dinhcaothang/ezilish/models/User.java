package vn.edu.tlu.dinhcaothang.ezilish.models;

public class User {
    public String id;
    public String username;
    public String email;
    public double latitude;
    public double longitude;

    public User() {}

    public User(String id, String username, String email, double latitude, double longitude) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
