package com.example.flusffstroller.pages.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.flusffstroller.R;
import com.example.flusffstroller.databinding.DogOwnerMainPageFragmentBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DogOwnerMainPageFragment extends Fragment {

    private DogOwnerMainPageFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DogOwnerMainPageViewModel mViewModel = new ViewModelProvider(this).get(DogOwnerMainPageViewModel.class);

        binding = DogOwnerMainPageFragmentBinding.inflate(inflater, container, false);

        final RecyclerView selectedDogsRecyclerView = binding.selectedDogsRecyclerView;

        // todo add custom suffix or prefix ($, minutes)

        DogNamesAdapter dogNamesAdapter = new DogNamesAdapter(new ArrayList<>());
        selectedDogsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        selectedDogsRecyclerView.setAdapter(dogNamesAdapter);

        mViewModel.getDogNames().observe(getViewLifecycleOwner(), dogNamesAdapter::setDogNames);

        // todo get names from database
        List<String> names = new ArrayList<>();
        names.add("John Dog");
        names.add("Jane Dog");
        mViewModel.setDogNames(names);

        binding.findStrollerButton.setOnClickListener(view -> {
            if (!validateInputs()) {
                Snackbar.make(view, R.string.empty_required_fields, Snackbar.LENGTH_SHORT).show();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private boolean validateInputs() {
        return validateEditText(binding.walkPriceEditText) && validateEditText(binding.walkTimeEditText)
                && validateEditText(binding.feesEditText) && validateEditText(binding.totalPriceEditText);
    }

    private boolean validateEditText(EditText editText) {
        return !editText.getText().toString().isEmpty();
    }
}