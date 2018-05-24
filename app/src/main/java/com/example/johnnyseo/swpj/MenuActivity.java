package com.example.johnnyseo.swpj;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Intent intent=new Intent(this.getIntent());


    }



    public void onSeatLookupClicked(View v){
        Intent intent=new Intent(MenuActivity.this,SeatLookupActivity.class);
        startActivity(intent);
    }

    public void onReqConsider(View v){
        /* 배려요청 처리 */
        Switch sButton = (Switch) findViewById(R.id.reqConsider);
        sButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton cb, boolean on){
                if(on)
                {
                    Toast.makeText(getApplicationContext(), "배려요청 On", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    Toast.makeText(getApplicationContext(), "배려요청 Off", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void onSeatInOut(View v){
        Intent intent=new Intent(MenuActivity.this,SeatInOutActivity.class);
        startActivity(intent);
    }

    public void onSetting(View v){
        Intent intent=new Intent(MenuActivity.this,SettingActivity.class);
        startActivity(intent);
    }


}
