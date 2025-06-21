package vn.edu.tlu.dinhcaothang.ezilish.models;

public class Conversation {
    private String userId;
    private String username;
    private String lastMessage;

    public Conversation() {}

    public Conversation(String userId, String username, String lastMessage) {
        this.userId = userId;
        this.username = username;
        this.lastMessage = lastMessage;
    }

    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getLastMessage() { return lastMessage; }

    public void setUserId(String userId) { this.userId = userId; }
    public void setUsername(String username) { this.username = username; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
}
