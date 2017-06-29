package pptik.startup.ghvmobile.User_Admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.ionicons_typeface_library.Ionicons;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pptik.startup.ghvmobile.EditProgram;
import pptik.startup.ghvmobile.ImageGallery;
import pptik.startup.ghvmobile.R;
import pptik.startup.ghvmobile.Support.DataProgram;
import pptik.startup.ghvmobile.Support.DataProgram2;
import pptik.startup.ghvmobile.Setup.ApplicationConstants;
import pptik.startup.ghvmobile.Utilities.PictureFormatTransform;

/**
 * Created by GIGABYTE on 20/06/2016.
 */
public class ApprovalProgramDetail  extends AppCompatActivity {
    private DataProgram dp;
    private DataProgram2 dp2;
    private int id_program,status_program;
    private TextView nama,awal,akhir,status,supervisor,lokasi,keterangan,deskripsi;
    private ImageView imageView;
    private FloatingActionButton fabGallery;
    SharedPreferences prefs;
    private ProgressDialog pDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_approval_program_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Detail Program");
        prefs = getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);
        nama = (TextView) findViewById(R.id.detail_program_nama);
        awal = (TextView) findViewById(R.id.detail_program_awal);
        akhir=(TextView) findViewById(R.id.detail_program_akhir);
        supervisor=(TextView) findViewById(R.id.detail_program_supervisor);
        lokasi=(TextView) findViewById(R.id.detail_program_lokasi);
        keterangan=(TextView) findViewById(R.id.detail_program_keterangan);
        deskripsi=(TextView) findViewById(R.id.detail_program_deskripsi);
        imageView=(ImageView)findViewById(R.id.detailapprovalimageview);
        fabGallery=(FloatingActionButton)findViewById(R.id.fabGallery);
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            status_program=extras.getInt("status");

        }

         if (status_program==1){
            dp= (DataProgram) extras.getSerializable("detail");
            id_program=dp.getIdProgram();

        }else {
            dp= (DataProgram) extras.getSerializable("detail");
            id_program=dp.getIdProgram();

        }
        fabGallery.setImageBitmap(PictureFormatTransform.drawableToBitmap(new IconicsDrawable(this)
                .icon(Ionicons.Icon.ion_images)
                .color(this.getResources().getColor(R.color.white))
                .sizeDp(60)));
        fabGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i;
                i = new Intent(ApprovalProgramDetail.this, ImageGallery.class);
                i.putExtra("id_program",id_program);
                startActivity(i);
                finish();
            }
        });
        ambildataprogram();
    }
    private void deleteprogram(){
        pDialog = ProgressDialog.show(ApprovalProgramDetail.this, "Menghapus Program", "Harap Tunggu");
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(ApplicationConstants.API_DELETE_BERITA+id_program,
                new AsyncHttpResponseHandler() {
                    // When the response returned by REST has Http
                    // response code '200'
                    @Override
                    public void onSuccess(String response) {

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean status = jObj.getBoolean("status");
                            if (status) {
                                pDialog.dismiss();
                                finish();
                            } else {
                                pDialog.dismiss();
                                String errorMsg = jObj.getString("msg");
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
                            /*Toast.makeText(
                                    getApplicationContext(),
                                    "Unexpected Error occcured! [Most common Error: Device might "
                                            + "not be connected to Internet or remote server is not up and running], check for other errors as well",
                                    Toast.LENGTH_LONG).show();*/
                        }
                    }
                });
    }
    private void approveprogram(final int no){
        pDialog = ProgressDialog.show(ApprovalProgramDetail.this, "Mengirim Data", "Harap Tunggu");
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(ApplicationConstants.API_APPROVAL_BERITA+id_program+"/"+no,
                new AsyncHttpResponseHandler() {
                    // When the response returned by REST has Http
                    // response code '200'
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean status = jObj.getBoolean("status");
                            if (status) {
                                pDialog.dismiss();
                                Intent i;
                                if (no==2){
                                    i = new Intent(ApprovalProgramDetail.this, DisapprovProgram.class);
                                    startActivity(i);

                                }else{
                                    i = new Intent(ApprovalProgramDetail.this, ApprovalProgram.class);
                                    startActivity(i);

                                }

                                finish();
                            } else {
                                pDialog.dismiss();
                                String errorMsg = jObj.getString("msg");
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

    private void ambildataprogram(){
        pDialog = ProgressDialog.show(ApprovalProgramDetail.this, "Memuat Program", "Harap Tunggu");
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(ApplicationConstants.API_GET_PROGRAM_BY_ID+id_program,
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
                                    nama.setText(abc.getString("nama_program"));
                                    awal.setText(abc.getString("mulai"));
                                    akhir.setText(abc.getString("akhir"));
                                    supervisor.setText(abc.getString("supervisor"));
                                    lokasi.setText(abc.getString("lokasi_program"));
                                    keterangan.setText(abc.getString("keterangan"));
                                    deskripsi.setText(abc.getString("deskripsi"));
                                    if (!abc.getString("main_image").isEmpty() ||abc.getString("main_image")!=null){
                                        Picasso.with(ApprovalProgramDetail.this)
                                                .load(abc.getString("main_image"))
                                                .fit()
                                                .into(imageView);
                                    }

                                }


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
        // Inflate the menu; this adds items to the action bar if it is present.

        if (status_program==1){
            MenuItem approve=menu.add(Menu.NONE,1,1,"Disapprove");
            approve.setIcon(new IconicsDrawable(this)
                    .icon(GoogleMaterial.Icon.gmd_pin_drop)
                    .color(ApprovalProgramDetail.this.getResources().getColor(R.color.black))
                    .sizeDp(5));

            approve.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        }else {
            MenuItem approve=menu.add(Menu.NONE,2,1,"Approve");
            approve.setIcon(new IconicsDrawable(this)
                    .icon(GoogleMaterial.Icon.gmd_pin_drop)
                    .color(ApprovalProgramDetail.this.getResources().getColor(R.color.black))
                    .sizeDp(5));
            approve.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        }
        MenuItem edit=menu.add(Menu.NONE,3,2,"Edit");
        edit.setIcon(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_pin_drop)
                .color(ApprovalProgramDetail.this.getResources().getColor(R.color.black))
                .sizeDp(5));
        edit.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        MenuItem delete=menu.add(Menu.NONE,4,3,"Delete");
        delete.setIcon(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_pin_drop)
                .color(ApprovalProgramDetail.this.getResources().getColor(R.color.black))
                .sizeDp(5));

        delete.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        Intent intent;
        switch(id){
            case 1:approveprogram(2) ;
                break;
            case 2: approveprogram(1) ;
                break;
            case 3:  intent = new Intent(ApprovalProgramDetail.this, EditProgram.class);
                intent.putExtra("id_program",id_program);
                startActivity(intent);
                finish();
                break;
            case 4:deleteprogram();

                break;
            case android.R.id.home:
                intent = new Intent(ApprovalProgramDetail.this, ApprovalProgram.class);
                startActivity(intent);
                finish();
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void  onDestroy(){
        super.onDestroy();
    }
}
