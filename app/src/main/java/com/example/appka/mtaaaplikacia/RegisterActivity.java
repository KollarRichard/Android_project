package com.example.appka.mtaaaplikacia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private EditText mLoginView;
    private EditText mPasswordView;
    private EditText mEmailView;
    private EditText mCPasswordView;
    private final String url = "https://eu-api.backendless.com/39E3DA48-2F5E-4EC5-FF1C-15EC0E508400/7A5F072E-9D48-4C90-978F-DD29516E5562/users/register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mLoginView = (EditText) findViewById(R.id.registerLoginET);
        mEmailView = (EditText) findViewById(R.id.registerEmailET);
        mPasswordView = (EditText) findViewById(R.id.registerPWET);
        mCPasswordView = (EditText) findViewById(R.id.registerCPWET);

        Button mRegisterButton = (Button) findViewById(R.id.registerAcceptButton);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });

    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPassValid(String pass) {
        return (pass.length() > 7);
    }

    private boolean passMatch(String pass1, String pass2) {
        return pass1.equals(pass2);
    }

    private void attemptRegister() {
        View focusView = null;
        boolean result;

        mLoginView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mCPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String login = mLoginView.getText().toString();
        String password = mPasswordView.getText().toString();
        String cpassword = mCPasswordView.getText().toString();
        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();

        try {
            obj.put("login", login);
            obj.put("email", email);
            obj.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        boolean cancel = false;

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (TextUtils.isEmpty(login)) {
            mLoginView.setError(getString(R.string.error_field_required));
            focusView = mLoginView;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPassValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(cpassword)) {
            mCPasswordView.setError(getString(R.string.error_field_required));
            focusView = mCPasswordView;
            cancel = true;
        } else if (!isPassValid(cpassword)) {
            mCPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mCPasswordView;
            cancel = true;
        } else if (!passMatch(password, cpassword)) {
            mCPasswordView.setError(getString(R.string.error_password_match));
            focusView = mCPasswordView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {

            CustomJSONObjectRequest registerRequest = new CustomJSONObjectRequest(Request.Method.POST, url, obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    JSONParser jsonParser = new JSONParser();
                    Intent homeIntent = new Intent(RegisterActivity.this, HomeActivity.class);
                    homeIntent.putExtra("login", jsonParser.getSingleString(response, "login"));
                    homeIntent.putExtra("userID", jsonParser.getSingleString(response, "objectId"));
                    startActivity(homeIntent);
                    RegisterActivity.this.finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println(error.networkResponse.statusCode);
                    showError(error.networkResponse.statusCode);
                }
            });
            MTAAApplication.getInstance().addToRequestQueue(registerRequest, "register");
        }
    }
    private void showError(int err) {
        ErrDialog errFragment = ErrDialog.newInstance(err);
        errFragment.show(getFragmentManager(), "Error");
    }

}