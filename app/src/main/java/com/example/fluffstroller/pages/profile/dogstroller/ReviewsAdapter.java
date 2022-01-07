package com.example.fluffstroller.pages.profile.dogstroller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fluffstroller.R;
import com.example.fluffstroller.models.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    private List<Review> reviews;

    public ReviewsAdapter() {
        this.reviews = new ArrayList<>();
    }

    @NonNull
    @Override
    public ReviewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_card, parent, false);
        return new ReviewsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsAdapter.ViewHolder holder, int position) {
        Review review = reviews.get(position);

        holder.getReviewerName().setText(review.getReviewerName());
        holder.getReviewText().setText(review.getReviewText());
        if (review.getGivenStars() != null) {
            holder.getRatingBar().setRating(review.getGivenStars().floatValue());
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView reviewerName;
        private final TextView reviewText;
        private final RatingBar ratingBar;

        public ViewHolder(View view) {
            super(view);
            reviewerName = view.findViewById(R.id.reviewerNameReviewCard);
            reviewText = view.findViewById(R.id.reviewDescriptionReviewCard);
            ratingBar = view.findViewById(R.id.ratingBarReviewCard);
        }

        public TextView getReviewerName() {
            return reviewerName;
        }

        public TextView getReviewText() {
            return reviewText;
        }

        public RatingBar getRatingBar() {
            return ratingBar;
        }
    }
}
