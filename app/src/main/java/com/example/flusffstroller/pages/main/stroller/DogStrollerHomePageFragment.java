package com.example.flusffstroller.pages.main.stroller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.flusffstroller.R;
import com.example.flusffstroller.databinding.DogStrollerHomePageFragmentBinding;
import com.google.android.material.tabs.TabLayoutMediator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class DogStrollerHomePageFragment extends Fragment {

    private DogStrollerHomePageViewModel viewModel;
    private DogStrollerHomePageFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DogStrollerHomePageFragmentBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(this).get(DogStrollerHomePageViewModel.class);

        StrollerViewPagerAdapter viewPagerAdapter = new StrollerViewPagerAdapter(requireActivity());
        viewPagerAdapter.addFragment(new DogStrollerHomePageMapFragment());
        viewPagerAdapter.addFragment(new DogStrollerHomePageListFragment());

        binding.strollerViewPager.setAdapter(viewPagerAdapter);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new TabLayoutMediator(binding.strollerTabLayout, binding.strollerViewPager, (tab, position) -> {
            switch (position) {
                case 0: {
                    tab.setIcon(R.drawable.ic_baseline_map_24);
                    tab.setText("Map");
                }
                break;

                case 1: {
                    tab.setIcon(R.drawable.ic_baseline_list_24);
                    tab.setText("Available Walks");
                }
                break;
            }
        }).attach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}