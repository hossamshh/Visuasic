package com.example.visuasic.model;


import android.app.Application;
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
        firebaseUser = null;
        firebaseAuth.signOut();

        User user = localUser.getValue();
        new UpdateUser(userDAO, new User(user.getUid(), user.isRegistered(), false)).start();
    }

    public void setCurrentColor(int r, int g, int b) {
        String uid = firebaseUser.getUid();
        DatabaseReference ref = database.getReference(uid + "/rgb");
        String red = r == 0? "0" : r + "";
        String green = g == 0? "0" : g + "";
        String blue = b == 0? "0" : b + "";

        ref.setValue(red + green + blue);
    }

    public void addColorCommand(ColorCommand colorCommand) {
        new InsertColorCommand(colorsDAO, colorCommand).start();
    }

    public void deleteColor(ColorCommand colorCommand) {
        new DeleteColorCommand(colorsDAO, colorCommand).start();
    }
}