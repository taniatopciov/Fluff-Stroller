package com.example.fluffstroller.pages.profile.dogstroller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fluffstroller.databinding.ViewStrollerProfileFragmentBinding;

public class ViewStrollerProfileFragment extends Fragment {

    private StrollerProfileViewModel mViewModel;

    private ViewStrollerProfileFragmentBinding binding;

    public static ViewStrollerProfileFragment newInstance() {
        return new ViewStrollerProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = ViewStrollerProfileFragmentBinding.inflate(inflater, container, false);

        binding.editProfileButtonViewStrollerProfile.setOnClickListener(view -> {
            Toast.makeText(this.getContext(), "edit button",
                    Toast.LENGTH_LONG).show();
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}