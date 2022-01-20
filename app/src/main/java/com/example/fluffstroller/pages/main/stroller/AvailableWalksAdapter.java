package com.example.fluffstroller.pages.main.stroller;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.fluffstroller.R;
import com.example.fluffstroller.models.DogWalk;

import java.util.List;
import java.util.function.Consumer;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AvailableWalksAdapter extends RecyclerView.Adapter<AvailableWalksAdapter.ViewHolder> {

    private final Consumer<Pair<DogWalk, Integer>> requestButtonListener, visitProfileButtonListener, callButtonListener;
    private List<DogWalk> availableWalks;

    public AvailableWalksAdapter(List<DogWalk> availableWalks,
                                 Consumer<Pair<DogWalk, Integer>> requestButtonListener,
                                 Consumer<Pair<DogWalk, Integer>> visitProfileButtonListener,
                                 Consumer<Pair<DogWalk, Integer>> callButtonListener) {
        this.requestButtonListener = requestButtonListener;
        this.visitProfileButtonListener = visitProfileButtonListener;
        this.availableWalks = availableWalks;
        this.callButtonListener = callButtonListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.available_walk_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DogWalk dogWalk = availableWalks.get(position);

        String dogNames = "";
        if (dogWalk.getDogNames() != null) {
            dogNames = dogWalk.getDogNames().stream().reduce("", (s, s2) -> s + s2 + ", ");
            int lastIndex = dogNames.lastIndexOf(", ");
            if (lastIndex >= 0) {
                dogNames = dogNames.substring(0, lastIndex);
            }
        }

        holder.dogOwnerNameTextView.setText(dogWalk.getOwnerName());
        holder.dogNamesTextView.setText(dogNames);
        holder.walkingTimeTextView.setText(dogWalk.getWalkTime() + " minutes");
        holder.priceTextView.setText(dogWalk.getTotalPrice() + " RON");

        holder.requestButton.setOnClickListener(view -> {
            if (requestButtonListener != null) {
                requestButtonListener.accept(new Pair<>(dogWalk, position));
            }
        });

        holder.visitProfileButton.setOnClickListener(view -> {
            if (visitProfileButtonListener != null) {
                visitProfileButtonListener.accept(new Pair<>(dogWalk, position));
            }
        });

        if (dogWalk.getOwnerPhoneNumber() == null || dogWalk.getOwnerPhoneNumber().trim().isEmpty()) {
            holder.callButton.setVisibility(View.INVISIBLE);
        } else {
            holder.phoneTextView.setText(dogWalk.getOwnerPhoneNumber());
            holder.callButton.setOnClickListener(view -> {
                if (callButtonListener != null) {
                    callButtonListener.accept(new Pair<>(dogWalk, position));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return availableWalks.size();
    }

    public void setAvailableWalks(List<DogWalk> availableWalks) {
        this.availableWalks = availableWalks;
        notifyDataSetChanged();
    }

    public void addAvailableWalk(DogWalk walkRequest) {
        availableWalks.add(walkRequest);
        notifyItemChanged(availableWalks.size() - 1);
    }

    public void removeAvailableWalk(int index) {
        if (index >= 0 && index < availableWalks.size()) {
            availableWalks.remove(index);
            notifyItemRemoved(index);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView dogOwnerNameTextView;
        public final TextView dogNamesTextView;
        public final TextView walkingTimeTextView;
        public final TextView priceTextView;
        public final TextView phoneTextView;
        public final Button requestButton;
        public final Button visitProfileButton;
        public final ImageButton callButton;

        public ViewHolder(View view) {
            super(view);
            dogOwnerNameTextView = view.findViewById(R.id.dogOwnerNameTextView);
            dogNamesTextView = view.findViewById(R.id.dogNamesTextView);
            walkingTimeTextView = view.findViewById(R.id.walkingTimeValueTextView);
            priceTextView = view.findViewById(R.id.priceValueTextView);
            phoneTextView = view.findViewById(R.id.dogOwnerPhoneNumberTextView);
            requestButton = view.findViewById(R.id.requestButton);
            visitProfileButton = view.findViewById(R.id.visitProfileButton);
            callButton = view.findViewById(R.id.callImageButton);
        }
    }
}
