package com.srinivas.mudavath.porter;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.srinivas.mudavath.network.MyApplication;
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
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Mudavath Srinivas on 21-02-2016.
 */
public class DetailedParcelActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    ImageView iv_item_pic;
    TextView tv_name;
    TextView tv_price;
    TextView tv_quantity;
    TextView tv_weight;
    View v_color;
    TextView tv_type;
    TextView tv_eta;
    ImageView iv_refresh;
    CourierItem courierItem=new CourierItem();

    VolleySingleton volleySingleton=null;
    ImageLoader imageLoader=null;
    private MapFragment mapFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.courier_details);
        if(getIntent().hasExtra("parcel_data")){
            courierItem= (CourierItem) getIntent().getSerializableExtra("parcel_data");
        }else {
            Util.showBottomToast(this,"Something went wrong...");
            super.onBackPressed();
        }

        volleySingleton= VolleySingleton.getInstance();
        imageLoader=volleySingleton.getImageLoader();
        initializeView();
        setContent();

    }

    private void initializeView() {
        iv_item_pic= (ImageView) findViewById(R.id.iv_item_pic);
        tv_name= (TextView) findViewById(R.id.tv_name);
        tv_weight= (TextView) findViewById(R.id.tv_weight);
        tv_price= (TextView) findViewById(R.id.tv_price);
        tv_quantity= (TextView) findViewById(R.id.tv_quantity);
        tv_type= (TextView) findViewById(R.id.tv_type);
        v_color= (View) findViewById(R.id.v_color);
        tv_eta= (TextView) findViewById(R.id.tv_eta);
        iv_refresh= (ImageView) findViewById(R.id.iv_refresh);

        iv_refresh.setOnClickListener(this);

        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void setContent() {
        Resources res = getResources();

        tv_type.setText(courierItem.getType());
        tv_name.setText(courierItem.getName());
        tv_weight.setText(String.format(res.getString(R.string.weight), courierItem.getWeight()));
        tv_price.setText(String.format(res.getString(R.string.price), courierItem.getValue()));
        tv_quantity.setText(""+courierItem.getQuantity());
        v_color.setBackgroundColor(Color.parseColor(courierItem.getColor()));
        tv_eta.setText(courierItem.getEta().substring(0, 10));

        imageLoader.get(courierItem.getUrl(), new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (response.getBitmap() != null) {
                    iv_item_pic.setImageBitmap(response.getBitmap());
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                iv_item_pic.setImageResource(R.drawable.profile_pic_default_profile_pic);
            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_refresh:
                new UpdateLocation().execute();
                break;
            default:
                break;
        }
    }

    private class UpdateLocation extends AsyncTask<String, Void, String>  {

        @Override
        protected String doInBackground(String... params) {
            StringBuilder result = new StringBuilder();
            try {

                URL url = new URL("https://api-test.theporter.in/interview_api/parcels/latest_location.json?name="+ URLEncoder.encode(courierItem.getName(),"utf-8"));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                System.out.println("Response Code: " + conn.getResponseCode());
                InputStream is = new BufferedInputStream(conn.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONObject jsonObject=new JSONObject(result.toString());
                if(jsonObject.has("lat")){
                    courierItem.setCurrentLocation(new Location(jsonObject.getString("lat"), jsonObject.getString("long")));
                   return "ok";
                }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String status) {
            if(status!=null){
                mapFragment.getMapAsync(DetailedParcelActivity.this);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(Double.parseDouble(courierItem.getCurrentLocation().getLatitude()),
                Double.parseDouble(courierItem.getCurrentLocation().getLongitude()));
        googleMap.clear();
        googleMap.setMyLocationEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
        googleMap.addMarker(new MarkerOptions()
                .position(latLng));
    }
}
