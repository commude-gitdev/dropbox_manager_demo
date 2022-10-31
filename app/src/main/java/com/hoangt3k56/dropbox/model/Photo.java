package com.hoangt3k56.dropbox.model;

import com.google.gson.annotations.SerializedName;

public class Photo {
    @SerializedName(".tag")
    private String tag;
    private String base64_data;

    public Photo(String base64_data) {
        this.tag = "base64_data";
        this.base64_data = base64_data;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getBase64_data() {
        return base64_data;
    }

    public void setBase64_data(String base64_data) {
        this.base64_data = base64_data;
    }
}
