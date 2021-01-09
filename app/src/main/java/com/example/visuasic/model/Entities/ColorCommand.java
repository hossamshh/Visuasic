package com.example.visuasic.model.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "color_commands")
public class ColorCommand {
    @NonNull
    @PrimaryKey
    private int ID;

    @NonNull
    @ColumnInfo(name = "rgb")
    private int rgb;

    @NonNull
    @ColumnInfo(name = "command")
    private String command;

    public ColorCommand(@NonNull int ID, @NonNull int rgb, @NonNull String command) {
        this.ID = ID;
        this.rgb = rgb;
        this.command = command;
    }

    public int getID() {
        return ID;
    }

    @NonNull
    public int getRgb() {
        return rgb;
    }

    @NonNull
    public String getCommand() {
        return command;
    }
}
