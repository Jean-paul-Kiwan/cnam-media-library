package model;

import java.util.List;

public class VideoSession extends Media {
    private int durationMinutes;

    public VideoSession(int id, String title, String author, int year, String description, List<String> subjectCodes, int durationMinutes) {
        super(id, title, author, year, description, subjectCodes);
        this.durationMinutes = durationMinutes;
    }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    @Override
    public String getType() { return "Video"; }
}
