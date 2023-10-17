package com.example.Repositories;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.Entities.IItem;
import com.example.Entities.EplShirt;
import com.example.Entities.League;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ItemsRepository implements IItemsRepository{

    List<IItem> allItems = new ArrayList<>();
    List<IItem> recentlyViewed = new ArrayList<>();
    FirebaseFirestore db;


    public ItemsRepository() {

        db = FirebaseFirestore.getInstance();
    }

    @Override
    public List<IItem> fetchAllItems(Context context) {
        League league = new League();
        ArrayList<String> allClubs = league.getAllClubs();

        for (String string : allClubs) {
            db.collection("products").document(string).collection("products").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        allItems.addAll(task.getResult().toObjects(EplShirt.class));
                    } else
                        Toast.makeText(context, "Loading items collection failed from Firestore!", Toast.LENGTH_LONG).show();
                }
            });
        }
        return allItems;
    }

}
