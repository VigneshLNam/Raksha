package com.devlover.g_raksha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MapActivity extends AppCompatActivity {
    TextView myname,hisname,mymail;
    EditText message;
    Button submit;
    String UserName,UserMail;
    FirebaseFirestore Mydb;
    DocumentReference eref;
    DatabaseReference myref;
    private String user_status,user_id,user_name;
    CardView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        myref = FirebaseDatabase.getInstance().getReference("Message");
        Intent intent = getIntent();
        map = (CardView)findViewById(R.id.map);
        UserName = intent.getStringExtra("Username");
        UserMail = intent.getStringExtra("UserMail");
        myname = (TextView)findViewById(R.id.myname);
        hisname = (TextView)findViewById(R.id.hisname);
        mymail = (TextView)findViewById(R.id.mymail);
        message = (EditText)findViewById(R.id.message);
        myname.setText(UserName);
        mymail.setText(UserMail);
        submit = (Button)findViewById(R.id.submit);
        Mydb = FirebaseFirestore.getInstance();
        eref = Mydb.collection("Parents").document(UserMail);
        eref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    user_name = documentSnapshot.getString("P_User");
                    user_id = documentSnapshot.getString("User_id");
                    user_status = documentSnapshot.getString("Status");
                    submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!TextUtils.isEmpty(message.getText().toString())) {
                                Map<String, Object> mes = new HashMap<>();
                                mes.put("Alert", message.getText().toString());
                                myref.child(user_id).setValue(mes);
                                Toast.makeText(MapActivity.this, "Message Sent!!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    if(user_status.equals("YES")){
                        map.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent Map =new Intent(MapActivity.this,LocateActivity.class);
                                Map.putExtra("User_ID",user_id);
                                startActivity(Map);
                            }
                        });

                    }
                    Log.e("ID",user_name + " " +user_id);
                    hisname.setText(user_name);
                }
                else{
                    map.setVisibility(View.GONE);
                    Toast.makeText(MapActivity.this,"Raksha Is Not Registered to This User",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
