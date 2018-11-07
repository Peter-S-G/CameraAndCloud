package com.petergeras.cameraandcloud.inApp;

public class UploadInfoWithKey {

    private String key;
    private UploadInfo value;

    public UploadInfoWithKey(String key, UploadInfo value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public UploadInfo getValue() {
        return value;
    }

    public void setValue(UploadInfo value) {
        this.value = value;
    }
}
