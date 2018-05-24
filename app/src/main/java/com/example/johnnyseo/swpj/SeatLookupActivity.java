package com.example.johnnyseo.swpj;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SeatLookupActivity extends AppCompatActivity {
    EditText sttnName;
    String sttnNameEncoded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seatlookup);

        sttnName = (EditText)findViewById(R.id.SttnName);

        Intent intent=new Intent(this.getIntent());
    }

    public void onSeatLookup(View v){
        Intent intent=new Intent(SeatLookupActivity.this,ChoiceSubwayActivity.class);

        sttnNameEncoded = sttnName.getText().toString();
        try {
            sttnNameEncoded = URLEncoder.encode(sttnNameEncoded,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        intent.putExtra("sttnNameEncoded", sttnNameEncoded);
        startActivity(intent);
    }


}
