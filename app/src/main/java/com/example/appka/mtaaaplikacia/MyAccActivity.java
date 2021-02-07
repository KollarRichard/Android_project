package com.example.appka.mtaaaplikacia;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

public class MyAccActivity extends AppCompatActivity {
    private String url2 = "https://api.backendless.com/3EDD8AD3-62EC-D330-FF4D-4855F4C77C00/96B7A309-C50A-45F9-A833-27DBC102ED1E/data/Users/";
    private final String url = "https://api.backendless.com/3EDD8AD3-62EC-D330-FF4D-4855F4C77C00/96B7A309-C50A-45F9-A833-27DBC102ED1E/data/restaurants?where=ownerId%3D";
    private ListView mListView;
    private String loggedUser;
    private String loggedName;
    private String names[];
    private String addresses[];
    private int nOfObjects;
    private String ids[];
    private String urls[];
    private String categories[];
    private String nUrl;
    ProgressDialog progressDialog;
  //  private String test = "%27A4CA0145-30A3-184E-FFF8-A510E0DB2E00%27";
    private String newUrl;
    private Button logout;
    private Button settings;
    private TextView last;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_acc);
        progressDialog = new ProgressDialog(MyAccActivity.this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Updating");
        progressDialog.setCancelable(false);
        progressDialog.show();
        loggedUser = getIntent().getStringExtra("userID");
        loggedName = getIntent().getStringExtra("login");
        //System.out.println(loggedUser);
        TextView myAcc = (TextView)findViewById(R.id.myAccToobarTV);
        myAcc.setText("My account");
        logout = (Button)findViewById(R.id.myAccLogoutButton);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MyAccActivity.this);
                // set dialog message
                alertDialogBuilder
                        .setTitle("Log out?")
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        MyAccActivity.this.finish();
                                        Intent loginIntent = new Intent(MyAccActivity.this, LoginActivity.class);
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

        settings = (Button)findViewById(R.id.myAccSettingsButton);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(MyAccActivity.this,SettingsActivity.class);
                settingsIntent.putExtra("userID",loggedUser);
                settingsIntent.putExtra("login",loggedName);
                startActivity(settingsIntent);
            }
        });

        startUpdatingData();
        lastLogin();
    }

    @Override
    public void onBackPressed() {
        MyAccActivity.this.finish();
    }

    private void showError(int err) {
        ErrDialog errFragment = ErrDialog.newInstance(err);
        errFragment.show(getFragmentManager(), "Error");
    }


    private void lastLogin() {
        last = (TextView) findViewById(R.id.myAccLastLogin);
        Long datum;
        String date = "";
        if (MyProperties.getInstance().lastLogin != null) {
            datum = Long.parseLong(MyProperties.getInstance().lastLogin);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm"); // the format of your date
            date = sdf.format(datum);
        }
        last.setText(date);
    }



    private void startUpdatingData() {
        progressDialog.show();
        newUrl = url + "%27" + loggedUser + "%27";
        JSONArray obj = new JSONArray();
        CustomJSONOArrayRequest dataRequest = new CustomJSONOArrayRequest(Request.Method.GET, newUrl, obj, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray r) {
                //System.out.println(response.toString());
                JSONObject response = null;
                try {
                    response = r.getJSONObject(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONParser jsonParser = new JSONParser();

                names = jsonParser.getStringFromJson(response, "rst_name");
                addresses = jsonParser.getStringFromJson(response, "rst_address");
                ids = jsonParser.getStringFromJson(response, "objectId");
                urls = jsonParser.getStringFromJson(response, "rst_img_url");
                categories = jsonParser.getStringFromJson(response, "rst_cat");
                if (names.length < nOfObjects) {
                    updateData();
                } else {
                    startList();
                }
                //System.out.println(nOfObjects);
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
        MTAAApplication.getInstance().addToRequestQueue(dataRequest, "datarequest");
    }

    private void updateData() {
        final JSONArray obj = new JSONArray();
        CustomJSONOArrayRequest dataRequest = new CustomJSONOArrayRequest(Request.Method.GET, nUrl, obj, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray r) {
                //System.out.println(response.toString());
                JSONObject response = null;
                try {
                    response = r.getJSONObject(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONParser jsonParser = new JSONParser();

                names = arrayConcat(names, jsonParser.getStringFromJson(response, "rst_name"));
                addresses = arrayConcat(addresses, jsonParser.getStringFromJson(response, "rst_address"));
                ids = arrayConcat(ids, jsonParser.getStringFromJson(response, "objectId"));
                urls = arrayConcat(urls, jsonParser.getStringFromJson(response, "rst_img_url"));
                categories = arrayConcat(categories, jsonParser.getStringFromJson(response, "rst_cat"));
                if (names.length < nOfObjects) {
                    updateData();
                } else {
                    startList();
                }
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
        MTAAApplication.getInstance().addToRequestQueue(dataRequest, "datarequest");
    }

    private String[] arrayConcat(String[] a, String[] b) {
        int aLen = a.length;
        int bLen = b.length;
        String[] c =  new String[aLen+bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }

    private void startList() {
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_row, R.id.firstLine, names);
        TwoLineArrayAdapter adapter = new TwoLineArrayAdapter(this, R.layout.list_row, names, addresses, categories);
        mListView = (ListView)findViewById(R.id.restaurantList);
        mListView.setAdapter(adapter);
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detailIntent = new Intent(MyAccActivity.this, DetailActivity.class);
                System.out.println(position);
                detailIntent.putExtra("itemID", ids[position]);
                detailIntent.putExtra("userID",loggedUser);
                startActivity(detailIntent);
            }
        });
    }
}
