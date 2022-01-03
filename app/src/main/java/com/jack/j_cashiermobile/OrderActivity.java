package com.jack.j_cashiermobile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class OrderActivity extends AppCompatActivity {

    int valueCount = 1;
    int priceCount;
    int idProduct;
    String name;
    TextView nameOrder, priceOrder, valueOrder;
    ImageButton plus, minus;

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        db =  new DatabaseHelper(this);

        nameOrder = findViewById(R.id.order_name);
        priceOrder = findViewById(R.id.order_price);
        valueOrder = findViewById(R.id.order_value);

        plus = findViewById(R.id.increase);
        minus = findViewById(R.id.decrease);

        Intent intent = getIntent();
        idProduct = intent.getIntExtra("ids", 1);
        name =  intent.getStringExtra("name");
        int price = intent.getIntExtra("price",0);

        priceCount = valueCount * price;

        valueOrder.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN || keyCode == KeyEvent.KEYCODE_ENTER) {
                    if(valueOrder.getText().toString().equals("")) {
                        Toast.makeText(OrderActivity.this,
                                "Masukkan tidak tersedia",
                                Toast.LENGTH_SHORT).show();;
                        valueCount = 1;
                        priceCount = valueCount * price;
                        valueOrder.setText(String.valueOf(valueCount));
                        priceOrder.setText(String.valueOf(priceCount));
                    }
                    else if(valueOrder.getText().toString().equals("0")){
                        Toast.makeText(OrderActivity.this,
                                "Telah mencapai nilai minimum item", Toast.LENGTH_SHORT).show();
                        valueCount = 1;
                        priceCount = valueCount * price;
                        valueOrder.setText(String.valueOf(valueCount));
                        priceOrder.setText(String.valueOf(priceCount));
                    }
                    else {
                        valueCount = Integer.parseInt(valueOrder.getText().toString());
                        priceCount = valueCount * price;
                        valueOrder.setText(String.valueOf(valueCount));
                        priceOrder.setText(String.valueOf(priceCount));
                    }
                }
                return false;
            }
        });

        nameOrder.setText(name);
        priceOrder.setText(String.valueOf(priceCount));
        valueOrder.setText(String.valueOf(valueCount));

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valueCount++;
                priceCount = valueCount * price;
                valueOrder.setText(String.valueOf(valueCount));
                priceOrder.setText(String.valueOf(priceCount));
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(valueCount <= 1) {
                    Toast.makeText(OrderActivity.this,
                            "Telah mencapai nilai minimum item", Toast.LENGTH_SHORT).show();
                }
                else {
                    valueCount--;
                    priceCount = valueCount * price;
                    valueOrder.setText(String.valueOf(valueCount));
                    priceOrder.setText(String.valueOf(priceCount));
                }
            }
        });
    }

    public void ClickMain(View view) {
        Intent in = new Intent(this, MainActivity.class);
        startActivity(in);
    }

    public void ClickContinue(View view) {
        Cursor cursor = db.viewAllTransaction();
        while(cursor.moveToNext()) {
            db.transactionId = cursor.getInt(0);
        }
        db.addCart(idProduct, priceCount, valueCount);
        Intent in = new Intent(this, MainActivity.class);
        startActivity(in);
        Toast.makeText(OrderActivity.this, "Barang berhasil ditambahkan ke" +
                        " keranjang belanja",
                Toast.LENGTH_SHORT).show();
    }

    public void ClickOrder(View view) {
        Intent in = new Intent(this, HintActivity.class);
        startActivity(in);
    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent(this, MainActivity.class);
        startActivity(in);
    }
}