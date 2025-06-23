package vn.edu.tlu.dinhcaothang.ezilish.models;

public class Message {
    public String senderId;
    public String receiverId;
    public String text;
    public long timestamp;

    public Message() {}

    public Message(String senderId, String receiverId, String text, long timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.text = text;
        this.timestamp = timestamp;
    }
}

