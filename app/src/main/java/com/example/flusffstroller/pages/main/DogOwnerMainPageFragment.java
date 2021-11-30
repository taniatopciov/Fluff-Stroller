package com.example.flusffstroller.pages.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.flusffstroller.R;
import com.example.flusffstroller.databinding.DogOwnerMainPageFragmentBinding;
import com.example.flusffstroller.di.Injectable;
import com.example.flusffstroller.di.ServiceLocator;
import com.example.flusffstroller.services.FeesService;
import com.example.flusffstroller.utils.formatting.CurrencyIntegerTextWatcher;
import com.example.flusffstroller.utils.formatting.TimeIntegerTextWatcher;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DogOwnerMainPageFragment extends Fragment {

    @Injectable
    private FeesService feesService;

    private DogOwnerMainPageViewModel viewModel;
    private DogOwnerMainPageFragmentBinding binding;

    public DogOwnerMainPageFragment() {
        ServiceLocator.getInstance().inject(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(DogOwnerMainPageViewModel.class);

        binding = DogOwnerMainPageFragmentBinding.inflate(inflater, container, false);

        final RecyclerView selectedDogsRecyclerView = binding.selectedDogsRecyclerView;

        binding.walkPriceEditText.addTextChangedListener(new CurrencyIntegerTextWatcher(binding.walkPriceEditText, "$", text -> {
            try {
                viewModel.setWalkPrice(Integer.parseInt(text));
            } catch (Exception e) {
                viewModel.setWalkPrice(0);
            }
        }));
        binding.walkTimeEditText.addTextChangedListener(new TimeIntegerTextWatcher(binding.walkTimeEditText, "minutes"));

        binding.feesEditText.addTextChangedListener(new CurrencyIntegerTextWatcher(binding.feesEditText, "$"));
        binding.totalPriceEditText.addTextChangedListener(new CurrencyIntegerTextWatcher(binding.totalPriceEditText, "$"));

        DogNamesAdapter dogNamesAdapter = new DogNamesAdapter(new ArrayList<>());
        selectedDogsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        selectedDogsRecyclerView.setAdapter(dogNamesAdapter);

        viewModel.getDogNames().observe(getViewLifecycleOwner(), dogNamesAdapter::setDogNames);

        viewModel.getFees().observe(getViewLifecycleOwner(), fees -> {
            binding.feesEditText.setText(fees.toString());


            MutableLiveData<Integer> walkPrice = viewModel.getWalkPrice();
            int totalPrice = fees;

            if (walkPrice != null && walkPrice.getValue() != null) {
                totalPrice += walkPrice.getValue();
            }

            binding.totalPriceEditText.setText(totalPrice + "");
        });

        viewModel.getWalkPrice().observe(getViewLifecycleOwner(), walkPrice -> {
            MutableLiveData<Integer> fees = viewModel.getFees();
            int totalPrice = walkPrice;

            if (fees != null && fees.getValue() != null) {
                totalPrice += fees.getValue();
            }

            binding.totalPriceEditText.setText(totalPrice + "");
        });

        // todo get names from database
        List<String> names = new ArrayList<>();
        names.add("John Dog");
        names.add("Jane Dog");
        viewModel.setDogNames(names);

        binding.findStrollerButton.setOnClickListener(view -> {
            List<String> checkedDogs = dogNamesAdapter.getCheckedDogs();

            if (!validateInputs() || checkedDogs.isEmpty()) {
                Snackbar.make(view, R.string.empty_required_fields, Snackbar.LENGTH_SHORT).show();
                return;
            }

            // todo create walk
            Snackbar.make(view, checkedDogs.stream().reduce("", (s, s2) -> s + " " + s2), Snackbar.LENGTH_SHORT).show();
        });

        viewModel.setFees(feesService.getDogWalkFees());

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