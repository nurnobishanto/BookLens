package com.softitbd.booklens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.io.IOException;
import java.util.List;

public class SelectActivity extends AppCompatActivity {

    private static final int SELECT_PICTURE = 200;
    ImageView imageView;
    Button select,detect;
    EditText filter;
    TextView textview;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        imageView = findViewById(R.id.image);
        select = findViewById(R.id.select);
        detect = findViewById(R.id.detect);
        filter = findViewById(R.id.filter);
        textview = findViewById(R.id.text);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();

            }
        });
        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calling a method to
                // detect a text .
                if (filter.getText().toString().isEmpty()){
                    Toast.makeText(SelectActivity.this,"Filter Text Empty",Toast.LENGTH_LONG).show();

                }else {
                    try {
                        detectTxt();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    private void detectTxt() throws IOException {
        FirebaseVisionImage image = FirebaseVisionImage.fromFilePath(this,selectedImageUri);
       // FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(selectedImageUri);
        FirebaseVisionTextDetector detector = FirebaseVision.getInstance().getVisionTextDetector();
        detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {

                processTxt(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // handling an error listener.
                Toast.makeText(SelectActivity.this, "Fail to detect the text from image...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                 selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    imageView.setImageURI(selectedImageUri);
                }
            }
        }
    }
    private void processTxt(FirebaseVisionText text) {
        List<FirebaseVisionText.Block> blocks = text.getBlocks();
        if (blocks.size() == 0) {
            Toast.makeText(SelectActivity.this, "No Text ", Toast.LENGTH_LONG).show();
            return;
        }
        String txt="";
        for (FirebaseVisionText.Block block : text.getBlocks()) {
             txt = txt+ " " +block.getText();
            String filterTxt = filter.getText().toString();
            if (txt.contains(filterTxt)){
                textview.setText("Matched!");
            }else {
                textview.setText("Detect: "+txt+"\n\n\nResult : Not Matched!");
            }
        }
    }


}