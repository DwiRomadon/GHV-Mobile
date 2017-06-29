package pptik.startup.ghvmobile;

/**
 * Created by edo on 6/11/2016.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.ionicons_typeface_library.Ionicons;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import pptik.startup.ghvmobile.Connection.IConnectionResponseHandler;
import pptik.startup.ghvmobile.Connection.RequestRest;
import pptik.startup.ghvmobile.Setup.ApplicationConstants;
import pptik.startup.ghvmobile.User_Admin.Admin;

public class Login extends AppCompatActivity {
    private static final int REQUEST_READ_CONTACTS = 0;

    RequestParams params = new RequestParams();

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView,retypePassword;
    private View mProgressView;
    private TextView text1,fab ;

    private ProgressDialog prgDialog;
    private RadioGroup role;
    private RadioButton inputRole;
    private Button mEmailSignInButton;
    private String deviceid;

    private LinearLayout li1;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private int statusLoginOrRegister=0;

    GoogleCloudMessaging gcmObj;
    SharedPreferences prefs;
    private String email="";
    String regId;
    public  String Facebook_Email=null;

    private LoginButton loginButton;
    private CallbackManager callbackManager;

    private  int loginTag=0;
    private Context context;
    private ImageButton IB_fb,IB_google;
    private FrameLayout fl_google,fl_fb;

    // deklarasi
    private SignInButton btnSignIn;
    private static final String TAG = Login.class.getSimpleName();
    private static final int RC_SIGN_IN = 007;

    private GoogleApiClient mGoogleApiClient;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.login_activity);
        context = this;
        prefs = getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);
        retypePassword=(EditText)findViewById(R.id.passwordretype) ;
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        text1=(TextView)findViewById(R.id.text);
        IB_fb=(ImageButton) findViewById(R.id.IB_fb);
        IB_google=(ImageButton) findViewById(R.id.IB_google);
        IB_fb.setImageDrawable(new IconicsDrawable(this)
                .icon(Ionicons.Icon.ion_social_facebook_outline)
                .color(context.getResources().getColor(R.color.white))
                .sizeDp(50));
        IB_google.setImageDrawable(new IconicsDrawable(this)
                .icon(Ionicons.Icon.ion_social_google_outline)
                .color(context.getResources().getColor(R.color.white))
                .sizeDp(50));

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, null)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        fab = (TextView) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /*Intent i = new Intent(Login.this, Signup.class);
                startActivity(i);*/
                if (statusLoginOrRegister==0){
                    li1.setEnabled(true);
                    li1.setVisibility(View.VISIBLE);
                    mEmailSignInButton.setText("Register");
                    text1.setText("Have an Account? ");
                    fab.setText("Login");

                    statusLoginOrRegister=1;
                }else {
                    li1.setEnabled(false);
                    li1.setVisibility(View.GONE);
                    mEmailSignInButton.setText("Login");
                    text1.setText("Dont have Account yet? ");
                    fab.setText("Register");

                    statusLoginOrRegister=0;
                }

            }
        });


        String registrationId = prefs.getString(ApplicationConstants.REG_ID, "");
        String theRole = prefs.getString(ApplicationConstants.LEVEL_ID, "");
        if (!TextUtils.isEmpty(registrationId)) {
           if(theRole.contains("1")){
                Intent i = new Intent(Login.this, Admin.class);
                i.putExtra("regId", registrationId);
                startActivity(i);
                finish();
            }else {
                Intent i = new Intent(Login.this, MainMenu.class);
                i.putExtra("regId", registrationId);
                startActivity(i);
                finish();
            }

        }

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {

                    return true;
                }
                return false;
            }
        });



        prgDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);

      /*  TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        deviceid = tm.getDeviceId();
*/
        li1=(LinearLayout)findViewById(R.id.li4);

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
               // attemptLogin();

                if (statusLoginOrRegister==0){
                    if(mEmailView.getText().toString().equals("") || mPasswordView.getText().toString().equals("")){
                        Snackbar.make(view, "Please Input Email and Password", Snackbar.LENGTH_LONG).show();
                    }else {
                        if (checkPlayServices())
                            loginTag=1;
                            registerInBackground();
                    }
                }else {
                    attemptRegister();
                }


            }
        });
        mProgressView = findViewById(R.id.login_progress);

        // login facebook
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.btn_fb_login);
        loginButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday"));
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {

                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("Main", response.toString());
                                if (checkPlayServices())
                                    loginTag=2;
                                try {
                                    email=object.getString("email").trim();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                    registerInBackground();
                               // setProfileToView(object);

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,age_range,locale, birthday");
                request.setParameters(parameters);
                request.executeAsync();
                // goMainScreen();

            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(),"Cancel Login", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "Login Error", Toast.LENGTH_SHORT).show();
            }
        });

        fl_google=(FrameLayout)findViewById(R.id.FrameLayout2);
        fl_fb=(FrameLayout)findViewById(R.id.FrameLayout1);
        fl_fb.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                loginButton.performClick();
            }
        });
        btnSignIn=(SignInButton)findViewById(R.id.btn_sign_in);
        btnSignIn.setScopes(gso.getScopeArray());
        btnSignIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Snackbar.make(v, "Tes Login by google", Snackbar.LENGTH_LONG).show();

                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);

            }
        });

        fl_google.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.toString());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
    
            if (checkPlayServices()) {
                loginTag = 3;
                email = acct.getEmail();
                registerInBackground();
            } else {
                Toast.makeText(this,"Please Update Your Play Service",Toast.LENGTH_LONG).show();
            }
        } else {
            // Signed out, show unauthenticated UI.

            Toast.makeText(this,"Failed",Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }


    private void attemptRegister() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        retypePassword.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String retypepassword = retypePassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        if (!retypepassword.equals(password)) {
            retypePassword.setError(getString(R.string.error_invalid_password2));
            focusView =retypePassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            // test connection
            //testCon();
            registerUser(email,password,"1234");
        }
    }

    //----------- regsiter gcm
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                prgDialog = ProgressDialog.show(Login.this, null, "Memuat...", true);
            }


            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcmObj == null) {
                        gcmObj = GoogleCloudMessaging
                                .getInstance(Login.this);
                    }
                    regId = gcmObj
                            .register(ApplicationConstants.GOOGLE_PROJ_ID);
                    msg = "Registration ID :" + regId;

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                if (!TextUtils.isEmpty(regId)) {
                    //    storeRegIdinSharedPref(applicationContext, regId, email);
                    prgDialog.dismiss();
                    //    registerUser(name, email, gender, birth, phone, password, role, ojegNumber, idktp, regId);
                    if (loginTag==1){
                        attemptLogin();

                    }else {
                        loginviasocmed();
                    }

                    //    Toast.makeText(applicationContext, "Registered with GCM Server successfully.\n\n" + msg, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Login.this, "Login Failed.\nPlease Check Your Internet Connection", Toast.LENGTH_LONG).show();
                    prgDialog.dismiss();
                }
            }
        }.execute(null, null, null);
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(
                        Login.this,
                        "Please Update Your Play Service",
                        Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        } else {

        }
        return true;
    }
    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            // test connection
            //testCon();
            String testregid="test";
            doLogin(email, password, regId);
        }
    }
    private void loginviasocmed() {
        final ProgressDialog  dialog = ProgressDialog.show(Login.this, null, "Memuat...", true);
        RequestRest req = new RequestRest(Login.this, new IConnectionResponseHandler(){
            @Override
            public void OnSuccessArray(JSONArray result){
                dialog.dismiss();
            }

            @Override
            public void onSuccessJSONObject(String result){
                try {
                    JSONObject obj = new JSONObject(result);
                    boolean status=obj.getBoolean("status");
                    dialog.dismiss();
                    if (status){

                        JSONObject user = obj.getJSONObject("user");
                        String Email = user.getString("email");
                        String gcmid = user.getString("gcm_id");
                        int id_user = user.getInt("id_user");
                        String Level = user.getString("level");
                        String pathfoto=user.getString("path_foto");
                        if(Level.equals("")){
                            Level = "3";
                        }
                        //store to sharedpreference
                        storeRegIdinSharedPref(getApplicationContext(), gcmid, Email, Level,id_user,pathfoto );
                        String theRole = Level;

                        if(theRole.contains("1")){
                            Intent i = new Intent(Login.this, Admin.class);
                            i.putExtra("regId", gcmid);
                            startActivity(i);
                            finish();
                        }else {
                            Intent i = new Intent(Login.this, MainMenu.class);
                            i.putExtra("regId", gcmid);
                            startActivity(i);
                            finish();
                        }

                    }else {
                        Toast.makeText(Login.this, "Email Sudah Digunakan", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e){

                }


            }

            @Override
            public void onFailure(String e){
                Log.i("Test", e);
                dialog.dismiss();
            }

            @Override
            public void onSuccessJSONArray(String result){
                dialog.dismiss();
            }
        });

        if (loginTag==2){
            req.loginviafb(email,regId);
        }else {
            req.loginviagoogle(email,regId);
        }

    }
    private void registerUser(String email, String password, String regId) {
        final ProgressDialog  dialog = ProgressDialog.show(Login.this, null, "Memuat...", true);
        RequestRest req = new RequestRest(Login.this, new IConnectionResponseHandler(){
            @Override
            public void OnSuccessArray(JSONArray result){
                dialog.dismiss();
            }

            @Override
            public void onSuccessJSONObject(String result){
                try {
                    JSONObject obj = new JSONObject(result);
                    boolean status=obj.getBoolean("status");
                    dialog.dismiss();
                    if (status){

                        Toast.makeText(Login.this, "Success Create Account, Please Login", Toast.LENGTH_LONG).show();
                        li1.setEnabled(false);
                        li1.setVisibility(View.GONE);
                        mEmailSignInButton.setText("Login");
                        text1.setText("Dont have Account yet? ");
                        fab.setText("Register");

                        statusLoginOrRegister=0;

                    }else {
                        Toast.makeText(Login.this, "Email Sudah Digunakan", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e){

                }


            }

            @Override
            public void onFailure(String e){
                Log.i("Test", e);
                dialog.dismiss();
            }

            @Override
            public void onSuccessJSONArray(String result){
                dialog.dismiss();
            }
        });


        req.registerUser(email, password, "12345");
    }
    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private void doLogin(final String email, final String password, final  String regid){
        prgDialog.show();
        params.put("email", email);
        params.put("password", password);
        params.put("gcm_id", regid);

        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(ApplicationConstants.API_LOGIN, params,
                new AsyncHttpResponseHandler() {
                    // When the response returned by REST has Http
                    // response code '200'
                    @Override
                    public void onSuccess(String response) {

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean status = jObj.getBoolean("status");
                            if (status) {
                                JSONObject user = jObj.getJSONObject("user");
                                String Email = user.getString("email");
                                String gcmid = user.getString("gcm_id");
                                int id_user = user.getInt("id_user");
                                String Level = user.getString("level");
                                String pathfoto=user.getString("path_foto");
                                if(Level.equals("")){
                                    Level = "3";
                                }
                                //store to sharedpreference
                                storeRegIdinSharedPref(getApplicationContext(), gcmid, Email, Level,id_user,pathfoto );
                                String theRole = Level;

                                if(theRole.contains("1")){
                                    Intent i = new Intent(Login.this, Admin.class);
                                    i.putExtra("regId", gcmid);
                                    startActivity(i);
                                    finish();
                                }else {
                                    Intent i = new Intent(Login.this, MainMenu.class);
                                    i.putExtra("regId", gcmid);
                                    startActivity(i);
                                    finish();
                                }

                            } else {

                                String errorMsg = jObj.getString("msg");
                                Toast.makeText(getApplicationContext(),
                                        errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // Hide Progress Dialog
                        prgDialog.hide();
                        if (prgDialog != null) {
                            prgDialog.dismiss();
                        }
                    }

                    // When the response returned by REST has Http
                    // response code other than '200' such as '404',
                    // '500' or '403' etc
                    @Override
                    public void onFailure(int statusCode, Throwable error,
                                          String content) {
                        // Hide Progress Dialog
                        prgDialog.hide();
                        if (prgDialog != null) {
                            prgDialog.dismiss();
                        }
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
                          /*  Toast.makeText(
                                    getApplicationContext(),
                                    "Unexpected Error occcured! [Most common Error: Device might "
                                            + "not be connected to Internet or remote server is not up and running], check for other errors as well",
                                    Toast.LENGTH_LONG).show();*/
                        }
                    }
                });
    }
    private void storeRegIdinSharedPref(Context context, String regId,
                                        String emailID, String role, int id,String pathfotouser) {

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ApplicationConstants.REG_ID, regId);
        editor.putString(ApplicationConstants.EMAIL_ID, emailID);
        editor.putString(ApplicationConstants.LEVEL_ID, role);
        editor.putInt(ApplicationConstants.USER_ID, id);
        editor.putString(ApplicationConstants.PATH_FOTO_USER, pathfotouser);
        editor.putInt(ApplicationConstants.LOGIN_TAG_BY,loginTag);
        editor.commit();
    }
}
