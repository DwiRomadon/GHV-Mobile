package pptik.startup.ghvmobile.User_Admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pptik.startup.ghvmobile.R;
import pptik.startup.ghvmobile.Support.DataUser2;
import pptik.startup.ghvmobile.Support.LRB_costumAdapter;
import pptik.startup.ghvmobile.Setup.ApplicationConstants;

/**
 * Created by GIGABYTE on 15/06/2016.
 */
public class ApprovalRelawan extends AppCompatActivity {
    private ListView lv;
    private ArrayList<DataUser2> listDataUser;

    private ProgressDialog pDialog;
    private Context applicationContext;
    private LRB_costumAdapter mAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.approval_relawan_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("GHV Mobile");
        applicationContext = getApplicationContext();
        lv=(ListView) findViewById(R.id.lv_belum_diapprove);
        listDataUser = new ArrayList<DataUser2>();

    //    CollectingMateri(this);

    }


    private void CollectingMateri(final ApprovalRelawan ar) {
        pDialog = ProgressDialog.show(ApprovalRelawan.this, null, "Memuat Data..");

        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(ApplicationConstants.API_GET_LIST_BELUM_DI_APPROVE,
                new AsyncHttpResponseHandler() {
                    // When the response returned by REST has Http
                    // response code '200'
                    @Override
                    public void onSuccess(String response) {
                        listDataUser.clear();

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean status = jObj.getBoolean("status");
                            if (status) {
                                JSONArray listuser= jObj.getJSONArray("listuser");
                                  for (int i=0;i<listuser.length();i++){
                                    JSONObject abc=listuser.getJSONObject(i);
                                    DataUser2 d = new DataUser2();
                                    d.setIdUser(abc.getInt("id_user"));
                                    d.setEmail(abc.getString("email"));
                                    d.setNamaLengkap(abc.getString("nama_lengkap"));
                                    String[] splitdate=abc.getString("created_at").split("\\s+");
                                    d.set_joindate(splitdate[0].toString());
                                    listDataUser.add(d);
                                }
                                mAdapter = new LRB_costumAdapter(ar, listDataUser);
                                lv.setAdapter(null);
                                lv.setAdapter(mAdapter);
                                pDialog.dismiss();
                            } else {
                                pDialog.dismiss();
                                String errorMsg = jObj.getString("Message");
                                Toast.makeText(getApplicationContext(),
                                        errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // Hide Progress Dialog
                        pDialog.hide();
                        if (pDialog != null) {
                            pDialog.dismiss();
                        }
                    }

                    // When the response returned by REST has Http
                    // response code other than '200' such as '404',
                    // '500' or '403' etc
                    @Override
                    public void onFailure(int statusCode, Throwable error,
                                          String content) {
                        // Hide Progress Dialog
                        pDialog.hide();
                        if (pDialog != null) {
                            pDialog.dismiss();
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
                           /* Toast.makeText(
                                    getApplicationContext(),
                                    "Unexpected Error occcured! [Most common Error: Device might "
                                            + "not be connected to Internet or remote server is not up and running], check for other errors as well",
                                    Toast.LENGTH_LONG).show();*/
                        }
                    }
                });

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case android.R.id.home:
                intent = new Intent(applicationContext, Admin.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    @Override
    protected void onResume(){
        super.onResume();
        CollectingMateri(this);
    }
    @Override
    public void onBackPressed() {
        Intent intent;
        intent = new Intent(applicationContext, Admin.class);
        startActivity(intent);
        finish();
    }
}
