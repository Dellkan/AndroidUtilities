package com.dellkan.sample.viewmodels;

import com.dellkan.dialogs.RequestDialog;
import com.dellkan.dialogs.RequestDialogCallback;
import com.dellkan.dialogs.SinglePartRequestDialog;
import com.dellkan.robobinding.helpers.model.PresentationModelWrapper;
import com.dellkan.robobinding.helpers.modelgen.PresentationModel;

import java.io.Serializable;

@PresentationModel
public class Main extends PresentationModelWrapper implements Serializable {
    String url;

    public void send() {
        SinglePartRequestDialog.newInstance().get(url, null, new RequestDialogCallback() {
            @Override
            public void onFailure(RequestDialog dialog) {
                super.onFailure(dialog);
                dialog.setFinished(getResponse());
            }

            @Override
            public void onSuccess(RequestDialog dialog) {
                super.onSuccess(dialog);
                dialog.setFinished(getResponse());
            }
        });
    }
}
