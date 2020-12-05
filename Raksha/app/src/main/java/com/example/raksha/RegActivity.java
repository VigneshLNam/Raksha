package com.example.raksha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

public class RegActivity extends AppCompatActivity {
  EditText Username,Email,Password;
  TextView AlreadyReg;
  Button btn;
  DataBaseHelper mydb;
  ProgressBar pBar;
  private FirebaseAuth mAuth;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_reg);
    Username = (EditText)findViewById(R.id.name);
    Email = (EditText)findViewById(R.id.email);
    Password = (EditText)findViewById(R.id.pword);
    btn = (Button)findViewById(R.id.button);
    pBar = (ProgressBar)findViewById(R.id.pBar) ;
    mydb = new DataBaseHelper(RegActivity.this);
    AlreadyReg = (TextView)findViewById(R.id.lnkLogin);
    mAuth = FirebaseAuth.getInstance();
    AlreadyReg.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(RegActivity.this, MainActivity.class);
        startActivity(intent);
      }
    });
    btn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String name = Username.getText().toString();
        String email=Email.getText().toString();
        String password = Password.getText().toString();
        if (password.length() > 8){
          if (mydb.register(name,email,password))
            registerAccount();
          else
            Toast.makeText(RegActivity.this,"Registeration Failed",Toast.LENGTH_LONG).show();
        }
        else {
          Toast.makeText(RegActivity.this,"Password length should be greater than 8",Toast.LENGTH_LONG).show();
        }
      }

      private void registerAccount() {
        pBar.setVisibility(View.VISIBLE);
        String email=Email.getText().toString();
        String password = Password.getText().toString();
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
              Toast.makeText(getApplicationContext(), "Registeration successful!", Toast.LENGTH_LONG).show();
              pBar.setVisibility(View.GONE);
              Intent intent = new Intent(RegActivity.this,MainActivity.class);
              startActivity(intent);
            }
            else {
              Toast.makeText(getApplicationContext(), "Registeration failed! Not Registered", Toast.LENGTH_LONG).show();
              pBar.setVisibility(View.GONE);
            }
          }
        });
      }
    });

  }

}
