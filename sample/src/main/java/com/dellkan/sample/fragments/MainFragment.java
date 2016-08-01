package com.dellkan.sample.fragments;

import android.os.Bundle;

import com.dellkan.fragmentbootstrap.ModelFragment;
import com.dellkan.robobinding.helpers.model.PresentationModelWrapper;
import com.dellkan.sample.R;
import com.dellkan.sample.viewmodels.Main;

public class MainFragment extends ModelFragment {
    @Override
    public int getLayout() {
        return R.layout.main_fragment;
    }

    @Override
    public PresentationModelWrapper getModel() {
        PresentationModelWrapper model = super.getModel();
        if (model == null) {
            model = Main.get();
        }
        return model;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }
}
