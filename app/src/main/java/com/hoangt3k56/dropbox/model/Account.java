package com.hoangt3k56.dropbox.model;

import com.google.gson.annotations.SerializedName;

public class Account {
    private String account_id;

    public Account(String account_id) {
        this.account_id = account_id;
    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }
}
