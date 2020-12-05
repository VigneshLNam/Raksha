package com.example.raksha;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class emer_gency extends AppCompatActivity {
  EditText fpno,vfpno,mpno,vmpno,gpno,vgpno,temp;
  Button verfpno,subfpno,vermpno,submpno,vergpno,subgpno;
  LinearLayout fpverify,mpverify,gpverify;
  DataBaseHelper mydb;
  String UserName;
  private int randomnumber;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_emer_gency);
    mydb = new DataBaseHelper(emer_gency.this);
    Intent intent = getIntent();
    UserName = intent.getStringExtra("UserName");
    fpverify = findViewById(R.id.fpverify);
    mpverify = findViewById(R.id.mpverify);
    gpverify = findViewById(R.id.gpverify);
    fpno = (EditText)findViewById(R.id.fpno);
    vfpno = (EditText)findViewById(R.id.vfpno);
    mpno = (EditText)findViewById(R.id.mpno);
    vmpno = (EditText)findViewById(R.id.vmpno);
    gpno = (EditText)findViewById(R.id.gpno);
    vgpno = (EditText)findViewById(R.id.vgpno);
    verfpno = (Button)findViewById(R.id.verfpno);
    subfpno = (Button)findViewById(R.id.subfpno);
    vermpno = (Button)findViewById(R.id.vermpno);
    submpno = (Button)findViewById(R.id.submpno);
    vergpno = (Button)findViewById(R.id.vergpno);
    subgpno = (Button)findViewById(R.id.subgpno);
    ArrayList<String> phonenos= mydb.ifphone();
    String primary = phonenos.get(0);
    String secondary = phonenos.get(1);
    String tertiary = phonenos.get(2);
    fpno.setText(primary);
    mpno.setText(secondary);
    gpno.setText(tertiary);
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    StrictMode.setThreadPolicy(policy);
    verfpno.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final String Phn_No = fpno.getText().toString();
        if (Phn_No.isEmpty()){
          Toast.makeText(emer_gency.this,"Please Enter the Number",Toast.LENGTH_LONG).show();
        }
        else{
          if(Phn_No.length() == 10){
            fpverify.setVisibility(View.VISIBLE);
            sendSms(Phn_No);
            subfpno.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                String mVerificationId = String.valueOf(randomnumber);
                if (mVerificationId.equals(vfpno.getText().toString())){
                  if(mydb.updatefphone(UserName,Phn_No)) {
                    Toast.makeText(emer_gency.this, "Succesfull", Toast.LENGTH_LONG).show();
                    vfpno.setText("");
                    fpverify.setVisibility(View.GONE);
                  }
                }
                else{
                  Toast.makeText(emer_gency.this,"Invalid Code",Toast.LENGTH_LONG).show();
                }
              }
            });

          }
          else{
            Toast.makeText(emer_gency.this,"Invalid Number",Toast.LENGTH_LONG).show();
          }
        }

      }
    });

    vermpno.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final String Phn_No = mpno.getText().toString();
        if (Phn_No.isEmpty()){
          Toast.makeText(emer_gency.this,"Please Enter the Number",Toast.LENGTH_LONG).show();
        }
        else{
          if(Phn_No.length() == 10){
            mpverify.setVisibility(View.VISIBLE);
            sendSms(Phn_No);
            submpno.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                String mVerificationId = String.valueOf(randomnumber);
                if (mVerificationId.equals(vmpno.getText().toString())){
                  if(mydb.updatemphone(UserName,Phn_No)) {
                    Toast.makeText(emer_gency.this, "Succesfull", Toast.LENGTH_LONG).show();
                    vmpno.setText("");
                    mpverify.setVisibility(View.GONE);
                  }
                }
                else{
                  Toast.makeText(emer_gency.this,"Invalid Code",Toast.LENGTH_LONG).show();
                }
              }
            });
          }
          else{
            Toast.makeText(emer_gency.this,"Invalid Number",Toast.LENGTH_LONG).show();
          }
        }

      }
    });

    vergpno.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final String Phn_No = gpno.getText().toString();
        if (Phn_No.isEmpty()){
          Toast.makeText(emer_gency.this,"Please Enter the Number",Toast.LENGTH_LONG).show();
        }
        else{
          if(Phn_No.length() == 10){
            gpverify.setVisibility(View.VISIBLE);
            sendSms(Phn_No);
            subgpno.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                String mVerificationId = String.valueOf(randomnumber);
                if (mVerificationId.equals(vgpno.getText().toString())){
                  Log.e("where","gaurd");
                  if(mydb.updategphone(UserName,Phn_No)) {
                    Toast.makeText(emer_gency.this, "Succesfull", Toast.LENGTH_LONG).show();
                    vmpno.setText("");
                    gpverify.setVisibility(View.GONE);
                  }
                }
                else{
                  Toast.makeText(emer_gency.this,"Invalid Code",Toast.LENGTH_LONG).show();
                }
              }
            });
          }
          else{
            Toast.makeText(emer_gency.this,"Invalid Number",Toast.LENGTH_LONG).show();
          }
        }

      }
    });

  }

  public void sendSms(String phno) {
    try {
      // Construct data
      String apiKey = "apikey=" + "(TEXT-LOCAL API)"; //create a textlocal account and put your api here
      Random random = new Random();
      randomnumber = random.nextInt(999999);
      Log.e("otp",String.valueOf(randomnumber));
      String message = "&message=" + "Your Verification Code is "+randomnumber;
      String sender = "&sender=" + "TXTLCL";
      String numbers = "&numbers=" + phno;

      // Send data
      HttpURLConnection conn = (HttpURLConnection) new URL("https://api.textlocal.in/send/?").openConnection();
      String data = apiKey + numbers + message + sender;
      conn.setDoOutput(true);
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
      conn.getOutputStream().write(data.getBytes("UTF-8"));
      final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      final StringBuffer stringBuffer = new StringBuffer();
      String line;
      while ((line = rd.readLine()) != null) {
        Toast.makeText(emer_gency.this,"SMS sent",Toast.LENGTH_LONG).show();
      }
      rd.close();
      Toast.makeText(emer_gency.this,"SMS sent",Toast.LENGTH_LONG).show();
    } catch (Exception e) {
      Toast.makeText(emer_gency.this,"SMS NOT sent",Toast.LENGTH_LONG).show();
    }
  }
}
