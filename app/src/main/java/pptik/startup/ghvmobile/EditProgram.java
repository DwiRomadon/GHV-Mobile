package pptik.startup.ghvmobile;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

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
import pptik.startup.ghvmobile.Setup.ApplicationConstants;

public class EditProgram extends AppCompatActivity {

    private ImageView mainimage;
    EditText nama,lokasi,mulai,akhir,supervisor,deskripsi,keterangan;
    Button submit;
    private int id_program;
    private ProgressDialog pDialog;
    private int mulaiyear;
    private int mulaimonth;
    private int mulaiday;
    private int akhiryear;
    private int akhirmonth;
    private int akhirday;

    private Switch aSwitch1;
    private int urgent;
    //upload image variable
    private TextView upload_image;
    protected final int CAMERA_REQUEST = 100;
    protected final int SELECT_PICTURE = 200;
    private PhotoManager photoManager;
    private String rootPhotoDirectory = "", finalPhotoPath = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_program_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bindingXml();
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            id_program=extras.getInt("id_program");
            ambildataprogram();
        }


    }

    private void bindingXml(){
    mainimage=(ImageView)findViewById(R.id.picture_path_program);
        nama=(EditText)findViewById(R.id.submit_program_nama);
        lokasi=(EditText)findViewById(R.id.submit_program_lokasi);
        mulai=(EditText)findViewById(R.id.submit_program_mulai);
        akhir=(EditText)findViewById(R.id.submit_program_akhir);
        supervisor=(EditText)findViewById(R.id.submit_program_supervisor);
        deskripsi=(EditText)findViewById(R.id.submit_program_deskripsi);
        keterangan=(EditText)findViewById(R.id.submit_program_keterangan);


        upload_image=(TextView)findViewById(R.id.upload_photo_program);
        upload_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(EditProgram.this).create();
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
        aSwitch1=(Switch)findViewById(R.id.switch1);
        aSwitch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if(isChecked){
                    urgent=1;
                }else{
                    urgent=0;
                }

            }
        });
        submit=(Button)findViewById(R.id.submit_program_btnsubmit);
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String tanggalmulai = mulaiyear + "-" + mulaimonth + "-" + mulaiday;
                String tanggalakhir =akhiryear + "-" + akhirmonth + "-" + akhirday;
                if (!nama.getText().toString().isEmpty()&&
                        !lokasi.getText().toString().isEmpty()&&
                        !supervisor.getText().toString().isEmpty()&&
                        !deskripsi.getText().toString().isEmpty()){

                    updateProgram(String.valueOf(id_program),
                            nama.getText().toString(),
                            lokasi.getText().toString(),
                            tanggalmulai,tanggalakhir,
                            supervisor.getText().toString(),
                            deskripsi.getText().toString(),
                            keterangan.getText().toString(),urgent);

                }   else {
                    Toast.makeText(getApplicationContext(),
                            "Silahkan Lengkapi data", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });


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
            mainimage.setImageBitmap(resizedimage);
            savebitmap(photo, fullPhotoPath);
        }else if(requestCode == SELECT_PICTURE && resultCode==RESULT_OK && data!=null){
            Uri selectedImage = data.getData();
            String path = getPath(selectedImage);
            //takePictures.setImageBitmap(BitmapFactory.decodeFile(path));
            //File source = new File(path);
            //SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
            //File destination = new File(rootPhotoDirectory+File.separator+sd.format(new Date())+".png");
            finalPhotoPath = path;
            Bitmap decode= BitmapFactory.decodeFile(path);
            Bitmap resizedimage=Bitmap.createScaledBitmap(decode,(int)(decode.getWidth()*0.5), (int)(decode.getHeight()*0.5), true);
            mainimage.setImageBitmap(resizedimage);
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
            Toast.makeText(EditProgram.this, path, Toast.LENGTH_LONG).show();
            Log.d("Path", path);
            return photo;
        }catch (Exception e) {
            Log.d("Failed","File Failed Created : "+e.getMessage());
            return null;
        }
    }
    private void ambildataprogram(){
        pDialog = ProgressDialog.show(EditProgram.this, "Memuat Program", "Harap Tunggu");
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
                                    mulai.setText(abc.getString("mulai"));
                                    String tempMulai=abc.getString("mulai");
                                    String[] data=tempMulai.split("-");
                                    for (i=0;i<data.length;i++){
                                        if (i==0){
                                            try{
                                                mulaiyear = Integer.parseInt(data[i]);
                                            } catch (NumberFormatException e) {
                                                mulaiyear = 2016;
                                            }

                                        }else if (i==1){
                                            mulaimonth=Integer.parseInt(data[i]);
                                        }else if (i==2){
                                            mulaiday=Integer.parseInt(data[i]);
                                        }
                                    }

                                    akhir.setText(abc.getString("akhir"));
                                    String tempAkhir=abc.getString("akhir");
                                    String[] data2=tempAkhir.split("-");
                                    for (i=0;i<data2.length;i++){
                                        if (i==0){
                                            try{
                                                akhiryear = Integer.parseInt(data[i]);
                                            } catch (NumberFormatException e) {
                                                akhiryear = 2016;
                                            }

                                        }else if (i==1){
                                            akhirmonth=Integer.parseInt(data[i]);
                                        }else if (i==2){
                                            akhirday=Integer.parseInt(data[i]);
                                        }
                                    }

                                    supervisor.setText(abc.getString("supervisor"));
                                    lokasi.setText(abc.getString("lokasi_program"));
                                    keterangan.setText(abc.getString("keterangan"));
                                    deskripsi.setText(abc.getString("deskripsi"));
                                    if (!abc.getString("main_image").isEmpty() ||abc.getString("main_image")!=null){
                                        Picasso.with(EditProgram.this)
                                                .load(abc.getString("main_image"))
                                                .fit()
                                                .into(mainimage);
                                    }

                                    if (abc.getInt("urgent")==1){
                                        aSwitch1.setChecked(true);
                                        urgent=1;
                                    }else {
                                        aSwitch1.setChecked(false);
                                        urgent=0;
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
                           /* Toast.makeText(
                                    getApplicationContext(),
                                    "Unexpected Error occcured! [Most common Error: Device might "
                                            + "not be connected to Internet or remote server is not up and running], check for other errors as well",
                                    Toast.LENGTH_LONG).show();*/
                        }
                    }
                });
    }
    private void updateProgram(String id_program,String nama_program
            ,String lokasi_program,String mulai,String akhir
            ,String supervisor,String deskripsi,String keterangan,int   urgent) {
        final ProgressDialog dialog = ProgressDialog.show(EditProgram.this, "Connecting", "Send Data", true);
        RequestRest req = new RequestRest(EditProgram.this, new IConnectionResponseHandler(){
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
                    Toast.makeText(EditProgram.this, "Update Success", Toast.LENGTH_LONG).show();
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


        req.updateProgram( id_program, nama_program
                , lokasi_program, mulai, akhir
                , supervisor, deskripsi, keterangan,finalPhotoPath,urgent);
    }

}
