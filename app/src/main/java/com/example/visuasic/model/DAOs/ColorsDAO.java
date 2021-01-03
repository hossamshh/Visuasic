package com.example.visuasic.model.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.visuasic.model.Entities.ColorCommand;

import java.util.List;

@Dao
public interface ColorsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertColor(ColorCommand colorCommand);

    @Delete
    public void deleteColor(ColorCommand colorCommand);

    @Query("select * from color_commands")
    public LiveData<List<ColorCommand>> getAllColors();

    @Query("delete from color_commands")
    public void deleteColors();
}
