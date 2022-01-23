package com.example.gumloso.consumer;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gumloso.GeoLocation;
import com.example.gumloso.R;
import com.example.gumloso.Restaurant;

import java.util.List;

public class MyRecyclerViewAdapterConsumer extends RecyclerView.Adapter<MyRecyclerViewAdapterConsumer.ViewHolder> {

    private final List<Restaurant> mData;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private final GeoLocation userLocation;

    // data is passed into the constructor
    MyRecyclerViewAdapterConsumer(Context context, List<Restaurant> data, GeoLocation userLocation) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.userLocation = userLocation;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.restaurant_view, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Restaurant restaurant = mData.get(position);
        holder.restaurantView.setText(restaurant.getName());
        holder.distanceView.setText(String.valueOf(GeoLocation.getDistance(userLocation, restaurant.getLocation())));
        holder.ratingView.setText(String.valueOf(restaurant.getRating()));
        holder.typeView.setText(restaurant.getType());
        holder.imageView.setImageBitmap(BitmapFactory.decodeByteArray(restaurant.getImage(), 0, restaurant.getImage().length));

        holder.favImg.setVisibility(restaurant.isFavorite() ? View.VISIBLE : View.INVISIBLE);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView restaurantView, distanceView, ratingView, typeView;
        ImageView imageView, favImg;

        ViewHolder(View itemView) {
            super(itemView);
            restaurantView = itemView.findViewById(R.id.restaurantView);
            distanceView = itemView.findViewById(R.id.restaurantInfo2);
            ratingView = itemView.findViewById(R.id.restaurantInfo1);
            typeView = itemView.findViewById(R.id.restaurantInfo3);
            imageView = itemView.findViewById(R.id.imageView);
            favImg = itemView.findViewById(R.id.imageView3);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Restaurant getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
