package com.hoangt3k56.dropbox.model;

public class Copy {
    private boolean allow_ownership_transfer;
    private boolean allow_shared_folder;
    private boolean autorename;
    private String from_path;
    private String to_path;

    public Copy(String from_path, String to_path) {
        this.allow_ownership_transfer = false;
        this.allow_shared_folder = false;
        this.autorename = false;
        this.from_path = from_path;
        this.to_path = to_path;
    }
}
