package com.example.fotnewsapp.ui.userinfo;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
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
        Button okButton = dialogView.findViewById(R.id.btn_ok);
        Button cancelButton = dialogView.findViewById(R.id.btn_cancel);

        usernameEdit.setText(currentUsername);
        emailEdit.setText(currentEmail);

        AlertDialog dialog = builder.create();

        okButton.setOnClickListener(v -> {
            String newUsername = usernameEdit.getText().toString().trim();
            String newEmail = emailEdit.getText().toString().trim();

            if (newUsername.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(getContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                return;
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
                                                    logoutUser();  // <-- LOGOUT USER HERE
                                                });
                                    }
                                });
                            }
                        });
            } else {
                // Username unchanged, update email only
                userRef.child("email").setValue(newEmail).addOnSuccessListener(unused -> {
                    currentEmail = newEmail;
                    binding.emailValue.setText(newEmail);
                    Toast.makeText(getContext(), "Information updated", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });
            }
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
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
