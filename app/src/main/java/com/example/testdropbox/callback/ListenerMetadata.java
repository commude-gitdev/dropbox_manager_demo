package com.example.testdropbox.callback;

import com.dropbox.core.v2.files.Metadata;

public interface ListenerMetadata {
    void listener(Metadata metadata);
}
