package vn.edu.tlu.dinhcaothang.ezilish.models;

public class Conversation {
    private String userId;
    private String username;
    private String lastMessage;
    private String avatarBase64;

    public Conversation() {}

    public Conversation(String userId, String username, String lastMessage, String avatarBase64) {
        this.userId = userId;
        this.username = username;
        this.lastMessage = lastMessage;
        this.avatarBase64 = avatarBase64;
    }

    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getLastMessage() { return lastMessage; }
    public String getAvatarBase64() { return avatarBase64; }

    public void setUserId(String userId) { this.userId = userId; }
    public void setUsername(String username) { this.username = username; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
    public void setAvatarBase64(String avatarBase64) { this.avatarBase64 = avatarBase64;}
}
