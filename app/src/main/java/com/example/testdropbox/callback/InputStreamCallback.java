package com.example.testdropbox.callback;

import com.dropbox.core.v2.files.Metadata;

import java.io.InputStream;
import java.util.List;

public interface InputStreamCallback {
    void listener(InputStream inputStream);
}

