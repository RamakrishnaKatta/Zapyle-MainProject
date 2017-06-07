package models;

/**
 * Created by hitech on 11/7/15.
 */
public class CommentData {
    String commenttext;
    String username;
    String time;
    String imgResId;
    int commentId;
    String userId;

    public String getUserId(){return userId;}

    public void setUserId(String userId){
        this.userId = userId;
    }

    public int getCommentId(){return commentId;}

    public void setCommentId(int commentId){
        this.commentId = commentId;
    }

    public String getCommenttext() {
        return commenttext;
    }

    public void setCommenttext(String description) {
        commenttext = description;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String title) {
        this.username = title;
    }
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImgResId() {
        return imgResId;
    }

    public void setImgResId(String imgResId) {
        this.imgResId = imgResId;
    }
}
