package vn.edu.tlu.dinhcaothang.ezilish.activities;

public class VocabularyTopic {
    private String id;
    private String name;
    private int wordCount;

    public VocabularyTopic() {
    }

    public VocabularyTopic(String id, String name, int wordCount) {
        this.id = id;
        this.name = name;
        this.wordCount = wordCount;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getWordCount() {
        return wordCount;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }
}