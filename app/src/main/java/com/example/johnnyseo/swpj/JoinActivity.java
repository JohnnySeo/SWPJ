package com.example.johnnyseo.swpj;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class JoinActivity extends Activity {

    EditText userId, userPwd,userPwdConfirm, userName, userPhone;
    Button loginBtn, joinBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        Intent intent=new Intent(this.getIntent());

        userId = (EditText) findViewById(R.id.userId);
        userName = (EditText) findViewById(R.id.userName);
        userPwd = (EditText) findViewById(R.id.userPwd);
        userPwdConfirm = (EditText) findViewById(R.id.userPwdConfirm);
        userPhone = (EditText) findViewById(R.id.userPhone);
        joinBtn = (Button) findViewById(R.id.joinBtn);
        joinBtn.setOnClickListener(btnListener);
    }

    class CustomTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;
        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;
                URL url = new URL("http://203.246.82.142:8080/SWPJ/data.jsp");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                sendMsg = "userId="+strings[0]+"&userName="+strings[1]+"&userPwd="+strings[2]+"&userPhone="+strings[3]+"&type="+strings[4];
                osw.write(sendMsg);
                osw.flush();
                if(conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    receiveMsg = buffer.toString();

                } else {
                    Log.i("통신 결과", conn.getResponseCode()+"에러");
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return receiveMsg;
        }
    }

    // 버튼 클릭 이벤트 리스너
    View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.joinBtn : // 회원가입 버튼 눌렀을 경우

                    String joinid = userId.getText().toString();
                    String joinname = userName.getText().toString();
                    String joinpwd = userPwd.getText().toString();
                    String joinpwdConfirm = userPwdConfirm.getText().toString();
                    String joinphone = userPhone.getText().toString();

                    /*유효성검사*/
                    if ( userId.getText().toString().length() == 0 ) {
                        Toast.makeText(JoinActivity.this, "Email을 입력하세요", Toast.LENGTH_SHORT).show();
                        userId.requestFocus();
                        return;
                    }

                    if ( userName.getText().toString().length() == 0 ) {
                        Toast.makeText(JoinActivity.this, "이름을 입력하세요", Toast.LENGTH_SHORT).show();
                        userName.requestFocus();
                        return;
                    }

                    if ( userPwd.getText().toString().length() == 0 ) {
                        Toast.makeText(JoinActivity.this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                        userPwd.requestFocus();
                        return;
                    }

                    if ( userPwdConfirm.getText().toString().length() == 0 ) {
                        Toast.makeText(JoinActivity.this, "비밀번호 확인을 입력하세요", Toast.LENGTH_SHORT).show();
                        userPwdConfirm.requestFocus();
                        return;
                    }

                    if ( !userPwd.getText().toString().equals(userPwdConfirm.getText().toString()) ) {
                        Toast.makeText(JoinActivity.this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                        userPwd.setText("");
                        userPwdConfirm.setText("");
                        userPwd.requestFocus();
                        return;
                    }

                    if ( userPhone.getText().toString().length() == 0 ) {
                        Toast.makeText(JoinActivity.this, "전화번호를 입력하세요", Toast.LENGTH_SHORT).show();
                        userPhone.requestFocus();
                        return;
                    }



                    try {
                        String result  = new CustomTask().execute(joinid,joinname,joinpwd,joinphone,"join").get();
                        if(result.equals("id")) {
                            Toast.makeText(JoinActivity.this,"이미 존재하는 아이디입니다.",Toast.LENGTH_SHORT).show();
                            userId.setText("");
                            userName.setText("");
                            userPwd.setText("");
                            userPwdConfirm.setText("");
                            userPhone.setText("");
                        } else if(result.equals("ok")) {
                            userId.setText("");
                            userName.setText("");
                            userPwd.setText("");
                            userPwdConfirm.setText("");
                            userPhone.setText("");
                            Toast.makeText(JoinActivity.this,"회원가입을 축하합니다.",Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(JoinActivity.this,MainActivity.class);
                            startActivity(intent);
                        }
                    }catch (Exception e) {}
                    break;
            }
        }
    };
}
