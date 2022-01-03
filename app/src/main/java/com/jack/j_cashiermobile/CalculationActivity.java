package com.jack.j_cashiermobile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class CalculationActivity extends AppCompatActivity {

    DatabaseHelper db;

    TextView totalPrice, totalChange;
    EditText totalPay;
    Button donePayment;

    int dChange;
    int dPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculation);
        db =  new DatabaseHelper(this);

        totalPrice = findViewById(R.id.cal_total);
        totalPay = findViewById(R.id.cal_pay);
        totalChange = findViewById(R.id.cal_change);

        donePayment = findViewById(R.id.cal_done);

        Cursor cursor = db.viewTransaction();
        int total = 0, i;
        while (cursor.moveToNext()) {
            i = cursor.getInt(0);
            total = total + i;
        }

        totalPrice.setText(String.valueOf(total));

        int finalTotal = total;
        totalPay.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    if(totalPay.getText().toString().equals("")) {
                        Toast.makeText(CalculationActivity.this,
                                "Masukkan tidak tersedia, silahkan isi kembali",
                                Toast.LENGTH_SHORT).show();;
                    }
                    else {
                    dPay = Integer.parseInt(totalPay.getText().toString());
                    dChange = dPay - finalTotal;
                    totalChange.setText(String.valueOf(dChange));
                    }
                }
                return false;
            }
        });

        donePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dPay < finalTotal) {
                    Toast.makeText(CalculationActivity.this, "Silahkan isi nominal" +
                            " uang yang dibayar pada borang Jumlah Uang Yang Dibayar",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Cursor getId = db.viewAllTransaction();
                    while(getId.moveToNext()) {
                        db.transactionId = getId.getInt(0);
                    }
                    Cursor cursor1 = db.viewAllCart();
                    int idP, mValue, mSub;
                    while (cursor1.moveToNext()) {
                        idP = cursor1.getInt(1);
                        mValue = cursor1.getInt(2);
                        mSub = cursor1.getInt(3);
                        db.addDetail(idP, mSub, mValue);
                    }
                    db.updateTransaction(finalTotal, dPay, dChange);
                    db.deleteAllCart();
                    db.transactionId++;
                    db.addTransaction(0, 0, 0);
                    Intent in = new Intent(CalculationActivity.this, MainActivity.class);
                    startActivity(in);
                    Toast.makeText(CalculationActivity.this,"Transaksi senilai "
                            + finalTotal + " berhasil", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void ClickCart(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Batalkan pembayaran?");
        builder.setPositiveButton("Ya", (dialog, which) -> {
            Intent in = new Intent(this, CartActivity.class);
            startActivity(in);
        });
        builder.setNegativeButton("Tidak", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    public void ClickCal(View view) {
        Intent in = new Intent(this, HintActivity.class);
        startActivity(in);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Batalkan pembayaran?");
        builder.setPositiveButton("Ya", (dialog, which) -> {
            Intent in = new Intent(this, CartActivity.class);
            startActivity(in);
        });
        builder.setNegativeButton("Tidak", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}