package com.back;

public class Writer {
    private int idx;
    private String name;
    private String word;

    public Writer(int idx, String word, String name) {
        this.idx = idx;
        this.word = word;
        this.name = name;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}