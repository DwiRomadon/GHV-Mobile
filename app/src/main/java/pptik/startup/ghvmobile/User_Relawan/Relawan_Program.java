package pptik.startup.ghvmobile.User_Relawan;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.ionicons_typeface_library.Ionicons;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pptik.startup.ghvmobile.MainMenu;
import pptik.startup.ghvmobile.R;
import pptik.startup.ghvmobile.SubmitProgram;
import pptik.startup.ghvmobile.Support.CustomAdapter;
import pptik.startup.ghvmobile.Support.Program;
import pptik.startup.ghvmobile.Utilities.PictureFormatTransform;
import pptik.startup.ghvmobile.Setup.ApplicationConstants;

/**
 * Created by edo on 6/12/2016.
 */

public class Relawan_Program extends AppCompatActivity {

    private FloatingActionButton fabAddProgram;
    private ListView lv;
    private ArrayList<Program> listProgram;
    private ProgressDialog pDialog;
    private Context applicationContext;
    private Dialog addingClassDialog;
    private EditText inputCode;
    private CustomAdapter mAdapter;
    private Button bt;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.relawan_program_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        applicationContext = getApplicationContext();
        lv=(ListView) findViewById(R.id.listView_program);
        listProgram = new ArrayList<Program>();

        CollectingMateri(this);
        fabAddProgram=(FloatingActionButton)findViewById(R.id.fab_add_program);
        fabAddProgram.setImageBitmap(PictureFormatTransform.drawableToBitmap(new IconicsDrawable(this)
                .icon(Ionicons.Icon.ion_plus_circled)
                .color(this.getResources().getColor(R.color.colorPrimary))
                .sizeDp(60)));
        fabAddProgram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SubmitProgram.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void CollectingMateri(final Relawan_Program relawan) {
        pDialog = ProgressDialog.show(Relawan_Program.this, null, "Memuat data..");

        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(ApplicationConstants.API_GET_ALL_PROGRAM,
                new AsyncHttpResponseHandler() {
                    // When the response returned by REST has Http
                    // response code '200'
                    @Override
                    public void onSuccess(String response) {


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
                                    //  Log.i("idberita",abc.getString("id_program"));
                                }

                                mAdapter = new CustomAdapter(relawan, listProgram);
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

    public void onBackPressed() {
        Intent intent;
        intent = new Intent(applicationContext, RelawanMenu.class);
        startActivity(intent);
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case android.R.id.home:
                intent = new Intent(applicationContext, MainMenu.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
