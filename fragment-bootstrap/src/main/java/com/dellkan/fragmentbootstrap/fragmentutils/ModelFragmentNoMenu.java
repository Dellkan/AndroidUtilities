package com.dellkan.fragmentbootstrap.fragmentutils;

import android.os.Bundle;

/**
 * ModelFragment usually have options menu handling (for NavigationIcon/R.menu.home), this simply
 * removes that, since the menu in itself shouldn't do anything with how the menu buttons are handled
 */
public abstract class ModelFragmentNoMenu extends ModelFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(false);
	}
}
