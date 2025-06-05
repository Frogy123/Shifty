package com.example.shifty.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.shifty.ShiftyApplication;
import com.example.shifty.model.CurrentUserManager;
import com.example.shifty.model.RefferalCodesManager;
import com.example.shifty.viewmodel.SignupViewModel;

/**
 * Activity for user registration (sign-up) screen in the Shifty app.
 * <p>
 * This screen allows new users to register an account using their email, password, referral code, and username.
 * It connects the UI to the signup business logic via {@link SignupViewModel} (MVVM pattern) and observes LiveData
 * for asynchronous sign-up results and error handling.
 * </p>
 *
 * <h3>Key Features:</h3>
 * <ul>
 *     <li>Handles user input validation for sign-up fields</li>
 *     <li>Triggers sign-up via {@link SignupViewModel}</li>
 *     <li>Observes sign-up result and displays appropriate feedback</li>
 *     <li>Returns email/password to the calling activity upon success</li>
 *     <li>Provides a login button to return to login screen</li>
 * </ul>
 *
 * @author Eitan Navon
 * @see SignupViewModel
 * @see com.example.shifty.model.User
 */
public class SignupActivity extends AppCompatActivity {

    /** ViewModel managing sign-up logic and LiveData. */
    SignupViewModel signupViewModel;
    /** Stores user email for result return. */
    String email;
    /** Stores user password for result return. */
    String password;

    /**
     * Lifecycle method called when the activity is created.
     * Initializes UI components, sets up MVVM observers, and prepares to handle sign-up feedback.
     *
     * @param savedInstanceState Previous state (unused in this activity).
     */
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

        // Observe error messages from ViewModel
        signupViewModel.getErrorMessage().observe(this, this::onError);

        // Observe sign-in status to know if sign-up succeeded or failed
        ShiftyApplication.signInStatus.observe(this, isSuccess -> {
            ShiftyApplication.signInStatus.removeObservers(this);
            Intent resultIntent = new Intent();

            if (isSuccess) {
                resultIntent.putExtra("email", email);
                resultIntent.putExtra("password", password);
                setResult(RESULT_OK, resultIntent);
            } else {
                Toast toast = Toast.makeText(SignupActivity.this, "Sign up failed", Toast.LENGTH_SHORT);
                setResult(RESULT_CANCELED, resultIntent);
                toast.show();
            }
            try {
                finish();
            } catch (Exception e) {
                Log.d("SignupActivity", "Error finishing activity: " + e.getMessage());
            }
        });
    }

    /**
     * Handles the sign-up button click event.
     * Collects user input and invokes {@link SignupViewModel#signUp(String, String, String, String)} to start sign-up.
     *
     * @param view The view that was clicked (the sign-up button).
     */
    public void onSignupButtonClick(View view) {
        EditText emailEditText = findViewById(R.id.email);
        EditText passwordEditText = findViewById(R.id.password);
        EditText referalCodeEditText = findViewById(R.id.referalCode);
        EditText usernameEditText = findViewById(R.id.username);

        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();
        String referalCode = referalCodeEditText.getText().toString();
        String username = usernameEditText.getText().toString();

        signupViewModel.signUp(username, email, password, referalCode);
    }

    /**
     * Displays an error message as a toast if sign-up fails or input is invalid.
     *
     * @param message The error message to display.
     */
    private void onError(String message) {
        Toast.makeText(SignupActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Handles the login button click event (user wants to return to login instead of signing up).
     * Cancels the sign-up process and finishes the activity.
     *
     * @param view The view that was clicked (the login button).
     */
    public void onLoginButtonClick(View view) {
        Intent resultIntent = new Intent();
        setResult(RESULT_CANCELED, resultIntent);
        finish();
    }
}
