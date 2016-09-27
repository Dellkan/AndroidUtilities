package com.dellkan.sample.viewmodels;

import com.dellkan.fragmentbootstrap.fragmentutils.ModelFragment;
import com.dellkan.fragmentbootstrap.transitions.SharedElements;
import com.dellkan.robobinding.helpers.model.PresentationModelWrapper;
import com.dellkan.robobinding.helpers.modelgen.Get;
import com.dellkan.robobinding.helpers.modelgen.GetSet;
import com.dellkan.robobinding.helpers.modelgen.ItemPresentationModel;
import com.dellkan.robobinding.helpers.modelgen.PresentationMethod;
import com.dellkan.robobinding.helpers.modelgen.PresentationModel;
import com.dellkan.sample.R;
import com.dellkan.sample.SampleActivity;
import com.dellkan.sample.fragments.NavigationDepthTestFragment;
import com.dellkan.sample.fragments.YoutubeVideoFragment;

import org.robobinding.widget.view.ClickEvent;

import java.io.Serializable;

@ItemPresentationModel
@PresentationModel
public class YoutubeVideo extends PresentationModelWrapper implements Serializable {
	private String id;
	@Get
	String title;
	@Get
	String thumbnail;
	@GetSet
	String input;

	@PresentationMethod
	public String getTitleTransitionName() {
		return String.format("%s_title", id);
	}

	@PresentationMethod
	public String getThumbnailTransitionName() {
		return String.format("%s_thumbnail", id);
	}

	public YoutubeVideo(String id, String title, String thumbnail) {
		this.id = id;
		this.title = title;
		this.thumbnail = thumbnail;
	}

	@PresentationMethod
	public void open(ClickEvent event) {
		SampleActivity.swapFragment(
				ModelFragment.newInstance(YoutubeVideoFragment.class, this),
				null,
				new SharedElements(
						SampleActivity.getInstance().getResources().getInteger(android.R.integer.config_shortAnimTime),
						new SharedElements.SharedElement(event.getView().findViewById(R.id.thumbnail), "thumbnail"),
						new SharedElements.SharedElement(event.getView().findViewById(R.id.title), "title")
				)
		);
	}

	@PresentationMethod
	public void clickButton() {
		SampleActivity.swapFragment(ModelFragment.newInstance(NavigationDepthTestFragment.class, this));
	}
}
