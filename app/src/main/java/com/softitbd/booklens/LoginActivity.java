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

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        }

    }

    public void goSignUp(View view) {
        startActivity(new Intent(LoginActivity.this,Signup.class));
        finish();
    }

    public void goMainPage(View view) {
        EditText emailFld = findViewById(R.id.emailId);
        EditText passwordFld = findViewById(R.id.passId);
        String email = emailFld.getText().toString();
        String password = passwordFld.getText().toString();
        if (!email.isEmpty() && !password.isEmpty()){
            if (email.contains("@")){
                if (password.length()>7){
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                        finish();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                }else {
                    Toast.makeText(LoginActivity.this,"Password Length minimum 8",Toast.LENGTH_LONG).show();
                }

            }else {
                Toast.makeText(LoginActivity.this,"valid Email Required!",Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(LoginActivity.this,"Email and password Required!",Toast.LENGTH_LONG).show();
        }

    }
}