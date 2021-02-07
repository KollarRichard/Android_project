package com.example.appka.mtaaaplikacia;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AddActivity extends AppCompatActivity {

    private ImageView mImageView;
    private EditText mNameET;
    private EditText mAddressET;
    private TextView mImageTV;
    private Button doneButton;
    private Spinner mSpinner;
    private String imgUrl = "";
    private final Context context = this;
    private String loggedUser;
    private String loggedName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        loggedUser = getIntent().getStringExtra("userID");
        loggedName = getIntent().getStringExtra("login");
        mNameET = (EditText)findViewById(R.id.addNameET);
        mNameET.setHorizontallyScrolling(true);
        mNameET.setScrollbarFadingEnabled(true);
        mNameET.setMovementMethod(new ScrollingMovementMethod());
        mAddressET = (EditText)findViewById(R.id.addAddressET);
        mAddressET.setHorizontallyScrolling(true);
        mAddressET.setScrollbarFadingEnabled(true);
        mAddressET.setMovementMethod(new ScrollingMovementMethod());
        mImageTV = (TextView)findViewById(R.id.addImageDescriptionTV);
        TextView mTitle = (TextView)findViewById(R.id.addToobarTV);
        mTitle.setText("Add new");
        doneButton = (Button)findViewById(R.id.addConfirmButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptAdding();
            }
        });
        mImageView = (ImageView)findViewById(R.id.addImageView);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(context);
                final View imgView = li.inflate(R.layout.imgdialog_layout, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(imgView);

                final EditText userInput = (EditText) imgView.findViewById(R.id.addImageUrlET);

                // set dialog message
                alertDialogBuilder
                        .setTitle("Please enter image URL")
                        .setPositiveButton("Done",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        imgUrl = userInput.getText().toString();
                                        mImageTV.setVisibility(View.GONE);
                                        startShowingImage();
                                    }
                                })
                        .setNegativeButton("Cancel",
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AddActivity.this.finish();
    }

    private void attemptAdding() {
        mSpinner = (Spinner)findViewById(R.id.spinner);
        boolean cancel = false;
        final String url = "https://eu-api.backendless.com/39E3DA48-2F5E-4EC5-FF1C-15EC0E508400/7A5F072E-9D48-4C90-978F-DD29516E5562/data/restaurants";
        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();
        int cat = 1;
        String name = mNameET.getText().toString();
        String address = mAddressET.getText().toString();
        View focusView = null;

        if (TextUtils.isEmpty(name)) {
            mNameET.setError(getString(R.string.error_field_required));
            focusView = mNameET;
            cancel = true;
        }

        if (TextUtils.isEmpty(address)) {
            mAddressET.setError(getString(R.string.error_field_required));
            focusView = mAddressET;
            cancel = true;
        }
        cat = mSpinner.getSelectedItemPosition();
        try {
            obj.put("rst_name", name);
            obj.put("rst_address", address);
            obj.put("rst_img_url", imgUrl);
            obj.put("rst_cat", cat);
            obj.put("ownerId", loggedUser);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            arr.put(obj);
            CustomJSONOArrayRequest addRequest = new CustomJSONOArrayRequest(Request.Method.POST, url, arr, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println(response.toString());
                    Intent backIntent = new Intent(AddActivity.this, HomeActivity.class);
                    backIntent.putExtra("userID",loggedUser);
                    backIntent.putExtra("login",loggedName);
                    startActivity(backIntent);
                    AddActivity.this.finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println(error.networkResponse.statusCode);
                    if (error instanceof NoConnectionError) {
                        showError(0);
                    } else {
                        showError(error.networkResponse.statusCode);
                    }
                }
            });
            MTAAApplication.getInstance().addToRequestQueue(addRequest, "addnew");
        }
    }

    private void showError(int err) {
        ErrDialog errFragment = ErrDialog.newInstance(err);
        errFragment.show(getFragmentManager(), "Error");
    }

    private void startShowingImage() {
        SimpleImageLoader downloadImageTask = new SimpleImageLoader(mImageView);
        if (imgUrl != null) {
            downloadImageTask.execute(imgUrl);
        } else {
            downloadImageTask = null;
        }
    }

}
