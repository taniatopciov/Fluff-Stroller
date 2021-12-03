package com.example.fluffstroller.pages.profile.dogowner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fluffstroller.R;
import com.example.fluffstroller.models.Dog;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DogsAdapter extends RecyclerView.Adapter<DogsAdapter.ViewHolder> {

    private List<Dog> dogs;
    private final boolean canRemove;

    public DogsAdapter(boolean canRemove) {
        this.dogs = new ArrayList<>();
        this.canRemove = canRemove;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dog_details_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Dog dog = dogs.get(position);

        holder.getDogName().setText(dog.getName());
        holder.getBreed().setText(dog.getBreed());
        holder.getDescription().setText(dog.getDescription());
        //todo add photo

        if(canRemove) {
            holder.removeButton.setOnClickListener(view -> {
                dogs.remove(position);
                notifyItemRemoved(position);
            });
        } else {
            holder.removeButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return dogs.size();
    }

    public void setDogs(List<Dog> dogs) {
        this.dogs = dogs;
        notifyDataSetChanged();
    }

    public List<Dog> getDogs() {
        return dogs;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final EditText dogName;
        private final EditText breed;
        private final EditText description;
        private final ImageView image;
        private final Button removeButton;

        public ViewHolder(View view) {
            super(view);
            dogName = view.findViewById(R.id.dogNameTextViewDogCard);
            breed = view.findViewById(R.id.breedTextViewDogCard);
            description = view.findViewById(R.id.descriptionTextViewDogCard);
            image = view.findViewById(R.id.imageDogCard);
            removeButton = view.findViewById(R.id.removeButtonDogCard);
        }

        public EditText getDogName() {
            return dogName;
        }

        public EditText getBreed() {
            return breed;
        }

        public EditText getDescription() {
            return description;
        }

        public ImageView getImage() {
            return image;
        }

        public Button getRemoveButton() {
            return removeButton;
        }
    }
}
