package com.example.fanzone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Adapters.ItemAdapter;
import com.example.Entities.IItem;
import com.example.Entities.EplShirt;
import com.example.Repositories.ItemsRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private class ViewHolder {
        CardView bundesligaCardView, eplCardView, laLigaCardView;
        RecyclerView recyclerView;
        Button cartButton;

        public ViewHolder() {
            bundesligaCardView = findViewById(R.id.bundesliga);
            eplCardView = findViewById(R.id.epl);
            laLigaCardView = findViewById(R.id.laliga);
            recyclerView = findViewById(R.id.recycler_view);
            cartButton = findViewById(R.id.cart_button);
        }
    }

    ViewHolder vh;

    List<IItem> items = new ArrayList<>();
    List<IItem> queryResults = new ArrayList<>();
    List<IItem> recentlyViewed = new ArrayList<>();
    ItemsRepository repository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        repository = new ItemsRepository();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // fetch all items from firebase
        items = repository.fetchAllItems(getBaseContext());

        // If coming from Payment Activity, make toast
        if(getIntent().hasExtra("showToast")){
            Toast.makeText(getBaseContext(),"Order Successful, Thank you for shopping with us!",Toast.LENGTH_SHORT).show();
        }

        SearchView searchView = findViewById(R.id.searchView);
        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = searchView.findViewById(id);
        textView.setTextColor(Color.WHITE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchItems(query);

                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                intent.putExtra("Results", (Serializable) queryResults);
                startActivity(intent);
                // clear search query in between search
                searchView.setQuery("",false);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        vh = new ViewHolder();
        // Increment Button
        vh.cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(getBaseContext(), CartActivity.class);
                startActivity(cartIntent);
            }
        });

        vh.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // fetch recently viewed
        fetchRecentlyViewed();

        vh.bundesligaCardView.setOnClickListener(new View.OnClickListener() {
            //Corresponds to the Bundesliga button, sends a message informing ListActivity of the league type
            @Override
            public void onClick(View view) {
                Intent listIntent = new Intent(getBaseContext(), ClubSelectionActivity.class);
                listIntent.putExtra("leagueId","bundesliga");
                startActivity(listIntent);
            }
        });

        vh.eplCardView.setOnClickListener(new View.OnClickListener() {
            //Corresponds to the EPL button, sends a message informing ListActivity of the league type
            @Override
            public void onClick(View v) {
                Intent listIntent = new Intent(getBaseContext(), ClubSelectionActivity.class);
                listIntent.putExtra("leagueId","epl");
                startActivity(listIntent);
            }
        });

        vh.laLigaCardView.setOnClickListener(new View.OnClickListener() {
            //Corresponds to the La Liga button, sends a message informing ListActivity of the league type
            @Override
            public void onClick(View v) {

                Intent listIntent = new Intent(getBaseContext(), ClubSelectionActivity.class);
                listIntent.putExtra("leagueId","laliga");

                startActivity(listIntent);
            }
        });
    }

    private void fetchRecentlyViewed(){
        List<IItem> recentlyViewed = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("recent").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Set<String> set = new HashSet<>();
                if (task.isSuccessful()) {
                    QuerySnapshot results = task.getResult();
                    if (results.size() > 0) {
                        if (results.size() > 6) {
                            results.getDocuments().get(0).getReference().delete();
                        }

                        for (IItem product : task.getResult().toObjects(EplShirt.class)) {
                            if (set.add(product.getName())) {
                                recentlyViewed.add(product);
                            }
                        }


                        propagateAdapter(recentlyViewed);
                    }
                } else {
                    Toast.makeText(getBaseContext(), "Loading items collection failed from Firestore!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void propagateAdapter(List<IItem> items){
        ItemAdapter adapter = new ItemAdapter(this, R.layout.list_view_item,
                items);
        vh.recyclerView.setAdapter(adapter);
        vh.recyclerView.setVisibility(View.VISIBLE);
    }

    private void searchItems(String queryString) {
        // reset query results in between searches
        if (queryResults != null && queryResults.size() > 0) {
            queryResults.clear();
        }

        // capitalise first char of query string
        String capitalisedQueryString = queryString.substring(0,1).toUpperCase() +queryString.substring(1);

        for(IItem item : items){
            if(item.getName() != null && item.getName().contains(capitalisedQueryString)){
                queryResults.add(item);
            }
        }
    }

    @Override
    //Triggers when the Main Activity is resumed, updates the recently viewed list
    protected void onResume(){
        super.onResume();
        fetchRecentlyViewed();
    }

}