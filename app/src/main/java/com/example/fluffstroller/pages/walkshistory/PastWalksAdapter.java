package com.example.fluffstroller.pages.walkshistory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fluffstroller.R;
import com.example.fluffstroller.models.DogWalk;
import com.example.fluffstroller.models.UserType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

public class PastWalksAdapter extends RecyclerView.Adapter<PastWalksAdapter.ViewHolder> {

    private List<DogWalk> pastWalks;
    private final UserType userType;
    private final Consumer<String> onPastWalkCardClick;

    public PastWalksAdapter(UserType userType, Consumer<String> onPastWalkCardClick) {
        this.onPastWalkCardClick = onPastWalkCardClick;
        this.pastWalks = new ArrayList<>();
        this.userType = userType;
    }

    @NonNull
    @Override
    public PastWalksAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.past_walk_card, parent, false);
        return new PastWalksAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PastWalksAdapter.ViewHolder holder, int position) {
        DogWalk pastWalk = pastWalks.get(position);

        Date date = new Date(pastWalk.getCreationTimeMillis());
        String pattern = "dd-MM-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String formattedDate = simpleDateFormat.format(date);
        holder.date.setText(formattedDate);

        String dogNames = "";
        if (pastWalk.getDogNames() != null) {
            dogNames = pastWalk.getDogNames().stream().reduce("", (s, s2) -> s + s2 + ", ");
            int lastIndex = dogNames.lastIndexOf(", ");
            if (lastIndex >= 0) {
                dogNames = dogNames.substring(0, lastIndex);
            }
        }
        holder.dogs.setText(dogNames);

        if (userType.equals(UserType.STROLLER)) {
            holder.nameLabel.setText(R.string.owner_name);
            holder.name.setText(pastWalk.getOwnerName());
        } else {
            if (pastWalk.getAcceptedRequest() != null) {
                holder.nameLabel.setText(R.string.stroller_actual_name);
                holder.name.setText(pastWalk.getAcceptedRequest().getStrollerName());
            } else {
                holder.nameLabel.setVisibility(View.GONE);
            }
        }

        holder.itemView.setOnClickListener(view -> {
            if (onPastWalkCardClick != null) {
                onPastWalkCardClick.accept(pastWalk.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return pastWalks.size();
    }

    public void setPastWalks(List<DogWalk> pastWalks) {
        this.pastWalks = pastWalks;
        notifyDataSetChanged();
    }

    public List<DogWalk> getPastWalks() {
        return pastWalks;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView date;
        private final TextView dogs;
        private final TextView name;
        private final TextView nameLabel;

        public ViewHolder(View view) {
            super(view);
            date = view.findViewById(R.id.datePastWalksCard);
            dogs = view.findViewById(R.id.dogsPastWalksCard);
            name = view.findViewById(R.id.namePastWalksCard);
            nameLabel = view.findViewById(R.id.nameLabelPastWalksCard);
        }
    }
}
