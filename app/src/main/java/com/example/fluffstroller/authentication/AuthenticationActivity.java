package com.example.fluffstroller.authentication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fluffstroller.ServicesRegistration;
import com.example.fluffstroller.databinding.AuthenticationActivityBinding;
import com.example.fluffstroller.di.ServiceLocator;

public class AuthenticationActivity extends AppCompatActivity {

    private AuthenticationActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ServicesRegistration.getInstance().registerServices();

        binding = AuthenticationActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ServiceLocator.getInstance().dispose();
    }
}
