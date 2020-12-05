package com.example.raksha;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class PagerAdpter extends FragmentStatePagerAdapter {
  int NoofTasks;
  Bundle bundle;
  public PagerAdpter(FragmentManager fm, int NoofTabs, Bundle bund) {
    super(fm);
    this.NoofTasks = NoofTabs;
    this.bundle = bund;
  }

  @Override
  public Fragment getItem(int position) {
    switch (position){
      case 0:
        profile pro = new profile();
        pro.setArguments(bundle);
        return pro;
      case 1:
        connection con = new connection();
        con.setArguments(bundle);
        return con;

      default:
        return null;
    }
  }

  @Override
  public int getCount() {
    return NoofTasks;
  }
}
