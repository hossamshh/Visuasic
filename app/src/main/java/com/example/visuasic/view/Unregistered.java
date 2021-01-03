package com.example.visuasic.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.visuasic.R;
import com.example.visuasic.databinding.ActivityUnregisteredBinding;
import com.example.visuasic.model.Entities.User;
import com.example.visuasic.viewModel.AuthViewModel;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.CountDownLatch;

public class Unregistered extends AppCompatActivity {
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityUnregisteredBinding binding;
        binding = ActivityUnregisteredBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        authViewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if(!user.isLoggedIn())
                    logout();
            }
        });

        binding.logoutButton.setText(" " + getText(R.string.button_logout));
        binding.logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authViewModel.logout();
            }
        });

    }

    private void logout() {
        Intent intent = new Intent(Unregistered.this, Login.class);
        startActivity(intent);
    }

}