package com.dellkan.fragmentbootstrap;

import android.support.v4.app.Fragment;

public interface IHasParent {
	Class<? extends Fragment> getHierarchyParent();
}
