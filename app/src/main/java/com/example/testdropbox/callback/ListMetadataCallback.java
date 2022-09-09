package com.example.testdropbox.callback;

import com.dropbox.core.v2.files.Metadata;

import java.util.List;

public interface ListMetadataCallback {
    void listener(List<Metadata> metadata);
}
