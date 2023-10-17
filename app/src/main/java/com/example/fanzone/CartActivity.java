package com.example.fanzone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.Adapters.CartAdaptor;
import com.example.Adapters.ClubCardAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CartActivity extends AppCompatActivity implements CartAdaptor.OnClickListener{

    List<Bitmap> imageList = new ArrayList<>();
    List<String> nameList = new ArrayList<>();
    List<String> sizeList = new ArrayList<>();
    List<Long> quantityList = new ArrayList<>();
    List<String> priceList = new ArrayList<>();
    private CartAdaptor cartAdapter;

    // centralise declaration and initialisation of all views that are referenced in this activity
    class ViewHolder {
        TextView subTotalTextView;
        Button checkoutButton;
        RecyclerView recyclerView;

        public ViewHolder() {
            subTotalTextView = findViewById(R.id.subtotal);
            checkoutButton = findViewById(R.id.checkout_button);
            recyclerView = findViewById(R.id.recycler_view);
        }
    }
    CartActivity.ViewHolder vh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        vh = new CartActivity.ViewHolder();
        vh.subTotalTextView = (TextView) findViewById(R.id.subtotal);

        // proceed to checkout
        vh.checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent checkoutIntent = new Intent(getBaseContext(), ShippingAddressActivity.class);
                checkoutIntent.putExtra("items", nameList.size());
                checkoutIntent.putExtra("price", vh.subTotalTextView.getText());
                startActivity(checkoutIntent);
            }
        });



        // Fetch data
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection("item");
        collectionRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Long subTotal = 0L;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Process each item
                    String imageURI = document.getString("imageURI");
                    String name = document.getString("name");
                    String size = document.getString("size");
                    Long quantity = document.getLong("quantity");
                    String price = Long.toString(document.getLong("price"));
                    // Create a Bitmap from the image URI
                    Bitmap image = getBitmapFromUri(imageURI);
                    // Add the image and data to their respective lists
                    imageList.add(image);
                    nameList.add(name);
                    sizeList.add(size);
                    quantityList.add(quantity);
                    priceList.add(price);
                    subTotal = subTotal + (Long.parseLong(price) * quantity);
                }
                cartAdapter = new CartAdaptor(this, imageList, nameList, sizeList, quantityList, priceList);
                vh.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                vh.recyclerView.setAdapter(cartAdapter);
                vh.recyclerView.setVisibility(View.VISIBLE);

                vh.subTotalTextView.setText("$" + subTotal);
            } else {
                Toast.makeText(getBaseContext(), "Loading items collection failed from Firestore!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onItemDelete(int position) {
        // Remove the item from your data lists
        nameList.remove(position);
        sizeList.remove(position);
        quantityList.remove(position);
        priceList.remove(position);

        // Notify the adapter that the item has been removed
        cartAdapter.notifyItemRemoved(position);
        cartAdapter.notifyItemRangeChanged(position, cartAdapter.getItemCount());
    }

    public void updateSubtotal(List<Long> quantityList, List<String> priceList) {
        // Calculate the new subtotal
        double subtotal = 0;
        for (int i = 0; i < priceList.size(); i++) {
            subtotal += (Long.parseLong(priceList.get(i)) * quantityList.get(i));
        }

        // Update the TextView
        vh.subTotalTextView.setText("$" + subtotal);
    }


    private Bitmap getBitmapFromUri(String imageName) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Bitmap> future = executorService.submit(new Callable<Bitmap>() {
            @Override
            public Bitmap call() {
                try {
                    int drawableResourceId = getApplicationContext().getResources().getIdentifier(imageName, "drawable", getApplicationContext().getPackageName());
                    return Glide.with(getApplicationContext())
                            .asBitmap()
                            .load(drawableResourceId)
                            .submit()
                            .get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });

        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
