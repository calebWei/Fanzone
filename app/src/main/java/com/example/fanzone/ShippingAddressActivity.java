package com.example.fanzone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ShippingAddressActivity extends AppCompatActivity {

    // centralise declaration and initialisation of all views that are referenced in this activity
    class ViewHolder {
        EditText nameEditText, emailEditText, address1EditText, address2EditText;
        CheckBox checkBoxTerms;
        TextView priceText;
    }

    ViewHolder vh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_address);
        Bundle extras = getIntent().getExtras();
        int numItems = extras.getInt("items");
        String total = extras.getString("price");
        vh = new ViewHolder();
        vh.nameEditText = findViewById(R.id.name);
        vh.emailEditText = findViewById(R.id.email);
        vh.address1EditText = findViewById(R.id.address_line_1);
        vh.address2EditText = findViewById(R.id.address_line_2);
        vh.priceText = findViewById(R.id.total_cost);
        Button payment_button = findViewById(R.id.proceed_to_payment_button);

        vh.priceText.setText("$" + Integer.toString(25 * numItems));
        payment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent paymentIntent = new Intent(getBaseContext(), PaymentActivity.class);
                paymentIntent.putExtra("shippingCost", Integer.toString(25 * numItems));
                paymentIntent.putExtra("itemsTotal", total);
                startActivity(paymentIntent);
            }
        });
    }

}


