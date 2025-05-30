package com.example.fotnewsapp.ui.userinfo;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fotnewsapp.SignupActivity;
import com.example.fotnewsapp.databinding.FragmentUserinfoBinding;
import com.example.fotnewsapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserinfoFragment extends Fragment {

    private FragmentUserinfoBinding binding;
    private DatabaseReference userRef;
    private ValueEventListener userListener;

    private String currentUsername;
    private String currentEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserinfoBinding.inflate(inflater, container, false);

        // Get username from Activity Intent extras
        currentUsername = requireActivity().getIntent().getStringExtra("username");

        if (currentUsername == null || currentUsername.isEmpty()) {
            Toast.makeText(getContext(), "No user logged in!", Toast.LENGTH_SHORT).show();
            // Optional: handle no username situation
        } else {
            userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUsername);
            attachUserListener();
        }

        binding.editInfoButton.setOnClickListener(v -> showEditDialog());

        // Added Delete Account button handler
        binding.deleteAccountButton.setOnClickListener(v -> showDeleteDialog());

        return binding.getRoot();
    }

    private void attachUserListener() {
        // Remove existing listener if any (good practice)
        if (userListener != null) {
            userRef.removeEventListener(userListener);
        }

        userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    currentUsername = snapshot.child("username").getValue(String.class);
                    currentEmail = snapshot.child("email").getValue(String.class);

                    binding.usernameValue.setText(currentUsername != null ? currentUsername : "");
                    binding.emailValue.setText(currentEmail != null ? currentEmail : "");
                } else {
                    // No data found, clear fields
                    binding.usernameValue.setText("");
                    binding.emailValue.setText("");
                    Toast.makeText(getContext(), "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load user data.", Toast.LENGTH_SHORT).show();
            }
        };

        userRef.addValueEventListener(userListener);
    }

    private void showEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.edit_userinfo_dialog, null);
        builder.setView(dialogView);

        EditText usernameEdit = dialogView.findViewById(R.id.edit_username);
        EditText emailEdit = dialogView.findViewById(R.id.edit_email);
        EditText passwordEdit = dialogView.findViewById(R.id.edit_password);
        EditText confirmPasswordEdit = dialogView.findViewById(R.id.edit_confirm_password);

        Button okButton = dialogView.findViewById(R.id.btn_ok);
        Button cancelButton = dialogView.findViewById(R.id.btn_cancel);

        // Set current username and email
        usernameEdit.setText(currentUsername);
        emailEdit.setText(currentEmail);

        // Load current password from Firebase and show as **** masked
        userRef.child("password").get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                String currentPassword = snapshot.getValue(String.class);
                // Show masked password as stars
                if (currentPassword != null && !currentPassword.isEmpty()) {
                    passwordEdit.setText(currentPassword);  // Keep actual password for toggle
                } else {
                    passwordEdit.setText("");
                }
                confirmPasswordEdit.setText("");
            }
        });

        // Initialize password fields as hidden (masked) and add toggle functionality
        togglePasswordVisibility(passwordEdit);
        togglePasswordVisibility(confirmPasswordEdit);

        AlertDialog dialog = builder.create();

        okButton.setOnClickListener(v -> {
            String newUsername = usernameEdit.getText().toString().trim();
            String newEmail = emailEdit.getText().toString().trim();
            String newPassword = passwordEdit.getText().toString();
            String confirmPassword = confirmPasswordEdit.getText().toString();

            if (newUsername.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(getContext(), "Username and Email cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.isEmpty()) {
                if (newPassword.length() < 6) {
                    Toast.makeText(getContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (!newUsername.equals(currentUsername)) {
                // Check if the new username already exists
                FirebaseDatabase.getInstance().getReference("users").child(newUsername)
                        .get().addOnSuccessListener(existingSnapshot -> {
                            if (existingSnapshot.exists()) {
                                Toast.makeText(getContext(), "Username already exists!", Toast.LENGTH_SHORT).show();
                            } else {
                                // Copy data to new node
                                userRef.get().addOnSuccessListener(snapshot -> {
                                    if (snapshot.exists()) {
                                        FirebaseDatabase.getInstance().getReference("users")
                                                .child(newUsername)
                                                .setValue(snapshot.getValue())
                                                .addOnSuccessListener(unused -> {
                                                    DatabaseReference newRef = FirebaseDatabase.getInstance()
                                                            .getReference("users").child(newUsername);
                                                    newRef.child("username").setValue(newUsername);
                                                    newRef.child("email").setValue(newEmail);

                                                    // Update password if provided
                                                    if (!newPassword.isEmpty()) {
                                                        newRef.child("password").setValue(newPassword);
                                                    }

                                                    // Remove old node
                                                    userRef.removeValue();

                                                    // Remove listener and update reference
                                                    userRef.removeEventListener(userListener);
                                                    userRef = newRef;
                                                    currentUsername = newUsername;
                                                    currentEmail = newEmail;

                                                    // Reattach listener
                                                    attachUserListener();

                                                    // Notify and logout
                                                    Toast.makeText(getContext(), "Username changed. Please log in again.", Toast.LENGTH_LONG).show();
                                                    dialog.dismiss();
                                                    logoutUser();
                                                });
                                    }
                                });
                            }
                        });
            } else {
                // Username unchanged, update email and password only
                userRef.child("email").setValue(newEmail);

                if (!newPassword.isEmpty()) {
                    userRef.child("password").setValue(newPassword)
                            .addOnSuccessListener(unused -> {
                                // Logout after password update
                                Toast.makeText(getContext(), "Password updated. Please log in again.", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                                logoutUser();
                            });
                } else {
                    currentEmail = newEmail;
                    binding.emailValue.setText(newEmail);
                    Toast.makeText(getContext(), "Information updated", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    // Helper method to toggle password visibility on EditText with drawableEnd icon
    private void togglePasswordVisibility(EditText editText) {
        // Initially password is hidden
        editText.setTransformationMethod(PasswordTransformationMethod.getInstance());

        editText.setOnTouchListener(new View.OnTouchListener() {
            final int DRAWABLE_RIGHT = 2; // index of drawableEnd/right drawable

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // Toggle visibility
                        if (editText.getTransformationMethod() instanceof PasswordTransformationMethod) {
                            // Show password
                            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            // Change icon to eye open
                            editText.setCompoundDrawablesWithIntrinsicBounds(
                                    editText.getCompoundDrawables()[0], // left
                                    null,
                                    getResources().getDrawable(R.drawable.ic_baseline_visibility_off_24),
                                    null);
                        } else {
                            // Hide password
                            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            // Change icon to eye closed
                            editText.setCompoundDrawablesWithIntrinsicBounds(
                                    editText.getCompoundDrawables()[0], // left
                                    null,
                                    getResources().getDrawable(R.drawable.ic_baseline_visibility_off_24),
                                    null);
                        }
                        // Move cursor to the end
                        editText.setSelection(editText.getText().length());
                        return true;
                    }
                }
                return false;
            }
        });
    }




    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Delete user data from Firebase
                    userRef.removeValue().addOnSuccessListener(unused -> {
                        Toast.makeText(getContext(), "Account deleted successfully", Toast.LENGTH_LONG).show();
                        logoutUser();  // Navigate back to signup
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to delete account", Toast.LENGTH_SHORT).show();
                    });
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void logoutUser() {
        // Clear saved user info from SharedPreferences (adjust name/key to your app)
        requireActivity().getSharedPreferences("MyAppPrefs", getContext().MODE_PRIVATE)
                .edit()
                .remove("username")
                .apply();

        // Redirect to login/signup activity (replace SignupActivity.class with your actual login activity)
        Intent intent = new Intent(getContext(), SignupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        requireActivity().finish(); // Close current activity
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (userListener != null && userRef != null) {
            userRef.removeEventListener(userListener);
            userListener = null;
        }
        binding = null;
    }
}
