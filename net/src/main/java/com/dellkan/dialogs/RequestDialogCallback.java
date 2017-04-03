package com.dellkan.dialogs;

import android.support.annotation.NonNull;

import com.dellkan.net.BuildConfig;
import com.dellkan.net.RequestCallback;
import com.dellkan.net.parsers.InboundParser;
import com.dellkan.net.parsers.json.JSONInboundParser;
import com.dellkan.net.R;
import com.dellkan.net.Request;

import java.io.InputStream;

/**
 * Identical to {@link JSONInboundParser} except that it reroutes all callbacks through so you get access to the {@link RequestDialog}
 */
public class RequestDialogCallback<T extends InboundParser> extends RequestCallback<T> {
    private RequestDialog dialog;

    public RequestDialogCallback(@NonNull T parser) {
        super(parser);
    }

    void setDialog(RequestDialog dialog) {
        this.dialog = dialog;
    }

	// Reroute through original callbacks
    @Override
    public final void onStart() {
        onStart(dialog);
    }

    @Override
    public final void onFinish() {
        onFinish(dialog);

        super.onFinish();
    }

    @Override
    public final void onSuccess() {
        onSuccess(dialog);
    }

    @Override
    public final void onFailure() {
        onFailure(dialog);
    }

    // Routed callbacks

    /**
     * See {@link #onStart()}
     * @param dialog Requesting dialog. Use it to change content, dismiss, etc
     */
    public void onStart(RequestDialog dialog) {
        dialog.setMessage(R.string.sending);
    }

    /**
     * See {@link #onFinish()}
     */
    public void onFinish(RequestDialog dialog) {
    }

    /**
     * See {@link #onSuccess()}
     */
    public void onSuccess(RequestDialog dialog) {
    }

    /**
     * Default handling is to set a generic error and stop loading
     * @param dialog A reference to the SinglePartRequestDialog
     */
    public void onFailure(RequestDialog dialog) {
        if (BuildConfig.DEBUG) {
            if (getException() != null) {
                dialog.setFinished(String.format("%s\n%s", getParser().getRequest().getURL().toString(), getException().getMessage()));
            } else {
                dialog.setFinished(String.format("%d\n%s", getResponseCode(), getResponse()));
            }
        } else {
            dialog.setFinished(R.string.error_network);
        }
    }
}
