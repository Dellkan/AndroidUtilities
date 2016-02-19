package com.dellkan.fragmentbootstrap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

public abstract class OverlayFragment extends Fragment implements IHasParent {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            FBActivity.swapFragment(getHierarchyParent());
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
