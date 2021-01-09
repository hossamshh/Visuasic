package com.example.visuasic.model;


import android.app.Application;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.visuasic.model.DAOs.ColorsDAO;
import com.example.visuasic.model.DAOs.UserDAO;
import com.example.visuasic.model.Entities.ColorCommand;
import com.example.visuasic.model.Entities.User;
import com.example.visuasic.model.Helpers.DeleteAllColorCommands;
import com.example.visuasic.model.Helpers.DeleteColorCommand;
import com.example.visuasic.model.Helpers.InsertColorCommand;
import com.example.visuasic.model.Helpers.UpdateUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Repository {
    private static Repository instance = null;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;

    private UserDAO userDAO;
    private ColorsDAO colorsDAO;

    private FirebaseUser firebaseUser;

    private LiveData<User> localUser;
    private LiveData<List<ColorCommand>> colorCommands;

    private boolean updateColors = true;


    private Repository(Application application) {
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        ApplicationDatabase applicationDatabase = ApplicationDatabase.getDatabase(application);
        userDAO = applicationDatabase.userDAO();
        colorsDAO = applicationDatabase.colorsDAO();

        firebaseUser = firebaseAuth.getCurrentUser();

        localUser = userDAO.getUser();
        colorCommands = colorsDAO.getAllColors();

    }

    public static Repository getRepo(Application application) {
        if(instance == null){
            instance = new Repository(application);
            return instance;
        }
        return instance;
    }

    public void authenticateUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            firebaseUser = firebaseAuth.getCurrentUser();
                            checkRegistered();
                        }
                        else {
                            new UpdateUser(userDAO, new User("initial", false, false)).start();
                        }
                    }
                });
    }

    private void checkRegistered() {
        if(firebaseUser != null) {
            String uid = firebaseUser.getUid();
            DatabaseReference ref = database.getReference(uid);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean isRegistered = dataSnapshot.getValue() != null;
                    User user = new User(firebaseUser.getUid(), isRegistered, true);
                    new UpdateUser(userDAO, user).start();

                    if(isRegistered)
                        loadUserColors();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    if(firebaseUser != null) {
                        User user = new User(firebaseUser.getUid(), false, true);
                        new UpdateUser(userDAO, user).start();
                    }
                }
            });
        }
    }

    public LiveData<User> getUser() {
        return localUser;
    }

    public LiveData<List<ColorCommand>> getColorCommands() {
        return colorCommands;
    }

    public void registerUser(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        firebaseUser = firebaseAuth.getCurrentUser();
                        if(task.isSuccessful() && firebaseUser != null){
                            User user = new User(firebaseUser.getUid(), false, true);
                            new UpdateUser(userDAO, user).start();
                        }
                    }
                });
    }

    public void logout() {
        updateColors = true;

        firebaseUser = null;
        firebaseAuth.signOut();

        User user = localUser.getValue();
        new UpdateUser(userDAO, new User(user.getUid(), user.isRegistered(), false)).start();

        new DeleteAllColorCommands(colorsDAO).start();
    }

    public void setCurrentColor(int r, int g, int b) {
        String uid = firebaseUser.getUid();
        DatabaseReference ref = database.getReference(uid + "/rgb");

        String red = r == 0? "00" : r < 10? "0" + r : r + "";
        String green = g == 0? "00" : g < 10? "0" + g : g + "";
        String blue = b == 0? "00" : b < 10? "0" + b : b + "";

        ref.setValue(red + green + blue);
    }

    public void addColorCommand(ColorCommand colorCommand) {
        new InsertColorCommand(colorsDAO, colorCommand).start();

        String uid = firebaseUser.getUid();

        int r = Color.red(colorCommand.getRgb()) / 5;
        int g = Color.green(colorCommand.getRgb()) / 5;
        int b = Color.blue(colorCommand.getRgb()) / 5;

        String red = r == 0? "00" : r < 10? "0" + r : r + "";
        String green = g == 0? "00" : g < 10? "0" + g : g + "";
        String blue = b == 0? "00" : b < 10? "0" + b : b + "";
        String rgb =  red + green + blue;

        DatabaseReference ref = database.getReference(uid + "/colors/" + colorCommand.getID() + "/" + rgb);
        ref.setValue(colorCommand.getCommand());
    }

    public void deleteColor(ColorCommand colorCommand) {
        int ID = colorCommand.getID();
        new DeleteColorCommand(colorsDAO, colorCommand).start();

        String uid = firebaseUser.getUid();

        DatabaseReference ref = database.getReference(uid + "/colors/" + ID);
        ref.removeValue();
    }

    public void loadUserColors() {
        String uid = firebaseUser.getUid();

        DatabaseReference ref = database.getReference(uid + "/colors");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(updateColors) {
                    for(DataSnapshot data: dataSnapshot.getChildren()) {
                        try {
                            int ID = Integer.parseInt(data.getKey());

                            for(DataSnapshot colorC: data.getChildren()) {
                                String rgbText = colorC.getKey();
                                String command = colorC.getValue().toString();

                                int rgb = Integer.parseInt(rgbText);
                                int r = rgb / 10000;
                                rgb -= r*10000;
                                int g = rgb / 100;
                                int b = rgb - g*100;
                                int color = Color.rgb(r*5, g*5, b*5);

                                ColorCommand colorCommand = new ColorCommand(ID, color, command);
                                new InsertColorCommand(colorsDAO, colorCommand).start();
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    updateColors = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
