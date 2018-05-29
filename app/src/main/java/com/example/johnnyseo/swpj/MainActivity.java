package com.example.johnnyseo.swpj;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends Activity {

    EditText userId, userPwd;
    Button loginBtn, joinBtn;
    String output = "";
    String sendMsg = "";
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        userId = (EditText) findViewById(R.id.userId);
        userPwd = (EditText) findViewById(R.id.userPwd);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        joinBtn = (Button) findViewById(R.id.joinBtn);


        loginBtn.setOnClickListener(btnListener);
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
                sendMsg = "userId="+strings[0]+"&userPwd="+strings[1]+"&type="+strings[2];
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
                case R.id.loginBtn : // 로그인 버튼 눌렀을 경우
                    String loginid = userId.getText().toString();
                    String loginpwd = userPwd.getText().toString();
                    try {
                        String result  = new CustomTask().execute(loginid,loginpwd,"login").get();
                        if(result.equals("true")) {
                            Toast.makeText(MainActivity.this,"로그인",Toast.LENGTH_SHORT).show();

                            // 로그인한 회원정보 JSON으로 받아오기
                            new JsonTask().execute("http://203.246.82.142:8080/SWPJ/JSONServer_member.jsp", loginid);

                            Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                            startActivity(intent);

                        } else if(result.equals("false")) {
                            Toast.makeText(MainActivity.this,"아이디 또는 비밀번호가 틀렸음",Toast.LENGTH_SHORT).show();
                            userId.setText("");
                            userPwd.setText("");
                        } else if(result.equals("noId")) {
                            Toast.makeText(MainActivity.this,"존재하지 않는 아이디",Toast.LENGTH_SHORT).show();
                            userId.setText("");
                            userPwd.setText("");
                        }



                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.joinBtn : // 회원가입 버튼 눌렀을 경우
                    Intent intent=new Intent(MainActivity.this,JoinActivity.class);
                    startActivity(intent);
            }
        }

    };

    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... strings) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();

                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestMethod("POST");
                connection.connect();
                OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
                sendMsg = "userId="+strings[1];
                osw.write(sendMsg);
                osw.flush();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();

                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)
                }

                JSONArray json = null;
                json = new JSONArray(buffer.toString());
                String testUserId = (String) json.getJSONObject(json.length()-1).get("userId").toString();
                String testUserName = (String) json.getJSONObject(json.length()-1).get("userName").toString();
                String testUserPhone = (String) json.getJSONObject(json.length()-1).get("userPhone").toString();
                Boolean testCertAprvTF = (Boolean)  json.getJSONObject(json.length()-1).get("certAprvTF");
                Boolean testCertUploadTF = (Boolean) json.getJSONObject(json.length()-1).get("certUploadTF");
                Log.i("체크", "certAprvTF="+testCertAprvTF);

                /* SharedPreferences*/
                SharedPreferences pref= getSharedPreferences("pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.putString("userId",testUserId);
                editor.putString("userName",testUserName);
                editor.putString("userPhone",testUserPhone);
                if (testCertAprvTF==true){
                    editor.putBoolean("certAprvTF",testCertAprvTF);
                }else{
                    editor.putBoolean("certAprvTF",false);
                }
                if (testCertUploadTF==true){
                    editor.putBoolean("certUploadTF",testCertUploadTF);
                }else{
                    editor.putBoolean("certUploadTF",false);
                }
                editor.commit();

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()){
                pd.dismiss();
            }
        }
    }
}
