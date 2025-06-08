package com.example.fotnewsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    EditText signupUsername, signupEmail, signupPassword, signupConfirmPassword;
    TextView loginRedirectText;
    Button signupButton;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupEmail = findViewById(R.id.signup_email);
        signupUsername = findViewById(R.id.signup_username);
        signupPassword = findViewById(R.id.signup_password);
        signupConfirmPassword = findViewById(R.id.signup_confirm_password);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        signupButton = findViewById(R.id.signup_button);
        CheckBox termsCheckbox = findViewById(R.id.termsCheckbox);

        // Highlight "Sign in" text
        String fullText = "Already an user? Sign in";
        SpannableString spannableString = new SpannableString(fullText);
        int start = fullText.indexOf("Sign in");
        int end = start + "Sign in".length();
        spannableString.setSpan(new ForegroundColorSpan(Color.rgb(24, 123, 205)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        loginRedirectText.setText(spannableString);

        // Disable button initially
        signupButton.setEnabled(false);
        signupButton.setAlpha(0.5f);

        termsCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            signupButton.setEnabled(isChecked);
            signupButton.setAlpha(isChecked ? 1f : 0.5f);
        });

        signupButton.setOnClickListener(view -> {
            database = FirebaseDatabase.getInstance();
            reference = database.getReference("users");

            String email = signupEmail.getText().toString().trim();
            String username = signupUsername.getText().toString().trim();
            String password = signupPassword.getText().toString().trim();
            String confirmPassword = signupConfirmPassword.getText().toString().trim();

            if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(SignupActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() ||
                    !(email.endsWith("@gmail.com") || email.endsWith("@yahoo.com") || email.endsWith("@outlook.com"))) {
                Toast.makeText(SignupActivity.this, "Invalid email format", Toast.LENGTH_SHORT).show();
                return;
            }


            if (!password.equals(confirmPassword)) {
                Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if username already exists
            reference.child(username).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    Toast.makeText(SignupActivity.this, "Username already taken", Toast.LENGTH_SHORT).show();
                } else {
                    // Check if email already exists
                    reference.get().addOnCompleteListener(emailTask -> {
                        if (emailTask.isSuccessful()) {
                            boolean emailExists = false;
                            for (DataSnapshot snapshot : emailTask.getResult().getChildren()) {
                                String existingEmail = snapshot.child("email").getValue(String.class);
                                if (existingEmail != null && email.equalsIgnoreCase(existingEmail)) {
                                    emailExists = true;
                                    break;
                                }
                            }

                            if (emailExists) {
                                Toast.makeText(SignupActivity.this, "Email already registered", Toast.LENGTH_SHORT).show();
                            } else {
                                HelperClass helperClass = new HelperClass(email, username, password);
                                reference.child(username).setValue(helperClass);

                                Toast.makeText(SignupActivity.this, "You have signed up successfully!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                            }
                        } else {
                            Toast.makeText(SignupActivity.this, "Failed to access database", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        });

        loginRedirectText.setOnClickListener(view -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}
