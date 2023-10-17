package com.example.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Entities.IItem;
import com.example.fanzone.DetailsActivity;
import com.example.fanzone.R;

import java.io.Serializable;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView imageView;

        public TextView textView;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            imageView = v.findViewById(R.id.imageView);
            textView = v.findViewById(R.id.textView);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            IItem thisItem = mItems.get(position);
            // Create an intent to start the next activity
            Intent intent = new Intent(mContext, DetailsActivity.class);

            // Pass any necessary data to the next activity using the intent's putExtra() method
            intent.putExtra("item", (Serializable) thisItem);

            // Start the next activity
            mContext.startActivity(intent);
        }
    }

    private List<IItem> mItems;
    private int mLayoutID;
    private Context mContext;

    public ItemAdapter(@NonNull Context context, int resource, List<IItem> items){
        mItems = items;
        mContext = context;
        mLayoutID = resource;

    }


    @NonNull
    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if(mLayoutID == R.layout.list_view_laliga){
            double rand = Math.random();
            if(rand<=0.5){
                itemView = inflater.inflate(R.layout.list_view_epl, parent, false);
            }
            else{
                itemView = inflater.inflate(R.layout.list_view_bundesliga, parent, false);
            }
        }
        else {
            itemView = inflater.inflate(mLayoutID, parent, false);
        }

        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder vh, int position) {
        IItem thisItem = mItems.get(position);
        int resourceID = mContext.getResources().getIdentifier(thisItem.getImageURI().get(0), "drawable", mContext.getPackageName());
        vh.imageView.setBackgroundResource(resourceID);
        String name = thisItem.getName();
        if (name != null && name.length() > 50) {
            name = name.substring(0, 27) + " ...";
        }
        vh.textView.setText(name);
    }


    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
