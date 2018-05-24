package com.example.johnnyseo.swpj;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Intent intent=new Intent(this.getIntent());
    }

    public void onLetmeConfirmClicked (View v){
        Toast.makeText(getApplicationContext(), "인증요청 버튼", Toast.LENGTH_LONG).show();

        Intent intent=new Intent(SettingActivity.this,LetMeConfirmActivity.class);
        startActivity(intent);
    }

}
