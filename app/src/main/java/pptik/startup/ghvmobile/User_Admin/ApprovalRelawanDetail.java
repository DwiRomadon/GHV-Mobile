package pptik.startup.ghvmobile.User_Admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

import pptik.startup.ghvmobile.R;
import pptik.startup.ghvmobile.Support.DataUser;
import pptik.startup.ghvmobile.Support.DataUser2;
import pptik.startup.ghvmobile.Setup.ApplicationConstants;

/**
 * Created by GIGABYTE on 17/06/2016.
 */
public class ApprovalRelawanDetail extends AppCompatActivity {
    private DataUser du;
    private DataUser2 du2;
    private Button btnApprove;
    private TextView namalengkap,namapanggilan,jeniskelamin,golongandarah,tempatlahir,tanggallahir,
    agama,statuspernikahan,jumlahanak,jenisidentitas,nomoridentitas,kewarganegaraan,alamat,kota,provinsi,
    kodepos,teleponrumah,handphone,aktivitaspekerjaan,namakerabat,nohpkerabat,pendidikanterakhir,minat,
    keahlian,pengalamanorganisasi,motivasi;
    private int id_user;
    private int status;
    private ImageView fotoProfile;
    private String pathfoto;

    SharedPreferences prefs;
    private ProgressDialog pDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_approval_relawan_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Detail Relawan");
        prefs = getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);
        fotoProfile=(ImageView)findViewById(R.id.picture_path_aspirasi);
        namalengkap=(TextView) findViewById(R.id.nama_lengkap);
        namapanggilan=(TextView) findViewById(R.id.nama_panggilan);
        jeniskelamin=(TextView) findViewById(R.id.jenis_kelamin);
        golongandarah=(TextView) findViewById(R.id.golongan_darah);
        tempatlahir=(TextView) findViewById(R.id.tempat_lahir);
        tanggallahir=(TextView) findViewById(R.id.tanggal_lahir);
        agama=(TextView) findViewById(R.id.agama);
        statuspernikahan=(TextView) findViewById(R.id.status_pernikahan);
        jumlahanak=(TextView) findViewById(R.id.jumlah_anak);
        jenisidentitas=(TextView) findViewById(R.id.jenis_identitas);
        nomoridentitas=(TextView) findViewById(R.id.nomor_identitas);
        kewarganegaraan=(TextView) findViewById(R.id.kewarganegaraan);
        alamat=(TextView) findViewById(R.id.alamat);
        kota    =(TextView) findViewById(R.id.kota);
        provinsi=(TextView) findViewById(R.id.provinsi);
        kodepos=(TextView) findViewById(R.id.kode_pos);
        teleponrumah=(TextView) findViewById(R.id.tlpn_rumah);
        handphone=(TextView) findViewById(R.id.hp);
        aktivitaspekerjaan=(TextView) findViewById(R.id.aktivitas_pekerjaan);
        namakerabat=(TextView) findViewById(R.id.nama_kerabat);
        nohpkerabat=(TextView) findViewById(R.id.hp_kerabat);
        pendidikanterakhir=(TextView) findViewById(R.id.pendidikan_terakhir);
        minat=(TextView) findViewById(R.id.minat_atau_hobi);
        keahlian=(TextView) findViewById(R.id.keahlian);
        pengalamanorganisasi=(TextView) findViewById(R.id.pengalaman_organisasi);
        motivasi=(TextView) findViewById(R.id.motifasi);
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            status=extras.getInt("status");

        }

        btnApprove = (Button) findViewById(R.id.btnapproverelawan);
        if (status==1){
            du= (DataUser) extras.getSerializable("detail");
            id_user=du.getIdUser();
            btnApprove.setText("DisApprove");
            btnApprove.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    approverelawan(3);
                }
            });
        }else {
            du2= (DataUser2) extras.getSerializable("detail");
            id_user=du2.getIdUser();
            btnApprove.setText("Approve");
            btnApprove.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    approverelawan(2);
                }
            });
        }
        ambildatauser();

    }
    private void approverelawan(int no){
        pDialog = ProgressDialog.show(ApprovalRelawanDetail.this, null, "Memuat Data..");
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(ApplicationConstants.API_APPROVAL+id_user+"/"+no,
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
                          /*  Toast.makeText(
                                    getApplicationContext(),
                                    "Unexpected Error occcured! [Most common Error: Device might "
                                            + "not be connected to Internet or remote server is not up and running], check for other errors as well",
                                    Toast.LENGTH_LONG).show();*/
                        }
                    }
                });
    }

    private void ambildatauser(){
        pDialog = ProgressDialog.show(ApprovalRelawanDetail.this, "Memuat Profile", "Harap Tunggu");
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(ApplicationConstants.API_GET_RELAWAN_BY_ID+id_user,
                new AsyncHttpResponseHandler() {
                    // When the response returned by REST has Http
                    // response code '200'
                    @Override
                    public void onSuccess(String response) {

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean status = jObj.getBoolean("status");
                            if (status) {
                                JSONArray berita= jObj.getJSONArray("user");

                                for (int i=0;i<berita.length();i++){
                                    JSONObject abc=berita.getJSONObject(i);
                                    pathfoto=abc.getString("path_foto");
                                    new DownloadImageTask(fotoProfile)
                                            .execute(pathfoto);
                                    namalengkap.setText(abc.getString("nama_lengkap"));
                                    namapanggilan.setText(abc.getString("nama_panggilan"));
                                    if (abc.getInt("jk")==1){
                                        jeniskelamin.setText("Laki-laki");
                                    }else {
                                        jeniskelamin.setText("Perempuan");
                                    }

                                    if (abc.getInt("gol_darah")==1){
                                        golongandarah.setText("O");
                                    }else if (abc.getInt("gol_darah")==2){
                                        golongandarah.setText("A");
                                    }else if (abc.getInt("gol_darah")==3){
                                        golongandarah.setText("B");
                                    }else {
                                        golongandarah.setText("AB");
                                    }
                                    tempatlahir.setText(abc.getString("tempat_lahir"));
                                    tanggallahir.setText(abc.getString("tgl_lahir"));
                                    agama.setText(abc.getString("agama"));

                                    if (abc.getInt("status_perkawinan")==1){
                                        statuspernikahan.setText("Menikah");
                                    }else {
                                        statuspernikahan.setText("Belum Menikah");
                                    }
                                    jumlahanak.setText(abc.getString("jumlah_anak"));
                                    if (abc.getInt("jenis_identitas")==1){
                                        jenisidentitas.setText("KTP");
                                    }else {
                                        jenisidentitas.setText("SIM");
                                    }
                                    nomoridentitas.setText(abc.getString("no_identitas"));
                                    if (abc.getInt("kewarganegaraan")==1){
                                        kewarganegaraan.setText("Warga Negara Indonesia");
                                    }else {
                                        kewarganegaraan.setText("Warga Negara Asing");
                                    }
                                    alamat.setText(abc.getString("alamat"));
                                    kota.setText(abc.getString("kota"));
                                    provinsi.setText(abc.getString("provinsi"));
                                    kodepos.setText(abc.getString("kode_pos"));
                                    teleponrumah.setText(abc.getString("telp_rumah"));
                                    handphone.setText(abc.getString("hp"));
                                    aktivitaspekerjaan.setText(abc.getString("pekerjaan"));
                                    namakerabat.setText(abc.getString("nama_kerabat"));
                                    nohpkerabat.setText(abc.getString("hp_kerabat"));
                                    if (abc.getInt("pendidikan_terakhir")==1){
                                        pendidikanterakhir.setText("SD");
                                    }else if (abc.getInt("pendidikan_terakhir")==2){
                                        pendidikanterakhir.setText("SMP");
                                    }else if (abc.getInt("pendidikan_terakhir")==3){
                                        pendidikanterakhir.setText("SMA");
                                    }else if (abc.getInt("pendidikan_terakhir")==4){
                                        pendidikanterakhir.setText("S1");
                                    }else if (abc.getInt("pendidikan_terakhir")==5){
                                        pendidikanterakhir.setText("S2");
                                    }else {
                                        pendidikanterakhir.setText("S3");
                                    }
                                    minat.setText(abc.getString("minat"));
                                    keahlian.setText(abc.getString("keahlian"));
                                    pengalamanorganisasi.setText(abc.getString("pengalaman_organisasi"));
                                    motivasi.setText(abc.getString("motivasi"));
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
                           /* Toast.makeText(
                                    getApplicationContext(),
                                    "Unexpected Error occcured! [Most common Error: Device might "
                                            + "not be connected to Internet or remote server is not up and running], check for other errors as well",
                                    Toast.LENGTH_LONG).show();*/
                        }
                    }
                });
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
            fotoProfile.setImageBitmap(result);

        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case android.R.id.home:
                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void  onDestroy(){
        super.onDestroy();
    }

}
