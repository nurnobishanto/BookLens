package com.softitbd.booklens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(Signup.this,MainActivity.class));
            finish();
        }

    }

    public void goSignIn(View view) {
        startActivity(new Intent(Signup.this,LoginActivity.class));
        finish();



    }

    public void SignUp(View view) {
        String name,email,password,cPassword;
        EditText nameFld,emailFld,passFld,cPassFld;
        nameFld = findViewById(R.id.name);
        emailFld = findViewById(R.id.emailId);
        passFld = findViewById(R.id.passId);
        cPassFld = findViewById(R.id.cPassId);
        name = nameFld.getText().toString();
        email = emailFld.getText().toString();
        password = passFld.getText().toString();
        cPassword = cPassFld.getText().toString();
        if (name.isEmpty()){
            Toast.makeText(Signup.this,"Name Field is Required",Toast.LENGTH_LONG).show();
        }
        else if (email.isEmpty()){
            Toast.makeText(Signup.this,"Email Field is Required",Toast.LENGTH_LONG).show();
        }
        else if (password.isEmpty()){
            Toast.makeText(Signup.this,"Password Field is Required",Toast.LENGTH_LONG).show();
        }
        else if (cPassword.isEmpty()){
            Toast.makeText(Signup.this,"Confirm Password Field is Required",Toast.LENGTH_LONG).show();
        }
        else if (!cPassword.equals(password)){
            Toast.makeText(Signup.this,"password and Confirm Password not matched",Toast.LENGTH_LONG).show();
        }else {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                mDatabase.child("users").child(user.getUid()).child("name").setValue(name);
                                startActivity(new Intent(Signup.this,MainActivity.class));
                                finish();

                            } else {
                                // If sign in fails, display a message to the user.

                                Toast.makeText(Signup.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }




    }
}