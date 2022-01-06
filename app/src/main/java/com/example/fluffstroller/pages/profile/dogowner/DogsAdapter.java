package com.example.fluffstroller.pages.profile.dogowner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fluffstroller.R;
import com.example.fluffstroller.models.Dog;

import java.util.ArrayList;
import java.util.List;

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

        holder.dogName.setText(dog.getName());
        holder.breed.setText(dog.getBreed());
        holder.age.setText(dog.getAge() + "");
        holder.description.setText(dog.getDescription());

        if (dog.bitmap != null) {
            holder.image.setImageBitmap(dog.bitmap);
        }

        if (canRemove) {
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
        private final TextView dogName;
        private final TextView breed;
        private final TextView age;
        private final TextView description;
        private final ImageView image;
        private final Button removeButton;

        public ViewHolder(View view) {
            super(view);
            dogName = view.findViewById(R.id.dogNameTextViewDogCard);
            breed = view.findViewById(R.id.breedTextViewDogCard);
            description = view.findViewById(R.id.descriptionTextViewDogCard);
            age = view.findViewById(R.id.ageTextViewDogDetailsCard);
            image = view.findViewById(R.id.dogImageDogDetailsCard);
            removeButton = view.findViewById(R.id.removeButtonDogCard);
        }
    }
}
