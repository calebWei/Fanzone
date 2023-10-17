package com.example.fanzone;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.Adapters.ItemAdapter;
import com.example.Entities.IItem;
import com.example.Entities.BundesligaShirt;
import com.example.Entities.EplShirt;
import com.example.Entities.LaligaShirt;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    // Adapted from https://stackoverflow.com/questions/28531996/android-recyclerview-gridlayoutmanager-column-spacing
    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int halfSpace;

        public SpacesItemDecoration(int space) {
            this.halfSpace = space / 2;
        }
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

            if (parent.getPaddingLeft() != halfSpace) {
                parent.setPadding(halfSpace, halfSpace, halfSpace, halfSpace);
                parent.setClipToPadding(false);
            }

            outRect.top = halfSpace;
            outRect.bottom = halfSpace;
            outRect.left = halfSpace;
            outRect.right = halfSpace;
        }
    }

    private class ViewHolder {
        RecyclerView recyclerView;

        public ViewHolder() {
            recyclerView = findViewById(R.id.recycler_view);
        }
    }

    ViewHolder vh;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras =getIntent().getExtras();
        String league =extras.getString("league");
        String club = extras.getString("club");

        if(league.equals("epl")){
            setContentView(R.layout.activity_epl);
        }
        else if(league.equals("bundesliga")){
            setContentView(R.layout.activity_bundesliga);
        }
        else if(league.equals("laliga")){
            setContentView(R.layout.activity_laliga);
        }
        else{
            setContentView(R.layout.activity_epl);
        }
        vh= new ViewHolder();
        createRecyclerViewLayoutManager(league);

        loadItems(club, league);
    }

    private void createRecyclerViewLayoutManager(String league){
        if(league.equals("epl")){
            vh.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        }
        else if(league.equals("bundesliga")){
            vh.recyclerView.setLayoutManager(new GridLayoutManager(this,2));
            vh.recyclerView.addItemDecoration(new SpacesItemDecoration(20));
        }
        else if(league.equals("laliga")){
            vh.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,LinearLayoutManager.VERTICAL));
            vh.recyclerView.addItemDecoration(new SpacesItemDecoration(20));
        }
    }

    private void loadItems(String club, String league){
        List<IItem> itemsToList = new ArrayList<>();

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("products").document(club).collection("products").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if (task.isSuccessful()) {
                        QuerySnapshot results = task.getResult();
                        if(league.equals("epl")){
                            for (IItem product : task.getResult().toObjects(EplShirt.class)) {
                                itemsToList.add(product);
                            }
                        }
                        else if(league.equals("bundesliga")){
                            for (IItem product : task.getResult().toObjects(BundesligaShirt.class)) {
                                itemsToList.add(product);
                            }

                        }
                        else if(league.equals("laliga")){
                            for (IItem product : task.getResult().toObjects(LaligaShirt.class)) {
                                itemsToList.add(product);
                            }


                        }

                        propagateAdapter(itemsToList,league);

                    } else
                        Toast.makeText(getBaseContext(), "Loading items collection failed from Firestore!", Toast.LENGTH_LONG).show();
                }
            });

        }




    private void propagateAdapter(List<IItem> items, String league){
        int resource = league.equals("epl") ? R.layout.list_view_epl : league.equals("bundesliga") ? R.layout.list_view_bundesliga : R.layout.list_view_laliga;
        ItemAdapter adapter = new ItemAdapter(this, resource,
                items);
        vh.recyclerView.setAdapter(adapter);
        vh.recyclerView.setVisibility(View.VISIBLE);
    }
}
