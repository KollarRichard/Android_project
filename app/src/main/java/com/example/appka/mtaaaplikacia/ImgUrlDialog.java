package com.example.appka.mtaaaplikacia;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

public class ImgUrlDialog extends DialogFragment{
    public String url = "";
    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, String s);
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
    public String returnUrl() {
        return url;
    }

   // @TargetApi(Build.VERSION_CODES.M)
    @Override

    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        //LayoutInflater linf = LayoutInflater.from();
        //final View inflator = linf.inflate(R.layout.imgdialog_layout, null);
        // Use the Builder class for convenient dialog construction
        //final EditText mEditText = (EditText)inflator.findViewById(R.id.addImageUrlET);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder .setView(R.layout.imgdialog_layout)
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //url = mEditText.getText().toString();

                        mListener.onDialogPositiveClick(ImgUrlDialog.this, url);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setTitle("Kappa");
            // Create the AlertDialog object and return it
        return builder.create();
    }
}

