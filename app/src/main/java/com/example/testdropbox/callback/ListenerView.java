package com.example.testdropbox.callback;

import android.view.View;

import com.dropbox.core.v2.files.Metadata;

public interface ListenerView {
    void listener(View view, Metadata metadata);
}
