package com.example.shifty.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import com.example.shifty.ShiftyApplication;
import com.example.shifty.model.CurrentUserManager;
import com.example.shifty.viewmodel.LoginViewModel;
import com.example.shifty.R;

import com.example.shifty.model.Role;
public class LoginActivity extends AppCompatActivity {

    LoginViewModel loginViewModel;

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
        loginViewModel.getErrorMessage().observe(this, errorMsg -> {
            if (errorMsg != null) {
                Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onLoginButtonClick(View view) {
        EditText usernameEditText = findViewById(R.id.email);
        EditText passwordEditText = findViewById(R.id.password);
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        loginViewModel.signIn(username, password);

        ShiftyApplication.signInStatus.observe(this, isSuccess -> {
            if (isSuccess) {
                Toast.makeText(LoginActivity.this, "Sign-in 'worked'. Please try again." + CurrentUserManager.getInstance().getUser().getUid(), Toast.LENGTH_SHORT).show();
                Intent intent;
                switch (CurrentUserManager.getInstance().getUser().getRole()) {
                    case EMPLOYEE:
                        intent = new Intent(this, EmployeeActivity.class);
                        break;
                    /*case "manager":
                        intent = new Intent(this, ManagerActivity.class);
                        break;
                    default:
                        intent = new Intent(this, LoginActivity.class);
                        break;*/
                    default:
                        intent = new Intent(this, EmployeeActivity.class);
                        break;
                }
                startActivity(intent);
                finish();

            }
        });
    }

    public void onSignupButtonClick(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
        finish(); //closes the loginActivity
    }

}