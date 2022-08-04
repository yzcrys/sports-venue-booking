package com.example.cscb07.ui.elements.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.cscb07.R;
import com.example.cscb07.ui.stateholders.LoginViewModel;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import org.jetbrains.annotations.NotNull;

public class LoginScreen extends Fragment {
    private LoginViewModel loginViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.screen_login, container, false);
    }

    // TODO: Declare FirebaseAuth instance
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        NavController navController = Navigation.findNavController(view);

        Navigation.findNavController(view);
        // Email/password fields
        EditText editTextEmail = view.findViewById(R.id.editTextEmail);
        EditText editTextPassword = view.findViewById(R.id.editTextPassword);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        Button loginButton = view.findViewById(R.id.loginButton);
        TextView signupLink = view.findViewById((R.id.signupLink));

        loginButton.setOnClickListener(v -> {
            // Get email/pass as Strings
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();

            loginViewModel.signUp(email, password).observe(getViewLifecycleOwner(), login -> {
                if (login.success) {
                    navController.popBackStack();
                }
            });
        });
        signupLink.setOnClickListener(v -> navController.navigate(LoginScreenDirections.actionScreenLoginToScreenSignup()));


        // TODO: Init FirebaseAuth instance

        // Show snackbar whenever error message is updated
        loginViewModel.getErrorMessage().observe(getViewLifecycleOwner(), stringId -> {
            Snackbar.make(view, stringId, BaseTransientBottomBar.LENGTH_SHORT).show();
        });
    }
}
