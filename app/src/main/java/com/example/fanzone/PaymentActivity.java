package com.example.fanzone;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

//import com.example.fanzone.models.Cart;

import com.example.Entities.Cart;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class PaymentActivity extends AppCompatActivity {

    class ViewHolder {

        TextView shippingText, totalText;
        CheckBox termsCheckBox;

        Button orderButton;
        public ViewHolder() {
            shippingText = findViewById(R.id.shipping_cost);
            totalText = findViewById(R.id.total_cost);
            termsCheckBox = findViewById(R.id.checkbox_terms);
            orderButton = findViewById(R.id.orderButton);

        }


    }

    ViewHolder vh;
    Cart cart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        setContentView(R.layout.activity_payment_details);

        vh = new ViewHolder();
        String shippingCost = (String) extras.getString("shippingCost");
        String itemsCost = extras.getString("itemsTotal");
        int total = Integer.valueOf(shippingCost.substring(0)) + Integer.valueOf(itemsCost.substring(1));
        vh.shippingText.setText("$"+shippingCost);
        vh.totalText.setText("$"+ Integer.toString(total));
        vh.orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vh.termsCheckBox.isChecked()) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("item").get().addOnCompleteListener( task -> {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot doc : task.getResult()){
                                doc.getReference().delete();
                            }
                        }
                    });
                    Intent shippingIntent = new Intent(getBaseContext(), MainActivity.class);
                    shippingIntent.putExtra("showToast",true);
                    startActivity(shippingIntent);
                }
                else{
                    Toast.makeText(getBaseContext(),"Please agree to the terms and services",Toast.LENGTH_LONG).show();
                }
            }
        });


    }







}
