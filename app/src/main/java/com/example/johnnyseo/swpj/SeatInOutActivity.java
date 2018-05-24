package com.example.johnnyseo.swpj;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.TextView;





public class SeatInOutActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private TextView tagDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seatinout);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // NFC 미지원단말체크
        if (nfcAdapter == null) {
            Toast.makeText(getApplicationContext(), "NFC를 지원하지 않는 단말기입니다.", Toast.LENGTH_SHORT).show();
            return;
        }else{
            Toast.makeText(getApplicationContext(), "NFC를 지원하는 단말기입니다.", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override

    protected void onResume() {
        super.onResume();

        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    @Override

    protected void onPause() {
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
        super.onPause();
    }

    @Override

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            byte[] tagId = tag.getId();
            tagDesc.setText("TagID: " + toHexString(tagId));
        }
    }

    public static final String CHARS = "0123456789ABCDEF";



    public static String toHexString(byte[] data) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < data.length; ++i) {

            sb.append(CHARS.charAt((data[i] >> 4) & 0x0F))

                    .append(CHARS.charAt(data[i] & 0x0F));

        }

        return sb.toString();

    }















}
