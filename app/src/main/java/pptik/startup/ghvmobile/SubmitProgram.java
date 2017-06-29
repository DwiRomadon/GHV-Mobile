package pptik.startup.ghvmobile;

import android.app.AlertDialog;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import pptik.startup.ghvmobile.Support.PhotoManager;
import pptik.startup.ghvmobile.User_Admin.Admin;
import pptik.startup.ghvmobile.User_Relawan.RelawanMenu;
import pptik.startup.ghvmobile.Setup.ApplicationConstants;

/**
 * Created by GIGABYTE on 14/06/2016.
 */
public class SubmitProgram extends AppCompatActivity {
    SharedPreferences prefs;
    Context applicationContext;

    private EditText namaprogram;
    private EditText lokasiprogram;
    private EditText mulai;
    private int mulaiyear;
    private int mulaimonth;
    private int mulaiday;
    private EditText akhir;
    private int akhiryear;
    private int akhirmonth;
    private int akhirday;
    private EditText supervisor;
    private EditText deskripsi;
    private  EditText keterangan;
    private Button btnSubmit;
    private String RoleID;
    private  int id_user;

    //lokasi
    private String currentLatitude,currentLongitude,pathfoto;
    //upload image variable
    private TextView upload_image;
    protected final int CAMERA_REQUEST = 100;
    protected final int SELECT_PICTURE = 200;
    private ImageView picture_path;
    private PhotoManager photoManager;
    private String rootPhotoDirectory = "", finalPhotoPath = "";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.submit_issue_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Submit Program");
        applicationContext = getApplicationContext();
        prefs = getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);
        currentLatitude=prefs.getString(ApplicationConstants.USER_LATITUDE,"0");
        currentLongitude=prefs.getString(ApplicationConstants.USER_LONGITUDE,"0");
        RoleID = prefs.getString(ApplicationConstants.LEVEL_ID, "");
        id_user=prefs.getInt(ApplicationConstants.USER_ID,0);
        namaprogram=(EditText)findViewById(R.id.submit_program_nama);
        lokasiprogram=(EditText)findViewById(R.id.submit_program_lokasi);
        mulai=(EditText)findViewById(R.id.submit_program_mulai);
        final Calendar c = Calendar.getInstance();
        mulaiyear  = c.get(Calendar.YEAR);
        mulaimonth = c.get(Calendar.MONTH);
        mulaiday   = c.get(Calendar.DAY_OF_MONTH);

        mulai.setFocusable(false);
        mulai.setFocusableInTouchMode(false);
        mulai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // On button click show datepicker dialog
                DateMulai();
            }
        });

        akhir=(EditText)findViewById(R.id.submit_program_akhir);
        akhiryear  = c.get(Calendar.YEAR);
        akhirmonth = c.get(Calendar.MONTH);
        akhirday   = c.get(Calendar.DAY_OF_MONTH);

        akhir.setFocusable(false);
        akhir.setFocusableInTouchMode(false);
        akhir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // On button click show datepicker dialog
                DateAkhir();
            }
        });
        supervisor=(EditText)findViewById(R.id.submit_program_supervisor);
        deskripsi=(EditText)findViewById(R.id.submit_program_deskripsi);
        keterangan=(EditText)findViewById(R.id.submit_program_keterangan);

        btnSubmit=(Button) findViewById(R.id.submit_program_btnsubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String tanggalmulai = mulaiyear + "-" + mulaimonth + "-" + mulaiday;
                String tanggalakhir =akhiryear + "-" + akhirmonth + "-" + akhirday;
                if (!namaprogram.getText().toString().isEmpty()&&
                        !lokasiprogram.getText().toString().isEmpty()&&
                        !supervisor.getText().toString().isEmpty()&&
                        !deskripsi.getText().toString().isEmpty()){

                    submitProgram(String.valueOf(id_user),
                            namaprogram.getText().toString(),
                            lokasiprogram.getText().toString(),
                            tanggalmulai,tanggalakhir,
                            supervisor.getText().toString(),
                            deskripsi.getText().toString(),
                            keterangan.getText().toString());

                }   else {
                    Toast.makeText(getApplicationContext(),
                            "Silahkan Lengkapi data", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
        picture_path=(ImageView)findViewById(R.id.picture_path_program);
        upload_image=(TextView)findViewById(R.id.upload_photo_program);
        upload_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(SubmitProgram.this).create();
                dialog.setMessage("Please Choose Picture Method :");
                dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Gallery", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, SELECT_PICTURE);
                    }
                });

                dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Take Photo", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }
                });
                dialog.show();
            }
        });
        photoManager  = new PhotoManager();
        rootPhotoDirectory = photoManager.getNewPathAppsDirectory();
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
            Toast.makeText(SubmitProgram.this, path, Toast.LENGTH_LONG).show();
            Log.d("Path", path);
            return photo;
        }catch (Exception e) {
            Log.d("Failed","File Failed Created : "+e.getMessage());
            return null;
        }
    }
    private void submitProgram(String id_user,String nama_program
            ,String lokasi_program,String mulai,String akhir
            ,String supervisor,String deskripsi,String keterangan) {
        final ProgressDialog dialog = ProgressDialog.show(SubmitProgram.this, "Connecting", "Send Data", true);
        RequestRest req = new RequestRest(SubmitProgram.this, new IConnectionResponseHandler(){
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
                    Toast.makeText(SubmitProgram.this, "Program Akan Di tampilkan setelah di verivikasi admin", Toast.LENGTH_LONG).show();

                    if (RoleID.contains("2")){

                        Intent i = new Intent(SubmitProgram.this,RelawanMenu.class);
                        startActivity(i);
                        finish();
                    }else if(RoleID.contains("1")){

                        Intent i = new Intent(SubmitProgram.this,Admin.class);
                        startActivity(i);
                        finish();
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
                Log.i("Test", result);
                dialog.dismiss();
            }
        });


        req.submitProgram( id_user, nama_program
                , lokasi_program, mulai, akhir
                , supervisor, deskripsi, keterangan,currentLatitude,currentLongitude,finalPhotoPath);
    }
        public void DateMulai(){

            DatePickerDialog.OnDateSetListener listener=new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int _year, int _monthOfYear, int _dayOfMonth)
                {
                    mulaiyear = _year;
                    mulaimonth = _monthOfYear+1;
                    mulaiday = _dayOfMonth;
                    mulai.setText(mulaiyear+"-"+mulaimonth+"-"+mulaiday);
                }};

            DatePickerDialog dpDialog=new DatePickerDialog(this, listener, mulaiyear, mulaimonth, mulaiday);
            dpDialog.show();

        }
        public void DateAkhir (){

            DatePickerDialog.OnDateSetListener listener=new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int _year, int _monthOfYear, int _dayOfMonth)
                {
                    akhiryear = _year;
                    akhirmonth = _monthOfYear+1;
                    akhirday = _dayOfMonth;
                    akhir.setText(akhiryear+"-"+akhirmonth+"-"+akhirday);
                }};

            DatePickerDialog dpDialog=new DatePickerDialog(this, listener, akhiryear, akhirmonth, akhirday);
            dpDialog.show();

        }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        checkRoleToBack();
    }

    private void checkRoleToBack() {
        Intent intent;
        GetRole g=new GetRole(this);
        String roleid=g.getrole();
        if (roleid.contains("2")){
            intent = new Intent(applicationContext, RelawanMenu.class);
            startActivity(intent);
            finish();
        }else if (roleid.contains("1")) {
            intent = new Intent(applicationContext, Admin.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                checkRoleToBack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}

