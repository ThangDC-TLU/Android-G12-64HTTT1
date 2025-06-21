package vn.edu.tlu.dinhcaothang.ezilish.activities;

import java.util.List;

public class OxfordResponse {
    public List<Result> results;
    public static class Result {
        public List<LexicalEntry> lexicalEntries;
    }
    public static class LexicalEntry {
        public List<Entry> entries;
    }
    public static class Entry {
        public List<Pronunciation> pronunciations;
    }
    public static class Pronunciation {
        public String phoneticSpelling;
    }
}
