package com.example.shifty.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.shifty.ShiftyApplication;
import com.example.shifty.model.CurrentUserManager;
import com.example.shifty.model.Employee;
import com.example.shifty.model.User;
import com.example.shifty.viewmodel.LoginViewModel;
import com.example.shifty.R;

import com.example.shifty.model.Role;

/**
 * Activity for handling user login and navigation to signup or main screens.
 * <p>
 * This activity authenticates the user, handles login and signup flows,
 * observes login errors, and navigates to the main activity based on user role.
 * </p>
 * <b>Related:</b>
 * <ul>
 *     <li>{@link LoginViewModel}</li>
 *     <li>{@link SignupActivity}</li>
 *     <li>{@link AdminActivity}</li>
 *     <li>{@link EmployeeActivity}</li>
 *     <li>{@link CurrentUserManager}</li>
 * </ul>
 *
 * @author Eitan Navon
 */
public class LoginActivity extends AppCompatActivity {

    /** ViewModel for login logic and state. */
    LoginViewModel loginViewModel;
    /** Input for the username or email. */
    EditText usernameEditText;
    /** Input for the password. */
    EditText passwordEditText;
    /** Launcher for starting the signup activity and receiving the result. */
    ActivityResultLauncher<Intent> signupLauncher;
    /** Flag to indicate if a user should be created (currently unused). */
    boolean needToCreateUser = false;

    /**
     * Called when the activity is starting. Initializes UI, sets up ViewModel and result handlers.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
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

        // Observe error messages from ViewModel
        loginViewModel.getErrorMessage().observe(this, errorMsg -> {
            if (errorMsg != null) {
                Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        });

        // Launcher for the signup activity
        signupLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK &&  result.getData() != null) {
                        String email = result.getData().getStringExtra("email");
                        String password = result.getData().getStringExtra("password");
                        String username = result.getData().getStringExtra("username");
                        Role role = Role.EMPLOYEE;

                        User user = new User(null, username, email, password, role);
                        CurrentUserManager.getInstance().signIn(user);

                        usernameEditText = findViewById(R.id.email);
                        passwordEditText = findViewById(R.id.password);

                        usernameEditText.setText(email);
                        passwordEditText.setText(password);

                        Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    /**
     * Handles the login button click event.
     * Attempts to log the user in with the provided credentials using the ViewModel,
     * observes sign-in status, and navigates to the appropriate main screen based on user role.
     *
     * @param view The login button view that was clicked.
     * @see EmployeeActivity
     * @see AdminActivity
     * @see LoginViewModel#signIn(String, String)
     */
    public void onLoginButtonClick(View view) {
        usernameEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        loginViewModel.signIn(username, password);

        ShiftyApplication.signInStatus.observe(this, isSuccess -> {
            if (isSuccess) {
                Toast.makeText(LoginActivity.this, "Sign-in 'worked'." + CurrentUserManager.getInstance().getUser().getUid(), Toast.LENGTH_SHORT).show();
                Intent intent;
                switch (CurrentUserManager.getInstance().getUser().getRole()) {
                    case EMPLOYEE:
                        intent = new Intent(this, EmployeeActivity.class);
                        break;
                    case ADMIN:
                        intent = new Intent(this, AdminActivity.class);
                        break;
                    /*default:
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

    /**
     * Handles the signup button click event. Starts the {@link SignupActivity} for a result.
     *
     * @param view The signup button view that was clicked.
     * @see SignupActivity
     */
    public void onSignupButtonClick(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        signupLauncher.launch(intent);
    }

}
