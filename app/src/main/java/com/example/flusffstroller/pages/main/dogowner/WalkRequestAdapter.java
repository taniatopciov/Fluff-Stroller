package com.example.flusffstroller.pages.main.dogowner;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.flusffstroller.R;
import com.example.flusffstroller.models.WalkRequest;

import java.util.List;
import java.util.function.Consumer;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WalkRequestAdapter extends RecyclerView.Adapter<WalkRequestAdapter.ViewHolder> {

    private final Consumer<Pair<WalkRequest, Integer>> acceptButtonListener, rejectButtonListener, visitProfileButtonListener, callButtonListener;
    private List<WalkRequest> walkRequests;

    public WalkRequestAdapter(List<WalkRequest> walkRequests, Consumer<Pair<WalkRequest, Integer>> acceptButtonListener,
                              Consumer<Pair<WalkRequest, Integer>> rejectButtonListener, Consumer<Pair<WalkRequest, Integer>> visitProfileButtonListener,
                              Consumer<Pair<WalkRequest, Integer>> callButtonListener) {
        this.acceptButtonListener = acceptButtonListener;
        this.rejectButtonListener = rejectButtonListener;
        this.visitProfileButtonListener = visitProfileButtonListener;
        this.callButtonListener = callButtonListener;
        this.walkRequests = walkRequests;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.walk_request_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WalkRequest request = walkRequests.get(position);

        holder.strollerNameTextView.setText(request.getStrollerName());
        holder.strollerPhoneNumberTextView.setText(request.getStrollerPhoneNumber());
        holder.strollerRatingNumberTextView.setText(formatRating(request.getStrollerRating()));

        holder.acceptButton.setOnClickListener(view -> {
            if (acceptButtonListener != null) {
                acceptButtonListener.accept(new Pair<>(request, position));
            }
        });

        holder.rejectButton.setOnClickListener(view -> {
            if (rejectButtonListener != null) {
                rejectButtonListener.accept(new Pair<>(request, position));
            }
        });

        holder.visitProfileButton.setOnClickListener(view -> {
            if (visitProfileButtonListener != null) {
                visitProfileButtonListener.accept(new Pair<>(request, position));
            }
        });

        holder.callButton.setOnClickListener(view -> {
            if (callButtonListener != null) {
                callButtonListener.accept(new Pair<>(request, position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return walkRequests.size();
    }

    public void setWalkRequests(List<WalkRequest> walkRequests) {
        this.walkRequests = walkRequests;
        notifyDataSetChanged();
    }

    public void addWalkRequest(WalkRequest walkRequest) {
        walkRequests.add(walkRequest);
        notifyItemChanged(walkRequests.size() - 1);
    }

    public void removeWalkRequest(int index) {
        if (index >= 0 && index < walkRequests.size()) {
            walkRequests.remove(index);
            notifyItemRemoved(index);
        }
    }

    private String formatRating(Double rating) {
        return rating + "/5";
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView strollerNameTextView;
        public final TextView strollerPhoneNumberTextView;
        public final TextView strollerRatingNumberTextView;
        public final Button acceptButton, rejectButton, visitProfileButton;
        public final ImageButton callButton;

        public ViewHolder(View view) {
            super(view);
            strollerNameTextView = view.findViewById(R.id.strollerNameTextView);
            strollerPhoneNumberTextView = view.findViewById(R.id.strollerPhoneNumberTextView);
            strollerRatingNumberTextView = view.findViewById(R.id.strollerRatingTextView);
            acceptButton = view.findViewById(R.id.acceptButton);
            rejectButton = view.findViewById(R.id.rejectButton);
            visitProfileButton = view.findViewById(R.id.visitProfileButton);
            callButton = view.findViewById(R.id.callImageButton);
        }
    }
}
