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

import com.example.shifty.R;
import com.example.shifty.viewmodel.SignupViewModel;

public class SignupActivity extends AppCompatActivity {

    SignupViewModel signupViewModel;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        signupViewModel = new ViewModelProvider(this).get(SignupViewModel.class);


        //observer that will observe the error message if there is one.
        signupViewModel.getErrorMessage().observe(this, this::onError);
    }

    public void onSignupButtonClick(View view) {
        EditText emailEditText = findViewById(R.id.email);
        EditText passwordEditText = findViewById(R.id.password);
        EditText referalCodeEditText = findViewById(R.id.referalCode);
        EditText usernameEditText = findViewById(R.id.email);

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String referalCode = referalCodeEditText.getText().toString();
        String username = usernameEditText.getText().toString();

        signupViewModel.signUp(username, email, password, referalCode);

        signupViewModel.getSignInStatus().observe(this, isSuccess -> {
            if (isSuccess) {
                //todo intent to the main page based on role and work from there.
            }
        });
    }



    private void onError(String message){
        Toast.makeText(SignupActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}