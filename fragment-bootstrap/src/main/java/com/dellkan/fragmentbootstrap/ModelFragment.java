package com.dellkan.fragmentbootstrap;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dellkan.robobinding.helpers.common.LayoutBuilder;
import com.dellkan.robobinding.helpers.model.PresentationModelWrapper;

import java.io.Serializable;

/**
 * Shorthand fragment that will allow you to set up a typical robobinding-enabled fragment fit for most purposes
 */
public abstract class ModelFragment extends OverlayFragment {
	static final String MODEL_KEY = "model";
	protected PresentationModelWrapper model;
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		try {
			return LayoutBuilder.getViewBinder(inflater.getContext()).inflateAndBindWithoutAttachingToRoot(getLayout(), getModel().getPresentationModel(), container);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public abstract @LayoutRes int getLayout();

	public PresentationModelWrapper getModel() {
		if (model == null) {
			Bundle args = getArguments();
			if (args != null && args.containsKey(MODEL_KEY)) {
				model = (PresentationModelWrapper) args.getSerializable(MODEL_KEY);
			}
		}
		return model;
	}

	public static Bundle getProcessedArgs(PresentationModelWrapper model) {
		Bundle args = new Bundle();
		args.putSerializable(MODEL_KEY, (Serializable) model);
		return args;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (getModel() != null) {
			outState.putSerializable(MODEL_KEY, (Serializable) getModel());
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null && savedInstanceState.containsKey(MODEL_KEY)) {
			model = (PresentationModelWrapper) savedInstanceState.getSerializable(MODEL_KEY);
		}
	}
}
