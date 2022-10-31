package com.hoangt3k56.dropbox.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class Arg {
    private boolean autorename;
    private String mode;
    private boolean mute;
    private String path;
    private boolean strict_conflict;

    public Arg(String path) {
        this.autorename = false;
        this.mode = "add";
        this.mute = false;
        this.path = path;
        this.strict_conflict = false;
    }

    public String getPath() {
        return path;
    }


    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
