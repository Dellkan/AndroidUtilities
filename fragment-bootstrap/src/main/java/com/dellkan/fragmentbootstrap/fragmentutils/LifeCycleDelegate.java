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

public interface LifecycleDelegate {
	public void setInitialSavedState(Fragment.SavedState state);
	public void onActivityResult(int requestCode, int resultCode, Intent data);
	public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState);
	public void onAttachFragment(Fragment childFragment);
	public void onAttach(Context context);
	public void onCreate(@Nullable Bundle savedInstanceState);
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState);
	public void onActivityCreated(@Nullable Bundle savedInstanceState);
	public void onViewStateRestored(@Nullable Bundle savedInstanceState);
	public void onStart();
	public void onResume();
	public void onSaveInstanceState(Bundle outState);
	public void onConfigurationChanged(Configuration newConfig);
	public void onPause();
	public void onStop();
	public void onLowMemory();
	public void onDestroyView();
	public void onDestroy();
	public void onDetach();
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater);
	public void onPrepareOptionsMenu(Menu menu);
	public void onDestroyOptionsMenu();
	public boolean onOptionsItemSelected(MenuItem item);
	public void onOptionsMenuClosed(Menu menu);
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo);
}
