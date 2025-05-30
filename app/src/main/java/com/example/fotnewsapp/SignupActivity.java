package com.example.fotnewsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

        // 🔵 Highlight "Sign in" in blue color
        String fullText = "Already an user? Sign in";
        SpannableString spannableString = new SpannableString(fullText);
        int start = fullText.indexOf("Sign in");
        int end = start + "Sign in".length();
        spannableString.setSpan(new ForegroundColorSpan(Color.rgb(24, 123, 205)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        loginRedirectText.setText(spannableString);

        // Initially disable button
        signupButton.setEnabled(false);
        signupButton.setAlpha(0.5f);

        // Enable only when checkbox is checked
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

            if (!password.equals(confirmPassword)) {
                Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            HelperClass helperClass = new HelperClass(email, username, password);
            reference.child(username).setValue(helperClass);

            Toast.makeText(SignupActivity.this, "You have signed up successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        loginRedirectText.setOnClickListener(view -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}
