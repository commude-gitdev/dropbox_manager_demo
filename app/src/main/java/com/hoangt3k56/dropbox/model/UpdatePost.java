package com.hoangt3k56.dropbox.model;

import com.google.gson.annotations.SerializedName;

public class UpdatePost {
    private String name;
    @SerializedName("path_lower")
    private String pathLower;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPathLower() {
        return pathLower;
    }

    public void setPathLower(String pathLower) {
        this.pathLower = pathLower;
    }
}
