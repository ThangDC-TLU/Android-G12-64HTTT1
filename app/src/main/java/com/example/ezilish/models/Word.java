package com.example.ezilish.models;

public class Word {
    private String word;
    private String meaning;
    private String example;
    private String pronunciation;

    public Word() {
    }

    public Word(String word, String meaning, String example, String pronunciation) {
        this.word = word;
        this.meaning = meaning;
        this.example = example;
        this.pronunciation = pronunciation;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }
}
