package pptik.startup.ghvmobile.User_Relawan;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import pptik.startup.ghvmobile.Connection.IConnectionResponseHandler;
import pptik.startup.ghvmobile.Connection.RequestRest;
import pptik.startup.ghvmobile.GetRole;
import pptik.startup.ghvmobile.MainMenu;
import pptik.startup.ghvmobile.R;
import pptik.startup.ghvmobile.Support.PhotoManager;
import pptik.startup.ghvmobile.User_Admin.Admin;
import pptik.startup.ghvmobile.User_Guest.GuestMenu;
import pptik.startup.ghvmobile.Setup.ApplicationConstants;
/**
 * Created by GIGABYTE on 15/06/2016.
 */
public class ProfileRelawan extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Button btnDaftar;
    private EditText namalengkap;
    private EditText namapanggilan;
    private RadioGroup gender;
    private RadioButton inputgender;
    private RadioGroup goldarah;
    private RadioButton inputgoldarah;
    private EditText tempatlahir;
    private EditText tanggallahir;
    private int year;
    private int month;
    private int day;
    private EditText agama;
    private RadioGroup statuskawin;
    private RadioButton inputstatuskawin;
    private EditText jumlahanak;
    private int jumlah_anak=0;
    private RadioGroup jidentitas;
    private RadioButton inputjidentitas;
    private EditText noidentitas;
    private RadioGroup kewarganegaraan;
    private RadioButton inputkewarganegaraan;
    private EditText alamat;
    private EditText kota,provinsi,kodepos,telprumah,hp,aktivitas,namakerabat,hpkerabat;
    private TextInputLayout jumlahanaklayout;
    private EditText minat,keahlian,pengalaman,motivasi;
    private String pendidikanterakhir;
    private Spinner s_pendidikan;
    public static final String REG_ID = "regId";
    public static final String EMAIL_ID = "eMailId";
    public static final String BSTS_ID  = "bStsID";
    public static final String USER_ID    = "UsErId";
    public static final String LEVEL_ID = "roleId";
    SharedPreferences prefs;
    private ProgressDialog pDialog;
    private String roleid;
    private Context applicationContext;
    private ImageView picture_path;
    private String TAG ="Vik";
    String pathfoto__="";
    //variable untuk foto
    protected final int CAMERA_REQUEST = 100;

    protected final int SELECT_PICTURE = 200;
    private PhotoManager photoManager;
    private String rootPhotoDirectory = "", finalPhotoPath = "";

    //location
    private  String finalLatitude,finalLongitude;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.relawan_profile_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        applicationContext = getApplicationContext();
        prefs = getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);
        finalLatitude=prefs.getString(ApplicationConstants.USER_LATITUDE,"0");
        finalLongitude=prefs.getString(ApplicationConstants.USER_LONGITUDE,"0");
        final String emailID = prefs.getString(EMAIL_ID, "");
        int user_ID=prefs.getInt(USER_ID,0);

        GetRole g=new GetRole(this);
        roleid=g.getrole();

        s_pendidikan=(Spinner)findViewById(R.id.pendidikan_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.pendidikan_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_pendidikan.setAdapter(adapter);
        s_pendidikan.setOnItemSelectedListener(this);

        picture_path = (ImageView)findViewById(R.id.picture_path_editprofile);
        namalengkap=(EditText)findViewById(R.id.profile_relawan_name);
        namapanggilan=(EditText)findViewById(R.id.profile_relawan_nick);
        gender = (RadioGroup) findViewById(R.id.profile_relawan_jk);
        inputgender = (RadioButton)findViewById(R.id.pjk_laki);
        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.pjk_laki:
                        // do operations specific to this selection
                        inputgender = (RadioButton)findViewById(R.id.pjk_laki);
                        break;
                    case R.id.pjk_perempuan:
                        inputgender = (RadioButton)findViewById(R.id.pjk_perempuan);
                        break;
                }
            }
        });
        goldarah = (RadioGroup) findViewById(R.id.profile_relawan_gol_darah);
        inputgoldarah = (RadioButton)findViewById(R.id.gol_o);
        goldarah.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.gol_o:
                        // do operations specific to this selection
                        inputgoldarah = (RadioButton)findViewById(R.id.gol_o);
                        break;
                    case R.id.gol_a:
                        inputgoldarah = (RadioButton)findViewById(R.id.gol_a);
                        break;
                    case R.id.gol_b:
                        inputgoldarah = (RadioButton)findViewById(R.id.gol_b);
                        break;
                    case R.id.gol_ab:
                        inputgoldarah = (RadioButton)findViewById(R.id.gol_ab);
                        break;
                }
            }
        });
        tempatlahir=(EditText)findViewById(R.id.profile_relawan_tlahir);
        tanggallahir=(EditText)findViewById(R.id.profile_relawan_tgllahir);
        final Calendar c = Calendar.getInstance();
        year  = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day   = c.get(Calendar.DAY_OF_MONTH);

        tanggallahir.setFocusable(false);
        tanggallahir.setFocusableInTouchMode(false);
        tanggallahir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // On button click show datepicker dialog
                DateDialog();
            }
        });
        agama=(EditText)findViewById(R.id.profile_relawan_agama);

        statuskawin = (RadioGroup) findViewById(R.id.profile_relawan_status);
        inputstatuskawin = (RadioButton)findViewById(R.id.s_menikah);
        statuskawin.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.s_menikah:
                        // do operations specific to this selection
                        jumlahanaklayout.setVisibility(View.VISIBLE);
                        jumlahanak.setEnabled(true);
                        jumlahanak.setVisibility(View.VISIBLE);
                        inputstatuskawin = (RadioButton)findViewById(R.id.s_menikah);
                        break;
                    case R.id.s_b_menikah:
                        jumlahanaklayout.setVisibility(View.INVISIBLE);
                        jumlahanak.setEnabled(false);
                        jumlahanak.setVisibility(View.INVISIBLE);
                        inputstatuskawin = (RadioButton)findViewById(R.id.s_b_menikah);
                        break;

                }
            }
        });
        jumlahanaklayout=(TextInputLayout)findViewById(R.id.profilrelawantextinputlayout_jumlahanak);
        jumlahanak=(EditText)findViewById(R.id.profile_relawan_jumlahanak);

        jidentitas = (RadioGroup) findViewById(R.id.profile_relawan_jidentitas);
        inputjidentitas = (RadioButton)findViewById(R.id.ji_ktp);
        jidentitas.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.ji_ktp:
                        // do operations specific to this selection
                        inputjidentitas = (RadioButton)findViewById(R.id.ji_ktp);
                        break;
                    case R.id.ji_sim:
                        inputjidentitas = (RadioButton)findViewById(R.id.ji_sim);
                        break;

                }
            }
        });
        noidentitas=(EditText)findViewById(R.id.profile_relawan_no_identitas);
        kewarganegaraan = (RadioGroup) findViewById(R.id.profile_relawan_kewarganegaraan);
        inputkewarganegaraan = (RadioButton)findViewById(R.id.k_wni);
        kewarganegaraan.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.k_wni:
                        // do operations specific to this selection
                        inputkewarganegaraan = (RadioButton)findViewById(R.id.k_wni);
                        break;
                    case R.id.k_wna:
                        inputkewarganegaraan = (RadioButton)findViewById(R.id.k_wna);
                        break;

                }
            }
        });

        alamat=(EditText)findViewById(R.id.profile_relawan_alamat);
        kota=(EditText)findViewById(R.id.profile_relawan_kota);
        provinsi=(EditText)findViewById(R.id.profile_relawan_prov);
        kodepos=(EditText)findViewById(R.id.profile_relawan_kodepos);
        telprumah=(EditText)findViewById(R.id.profile_relawan_tlpn);
        hp=(EditText)findViewById(R.id.profile_relawan_hp);
        aktivitas=(EditText)findViewById(R.id.profile_relawan_aktivitas);
        namakerabat=(EditText)findViewById(R.id.profile_relawan_nama_kerabat);
        hpkerabat=(EditText)findViewById(R.id.profile_relawan_hp_kerabat);
        minat=(EditText)findViewById(R.id.profile_relawan_minat);
        keahlian=(EditText)findViewById(R.id.profile_relawan_keahlian);
        pengalaman=(EditText)findViewById(R.id.profile_relawan_pengalaman);
        motivasi=(EditText)findViewById(R.id.profile_relawan_motivasi);

        btnDaftar = (Button) findViewById(R.id.profile_relawan_btnRegister);
        btnDaftar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String bd = year + "-" + month + "-" + day;
                if (!TextUtils.isEmpty(jumlahanak.getText().toString()) && jumlahanak.getText().toString()!=null){
                    jumlah_anak=Integer.parseInt(jumlahanak.getText().toString());
                }
                if (!checkBeforSubmit()){
                    Toast.makeText(getApplicationContext(),
                            "Please Fill Required Form", Toast.LENGTH_LONG)
                            .show();
                }else {
                  /*  if (finalPhotoPath.isEmpty() || finalPhotoPath == null) {
                        Toast.makeText(getApplicationContext(),
                                "Please Upload Photo", Toast.LENGTH_LONG)
                                .show();
                    }else*/ if(pendidikanterakhir.contains("0")){
                        Toast.makeText(getApplicationContext(),
                                "Please Choose your Education Background", Toast.LENGTH_LONG)
                                .show();

                    } else {
                        registerUser(emailID, namalengkap.getText().toString(), namapanggilan.getText().toString(),
                                inputgender.getTag().toString(), inputgoldarah.getTag().toString(), tempatlahir.getText().toString(),
                                bd, agama.getText().toString(), inputstatuskawin.getTag().toString(),
                                String.valueOf(jumlah_anak), inputjidentitas.getTag().toString(), noidentitas.getText().toString(),
                                inputkewarganegaraan.getTag().toString(), alamat.getText().toString(), kota.getText().toString(), provinsi.getText().toString(),
                                kodepos.getText().toString(),
                                telprumah.getText().toString(), hp.getText().toString(), aktivitas.getText().toString(),
                                namakerabat.getText().toString(),
                                hpkerabat.getText().toString(), pendidikanterakhir, minat.getText().toString(),
                                keahlian.getText().toString(), pengalaman.getText().toString(), motivasi.getText().toString());

                    }

                }
            }
        });
        TextView uploadFoto = (TextView)findViewById(R.id.upload_photo_edit_profil_relawan);
        uploadFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(ProfileRelawan.this).create();
                dialog.setMessage("Please Choose Picture Method :");
                dialog.setButton(android.app.AlertDialog.BUTTON_NEUTRAL, "Gallery", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, SELECT_PICTURE);
                    }
                });

                dialog.setButton(android.app.AlertDialog.BUTTON_POSITIVE, "Take Photo", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }
                });
                dialog.show();
            }
        });
        getprofileuser(user_ID);
        photoManager  = new PhotoManager();
        rootPhotoDirectory = photoManager.getNewPathAppsDirectory();

    }
    private boolean checkBeforSubmit(){

        boolean cancel = false;
        View focusView = null;
        motivasi.setError(null);
        String _motivasi=motivasi.getText().toString();
        if (TextUtils.isEmpty(_motivasi)) {
            motivasi.setError(getString(R.string.error_field_required));
            focusView = motivasi;
            cancel = true;
        }

        pengalaman.setError(null);
        String _pengalaman=pengalaman.getText().toString();
        if (TextUtils.isEmpty(_pengalaman)) {
            pengalaman.setError(getString(R.string.error_field_required));
            focusView = pengalaman;
            cancel = true;
        }

        keahlian.setError(null);
        String _keahlian=keahlian.getText().toString();
        if (TextUtils.isEmpty(_keahlian)) {
            keahlian.setError(getString(R.string.error_field_required));
            focusView = keahlian;
            cancel = true;
        }

        minat.setError(null);
        String _minat=minat.getText().toString();
        if (TextUtils.isEmpty(_minat)) {
            minat.setError(getString(R.string.error_field_required));
            focusView = minat;
            cancel = true;
        }

        hpkerabat.setError(null);
        String _hpkerabat=hpkerabat.getText().toString();
        if (TextUtils.isEmpty(_hpkerabat)) {
            hpkerabat.setError(getString(R.string.error_field_required));
            focusView = hpkerabat;
            cancel = true;
        }

        namakerabat.setError(null);
        String _namakerabat=namakerabat.getText().toString();
        if (TextUtils.isEmpty(_namakerabat)) {
            namakerabat.setError(getString(R.string.error_field_required));
            focusView = namakerabat;
            cancel = true;
        }

        aktivitas.setError(null);
        String _aktivitas=aktivitas.getText().toString();
        if (TextUtils.isEmpty(_aktivitas)) {
            aktivitas.setError(getString(R.string.error_field_required));
            focusView = aktivitas;
            cancel = true;
        }
        //telprumah.setError(null);
        hp.setError(null);
        String _hp=hp.getText().toString();
        if (TextUtils.isEmpty(_hp)) {
            hp.setError(getString(R.string.error_field_required));
            focusView = hp;
            cancel = true;
        }

        kodepos.setError(null);
        String _kodepos=kodepos.getText().toString();
        if (TextUtils.isEmpty(_kodepos)) {
            kodepos.setError(getString(R.string.error_field_required));
            focusView = kodepos;
            cancel = true;
        }
        provinsi.setError(null);
        String _provinsi=provinsi.getText().toString();
        if (TextUtils.isEmpty(_provinsi)) {
            provinsi.setError(getString(R.string.error_field_required));
            focusView = provinsi;
            cancel = true;
        }
        kota.setError(null);
        String _kota=kota.getText().toString();
        if (TextUtils.isEmpty(_kota)) {
            kota.setError(getString(R.string.error_field_required));
            focusView = kota;
            cancel = true;
        }
        alamat.setError(null);
        String _alamat=alamat.getText().toString();
        if (TextUtils.isEmpty(_alamat)) {
            alamat.setError(getString(R.string.error_field_required));
            focusView = alamat;
            cancel = true;
        }
        noidentitas.setError(null);
        String _noid=noidentitas.getText().toString();
        if (TextUtils.isEmpty(_noid)) {
            noidentitas.setError(getString(R.string.error_field_required));
            focusView = noidentitas;
            cancel = true;
        }
        agama.setError(null);
        String _agama=agama.getText().toString();
        if (TextUtils.isEmpty(_agama)) {
            agama.setError(getString(R.string.error_field_required));
            focusView = agama;
            cancel = true;
        }
        tanggallahir.setError(null);
        String _tanggallahir=tanggallahir.getText().toString();
        if (TextUtils.isEmpty(_tanggallahir)) {
            tanggallahir.setError(getString(R.string.error_field_required));
            focusView = tanggallahir;
            cancel = true;
        }
        tempatlahir.setError(null);
        String _tempatlahir=tempatlahir.getText().toString();
        if (TextUtils.isEmpty(_tempatlahir)) {
            tempatlahir.setError(getString(R.string.error_field_required));
            focusView = tempatlahir;
            cancel = true;
        }
        namapanggilan.setError(null);
        String _namapanggilan=namapanggilan.getText().toString();
        if (TextUtils.isEmpty(_namapanggilan)) {
            namapanggilan.setError(getString(R.string.error_field_required));
            focusView = namapanggilan;
            cancel = true;
        }
        namalengkap.setError(null);
        String _namalengkap=namalengkap.getText().toString();
        if (TextUtils.isEmpty(_namalengkap)) {
            namalengkap.setError(getString(R.string.error_field_required));
            focusView = namalengkap;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
            return false;
        }
        return true;
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        pendidikanterakhir=String.valueOf(parent.getSelectedItemId()) ;

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void DateDialog(){

        DatePickerDialog.OnDateSetListener listener=new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int _year, int _monthOfYear, int _dayOfMonth)
            {
                year = _year;
                month = _monthOfYear+1;
                day = _dayOfMonth;
                tanggallahir.setText(year+"-"+month+"-"+day);  }};

        DatePickerDialog dpDialog=new DatePickerDialog(this, listener, year, month, day);
        dpDialog.show();

    }



    private void getprofileuser(int id_user){
        pDialog = ProgressDialog.show(ProfileRelawan.this, "Memuat Profile", "Harap Tunggu");

        // Make RESTful webservice call using AsyncHttpClient object
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
                                    pathfoto__=abc.getString("path_foto");
                                    new DownloadImageTask(picture_path)
                                                .execute(pathfoto__);

                                    namalengkap.setText(abc.getString("nama_lengkap"));
                                    namapanggilan.setText(abc.getString("nama_panggilan"));
                                    if (abc.getInt("jk")==1){
                                        inputgender = (RadioButton)findViewById(R.id.pjk_laki);
                                        inputgender.setChecked(true);
                                    }else {
                                        inputgender = (RadioButton)findViewById(R.id.pjk_perempuan);
                                        inputgender.setChecked(true);
                                    }

                                    if (abc.getInt("gol_darah")==1){
                                        inputgoldarah = (RadioButton)findViewById(R.id.gol_o);
                                        inputgoldarah.setChecked(true);
                                    }else if (abc.getInt("gol_darah")==2){
                                        inputgoldarah = (RadioButton)findViewById(R.id.gol_a);
                                        inputgoldarah.setChecked(true);
                                    }else if (abc.getInt("gol_darah")==3){
                                        inputgoldarah = (RadioButton)findViewById(R.id.gol_b);
                                        inputgoldarah.setChecked(true);
                                    }else {
                                        inputgoldarah = (RadioButton)findViewById(R.id.gol_ab);
                                        inputgoldarah.setChecked(true);
                                    }
                                    tempatlahir.setText(abc.getString("tempat_lahir"));

                                    tanggallahir.setText(abc.getString("tgl_lahir"));
                                    String temptanggallahir=abc.getString("tgl_lahir");
                                    String[] data=temptanggallahir.split("-");
                                    for (i=0;i<data.length;i++){
                                        if (i==0){
                                            try{
                                            year = Integer.parseInt(data[i]);
                                        } catch (NumberFormatException e) {
                                            year = 2016;
                                        }

                                        }else if (i==1){
                                            month=Integer.parseInt(data[i]);
                                        }else if (i==2){
                                            day=Integer.parseInt(data[i]);
                                        }
                                    }
                                    agama.setText(abc.getString("agama"));

                                    if (abc.getInt("status_perkawinan")==1){
                                        jumlahanaklayout.setVisibility(View.VISIBLE);
                                        jumlahanak.setEnabled(true);
                                        jumlahanak.setVisibility(View.VISIBLE);
                                        inputstatuskawin=(RadioButton)findViewById(R.id.s_menikah);
                                        inputstatuskawin.setChecked(true);
                                    }else {
                                        jumlahanaklayout.setVisibility(View.INVISIBLE);
                                        jumlahanak.setEnabled(false);
                                        jumlahanak.setVisibility(View.INVISIBLE);
                                        inputstatuskawin=(RadioButton)findViewById(R.id.s_b_menikah);
                                        inputstatuskawin.setChecked(true);
                                    }
                                    jumlahanak.setText(abc.getString("jumlah_anak"));
                                    if (abc.getInt("jenis_identitas")==1){
                                        inputjidentitas=(RadioButton)findViewById(R.id.ji_ktp);
                                        inputjidentitas.setChecked(true);
                                    }else {
                                        inputjidentitas=(RadioButton)findViewById(R.id.ji_sim);
                                        inputjidentitas.setChecked(true);
                                    }
                                    noidentitas.setText(abc.getString("no_identitas"));
                                    if (abc.getInt("kewarganegaraan")==1){
                                        inputkewarganegaraan=(RadioButton)findViewById(R.id.k_wni);
                                        inputkewarganegaraan.setChecked(true);
                                    }else {
                                        inputkewarganegaraan=(RadioButton)findViewById(R.id.k_wna);
                                        inputkewarganegaraan.setChecked(true);
                                    }
                                    alamat.setText(abc.getString("alamat"));
                                    kota.setText(abc.getString("kota"));
                                    provinsi.setText(abc.getString("provinsi"));
                                    kodepos.setText(abc.getString("kode_pos"));
                                    telprumah.setText(abc.getString("telp_rumah"));
                                    hp.setText(abc.getString("hp"));
                                    aktivitas.setText(abc.getString("pekerjaan"));
                                    namakerabat.setText(abc.getString("nama_kerabat"));
                                    hpkerabat.setText(abc.getString("hp_kerabat"));
                                    s_pendidikan.setSelection(abc.getInt("pendidikan_terakhir"));

                                    minat.setText(abc.getString("minat"));
                                    keahlian.setText(abc.getString("keahlian"));
                                    pengalaman.setText(abc.getString("pengalaman_organisasi"));
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
    private void registerUser(String email,  String nama_lengkap, String nama_panggilan,
                              String jk, String gol_darah, String tempat_lahir,
                              String tgl_lahir, String agama, String status_perkawinan,
                              String jumlah_anak, String jenis_identitas, String no_identitas,
                              String kewarganegaraan, String alamat, String kota,String provinsi, String kode_pos,
                              String telp_rumah, String hp, String pekerjaan, String nama_kerabat,
                              String hp_kerabat, String pendidikan_terakhir, String minat,
                              String keahlian, String pengalaman_organisasi, String motivasi) {
        final ProgressDialog dialog = ProgressDialog.show(ProfileRelawan.this, "Connecting", "Register", true);
        RequestRest req = new RequestRest(ProfileRelawan.this, new IConnectionResponseHandler(){
            @Override
            public void OnSuccessArray(JSONArray result){

                dialog.dismiss();
            }

            @Override
            public void onSuccessJSONObject(String result){
                try {
                    JSONObject obj = new JSONObject(result);

                    dialog.dismiss();
                    Toast.makeText(ProfileRelawan.this, "Data Berhasil DI UPDATE", Toast.LENGTH_LONG).show();
                    Intent intent;

                    if (roleid.contains("2")){
                        intent = new Intent(applicationContext, MainMenu.class);
                        startActivity(intent);
                        finish();
                    }else if (roleid.contains("3")){
                        intent = new Intent(applicationContext, MainMenu.class);
                        startActivity(intent);
                        finish();
                    }else if (roleid.contains("1")) {
                        intent = new Intent(applicationContext, MainMenu.class);
                        startActivity(intent);
                        finish();
                    }


                } catch (JSONException e){

                }


            }

            @Override
            public void onFailure(String e){

                dialog.dismiss();
            }

            @Override
            public void onSuccessJSONArray(String result){

                dialog.dismiss();
            }
        });


        req.registerRelawan( email,   nama_lengkap,  nama_panggilan,
                jk,  gol_darah,  tempat_lahir,
                tgl_lahir,  agama,  status_perkawinan,
                jumlah_anak,  jenis_identitas,  no_identitas,
                kewarganegaraan,  alamat,  kota, provinsi,  kode_pos,
                telp_rumah,  hp,  pekerjaan,  nama_kerabat,
                hp_kerabat,  pendidikan_terakhir,  minat,
                keahlian,  pengalaman_organisasi,  motivasi,finalPhotoPath,finalLatitude,finalLongitude);
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
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            try {
                result=Bitmap.createScaledBitmap(result,(int)(result.getWidth()*0.5), (int)(result.getHeight()*0.5), true);

            }catch (Exception e){
                Log.d("e",e.toString());
            } picture_path.setImageBitmap(result);

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==CAMERA_REQUEST && resultCode==RESULT_OK){
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
            String fileDest = sd.format(new Date())+".png";
            File photoName = new File(fileDest);
            String fullPhotoPath = rootPhotoDirectory+File.separator+photoName.toString();
            //Bitmap bmp = photo.createScaledBitmap(photo, 400,400, true);
            //Bitmap bmp = ImageScaleUtil.createScaledBitmap(photo, 400, 400, ImageScaleUtil.ScalingLogic.FIT);
            //bmp.recycle();
            //takePictures.setImageBitmap(photo);
            //picture_path.setText(fullPhotoPath);
            finalPhotoPath = fullPhotoPath;
            Bitmap resizedimage=Bitmap.createScaledBitmap(photo,(int)(photo.getWidth()*0.5), (int)(photo.getHeight()*0.5), true);
            picture_path.setImageBitmap(resizedimage);
            savebitmap(photo, fullPhotoPath);
        }else if(requestCode == SELECT_PICTURE && resultCode==RESULT_OK && data!=null){
            Uri selectedImage = data.getData();
            String path = getPath(selectedImage);
            //takePictures.setImageBitmap(BitmapFactory.decodeFile(path));
            //File source = new File(path);
            //SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
            //File destination = new File(rootPhotoDirectory+File.separator+sd.format(new Date())+".png");
            finalPhotoPath = path;
            Bitmap decode=BitmapFactory.decodeFile(path);
            Bitmap resizedimage=Bitmap.createScaledBitmap(decode,(int)(decode.getWidth()*0.5), (int)(decode.getHeight()*0.5), true);
            picture_path.setImageBitmap(resizedimage);
            //picture_path.setText(destination.getAbsolutePath());
            //if(photoManager.copyPhotoFromGallery(source, destination)){
            //    Log.d("Succes","Success Copy File");
            //}else{
            //    Log.d("Failed", "Failde Copy File");
            //}
        }
    }

    private String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String picturepath = cursor.getString(column_index);
        cursor.close();
        return picturepath;
    }



    private File savebitmap(Bitmap bmp, String path) {
        try {
            File photo = new File(path);
            FileOutputStream fos = new FileOutputStream(photo);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            Toast.makeText(ProfileRelawan.this, path, Toast.LENGTH_LONG).show();
            return photo;
        }catch (Exception e) {
            Log.d("Failed","File Failed Created : "+e.getMessage());
            return null;
        }
    }
    @Override
    public void onBackPressed() {
        checkRoleForBack();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()) {
            case android.R.id.home:
                checkRoleForBack();
                return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkRoleForBack() {
        Intent intent;

        if (roleid.contains("2")){
            intent = new Intent(applicationContext, MainMenu.class);
            startActivity(intent);
            finish();
        }else if (roleid.contains("3")){
            intent = new Intent(applicationContext, MainMenu.class);
            startActivity(intent);
            finish();
        }else if (roleid.contains("1")) {
            intent = new Intent(applicationContext, Admin.class);
            startActivity(intent);
            finish();
        }
    }
}
