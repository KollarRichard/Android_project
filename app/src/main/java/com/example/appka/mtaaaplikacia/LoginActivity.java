package com.example.appka.mtaaaplikacia;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

import java.util.ArrayList;
import java.util.List;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private String logins[];
    private String users[];
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Logging in");
        progressDialog.setCancelable(false);
        getLastLogins();
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        TextView mTitle = (TextView)findViewById(R.id.loginToolbarTV);
        mTitle.setText("Log in");
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //if (view.getId() == R.id.email_sign_in_button) {
                attemptLogin();
            }


        });

        Button myRegisterButton = (Button) findViewById(R.id.register_button);
        myRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        Button mContinue = (Button) findViewById(R.id.button);
        mContinue.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.button) {
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                }

            }
        });

    }

    private void showError(int err) {
        ErrDialog errFragment = ErrDialog.newInstance(err);
        errFragment.show(getFragmentManager(), "Error");
    }

    @Override
    public void onBackPressed() {
        System.out.println("Stlacil som back haha!!");
        System.exit(0);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        final String url = "https://api.backendless.com/v1/users/login";
        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String login = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(login)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            progressDialog.show();
            try {
                obj.put("login", login);
                obj.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            System.out.println(obj);
            arr.put(obj);
            CustomJSONOArrayRequest loginRequest = new CustomJSONOArrayRequest(Request.Method.POST, url, arr, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray r) {
                    JSONObject response = null;
                    try {
                        response = r.getJSONObject(0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    System.out.println(response.toString());
                    JSONParser jsonParser = new JSONParser();
                    progressDialog.dismiss();
                    startHomeScreen(jsonParser.getSingleString(response, "login"), jsonParser.getSingleString(response, "objectId"));
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
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


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    private void startHomeScreen(String login, String userid) {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.putExtra("login", login);
        intent.putExtra("userID", userid);
        if (users.length > 0 ) {
            for (int i = 0; i < users.length; i++) {
                if (userid.equals(users[i])) {
                    MyProperties.getInstance().lastLogin = logins[i];
                }
            }
        }
        startActivity(intent);
        LoginActivity.this.finish();
    }

    private void getLastLogins() {
        JSONArray obj = new JSONArray();
        final String url = "https://api.backendless.com/v1/data/Users?props=lastLogin%2CobjectId";
        CustomJSONOArrayRequest llRequest = new CustomJSONOArrayRequest(Request.Method.GET, url, obj, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray r) {
                JSONObject response = null;
                try {
                    response = r.getJSONObject(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONParser jsonParser = new JSONParser();
                logins = jsonParser.getStringFromJson(response, "lastLogin");
                users = jsonParser.getStringFromJson(response, "objectId");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MTAAApplication.getInstance().addToRequestQueue(llRequest, "logins");
    }
}
