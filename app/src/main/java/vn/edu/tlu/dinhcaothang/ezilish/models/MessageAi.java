package vn.edu.tlu.dinhcaothang.ezilish.models;

public class MessageAi {
    private String content;
    private boolean isUser;

    public MessageAi(String content, boolean isUser) {
        this.content = content;
        this.isUser = isUser;
    }

    public String getContent() {
        return content;
    }

    public boolean isUser() {
        return isUser;
    }
}

