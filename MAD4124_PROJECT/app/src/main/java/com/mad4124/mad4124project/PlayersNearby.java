package com.mad4124.mad4124project;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;

public class PlayersNearby extends RecyclerView.Adapter<PlayersNearby.ViewHolder>{
    private ArrayList<Player> players;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    Context context;
    double userLatitude;
    double userLongitude;
    private static final double EARTH_RADIUS = 6371;
    final static String TAG = "Akashdeep";

    public PlayersNearby(Context context, ArrayList<Player> players, double userLatitude, double userLongitude) {
        this.mInflater = LayoutInflater.from(context);
        this.players = players;
        this.context = context;
        this.userLatitude = userLatitude;
        this.userLongitude = userLongitude;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.player_nearby_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        double distance = getDistance(userLatitude,userLongitude,players.get(position).getLat(),players.get(position).getLng());
        holder.txtPlayer.setText(players.get(position).getPhoneNumber());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return players.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView txtPlayer;

        ViewHolder(View itemView) {
            super(itemView);
            txtPlayer = itemView.findViewById(R.id.txtPlayer);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null){
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {

            return true;
        }
    }


    public Player getItem(int id) {
        return players.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    //get distance
    public double getDistance(double startLat, double startLong,
                              double endLat, double endLong) {

        double dLat  = Math.toRadians((endLat - startLat));
        double dLong = Math.toRadians((endLong - startLong));

        startLat = Math.toRadians(startLat);
        endLat   = Math.toRadians(endLat);

        double a = haversin(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversin(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    public static double haversin(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }
}
