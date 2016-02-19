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

public abstract class ModelFragment extends OverlayFragment {
	protected PresentationModelWrapper model;
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		try {
			return LayoutBuilder.getViewBinder(inflater.getContext()).inflateAndBindWithoutAttachingToRoot(getLayout(), getModel().getPresentationModel(), container);
		} catch (Exception e) {

		}
		return null;
	}

	public abstract @LayoutRes int getLayout();

	public boolean updateState() {
		return false;
	}

	public PresentationModelWrapper getModel() {
		if (model == null) {
			Bundle args = getArguments();
			if (args != null && args.containsKey("model")) {
				model = (PresentationModelWrapper) args.getSerializable("model");
			}
		}
		return model;
	}

	public static Bundle getProcessedArgs(PresentationModelWrapper model) {
		Bundle args = new Bundle();
		args.putSerializable("model", (Serializable) model);
		return args;
	}

	@Override
	public void onDestroyView() {
		Bundle args = getArguments();
		if (args != null && updateState()) {
			args.putSerializable("model", (Serializable) getModel());
		}
		super.onDestroyView();
	}
}
