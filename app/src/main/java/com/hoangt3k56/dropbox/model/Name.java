package com.hoangt3k56.dropbox.model;

import com.google.gson.annotations.SerializedName;

public class Name {
    @SerializedName("abbreviated_name")
    private String abbreviatedName;
    @SerializedName("display_name")
    private String displayName;
    @SerializedName("familiar_name")
    private String familiarName;
    @SerializedName("given_name")
    private String givenName;
    private String surname;

    public Name() {
    }

    public String getAbbreviatedName() {
        return abbreviatedName;
    }

    public void setAbbreviatedName(String abbreviatedName) {
        this.abbreviatedName = abbreviatedName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFamiliarName() {
        return familiarName;
    }

    public void setFamiliarName(String familiarName) {
        this.familiarName = familiarName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
