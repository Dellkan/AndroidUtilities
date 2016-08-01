package com.dellkan.fragmentbootstrap;

import android.view.View;

public class SharedElement {
	private View view;
	private String name;

	public SharedElement(View view, String name) {
		this.view = view;
		this.name = name;
	}

	public View getView() {
		return view;
	}

	public String getName() {
		return name;
	}
}
