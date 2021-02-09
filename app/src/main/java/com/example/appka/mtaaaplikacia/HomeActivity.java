package com.example.appka.mtaaaplikacia;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

public class HomeActivity extends AppCompatActivity {

    private final String url = "https://eu-api.backendless.com/39E3DA48-2F5E-4EC5-FF1C-15EC0E508400/7A5F072E-9D48-4C90-978F-DD29516E5562/data/restaurants";
    private ListView mListView;
    private String names[];
    private String addresses[];
    private int nOfObjects;
    private String ids[];
    private String urls[];
    private String categories[];
    private String nUrl;
    private ImageView refreshButton;
    private ImageView settingsButton;
    private String loggedUser = "";
    private EditText searchET;
    ProgressDialog progressDialog;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        final TextView mTitle = (TextView)findViewById(R.id.homeTV);
        context = getApplicationContext();
        loggedUser = getIntent().getStringExtra("userID");
        progressDialog = new ProgressDialog(HomeActivity.this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Updating");
        progressDialog.setCancelable(false);
        progressDialog.show();
        mTitle.setText(TextUtils.isEmpty(getIntent().getStringExtra("login")) ? "Home" : (getIntent().getStringExtra("login")));
        searchET = (EditText)findViewById(R.id.searchET);
        searchET.setHorizontallyScrolling(true);
        searchET.setScrollbarFadingEnabled(true);
        searchET.setMovementMethod(new ScrollingMovementMethod());
        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(searchET.getText())) {
                    startList();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Button searchButton = (Button)findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFor(searchET.getText().toString());
            }
        });
        Button mAddButton = (Button) findViewById(R.id.addButton);
       // mAddButton.setClickable(true);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(getIntent().getStringExtra("login"))){
                Intent addIntent = new Intent(HomeActivity.this, AddActivity.class);
                addIntent.putExtra("userID", loggedUser);
                addIntent.putExtra("login",getIntent().getStringExtra("login"));
                    startActivity(addIntent);}
                else{
                    showErr();
                }
            }
        });
        settingsButton = (ImageView)findViewById(R.id.homeSettingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(getIntent().getStringExtra("login"))){
                    Intent accountIntent = new Intent(HomeActivity.this,MyAccActivity.class);
                    accountIntent.putExtra("userID",loggedUser);
                    accountIntent.putExtra("login", getIntent().getStringExtra("login"));
                    startActivity(accountIntent);
                } else{
                    showErr();
                }
            }
        });

        refreshButton = (ImageView)findViewById(R.id.homeRefreshButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nOfObjects = 0;
                nUrl = null;
                names = null;
                addresses = null;
                ids = null;
                urls = null;
                categories = null;
                startUpdatingData();
            }
        });
        startUpdatingData();
    }

    private void searchFor(String what) {
        progressDialog.setMessage("Searching");
        progressDialog.show();
        LinkedList<String> results1 = new LinkedList<String>();
        LinkedList<String> results2 = new LinkedList<String>();
        LinkedList<String> results3 = new LinkedList<String>();
        for (int i = 0; i < names.length; i++) {
            if (names[i].contains(what)) {
                results1.add(names[i]);
                results2.add(addresses[i]);
                results3.add(categories[i]);
            }
        }
        String[] data1 = new String[results1.size()];
        String[] data2 = new String[results2.size()];
        String[] data3 = new String[results3.size()];
        for (int i = 0; i < results1.size(); i++) {
            data1[i] = results1.get(i);
            data2[i] = results2.get(i);
            data3[i] = results3.get(i);
        }
        startFilteredList(data1, data2, data3);
    }

    private void showError(int err) {
        ErrDialog errFragment = ErrDialog.newInstance(err);
        errFragment.show(getFragmentManager(), "Error");
    }

    private void showErr(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
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

    private void startList() {
        if ((names == null) || (addresses == null) || (categories == null)) {
            startUpdatingData();
        } else {
            TwoLineArrayAdapter adapter = new TwoLineArrayAdapter(this, R.layout.list_row, names, addresses, categories);
            mListView = (ListView) findViewById(R.id.restaurantList);
            mListView.setAdapter(adapter);

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent detailIntent = new Intent(HomeActivity.this, DetailActivity.class);
                    System.out.println(position);
                    detailIntent.putExtra("itemID", ids[position]);
                    detailIntent.putExtra("userID", loggedUser);
                    startActivity(detailIntent);
                }
            });
        }
    }

    private void startFilteredList(final String[] namess, String[] addressess, String[] categoriess) {
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_row, R.id.firstLine, names);
        TwoLineArrayAdapter adapter = new TwoLineArrayAdapter(this, R.layout.list_row, namess, addressess, categoriess);
        mListView = (ListView)findViewById(R.id.restaurantList);
        mListView.setAdapter(adapter);
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog.setMessage("Updating");
        }
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detailIntent = new Intent(HomeActivity.this, DetailActivity.class);
                int i = 0;
                for (String element : names) {
                    if (element.equals(namess[position])) {
                        detailIntent.putExtra("itemID", ids[i]);
                    }
                    i++;
                }
                detailIntent.putExtra("userID", loggedUser);
                startActivity(detailIntent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        System.out.println("Stlacil som back haha!!");
        if(!TextUtils.isEmpty(getIntent().getStringExtra("login"))) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
            // set dialog message
            alertDialogBuilder
                    .setTitle("Log out?")
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    HomeActivity.this.finish();
                                    Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
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
        else{
            HomeActivity.this.finish();
            Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        }
    }

    private void startUpdatingData() {
        progressDialog.show();
        JSONArray obj = new JSONArray();

        CustomJSONOArrayRequest dataRequest = new CustomJSONOArrayRequest(Request.Method.GET, url, obj, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray r) {

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
                if (r.length() > 1) {
                    for(int i=1; i < r.length();i++){
                        try {
                            response = r.getJSONObject(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        names = arrayConcat(names, jsonParser.getStringFromJson(response, "rst_name"));
                        addresses = arrayConcat(addresses, jsonParser.getStringFromJson(response, "rst_address"));
                        ids = arrayConcat(ids, jsonParser.getStringFromJson(response, "objectId"));
                        urls = arrayConcat(urls, jsonParser.getStringFromJson(response, "rst_img_url"));
                        categories = arrayConcat(categories, jsonParser.getStringFromJson(response, "rst_cat"));
                    }
                }
                saveArrayData(names,"names",context);
                saveArrayData(addresses,"addresses",context);
                saveArrayData(ids,"ids",context);
                saveArrayData(urls,"urls",context);
                saveArrayData(categories,"categories",context);
                startList();

                //System.out.println(nOfObjects);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString());
                if (error instanceof NoConnectionError) {
                    showError(0);

                    names = getArrayData("names",context);
                    addresses = getArrayData("addresses",context);
                    ids = getArrayData("ids",context);
                    urls = getArrayData("urls",context);
                    categories = getArrayData("categories",context);
                    startList();
                } /*else {
                    showError(error.networkResponse.statusCode);
                }*/
            }
        });
       // System.out.println(dataRequest);
        MTAAApplication.getInstance().addToRequestQueue(dataRequest, "datarequest");
    }


    public boolean saveArrayData(String[] array, String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("preferencename", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(arrayName +" _size ", array.length);
        for(int i=0 ; i < array.length ; i++){
            editor.putString(arrayName + "_ " + i, array[i]);
        }
        return editor.commit();
    }

    public String[] getArrayData(String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("preferencename", 0);

        int size = prefs.getInt(arrayName + " _size ", 0);
        String[] arr = new String[size];

        for(int i=0 ; i < size ; i++){
           arr[i] = prefs.getString(arrayName + "_ " + i,"no_data");
            //editor.getString(arrayName + "_ " + i, array[i]);
        }
        return arr;
    }
/*    private void updateData() {
        final JSONObject obj = new JSONObject();
        CustomJSONOArrayRequest dataRequest = new CustomJSONOArrayRequest(Request.Method.GET, nUrl, obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //System.out.println(response.toString());
                JSONParser jsonParser = new JSONParser();
                nUrl = jsonParser.getNextPageUrl(response);
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
                };
            }
        });
        MTAAApplication.getInstance().addToRequestQueue(dataRequest, "datarequest");
    }*/

    private String[] arrayConcat(String[] a, String[] b) {
        int aLen = a.length;
        int bLen = b.length;
        String[] c =  new String[aLen+bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }


}
