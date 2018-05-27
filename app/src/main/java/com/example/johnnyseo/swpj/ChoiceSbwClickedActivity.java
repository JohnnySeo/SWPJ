package com.example.johnnyseo.swpj;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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

public class ChoiceSbwClickedActivity extends AppCompatActivity {
    String sendMsg = "";
    String clickedbTrainNo="";
    TextView seatInfo;
    String seatInfoTemp="";
    StringBuffer seatInfoTempSB = new StringBuffer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choicesbwclicked);

        Intent intent = getIntent();
        TextView trainNo = (TextView) findViewById(R.id.choicedTrainNo);
        seatInfo = (TextView) findViewById(R.id.seatinfo);

        clickedbTrainNo = intent.getStringExtra("bTrainNo");
        trainNo.setText(clickedbTrainNo);


        // 좌석상태정보 JSON으로 받아오기
        new JsonTask().execute("http://203.246.82.142:8080/SWPJ/JSONServer_seat.jsp", clickedbTrainNo);







    }

    private class JsonTask extends AsyncTask<String, String, String> {


        protected void onPreExecute() {
            super.onPreExecute();

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
                sendMsg = "bTrainNo="+strings[1];
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

                String [] testBeaconNo = new String[json.length()];
                Boolean [] testSeatInfo = new Boolean[json.length()];
                for (int i = 0; i<json.length(); i++) {
                    testBeaconNo[i] = (String) json.getJSONObject(i).get("beaconNum").toString();
                    testSeatInfo[i] = (Boolean) json.getJSONObject(i).get("seatInfo");
                    if (testSeatInfo[i].equals(true)) {
                        seatInfoTempSB.append((i+1)+"번칸 좌석은 이용이 가능합니다.\n");
                    }else if (testSeatInfo[i].equals(false)){
                        seatInfoTempSB.append((i+1)+"번칸 좌석은 이용이 불가능합니다.\n");
                    }
                }

                return seatInfoTemp;

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
            return seatInfoTemp;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            seatInfo.setText(seatInfoTempSB.toString());
        }
    }


}
