package com.example.raksha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Messenger_Activity extends AppCompatActivity {
    String reference,uid;
    DatabaseReference myref;
    ImageButton send;
    EditText message;
    ArrayList<String> reciever_list = new ArrayList<>();
    ArrayList<String> sender_list = new ArrayList<>();
    ListView sender,reciever;
    ArrayAdapter<String> radapter,sadapter;
    ArrayList<String> temps_list = new ArrayList<>();
    ArrayList<String> tempr_list = new ArrayList<>();
    Message_dbHelp MyDb;
    ImageButton delete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messenger);
        MyDb = new Message_dbHelp(Messenger_Activity.this);
        delete = (ImageButton) findViewById(R.id.delete);
        send = (ImageButton) findViewById(R.id.send);
        message = (EditText)findViewById(R.id.editText);
        sender = findViewById(R.id.sender);
        reciever = findViewById(R.id.reciever);
        Intent intent = getIntent();
        uid = intent.getStringExtra("user_id");
        String upno = intent.getStringExtra("user_no");
        String mypno = intent.getStringExtra("select");
        long user_phone = Long.parseLong(upno);
        long my_phone = Long.parseLong(mypno);
        if(sum(user_phone) > sum(my_phone)){
            reference = upno+mypno;
        }
        else{
            reference = mypno+upno;
        }

        myref = FirebaseDatabase.getInstance().getReference(reference);

        ArrayList<String> slist = MyDb.sender(reference);
        ArrayList<String> rlist = MyDb.reciever(reference);
        if(!slist.isEmpty()){
            sender_list.addAll(slist);
        }
        else{
            Map<String,Object> insert = new HashMap<>();
            insert.put("Message","  ");
            insert.put("User_id",uid);
            myref.setValue(insert);
        }
        if(!rlist.isEmpty()){
            reciever_list.addAll(rlist);
        }

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object> loca = new HashMap<>();
                loca.put("Message",message.getText().toString());
                loca.put("User_id",uid);
                myref.setValue(loca);
                message.setText("");
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MyDb.deletechat(reference)) {
                    ;
                    sender_list.clear();
                    reciever_list.clear();
                    Toast.makeText(Messenger_Activity.this, "Deletes chat on Restart", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(Messenger_Activity.this, "Could Not delete Chat", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private static int sum(long num)
    {
        int sum=0;
        while(num!=0)
        {
            sum+=num%10;
            num/=10;
        }
        return sum;
    }

    @Override
    protected void onStart() {
        super.onStart();
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String in_mes = dataSnapshot.child("Message").getValue().toString();
                String user_info = dataSnapshot.child("User_id").getValue().toString();
                if(user_info.equals(uid)){
                    sender_list.add(in_mes);
                    temps_list.add(in_mes);
                }
                else{
                    reciever_list.add(in_mes);
                    tempr_list.add(in_mes);
                }
                radapter = new ArrayAdapter<>(Messenger_Activity.this,R.layout.their_message,R.id.his_message_body,reciever_list);
                sadapter = new ArrayAdapter<>(Messenger_Activity.this,R.layout.my_message,R.id.message_body,sender_list);
                reciever.setAdapter(sadapter);
                sender.setAdapter(radapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        MyDb.M_DB(reference,temps_list,tempr_list);
        sender_list.clear();
        reciever_list.clear();
    }
}
