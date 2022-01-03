package com.jack.j_cashiermobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class HintActivity extends AppCompatActivity {

    ImageButton askHint;
    int extra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hint);

        Intent intent = getIntent();
        extra = intent.getIntExtra("extra", 0);

        askHint = findViewById(R.id.ask_hint);
        askHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendEmail = new Intent(Intent.ACTION_SEND);
                sendEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{"jakkirahman@mhs.eng.upr.ac.id"});
                sendEmail.putExtra(Intent.EXTRA_SUBJECT, "Bug J-Cashier Mobile");
                sendEmail.setType("message/rfc822");
                startActivity(sendEmail);
            }
        });
    }

    public void ClickBack(View view) {
        if(extra == 1) {
            Intent in = new Intent(HintActivity.this, MainActivity.class);
            startActivity(in);
        }
        else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if(extra == 1) {
            Intent in = new Intent(HintActivity.this, MainActivity.class);
            startActivity(in);
        }
        else {
            finish();
        }
    }
}