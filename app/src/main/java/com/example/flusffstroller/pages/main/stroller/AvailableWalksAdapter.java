package com.example.flusffstroller.pages.main.stroller;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.flusffstroller.R;
import com.example.flusffstroller.models.AvailableWalk;

import java.util.List;
import java.util.function.Consumer;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AvailableWalksAdapter extends RecyclerView.Adapter<AvailableWalksAdapter.ViewHolder> {

    private final Consumer<Pair<AvailableWalk, Integer>> requestButtonListener, visitProfileButtonListener, callButtonListener;
    private List<AvailableWalk> availableWalks;

    public AvailableWalksAdapter(List<AvailableWalk> availableWalks,
                                 Consumer<Pair<AvailableWalk, Integer>> requestButtonListener,
                                 Consumer<Pair<AvailableWalk, Integer>> visitProfileButtonListener,
                                 Consumer<Pair<AvailableWalk, Integer>> callButtonListener) {
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
        AvailableWalk availableWalk = availableWalks.get(position);

        String dogNames = "";
        if (availableWalk.getDogNames() != null) {
            dogNames = availableWalk.getDogNames().stream().reduce("", (s, s2) -> s + s2 + ", ");
            int lastIndex = dogNames.lastIndexOf(", ");
            if (lastIndex >= 0) {
                dogNames = dogNames.substring(0, lastIndex);
            }
        }

        holder.dogOwnerNameTextView.setText(availableWalk.getDogOwnerName());
        holder.dogNamesTextView.setText(dogNames);
        holder.walkingTimeTextView.setText(availableWalk.getWalkingTimeMinutes() + " minutes");
        holder.priceTextView.setText(availableWalk.getPrice() + " $");

        holder.requestButton.setOnClickListener(view -> {
            if (requestButtonListener != null) {
                requestButtonListener.accept(new Pair<>(availableWalk, position));
            }
        });

        holder.visitProfileButton.setOnClickListener(view -> {
            if (visitProfileButtonListener != null) {
                visitProfileButtonListener.accept(new Pair<>(availableWalk, position));
            }
        });

        holder.callButton.setOnClickListener(view -> {
            if (callButtonListener != null){
                callButtonListener.accept(new Pair<>(availableWalk, position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return availableWalks.size();
    }

    public void setAvailableWalks(List<AvailableWalk> availableWalks) {
        this.availableWalks = availableWalks;
        notifyDataSetChanged();
    }

    public void addAvailableWalk(AvailableWalk walkRequest) {
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
        public final Button requestButton;
        public final Button visitProfileButton;
        public final ImageButton callButton;

        public ViewHolder(View view) {
            super(view);
            dogOwnerNameTextView = view.findViewById(R.id.dogOwnerNameTextView);
            dogNamesTextView = view.findViewById(R.id.dogNamesTextView);
            walkingTimeTextView = view.findViewById(R.id.walkingTimeValueTextView);
            priceTextView = view.findViewById(R.id.priceValueTextView);
            requestButton = view.findViewById(R.id.requestButton);
            visitProfileButton = view.findViewById(R.id.visitProfileButton);
            callButton = view.findViewById(R.id.callImageButton);
        }
    }
}
