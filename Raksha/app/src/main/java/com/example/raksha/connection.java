package com.example.raksha;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class connection extends Fragment {

  private Button pairedBtn;
  private String dev_id ;
  private ListView deviceList;
  DataBaseHelper mydb;
  private String user_Name;
  private BluetoothAdapter myBluetooth = null;
  private Set<BluetoothDevice> pairedDevices;
  public static String EXTRA_ADDRESS = "device_address";
  private OnFragmentInteractionListener mListener;

  public connection() {
    // Required empty public constructor
  }


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View v = inflater.inflate(R.layout.fragment_connection, container, false);
    pairedBtn = (Button) v.findViewById(R.id.button);
    mydb = new DataBaseHelper(getActivity());
    deviceList = (ListView) v.findViewById(R.id.listView);
    user_Name = getArguments().getString("Uname");
    final BluetoothAdapter myBluetooth = BluetoothAdapter.getDefaultAdapter();
    if(myBluetooth == null)
    {
      //no supported
      new AlertDialog.Builder(getActivity())
              .setTitle("Not compitable")
              .setMessage("Your Phone doesnot Support Bluetooth")
              .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                  System.exit(0);
                }
              })
              .setIcon(android.R.drawable.ic_dialog_alert)
              .show();

//            Toast.makeText(getApplicationContext(),"Bluetooth not Available",Toast.LENGTH_LONG).show();
//            finish();
    }
    else if(!myBluetooth.isEnabled())
    {
      Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      startActivityForResult(turnBTon,1);

    }

    pairedBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v)
      {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new AccelerateInterpolator());
        fadeIn.setStartOffset(0);
        fadeIn.setDuration(300);
        deviceList.setAnimation(fadeIn);
        deviceList.setVisibility(View.VISIBLE);
        pairedDevicesList();
      }

      private void pairedDevicesList() {
        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();

        if (pairedDevices.size()>0)
        {
          for(BluetoothDevice bt : pairedDevices)
          {
            list.add(bt.getName() + "\n" + bt.getAddress()); //Get the device's name and the address
          }

        }
        else
        {
          deviceList.setVisibility(View.GONE);
          new AlertDialog.Builder(getActivity())
                  .setTitle("No Paired Device Found")
                  .setMessage("Please first pair HC-05 or HC-06 from the phone settings and try again")
                  .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                      System.exit(0);
                    }
                  })
                  .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                      pairedBtn.performClick();
                      //pairedDevicesList();
                    }
                  })
                  .setIcon(android.R.drawable.ic_dialog_alert)
                  .show();

          //Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }

        //final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
        //override to set text color
        deviceList.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, list){
          @Override
          public View getView(int position, View convertView, ViewGroup parent){
            TextView textView = (TextView) super.getView(position, convertView, parent);

            textView.setTextColor(Color.parseColor("#ffffff"));
            return  textView;
          }
        });

        deviceList.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked
      }
    });
    return v;
  }

  // TODO: Rename method, update argument and hook method into UI event
  public void onButtonPressed(Uri uri) {
    if (mListener != null) {
      mListener.onFragmentInteraction(uri);
    }
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof OnFragmentInteractionListener) {
      mListener = (OnFragmentInteractionListener) context;
    } else {
      throw new RuntimeException(context.toString()
              + " must implement OnFragmentInteractionListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  /**
   * This interface must be implemented by activities that contain this
   * fragment to allow an interaction in this fragment to be communicated
   * to the activity and potentially other fragments contained in that
   * activity.
   * <p>
   * See the Android Training lesson <a href=
   * "http://developer.android.com/training/basics/fragments/communicating.html"
   * >Communicating with Other Fragments</a> for more information.
   */

  private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener()
  {
    public void onItemClick (AdapterView<?> av, View v, int arg2, long arg3)
    {

      // Get the device MAC address, the last 17 chars in the View
      String info = ((TextView) v).getText().toString();
      String address = info.substring(info.length() - 17);
      dev_id = address;
      if(mydb.update(user_Name,dev_id)){
        Intent restart = new Intent(getActivity(),StartActivity.class);
        restart.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(restart);
        Toast.makeText(getActivity(),"done",Toast.LENGTH_LONG).show();
      }

    }
  };

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }


  public interface OnFragmentInteractionListener {
    // TODO: Update argument type and name
    void onFragmentInteraction(Uri uri);
  }
}
