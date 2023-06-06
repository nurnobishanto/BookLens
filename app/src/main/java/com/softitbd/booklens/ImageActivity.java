package com.softitbd.booklens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ortiz.touchview.TouchImageView;

import java.util.ArrayList;

public class ImageActivity extends AppCompatActivity {
    TouchImageView imageView;
    TextView text;
    ArrayList<ImageModel> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        imageView = (TouchImageView) findViewById(R.id.image);
        text = (TextView) findViewById(R.id.text);
        list=new ArrayList<ImageModel>();

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        text.setText(title);
        boolean isBitmap = intent.getBooleanExtra("isBitmap",false);
        if (isBitmap){
            Bitmap bitmap = intent.getParcelableExtra("bitmap");
            imageView.setImageBitmap(bitmap);
        }else {
            Uri uri = intent.getParcelableExtra("uri");
            imageView.setImageURI(uri);
        }



    }
}