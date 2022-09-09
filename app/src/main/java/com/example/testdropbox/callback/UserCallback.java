package com.example.testdropbox.callback;

import com.dropbox.core.v2.users.FullAccount;

public interface UserCallback {
    void listener(FullAccount fullAccount);
}
