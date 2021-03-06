package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.lib.QRCodeUtil.QRCodeUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import static android.Manifest.permission.ACCESS_MEDIA_LOCATION;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    private ImageView mImageView;
    private EditText medittext;
    private Button mbutton, mbutton_share;
    private Context context;
    private String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_main);
        mImageView = findViewById(R.id.iv);
        mbutton = findViewById(R.id.input_text_bt);
        medittext = findViewById(R.id.input_text);
        mbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageView.setImageBitmap(QRCodeUtil.createQRCodeBitmap(String.valueOf(medittext.getText()), 800));
            }
        });
        mbutton_share = findViewById(R.id.input_text_bt_1);
        mbutton_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // allShare();
                long timeGetTime = new Date().getTime();
                name = "" + timeGetTime;
                BitmapDrawable bd = (BitmapDrawable) mImageView.getDrawable();
                Bitmap bitmap = bd.getBitmap();
                saveBitmap(name, bitmap, context);
                shareImage(context, name);
            }
        });
    }

    public void deleteImage(String name){
        File file = new File("/storage/emulated/0/share_qq/"+name);
        if(file.exists()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                requestPermissions(new String[]{ACCESS_MEDIA_LOCATION}, 1);

            }
            file.setExecutable(true,false);
            file.setReadable(true,false);

            file.setWritable(true,false);
            file.delete();
        }

    }

    public void allShare() {
        Intent share_intent = new Intent();
        share_intent.setAction(Intent.ACTION_SEND);
        share_intent.setType("text/plain");
        share_intent.putExtra(Intent.EXTRA_SUBJECT, "share");
        share_intent.putExtra(Intent.EXTRA_TEXT, "share with you:" + "android");
        share_intent = Intent.createChooser(share_intent, "share");
        startActivity(share_intent);
    }


    public void shareImage(Context context, String name) {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, 1);
        }
        Uri uri = FileProvider.getUriForFile(context.getApplicationContext(), "com.example.myapplication.fileprovider", new File("/storage/emulated/0/share_qq/" + name));
        grantUriPermission(getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);  //???????????????????????? ??????????????????
        intent.putExtra(Intent.EXTRA_TEXT, "??????");   //?????????????????????
        intent.putExtra(Intent.EXTRA_SUBJECT, "??????");
        intent.setType("image/*");   //????????????
        intent = Intent.createChooser(intent, "share");

        startActivity(intent);
    }

    public void saveBitmap(String name, Bitmap bm, Context mContext) {
        //???????????????????????????????????????
        String TargetPath = "/storage/emulated/0/share_qq";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, 1);

        }
        if (!FileUtils.fileIsExist(TargetPath)) {
            Log.d("Save Bitmap", "TargetPath isn't exist");
        } else {
            //??????????????????????????????????????????
            //?????????????????????????????????????????????????????????????????????????????????
            File saveFile = new File(TargetPath, name);

            try {
                FileOutputStream saveImgOut = new FileOutputStream(saveFile);
                // compress - ???????????????
                bm.compress(Bitmap.CompressFormat.JPEG, 80, saveImgOut);
                //??????????????????????????????????????????
                saveImgOut.flush();
                saveImgOut.close();
                Log.d("Save Bitmap", "The picture is save to your phone!");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}

class FileUtils {
    /**
     * ??????????????????????????????????????????????????????????????????????????????????????????
     *
     * @param fileName ????????????
     * @return ?????????????????? TRUE or FALSE
     */
    static boolean fileIsExist(String fileName) {
        //??????????????????????????????????????????????????????
        File file = new File(fileName);
        if (file.exists())
            return true;
        else {
            //file.mkdirs() ????????????????????????
            return file.mkdirs();
        }
    }
}