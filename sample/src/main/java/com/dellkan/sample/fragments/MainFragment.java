package com.dellkan.sample.fragments;

import android.os.Bundle;

import com.dellkan.fragmentbootstrap.ModelFragment;
import com.dellkan.sample.R;
import com.dellkan.sample.viewmodels.Main;

public class MainFragment extends ModelFragment {
    @Override
    public int getLayout() {
        return R.layout.main_fragment;
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        fragment.setArguments(getProcessedArgs(new Main()));

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }
}
