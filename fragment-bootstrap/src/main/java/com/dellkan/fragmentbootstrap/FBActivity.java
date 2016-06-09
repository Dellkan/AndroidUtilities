package com.dellkan.fragmentbootstrap;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialmenu.extras.toolbar.MaterialMenuIconCompat;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.Locale;

public abstract class FBActivity<MainFragment extends Fragment> extends AppCompatActivity {
    // Drawer navigation stuff
    private DrawerLayout mDrawer;
    //private ActionBarDrawerToggle mDrawerToggle;
    private float mLastTranslate = 0.0f;

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

        setInitialView();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mInstance = new WeakReference<FBActivity>(this);
        super.onConfigurationChanged(newConfig);
    }

    private static String langCode = Locale.getDefault().getLanguage();
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

    public static void swapFragment(Class<? extends Fragment> fragmentClass) {
        if (fragmentClass == null) {
            fragmentClass = getInstance().getMainFragmentClass();
        }
        boolean foundParent = false;
        FBActivity activity = getInstance();
        FragmentManager manager = null;
        if (activity != null && !activity.isFinishing()) {
            // Hide keyboard
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(activity.findViewById(R.id.container).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            manager = activity.getSupportFragmentManager();

            // If we're moving to where we were, then stawp.
            Fragment inContainer = manager.findFragmentById(R.id.container);
            if (inContainer != null && inContainer.getClass().equals(fragmentClass)) {
                return;
            }

            // Search for parent
            try {
                foundParent = manager.popBackStackImmediate(fragmentClass.getName(), 0);
            } catch (IllegalStateException e) {
                // manager.popBackstackImmediate has given rise to a variety amount of mandelbugs,
                // which we're completely unable to debug further at this point due to lack of method of reproduction.
                // As such, all we can do for now is take random stabs, as well as log when the issue occurs
            }
        }
        if (foundParent) {
            activity.mActiveFragment = new WeakReference<>(manager.findFragmentById(R.id.container));

            // Overlay
            activity.toggleOverlayToolbar(fragmentClass);
        } else {
            try {
                swapFragment(fragmentClass.newInstance());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static void swapFragment(Fragment fragment) {
        swapFragment(fragment, null);
    }

    public static void swapFragment(Fragment fragment, FragmentAnimationCallback aniCallback) {
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
                if (aniCallback != null) {
                    aniCallback.setAnimations(transaction);
                }

                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
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
                    for (Fragment activeFragment : manager.getFragments()) {
                        if (getInstance().getMainFragmentClass().isInstance(activeFragment) && !activeFragment.equals(fragment)) {
                            transaction.remove(activeFragment);
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

    private Date backLastPressed = null;

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager manager = getSupportFragmentManager();
            Fragment active = getActiveFragment();

            boolean foundParent = false;

            if (active instanceof IHasParent) {
                // If we know of a parent, search for that parent explicitly in the backstack log
                Class<? extends Fragment> parentClass = ((IHasParent) active).getHierarchyParent();
                if (manager.findFragmentById(R.id.container).getClass().equals(parentClass)) {
                    // Do nothing. We wanted to move to where we are. (also, wtf?)
                    // (this will trigger the !foundParent below, replacing instead of reusing our target fragment)
                } else {
                    try {
                        foundParent = manager.popBackStackImmediate(parentClass.getName(), 0);
                        if (!foundParent) {
                            clearBackstack();
                        }
                    } catch (IllegalStateException e) {
                        // If we get IllegalStateException, such as #Fragment already added,
                        // Hopefully we can circumvent the issue by creating new fragment instead
                    }
                }
            }

            if (!foundParent) {
                // Poor kid didn't have any parents. Let's create one who can adopt it.
                if (active != null && active instanceof IHasParent) {
                    try {
                        Fragment fragment = ((IHasParent) active).getHierarchyParent().newInstance();
                        //fragment.setArguments(((IHasParent) active).getHierarchyParentArgs());
                        swapFragment(fragment);
                        foundParent = true;
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

                if (!foundParent && !(getMainFragmentClass().getClass().isInstance(active))) { // None would have it; send it along to the orphanage (home)
                    swapFragment(getMainFragment());
                } else if (getMainFragmentClass().isInstance(active)) {
                    Date now = new Date(System.currentTimeMillis());
                    if (backLastPressed == null || backLastPressed.before(new Date(now.getTime() - 2000))) {
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
            } else {
                // If backstack worked, then we must manually update mActiveFragment because onAttachFragment won't be triggered.
                mActiveFragment = new WeakReference<>(manager.findFragmentById(R.id.container));
                toggleOverlayToolbar(mActiveFragment.get());
            }
        }
    }

    /*
        Utility
     */

    public static FBActivity getInstance() {
        return mInstance != null ? mInstance.get() : null;
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

    /*
        Customizable
     */
    public @LayoutRes int getLayoutContainer() {
        return R.layout.activity_container;
    }

    public abstract Class<MainFragment> getMainFragmentClass();

    public abstract MainFragment getMainFragment();

    public abstract Fragment getMenuFragment();

    protected void setInitialView() {
        // Set main container view
        setContentView(getLayoutContainer());

        // Setup fragments
        try {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.menu, getMenuFragment());

            transaction.commit();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup action bar home button
        // Set up drawer toggle button
        this.mDrawer = (DrawerLayout) this.findViewById(R.id.drawer_layout);

        mNavIcon = new MaterialMenuIconCompat(this, Color.WHITE, MaterialMenuDrawable.Stroke.THIN);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeButtonEnabled(true);
        }

        // Set our initial view, the home screen
        FBActivity.swapFragment(getMainFragmentClass());
    }
}
