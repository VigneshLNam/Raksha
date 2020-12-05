package com.devlover.g_raksha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class StartActivity extends AppCompatActivity {
  DataBaseHelper mydb;
  private FirebaseAuth mAuth;
  ProgressBar pBar;
  private String uservar;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_start);
    mAuth = FirebaseAuth.getInstance();
    mydb = new DataBaseHelper(StartActivity.this);
    pBar = (ProgressBar)findViewById(R.id.pBar);
    ArrayList<String> User = mydb.ifAvailable();
    if (User.isEmpty()) {
      pBar.setVisibility(View.VISIBLE);
      new Timer().schedule(new TimerTask(){
        public void run() {
          Intent intent = new Intent(StartActivity.this,MainActivity.class);
          intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
          startActivity(intent);
          finish();

        }
      }, 2000 );
    }
    else {
      String UserN = User.get(0);
      String Email = User.get(1);
      String Password = User.get(2);
      this.uservar = UserN;
      loginUser(Email,Password,UserN);
    }
  }

  private void loginUser(final String email , String pword, final String name) {
    pBar.setVisibility(View.VISIBLE);
    mAuth.signInWithEmailAndPassword(email, pword)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
              @Override
              public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent login = new Intent(StartActivity.this,MapActivity.class);
                    login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    login.putExtra("Username",name);
                    login.putExtra("UserMail",email);
                    startActivity(login);
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Login failed! Not Registered?", Toast.LENGTH_LONG).show();
                    pBar.setVisibility(View.GONE);
                }
              }
            });
  }
}
