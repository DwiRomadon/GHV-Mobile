package pptik.startup.ghvmobile.User_Guest;

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
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import pptik.startup.ghvmobile.Connection.IConnectionResponseHandler;
import pptik.startup.ghvmobile.Connection.RequestRest;
import pptik.startup.ghvmobile.GetRole;
import pptik.startup.ghvmobile.MainMenu;
import pptik.startup.ghvmobile.R;
import pptik.startup.ghvmobile.Setup.ApplicationConstants;
import pptik.startup.ghvmobile.Support.PhotoManager;
import pptik.startup.ghvmobile.User_Admin.Admin;


/**
 * Created by edo on 6/12/2016.
 */
public class DaftarRelawan extends AppCompatActivity implements AdapterView.OnItemSelectedListener  {

    Context applicationContext;


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
    private RadioGroup jidentitas;
    private RadioButton inputjidentitas;
    private EditText noidentitas;
    private RadioGroup kewarganegaraan;
    private RadioButton inputkewarganegaraan;
    private EditText alamat;
    private EditText kota,provinsi,kodepos,telprumah,hp,aktivitas,namakerabat,hpkerabat;
    private String pendidikanterakhir;
    private Spinner s_pendidikan;
    private EditText minat,keahlian,pengalaman,motivasi;
    private int jumlah_anak=0;
    private TextInputLayout jumlahanaklayout;
    SharedPreferences prefs;

    //variable untuk foto
    protected final int CAMERA_REQUEST = 100;
    protected final int SELECT_PICTURE = 200;
    private ImageView picture_path;
    private PhotoManager photoManager;
    private String rootPhotoDirectory = "", finalPhotoPath = "";

    //location
    private  String finalLatitude,finalLongitude;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daftar_relawan_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        applicationContext = getApplicationContext();

        prefs = getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);
        String registrationId = prefs.getString(ApplicationConstants.REG_ID, "");
        final String emailID = prefs.getString(ApplicationConstants.EMAIL_ID, "");
        finalLatitude=prefs.getString(ApplicationConstants.USER_LATITUDE,"0");
        finalLongitude=prefs.getString(ApplicationConstants.USER_LONGITUDE,"0");


        namalengkap=(EditText)findViewById(R.id.daftar_relawan_name);
        namapanggilan=(EditText)findViewById(R.id.daftar_relawan_nick);
        gender = (RadioGroup) findViewById(R.id.daftar_relawan_jk);
        inputgender = (RadioButton)findViewById(R.id.jk_Laki);
        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.jk_Laki:
                        // do operations specific to this selection
                        inputgender = (RadioButton)findViewById(R.id.jk_Laki);
                        break;
                    case R.id.jk_perempuan:
                        inputgender = (RadioButton)findViewById(R.id.jk_perempuan);
                        break;
                }
            }
        });
        goldarah = (RadioGroup) findViewById(R.id.daftar_relawan_gol_darah);
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
        tempatlahir=(EditText)findViewById(R.id.daftar_relawan_tlahir);
        tanggallahir=(EditText)findViewById(R.id.daftar_relawan_tgllahir);
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
        agama=(EditText)findViewById(R.id.daftar_relawan_agama);

        statuskawin = (RadioGroup) findViewById(R.id.daftar_relawan_status);
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

        jumlahanak=(EditText)findViewById(R.id.daftar_relawan_jumlahanak);
        jumlahanaklayout=(TextInputLayout)findViewById(R.id.textinputlayout_jumlahanak);

        jidentitas = (RadioGroup) findViewById(R.id.daftar_relawan_jidentitas);
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
        noidentitas=(EditText)findViewById(R.id.daftar_relawan_no_identitas);
        kewarganegaraan = (RadioGroup) findViewById(R.id.daftar_relawan_kewarganegaraan);
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
        s_pendidikan=(Spinner)findViewById(R.id.pendidikan_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.pendidikan_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_pendidikan.setAdapter(adapter);
        s_pendidikan.setOnItemSelectedListener(this);
        alamat=(EditText)findViewById(R.id.daftar_relawan_alamat);
        kota=(EditText)findViewById(R.id.daftar_relawan_kota);
        provinsi=(EditText)findViewById(R.id.daftar_relawan_prov);
        kodepos=(EditText)findViewById(R.id.daftar_relawan_kodepos);
        telprumah=(EditText)findViewById(R.id.daftar_relawan_tlpn);
        hp=(EditText)findViewById(R.id.daftar_relawan_hp);
        aktivitas=(EditText)findViewById(R.id.daftar_relawan_aktivitas);
        namakerabat=(EditText)findViewById(R.id.daftar_relawan_nama_kerabat);
        hpkerabat=(EditText)findViewById(R.id.daftar_relawan_hp_kerabat);


        minat=(EditText)findViewById(R.id.daftar_relawan_minat);
        keahlian=(EditText)findViewById(R.id.daftar_relawan_keahlian);
        pengalaman=(EditText)findViewById(R.id.daftar_relawan_pengalaman);
        motivasi=(EditText)findViewById(R.id.daftar_relawan_motivasi);

        btnDaftar = (Button) findViewById(R.id.daftar_relawan_btnRegister);
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
                    if (finalPhotoPath.isEmpty() || finalPhotoPath == null) {
                        Toast.makeText(getApplicationContext(),
                                "Please Upload Photo", Toast.LENGTH_LONG)
                                .show();
                    }else if(pendidikanterakhir.contains("0")){
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

        TextView uploadFoto = (TextView)findViewById(R.id.upload_photo_profil_relawan);
        uploadFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(DaftarRelawan.this).create();
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
        picture_path = (ImageView)findViewById(R.id.picture_path_aspirasi);
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
                tanggallahir.setText(year+"-"+month+"-"+day);
            }};

        DatePickerDialog dpDialog=new DatePickerDialog(this, listener, year, month, day);
        dpDialog.show();

    }
    private void registerUser(String email,  String nama_lengkap, String nama_panggilan,
                              String jk, String gol_darah, String tempat_lahir,
                              String tgl_lahir, String agama, String status_perkawinan,
                              String jumlah_anak, String jenis_identitas, String no_identitas,
                              String kewarganegaraan, String alamat, String kota,String provinsi, String kode_pos,
                              String telp_rumah, String hp, String pekerjaan, String nama_kerabat,
                              String hp_kerabat, String pendidikan_terakhir, String minat,
                              String keahlian, String pengalaman_organisasi, String motivasi) {
        final ProgressDialog dialog = ProgressDialog.show(DaftarRelawan.this, "Connecting", "Register", true);
        RequestRest req = new RequestRest(DaftarRelawan.this, new IConnectionResponseHandler(){
            @Override
            public void OnSuccessArray(JSONArray result){
                Log.i("result", result.toString());
                dialog.dismiss();
            }

            @Override
            public void onSuccessJSONObject(String result){
                try {
                    JSONObject obj = new JSONObject(result);
                    Log.i("Test", result);
                    dialog.dismiss();
                    Toast.makeText(DaftarRelawan.this, "Berhasil mendaftar, Menunggu Persetujuan Admin", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(DaftarRelawan.this,MainMenu.class);
                    startActivity(i);
                    finish();

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
                Log.i("Test", result);
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
            Log.d("Success", "File Success Created");
            Toast.makeText(DaftarRelawan.this, path, Toast.LENGTH_LONG).show();
            Log.d("Path", path);
            return photo;
        }catch (Exception e) {
            Log.d("Failed","File Failed Created : "+e.getMessage());
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent;
        GetRole g=new GetRole(this);
        String roleid=g.getrole();
        if (roleid.contains("1")) {
            intent = new Intent(applicationContext, Admin.class);
            startActivity(intent);
            finish();
        }else {
            intent = new Intent(applicationContext, MainMenu.class);
            startActivity(intent);
            finish();
        }

    }
}
