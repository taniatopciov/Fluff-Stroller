package com.example.fluffstroller.pages.main.dogowner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.example.fluffstroller.R;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DogNamesAdapter extends RecyclerView.Adapter<DogNamesAdapter.ViewHolder> {

    private List<CheckableDogName> dogNames;

    public DogNamesAdapter(List<String> dogNames) {
        this.dogNames = new ArrayList<>();
        for (String name : dogNames) {
            this.dogNames.add(new CheckableDogName(name, false));
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dog_name_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CheckableDogName checkableDogName = dogNames.get(position);
        CheckBox dogCheckBox = holder.getDogCheckBox();

        dogCheckBox.setText(checkableDogName.name);
        dogCheckBox.setChecked(checkableDogName.isChecked);

        dogCheckBox.setOnCheckedChangeListener((compoundButton, isChecked) -> checkableDogName.isChecked = isChecked);
    }

    @Override
    public int getItemCount() {
        return dogNames.size();
    }

    public void setDogNames(List<String> dogNames) {
        this.dogNames = new ArrayList<>();
        for (String name : dogNames) {
            this.dogNames.add(new CheckableDogName(name, false));
        }
        notifyDataSetChanged();
    }

    public List<String> getCheckedDogs() {
        return dogNames.stream()
                .filter(d -> d.isChecked)
                .map(d -> d.name)
                .collect(Collectors.toList());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox dogCheckBox;

        public ViewHolder(View view) {
            super(view);
            dogCheckBox = view.findViewById(R.id.dogNameCheckBox);
        }

        public CheckBox getDogCheckBox() {
            return dogCheckBox;
        }
    }

    private static class CheckableDogName {
        public String name;
        public boolean isChecked;

        public CheckableDogName(String name, boolean isChecked) {
            this.name = name;
            this.isChecked = isChecked;
        }
    }
}
