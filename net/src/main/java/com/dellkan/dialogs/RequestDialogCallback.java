package com.dellkan.dialogs;

import com.dellkan.net.BuildConfig;
import com.dellkan.net.R;
import com.dellkan.net.RequestCallback;

import org.json.JSONException;

/**
 * Identical to {@link com.dellkan.net.RequestCallback} except that it reroutes all callbacks through so you get access to the {@link RequestDialog}
 */
public class RequestDialogCallback extends RequestCallback {
    private RequestDialog dialog;

    public RequestDialogCallback() {
    }

    public RequestDialogCallback(RequestDialog dialog) {
        this.dialog = dialog;
    }

    // Setters
    public RequestDialogCallback setDialog(RequestDialog dialog) {
        this.dialog = dialog;
        return this;
    }

    // Reroute through original callbacks
    @Override
    public final void onStart() {
        super.onStart();
        onStart(dialog);
    }

    @Override
    public final void onFinish() {
        super.onFinish();
        onFinish(dialog);
    }

    @Override
    public final void onSuccess() {
        super.onSuccess();
        onSuccess(dialog);
    }

    @Override
    public void onFailure() {
        super.onFailure();
        onFailure(dialog);
    }

    // Routed callbacks

    /**
     * See {@link #onStart()}
     * @param dialog Requesting dialog. Use it to change content, dismiss, etc
     */
    public void onStart(RequestDialog dialog) {
        dialog.setMessage(R.string.sending);
        dialog.onStart();
    }

    /**
     * See {@link #onFinish()}
     */
    public void onFinish(RequestDialog dialog) {
        dialog.onFinish();
    }

    /**
     * See {@link #onSuccess()}
     */
    public void onSuccess(RequestDialog dialog) {
        dialog.onSuccess();
    }

    /**
     * Default handling is to set a generic error and stop loading
     * @param dialog A reference to the SinglePartRequestDialog
     */
    public void onFailure(RequestDialog dialog) {
        dialog.onFailure();
        if (BuildConfig.DEBUG) {
            if (getException() != null) {
                dialog.setFinished(String.format("%s\n%s", getRequest().getURL().toString(), getException().getMessage()));
            } else {
                try {
                    dialog.setFinished(String.format("%d\n%s", getResponseCode(), getObjectResponse().toString(4)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            dialog.setFinished(R.string.error_network);
        }
    }
}
