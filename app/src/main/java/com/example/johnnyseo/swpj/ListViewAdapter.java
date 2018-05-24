package com.example.johnnyseo.swpj;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter{
    Context context;
    ArrayList<Listviewitem> list_itemArrayList;
    TextView rowNum;
    TextView trainLineNm;
    TextView subwayId;
    TextView arvlMsg2;
    TextView btrainNo;

    public ListViewAdapter(Context context, ArrayList<Listviewitem> list_itemArrayList) {
        this.context = context;
        this.list_itemArrayList = list_itemArrayList;
    }

    @Override
    public int getCount() {
        return this.list_itemArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.list_itemArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item,null);

            //뷰 레이아웃 생성
            rowNum = (TextView) convertView.findViewById(R.id.rowNum);
            trainLineNm =(TextView) convertView.findViewById(R.id.trainLineNm);
            subwayId =(TextView) convertView.findViewById(R.id.subwayId);
            arvlMsg2 =(TextView) convertView.findViewById(R.id.arvlMsg2);
            btrainNo =(TextView) convertView.findViewById(R.id.btrainNo);


            // 값 입력
            rowNum.setText(list_itemArrayList.get(position).getRowNum());
            trainLineNm.setText(list_itemArrayList.get(position).getTrainLineNm());
            subwayId.setText(list_itemArrayList.get(position).getSubwayId());
            arvlMsg2.setText(list_itemArrayList.get(position).getArvlMsg2());
            btrainNo.setText(list_itemArrayList.get(position).getBtrainNo());
            rowNum.setVisibility(View.GONE);
        }

        return convertView;
    }
}
