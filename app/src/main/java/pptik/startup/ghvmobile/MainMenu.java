package pptik.startup.ghvmobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;

import pptik.startup.ghvmobile.Support.Program;
import pptik.startup.ghvmobile.User_Guest.GuestListProgram;
import pptik.startup.ghvmobile.User_Guest.GuestMenu;
import pptik.startup.ghvmobile.User_Relawan.ProfileRelawan;
import pptik.startup.ghvmobile.User_Relawan.RelawanMenu;
import pptik.startup.ghvmobile.User_Relawan.Relawan_Program;
import pptik.startup.ghvmobile.Setup.ApplicationConstants;
/**
 * Created by GIGABYTE on 05/08/2016.
 */
public class MainMenu extends AppCompatActivity
        implements BaseSliderView.OnSliderClickListener,
        ViewPagerEx.OnPageChangeListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener
         {
             private ArrayList<Program> listProgram;
             private SliderLayout mDemoSlider;
             private Toolbar toolbar;
             private LinearLayout L_volunteer,L_program,L_map;
             private String theRole;
             private SharedPreferences prefs;
             private TextView v_text,v_desc;
             private ImageButton icon_relawan,icon_program,icon_map;


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
           protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.main_menu_activity);
                toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                context = this;
               prefs = getSharedPreferences("UserDetails",
                       Context.MODE_PRIVATE);
               theRole = prefs.getString(ApplicationConstants.LEVEL_ID, "");
                bindingXml();
               setLocationBuilder();
                getDataNews();
                }


            private void bindingXml(){
                mDemoSlider = (SliderLayout)findViewById(R.id.slider);
                listProgram = new ArrayList<Program>();
                v_text=(TextView)findViewById(R.id.mainmenu_volunteer_text);
                v_desc=(TextView)findViewById(R.id.mainmenu_volunteer_description);
                L_volunteer=(LinearLayout)findViewById(R.id.layout_1);
                L_program=(LinearLayout)findViewById(R.id.layout_2);
                L_map=(LinearLayout)findViewById(R.id.layout_3);
                icon_relawan=(ImageButton) findViewById(R.id.mainmenu_icon_relawan);
                icon_program=(ImageButton) findViewById(R.id.mainmenu_icon_program);
                icon_map=(ImageButton) findViewById(R.id.mainmenu_map);
                icon_relawan.setImageDrawable(new IconicsDrawable(this)
                        .icon(GoogleMaterial.Icon.gmd_pan_tool)
                        .color(context.getResources().getColor(R.color.white))
                        .sizeDp(50));
                icon_program.setImageDrawable(new IconicsDrawable(this)
                        .icon(GoogleMaterial.Icon.gmd_event_available)
                        .color(context.getResources().getColor(R.color.white))
                        .sizeDp(50));
                icon_map.setImageDrawable(new IconicsDrawable(this)
                        .icon(GoogleMaterial.Icon.gmd_pin_drop)
                        .color(context.getResources().getColor(R.color.white))
                        .sizeDp(50));
                if (theRole.contains("2")){
                    v_text.setText("You Have Become Volunteer");
                    v_desc.setText("see your profile");
                    icon_relawan.setImageDrawable(new IconicsDrawable(this)
                            .icon(GoogleMaterial.Icon.gmd_face)
                            .color(context.getResources().getColor(R.color.white))
                            .sizeDp(50));
                }

                L_volunteer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i;
                        if (theRole.contains("2")){
                            i = new Intent(MainMenu.this, ProfileRelawan.class);
                            startActivity(i);
                            finish();
                        }else {
                            GuestMenu gm=new GuestMenu();
                            gm.checkstatusdaftar(context);
                        }
                    }
                });
                L_program.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i;
                        if (theRole.contains("2")){
                            i = new Intent(MainMenu.this, Relawan_Program.class);
                            startActivity(i);
                            finish();
                        }else {
                            i = new Intent(MainMenu.this, GuestListProgram.class);
                            startActivity(i);
                            finish();
                        }
                    }
                });
                L_map.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i;
                        if (theRole.contains("2")){
                            i = new Intent(MainMenu.this, RelawanMenu.class);
                            startActivity(i);
                            finish();
                        }else {
                            i = new Intent(MainMenu.this, GuestMenu.class);
                            startActivity(i);
                            finish();
                        }
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
                      currentLatitude = mLocation.getLatitude();
                     currentLongitude = mLocation.getLongitude();
                     currentPoint = new GeoPoint(currentLatitude, currentLongitude);
                     storeLastLocation(getApplicationContext(), String.valueOf(currentLatitude),String.valueOf(currentLongitude));
                     isFirstZoom = true;
                 } else {
                     LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                   //  Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
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
             public void onLocationChanged(Location location) {
                 currentLatitude = location.getLatitude();
                 currentLongitude = location.getLongitude();
                 currentPoint = new GeoPoint(currentLatitude, currentLongitude);
                 storeLastLocation(getApplicationContext(), String.valueOf(currentLatitude),String.valueOf(currentLongitude));
                 if(isFirstZoom == false){
                     isFirstZoom = true;
                 }
             }

             @Override
             protected void onResume() {
                 super.onResume();

                 //Now lets connect to the API
                 if(mGoogleApiClient.isConnected() == false)
                     mGoogleApiClient.connect();

             }

             @Override
             protected  void onDestroy(){
                 super.onDestroy();
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


             private void  sliderInit(){

                 for (int i=0;i<listProgram.size();i++){
                     TextSliderView textSliderView = new TextSliderView(this);
                     // initialize a SliderLayout
                     textSliderView
                             .description(listProgram.get(i).getNamaProgram().toUpperCase())
                             .image(listProgram.get(i).getPathfoto())
                             .setScaleType(BaseSliderView.ScaleType.Fit)
                             .setOnSliderClickListener(this);

                     //add your extra information
                     textSliderView.bundle(new Bundle());
                     textSliderView.getBundle()
                             .putInt("position",i);

                     mDemoSlider.addSlider(textSliderView);

                 }


                 mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
                 mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                 mDemoSlider.setCustomAnimation(new DescriptionAnimation());
                 mDemoSlider.setDuration(4000);
                 mDemoSlider.addOnPageChangeListener(this);

             }
             private void getDataNews(){

                 // Make RESTful webservice call using AsyncHttpClient object
                 AsyncHttpClient client = new AsyncHttpClient();
                 client.get(ApplicationConstants.API_GET_URGENT_NEWS,
                         new AsyncHttpResponseHandler() {
                             // When the response returned by REST has Http
                             // response code '200'
                             @Override
                             public void onSuccess(String response) {
                                 Log.i("response data : ", response);

                                 try {
                                     JSONObject jObj = new JSONObject(response);
                                     boolean status = jObj.getBoolean("status");
                                     if (status) {
                                         JSONArray berita= jObj.getJSONArray("berita");

                                         for (int i=0;i<berita.length();i++){
                                             JSONObject abc=berita.getJSONObject(i);
                                             Program p = new Program();
                                             p.setIdProgram(abc.getInt("id_program"));
                                             p.setNamaProgram(abc.getString("nama_program"));
                                             p.setMulai(abc.getString("mulai"));
                                             p.setAkhir(abc.getString("akhir"));
                                             p.setStatus(abc.getInt(("status")));
                                             p.setSupervisor(abc.getString("supervisor"));
                                             p.setDeskripsi(abc.getString("deskripsi"));
                                             p.setLokasiProgram(abc.getString("lokasi_program"));
                                             p.setLatitude(abc.getString("latitude"));
                                             p.setLongitude(abc.getString("longitude"));
                                             p.setKeterangan(abc.getString("keterangan"));
                                             p.setPathfoto(abc.getString("main_image"));
                                             listProgram.add(p);
                                         }
                                        sliderInit();

                                     } else {


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

                             }
                         });

             }
             @Override
             protected void onStop() {
                 // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
                 mDemoSlider.stopAutoCycle();
                 if (mGoogleApiClient.isConnected()) {
                     mGoogleApiClient.disconnect();
                 }
                 super.onStop();
             }

             @Override
             public void onSliderClick(BaseSliderView slider) {
                 Intent intent = new Intent(context, Detailmateri.class);
                 intent.putExtra("program", listProgram.get(slider.getBundle().getInt("position")));
                 context.startActivity(intent);
             }

             @Override
             public boolean onCreateOptionsMenu(Menu menu) {
                 // Inflate the menu; this adds items to the action bar if it is present.
                 getMenuInflater().inflate(R.menu.menu_main, menu);
                 return true;
             }

             @Override
             public boolean onOptionsItemSelected(MenuItem item) {
                 // Handle action bar item clicks here. The action bar will
                 // automatically handle clicks on the Home/Up button, so long
                 // as you specify a parent activity in AndroidManifest.xml.
                 int id = item.getItemId();
                 Intent intent;
                 //noinspection SimplifiableIfStatement
                 if (id == R.id.logout) {
                     //--- logout
                     context.getSharedPreferences("UserDetails",
                             Context.MODE_PRIVATE).edit().clear().commit();
                     intent = new Intent(context, Login.class);
                     context.startActivity(intent);
                     finish();
                     return true;
                 }

                 return super.onOptionsItemSelected(item);
             }

             @Override
             public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

             @Override
             public void onPageSelected(int position) {
                 Log.d("Slider Demo", "Page Changed: " + position);
             }

             @Override
             public void onPageScrollStateChanged(int state) {}
             @Override
             public void onBackPressed() {

                 finish();
             }
}
