package com.petergeras.cameraandcloud.inApp;

import java.util.Map;

public class UploadInfo {

    private String name;
    private String uri;
    private String publish_by;
    private Long date;
    private String email;


    private Map<String, Boolean> likes;

    private Map<String, Comment> comments;



    public UploadInfo() {
        // empty constructor needed
    }



    public UploadInfo(String name, String uri, String publish_by, Long date, String email) {

        this.name = name;
        this.uri = uri;
        this.publish_by = publish_by;
        this.date = date;
        this.email = email;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getPublish_by() {
        return publish_by;
    }

    public void setPublish_by(String publish_by) {
        this.publish_by = publish_by;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Map<String, Boolean> getLikes() {
        return likes;
    }

    public void setLikes(Map<String, Boolean> likes) {
        this.likes = likes;
    }


    public Map<String, Comment> getComments() {
        return comments;
    }

    public void setComments(Map<String, Comment> comments) {
        this.comments = comments;
    }
}
