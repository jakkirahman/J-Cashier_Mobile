package com.jack.j_cashiermobile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    DatabaseHelper db;

    ListView listView;

    ArrayList<Integer> cart_id = new ArrayList<>();
    ArrayList<String> cart_name = new ArrayList<>();
    ArrayList<Integer> cart_price = new ArrayList<>();
    ArrayList<Integer> cart_value = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        db =  new DatabaseHelper(this);

        cartView();

        listView = findViewById(R.id.cart_list);

        CartAdapter cartAdapter = new CartAdapter(this, cart_name, cart_price, cart_value);
        listView.setAdapter(cartAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                builder.setTitle("Hapus barang?");
                builder.setPositiveButton("Ya", (dialog, which) -> {
                    String index = cart_id.get(position).toString();
                    db.deleteItemCart(index);
                    startActivity(getIntent());
                    Toast.makeText(CartActivity.this, "Barang berhasil dihapus",
                            Toast.LENGTH_SHORT).show();
                });
                builder.setNegativeButton("Tidak", (dialog, which) -> dialog.dismiss());
                builder.show();
            }
        });
    }

    private void startActivity(CartActivity cartActivity) {
    }

    private class CartAdapter extends ArrayAdapter<String> {
        Context context;
        ArrayList<String> pName;
        ArrayList<Integer> pPrice;
        ArrayList<Integer> pValue;

        CartAdapter (Context c, ArrayList<String> mName, ArrayList<Integer> mPrice,
                     ArrayList<Integer> mValue) {
            super(c, R.layout.cart_row, R.id.cart_name, cart_name);

            this.context = c;
            this.pName = mName;
            this.pPrice = mPrice;
            this.pValue = mValue;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View cart_row = layoutInflater.inflate(R.layout.cart_row, parent, false);
            TextView cart_name = cart_row.findViewById(R.id.cart_name);
            TextView cart_price = cart_row.findViewById(R.id.cart_price);
            TextView cart_value = cart_row.findViewById(R.id.cart_value);

            cart_name.setText(pName.get(position));
            cart_price.setText(String.valueOf(pPrice.get(position)));
            cart_value.setText(String.valueOf(pValue.get(position)));

            return cart_row;
        }
    }

    public void cartView(){
        Cursor cursor = db.viewCart();
        if(cursor.getCount() == 0) {
            Toast.makeText(this, "Tidak ada barang", Toast.LENGTH_SHORT).show();
        } else {
            while(cursor.moveToNext()) {
                cart_id.add(cursor.getInt(0));
                cart_name.add(cursor.getString(1));
                cart_price.add(cursor.getInt(2));
                cart_value.add(cursor.getInt(3));
            }
        }
    }


    public void ClickMain(View view) {
        Intent in = new Intent(this, MainActivity.class);
        startActivity(in);
    }

    public void ClickDelete(View view) {
        Cursor cursor = db.viewCart();
        if(cursor.getCount() == 0) {
            Toast.makeText(this, "Tidak ada barang, silahkan isi keranjang belanja"
                    , Toast.LENGTH_SHORT).show();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Hapus seluruh isi keranjang belanja?");
            builder.setPositiveButton("Ya", (dialog, which) -> {
                db.deleteAllCart();
                Intent in = new Intent(this, CartActivity.class);
                startActivity(in);
                Toast.makeText(CartActivity.this, "Berhasil menghapus seluruh isi" +
                        " keranjang belanja", Toast.LENGTH_SHORT).show();
            });
            builder.setNegativeButton("Tidak", (dialog, which) -> dialog.dismiss());
            builder.show();
        }
    }

    public void ClickPay(View view) {
        Cursor cursor = db.viewCart();
        if(cursor.getCount() == 0) {
            Toast.makeText(this, "Tidak ada barang, silahkan isi keranjang belanja " +
                            "untuk melanjutkan pembayaran", Toast.LENGTH_SHORT).show();
        }
        else {
            Intent in = new Intent(this, CalculationActivity.class);
            startActivity(in);
        }
    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent(this, MainActivity.class);
        startActivity(in);
    }
}