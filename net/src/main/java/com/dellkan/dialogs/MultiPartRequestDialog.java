package com.dellkan.dialogs;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.annotation.StringRes;

import com.dellkan.ContextFragment;
import com.dellkan.net.R;
import com.dellkan.net.Request;

import java.util.ArrayList;
import java.util.List;

public final class MultiPartRequestDialog implements RequestDialog {
    private AlertDialog mDialog;
    List<Request> requests = new ArrayList<>();

    public static MultiPartRequestDialog newInstance() {
        ProgressDialog dialog = new ProgressDialog(ContextFragment.getStaticContext());
        dialog.setIndeterminate(true);
        dialog.setMessage(dialog.getContext().getString(R.string.loading));
        dialog.setCancelable(false);

        return new MultiPartRequestDialog(dialog);
    }

    private MultiPartRequestDialog(AlertDialog dialog) {
        this.mDialog = dialog;
    }

    public void setFinished(@StringRes int message) {
        setFinished(ContextFragment.getStaticContext().getString(message));
    }

    public void addRequest(Request request, RequestDialogCallback callback) {
        callback.setDialog(this);

        // Prepare request
        request.setInboundParser(callback);

        // Add request
        requests.add(request);
    }

    public void startRequests() {
        Request request = requests.size() > 0 ? requests.remove(0) : null;
        if (request != null) {
            startRequest(request);
        } else {
            for (Callback callback : callbacks) {
                callback.requestQueueEmpty(this);
            }
        }
    }

    private void startRequest(Request request) {
        mDialog.show();
        request.start();
    }

    @Override
    public void setFinished(String message) {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(ContextFragment.getStaticContext())
                .setCancelable(false)
                .setMessage(message)
                .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        mDialog = builder.show();
    }

    @Override
    public void setMessage(@StringRes int message) {
        mDialog.setMessage(mDialog.getContext().getString(message));
    }

    @Override
    public void setMessage(String message) {
        mDialog.setMessage(message);
    }

    @Override
    public void dismiss() {
        this.mDialog.dismiss();
    }

    /*
        Request lifecycle
     */
    @Override public void onStart() {}
    @Override public void onFinish() {}
    @Override public void onSuccess() {
        startRequests();
    }
    @Override public void onFailure() {}

    /**
        Utility callback
     */
    public interface Callback {
        void requestQueueEmpty(MultiPartRequestDialog dialog);
    }

    List<Callback> callbacks = new ArrayList<>();
    public void addCallback(Callback callback) {
        callbacks.add(callback);
    }

    public void removeCallback(Callback callback) {
        callbacks.remove(callback);
    }
}
