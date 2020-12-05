package com.example.raksha;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class profile extends Fragment {

    private OnFragmentInteractionListener mListener;
    CardView profile,authenticate,emergency;
    TextView stat,statof;
    DataBaseHelper mydb;
    public String value;
    private String user_Name,e_Mail;

    public profile() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        mydb = new DataBaseHelper(getActivity());
        stat = (TextView)v.findViewById(R.id.connected);
        statof = (TextView)v.findViewById(R.id.failedconn);
        profile = (CardView)v.findViewById(R.id.profile);
        authenticate = (CardView)v.findViewById(R.id.Authen);
        emergency = (CardView)v.findViewById(R.id.Emergen);
        user_Name = getArguments().getString("Uname");
        e_Mail = getArguments().getString("emailss");
        ArrayList<String> device = mydb.getdevice();
        String DevId = device.get(1);
        if (DevId.equals("00")){
            statof.setVisibility(View.VISIBLE);
        }
        else{
            stat.setVisibility(View.VISIBLE);
        }
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent prof = new Intent(getActivity(),updatedata.class);
                prof.putExtra("UserName",user_Name);
                prof.putExtra("UserMail",e_Mail);
                startActivity(prof);
            }
        });
        authenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent prof1 = new Intent(getActivity(),auth_user.class);
                prof1.putExtra("UserName",user_Name);
                prof1.putExtra("UserMail",e_Mail);
                startActivity(prof1);
            }
        });
        emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent prof2 = new Intent(getActivity(),emer_gency.class);
                prof2.putExtra("UserName",user_Name);
                prof2.putExtra("UserMail",e_Mail);
                startActivity(prof2);
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
