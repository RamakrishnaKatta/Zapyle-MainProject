package models;

/**
 * Created by haseeb on 10/9/16.
 */
public class TrackerModel {
    String title,Description ,date;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return Description;
    }

    public String getDate() {
        return date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
