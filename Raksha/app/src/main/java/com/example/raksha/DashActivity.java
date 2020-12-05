package com.example.raksha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.gms.location.FusedLocationProviderClient;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class DashActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    public static final int MESSAGE_READ = 2;
    DataBaseHelper mydb;
    private DrawerLayout drawer;
    NavigationView vw;
    CardView dashboard;
    DocumentReference Emerref;
    FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private static final int REQUEST_LOCATION = 1;
    final int handlerState = 0;                        //used to identify handler message
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();
    private ConnectedThread mConnectedThread;

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static String SERVICE_ADDRESS = "";
    private Handler mHandler;
    String UserName,Mail;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation);
        LocationManager lm = (LocationManager)DashActivity.this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mydb = new DataBaseHelper(DashActivity.this);
        vw = (NavigationView) findViewById(R.id.nav_view);
        if (vw != null) { vw.setNavigationItemSelectedListener(this);}
        View header = vw.getHeaderView(0);
        TextView tv = (TextView) header.findViewById(R.id.head);
        TextView subtv = (TextView) header.findViewById(R.id.subhead);
        final TextView getBt = (TextView) findViewById(R.id.getBt);
        dashboard = (CardView) findViewById(R.id.prof_setting);
        ArrayList<String> device = mydb.getdevice();
        drawer = findViewById(R.id.drawer_layout);
        Intent intent = getIntent();
        UserName = intent.getStringExtra("Username");
        Mail = intent.getStringExtra("Mail");
        tv.setText(UserName);
        subtv.setText(Mail);
        String DevId = device.get(1);
        if (DevId == "") {
            Toast.makeText(DashActivity.this, "Empty", Toast.LENGTH_LONG).show();
        } else {
            this.SERVICE_ADDRESS = DevId;
        }
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MESSAGE_READ:
                        byte[] readBuf = (byte[]) msg.obj;
                        String string = new String(readBuf, 0, msg.arg1);
                        getBt.setText(string);
                        call(string);
                        location(string);
                        statuschk(string);
                        break;
                }
            }
        };
        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();
        getBt.setText(SERVICE_ADDRESS);
        Toast.makeText(DashActivity.this, SERVICE_ADDRESS, Toast.LENGTH_LONG).show();

        dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inte = new Intent(DashActivity.this, navActivity.class);
                inte.putExtra("Username", UserName);
                inte.putExtra("Mail", Mail);
                try {
                    btSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                startActivity(inte);
            }
        });
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    @Override
    public void onResume() {
        super.onResume();
        String address = SERVICE_ADDRESS;
        Log.e("check", address);

        //create device and set the MAC address
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
            Toast.makeText(getBaseContext(), "Socket creation success", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
        }
        // Establish the Bluetooth socket connection.
        try {
            btSocket.connect();
            Log.e("connection", "success");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                //insert code to deal with this
            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
        mConnectedThread.write("x");
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onPause() {
        super.onPause();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MESSAGE_READ:
                        byte[] readBuf = (byte[]) msg.obj;
                        String string = new String(readBuf, 0, msg.arg1);
                        call(string);
                        location(string);
                        statuschk(string);
                        break;
                }
            }
        };
        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onStop() {
        super.onStop();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MESSAGE_READ:
                        byte[] readBuf = (byte[]) msg.obj;
                        String string = new String(readBuf, 0, msg.arg1);
                        call(string);
                        location(string);
                        statuschk(string);
                        break;
                }
            }
        };
        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();

    }

    private void checkBTState() {

        if (btAdapter == null) {
            Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_messages:
                Intent message = new Intent(DashActivity.this,Message_notify.class);
                startActivity(message);
                break;
            case R.id.nav_chat:
                Intent chat = new Intent(DashActivity.this, com.example.raksha.chat.class);
                startActivity(chat);
                break;
            case R.id.nav_logout:
                Toast.makeText(DashActivity.this, "Logout!", Toast.LENGTH_LONG).show();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.available();
                    if (bytes != 0) {
                        SystemClock.sleep(100); //pause and wait for rest of data. Adjust this depending on your sending speed.
                        bytes = mmInStream.available(); // how many bytes are ready to be read?
                        bytes = mmInStream.read(buffer, 0, bytes); // record how many bytes we actually read
                        byte[] readBuf = (byte[]) buffer;
                        String string = new String(readBuf, 0, bytes);
                        mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                                .sendToTarget(); // Send the obtained bytes to the UI activity
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("Check", "some mistake");
                    break;
                }
            }
        }

        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e("check", "close() of connect socket failed", e);
            }
        }
    }

    public void call(String var) {
        ArrayList<String> phonenos= mydb.ifphone();
        String phno = phonenos.get(0);
        if (var.equals("0")) {
            Intent call = new Intent(Intent.ACTION_CALL);
            call.setData(Uri.parse("tel:"+phno));
            if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            startActivity(call);
        }
    }


    public void location(String var) {
        String lat = "25";
        String longit = "25";
        if (var.equals("0")) {
            if(ActivityCompat.checkSelfPermission(DashActivity.this
                    , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                getLocation();
            }
            else{
                ActivityCompat.requestPermissions(DashActivity.this
                        , new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
            }


        }
    }

    private void getLocation() {
        ArrayList<String> phonenos= mydb.ifphone();
        List<Address> addresses;
        Geocoder geocoder;
        geocoder = new Geocoder(this, Locale.getDefault());
        SmsManager sms = SmsManager.getDefault();
        String address = "Your Child " + UserName + " might be in danger";
        String A_Message = "Your child " + UserName + " might be in Danger !, Raksha has started sharing the Live Location with your Device";
        String sphno = phonenos.get(1);
        String tphno = phonenos.get(2);
        String phno = phonenos.get(0);
        String numbers[] = {phno,tphno,sphno};
        String content = "Your Child" + UserName + " might be in danger, Last found location : " + address;
        GPSTracker mGPS = new GPSTracker(this);
        if(mGPS.canGetLocation ){
            mGPS.getLocation();
            try {
                addresses = geocoder.getFromLocation(mGPS.getLatitude(), mGPS.getLongitude(), 1);
                address = "Your Child" + UserName + " might be in danger, Last found location : " + "Address : " + addresses.get(0).getAddressLine(0) + " Postal Code : " + addresses.get(0).getPostalCode();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for(String number : numbers) {
            if(number.equals(phno)) {
                sms.sendTextMessage(number, null, A_Message, null, null);
            }
            else{
                sms.sendTextMessage(number, null, address, null, null);
            }
        }
    }

    public void statuschk(String var){
        ArrayList<String> USER = mydb.Insertinfo();
        if (var.equals("0")) {
        if (!USER.get(3).isEmpty()) {
            Emerref = db.collection("Parents").document(USER.get(3));
            Map<String, Object> emer = new HashMap<>();
            emer.put("P_User", UserName);
            emer.put("Status", "YES");
            emer.put("User_id", mAuth.getCurrentUser().getUid());
            Emerref.set(emer);
        }
        }
    }
}
