package com.hoangt3k56.dropbox.model;

import com.google.gson.annotations.SerializedName;

public class PathShare {
    private String path;
    @SerializedName("short_url")
    private Boolean shortUrl;

    public PathShare(String pathFile) {
        this.path = pathFile;
        this.shortUrl = false;
    }
}
