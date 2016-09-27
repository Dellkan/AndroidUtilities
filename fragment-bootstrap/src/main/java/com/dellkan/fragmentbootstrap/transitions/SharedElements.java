package com.dellkan.fragmentbootstrap.transitions;

import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SharedElements {
	private List<SharedElement> mSharedElements = new ArrayList<>();
	private long duration = -1;

	public SharedElements(SharedElement... sharedElement) {
		mSharedElements.addAll(Arrays.asList(sharedElement));
	}

	public SharedElements(long duration, SharedElement... sharedElement) {
		this.duration = duration;
		mSharedElements.addAll(Arrays.asList(sharedElement));
	}

	public void addSharedElement(SharedElement sharedElement) {
		mSharedElements.add(sharedElement);
	}

	public List<SharedElement> getSharedElements() {
		return mSharedElements;
	}

	public long getDuration() {
		return duration;
	}

	public static class SharedElement {
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
}
