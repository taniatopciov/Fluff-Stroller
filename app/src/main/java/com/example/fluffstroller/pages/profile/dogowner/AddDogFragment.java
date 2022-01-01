package com.example.fluffstroller.pages.profile.dogowner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.fluffstroller.databinding.AddDogFragmentBinding;
import com.example.fluffstroller.models.Dog;
import com.example.fluffstroller.utils.formatting.TimeIntegerTextWatcher;

public class AddDogFragment extends Fragment {

    private AddDogFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = AddDogFragmentBinding.inflate(inflater, container, false);
        binding.ageTextWithLabelAddDogFragment.addTextChangedListener(new TimeIntegerTextWatcher(binding.ageTextWithLabelAddDogFragment.editText, "years"));

        binding.saveButtonAddDogFragment.setOnClickListener(view -> {
            String name = binding.nameTextWithLabelAddDogFragment.getText();
            String breed = binding.breedTextWithLabelAddDogFragment.getText();
            int age = extractInteger(binding.ageTextWithLabelAddDogFragment.getText());
            String description = binding.descriptionTextWithLabelAddDogFragment.getText();

            Dog dog = new Dog(name, breed, age, description);
            AddDogFragmentDirections.ActionFromAddDogToEditOwnerProfile addDogToEdit = AddDogFragmentDirections.actionFromAddDogToEditOwnerProfile(dog);
            Navigation.findNavController(view).navigate(addDogToEdit);
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private int extractInteger(String input) {
        if (input == null || input.isEmpty()) {
            return 0;
        }

        try {
            String[] parts = input.split(" ");
            if (parts.length == 2) {
                return Integer.parseInt(parts[0]);
            }

        } catch (NumberFormatException e) {
            return 0;
        }

        return 0;
    }
}