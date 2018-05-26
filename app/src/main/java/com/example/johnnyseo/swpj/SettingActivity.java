package com.example.johnnyseo.swpj;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {
    TextView memberInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Intent intent=new Intent(this.getIntent());

        String info ="";
        SharedPreferences pref;
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        String userId = pref.getString("userId",null);
        String userName = pref.getString("userName",null);
        String userPhone = pref.getString("userPhone",null);
        Boolean certAprvTF = pref.getBoolean("certAprvTF",false);
        Boolean certUploadTF = pref.getBoolean("certUploadTF",false);


        info = "아이디(이메일) : " + userId + "\n성  명 : " + userName + "\n연락처 : " + userPhone;
        if (certUploadTF==true){
            info = info + "\n인증서 등록 : 완료";
        } else {
            info = info + "\n인증서 등록 : 없음. 인증파일을 제출해주세요.";
        }
        if (certAprvTF==true && certUploadTF==true){
            info = info + "\n임산부인증 : 완료";
        } else if (certAprvTF==false && certUploadTF==true){
            info = info + "\n임산부인증 : 인증승인 대기중입니다.";
        } else{

        }
        memberInfo = (TextView) findViewById(R.id.memberInfo);
        memberInfo.setText(info);


    }

    public void onLetmeConfirmClicked (View v){
        Toast.makeText(getApplicationContext(), "인증요청 버튼", Toast.LENGTH_LONG).show();

        Intent intent=new Intent(SettingActivity.this,LetMeConfirmActivity.class);
        startActivity(intent);
    }

}
