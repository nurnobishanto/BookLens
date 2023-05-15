package com.softitbd.booklens;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
//import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.ortiz.touchview.TouchImageView;

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
    private TouchImageView img;
    private TextView textview,name,count;
    private EditText filter;
    private Button snapBtn;
    private Button detectBtn,detectBtn2;
    private Uri selectedImageUri;
    private FirebaseAuth mAuth;
    private ProgressBar pb;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // on below line we are initializing our variables.
        img = (TouchImageView) findViewById(R.id.image);
        textview = (TextView) findViewById(R.id.text);
        count = (TextView) findViewById(R.id.count);
        name = (TextView) findViewById(R.id.name);
        filter = (EditText) findViewById(R.id.filter);
        pb = (ProgressBar) findViewById(R.id.load);
        pb.setVisibility(View.GONE);

        snapBtn = (Button) findViewById(R.id.snapbtn);
        detectBtn = (Button) findViewById(R.id.detectbtn);
        detectBtn2 = (Button) findViewById(R.id.detectbtn2);

        detectBtn.setVisibility(View.INVISIBLE);
        detectBtn2.setVisibility(View.INVISIBLE);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        detectBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
                        .getOnDeviceTextRecognizer();
                Bitmap bitmap = ((BitmapDrawable) img.getDrawable()).getBitmap();
                FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

                textRecognizer.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                // Process the detected text
                                if (filter.getText().toString().isEmpty()){
                                    Toast.makeText(MainActivity.this,"Filter Text Empty",Toast.LENGTH_LONG).show();

                                }else {
                                    processDetectedText(firebaseVisionText,bitmap);
                                }

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle the failure
                            }
                        });

            }
        });

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
//                        try {
//                            detectTxt();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                    }

            }
        });

        snapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(MainActivity.this)
                        .crop()
                        .compress(3072)
                        .start();
                img.setVisibility(View.GONE);
                pb.setVisibility(View.VISIBLE);
                detectBtn.setVisibility(View.INVISIBLE);

            }
        });
    }

    private void processDetectedText(FirebaseVisionText firebaseVisionText, Bitmap bitmap) {
        int c = 0;
        // 4. Process the recognized text results
        for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {
            for (FirebaseVisionText.Line line : block.getLines()) {
                for (FirebaseVisionText.Element element : line.getElements()) {
                    // Access the extracted text and its metadata
                    String text = element.getText();

                    Rect boundingBox = element.getBoundingBox();
                    if (text.toLowerCase(Locale.ROOT).equals(filter.getText().toString().toLowerCase(Locale.ROOT).trim())){
                        drawBoundingBoxOnImage(boundingBox, bitmap);
                        c++;
                        Toast.makeText(MainActivity.this,"Detected",Toast.LENGTH_LONG).show();
                    }

                    // Highlight the text in the image (e.g., by drawing bounding boxes)

                }
            }
        }
        count.setVisibility(View.VISIBLE);
        count.setText("Detected : "+c);
        detectBtn2.setVisibility(View.INVISIBLE);
    }
    private void drawBoundingBoxOnImage(Rect boundingBox, Bitmap bitmap) {
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);

        // Define the paint properties for the bounding box
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2f);

        // Draw the bounding box on the canvas
        canvas.drawRect(boundingBox, paint);

        // Update the image view with the highlighted image
        img.setImageBitmap(mutableBitmap);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
        // Image Uri will not be null for RESULT_OK
             selectedImageUri = data.getData();
            // Use Uri object instead of File to avoid storage permissions
         //   img.setImageURI(selectedImageUri);


            count.setVisibility(View.GONE);
            textview.setVisibility(View.GONE);
            img.setImageURI(selectedImageUri);
            pb.setVisibility(View.GONE);
            detectBtn.setVisibility(View.INVISIBLE);
            detectBtn2.setVisibility(View.VISIBLE);
            img.setVisibility(View.VISIBLE);
            // Load image using Glide
            Glide.with(this)
                    .load(selectedImageUri)
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.error)
                    .into(img);

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }
//    private void detectTxt() throws IOException {
//        FirebaseVisionImage image;
//        image = FirebaseVisionImage.fromFilePath(this,selectedImageUri);
//        FirebaseVisionTextDetector detector = FirebaseVision.getInstance().getVisionTextDetector();
//        detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
//            @Override
//            public void onSuccess(FirebaseVisionText firebaseVisionText) {
//
//                processTxt(firebaseVisionText);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(MainActivity.this, "Fail to detect the text from image...", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

//    private void processTxt(FirebaseVisionText text) {
//
//        List<FirebaseVisionText.Block> blocks = text.getBlocks();
//
//
//        if (blocks.size() == 0) {
//
//            Toast.makeText(MainActivity.this, "No Text ", Toast.LENGTH_LONG).show();
//            return;
//        }
//        String txt = "";
//        for (FirebaseVisionText.Block block : text.getBlocks()) {
//
//            textview.setVisibility(View.VISIBLE);
//            count.setVisibility(View.VISIBLE);
//
//            txt = txt +" "+block.getText();
//            String filterTxt = filter.getText().toString().trim();
//            if (txt.toLowerCase(Locale.ROOT).contains(filterTxt.toLowerCase(Locale.ROOT))){
//                textview.setText("Detect: "+txt);
//                setHighLightedText(textview, filterTxt.toLowerCase(Locale.ROOT));
//            }else {
//                count.setText("Result : Not Matched!");
//                textview.setText("Detect: "+txt);
//
//            }
//        }
//    }

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