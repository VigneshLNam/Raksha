package com.devlover.g_raksha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
  EditText Email,Password,Name;
  ProgressBar pBar;
  private FirebaseAuth mAuth;
  TextView AlreadyReg;
  Button login;
  DataBaseHelper mydb;
  private String userName;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Email=(EditText)findViewById(R.id.email);
    Password=(EditText)findViewById(R.id.pword);
    Name=(EditText)findViewById(R.id.name);
    this.userName = Name.getText().toString();
    pBar = (ProgressBar)findViewById(R.id.pBar) ;
    login = (Button)findViewById(R.id.button);
    AlreadyReg = (TextView)findViewById(R.id.lnkLogin);
    mydb = new DataBaseHelper(MainActivity.this);
    mAuth = FirebaseAuth.getInstance();
    AlreadyReg.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, RegActivity.class);
        startActivity(intent);
      }
    });
    login.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String name = Name.getText().toString();
        String email = Email.getText().toString();
        String password = Password.getText().toString();
        if (name.isEmpty() | email.isEmpty() | password.isEmpty())
          Toast.makeText(MainActivity.this, "TextFields Cannot be Empty", Toast.LENGTH_LONG).show();
        else {
          if (mydb.login(email, password)) {
            if (password.length() > 8) {
              if (mydb.checkUser(name)) {
                loginUserAccount();
              } else {
                Toast.makeText(MainActivity.this, "Check the entered Username", Toast.LENGTH_LONG).show();
              }
            } else
              Toast.makeText(MainActivity.this, "Password length should be greater than 8", Toast.LENGTH_LONG).show();
          } else {
            Toast.makeText(MainActivity.this, "Invalid Username OR Password", Toast.LENGTH_LONG).show();
          }
        }
      }
      private void loginUserAccount() {
        pBar.setVisibility(View.VISIBLE);
        final String email=Email.getText().toString();
        String password = Password.getText().toString();
        if (TextUtils.isEmpty(email)) {
          Toast.makeText(getApplicationContext(), "Please enter email...", Toast.LENGTH_LONG).show();
          return;
        }
        if (TextUtils.isEmpty(password)) {
          Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_LONG).show();
          return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                  @Override
                  public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Intent login = new Intent(MainActivity.this,MapActivity.class);
                        login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        login.putExtra("Username",Name.getText().toString());
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
    });
  }

}
