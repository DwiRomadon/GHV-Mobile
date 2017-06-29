package pptik.startup.ghvmobile.User_Admin;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pptik.startup.ghvmobile.R;
import pptik.startup.ghvmobile.Support.DataProgram;
import pptik.startup.ghvmobile.Support.LPS_costumAdapter;
import pptik.startup.ghvmobile.Setup.ApplicationConstants;

/**
 * Created by GIGABYTE on 20/06/2016.
 */
public class DisapprovProgram extends AppCompatActivity {
    private ListView lv,lv2;
    private ArrayList<DataProgram> listDataProgram2;

    private ProgressDialog pDialog,pDialog2;
    private Context applicationContext;
    private Dialog addingClassDialog;
    private EditText inputCode;
    private LPS_costumAdapter mAdapter2;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dissaprove_program_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        applicationContext = getApplicationContext();
        lv2=(ListView) findViewById(R.id.lv_program_sudah_diapprove);
        listDataProgram2 = new ArrayList<DataProgram>();


     //   CollectingMateri2(this);
    }
    private void CollectingMateri2(final DisapprovProgram ap) {
        pDialog2 = ProgressDialog.show(DisapprovProgram.this, null, "Memuat Data..");

        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(ApplicationConstants.API_GET_PROGRAM_SUDAH_DI_APPROVE,
                new AsyncHttpResponseHandler() {
                    // When the response returned by REST has Http
                    // response code '200'
                    @Override
                    public void onSuccess(String response) {
                        Log.i("response data : ", response);
                        listDataProgram2.clear();

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean status = jObj.getBoolean("status");
                            if (status) {
                                JSONArray listuser= jObj.getJSONArray("listprogram");

                                for (int i=0;i<listuser.length();i++){
                                    JSONObject abc=listuser.getJSONObject(i);
                                    DataProgram d = new DataProgram();
                                    d.setIdProgram(abc.getInt("id_program"));
                                    d.setNamaProgram(abc.getString("nama_program"));
                                    d.setLokasiProgram(abc.getString("lokasi_program"));
                                    d.set_supervisor(abc.getString("supervisor"));
                                    d.set_tanggal(abc.getString("mulai"));
                                    listDataProgram2.add(d);
                                    //  Log.i("idberita",abc.getString("id_program"));
                                }

                                mAdapter2 = new LPS_costumAdapter(ap, listDataProgram2);
                                lv2.setAdapter(null);
                                lv2.setAdapter(mAdapter2);
                                pDialog2.dismiss();
                            } else {
                                pDialog2.dismiss();
                                String errorMsg = jObj.getString("Message");
                                Toast.makeText(getApplicationContext(),
                                        errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // Hide Progress Dialog
                        pDialog2.hide();
                        if (pDialog2 != null) {
                            pDialog2.dismiss();
                        }
                    }

                    // When the response returned by REST has Http
                    // response code other than '200' such as '404',
                    // '500' or '403' etc
                    @Override
                    public void onFailure(int statusCode, Throwable error,
                                          String content) {
                        // Hide Progress Dialog
                        pDialog2.hide();
                        if (pDialog2 != null) {
                            pDialog2.dismiss();
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
                            /*Toast.makeText(
                                    getApplicationContext(),
                                    "Unexpected Error occcured! [Most common Error: Device might "
                                            + "not be connected to Internet or remote server is not up and running], check for other errors as well",
                                    Toast.LENGTH_LONG).show();*/
                        }
                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_index, menu);
        return true;
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
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume(){
        super.onResume();
        CollectingMateri2(this);
    }
    @Override
    public void onBackPressed() {
        Intent intent;
        intent = new Intent(applicationContext, Admin.class);
        startActivity(intent);
        finish();
    }
}
