package vn.edu.tlu.dinhcaothang.ezilish.models;

public class Word {
    private String id;
    private String english;
    private String phonetic;
    private String meaning;
    private String example;
    private String explanation;
    private String topic_id;

    // Thêm thuộc tính favorite
    private boolean favorite = false;

    public Word() {}

    public Word(String id, String english, String phonetic, String meaning, String example, String explanation, String topic_id) {
        this.id = id;
        this.english = english;
        this.phonetic = phonetic;
        this.meaning = meaning;
        this.example = example;
        this.explanation = explanation;
        this.topic_id = topic_id;
    }

    // Getter, setter mặc định...

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    // Các getter/setter khác giữ nguyên
    public String getId() { return id; }
    public String getEnglish() { return english; }
    public String getPhonetic() { return phonetic; }
    public String getMeaning() { return meaning; }
    public String getExample() { return example; }
    public String getExplanation() { return explanation; }
    public String getTopic_id() { return topic_id; }
    public void setId(String id) { this.id = id; }
    public void setEnglish(String english) { this.english = english; }
    public void setPhonetic(String phonetic) { this.phonetic = phonetic; }
    public void setMeaning(String meaning) { this.meaning = meaning; }
    public void setExample(String example) { this.example = example; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    public void setTopic_id(String topic_id) { this.topic_id = topic_id; }
}