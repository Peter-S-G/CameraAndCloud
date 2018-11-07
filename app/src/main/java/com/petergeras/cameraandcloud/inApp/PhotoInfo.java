package com.petergeras.cameraandcloud.inApp;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class PhotoInfo {


    private ImageView selectedImageView;
    private ImageView likeBtn;
    private ImageView commentBtn;

    private TextView likeCount;
    private TextView commentCount;
    private String date;

    private EditText commentET;

    private String email;
    private long likeCountValue;
    private Boolean liked;

    private long commentCountValue;



    public ImageView getSelectedImageView() {
        return selectedImageView;
    }

    public void setSelectedImageView(ImageView selectedImageView) {
        this.selectedImageView = selectedImageView;
    }

    public ImageView getLikeBtn() {
        return likeBtn;
    }

    public void setLikeBtn(ImageView likeBtn) {
        this.likeBtn = likeBtn;
    }

    public ImageView getCommentBtn() {
        return commentBtn;
    }

    public void setCommentBtn(ImageView commentBtn) {
        this.commentBtn = commentBtn;
    }


    public TextView getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(TextView likeCount) {
        this.likeCount = likeCount;
    }


    public TextView getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(TextView commentCount) {
        this.commentCount = commentCount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public EditText getCommentET() {
        return commentET;
    }

    public void setCommentET(EditText commentET) {
        this.commentET = commentET;
    }

    public long getCommentCountValue() {
        return commentCountValue;
    }

    public void setCommentCountValue(long commentCountValue) {
        this.commentCountValue = commentCountValue;
    }

    public long getLikeCountValue() {
        return likeCountValue;
    }

    public void setLikeCountValue(long likeCountValue) {
        this.likeCountValue = likeCountValue;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getLiked() {
        return liked;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }
}
