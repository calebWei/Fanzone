package com.example.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.Entities.IItem;
import com.example.fanzone.ListActivity;
import com.example.fanzone.R;

import java.util.List;

public class ClubCardAdapter extends RecyclerView.Adapter<ClubCardAdapter.ViewHolder> {
    private List<Bitmap> imageList;
    private List<String> descriptionList;
    private List<String> clubList;

    private String league;

    protected Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView imageView;
        public TextView descriptionTextView;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            imageView = v.findViewById(R.id.imageView);
            descriptionTextView = v.findViewById(R.id.descriptionTextView);
        }

        @Override
        public void onClick(View v) {
            String club = clubList.get(getBindingAdapterPosition());
            Intent listIntent = new Intent(mContext, ListActivity.class);
            listIntent.putExtra("club",club);
            listIntent.putExtra("league", league);
            mContext.startActivity(listIntent);
        }
    }

    public ClubCardAdapter(List<Bitmap> imageList, List<String> descriptionList, List<String> clubList, String league) {
        this.imageList = imageList;
        this.descriptionList = descriptionList;
        this.clubList = clubList;
        this.league = league;
    }

    @Override
    public ClubCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.club_cardview, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Bitmap image = imageList.get(position);
        String description = descriptionList.get(position);
        Glide.with(holder.imageView.getContext())
                .load(image)
                .into(holder.imageView);
        holder.descriptionTextView.setText(description);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }
}
