package com.dellkan.sample.fragments;

import android.os.Bundle;

import com.dellkan.fragmentbootstrap.AbstractMenuFragment;
import com.dellkan.fragmentbootstrap.ModelFragment;
import com.dellkan.sample.R;
import com.dellkan.sample.viewmodels.Menu;

public class MenuFragment extends AbstractMenuFragment {
    @Override
    public int getLayout() {
        return R.layout.menu_fragment;
    }

    public static MenuFragment newInstance() {

        Bundle args = new Bundle();

        MenuFragment fragment = new MenuFragment();
        fragment.setArguments(getProcessedArgs(new Menu()));
        return fragment;
    }
}
