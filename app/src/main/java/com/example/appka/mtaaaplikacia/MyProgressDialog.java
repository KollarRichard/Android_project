package com.example.appka.mtaaaplikacia;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

public class MyProgressDialog extends ProgressDialog {

    String msg;
    public MyProgressDialog(Context context, String message) {
        super(context);
        this.msg = message;
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProgressDialog.Builder builder = new ProgressDialog.Builder(getContext());
        builder.setMessage(msg)
                .setCancelable(false)
                .setTitle("Please wait");

        // Create the AlertDialog object and return it
    }

}
