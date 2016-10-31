package com.dellkan.fragmentbootstrap;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialmenu.extras.toolbar.MaterialMenuIconCompat;
import com.dellkan.fragmentbootstrap.fragmentutils.IAcceptUpdates;
import com.dellkan.fragmentbootstrap.fragmentutils.IHasParent;
import com.dellkan.fragmentbootstrap.fragmentutils.IRequire;
import com.dellkan.fragmentbootstrap.fragmentutils.LifecycleDelegateFragment;
import com.dellkan.fragmentbootstrap.fragmentutils.OverlayFragment;
import com.dellkan.fragmentbootstrap.transitions.IHasSharedElements;
import com.dellkan.fragmentbootstrap.transitions.SharedElementTransition;
import com.dellkan.fragmentbootstrap.transitions.SharedElements;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.List;
import java.util.Random;

public abstract class FBActivity<MainFragment extends Fragment> extends AppCompatActivity {
    // Drawer navigation stuff
    private DrawerLayout mDrawer;

    // Navigation icon with animations
    MaterialMenuIconCompat mNavIcon;
    boolean isDrawerOpened = false;

    // Fragment history
    private WeakReference<Fragment> mActiveFragment = new WeakReference<Fragment>(null);

    // Static reference to local instance
    private static WeakReference<FBActivity> mInstance = null;

    /*
        Lifecycle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mInstance = new WeakReference<FBActivity>(this);
        super.onCreate(savedInstanceState);

        setInitialView(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mInstance = new WeakReference<FBActivity>(this);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onResume() {
        mInstance = new WeakReference<FBActivity>(this);
        super.onResume();

        if (mShouldClearBackstack) {
            clearBackstack();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        mInstance = new WeakReference<FBActivity>(this);
        super.attachBaseContext(newBase);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment.getId() == R.id.container) {
            mActiveFragment = new WeakReference<>(fragment);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //mDrawerToggle.syncState();
        isDrawerOpened = mDrawer.isDrawerOpen(GravityCompat.START); // or END, LEFT, RIGHT
        mNavIcon.syncState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mNavIcon.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mInstance = new WeakReference<FBActivity>(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment activeFragment = getActiveFragment();
        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            // If fragment handles options menu, give it first shot at handling navigation.
            // This pattern must be utilized for every action we also use here.
            if (activeFragment != null && activeFragment.hasOptionsMenu() && activeFragment.isAdded() && activeFragment.onOptionsItemSelected(item)) {
                return true;
            } else {
                if (mDrawer.isDrawerOpen(GravityCompat.START)) {
                    mDrawer.closeDrawer(GravityCompat.START);
                } else {
                    mDrawer.openDrawer(GravityCompat.START);

                    // Hide keyboard
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(findViewById(R.id.container).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }

        // If the following line returns false, any attached fragment with options menu will be given a crack at it.
        return super.onOptionsItemSelected(item);
    }

    /*
        Fragment operations
     */

    public interface FragmentAnimationCallback {
        void setAnimations(FragmentTransaction transaction);
    }

    public static Fragment getActiveFragment() {
        FBActivity activity = getInstance();
        if (activity != null && activity.mActiveFragment != null) {
            return (Fragment) activity.mActiveFragment.get();
        }
        return null;
    }

	/*
		Swap fragment + onBackPressed hijack
	 */
	private Date backLastPressed = null;
	@Override
	public void onBackPressed() {
		if (mDrawer.isDrawerOpen(GravityCompat.START)) {
			mDrawer.closeDrawer(GravityCompat.START);
		} else {
			Fragment active = getActiveFragment();

			// If we're at root, offer to close the app
			if (getMainFragmentClass().isInstance(active)) {
				Date now = new Date(System.currentTimeMillis());
				if (backLastPressed == null || backLastPressed.before(new Date(now.getTime() - closeTimeout()))) {
					backLastPressed = now;
					Toast.makeText(this, "Are you sure you want to close the application?", Toast.LENGTH_SHORT).show();
				} else {
					try {
						//noinspection FinalizeCalledExplicitly
						this.finalize(); // Yeah, we're doing this explicitly
						System.exit(0);
					} catch (Throwable throwable) {
						throwable.printStackTrace();
					}
				}
			}
			// If not at root, we can either go to root, or to whatever parent
			// defined by the active fragment
			else {
				if (active instanceof IHasParent) {
					// If we know of a parent, search for that parent explicitly in the backstack log
					Class<? extends Fragment> parentClass = ((IHasParent) active).getHierarchyParent();
					swapFragment(parentClass);
				} else  {
					swapFragment(getMainFragmentClass());
				}
			}
		}
	}

    public static boolean swapFragment(Class<? extends Fragment> fragmentClass) {
        if (fragmentClass == null) { // FIXME: Check if getMainFragmentClass returns the wrong class
            return false;
        }

        FBActivity activity = getInstance();
        if (activity != null && !activity.isFinishing()) {
	        FragmentManager manager = activity.getSupportFragmentManager();
	        boolean foundParent = false;

            // Hide keyboard
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(activity.findViewById(R.id.container).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            // If we're moving to where we were, then stawp.
            Fragment inContainer = manager.findFragmentById(R.id.container);
            if (inContainer != null && inContainer.getClass().equals(fragmentClass)) {
                return false;
            }

            // Search for parent
            try {
                if (!(activity.getMainFragmentClass().equals(fragmentClass))) {
                    foundParent = manager.popBackStackImmediate(fragmentClass.getName(), 0);
                } else {
	                foundParent = manager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
            } catch (IllegalStateException e) {
                // manager.popBackstackImmediate has given rise to a variety amount of mandelbugs,
                // which we're completely unable to debug further at this point due to lack of method of reproduction.
                // As such, all we can do for now is take random stabs, as well as log when the issue occurs
            }

	        // Parent was found in the backstack, and reused
	        if (foundParent) {
		        activity.mActiveFragment = new WeakReference<>(manager.findFragmentById(R.id.container));

		        // Overlay
		        activity.toggleOverlayToolbar(getActiveFragment());

		        return true;
	        } else {
		        //noinspection TryWithIdenticalCatches
		        try {
			        swapFragment(fragmentClass.newInstance());
		        } catch (InstantiationException e) {
			        e.printStackTrace();
		        } catch (IllegalAccessException e) {
			        e.printStackTrace();
		        }
	        }
        }
	    return false;
    }

    public static void swapFragment(Fragment fragment) {
        swapFragment(fragment, null);
    }

	public static void swapFragment(Fragment fragment, @Nullable FragmentAnimationCallback animationCallback) {
		swapFragment(fragment, animationCallback, null);
	}

    public static void swapFragment(Fragment fragment, @Nullable FragmentAnimationCallback animationCallback, @Nullable SharedElements sharedElements) {
        FBActivity activity = getInstance();
        if (activity != null) {
            // Close the drawer if it's still open
            if (activity.mDrawer != null && activity.mDrawer.isDrawerOpen(GravityCompat.START)) {
                activity.mDrawer.closeDrawer(GravityCompat.START);
            }
            if (!activity.isFinishing()) {
                FragmentManager manager = activity.getSupportFragmentManager();
                Fragment oldFragment = getActiveFragment();

                // Check if fragment's requirement is satisfied first
                if (fragment instanceof IRequire) {
                    Class<? extends Fragment> newFragClass = ((IRequire) fragment).redirect();
                    if (newFragClass != null) {
                        swapFragment(newFragClass);
                        return;
                    }
                }

                activity.toggleOverlayToolbar(fragment);

                if (oldFragment != null) {
                    // If we're moving to where we already are, update it instead of swapping
                    if (oldFragment.getClass() == fragment.getClass()) {
                        if (fragment.getArguments() != null && oldFragment instanceof IAcceptUpdates) {
                            ((IAcceptUpdates) oldFragment).incomingUpdate(fragment.getArguments());
                        }
                        return;
                    }

                    IHasParent oldFragmentHierarchyCompatible = oldFragment instanceof IHasParent ? (IHasParent) oldFragment : null;
                    IHasParent newFragmentHierarchyCompatible = fragment instanceof IHasParent ? (IHasParent) fragment : null;

                    // If we're moving to a fragment outside of our current hierarchy, clear the 'stack
                    if (newFragmentHierarchyCompatible == null
                            || (oldFragmentHierarchyCompatible != null
                            && !oldFragmentHierarchyCompatible.getClass().equals(newFragmentHierarchyCompatible.getHierarchyParent())
                            && oldFragmentHierarchyCompatible.getHierarchyParent() != newFragmentHierarchyCompatible.getHierarchyParent()
                    )) {
                        clearBackstack();
                    }
                }

                FragmentTransaction transaction = manager.beginTransaction();

	            /*
	                Animations
	             */
                if (animationCallback != null) {
                    animationCallback.setAnimations(transaction);
                } else {
	                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                }

	            // Check if we got any SharedElements passed to us
	            if (sharedElements != null && oldFragment instanceof IHasSharedElements) {
		            sharedElements = ((IHasSharedElements) oldFragment).getSharedElements();
	            }

	            if (sharedElements != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
		            fragment.setSharedElementEnterTransition(new SharedElementTransition().setDuration(sharedElements.getDuration()));
		            fragment.setSharedElementReturnTransition(new SharedElementTransition().setDuration(sharedElements.getDuration()));
		            for (SharedElements.SharedElement sharedElement : sharedElements.getSharedElements()) {
			            if (ViewCompat.getTransitionName(sharedElement.getView()) == null) {
				            ViewCompat.setTransitionName(sharedElement.getView(), Long.toString(new Random().nextLong(), 32));
			            }
			            transaction.addSharedElement(sharedElement.getView(), sharedElement.getName());
		            }
	            }

                transaction.replace(R.id.container, fragment);

                // Add to backstack, enabling back button
                if (!(getInstance().getMainFragmentClass().isInstance(fragment))) {
                    transaction.addToBackStack(fragment.getClass().getName());
                } else {
                    /* FIXME: The way MainFragment isn't placed in backstack, creates duplicates of MainFragment which are simultaneously active,
                     Which can in some circumstances create overlap over certain elements from the-still-active MainFragment.
                     Therefore, we explicitly remove all MainFragments we know are duplicates
                     Of course, the better fix would be to make sure duplicate isn't created in the first place
                     */
                    List<Fragment> fragments = manager.getFragments();
                    if (fragments != null) {
                        for (Fragment activeFragment : manager.getFragments()) {
                            if (getInstance().getMainFragmentClass().isInstance(activeFragment) && !activeFragment.equals(fragment)) {
                                transaction.remove(activeFragment);
                            }
                        }
                    }
                }

                // Commit the transaction
                try {
                    transaction.commit();
                } catch (IllegalStateException e) {
                    // Abandon ship
                }

                // Hide keyboard
                InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(activity.findViewById(R.id.container).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    public static void addFragment(final Fragment fragment, final String tag) {
        runOnContext(new ContextCallback() {
            @Override
            public void context(FBActivity context) {
                FragmentManager manager = context.getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();

                transaction.add(fragment, tag);

                transaction.commit();
            }
        });
    }

    public void enableOverlayToolbar(boolean toggle) {
        mNavIcon.animateState(toggle ? MaterialMenuDrawable.IconState.ARROW : MaterialMenuDrawable.IconState.BURGER);
    }

    public void toggleOverlayToolbar(Class<? extends Fragment> newFragment) {
        enableOverlayToolbar(
                OverlayFragment.class.isAssignableFrom(newFragment)
                        && !getMainFragmentClass().equals(newFragment)
        );
    }

    public void toggleOverlayToolbar(Fragment newFragment) {
        enableOverlayToolbar(
                newFragment instanceof OverlayFragment
                        && !getMainFragmentClass().isInstance(newFragment)
        );
    }

    /*
        Utility
     */

    public static FBActivity getInstance() {
        return mInstance != null ? mInstance.get() : null;
    }

    public static void runOnContext(ContextCallback callback) {
        FBActivity activity = getInstance();
        if (activity != null) {
            callback.context(activity);
        }
    }

    public static interface ContextCallback {
        public void context(FBActivity context);
    }

    private static boolean mShouldClearBackstack = false;
    public static void clearBackstack() {
        mShouldClearBackstack = true;
        try {
            FBActivity activity = getInstance();
            if (activity != null) {
                FragmentManager manager = activity.getSupportFragmentManager();
                if (manager != null && !activity.isFinishing()) {
                    if (manager.getBackStackEntryCount() > 0) {
                        manager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                    if (manager.getBackStackEntryCount() == 0) {
                        mShouldClearBackstack = false;
                    }
                }
            }

        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

	public @ColorRes int getColorValueFromReference(@ColorRes int res, int defaultColor) {
		TypedValue value = new TypedValue();
		getResources().getValue(R.color.icon_toolbar, value, true);

		TypedArray attributes = getTheme().obtainStyledAttributes(new int[]{value.data});
		@ColorRes int color = attributes.getColor(0, defaultColor);
		attributes.recycle();
		return color;
	}

    /*
        Customizable
     */

	/**
     * Overwrite this if you'd like to change the outer app container view.
     * Useful if you would like to replace the toolbar with your own creation, set your own parameters,
     * or would like to remove the menu.
     *
     * Remember to either use the same ID references, or also override {@link #setInitialView(Bundle)}
     *
     * @return the activity container
     */
    protected @LayoutRes int getLayoutContainer() {
        return R.layout.activity_container;
    }

    public abstract Class<MainFragment> getMainFragmentClass();

    public abstract MainFragment getMainFragment();

    public abstract @Nullable Fragment getMenuFragment();

	/**
     * When the app initially opens, this runs to set the initial view.
     * @param savedInstanceState
     */
    protected void setInitialView(Bundle savedInstanceState) {
        // Set main container view
        setContentView(getLayoutContainer());

        // Setup fragments
        try {
	        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

	        // Menu
	        Fragment menu = getMenuFragment();
	        if (menu != null && findViewById(R.id.menu) != null) {

                transaction.replace(R.id.menu, getMenuFragment());

            }

	        // LifecycleDelegate
	        transaction.add(new LifecycleDelegateFragment(), LifecycleDelegateFragment.TAG);

	        transaction.commit();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup action bar home button
        // Set up drawer toggle button
        this.mDrawer = (DrawerLayout) this.findViewById(R.id.drawer_layout);

        mNavIcon = new MaterialMenuIconCompat(this, getColorValueFromReference(R.color.icon_toolbar, Color.WHITE), MaterialMenuDrawable.Stroke.THIN);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeButtonEnabled(true);
        }

        // Set our initial view, the home screen
        if (savedInstanceState == null) {
            FBActivity.swapFragment(getMainFragment());
        }
    }

	/**
     * Customize the timeout used to determine if the app should close when pressing back
     *
     * @return timeout in milliseconds. If back is pressed twice within this period of time, the app closes.
     */
    protected int closeTimeout() {
        return 2000;
    }
}
