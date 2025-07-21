package dataAccess;

import com.google.firebase.Timestamp;

import java.time.Clock;
import java.time.Instant;

public abstract class UserData {
    private String title;
    private Long date;
    private String description;

    private String filePath;

    public UserData() {

    }
    public UserData(String title, String description) {
        this.title = title;
        Instant instant = Instant.now();
        date = instant.getEpochSecond() * 1000000000 + instant.getNano();
        this.description = description;
        generateFilePath();
    }

    public UserData(String title, String description, String filePath) {
        this.title = title;
        Instant instant = Instant.now();
        date = instant.getEpochSecond() * 1000000000 + instant.getNano();
        this.description = description;
        this.filePath = filePath;
    }

    public void generateFilePath() {
        filePath = String.valueOf(Math.abs(getDate().hashCode()));
    }

    public String getTitle() {
        return title;
    }

    public Long getDate() {
        return date;
    }
    public String getDescription() {
        return description;
    }

    public String getFilePath() {
        return filePath;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(Long date) {
        this.date = date;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "Title: " + title + "\nDescription: " + description;
    }
}
