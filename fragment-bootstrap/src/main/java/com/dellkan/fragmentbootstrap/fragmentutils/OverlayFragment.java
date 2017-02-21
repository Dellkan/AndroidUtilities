package com.dellkan.fragmentbootstrap.fragmentutils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.dellkan.fragmentbootstrap.FBActivity;

public abstract class OverlayFragment extends LifeCycleDelegateFragment implements IHasParent {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home && !this.getClass().equals(FBActivity.getInstance().getMainFragmentClass())) {
            getActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Class<? extends Fragment> getHierarchyParent() {
        try {
            return FBActivity.getInstance().getMainFragmentClass();
        } catch (NullPointerException e) {
            return null;
        }
    }
}
