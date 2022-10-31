package com.hoangt3k56.dropbox.model;

import retrofit2.http.Body;

public class Folder {
    private Boolean autorename;
    private String path;

    public Folder(String path) {
        this.autorename = false;
        this.path = path;
    }

    public Boolean getAutorename() {
        return autorename;
    }

    public void setAutorename(Boolean autorename) {
        this.autorename = autorename;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
