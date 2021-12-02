package com.example.fluffstroller.authentication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fluffstroller.databinding.AuthenticationActivityBinding;

public class AuthenticationActivity extends AppCompatActivity {

    private AuthenticationActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = AuthenticationActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
    }
}
