package com.dellkan.sample.fragments;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.dellkan.fragmentbootstrap.fragmentutils.ModelFragment;
import com.dellkan.sample.R;
import com.transitionseverywhere.SidePropagation;
import com.transitionseverywhere.Slide;
import com.transitionseverywhere.TransitionManager;

public class YoutubeVideoFragment extends ModelFragment {
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);

		final Rect viewRect = new Rect();
		container.getGlobalVisibleRect(viewRect);

		Slide transition = new Slide();

		transition.setSlideEdge(Gravity.START);

		transition.setStartDelay(getResources().getInteger(android.R.integer.config_shortAnimTime));
		transition.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));

		transition.addTarget(R.id.input);
		transition.addTarget(R.id.submit);

		transition.setInterpolator(new DecelerateInterpolator());

		// Fix up propagation
		SidePropagation propagation = new SidePropagation();
		propagation.setPropagationSpeed(0.3f);

		transition.setPropagation(propagation);

		TransitionManager.beginDelayedTransition(container, transition);

		return view;
	}

	@Override
	public int getLayout() {
		return R.layout.video_fragment;
	}
}
