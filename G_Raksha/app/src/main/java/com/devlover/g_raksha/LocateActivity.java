package com.devlover.g_raksha;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LocateActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String Reference;
    DatabaseReference myRef;
    private Marker mylocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate);
        Intent intent = getIntent();
        Reference = intent.getStringExtra("User_ID");
        myRef = FirebaseDatabase.getInstance().getReference("location");
        Toast.makeText(LocateActivity.this,Reference,Toast.LENGTH_LONG).show();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot loca : dataSnapshot.getChildren()){
                    if(loca.getKey().equals(Reference)) {
                        mMap = googleMap;
                        double latitude = Double.parseDouble(loca.child("Latitude").getValue().toString());
                        double longitude = Double.parseDouble(loca.child("Longitude").getValue().toString());
                        LatLng mypos = new LatLng(latitude, longitude);
                        if(mylocation!=null) {
                            mylocation.remove();
                        }
                            mylocation = mMap.addMarker(new MarkerOptions().position(mypos).title("Marker"));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mypos,16));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
