package com.example.appka.mtaaaplikacia;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SettingsActivity extends AppCompatActivity {

    private String loggedUser = "";
    private String loggedName = "";
    private String url = "https://api.backendless.com/3EDD8AD3-62EC-D330-FF4D-4855F4C77C00/96B7A309-C50A-45F9-A833-27DBC102ED1E/data/Users/";
    private EditText mOldPasswordView;
    private EditText mNewPasswordView;
    private EditText mConfPasswordView;
  //  private View mLoginFormView;
  //  private View mProgressView;
    private Button passwordSave;
    private Button accDelete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        loggedUser = getIntent().getStringExtra("userID");
        loggedName = getIntent().getStringExtra("login");
        TextView settingsTitle = (TextView)findViewById(R.id.accSettingsToobarTV);
        settingsTitle.setText("Account settings");
        System.out.println(loggedName);
        System.out.println(loggedUser);
        System.out.println("TU");
        passwordSave = (Button) findViewById(R.id.settingsSaveButton);
        passwordSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOldPasswordView = (EditText) findViewById(R.id.settingsOldPassET);
                mNewPasswordView = (EditText) findViewById(R.id.settingsNewPassET);
                mConfPasswordView = (EditText) findViewById(R.id.settingsConfPassET);
                //  if(mNewPasswordView == mConfPasswordView)
                System.out.println(mOldPasswordView);
                System.out.println(mNewPasswordView);
                System.out.println(mConfPasswordView);
                attemptLogin();
                //  else
                //  System.out.println("niesu rovnake hesla");
            }
        });

        accDelete = (Button)findViewById(R.id.settingsDeleteButton);
        accDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
                // set dialog message
                alertDialogBuilder
                        .setTitle("Delete Account?")
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        SettingsActivity.this.finish();
                                        Intent loginIntent = new Intent(SettingsActivity.this, LoginActivity.class);
                                        deleteAccount();
                                        startActivity(loginIntent);
                                    }
                                })
                        .setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
            }
        });

    }


    private void deleteAccount(){

            JSONObject obj = new JSONObject();
            String newUrl = url + loggedUser;
            DeleteJSONObjectRequest deleteRequest = new DeleteJSONObjectRequest(Request.Method.DELETE, newUrl, obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println(response);
                   // EditActivity.this.finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println(error);
                }
            });
            MTAAApplication.getInstance().addToRequestQueue(deleteRequest, "deleteitem");
        }


    private void startUpdate(String newPass) {
        System.out.println("noooooooooooooooooooooo ide");

        View focusView = null;
        boolean result;

       // mLoginView.setError(null);
        //mEmailView.setError(null);
        mNewPasswordView.setError(null);
        mConfPasswordView.setError(null);


        String password = mNewPasswordView.getText().toString();
        String cpassword = mConfPasswordView.getText().toString();

        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();
        String newUrl = url + loggedUser;



        try {
            obj.put("password",password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(obj);
        arr.put(obj);
        CustomJSONOArrayRequest updateRequest = new CustomJSONOArrayRequest(Request.Method.PUT, newUrl, arr, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError) {
                    showError(0);
                } else {
                    showError(error.networkResponse.statusCode);
                }
            }
        });
        MTAAApplication.getInstance().addToRequestQueue(updateRequest, "update");

    }

    private void showError(int err) {
        ErrDialog errFragment = ErrDialog.newInstance(err);
        errFragment.show(getFragmentManager(), "Error");
    }

    private void attemptLogin() {
        final String url = "https://api.backendless.com/3EDD8AD3-62EC-D330-FF4D-4855F4C77C00/96B7A309-C50A-45F9-A833-27DBC102ED1E/data/Users/login";
        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();
        String oldpassword = mOldPasswordView.getText().toString();
        String password = mNewPasswordView.getText().toString();
        String cpassword = mConfPasswordView.getText().toString();
        final String newPassw = mNewPasswordView.getText().toString();
        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mOldPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mOldPasswordView;
            cancel = true;
        }
        if (TextUtils.isEmpty(password)) {
            mNewPasswordView.setError(getString(R.string.error_field_required));
            focusView = mNewPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mNewPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mNewPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(cpassword)) {
            mConfPasswordView.setError(getString(R.string.error_field_required));
            focusView = mConfPasswordView;
            cancel = true;
        } else if (!isPasswordValid(cpassword)) {
            mConfPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mConfPasswordView;
            cancel = true;
        } else if (!passMatch(password, cpassword)) {
            mConfPasswordView.setError(getString(R.string.error_password_match));
            focusView = mConfPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //  showProgress(true);
            try {
                obj.put("login", loggedName);
                obj.put("password", oldpassword);
               // System.out.println(loggedUser);
                //System.out.println(password);
            } catch (JSONException e) {
                e.printStackTrace();
            }

          //  System.out.println(obj);
            arr.put(obj);
            CustomJSONOArrayRequest loginRequest = new CustomJSONOArrayRequest(Request.Method.POST, url, arr, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                   // System.out.println(response.toString());
                    JSONParser jsonParser = new JSONParser();

                    startUpdate(newPassw);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println(error.toString());
                    if (error instanceof NoConnectionError) {
                        showError(0);
                    } else {
                        showError(error.networkResponse.statusCode);
                    }
                }
            });
            MTAAApplication.getInstance().addToRequestQueue(loginRequest, "login");
        }
    }


    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 7;
    }

    private boolean passMatch(String pass1, String pass2) {
        return pass1.equals(pass2);
    }
}

