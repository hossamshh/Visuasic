package com.example.visuasic.model.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
public class User {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "uid")
    private String uid;

    @ColumnInfo(name = "isRegistered")
    private boolean isRegistered;

    @ColumnInfo(name = "isLoggedIn")
    private boolean isLoggedIn;

    public User(@NonNull String uid, boolean isRegistered, boolean isLoggedIn){
        this.uid = uid;
        this.isRegistered = isRegistered;
        this.isLoggedIn = isLoggedIn;
    }

    @NonNull
    public String getUid() {
        return uid;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }
}
