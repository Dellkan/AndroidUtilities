package com.dellkan;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dellkan.net.R;

import java.lang.ref.WeakReference;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 */
public class ContextFragment extends Fragment {
    static WeakReference<Context> mReference = new WeakReference<>(null);
    static boolean attached = false;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mReference = new WeakReference<Context>(context);
        attached = true;
    }

    public static Context getStaticContext() {
        if (!attached) {
            throw new RuntimeException("ContextFragment must be attached to an activity");
        }
        return mReference.get();
    }
}
