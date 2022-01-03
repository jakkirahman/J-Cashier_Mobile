package com.jack.j_cashiermobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class AboutActivity extends AppCompatActivity {

    ImageButton askAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        askAbout = findViewById(R.id.ask_about);
        askAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendEmail = new Intent(Intent.ACTION_SEND);
                sendEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{"jakkirahman@mhs.eng.upr.ac.id"});
                sendEmail.putExtra(Intent.EXTRA_SUBJECT, "Bug J-Cashier Mobile");
                sendEmail.putExtra(Intent.EXTRA_TEXT, "Saya menemukan bug berupa");
                sendEmail.setType("message/rfc822");
                startActivity(sendEmail);
            }
        });
    }

    public void ClickBack(View view) {
        Intent in = new Intent(this, MainActivity.class);
        startActivity(in);
    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent(this, MainActivity.class);
        startActivity(in);
    }
}