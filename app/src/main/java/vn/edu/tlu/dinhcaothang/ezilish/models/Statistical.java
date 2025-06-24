package vn.edu.tlu.dinhcaothang.ezilish.models;

public class Statistical {
    private String topicId;
    private String topicName;
    private int wordsLearned;
    private int totalWords;
    private int score;

    public Statistical() {}

    public Statistical(String topicId, String topicName, int wordsLearned, int totalWords, int score) {
        this.topicId = topicId;
        this.topicName = topicName;
        this.wordsLearned = wordsLearned;
        this.totalWords = totalWords;
        this.score = score;
    }

    public String getTopicId() {
        return topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public int getWordsLearned() {
        return wordsLearned;
    }

    public int getTotalWords() {
        return totalWords;
    }

    public int getScore() {
        return score;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public void setWordsLearned(int wordsLearned) {
        this.wordsLearned = wordsLearned;
    }

    public void setTotalWords(int totalWords) {
        this.totalWords = totalWords;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
