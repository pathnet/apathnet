package com.pathnet.interfaces;

import android.app.ProgressDialog;

public interface IDialogControl {

    void hideWaitDialog();

    ProgressDialog showWaitDialog();

    ProgressDialog showWaitDialog(int resid);

    ProgressDialog showWaitDialog(String text);
}
