package com.jack.j_cashiermobile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.app.Activity;
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

public class MainActivity extends AppCompatActivity {

    public static FragmentManager fragmentManager;
    DatabaseHelper db;

    DrawerLayout drawerLayout;
    ListView listView;

    ArrayList<Integer> sId = new ArrayList<>();
    ArrayList<String> sName = new ArrayList<>();
    ArrayList<Integer> sPrice = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db =  new DatabaseHelper(this);

        fragmentManager = getSupportFragmentManager();
        drawerLayout = findViewById(R.id.drawer_layout);
        listView = findViewById(R.id.shop_list);

        productView();

        db.TransactionExit();
        createTransaction();

        MainAdapter adapter = new MainAdapter(this, sName, sPrice);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),OrderActivity.class);
                intent.putExtra("ids", sId.get(position));
                intent.putExtra("name", sName.get(position));
                intent.putExtra("price", sPrice.get(position));
                startActivity(intent);
            }
        });
    }


    private void productView() {
        Cursor cursor = db.viewProduct();
        if(cursor.getCount() == 0) {
            Toast.makeText(this, "Tidak ada produk, silahkan tambahkan produk",
                    Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Tabel produk kosong! Ingin menambahkan produk?");
            builder.setPositiveButton("Ya", (dialog, which) -> {
                Intent intent = new Intent(getApplicationContext(),AddProductActivity.class);
                intent.putExtra("password", 1);
                startActivity(intent);
            });
            builder.setNegativeButton("Tidak", (dialog, which) -> dialog.dismiss());
            builder.show();
        } else {
            while(cursor.moveToNext()) {
                sId.add(cursor.getInt(0));
                sName.add(cursor.getString(1));
                sPrice.add(cursor.getInt(2));
            }
        }
    }

    private void createTransaction() {
        Cursor cursor = db.viewAllTransaction();
        if(cursor.getCount() == 0) {
            db.transactionId = 1;
        }
        else {
            while(cursor.moveToNext()) {
                db.transactionId = cursor.getInt(0);
            }
        }
        db.addTransaction(0, 0, 0);

    }

    private class MainAdapter extends ArrayAdapter<String> {
        Context context;
        ArrayList<String> pName;
        ArrayList<Integer> pPrice;

        MainAdapter (Context c, ArrayList<String> mName, ArrayList<Integer> mPrice) {
            super(c, R.layout.shop_row, R.id.product_name, sName);

            this.context = c;
            this.pName = mName;
            this.pPrice = mPrice;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View shop_row = layoutInflater.inflate(R.layout.shop_row, parent, false);
            TextView product_name = shop_row.findViewById(R.id.product_name);
            TextView product_price = shop_row.findViewById(R.id.product_price);

            product_name.setText(pName.get(position));
            product_price.setText(String.valueOf(pPrice.get(position)));
            return shop_row;
        }
    }

    public void ClickMenu(View view) {
        openDrawer(drawerLayout);
    }

    private static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void ClickLogo(View view) {
        closeDrawer(drawerLayout);
    }

    private static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void ClickHint(View view) {
        Intent intent = new Intent(getApplicationContext(),HintActivity.class);
        intent.putExtra("extra", 1);
        startActivity(intent);
        closeDrawer(drawerLayout);
    }

    public void ClickAbout(View view) {
        Intent in = new Intent(this, AboutActivity.class);
        startActivity(in);
        closeDrawer(drawerLayout);
    }

    public void ClickCart(View view) {
        Intent in = new Intent(this, CartActivity.class);
        startActivity(in);
    }

    public void MainExit(View view) {
        exitapp(this);
    }

    private void exitapp(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Keluar dari aplikasi?");
        builder.setPositiveButton("Ya", (dialog, which) -> {
            db.deleteAllCart();
            activity.finishAffinity();
            System.exit(0);
        });
        builder.setNegativeButton("Tidak", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }

    public void ClickReport(View view) {
        try {
            Cursor cursor = db.viewAllTransaction();
            if(cursor.getCount() == 1) {
                Toast.makeText(this, "Tidak ada data transaksi tersimpan"
                        , Toast.LENGTH_SHORT).show();
            }
            else {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_SUBJECT, "JReport");
                intent.putExtra(Intent.EXTRA_TEXT, db.getTableAsString(db.getReadableDatabase()));

                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        } catch (Exception e) {

            Toast.makeText(getBaseContext(), "Gagal memuat database", Toast.LENGTH_LONG)
                    .show();

        }
    }

    public void ClickDatabase(View view) {
        Cursor cursor = db.viewAllTransaction();
        if(cursor.getCount() == 1) {
            Toast.makeText(this, "Tidak ada data transaksi tersimpan"
                    , Toast.LENGTH_SHORT).show();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Hapus seluruh database transaksi?");
            builder.setPositiveButton("Ya", (dialog, which) -> {
                db.deleteAllCart();
                db.deleteAllDetail();
                db.deleteAllTransaction();
                Intent in = new Intent(this, MainActivity.class);
                startActivity(in);
                Toast.makeText(MainActivity.this, "Hapus database berhasil",
                        Toast.LENGTH_SHORT).show();
            });
            builder.setNegativeButton("Tidak", (dialog, which) -> dialog.dismiss());
            builder.show();
        }
    }

    public void ClickToProduct(View view) {
        Intent in = new Intent(this, ProductActivity.class);
        startActivity(in);
    }

    @Override
    public void onBackPressed() {
        exitapp(this);
    }
}