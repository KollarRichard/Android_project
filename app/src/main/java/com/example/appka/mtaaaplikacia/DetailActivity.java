package com.example.appka.mtaaaplikacia;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

public class DetailActivity extends AppCompatActivity {
    private String itemID;
    private String name;
    private String address;
    private String category;
    private String imgUrl;
    private String url = "https://api.backendless.com/3EDD8AD3-62EC-D330-FF4D-4855F4C77C00/96B7A309-C50A-45F9-A833-27DBC102ED1E/data/restaurants/";
    private TextView mNameView;
    private TextView mAddressView;
    private TextView mCategoryView;
    private ImageView mImageView;
    private Button mStartEdit;
    private String loggedUser;
    private String itemOwnerID;
    private TextView mOwnerView;
    private String[] owner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        TextView mTitle = (TextView)findViewById(R.id.detailToolbarTV);
        mTitle.setText("Details");
        itemID = getIntent().getStringExtra("itemID");
        loggedUser = getIntent().getStringExtra("userID");
      //  System.out.println(loggedUser);
        getDetailedData(itemID);
        mStartEdit = (Button)findViewById(R.id.startEditButton);
        mStartEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(loggedUser)){
                    if(idMatch(itemOwnerID,loggedUser)){
                        Intent editIntent = new Intent(DetailActivity.this, EditActivity.class);
                        editIntent.putExtra("itemID", itemID);
                        editIntent.putExtra("userID", loggedUser);
               // editIntent.putExtra("user",loggedUser);
                    startActivity(editIntent);
                DetailActivity.this.finish();}
                    else{
                        showErr2();
                    }
            }
            else {
                showErr();
                 }
            }
        });
    }



    private boolean idMatch(String id1, String id2) {
        return id1.equals(id2);
    }

    private void showErr(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DetailActivity.this);
        // set dialog message
        alertDialogBuilder
                .setTitle("You have to be logged in")

                .setNeutralButton("Ok",
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

    private void showErr2(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DetailActivity.this);
        // set dialog message
        alertDialogBuilder
                .setTitle("You have to be the owner to edit")

                .setNeutralButton("Ok",
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

    private void getDetailedData(String s) {
        String newUrl;
        JSONObject obj = new JSONObject();
        newUrl = url + s;
        CustomJSONObjectRequest detailRequest = new CustomJSONObjectRequest(Request.Method.GET, newUrl, obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);
                JSONParser jsonParser = new JSONParser();
                name = jsonParser.getSingleString(response, "rst_name");
                System.out.println(name);
                address = jsonParser.getSingleString(response, "rst_address");
                category = jsonParser.getSingleString(response, "rst_cat");
                imgUrl = jsonParser.getSingleString(response, "rst_img_url");
                itemOwnerID = jsonParser.getSingleString(response, "ownerId");
                getOwner(itemOwnerID);
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

    private void getOwner(String id) {
        JSONObject obj = new JSONObject();
        String url = "https://api.backendless.com/v1/data/Users?where=objectId%20%3D%20%27" + id + "%27";
        CustomJSONObjectRequest ownerRequest = new CustomJSONObjectRequest(Request.Method.GET, url, obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);
                JSONParser jsonParser = new JSONParser();
                owner = jsonParser.getStringFromJson(response, "login");
                setTextFields();
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
        MTAAApplication.getInstance().addToRequestQueue(ownerRequest, "getowner");
    }

    private void showError(int err) {
        ErrDialog errFragment = ErrDialog.newInstance(err);
        errFragment.show(getFragmentManager(), "Error");
    }

    private void setTextFields() {
        mNameView = (TextView)findViewById(R.id.detailnameTV);
        mAddressView = (TextView)findViewById(R.id.deatiladdressTV);
        mCategoryView = (TextView)findViewById(R.id.detailcategoryTV);
        mImageView = (ImageView)findViewById(R.id.detailIV);
        mOwnerView = (TextView)findViewById(R.id.detailOwnerTV);
        mOwnerView.setText("");
        mOwnerView.setText(owner[0]);
        mNameView.setText(name);
        mAddressView.setText(address);
        switch (Integer.parseInt(category)){
            case 0:  mCategoryView.setText("Coffee shop"); break;
            case 1:  mCategoryView.setText("Steakhouse"); break;
            case 2:  mCategoryView.setText("Pizzeria"); break;
            case 3:  mCategoryView.setText("Pub"); break;
            case 4:  mCategoryView.setText("Opiovy doupe"); break;
            case 5:  mCategoryView.setText("Restaurant"); break;
            default: mCategoryView.setText("Kappa");
        }
        SimpleImageLoader imageLoaderTask = new SimpleImageLoader(mImageView);
        imageLoaderTask.execute(imgUrl);
    }
}
