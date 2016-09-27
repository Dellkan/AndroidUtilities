package com.dellkan.sample.fragments;

import android.support.v4.app.Fragment;

import com.dellkan.fragmentbootstrap.fragmentutils.ModelFragment;
import com.dellkan.sample.R;

public class NavigationDepthTestFragment extends ModelFragment {
	@Override
	public int getLayout() {
		return R.layout.fragment_navigation_depth_test;
	}

	@Override
	public Class<? extends Fragment> getHierarchyParent() {
		return YoutubeVideoFragment.class;
	}
}
