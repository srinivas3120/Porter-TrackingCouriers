package com.srinivas.mudavath.porter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.srinivas.mudavath.adapter.CourierAdapter;
import com.srinivas.mudavath.network.CustomJsonObjectRequest;
import com.srinivas.mudavath.network.Util;
import com.srinivas.mudavath.network.VolleySingleton;
import com.srinivas.mudavath.pojo.CourierItem;
import com.srinivas.mudavath.pojo.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    SwipeRefreshLayout mSwipeRefreshLayout;
    LinearLayoutManager linearLayoutManager;
    RecyclerView rv_feeds;

    View.OnClickListener viewClickListener;
    Context mContext;

    LinearLayout ll_progress_status;
    TextView tv_error_status;
    ProgressBar progressBar;

    LinearLayout ll_search_container;
    EditText et_search_view;
    ImageView iv_clear_search;


    CustomJsonObjectRequest customJsonObjectRequest = null;
    VolleySingleton volleySingleton;

    ArrayList<CourierItem> courierItems = new ArrayList<CourierItem>();
    ArrayList<CourierItem> unFilteredCourierItems = new ArrayList<CourierItem>();
    CourierAdapter courierAdapter;
    public static String url = "https://api-test.theporter.in/interview_api/parcels/all_parcels.json";
    private TextWatcher onChangeWatcher;
    private String searchString;

    private ActionBar ab;
    private Toolbar toolbar;

    private TextView tv_total_parcels;
    private LinearLayout ll_name;
    private LinearLayout ll_price;
    private LinearLayout ll_weight;

    Resources res;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        setUpActionBar();
        initializeViews();
        setClickListeners();
        res = getResources();
        onTextChangeListener();

        linearLayoutManager = new LinearLayoutManager(mContext);
        rv_feeds.setLayoutManager(linearLayoutManager);
        mClicklistner();
        volleySingleton = VolleySingleton.getInstance();
        courierAdapter = new CourierAdapter(courierItems, viewClickListener);
        rv_feeds.setAdapter(courierAdapter);
        fetchCourierData();
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void setUpActionBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_navigation_menu_white);
        ab.setTitle("Porter Parcels");
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void initializeViews() {
        rv_feeds = (RecyclerView) findViewById(R.id.rv_feeds);
        ll_progress_status = (LinearLayout) findViewById(R.id.ll_progress_status);
        tv_error_status = (TextView) findViewById(R.id.tv_error_status);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        ll_search_container = (LinearLayout) findViewById(R.id.ll_search_container);
        et_search_view = (EditText) findViewById(R.id.et_search_view);
        iv_clear_search = (ImageView) findViewById(R.id.iv_clear_search);
        iv_clear_search.setOnClickListener(this);

        tv_total_parcels = (TextView) findViewById(R.id.tv_total_parcels);
        ll_name = (LinearLayout) findViewById(R.id.ll_name);
        ll_price = (LinearLayout) findViewById(R.id.ll_price);
        ll_weight = (LinearLayout) findViewById(R.id.ll_weight);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

    }

    private void setClickListeners() {

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                fetchCourierData();
            }
        });

        ll_name.setOnClickListener(this);
        ll_price.setOnClickListener(this);
        ll_weight.setOnClickListener(this);
        tv_error_status.setOnClickListener(this);
    }

    private void fetchCourierData() {
        if (Util.isNetworkAvailable(mContext)) {
            new LoadFeed().execute(url);
        } else {
            if (courierItems.size() > 0) {
                mSwipeRefreshLayout.setRefreshing(false);
                Util.showBottomToast(this,"Check your internet connection...");
            } else {
                ll_progress_status.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
                tv_error_status.setText("Check your internet connection...");
            }
        }
    }

    private void onTextChangeListener() {
        if (onChangeWatcher == null)
            onChangeWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i,
                                              int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i,
                                          int i1, int i2) {

                    searchString = et_search_view.getText().toString().trim();
                    filterDataSet();
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            };
        if (et_search_view != null) {
            et_search_view.addTextChangedListener(onChangeWatcher);
        }
    }

    private void filterDataSet() {

        courierItems.clear();
        for (CourierItem courierItem : unFilteredCourierItems) {
            if (courierItem.getName().toLowerCase().contains(searchString.toLowerCase()) ||
                    courierItem.getWeight().toLowerCase().contains(searchString.toLowerCase()) ||
                    courierItem.getValue().toLowerCase().contains(searchString.toLowerCase())) {
                courierItems.add(courierItem);
            }
        }
        courierAdapter.notifyDataSetChanged();
    }


    private void mClicklistner() {

        viewClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int position = (Integer) v.getTag();

                switch (v.getId()) {
                    case R.id.cv_courier_element:
                        startDetailedCourierActivity(position);
                        break;
                    default:
                        break;

                }
            }
        };
    }

    private void startDetailedCourierActivity(int position) {
        Intent intent = new Intent(this, DetailedParcelActivity.class);
        intent.putExtra("parcel_data", courierItems.get(position));
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_clear_search:
                et_search_view.setText("");
                break;
            case R.id.ll_name:
                ll_name.setBackgroundDrawable(res.getDrawable(R.drawable.bg_white));
                ll_price.setBackgroundDrawable(null);
                ll_weight.setBackgroundDrawable(null);
                Collections.sort(courierItems, new Comparator<CourierItem>() {
                    @Override
                    public int compare(CourierItem p1, CourierItem p2) {
                        return p1.getName().compareToIgnoreCase(p2.getName()); // Ascending
                    }

                });
                courierAdapter.notifyDataSetChanged();
                break;
            case R.id.ll_price:
                ll_name.setBackgroundDrawable(null);
                ll_price.setBackgroundDrawable(res.getDrawable(R.drawable.bg_white));
                ll_weight.setBackgroundDrawable(null);
                Collections.sort(courierItems, new Comparator<CourierItem>() {
                    @Override
                    public int compare(CourierItem p1, CourierItem p2) {
                        return new Double(Double.parseDouble(p1.getValue())).compareTo(new Double(Double.parseDouble(p2.getValue()))); // Ascending
                    }

                });
                courierAdapter.notifyDataSetChanged();
                break;
            case R.id.ll_weight:
                ll_name.setBackgroundDrawable(null);
                ll_price.setBackgroundDrawable(null);
                ll_weight.setBackgroundDrawable(res.getDrawable(R.drawable.bg_white));
                Collections.sort(courierItems, new Comparator<CourierItem>() {
                    @Override
                    public int compare(CourierItem p1, CourierItem p2) {
                        return new Double(Double.parseDouble(p1.getWeight())).compareTo(new Double(Double.parseDouble(p2.getWeight()))); // Ascending
                    }

                });
                courierAdapter.notifyDataSetChanged();
                break;
            case R.id.tv_error_status:
                fetchCourierData();
                break;
            default:
                break;
        }
    }



    @Override
    public void onStart() {
        super.onStart();
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "Main Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://com.srinivas.mudavath.porter/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "Main Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://com.srinivas.mudavath.porter/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    private class LoadFeed extends AsyncTask<String, Void, ArrayList<CourierItem>> {

        public LoadFeed() {
            if (courierItems.size() > 0) {
                ll_search_container.setVisibility(View.VISIBLE);
                mSwipeRefreshLayout.setRefreshing(true);
            } else {
                ll_search_container.setVisibility(View.GONE);
                ll_progress_status.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                tv_error_status.setText("Loading...");
                mSwipeRefreshLayout.setVisibility(View.GONE);
            }
        }

        @Override
        protected ArrayList<CourierItem> doInBackground(String... params) {
            ArrayList<CourierItem> courierItems = new ArrayList<CourierItem>();
            StringBuilder result = new StringBuilder();
            try {

                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                System.out.println("Response Code: " + conn.getResponseCode());
                InputStream is = new BufferedInputStream(conn.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONArray jsonArray = new JSONArray(result.toString());
                int size = jsonArray.length();
                for (int i = 0; i < size; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    JSONObject jsonObject1 = jsonObject.getJSONObject("current_location");
                    courierItems.add(new CourierItem(jsonObject.getString("name"), jsonObject.getString("image_link"),
                            jsonObject.getString("type"), jsonObject.getString("weight"), jsonObject.getInt("quantity"),
                            jsonObject.getString("value"), jsonObject.getString("color"), jsonObject.getString("datetime"),
                            new Location(jsonObject1.getString("lat"), jsonObject1.getString("long"))));
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return courierItems;
        }

        @Override
        protected void onPostExecute(ArrayList<CourierItem> feeds) {
            if (feeds.size() > 0) {
                courierItems.clear();
                courierItems.addAll(feeds);
                unFilteredCourierItems.clear();
                unFilteredCourierItems.addAll(courierItems);
                ll_search_container.setVisibility(View.VISIBLE);
                courierAdapter.notifyDataSetChanged();
            }
            tv_total_parcels.setText(String.format(res.getString(R.string.total_parcels), courierItems.size()));
            mSwipeRefreshLayout.setRefreshing(false);
            ll_progress_status.setVisibility(View.GONE);
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);

            ll_name.setBackgroundDrawable(res.getDrawable(R.drawable.bg_white));
            ll_price.setBackgroundDrawable(null);
            ll_weight.setBackgroundDrawable(null);
            Collections.sort(courierItems, new Comparator<CourierItem>() {
                @Override
                public int compare(CourierItem p1, CourierItem p2) {
                    return p1.getName().compareToIgnoreCase(p2.getName()); // Ascending
                }

            });
            courierAdapter.notifyDataSetChanged();
        }
    }
}
