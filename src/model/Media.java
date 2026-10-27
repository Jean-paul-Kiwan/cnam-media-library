package model;

import java.io.Serializable;
import java.util.List;

public abstract class Media implements Serializable {
    private final int id;
    private String title;
    private String author;
    private int year;
    private String description;

    // Subject codes this media is about (e.g., NFP121, NFA035)
    private final List<String> subjectCodes;

    protected Media(int id, String title, String author, int year, String description, List<String> subjectCodes) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.description = description;
        this.subjectCodes = subjectCodes;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getYear() { return year; }
    public String getDescription() { return description; }
    public List<String> getSubjectCodes() { return subjectCodes; }

    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setYear(int year) { this.year = year; }
    public void setDescription(String description) { this.description = description; }

    public abstract String getType();
}
