package com.example.johnnyseo.swpj;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class ChoiceSubwayActivity extends AppCompatActivity {

    ListView listView;
    ListViewAdapter listViewAdapter;
    ArrayList<Listviewitem> list_itemArrayList;
    String sttnNameEncoded;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choicesubway);
        Intent intent=new Intent(this.getIntent());

        sttnNameEncoded = intent.getStringExtra("sttnNameEncoded");

        listView = (ListView) findViewById(R.id.listView);
        list_itemArrayList = new ArrayList<Listviewitem>();

        getApi();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ChoiceSbwClickedActivity.class);
                /* putExtra의 첫 값은 식별 태그, 뒤에는 다음 화면에 넘길 값 */
                intent.putExtra("bTrainNo","7401");
                startActivity(intent);
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void getApi(){
        new AsyncTask<Void, Void, String>() {
            ProgressDialog progress;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress = new ProgressDialog(ChoiceSubwayActivity.this);
                progress.setTitle("안내");
                progress.setMessage("지하철 정보를 불러오는 중입니다.");
                progress.setProgressStyle((ProgressDialog.STYLE_SPINNER));
                progress.setCancelable(false);
                progress.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject json = new JSONObject(s);
                    JSONArray rows = json.getJSONArray("realtimeArrivalList");
                    int length = rows.length();
                    for(int i=0; i < length; i ++){
                        JSONObject result = (JSONObject) rows.get(i);
                        String subwayId;
                        String rowNum = result.getString("rowNum");
                        String rawsubwayId = result.getString("subwayId");
                        String trainLineNm = result.getString("trainLineNm");
                        String arvlMsg2 = result.getString("arvlMsg2");
                        String btrainNo = result.getString("btrainNo");

                        switch (rawsubwayId) {
                            case "1001":
                                subwayId = "1";
                                break;
                            case "1002":
                                subwayId = "2";
                                break;
                            case "1003":
                                subwayId = "3";
                                break;
                            case "1004":
                                subwayId = "4";
                                break;
                            case "1005":
                                subwayId = "5";
                                break;
                            case "1006":
                                subwayId = "6";
                                break;
                            case "1007":
                                subwayId = "1";
                                break;
                            case "1008":
                                subwayId = "1";
                                break;
                            default:
                                subwayId = "배려석 미지원";
                                break;
                        }

                        // 1~8호선 제외하고 리스트에 포함하지 않음.
                        if(subwayId.equals("배려석 미지원")){

                        }else{
                            list_itemArrayList.add(new Listviewitem(rowNum, trainLineNm, subwayId, arvlMsg2, btrainNo));
                        }
                    }
                }catch (Exception e ){}

                listViewAdapter = new ListViewAdapter(ChoiceSubwayActivity.this,list_itemArrayList);
                listView.setAdapter(listViewAdapter);

                progress.dismiss();
            }

            @Override
            protected String doInBackground(Void... params) {
                String result = "";
                try {
                    //서울시 오픈 API 제공(샘플 주소 json으로 작업)
                    result = Remote.getData("http://swopenapi.seoul.go.kr/api/subway/4b50784d556c75633633487446526c/json/realtimeStationArrival/0/10/" + sttnNameEncoded);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return result;
            }
        }.execute();
    }

}

