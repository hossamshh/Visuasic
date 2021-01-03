package com.example.visuasic.model.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "color_commands", primaryKeys = {"rgb", "command"})
public class ColorCommand {
    @NonNull
    @ColumnInfo(name = "rgb")
    private int rgb;

    @NonNull
    @ColumnInfo(name = "command")
    private String command;

    public ColorCommand(@NonNull int rgb, @NonNull String command) {
        this.rgb = rgb;
        this.command = command;
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
