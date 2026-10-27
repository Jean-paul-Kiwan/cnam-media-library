package model;

import java.util.List;

public class Document extends Media {
    private int pages;

    public Document(int id, String title, String author, int year, String description, List<String> subjectCodes, int pages) {
        super(id, title, author, year, description, subjectCodes);
        this.pages = pages;
    }

    public int getPages() { return pages; }
    public void setPages(int pages) { this.pages = pages; }

    @Override
    public String getType() { return "Document"; }
}
