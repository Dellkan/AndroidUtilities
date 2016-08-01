package com.dellkan.sample.viewmodels;

import com.dellkan.fragmentbootstrap.ModelFragment;
import com.dellkan.fragmentbootstrap.SharedElement;
import com.dellkan.robobinding.helpers.model.PresentationModelWrapper;
import com.dellkan.robobinding.helpers.modelgen.Get;
import com.dellkan.robobinding.helpers.modelgen.ItemPresentationModel;
import com.dellkan.robobinding.helpers.modelgen.PresentationModel;
import com.dellkan.sample.R;
import com.dellkan.sample.SampleActivity;
import com.dellkan.sample.fragments.YoutubeVideoFragment;

import org.robobinding.widget.view.ClickEvent;

import java.io.Serializable;

@ItemPresentationModel
@PresentationModel
public class YoutubeVideo extends PresentationModelWrapper implements Serializable {
	@Get
	String title;
	@Get
	String thumbnail;

	public YoutubeVideo(String title, String thumbnail) {
		this.title = title;
		this.thumbnail = thumbnail;
	}

	public void open(ClickEvent event) {
		SampleActivity.swapFragment(
				ModelFragment.newInstance(YoutubeVideoFragment.class, this),
				null,
				new SharedElement(event.getView().findViewById(R.id.thumbnail), "thumbnail"),
				new SharedElement(event.getView().findViewById(R.id.title), "title")
		);
	}
}
