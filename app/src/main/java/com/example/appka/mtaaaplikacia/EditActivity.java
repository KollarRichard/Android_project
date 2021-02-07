package com.example.appka.mtaaaplikacia;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

public class EditActivity extends AppCompatActivity {
    private String itemID = "";
    private String userID = "";
    private String name;
    private String address;
    private String category;
    private String imgUrl;
    private String nUrl;
    private String url = "https://eu-api.backendless.com/39E3DA48-2F5E-4EC5-FF1C-15EC0E508400/7A5F072E-9D48-4C90-978F-DD29516E5562/data/restaurants";
    private EditText mNameView;
    private EditText mAddressView;
    private Spinner mCategorySpin;
    private ImageView mImageView;
    private Button fireUpdate;
    private Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        TextView mTitle = (TextView)findViewById(R.id.editToolbarTV);
        mTitle.setText("Edit");
        itemID = getIntent().getStringExtra("itemID");
        userID = getIntent().getStringExtra("userID");
        mNameView = (EditText)findViewById(R.id.editNameET);
        mAddressView = (EditText)findViewById(R.id.editAddressET);
        mCategorySpin = (Spinner)findViewById(R.id.editSpinner);
        mImageView = (ImageView)findViewById(R.id.editImageView);
        mNameView.setHorizontallyScrolling(true);
        mNameView.setScrollbarFadingEnabled(true);
        mNameView.setMovementMethod(new ScrollingMovementMethod());
        mAddressView.setHorizontallyScrolling(true);
        mAddressView.setScrollbarFadingEnabled(true);
        mAddressView.setMovementMethod(new ScrollingMovementMethod());
        loadEditableData();
        mImageView = (ImageView)findViewById(R.id.editImageView);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(EditActivity.this);
                final View imgView = li.inflate(R.layout.imgdialog_layout, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EditActivity.this);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(imgView);

                final EditText userInput = (EditText) imgView.findViewById(R.id.addImageUrlET);

                // set dialog message
                alertDialogBuilder
                        .setTitle("Please enter image URL")
                        .setPositiveButton("Done",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        nUrl = userInput.getText().toString();
                                        //mImageTV.setVisibility(View.GONE);
                                        startShowingImage(nUrl);
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
        fireUpdate = (Button)findViewById(R.id.editConfirmButton);
        fireUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
            }
        });

        deleteButton = (Button)findViewById(R.id.editDeleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EditActivity.this);
                // set dialog message
                alertDialogBuilder
                        .setTitle("Are you sure to delete this item?")
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        deleteItem();
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


    private void loadEditableData () {
        String newUrl;
        JSONArray obj = new JSONArray();
        newUrl = url + itemID;
        CustomJSONOArrayRequest detailRequest = new CustomJSONOArrayRequest(Request.Method.GET, newUrl, obj, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray r) {
                //System.out.println(response);
                JSONObject response = null;
                try {
                    response = r.getJSONObject(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONParser jsonParser = new JSONParser();
                name = jsonParser.getSingleString(response, "rst_name");
                address = jsonParser.getSingleString(response, "rst_address");
                category = jsonParser.getSingleString(response, "rst_cat");
                imgUrl = jsonParser.getSingleString(response, "rst_img_url");
                setDataFields();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
                if (error instanceof NoConnectionError) {
                    showError(0);
                } else {
                    showError(error.networkResponse.statusCode);
                }
            }
        });
        MTAAApplication.getInstance().addToRequestQueue(detailRequest, "itemdetail");
    }

    private void showError(int err) {
        ErrDialog errFragment = ErrDialog.newInstance(err);
        errFragment.show(getFragmentManager(), "Error");
    }

    private void setDataFields() {
        mNameView.setText(name);
        mNameView.setSelection(mNameView.getText().length());
        mAddressView.setText(address);
        mAddressView.setSelection(mAddressView.getText().length());
        mCategorySpin.setSelection(Integer.parseInt(category));
        SimpleImageLoader imageLoaderTask = new SimpleImageLoader(mImageView);
        imageLoaderTask.execute(imgUrl);
    }

    private void updateData() {
        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();
        String newUrl = url + itemID;
        String nName = mNameView.getText().toString();
        String nAddress = mAddressView.getText().toString();
        int nCat = mCategorySpin.getSelectedItemPosition();
        try {
            obj.put("rst_name", nName);
            obj.put("rst_address", nAddress);
            obj.put("rst_cat", nCat);
            obj.put("rst_img_url", nUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        arr.put(obj);
        CustomJSONOArrayRequest updateRequest = new CustomJSONOArrayRequest(Request.Method.PUT, newUrl, arr, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray r) {
                JSONObject response = null;
                try {
                    response = r.getJSONObject(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent detailIntent = new Intent(EditActivity.this, DetailActivity.class);
                detailIntent.putExtra("itemID", itemID);
                detailIntent.putExtra("userID", userID);
                startActivity(detailIntent);
                EditActivity.this.finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showError(error.networkResponse.statusCode);
            }
        });
        MTAAApplication.getInstance().addToRequestQueue(updateRequest, "update");
    }

    private void startShowingImage(String url) {
        SimpleImageLoader downloadImageTask = new SimpleImageLoader(mImageView);
        if (url != null) {
            downloadImageTask.execute(url);
        } else {
            downloadImageTask = null;
        }
    }

    private void deleteItem() {
        JSONObject obj = new JSONObject();
        String newUrl = url + itemID;
        DeleteJSONObjectRequest deleteRequest = new DeleteJSONObjectRequest(Request.Method.DELETE, newUrl, obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);
                EditActivity.this.finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
                if (error instanceof NoConnectionError) {
                    showError(0);
                } else {
                    showError(error.networkResponse.statusCode);
                }
            }
        });
        MTAAApplication.getInstance().addToRequestQueue(deleteRequest, "deleteitem");
    }

}
