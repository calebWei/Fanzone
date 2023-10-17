package com.example.fanzone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Adapters.ItemAdapter;
import com.example.Entities.IItem;

import java.util.List;

public class SearchActivity extends AppCompatActivity {


    private class ViewHolder {
        RecyclerView recyclerView;
        TextView noMatchTextView;

        public ViewHolder() {
            recyclerView = findViewById(R.id.recycler_view);
            noMatchTextView = findViewById(R.id.noMatchText);
        }
    }

    ViewHolder vh;
    List<IItem> searchItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        vh = new ViewHolder();

        vh.noMatchTextView.setVisibility(View.INVISIBLE);
        vh.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        Intent intent = getIntent();
        searchItems = (List<IItem>) intent.getSerializableExtra("Results");
        StringBuilder builder = new StringBuilder();
        //TextView text = findViewById(R.id.results);

        for (IItem item : searchItems) {
            builder.append(item.getName());
        }
        // Print names of all relevant items
        //text.setText(builder.toString());
        if (searchItems.size() != 0) {
            Toast.makeText(this, String.format("%d Results Found", searchItems.size()), Toast.LENGTH_SHORT).show();
            propagateAdapter(searchItems);
        } else {
            vh.noMatchTextView.setVisibility(View.VISIBLE);
            Toast.makeText(this, "No Matches Found", Toast.LENGTH_SHORT).show();
        }
    }

    private void propagateAdapter(List<IItem> items){
        ItemAdapter adapter = new ItemAdapter(this, R.layout.list_view_epl,
                items);
        vh.recyclerView.setAdapter(adapter);
        vh.recyclerView.setVisibility(View.VISIBLE);
    }
}