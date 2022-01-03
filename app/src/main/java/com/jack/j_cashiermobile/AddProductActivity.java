package com.jack.j_cashiermobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddProductActivity extends AppCompatActivity {

    DatabaseHelper db;

    EditText addName, addPrice;
    Button doneButton;

    int password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        db =  new DatabaseHelper(this);

        addName = findViewById(R.id.product_add_name);
        addPrice = findViewById(R.id.product_add_price);
        doneButton = findViewById(R.id.product_add_done);

        Intent intent = getIntent();
        password = intent.getIntExtra("password", 0);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addName.getText().toString().equals("") || addPrice.getText().toString().equals("")) {
                    Toast.makeText(AddProductActivity.this,
                            "Masukkan tidak tersedia, silahkan isi kembali",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Cursor cursor = db.viewProduct();
                    int index = 0;
                    if(cursor.getCount() == 0) {
                        index++;
                    }
                    else {
                        cursor.moveToLast();
                        index = cursor.getInt(0);
                        index++;
                    }

                    db.addProduct(index, addName.getText().toString(), Integer.parseInt(addPrice.getText().toString()));
                    Toast.makeText(AddProductActivity.this, "Berhasil menambahkan produk", Toast.LENGTH_SHORT).show();
                    if(password == 1) {
                        Intent in = new Intent(AddProductActivity.this, MainActivity.class);
                        startActivity(in);
                    }
                    else {
                        Intent in = new Intent(AddProductActivity.this, ProductActivity.class);
                        startActivity(in);
                    }

                }
            }
        });
    }

    public void ClickProduct(View view) {
        Intent in = new Intent(this, HintActivity.class);
        startActivity(in);
    }

    public void ClickBack(View view) {
        if(password == 1) {
            Intent in = new Intent(AddProductActivity.this, MainActivity.class);
            startActivity(in);
        }
        else {
            Intent in = new Intent(this, ProductActivity.class);
            startActivity(in);
        }
    }

    @Override
    public void onBackPressed() {
        if(password == 1) {
            Intent in = new Intent(AddProductActivity.this, MainActivity.class);
            startActivity(in);
        }
        else {
            Intent in = new Intent(this, ProductActivity.class);
            startActivity(in);
        }
    }
}