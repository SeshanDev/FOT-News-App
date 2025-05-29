package com.example.fotnewsapp.ui.devinfo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fotnewsapp.databinding.FragmentDevinfoBinding;

public class DevinfoFragment extends Fragment {

    private FragmentDevinfoBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout using ViewBinding
        binding = FragmentDevinfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set values for name and reg number
        binding.usernameLabel.setText("W.M.S. Nethmika.");
        binding.emailLabel.setText("2020t00919");

        // Optionally set title or version
        binding.versionText.setText("Version 1.0");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
