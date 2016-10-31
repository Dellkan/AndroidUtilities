package com.dellkan.fragmentbootstrap.fragmentutils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.dellkan.fragmentbootstrap.FBActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class LifecycleDelegateFragment extends AppCompatDialogFragment implements LifecycleDelegate {
	public static final String TAG = LifecycleDelegateFragment.class.toString();
	private List<WeakReference<LifecycleDelegate>> delegates = new ArrayList<>();

	public static void addGlobalLifecycleDelegate(LifecycleDelegate delegate) {
		getInstance().delegates.add(new WeakReference<>(delegate));
	}

	public void addLifecycleDelegate(LifecycleDelegate delegate) {
		this.delegates.add(new WeakReference<LifecycleDelegate>(delegate));
	}

	public static LifecycleDelegateFragment getInstance() {
		return (LifecycleDelegateFragment) FBActivity.getInstance().getSupportFragmentManager().findFragmentByTag(TAG);
	}

	public void initLifecycleDelegates() {

	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initLifecycleDelegates();

		for (WeakReference<LifecycleDelegate> delegate : delegates) {
			if (delegate != null) {
				LifecycleDelegate callback = delegate.get();
				if (callback != null) {
					callback.onCreate(savedInstanceState);
				}
			}
		}
	}

	@Override
	public void setInitialSavedState(SavedState state) {
		super.setInitialSavedState(state);

		for (WeakReference<LifecycleDelegate> delegate : delegates) {
			if (delegate != null) {
				LifecycleDelegate callback = delegate.get();
				if (callback != null) {
					callback.setInitialSavedState(state);
				}
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		for (WeakReference<LifecycleDelegate> delegate : delegates) {
			if (delegate != null) {
				LifecycleDelegate callback = delegate.get();
				if (callback != null) {
					callback.onActivityResult(requestCode, resultCode, data);
				}
			}
		}
	}

	@Override
	public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
		super.onInflate(context, attrs, savedInstanceState);

		for (WeakReference<LifecycleDelegate> delegate : delegates) {
			if (delegate != null) {
				LifecycleDelegate callback = delegate.get();
				if (callback != null) {
					callback.onInflate(context, attrs, savedInstanceState);
				}
			}
		}
	}

	@Override
	public void onAttachFragment(Fragment childFragment) {
		super.onAttachFragment(childFragment);

		for (WeakReference<LifecycleDelegate> delegate : delegates) {
			if (delegate != null) {
				LifecycleDelegate callback = delegate.get();
				if (callback != null) {
					callback.onAttachFragment(childFragment);
				}
			}
		}
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);

		for (WeakReference<LifecycleDelegate> delegate : delegates) {
			if (delegate != null) {
				LifecycleDelegate callback = delegate.get();
				if (callback != null) {
					callback.onAttach(context);
				}
			}
		}
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		for (WeakReference<LifecycleDelegate> delegate : delegates) {
			if (delegate != null) {
				LifecycleDelegate callback = delegate.get();
				if (callback != null) {
					callback.onViewCreated(view, savedInstanceState);
				}
			}
		}
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		for (WeakReference<LifecycleDelegate> delegate : delegates) {
			if (delegate != null) {
				LifecycleDelegate callback = delegate.get();
				if (callback != null) {
					callback.onActivityCreated(savedInstanceState);
				}
			}
		}
	}

	@Override
	public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);

		for (WeakReference<LifecycleDelegate> delegate : delegates) {
			if (delegate != null) {
				LifecycleDelegate callback = delegate.get();
				if (callback != null) {
					callback.onViewStateRestored(savedInstanceState);
				}
			}
		}
	}

	@Override
	public void onStart() {
		super.onStart();

		for (WeakReference<LifecycleDelegate> delegate : delegates) {
			if (delegate != null) {
				LifecycleDelegate callback = delegate.get();
				if (callback != null) {
					callback.onStart();
				}
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		for (WeakReference<LifecycleDelegate> delegate : delegates) {
			if (delegate != null) {
				LifecycleDelegate callback = delegate.get();
				if (callback != null) {
					callback.onResume();
				}
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		for (WeakReference<LifecycleDelegate> delegate : delegates) {
			if (delegate != null) {
				LifecycleDelegate callback = delegate.get();
				if (callback != null) {
					callback.onSaveInstanceState(outState);
				}
			}
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		for (WeakReference<LifecycleDelegate> delegate : delegates) {
			if (delegate != null) {
				LifecycleDelegate callback = delegate.get();
				if (callback != null) {
					callback.onConfigurationChanged(newConfig);
				}
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		for (WeakReference<LifecycleDelegate> delegate : delegates) {
			if (delegate != null) {
				LifecycleDelegate callback = delegate.get();
				if (callback != null) {
					callback.onPause();
				}
			}
		}
	}

	@Override
	public void onStop() {
		super.onStop();

		for (WeakReference<LifecycleDelegate> delegate : delegates) {
			if (delegate != null) {
				LifecycleDelegate callback = delegate.get();
				if (callback != null) {
					callback.onStop();
				}
			}
		}
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();

		for (WeakReference<LifecycleDelegate> delegate : delegates) {
			if (delegate != null) {
				LifecycleDelegate callback = delegate.get();
				if (callback != null) {
					callback.onLowMemory();
				}
			}
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		for (WeakReference<LifecycleDelegate> delegate : delegates) {
			if (delegate != null) {
				LifecycleDelegate callback = delegate.get();
				if (callback != null) {
					callback.onDestroyView();
				}
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		for (WeakReference<LifecycleDelegate> delegate : delegates) {
			if (delegate != null) {
				LifecycleDelegate callback = delegate.get();
				if (callback != null) {
					callback.onDestroy();
				}
			}
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();

		for (WeakReference<LifecycleDelegate> delegate : delegates) {
			if (delegate != null) {
				LifecycleDelegate callback = delegate.get();
				if (callback != null) {
					callback.onDetach();
				}
			}
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		for (WeakReference<LifecycleDelegate> delegate : delegates) {
			if (delegate != null) {
				LifecycleDelegate callback = delegate.get();
				if (callback != null) {
					callback.onCreateOptionsMenu(menu, inflater);
				}
			}
		}
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		for (WeakReference<LifecycleDelegate> delegate : delegates) {
			if (delegate != null) {
				LifecycleDelegate callback = delegate.get();
				if (callback != null) {
					callback.onPrepareOptionsMenu(menu);
				}
			}
		}
	}

	@Override
	public void onDestroyOptionsMenu() {
		super.onDestroyOptionsMenu();

		for (WeakReference<LifecycleDelegate> delegate : delegates) {
			if (delegate != null) {
				LifecycleDelegate callback = delegate.get();
				if (callback != null) {
					callback.onDestroyOptionsMenu();
				}
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean value = super.onOptionsItemSelected(item);

		if (!value) {
			for (WeakReference<LifecycleDelegate> delegate : delegates) {
				if (delegate != null) {
					LifecycleDelegate callback = delegate.get();
					if (callback != null) {
						if(callback.onOptionsItemSelected(item)) {
							return true;
						}
					}
				}
			}
		}

		return value;
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {
		super.onOptionsMenuClosed(menu);

		for (WeakReference<LifecycleDelegate> delegate : delegates) {
			if (delegate != null) {
				LifecycleDelegate callback = delegate.get();
				if (callback != null) {
					callback.onOptionsMenuClosed(menu);
				}
			}
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		for (WeakReference<LifecycleDelegate> delegate : delegates) {
			if (delegate != null) {
				LifecycleDelegate callback = delegate.get();
				if (callback != null) {
					callback.onCreateContextMenu(menu, v, menuInfo);
				}
			}
		}
	}
}
