package com.dellkan.fragmentbootstrap.fragmentutils;

import android.support.v4.app.Fragment;

public interface IRequire {
    /**
     * With this, you can apply checks to make sure a fragment isn't loaded (this function will be run before being put through a fragment transaction)
     * unless certain conditions are satisfied. For an example: Prevent profile screen from showing unless the user has registered.
     * @return If redirecting, return a fragment class. If not redirecting, return null
     */
    Class<? extends Fragment> redirect();
}
