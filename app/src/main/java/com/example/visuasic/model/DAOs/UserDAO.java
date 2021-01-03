package com.example.visuasic.model.DAOs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.visuasic.model.Entities.User;

import java.util.List;

@Dao
public interface UserDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertUser(User user);

    @Query("update user set uid = :uid, isRegistered = :isRegistered, isLoggedIn = :isLoggedIn")
    public void updateUser(String uid, boolean isRegistered, boolean isLoggedIn);

    @Query("select * from user limit 1")
    public LiveData<User> getUser();

    @Query("delete from user")
    public void deleteUsers();

}
