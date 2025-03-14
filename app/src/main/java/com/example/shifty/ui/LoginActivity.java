package com.example.shifty.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.shifty.viewmodel.LoginViewModel;
import com.example.shifty.R;

public class LoginActivity extends AppCompatActivity {

    LoginViewModel loginViewModel;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
    }

    public void onLoginButtonClick(View view) {
        EditText usernameEditText = findViewById(R.id.email);
        EditText passwordEditText = findViewById(R.id.password);
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        loginViewModel.signIn(username, password);

        loginViewModel.getSignInStatus().observe(this, isSuccess -> {
            if (isSuccess) {
                //todo intent to the main page based on role and work from there.
            }else{
                Toast.makeText(LoginActivity.this, "Sign-in failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}