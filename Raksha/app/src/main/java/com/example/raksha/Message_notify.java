package com.example.raksha;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;

public class Message_notify extends AppCompatActivity {
    ListView notif_message;
    ArrayList<String> M_List = new ArrayList<>();
    ArrayAdapter<String> adapter;
    DatabaseReference My_Ref;
    String My_Id;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_notify);
        mAuth = FirebaseAuth.getInstance();
        notif_message = findViewById(R.id.notif_message);
        My_Ref = FirebaseDatabase.getInstance().getReference("Message");
        Intent intent = getIntent();
        My_Id = mAuth.getCurrentUser().getUid();
        My_Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dp : dataSnapshot.getChildren()){
                    String Message = dp.child("Alert").getValue().toString();
                    if(dp.getKey().equals(My_Id)){
                        M_List.add(Message);
                    }
                }
                adapter = new ArrayAdapter<>(Message_notify.this,R.layout.users,R.id.uname,M_List);
                notif_message.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
