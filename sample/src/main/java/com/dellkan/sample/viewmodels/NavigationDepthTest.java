package com.dellkan.sample.viewmodels;

import com.dellkan.robobinding.helpers.model.PresentationModelWrapper;
import com.dellkan.robobinding.helpers.modelgen.Get;
import com.dellkan.robobinding.helpers.modelgen.PresentationModel;

import java.io.Serializable;

@PresentationModel
public class NavigationDepthTest extends PresentationModelWrapper implements Serializable {
	@Get
	String title = "We'll enable video playing here shoon.";
}
