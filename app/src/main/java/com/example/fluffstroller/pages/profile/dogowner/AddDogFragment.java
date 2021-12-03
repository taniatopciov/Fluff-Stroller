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

public class AddDogFragment extends Fragment {

    private AddDogViewModel mViewModel;

    private AddDogFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = AddDogFragmentBinding.inflate(inflater, container, false);

        binding.saveButtonAddDogFragment.setOnClickListener(view -> {
            String name = binding.nameTextWithLabelAddDogFragment.getText();
            String breed = binding.breedTextWithLabelAddDogFragment.getText();
            String description = binding.descriptionTextWithLabelAddDogFragment.getText();

            Dog dog = new Dog(name, breed, description);
            AddDogFragmentDirections.FromAddDogToEdit addDogToEdit = AddDogFragmentDirections.fromAddDogToEdit(dog);
            Navigation.findNavController(view).navigate(addDogToEdit);
        });

        binding.uploadImageButtonAddDogFragment.setOnClickListener(view -> {
            Toast.makeText(this.getContext(), "upload image button",
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