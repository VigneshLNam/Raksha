package com.example.raksha;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class auth_user extends AppCompatActivity {
    EditText email,pno,vpno,pmail;
    Button verpno,subpno,discdev,subpmail;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    LinearLayout phverify;
    String UserName,user_Name;
    DatabaseReference myRef;
    DocumentReference Emerref;
    int randomnumber;
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private FirebaseAuth mAuth;
    DataBaseHelper mydb;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_user);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference("users");
        mydb = new DataBaseHelper(auth_user.this);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        Intent intent = getIntent();
        user_Name = intent.getStringExtra("UserName");
        UserName = intent.getStringExtra("UserMail");
        email = (EditText)findViewById(R.id.email);
        email.setText(UserName);
        pmail = (EditText)findViewById(R.id.pmail);
        pno = (EditText)findViewById(R.id.pno);
        vpno = (EditText)findViewById(R.id.vpno);
        verpno = (Button)findViewById(R.id.verpno);
        subpno = (Button)findViewById(R.id.subpno);
        phverify = findViewById(R.id.phverify);
        discdev = (Button)findViewById(R.id.dicsdev);
        subpmail = (Button)findViewById(R.id.subpmail);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        subpmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(pmail.getText().toString())) {
                    Emerref = db.collection("Parents").document(pmail.getText().toString());
                    Map<String, Object> emer = new HashMap<>();
                    emer.put("P_User",user_Name);
                    emer.put("Status","NO");
                    emer.put("User_id", mAuth.getCurrentUser().getUid());
                    Emerref.set(emer).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if(mydb.updatepri_mail(user_Name,pmail.getText().toString())) {
                                Toast.makeText(auth_user.this, "Registered", Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(auth_user.this, "Not Registered!!!!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(auth_user.this,"This Field Cannot Be Empty",Toast.LENGTH_LONG).show();
                }
            }
        });

        ArrayList<String> USER = mydb.Insertinfo();
        if (USER.get(1).isEmpty()) {
            Toast.makeText(auth_user.this,"Phone Number Not Updated",Toast.LENGTH_LONG).show();
            pno.setHint("Phone Number NOT Updated");
        }
        else{
            String PH_no =  USER.get(1);
            pno.setText(PH_no);
        }

        if (USER.get(3).isEmpty()) {
            Toast.makeText(auth_user.this,"Primary Mail Not Updated",Toast.LENGTH_LONG).show();
            pno.setHint("NOT Updated");
        }
        else{
            String mail_prim =  USER.get(3);
            pmail.setText(mail_prim);
        }


        discdev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mydb.update(user_Name,"00")){
                    Toast.makeText(auth_user.this,"Disconnected Successfully",Toast.LENGTH_LONG).show();
                    Intent restart = new Intent(auth_user.this,StartActivity.class);
                    restart.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(restart);
                    finish();
                }
            }
        });

        verpno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Phn_No = pno.getText().toString();
                if (Phn_No.isEmpty()){
                    Toast.makeText(auth_user.this,"Please Enter the Number",Toast.LENGTH_LONG).show();
                }
                else{
                    if(Phn_No.length() == 10){
                        phverify.setVisibility(View.VISIBLE);
                        sendSms(Phn_No);
                        subpno.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String mVerificationId = String.valueOf(randomnumber);
                                if (mVerificationId.equals(vpno.getText().toString())){
                                    if(mydb.updatefphone(UserName,Phn_No)) {
                                        Toast.makeText(auth_user.this, "Succesfull", Toast.LENGTH_LONG).show();
                                        vpno.setText("");
                                        phverify.setVisibility(View.GONE);
                                        Map<String,Object> loca = new HashMap<>();
                                        loca.put("User_ID",mAuth.getCurrentUser().getUid());
                                        loca.put("UserName",UserName);
                                        myRef.child(Phn_No).setValue(loca);
                                    }
                                }
                                else{
                                    Toast.makeText(auth_user.this,"Invalid Code",Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    }
                    else{
                        Toast.makeText(auth_user.this,"Invalid Number",Toast.LENGTH_LONG).show();
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
                Toast.makeText(auth_user.this,"SMS sent",Toast.LENGTH_LONG).show();
            }
            rd.close();
            Toast.makeText(auth_user.this,"SMS sent",Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e("error",e.toString());
            Toast.makeText(auth_user.this,"SMS NOT sent",Toast.LENGTH_LONG).show();
        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        Log.d("Check", "done");
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }
}
