package com.example.johnnyseo.swpj;

import android.Manifest;
import android.app.Activity;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LetMeConfirmActivity extends AppCompatActivity {

    private static final int PICK_FROM_FILE = 100;

    Button uploadCert;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letmeconfirm);

        verifyStoragePermissions(this);

        uploadCert = (Button)findViewById(R.id.uploadCert);
        uploadCert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
//                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(intent, PICK_FROM_FILE);

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                Intent chooser = Intent.createChooser(intent,"1a2a3a");

                startActivityForResult(chooser, PICK_FROM_FILE);

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == PICK_FROM_FILE){
            if(resultCode == RESULT_OK){
                Uri selectImage = data.getData();
                uploadImage(selectImage);
                Toast.makeText(getApplicationContext(), "업로드 완료", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(LetMeConfirmActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }



    public void uploadImage(Uri uri){

        UploadService service = MyRetrofit2.getRetrofit2().create(UploadService.class);

        File file = new File(getRealPathFromURI(uri));

        SharedPreferences pref;
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        String userId = pref.getString("userId",null);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("certUploadTF");
        editor.putBoolean("certUploadTF",true);
        editor.commit();

        Toast.makeText(getApplicationContext(), "전송완료", Toast.LENGTH_LONG).show();
        MultipartBody.Part body1 = prepareFilePart(userId, uri);

        RequestBody description = createPartFromString("hello, this is description speaking");

        Call<ResponseBody> call = service.uploadFile(description, body1);


        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }



    public static final String MULTIPART_FORM_DATA = "multipart/form-data";

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(
                MediaType.parse(MULTIPART_FORM_DATA), descriptionString);
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        File file = new File(getRealPathFromURI(fileUri));
        RequestBody requestFile = RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }


    // Storage Permissions variables
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //persmission method.
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // save file

            } else {
                Toast.makeText(getApplicationContext(), "PERMISSION_DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}






