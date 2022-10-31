package com.hoangt3k56.dropbox.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Entrie {
    @SerializedName(".tag")
    private String tag;
    private String name;
    @SerializedName("path_lower")
    private String pathLower;
    @SerializedName("path_display")
    private String pathDisplay;
    private String id;
    @SerializedName("client_modified")
    private Date clientModified;
    @SerializedName("server_modified")
    private Date serverModified;
    private String rev;
    private int size;
    @SerializedName("is_downloadable")
    private boolean isDownloadable;
    @SerializedName("content_hash")
    private String contentHash;

    public Entrie() {
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPathLower() {
        return pathLower;
    }

    public void setPathLower(String path_lower) {
        this.pathLower = path_lower;
    }

    public String getPathDisplay() {
        return pathDisplay;
    }

    public void setPathDisplay(String pathDisplay) {
        this.pathDisplay = pathDisplay;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getClientModified() {
        return clientModified;
    }

    public void setClientModified(Date clientModified) {
        this.clientModified = clientModified;
    }

    public Date getServerModified() {
        return serverModified;
    }

    public void setServerModified(Date serverModified) {
        this.serverModified = serverModified;
    }

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isDownloadable() {
        return isDownloadable;
    }

    public void setDownloadable(boolean downloadable) {
        isDownloadable = downloadable;
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }

    @Override
    public String toString() {
        return "Enty{" +
                "name='" + name + '\'' +
                ", path_display='" + pathDisplay + '\'' +
                '}';
    }
}
