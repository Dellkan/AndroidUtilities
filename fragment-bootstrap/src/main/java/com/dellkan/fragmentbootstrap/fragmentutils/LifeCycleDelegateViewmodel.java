package com.dellkan.fragmentbootstrap.fragmentutils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.dellkan.robobinding.helpers.model.PresentationModelWrapper;

public abstract class LifeCycleDelegateViewmodel extends PresentationModelWrapper implements LifeCycleDelegate {
	@Override
	public void setInitialSavedState(Fragment.SavedState state) {

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

	}

	@Override
	public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {

	}

	@Override
	public void onAttachFragment(Fragment childFragment) {

	}

	@Override
	public void onAttach(Context context) {

	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {

	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {

	}

	@Override
	public void onViewStateRestored(@Nullable Bundle savedInstanceState) {

	}

	@Override
	public void onStart() {

	}

	@Override
	public void onResume() {

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {

	}

	@Override
	public void onPause() {

	}

	@Override
	public void onStop() {

	}

	@Override
	public void onLowMemory() {

	}

	@Override
	public void onDestroyView() {

	}

	@Override
	public void onDestroy() {

	}

	@Override
	public void onDetach() {

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {

	}

	@Override
	public void onDestroyOptionsMenu() {

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return false;
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

	}
}
