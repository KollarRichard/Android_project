package com.example.appka.mtaaaplikacia;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

public class ErrDialog extends DialogFragment {

    public static ErrDialog newInstance(int errcode) {
        ErrDialog frag = new ErrDialog();
        Bundle args = new Bundle();
        args.putInt("errcode", errcode);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        int error = getArguments().getInt("errcode");
        String errmsg = "Error " + Integer.toString(error) + ": ";
        switch (error) {
            case   0 : errmsg = "No internet connection"; break;
            case 400 : errmsg += "Bad Request"; break;
            case 401 : errmsg += "Authentification Failure"; break;
            case 403 : errmsg += "Forbidden"; break;
            case 404 : errmsg += "Not Found"; break;
            case 408 : errmsg += "Request Timeout"; break;
            case 409 : errmsg += "This login is unavailable"; break;
            case 429 : errmsg += "Too Many Requests"; break;
            case 500 : errmsg += "Already exists"; break;
            case 502 : errmsg += "Bad Gateway"; break;
            case 503 : errmsg += "Service Unavilable"; break;
            default : errmsg = getString(R.string.general_error); break;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(errmsg)
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                })
                .setTitle("Error");
            // Create the AlertDialog object and return it
        return builder.create();
    }
}

