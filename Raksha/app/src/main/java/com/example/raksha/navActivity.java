package com.example.raksha;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.text.Editable;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class navActivity extends AppCompatActivity implements profile.OnFragmentInteractionListener, connection.OnFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    NavigationView vw;
    String UserName;
    String Mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        Toolbar toolbar = findViewById(R.id.toolbar);
        vw = (NavigationView)findViewById(R.id.nav_view);
        View header = vw.getHeaderView(0);
        TextView tv = (TextView)header.findViewById(R.id.head);
        TextView subtv = (TextView)header.findViewById(R.id.subhead);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        Intent intent = getIntent();
        UserName = intent.getStringExtra("Username");
        Mail = intent.getStringExtra("Mail");
        tv.setText(UserName);
        subtv.setText(Mail);
        Bundle bundle = new Bundle();
        bundle.putString("Uname",UserName);
        bundle.putString("emailss",Mail);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("Profile"));
        tabs.addTab(tabs.newTab().setText("Connection"));
        tabs.setTabGravity(tabs.GRAVITY_FILL);
        vw.setNavigationItemSelectedListener(this);
        final ViewPager vpage = (ViewPager) findViewById(R.id.viewpager);
        final PagerAdpter adapter = new PagerAdpter(getSupportFragmentManager(), tabs.getTabCount(),bundle);
        vpage.setAdapter(adapter);
        vpage.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vpage.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);

        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_messages:
                Intent message = new Intent(navActivity.this,Message_notify.class);
                startActivity(message);
                break;
            case R.id.nav_chat:
                Intent chat = new Intent(navActivity.this,chat.class);
                startActivity(chat);
                break;
            case R.id.nav_logout:
                Toast.makeText(navActivity.this, "Logout!", Toast.LENGTH_LONG).show();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
