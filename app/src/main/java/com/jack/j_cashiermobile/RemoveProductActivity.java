package com.jack.j_cashiermobile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RemoveProductActivity extends AppCompatActivity {

    DatabaseHelper db;
    ListView listView;

    ArrayList<Integer> lId = new ArrayList<>();
    ArrayList<Integer> lNumber = new ArrayList<>();
    ArrayList<String> lName = new ArrayList<>();
    ArrayList<Integer> lPrice = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_product);

        db = new DatabaseHelper(this);

        productView();

        ProductAdapter adapter = new ProductAdapter(this, lNumber, lName, lPrice);

        listView = findViewById(R.id.product_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RemoveProductActivity.this);
                builder.setTitle("Hapus produk?");
                builder.setPositiveButton("Ya", (dialog, which) -> {
                    String index = lId.get(position).toString();
                    db.deleteItemCart(index);
                    db.deleteItemProduct(index);
                    startActivity(getIntent());
                    Toast.makeText(RemoveProductActivity.this, "Berhasil dihapus", Toast.LENGTH_SHORT).show();
                });
                builder.setNegativeButton("Tidak", (dialog, which) -> dialog.dismiss());
                builder.show();
            }
        });
    }

    private class ProductAdapter extends ArrayAdapter<String> {
        Context context;
        ArrayList<Integer> rNumber;
        ArrayList<String> rName;
        ArrayList<Integer> rPrice;

        ProductAdapter (Context c,
                        ArrayList<Integer> mNumber, ArrayList<String> mName, ArrayList<Integer> mPrice) {
            super(c, R.layout.product_row, R.id.product_delete_name, lName);

            this.context = c;
            this.rNumber = mNumber;
            this.rName = mName;
            this.rPrice = mPrice;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View product_row = layoutInflater.inflate(R.layout.product_row, parent, false);
            TextView product_number = product_row.findViewById(R.id.number);
            TextView product_name = product_row.findViewById(R.id.product_delete_name);
            TextView product_price = product_row.findViewById(R.id.product_delete_price);

            product_number.setText(String.valueOf(rNumber.get(position)));
            product_name.setText(rName.get(position));
            product_price.setText(String.valueOf(rPrice.get(position)));

            return product_row;
        }
    }

    private void productView() {
        Cursor cursor = db.viewProduct();
        int i = 0;
        if(cursor.getCount() == 0) {
            Toast.makeText(this, "Tidak ada produk", Toast.LENGTH_SHORT).show();
        } else {
            while(cursor.moveToNext()) {
                i++;
                lNumber.add(i);
                lId.add(cursor.getInt(0));
                lName.add(cursor.getString(1));
                lPrice.add(cursor.getInt(2));
            }
        }
    }

    public void ClickDelete(View view) {
        Cursor cursor = db.viewProduct();
        if(cursor.getCount() == 0) {
            Toast.makeText(this, "Tidak ada produk"
                    , Toast.LENGTH_SHORT).show();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Hapus seluruh produk?");
            builder.setPositiveButton("Ya", (dialog, which) -> {
                db.deleteAllCart();
                db.deleteAllProduct();
                Intent in = new Intent(this, ProductActivity.class);
                startActivity(in);
                Toast.makeText(RemoveProductActivity.this, "Seluruh produk berhasil dihapus", Toast.LENGTH_SHORT).show();
            });
            builder.setNegativeButton("Tidak", (dialog, which) -> dialog.dismiss());
            builder.show();
        }
    }

    public void ClickBack(View view) {
        Intent in = new Intent(this, ProductActivity.class);
        startActivity(in);
    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent(this, ProductActivity.class);
        startActivity(in);
    }
}