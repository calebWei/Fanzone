package com.example.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.Entities.EplShirt;
import com.example.fanzone.CartActivity;
import com.example.fanzone.DetailsActivity;
import com.example.fanzone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CartAdaptor extends RecyclerView.Adapter<CartAdaptor.ViewHolder> {
    private List<Bitmap> imageList;
    private List<String> nameList;
    private List<String> sizeList;
    private List<Long> quantityList;
    private List<String> priceList;

    protected Context mContext;
    private CartActivity activity;

    // Define the listener interface
    // Define the public listener interface
    public interface OnClickListener {
        void onItemDelete(int position);
    }
    private OnClickListener onClickListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView imageView;
        public TextView nameTextView, sizeTextView, quantityTextView, priceTextView;
        public Button deleteButton;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            imageView = v.findViewById(R.id.imageView);
            nameTextView = v.findViewById(R.id.product_name);
            sizeTextView = v.findViewById(R.id.product_size);
            quantityTextView = v.findViewById(R.id.product_quantity);
            priceTextView = v.findViewById(R.id.product_price);
            deleteButton = v.findViewById(R.id.delete_button);
            // Set the OnClickListener for the delete button
            deleteButton.setOnClickListener(view -> {
                if (onClickListener != null) {
                    int position = getBindingAdapterPosition(); // or getAbsoluteAdapterPosition()
                    if (position != RecyclerView.NO_POSITION) {
                        onClickListener.onItemDelete(position);
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            // Sends user to the details page
            int position = getBindingAdapterPosition();
            String name = nameList.get(position);

            ArrayList<String> clubNames= new ArrayList<String>();
            clubNames.add("arsenal");
            clubNames.add("atleticoMadrid");
            clubNames.add("barcelona");
            clubNames.add("bayernMunich");
            clubNames.add("leipzig");
            clubNames.add("leverkusen");
            clubNames.add("manchesterCity");
            clubNames.add("manchesterUnited");
            clubNames.add("realMadrid");

            for (String string : clubNames) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("products").document(string).collection("products").whereEqualTo("name", name).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() != 0) {
                                List<EplShirt> items = task.getResult().toObjects(EplShirt.class);
                                // Create an intent to start the next activity
                                Intent intent = new Intent(mContext, DetailsActivity.class);
                                // Pass any necessary data to the next activity using the intent's putExtra() method
                                intent.putExtra("item", (Serializable) items.get(0));
                                // Start the next activity
                                mContext.startActivity(intent);
                            }
                        } else {
                            Toast.makeText(mContext, "Can't find the details page", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
    }

    public CartAdaptor(CartActivity activity, List<Bitmap> imageList, List<String> nameList, List<String> sizeList, List<Long> quantityList, List<String> priceList) {
        this.imageList = imageList;
        this.nameList = nameList;
        this.sizeList = sizeList;
        this.quantityList = quantityList;
        this.priceList = priceList;
        this.activity = activity;
    }

    @Override
    public CartAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Bitmap image = imageList.get(position);
        String name = nameList.get(position);
        String size = sizeList.get(position);
        String quantity = Long.toString(quantityList.get(position));
        String price = priceList.get(position);
        Glide.with(holder.imageView.getContext())
                .load(image)
                .into(holder.imageView);
        holder.nameTextView.setText(name);
        holder.sizeTextView.setText("Size: " + size);
        holder.quantityTextView.setText("Qty: " + quantity);
        holder.priceTextView.setText("$" + price);

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = holder.getBindingAdapterPosition();

                //Delete from database
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference itemsRef = db.collection("item");
                itemsRef.whereEqualTo("name", nameList.get(currentPosition))
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    document.getReference().delete();
                                }
                            } else {
                                Toast.makeText(mContext, "Deletion from database failed", Toast.LENGTH_LONG).show();
                            }
                        });

                // Remove the item from the data source
                imageList.remove(currentPosition);
                nameList.remove(currentPosition);
                sizeList.remove(currentPosition);
                quantityList.remove(currentPosition);
                priceList.remove(currentPosition);
                // Notify the adapter that an item has been removed
                notifyItemRemoved(currentPosition);
                activity.updateSubtotal(quantityList, priceList);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }
}
