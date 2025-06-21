package vn.edu.tlu.dinhcaothang.ezilish.activities;

public class Topic {
    public String id;
    public String name;

    public Topic() {} // Bắt buộc cho Firebase

    public Topic(String id, String name) {
        this.id = id;
        this.name = name;
    }
}