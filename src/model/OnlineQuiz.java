package model;

import java.util.List;

public class OnlineQuiz extends Media {
    private int estimatedMinutes;
    private String difficulty; // e.g. EASY/MEDIUM/HARD

    public OnlineQuiz(int id, String title, String author, int year, String description, List<String> subjectCodes,
                      int estimatedMinutes, String difficulty) {
        super(id, title, author, year, description, subjectCodes);
        this.estimatedMinutes = estimatedMinutes;
        this.difficulty = difficulty;
    }

    public int getEstimatedMinutes() { return estimatedMinutes; }
    public String getDifficulty() { return difficulty; }

    public void setEstimatedMinutes(int estimatedMinutes) { this.estimatedMinutes = estimatedMinutes; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    @Override
    public String getType() { return "Quiz"; }
}
