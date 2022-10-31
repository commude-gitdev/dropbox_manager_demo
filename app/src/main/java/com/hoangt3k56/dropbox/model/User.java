package com.hoangt3k56.dropbox.model;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("profile_photo_url")
    private String profilePhotoUrl;
    private Name name;

    public User() {
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }
}
