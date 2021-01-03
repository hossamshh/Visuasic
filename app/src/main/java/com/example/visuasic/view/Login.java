package com.example.visuasic.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.visuasic.R;
import com.example.visuasic.databinding.ActivityLoginBinding;
import com.example.visuasic.model.Entities.User;
import com.example.visuasic.viewModel.AuthViewModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {
    private static final String TAG = "dev";

    private ActivityLoginBinding binding;
    private AuthViewModel authViewModel;
    private boolean firstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViewModel();
        initUI();
    }

    private void initViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        authViewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if(user != null && user.isLoggedIn()){
                    Log.d(TAG, "login: ");
                    login(user);
                }
                else if(binding.emailInput.getText().length() > 0 && binding.passwordInput.getText().length() > 0 && !firstTime){
                    errorLogin();
                }
                firstTime = false;
            }
        });

    }

    private void initUI() {
        float width = binding.welcomText.getPaint().measureText(getText(R.string.text_Welcome)+"");
        Shader textShader = new LinearGradient(0, 0, width, binding.welcomText.getTextSize(),
                new int[]{
                        (getResources().getColor(R.color.gradient1)),
                        (getResources().getColor(R.color.gradient2)),
                        (getResources().getColor(R.color.gradient3)),
                        (getResources().getColor(R.color.gradient4)),
                        (getResources().getColor(R.color.gradient5)),
                        (getResources().getColor(R.color.gradient6)),
                }, null, Shader.TileMode.CLAMP);
        binding.welcomText.getPaint().setShader(textShader);

        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = formatEmail();
                String password = binding.passwordInput.getText()+"";
                disabledButtons();

                try {
                    if(validateEmail(email) && validatePassword(password)) {
                        authViewModel.authenticateUser(email, password);
                    }
                    else errorInputs();
                }
                catch (Exception e) {
                    errorInputs();
                }

                enableButtons();

            }
        });

        binding.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = formatEmail();
                String password = binding.passwordInput.getText()+"";
                disabledButtons();

                try {
                    if(validateEmail(email) && validatePassword(password)) {
                        authViewModel.registerUser(email, password);
                    }
                    else errorInputs();
                }
                catch (Exception e) {
                    errorInputs();
                }

                enableButtons();
            }
        });
    }

    private void login(User user) {
        Intent intent;
        if(user.isRegistered()) {
            intent = new Intent(Login.this, Home.class);
            intent.putExtra("UID", user.getUid());
        }
        else intent = new Intent(Login.this,  Unregistered.class);

        startActivity(intent);
    }

    private void errorInputs() {
        Toast.makeText(this, getText(R.string.error_inputs), Toast.LENGTH_SHORT).show();
    }

    private void errorLogin() {
        binding.passwordInput.setText("");
        Toast.makeText(this, getText(R.string.error_login), Toast.LENGTH_SHORT).show();
    }

    private String formatEmail() {
        return (binding.emailInput.getText() + "").replace(" ", "");
    }

    private boolean validateEmail(String email) {
        Pattern p = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
        Matcher m = p.matcher(email);

        return m.matches();
    }

    private boolean validatePassword(String password) {
        return password.length() > 0;
    }

    private void disabledButtons() {
        binding.loginButton.setEnabled(false);
        binding.registerButton.setEnabled(false);
    }

    private void enableButtons() {
        binding.loginButton.setEnabled(true);
        binding.registerButton.setEnabled(true);
    }

}