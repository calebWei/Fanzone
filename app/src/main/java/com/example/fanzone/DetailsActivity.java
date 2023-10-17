package com.example.fanzone;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.Adapters.ImageAdapter;
import com.example.Entities.IItem;
import com.example.Entities.Item;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailsActivity extends AppCompatActivity {

    private class ViewHolder {
        TextView titleTextView, priceTextView, descriptionTextView, unitsTextView, quantityTextView, detailsTextView;
        ViewPager2 viewPager;

        public ViewHolder() {
            titleTextView = (TextView) findViewById(R.id.shirt_name);
            priceTextView = (TextView) findViewById(R.id.shirt_price);
            descriptionTextView = (TextView) findViewById(R.id.shirt_description_content);
            detailsTextView = (TextView) findViewById(R.id.shirt_details_content);
            quantityTextView = (TextView) findViewById(R.id.quantity_text);
            unitsTextView = (TextView) findViewById(R.id.units_left);
        }
    }

    ViewHolder vh;
    String size = "M";

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        vh = new ViewHolder();

        Intent intent = getIntent();
        Item item = (Item) intent.getSerializableExtra("item");

        addItemToRecentlyViewed(item);

        // Populate ViewPager and TabLayout
        ViewPager2 viewPager = findViewById(R.id.shirt_images_viewpager);
        TabLayout tabLayout = findViewById(R.id.shirt_images_indicator);

        List<String> example = item.getImageURI();
        // List<String> example = new ArrayList<>(Arrays.asList("rbl11a_1", "rbl_away_1", "rbl_away_2")); // Temporary
        int[] imageIds = new int[example.size()];
        Resources res = getResources();
        for (int i = 0; i < example.size(); i++) {
            imageIds[i] = res.getIdentifier(example.get(i), "drawable", getPackageName());
        }

        List<Bitmap> bitmaps = new ArrayList<>();
        for (int resId : imageIds) {
            Bitmap bitmap = BitmapFactory.decodeResource(res, resId);
            bitmaps.add(bitmap);
        }
        // Display images and bind tab layout to page viewer
        viewPager.setAdapter(new ImageAdapter(bitmaps));
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // Configure the tab as needed
        }).attach();

        // Fill in the text views
        vh.titleTextView.setText(item.getName());
        vh.priceTextView.setText("$" + item.getPrice().toString() + "0");
        vh.descriptionTextView.setText(item.getDescription());
        List<String> details = item.getDetails();
        String detailsFormat = "Brand: " + details.get(0) + "\n" + "Material: " + details.get(1) + "\n" + "Sleeve: " + details.get(2) + "\n" + "License: " + details.get(3);
        vh.detailsTextView.setText(detailsFormat);
        // vh.unitsTextView.setText(item.getQuantity());
        vh.quantityTextView.setText(Integer.toString(1));

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the ID of the selected RadioButton
                if (checkedId == R.id.size_small) {
                    // Small size is selected
                    size = "S";
                } else if (checkedId == R.id.size_medium) {
                    // Medium size is selected
                    size = "M";
                } else if (checkedId == R.id.size_large) {
                    // Large size is selected
                    size = "L";
                }
            }
        });

        // Add to Cart Button
        Button cartButton = findViewById(R.id.cartButton);
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get cart from database
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference itemsRef = db.collection("item");
                int currentQuantity = Integer.parseInt(vh.quantityTextView.getText().toString());
                itemsRef.whereEqualTo("imageURI", item.getImageURI().get(0))
                        .whereEqualTo("size", size)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                QuerySnapshot queryDocumentSnapshots = task.getResult();
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    // The item already exists in the cart, update the quantity
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                        Long existingQuantity = document.getLong("quantity");
                                        Map<String, Object> updates = new HashMap<>();
                                        updates.put("quantity", existingQuantity + currentQuantity);
                                        itemsRef.document(document.getId()).update(updates)
                                                .addOnSuccessListener(aVoid ->
                                                        Toast.makeText(getBaseContext(), "Item Added To Cart", Toast.LENGTH_LONG).show())
                                                .addOnFailureListener(e ->
                                                        Toast.makeText(getBaseContext(), "Error updating document", Toast.LENGTH_LONG).show());
                                    }
                                } else {
                                    // The item does not exist in the cart, add it
                                    Map<String, Object> newItem = new HashMap<>();
                                    newItem.put("name", item.getName());
                                    newItem.put("quantity", currentQuantity);
                                    newItem.put("imageURI", item.getImageURI().get(0));
                                    newItem.put("price", item.getPrice());
                                    newItem.put("size", size);
                                    itemsRef.add(newItem)
                                            .addOnSuccessListener(documentReference ->
                                                    Toast.makeText(getBaseContext(), "Item Added To Cart", Toast.LENGTH_LONG).show())
                                            .addOnFailureListener(e ->
                                                    Toast.makeText(getBaseContext(), "Error adding document", Toast.LENGTH_LONG).show());
                                }
                            } else {
                                Toast.makeText(getBaseContext(), "Error getting documents", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        // Increment Button
        Button increButton = findViewById(R.id.plus_button);
        increButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the current quantity from the quantityTextView
                int currentQuantity = Integer.parseInt(vh.quantityTextView.getText().toString());
                //if (currentQuantity < item.getQuantity()) {
                    currentQuantity++;
                    // Update the text of the quantityTextView
                    vh.quantityTextView.setText(Integer.toString(currentQuantity));
                //}
            }
        });

        // Decrement Button
        Button decreButton = findViewById(R.id.minus_button);
        decreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the current quantity from the quantityTextView
                int currentQuantity = Integer.parseInt(vh.quantityTextView.getText().toString());
                if (currentQuantity > 1) {
                    currentQuantity--;
                    // Update the text of the quantityTextView
                    vh.quantityTextView.setText(Integer.toString(currentQuantity));
                }
            }
        });

    }

    public void addItemToRecentlyViewed(IItem item){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference doc = db.collection("recent").document();
        doc.set(item);
    }
}
