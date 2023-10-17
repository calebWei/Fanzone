package com.example.fanzone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

public class ClubSelectionActivity extends AppCompatActivity {

    private class ViewHolder {
        RecyclerView recyclerView;

        public ViewHolder() {
            recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        }
    }

    ViewHolder vh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_selection);

        // Get league ID
        Intent intent = getIntent();
        String leagueId = (String) intent.getSerializableExtra("leagueId");

        vh = new ViewHolder();
        vh.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        vh.recyclerView.setAdapter(new ClubCardAdapter(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new String()));

        // Fetch Clubs
        List<Bitmap> imageList = new ArrayList<>();
        List<String> descriptionList = new ArrayList<>();
        List<String> clubList = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection("clubs");
        collectionRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Get the image URI and description from the document
                    String leagueName = document.getString("leagueId");
                    if (leagueName.equals(leagueId)) {
                        // Process each club document with the specified leagueId here
                        String imageURI = document.getString("imageURI");
                        String description = document.getString("description");
                        String club = document.getId();
                        // Create a Bitmap from the image URI
                        Bitmap image = getBitmapFromUri(imageURI);

                        // Add the image and description to their respective lists
                        imageList.add(image);
                        descriptionList.add(description);
                        clubList.add(club);
                    }

                }
                ClubCardAdapter adapter = new ClubCardAdapter(imageList, descriptionList, clubList, leagueId);
                vh.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                vh.recyclerView.setAdapter(adapter);
                vh.recyclerView.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(getBaseContext(), "Loading items collection failed from Firestore!", Toast.LENGTH_LONG).show();
            }
        });
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