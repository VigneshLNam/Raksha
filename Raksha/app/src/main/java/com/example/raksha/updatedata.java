package com.example.raksha;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class updatedata extends AppCompatActivity {
    DatePickerDialog picker;
    EditText name,email,address,date,pno;
    DataBaseHelper mydb;
    private String UserName,E_Mail;
    Button update,insert;
    DatabaseReference myRef;
    ProgressBar pBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatedata);
        Intent intent = getIntent();
        mydb = new DataBaseHelper(updatedata.this);
        UserName = intent.getStringExtra("UserName");
        E_Mail = intent.getStringExtra("UserMail");
        name = (EditText)findViewById(R.id.name);
        pBar = (ProgressBar)findViewById(R.id.pBar);
        name.setText(UserName);
        email = (EditText)findViewById(R.id.email);
        email.setText(E_Mail);
        address = (EditText)findViewById(R.id.address);
        date = (EditText)findViewById(R.id.date);
        ArrayList<String> USER = mydb.Insertinfo();
        date.setInputType(InputType.TYPE_NULL);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                picker = new DatePickerDialog(updatedata.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        date.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                },year,month,day);
                picker.show();
            }
        });
        pno = (EditText)findViewById(R.id.pno);
        insert = (Button)findViewById(R.id.insert);
        update = (Button)findViewById(R.id.update);
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Uname = name.getText().toString();
                String Uemail = email.getText().toString();
                String Address = address.getText().toString();
                String phoneno = pno.getText().toString();
                String Date = date.getText().toString();
                if(mydb.insertinfo(Uname,Uemail,Address,phoneno,Date)){
                    Toast.makeText(updatedata.this,"updated Sucessfully",Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(updatedata.this,"Update Failure",Toast.LENGTH_LONG).show();
                }
            }
        });
        if (USER.isEmpty()) {
                    Toast.makeText(updatedata.this,"Insert Yor Data",Toast.LENGTH_LONG).show();
        }
        else{
            String ADD_RES = USER.get(0);
            String PH_no =  USER.get(1);
            String D_ate = USER.get(2);
            address.setText(ADD_RES);
            pno.setText(PH_no);
            date.setText(D_ate);
            insert.setVisibility(View.GONE);
            update.setVisibility(View.VISIBLE);
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mydb.updateuserI(UserName,address.getText().toString(),pno.getText().toString(),date.getText().toString())){
                        Toast.makeText(updatedata.this,"updated Sucessfully",Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(updatedata.this,"Failure",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
