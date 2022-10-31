package com.hoangt3k56.dropbox.model;

public class Avatar {
    private Photo photo;

    public Avatar(Photo photo) {
        this.photo = photo;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }
}
