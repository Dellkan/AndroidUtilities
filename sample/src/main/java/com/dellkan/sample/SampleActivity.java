package com.dellkan.sample;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.dellkan.ContextFragment;
import com.dellkan.fragmentbootstrap.FBActivity;
import com.dellkan.fragmentbootstrap.R;
import com.dellkan.sample.fragments.MainFragment;
import com.dellkan.sample.fragments.MenuFragment;
import com.dellkan.sample.viewmodels.Main;

public class SampleActivity extends FBActivity<MainFragment> {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SampleActivity.addFragment(ContextFragment.newInstance(), "net");
    }

    @Override
    public MainFragment getMainFragment() {
        return (MainFragment) MainFragment.newInstance(MainFragment.class, Main.get());
    }

    @Override
    public Class<MainFragment> getMainFragmentClass() {
        return MainFragment.class;
    }

    @Override
    public Fragment getMenuFragment() {
        return MenuFragment.newInstance();
    }
}
