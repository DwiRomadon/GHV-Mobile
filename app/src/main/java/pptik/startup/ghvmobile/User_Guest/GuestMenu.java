package pptik.startup.ghvmobile.User_Guest;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
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
import pptik.startup.ghvmobile.GetRole;
import pptik.startup.ghvmobile.MainMenu;
import pptik.startup.ghvmobile.User_Relawan.ProfileRelawan;
import pptik.startup.ghvmobile.R;
import pptik.startup.ghvmobile.Utilities.PictureFormatTransform;
import pptik.startup.ghvmobile.Fragments.MarkerProgramFragment;
import pptik.startup.ghvmobile.Fragments.MarkerUserFragment;
import pptik.startup.ghvmobile.Setup.ApplicationConstants;
import pptik.startup.ghvmobile.Utilities.DrawerUtil;

/**
 * Created by GIGABYTE on 27/06/2016.
 */

public class GuestMenu extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, Marker.OnMarkerClickListener {
    SharedPreferences prefs;
    public boolean status;

    //keperluan map
    private FloatingActionButton fabMyLoc;
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

    private Drawer mainDrawer;
    private AccountHeader mainHeader;
    DrawerUtil drawerUtil;
    ImageView headerAva;
    private TextView headername;
    Toolbar toolbar;
    private String pathfotoRef,email;

    //keperluan marker
    private Loading loadingPin;
    private Timer timer2;
    private String TAG_MAP_VIEW = "Map View";
    private int totalRequest = 0;
    private ImageButton closeFragment;
    private LinearLayout pinDetail;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guest_menu_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;
        permissionCheck = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        initMap();
        setLocationBuilder();
        bindingXml();
        prefs = getSharedPreferences(ApplicationConstants.USER_PREFS_NAME,
                Context.MODE_PRIVATE);
        id_user=prefs.getInt(ApplicationConstants.USER_ID,0);
        email = prefs.getString(ApplicationConstants.EMAIL_ID, "");
        pathfotoRef=prefs.getString(ApplicationConstants.PATH_FOTO_USER,"");
        initNavigationDrawer();
        updateMap();
        timer2 = new Timer();
        //setAndRunTimer();
    }
    public void initNavigationDrawer(){
        drawerUtil = new DrawerUtil(this, toolbar,0);
        drawerUtil.initDrawerGuest();
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
                //Log.e("Error get image", e.getMessage());
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



    public void checkstatusdaftar(final Context __context){
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(ApplicationConstants.API_GET_STATUS_DAFTAR+new GetRole(__context).getuserid(),
                new AsyncHttpResponseHandler() {
                    // When the response returned by REST has Http
                    // response code '200'
                    @Override
                    public void onSuccess(String response) {
                        Log.i("response login : ", response);
                        Log.i("response ID : ", String.valueOf(new GetRole(__context).getuserid()));

                        try {
                            JSONObject jObj = new JSONObject(response);
                            status = jObj.getBoolean("status");
                            if (status) {
                                final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(__context);
                                alertDialog.setTitle("Info");
                                alertDialog.setCancelable(false);
                                alertDialog.setMessage("We Are Sorry, but your account are not verified by admin yet.");
                                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        alertDialog.create().dismiss();
                                    }
                                });
                                alertDialog.setNegativeButton("Edit Your data", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(__context, ProfileRelawan.class);
                                        __context.startActivity(intent);
                                        ((Activity)__context).finish();
                                    }
                                });
                                alertDialog.show();
                            } else {
                                Intent intent = new Intent(__context, DaftarRelawan.class);
                                __context.startActivity(intent);
                                ((Activity)__context).finish();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // Hide Progress Dialog

                    }

                    // When the response returned by REST has Http
                    // response code other than '200' such as '404',
                    // '500' or '403' etc
                    @Override
                    public void onFailure(int statusCode, Throwable error,
                                          String content) {
                        // Hide Progress Dialog

                        // When Http response code is '404'
                      /*  if (statusCode == 404) {
                            Toast.makeText(__context,
                                    "Requested resource not found",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code is '500'
                        else if (statusCode == 500) {
                            Toast.makeText(__context,
                                    "Something went wrong at server end",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code other than 404, 500
                        else {
                            Toast.makeText(
                                    __context,
                                    "Unexpected Error occcured! [Most common Error: Device might "
                                            + "not be connected to Internet or remote server is not up and running], check for other errors as well",
                                    Toast.LENGTH_LONG).show();
                        }*/
                        //tes
                    }
                });
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
        fabMyLoc = (FloatingActionButton)findViewById(R.id.fab_myloc_guest);

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
        mapset = (MapView) findViewById(R.id.mainMap_guest);
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
            mapController.setZoom(50);
            mapController.animateTo(currentPoint);
            //  mapController.setCenter(currentPoint);
            mapset.invalidate();
            curMarker.setPosition(currentPoint);
            mapset.getOverlays().add(curMarker);
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

            // MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
            // app-defined int constant. The callback method gets the
            // result of the request.

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
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
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
        Intent intent = new Intent(GuestMenu.this, MainMenu.class);
        startActivity(intent);
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
                GuestMenu.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateMap();
                    }
                });
            }
        }, 5000, 5000);
    }
    private void updateMap(){
        GuestMenu.this.showLoadingPin();
        totalRequest += 1;
        Log.i(TAG_MAP_VIEW, "----------------------- Request no "+totalRequest+" ------------------------");
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(ApplicationConstants.API_GET_MAP_VIEW,
                new AsyncHttpResponseHandler() {
                    // When the response returned by REST has Http
                    // response code '200'
                    @Override
                    public void onSuccess(String response) {
                        Log.i(TAG_MAP_VIEW, response);
                        hideLoadingPin();

                        try {
                            JSONObject json = new JSONObject();
                            try {
                                json.put("type", ApplicationConstants.MARKER_ME);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            addMarker(ApplicationConstants.MARKER_ME, currentLatitude, currentLongitude, json );

                            JSONObject jObj = new JSONObject(response);
                            boolean status = jObj.getBoolean("status");
                            if (status) {
                                mapset.getOverlays().clear();
                                JSONArray jsonArray= jObj.getJSONArray("admin");
                                for(int i = 0; i < jsonArray.length(); i++){
                                    JSONObject childObject = jsonArray.getJSONObject(i);
                                    addMarker(ApplicationConstants.MARKER_ADMIN, childObject.optDouble("latitude"), childObject.optDouble("longitude"), childObject);
                                }
                                JSONArray jsonArray2= jObj.getJSONArray("relawan");
                                for(int i = 0; i < jsonArray2.length(); i++){
                                    JSONObject childObject = jsonArray2.getJSONObject(i);
                                    addMarker(ApplicationConstants.MARKER_USER, childObject.optDouble("latitude"), childObject.optDouble("longitude"), childObject);
                                }
                                JSONArray jsonArray3= jObj.getJSONArray("program");
                                for(int i = 0; i < jsonArray3.length(); i++){
                                    JSONObject childObject = jsonArray3.getJSONObject(i);
                                    addMarker(ApplicationConstants.MARKER_PROGRAM, childObject.optDouble("latitude"), childObject.optDouble("longitude"), childObject);
                                }


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        mapset.invalidate();
                    }

                    // When the response returned by REST has Http
                    // response code other than '200' such as '404',
                    // '500' or '403' etc
                    @Override
                    public void onFailure(int statusCode, Throwable error,
                                          String content) {
                        // Hide Progress Dialog
                        hideLoadingPin();
                        // When Http response code is '404'
                        if (statusCode == 404) {
                            Toast.makeText(getApplicationContext(),
                                    "Requested resource not found",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code is '500'
                        else if (statusCode == 500) {
                            Toast.makeText(getApplicationContext(),
                                    "Something went wrong at server end",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code other than 404, 500
                        else {
                           /* Toast.makeText(
                                    getApplicationContext(),
                                    "Unexpected Error occcured! [Most common Error: Device might "
                                            + "not be connected to Internet or remote server is not up and running], check for other errors as well",
                                    Toast.LENGTH_LONG).show();*/
                        }
                    }
                });

    }
    //------------------------ ADD MARKER
    private void addMarker(int type, double Latitude, double Longitude, JSONObject info){
        GeoPoint startPoint = new GeoPoint(Latitude, Longitude);

        Marker marker = new Marker(mapset);
        marker.setPosition(startPoint);
        try {
            info.put("type", type);
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
                Log.i("admin : ",String.valueOf(Latitude)+" "+String.valueOf(Longitude));
                break;
            case ApplicationConstants.MARKER_USER:
                marker.setIcon(new IconicsDrawable(this)
                        .icon(GoogleMaterial.Icon.gmd_person_pin_circle)
                        .color(context.getResources().getColor(R.color.actorange))
                        .sizeDp(48));
                Log.i("user : ",String.valueOf(Latitude)+" "+String.valueOf(Longitude));
                break;
            case ApplicationConstants.MARKER_PROGRAM:
                marker.setIcon(new IconicsDrawable(this)
                        .icon(GoogleMaterial.Icon.gmd_beenhere)
                        .color(context.getResources().getColor(R.color.green_200))
                        .sizeDp(48));
                Log.i("program : ",String.valueOf(Latitude)+" "+String.valueOf(Longitude));
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
            fragmentTransaction.commit();
            //    toolbar_bottom.setVisibility(View.INVISIBLE);
        }else if (obj.optInt("type")==ApplicationConstants.MARKER_PROGRAM){
            hidebutton();
            MarkerProgramFragment fragment=new MarkerProgramFragment();
            fragment.setData(obj);
            fragmentTransaction.replace(R.id.pinDetail, fragment);
            fragmentTransaction.commit();
        }else if (obj.optInt("type")==ApplicationConstants.MARKER_ME){
            Toast.makeText(context,
                    "Lokasi Saya",
                    Toast.LENGTH_LONG).show();
        }

        return true;
    }
    private void hidebutton(){
        fabMyLoc.setVisibility(View.INVISIBLE);

        pinDetail.setVisibility(View.VISIBLE);
    }
    private void showbutton(){
        fabMyLoc.setVisibility(View.VISIBLE);

        pinDetail.setVisibility(View.INVISIBLE);
    }

    private void updateCurrentLocation() {
        RequestRest req = new RequestRest(GuestMenu.this, new IConnectionResponseHandler(){
            @Override
            public void OnSuccessArray(JSONArray result){
                Log.i("result", result.toString());

            }
            @Override
            public void onSuccessJSONObject(String result){
                try {
                    JSONObject obj = new JSONObject(result);
                    Log.i("Test", result);
                } catch (JSONException e){
                }
            }
            @Override
            public void onFailure(String e){
                Log.i("Test", e);
            }
            @Override
            public void onSuccessJSONArray(String result){
                Log.i("Test", result);
            }
        });


        req.updateCurrentLocation(String.valueOf(id_user) ,String.valueOf(currentLatitude) ,String.valueOf(currentLongitude));
    }
}