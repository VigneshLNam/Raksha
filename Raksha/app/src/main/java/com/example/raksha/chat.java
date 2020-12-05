package com.example.raksha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class chat extends AppCompatActivity {

    DatabaseReference myRef;
    ArrayList<String> user_list = new ArrayList<>();
    ArrayList<String> id_list = new ArrayList<>();
    ArrayAdapter<String> adapter;
    ListView phview;
    DataBaseHelper mydb;
    String PHONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mydb = new DataBaseHelper(chat.this);
        myRef = FirebaseDatabase.getInstance().getReference("users");
        phview = (ListView)findViewById(R.id.phview);
        ArrayList<String> USER = mydb.Insertinfo();
        if(!USER.isEmpty()) {
            PHONE = USER.get(1);
        }
        else{
            Toast.makeText(chat.this,"User Not Registered For Chatting",Toast.LENGTH_LONG).show();
        }
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user_list.clear();
                for(DataSnapshot ph : dataSnapshot.getChildren()){
                    String User = ph.getKey();
                    String Id = ph.child("User_ID").getValue().toString();
                    if(!User.equals(PHONE)) {
                        user_list.add(User);
                        id_list.add(Id);
                    }
                }
                adapter = new ArrayAdapter<>(chat.this,R.layout.users,R.id.uname,user_list);
                phview.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        phview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent start = new Intent(chat.this,Messenger_Activity.class);
                start.putExtra("user_id",id_list.get(position));
                start.putExtra("user_no",user_list.get(position));
                start.putExtra("select",PHONE);
                startActivity(start);
            }
        });
    }
}
