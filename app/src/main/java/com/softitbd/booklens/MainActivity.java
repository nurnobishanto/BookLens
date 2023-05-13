package com.softitbd.booklens;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {



    private static int PICTURE = 0;
    private ImageView img;
    private TextView textview,name,count;
    private EditText filter;
    private Button snapBtn;
    private Button detectBtn;
    private Uri selectedImageUri;
    private FirebaseAuth mAuth;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // on below line we are initializing our variables.
        img = (ImageView) findViewById(R.id.image);
        textview = (TextView) findViewById(R.id.text);
        count = (TextView) findViewById(R.id.count);
        name = (TextView) findViewById(R.id.name);
        filter = (EditText) findViewById(R.id.filter);

        snapBtn = (Button) findViewById(R.id.snapbtn);
        detectBtn = (Button) findViewById(R.id.detectbtn);


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(user.getUid());
        // Read from the database
        myRef.child("name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    name.setText("Hi, "+String.valueOf(task.getResult().getValue()));

                }
            }
        });


        // adding on click listener for detect button.
        detectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calling a method to
                    if (filter.getText().toString().isEmpty()){
                        Toast.makeText(MainActivity.this,"Filter Text Empty",Toast.LENGTH_LONG).show();

                    }else {
                        try {
                            detectTxt();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }




            }
        });

        snapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(MainActivity.this)
                        .crop()
                        .start();



            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
        // Image Uri will not be null for RESULT_OK
             selectedImageUri = data.getData();
            // Use Uri object instead of File to avoid storage permissions
            img.setImageURI(selectedImageUri);

            img.setVisibility(View.VISIBLE);
            count.setVisibility(View.GONE);
            textview.setVisibility(View.GONE);
            img.setImageURI(selectedImageUri);

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }
    private void detectTxt() throws IOException {
        FirebaseVisionImage image;
        image = FirebaseVisionImage.fromFilePath(this,selectedImageUri);
        FirebaseVisionTextDetector detector = FirebaseVision.getInstance().getVisionTextDetector();
        detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
              
                processTxt(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Fail to detect the text from image...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processTxt(FirebaseVisionText text) {
  
        List<FirebaseVisionText.Block> blocks = text.getBlocks();

   
        if (blocks.size() == 0) {
          
            Toast.makeText(MainActivity.this, "No Text ", Toast.LENGTH_LONG).show();
            return;
        }
        String txt = "";
        for (FirebaseVisionText.Block block : text.getBlocks()) {

            textview.setVisibility(View.VISIBLE);
            count.setVisibility(View.VISIBLE);

            txt = txt +" "+block.getText();
            String filterTxt = filter.getText().toString();
            if (txt.toLowerCase(Locale.ROOT).contains(filterTxt.toLowerCase(Locale.ROOT))){
                textview.setText("Detect: "+txt);
                setHighLightedText(textview, filterTxt.toLowerCase(Locale.ROOT));
            }else {
                count.setText("Result : Not Matched!");
                textview.setText("Detect: "+txt);

            }
        }
    }

    private void setHighLightedText(TextView textview, String filterTxt) {

        String tvt = textview.getText().toString().toLowerCase(Locale.ROOT);
        int ofe = tvt.indexOf(filterTxt, 0);
        Spannable wordToSpan = new SpannableString(textview.getText());
        int cnt=0;
        for (int ofs = 0; ofs < tvt.length() && ofe != -1; ofs = ofe + 1) {
            ofe = tvt.indexOf(filterTxt, ofs);
            if (ofe == -1)
                break;
            else {
                // set color here
                wordToSpan.setSpan(new BackgroundColorSpan(0xFFFFFF00), ofe, ofe + filterTxt.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                textview.setText(wordToSpan, TextView.BufferType.SPANNABLE);
                cnt++;

            }
        }
        count.setText("Matched and Count : "+cnt);

    }

    public void Logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainActivity.this,LoginActivity.class));
        finish();
    }
}