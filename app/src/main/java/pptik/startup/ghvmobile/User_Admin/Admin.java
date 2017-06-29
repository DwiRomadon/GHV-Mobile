package pptik.startup.ghvmobile.User_Admin;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.ionicons_typeface_library.Ionicons;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;

import net.qiujuer.genius.ui.widget.Loading;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import pptik.startup.ghvmobile.Connection.IConnectionResponseHandler;
import pptik.startup.ghvmobile.Connection.RequestRest;
import pptik.startup.ghvmobile.Fragments.MarkerProgramFragment;
import pptik.startup.ghvmobile.Fragments.MarkerUserFragment;
import pptik.startup.ghvmobile.R;
import pptik.startup.ghvmobile.Setup.ApplicationConstants;
import pptik.startup.ghvmobile.SubmitProgram;
import pptik.startup.ghvmobile.Utilities.DrawerUtil;
import pptik.startup.ghvmobile.Utilities.PictureFormatTransform;

/**
 * Created by GIGABYTE on 15/06/2016.
 */
public class Admin extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, Marker.OnMarkerClickListener {
    private Context applicationContext;
    SharedPreferences prefs;
    //keperluan map
    private FloatingActionButton fabMyLoc,fabAddProgram;
    private boolean isFirstZoom = false;
    int permissionCheck =0;
    private static final int INITIAL_REQUEST=1337;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION=INITIAL_REQUEST+1;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLocation;
    private String TAG = this.getClass().getSimpleName();
    private double currentLatitude;
    private double currentLongitude;
    private int id_user;
    private Context context;
    MapView mapset;
    GeoPoint currentPoint;
    Marker curMarker;
    IMapController mapController;

    private String pathfotoRef,email;
    //keperluan marker
    private Loading loadingPin;
    private Timer timer2;
    private String TAG_MAP_VIEW = "Map View";
    private int totalRequest = 0;
    private ImageButton closeFragment;

    private Drawer mainDrawer;
    private AccountHeader mainHeader;
    DrawerUtil drawerUtil;
    ImageView headerAva;
    private TextView headername;
    private LinearLayout pinDetail;
    private Toolbar toolbar;


    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.admin_menu_activity);
    toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
   context = getApplicationContext();
        prefs = getSharedPreferences(ApplicationConstants.USER_PREFS_NAME,
                Context.MODE_PRIVATE);
        id_user=prefs.getInt(ApplicationConstants.USER_ID,0);
        pathfotoRef=prefs.getString(ApplicationConstants.PATH_FOTO_USER,ApplicationConstants.DEFAULT_PATH_FOTO);
        email=prefs.getString(ApplicationConstants.EMAIL_ID,"");

        permissionCheck = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        bindingXml();
        initMap();
        setLocationBuilder();
        initNavigationDrawer();
        updateMap();
        timer2 = new Timer();
        //setAndRunTimer();
    }
    public void initNavigationDrawer(){
        drawerUtil = new DrawerUtil(this, toolbar,0);
        drawerUtil.initDrawerAdmin();
        mainDrawer = drawerUtil.getDrawer();
        mainHeader = drawerUtil.getDrawerHeader();
        headername=(TextView)mainHeader.getView().findViewById(R.id.Headername);
        headerAva = (ImageView)mainHeader.getView().findViewById(R.id.mainHeaderAva);
        new DownloadImageTask(headerAva)
                .execute(pathfotoRef);
        headername.setText(email.toString());

    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            try {
                result=Bitmap.createScaledBitmap(result,(int)(result.getWidth()*0.5), (int)(result.getHeight()*0.5), true);

            }catch (Exception e){
                Log.d("e",e.toString());
            }
            headerAva.setImageBitmap(result);

        }
    }
    private void setLocationBuilder(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1 * 1000);
    }
    private void bindingXml(){
        fabMyLoc = (FloatingActionButton)findViewById(R.id.fab_myloc_admin);
        fabMyLoc.setImageBitmap(PictureFormatTransform.drawableToBitmap(new IconicsDrawable(this)
                .icon(Ionicons.Icon.ion_android_locate)
                .color(context.getResources().getColor(R.color.colorPrimary))
                .sizeDp(60)));
        fabMyLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomMapToCurrent();
            }
        });

        fabAddProgram=(FloatingActionButton)findViewById(R.id.fab_add_program);
        fabAddProgram.setImageBitmap(PictureFormatTransform.drawableToBitmap(new IconicsDrawable(this)
                .icon(Ionicons.Icon.ion_plus_circled)
                .color(context.getResources().getColor(R.color.colorPrimary))
                .sizeDp(60)));
        fabAddProgram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SubmitProgram.class);
                startActivity(intent);
                finish();
            }
        });

        closeFragment=(ImageButton)findViewById(R.id.closeFragment);
        closeFragment.setImageDrawable(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_keyboard_arrow_down)
                .color(context.getResources().getColor(R.color.white))
                .sizeDp(24));
        closeFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showbutton();
            }
        });
        loadingPin = (Loading)findViewById(R.id.loadingPin);
        pinDetail = (LinearLayout) findViewById(R.id.pinDetail);
        loadingPin.setAutoRun(true);
        loadingPin.setVisibility(View.GONE);



    }
    private void initMap() {
        mapset = (MapView) findViewById(R.id.mainMap_admin);
        mapset.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mapset.setMultiTouchControls(true);
        mapController = mapset.getController();

        curMarker = new Marker(mapset);
        curMarker.setTitle("My Location");
        curMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        curMarker.setIcon(new IconicsDrawable(this)
                .icon(Ionicons.Icon.ion_android_pin)
                .color(context.getResources().getColor(R.color.colorPrimary))
                .sizeDp(48));

    }

    private void zoomMapToCurrent(){
        try {
            updateCurrentLocation();
            mapController.setZoom(25);
            mapController.animateTo(currentPoint);
            mapset.invalidate();

        } catch (Throwable e) {
            Toast.makeText(this, "Location not Detected, Please Turn On GPS", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLocation != null) {
            Toast.makeText(this, "Location Detected "/*+mLocation.getLatitude()+" "+
                    mLocation.getLongitude()*/, Toast.LENGTH_SHORT).show();
            currentLatitude = mLocation.getLatitude();
            currentLongitude = mLocation.getLongitude();
            currentPoint = new GeoPoint(currentLatitude, currentLongitude);
            storeLastLocation(getApplicationContext(), String.valueOf(currentLatitude),String.valueOf(currentLongitude));
            zoomMapToCurrent();
            isFirstZoom = true;
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the task you need to do.

                } else {

                    // permission denied, boo! Disable the functionality that depends on this permission.
                }
                return;
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    protected void onStart() {
        super.onStart();
        try{
            mGoogleApiClient.connect();
        }catch (Exception e){
            Toast.makeText(this, "Location not Detected, Please Turn On GPS", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        currentPoint = new GeoPoint(currentLatitude, currentLongitude);
        storeLastLocation(getApplicationContext(), String.valueOf(currentLatitude),String.valueOf(currentLongitude));
        if(isFirstZoom == false){
            zoomMapToCurrent();
            isFirstZoom = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Now lets connect to the API
        if(mGoogleApiClient.isConnected() == false)
            mGoogleApiClient.connect();
        //setAndRunTimer();
        updateMap();
    }

    @Override
    protected  void onDestroy(){
        super.onDestroy();
        timer2.cancel();
        if(mGoogleApiClient.isConnected()){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        //
    }
    private void storeLastLocation(Context context, String latitude_,String longitude_) {

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ApplicationConstants.USER_LATITUDE, latitude_);
        editor.putString(ApplicationConstants.USER_LONGITUDE, longitude_);
        editor.commit();
    }


    @Override
    public void onBackPressed() {
        if (timer2!=null){
            timer2.cancel();
        }
        finish();
    }
    //---------------- RELOAD MAP EVERY 0.5 SEC
    private void showLoadingPin(){
        loadingPin.setVisibility(View.VISIBLE);
        loadingPin.start();
    }
    private void hideLoadingPin(){
        loadingPin.setVisibility(View.INVISIBLE);
        loadingPin.stop();
    }
    private void setAndRunTimer(){
        timer2.schedule(new TimerTask() {
            @Override
            public void run() {
                Admin.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateMap();
                    }
                });
            }
        }, 5000, 5000);
    }
    private void updateMap(){
        Admin.this.showLoadingPin();
        totalRequest += 1;
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(ApplicationConstants.API_GET_MAP_VIEW_ADMIN,
                new AsyncHttpResponseHandler() {
                    // When the response returned by REST has Http
                    // response code '200'
                    @Override
                    public void onSuccess(String response) {
                        hideLoadingPin();

                        try {
                            JSONObject json = new JSONObject();
                            try {
                                json.put("type", ApplicationConstants.MARKER_ME);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            addMarker(ApplicationConstants.MARKER_ME, currentLatitude, currentLongitude, json,1 );

                            JSONObject jObj = new JSONObject(response);
                            boolean status = jObj.getBoolean("status");
                            if (status) {
                                mapset.getOverlays().clear();
                                JSONArray jsonArray= jObj.getJSONArray("admin");
                                for(int i = 0; i < jsonArray.length(); i++){
                                    JSONObject childObject = jsonArray.getJSONObject(i);
                                    addMarker(ApplicationConstants.MARKER_ADMIN, childObject.optDouble("latitude"), childObject.optDouble("longitude"), childObject,childObject.getInt("level"));
                                }
                                JSONArray jsonArray2= jObj.getJSONArray("relawan");
                                for(int i = 0; i < jsonArray2.length(); i++){
                                    JSONObject childObject = jsonArray2.getJSONObject(i);
                                    addMarker(ApplicationConstants.MARKER_USER, childObject.optDouble("latitude"), childObject.optDouble("longitude"), childObject,childObject.getInt("level"));
                                }
                                JSONArray jsonArray3= jObj.getJSONArray("program");
                                for(int i = 0; i < jsonArray3.length(); i++){
                                    JSONObject childObject = jsonArray3.getJSONObject(i);
                                    addMarker(ApplicationConstants.MARKER_PROGRAM, childObject.optDouble("latitude"), childObject.optDouble("longitude"), childObject,childObject.getInt("status"));
                                }


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        mapset.invalidate();


                    }


                    @Override
                    public void onFailure(int statusCode, Throwable error,
                                          String content) {

                        hideLoadingPin();

                        if (statusCode == 404) {
                          }
                        else if (statusCode == 500) {

                        }

                        else {

                        }
                    }
                });

    }


    //------------------------ ADD MARKER
    private void addMarker(int type, double Latitude, double Longitude, JSONObject info,int status){
        GeoPoint startPoint = new GeoPoint(Latitude, Longitude);
        Marker marker = new Marker(mapset);
        marker.setPosition(startPoint);

        try {
            info.put("type", type);
            info.put("levelaksesuser","1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        marker.setRelatedObject(info);
        marker.setOnMarkerClickListener(this);

        switch (type){
            case ApplicationConstants.MARKER_ADMIN:
                marker.setIcon(new IconicsDrawable(this)
                        .icon(GoogleMaterial.Icon.gmd_person_pin_circle)
                        .color(context.getResources().getColor(R.color.actorange))
                        .sizeDp(48));
                 break;
            case ApplicationConstants.MARKER_USER:
                if (status==3){
                    marker.setIcon(new IconicsDrawable(this)
                            .icon(GoogleMaterial.Icon.gmd_person_pin_circle)
                            .color(context.getResources().getColor(R.color.red_200))
                            .sizeDp(48));
                }else {
                    marker.setIcon(new IconicsDrawable(this)
                            .icon(GoogleMaterial.Icon.gmd_person_pin_circle)
                            .color(context.getResources().getColor(R.color.actorange))
                            .sizeDp(48));
                }
                break;
            case ApplicationConstants.MARKER_PROGRAM:
                if (status==1) {
                    marker.setIcon(new IconicsDrawable(this)
                            .icon(GoogleMaterial.Icon.gmd_beenhere)
                            .color(context.getResources().getColor(R.color.green_200))
                            .sizeDp(48));
                }else {
                    marker.setIcon(new IconicsDrawable(this)
                            .icon(GoogleMaterial.Icon.gmd_beenhere)
                            .color(context.getResources().getColor(R.color.red))
                            .sizeDp(48));
                }
                break;
            case ApplicationConstants.MARKER_ME:
                marker.setIcon(new IconicsDrawable(this)
                        .icon(Ionicons.Icon.ion_android_pin)
                        .color(context.getResources().getColor(R.color.colorPrimary))
                        .sizeDp(48));
                break;
        }

        mapset.getOverlays().add(marker);
    }

    @Override
    public boolean onMarkerClick(Marker marker, MapView mapView) {

        FragmentManager fragmentManager=getFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        JSONObject obj = (JSONObject)marker.getRelatedObject();

        if(obj.optInt("type") == ApplicationConstants.MARKER_ADMIN ||obj.optInt("type") == ApplicationConstants.MARKER_USER) {
            hidebutton();
            MarkerUserFragment fragment = new MarkerUserFragment();
            fragment.setData(obj);
            fragmentTransaction.replace(R.id.pinDetail, fragment);
        }else if (obj.optInt("type")==ApplicationConstants.MARKER_PROGRAM){
            hidebutton();
            MarkerProgramFragment fragment=new MarkerProgramFragment();
            fragment.setData(obj);
            fragmentTransaction.replace(R.id.pinDetail, fragment);
        }else if (obj.optInt("type")==ApplicationConstants.MARKER_ME){
            hidebutton();
            MarkerUserFragment fragment = new MarkerUserFragment();
            fragment.setData(obj);
            fragmentTransaction.replace(R.id.pinDetail, fragment);
        }
        fragmentTransaction.commit();
        return true;
    }
    private void hidebutton(){
        fabMyLoc.setVisibility(View.INVISIBLE);
        fabAddProgram.setVisibility(View.INVISIBLE);
        pinDetail.setVisibility(View.VISIBLE);
    }
    private void showbutton(){
        fabMyLoc.setVisibility(View.VISIBLE);
        fabAddProgram.setVisibility(View.VISIBLE);
        pinDetail.setVisibility(View.INVISIBLE);
    }

    private void updateCurrentLocation() {
        RequestRest req = new RequestRest(Admin.this, new IConnectionResponseHandler(){
            @Override
            public void OnSuccessArray(JSONArray result){

            }
            @Override
            public void onSuccessJSONObject(String result){
                try {
                    JSONObject obj = new JSONObject(result);

                } catch (JSONException e){
                }
            }
            @Override
            public void onFailure(String e){
            }
            @Override
            public void onSuccessJSONArray(String result){
            }
        });


        req.updateCurrentLocation(String.valueOf(id_user) ,String.valueOf(currentLatitude) ,String.valueOf(currentLongitude));
    }
}
