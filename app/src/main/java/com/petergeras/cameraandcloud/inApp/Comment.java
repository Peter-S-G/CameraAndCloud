package com.petergeras.cameraandcloud.inApp;

public class Comment {

    private String uid;
    private String email;
    private String comment;
    private Long date;


    public Comment () {
        // empty constructor
    }

    public Comment(String uid, String email, String comment, Long date) {
        this.uid = uid;
        this.email = email;
        this.comment = comment;
        this.date = date;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getDate() {
        return date;
    }


    public void setDate(Long date) {
        this.date = date;
    }
}
