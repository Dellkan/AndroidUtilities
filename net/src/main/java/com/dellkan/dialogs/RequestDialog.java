package com.dellkan.dialogs;

import android.support.annotation.StringRes;

/**
 * Interface to ensure that all RequestDialogs have a common interface to update the dialog as necessary,
 * allowing us to replace the underlying dialog at will
 * @see #setMessage(int)
 * @see #setFinished(int)
 * @see #dismiss()
 */
public interface RequestDialog {
    /**
     * Set a message to display within the dialog, as feedback to the user
     * @param message The message you want to show
     * @see #setMessage(String)
     */
    void setMessage(@StringRes int message);

    /**
     * Set a message to display within the dialog, as feedback to the user
     * @param message The message you want to show
     * @see #setMessage(int)
     */
    void setMessage(String message);

    /**
     * Replace the ProgressDialog with an AlertDialog, allowing the user to close
     * @param message the message you want to show to the user
     */
    void setFinished(@StringRes int message);

    void setFinished(String message);

    /**
     * Dismiss the dialog
     */
    void dismiss();
}
