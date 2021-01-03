package com.example.visuasic.viewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.visuasic.model.Entities.User;
import com.example.visuasic.model.Repository;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.CountDownLatch;

public class AuthViewModel extends AndroidViewModel {
    private Repository repository;

    private LiveData<User> user;

    public AuthViewModel(Application application){
        super(application);
        repository = Repository.getRepo(application);
        user = repository.getUser();
    }

    public void authenticateUser(String email, String password) {
        repository.authenticateUser(email, password);
    }

    public void registerUser(String email, String password) {
        repository.registerUser(email, password);
    }

    public void logout() {
        repository.logout();
    }

    public LiveData<User> getUser() {
        return user;
    }
}
