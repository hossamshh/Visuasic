package com.example.visuasic.model;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.visuasic.model.DAOs.ColorsDAO;
import com.example.visuasic.model.DAOs.UserDAO;
import com.example.visuasic.model.Entities.ColorCommand;
import com.example.visuasic.model.Entities.User;
import com.example.visuasic.model.Helpers.InsertUser;

@Database(entities = {User.class, ColorCommand.class}, version = 3, exportSchema = false)
public abstract class ApplicationDatabase extends RoomDatabase {
    private static ApplicationDatabase instance;

    public abstract UserDAO userDAO();
    public abstract ColorsDAO colorsDAO();

    public static ApplicationDatabase getDatabase(final Context context) {
        if(instance == null) {
            synchronized (Database.class) {
                if(instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            ApplicationDatabase.class,
                            "database")
                            .fallbackToDestructiveMigration()
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    new InsertUser(instance.userDAO(), new User("initial", false, false)).start();
                                }
                            })
                            .build();
                }
            }
        }
        return instance;
    }

}


