package com.dellkan.dialogs;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.dellkan.ContextFragment;
import com.dellkan.net.R;
import com.dellkan.net.Request;

import java.util.Map;

public final class SinglePartRequestDialog implements RequestDialog {
    private AlertDialog mDialog;

    public static SinglePartRequestDialog newInstance() {
        ProgressDialog dialog = new ProgressDialog(ContextFragment.getStaticContext());
        dialog.setIndeterminate(true);
        dialog.setMessage(dialog.getContext().getString(R.string.loading));
        dialog.setCancelable(false);

        return new SinglePartRequestDialog(dialog);
    }

    private SinglePartRequestDialog(AlertDialog dialog) {
        this.mDialog = dialog;
    }

    public void get(String url, @Nullable Map<String, Object> params, RequestDialogCallback callback) {
        get(url, null, params, callback);
    }

    public void get(String url, @Nullable Map<String, String> headers, @Nullable Map<String, Object> params, RequestDialogCallback callback) {
        callback.setDialog(this);

        Request request = new Request(url, Request.Method.GET);
        request.setParameters(params);
        request.setCallback(callback);
        request.start();

        this.mDialog.show();
    }

    public void post(String url, @Nullable Map<String, Object> params, RequestDialogCallback callback) {
        post(url, null, params, callback);
    }

    public void post(String url, @Nullable Map<String, String> headers, @Nullable Map<String, Object> params, RequestDialogCallback callback) {
        callback.setDialog(this);

        Request request = new Request(url, Request.Method.POST);
        request.setParameters(params);
        request.setCallback(callback);
        request.start();

        this.mDialog.show();
    }

    public void setFinished(@StringRes int message) {
        setFinished(ContextFragment.getStaticContext().getString(message));
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
    @Override public void onSuccess() {}
    @Override public void onFailure() {}
}
