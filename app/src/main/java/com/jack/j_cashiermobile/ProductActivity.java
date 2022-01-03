package com.jack.j_cashiermobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class ProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
    }

    public void ClickProduct(View view) {
        Intent in = new Intent(this, HintActivity.class);
        startActivity(in);
    }

    public void ClickMain(View view) {
        Intent in = new Intent(this, MainActivity.class);
        startActivity(in);
    }

    public void ClickToAddProduct(View view) {
        Intent in = new Intent(this, AddProductActivity.class);
        startActivity(in);
    }

    public void ClickToRemoveProduct(View view) {
        Intent in = new Intent(this, RemoveProductActivity.class);
        startActivity(in);
    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent(this, MainActivity.class);
        startActivity(in);
    }
}