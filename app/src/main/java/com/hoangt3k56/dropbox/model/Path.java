package com.hoangt3k56.dropbox.model;

import com.google.gson.Gson;

public class Path {
    public boolean include_deleted;
    public boolean include_has_explicit_shared_members;
    public boolean include_media_info;
    public boolean include_mounted_folders;
    public boolean include_non_downloadable_files;
    public String path;
    public boolean recursive;

    public Path(String path) {
        this.include_deleted = false;
        this.include_has_explicit_shared_members = false;
        this.include_media_info = false;
        this.include_mounted_folders = true;
        this.include_non_downloadable_files = true;
        this.path = path;
        this.recursive = false;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public boolean isInclude_deleted() {
        return include_deleted;
    }

    public void setInclude_deleted(boolean include_deleted) {
        this.include_deleted = include_deleted;
    }

    public boolean isInclude_has_explicit_shared_members() {
        return include_has_explicit_shared_members;
    }

    public void setInclude_has_explicit_shared_members(boolean include_has_explicit_shared_members) {
        this.include_has_explicit_shared_members = include_has_explicit_shared_members;
    }

    public boolean isInclude_media_info() {
        return include_media_info;
    }

    public void setInclude_media_info(boolean include_media_info) {
        this.include_media_info = include_media_info;
    }

    public boolean isInclude_mounted_folders() {
        return include_mounted_folders;
    }

    public void setInclude_mounted_folders(boolean include_mounted_folders) {
        this.include_mounted_folders = include_mounted_folders;
    }

    public boolean isInclude_non_downloadable_files() {
        return include_non_downloadable_files;
    }

    public void setInclude_non_downloadable_files(boolean include_non_downloadable_files) {
        this.include_non_downloadable_files = include_non_downloadable_files;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }
}
