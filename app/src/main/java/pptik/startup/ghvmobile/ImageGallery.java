package pptik.startup.ghvmobile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.ionicons_typeface_library.Ionicons;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import pptik.startup.ghvmobile.Connection.IConnectionResponseHandler;
import pptik.startup.ghvmobile.Connection.RequestRest;
import pptik.startup.ghvmobile.Support.GalleryAdapter;
import pptik.startup.ghvmobile.Support.PhotoManager;
import pptik.startup.ghvmobile.Utilities.Image;
import pptik.startup.ghvmobile.Utilities.PictureFormatTransform;
import pptik.startup.ghvmobile.Setup.ApplicationConstants;

public class ImageGallery extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();
    private ArrayList<Image> images;
    private ProgressDialog pDialog;
    private GalleryAdapter mAdapter;
    private RecyclerView recyclerView;
    private int id_program;
    private  SharedPreferences prefs;

    //upload image variable
    protected final int CAMERA_REQUEST = 100;
    protected final int SELECT_PICTURE = 200;
    private PhotoManager photoManager;
    private String rootPhotoDirectory = "";

    private String final_id_program,final_id_user,final_path_foto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_gallery_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            id_program= extras.getInt("id_program");

        }
        prefs = getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);
        final_id_user=String.valueOf(prefs.getInt(ApplicationConstants.USER_ID,0));
        final_id_program=String.valueOf(id_program);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        pDialog = new ProgressDialog(this);
        images = new ArrayList<>();
        mAdapter = new GalleryAdapter(getApplicationContext(), images);


        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", images);
                bundle.putInt("position", position);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        photoManager  = new PhotoManager();
        rootPhotoDirectory = photoManager.getNewPathAppsDirectory();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageBitmap(PictureFormatTransform.drawableToBitmap(new IconicsDrawable(this)
                .icon(Ionicons.Icon.ion_plus_circled
                )
                .color(this.getResources().getColor(R.color.white))
                .sizeDp(60)));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = new AlertDialog.Builder(ImageGallery.this).create();
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
        getImage(String.valueOf(id_program));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==CAMERA_REQUEST && resultCode==RESULT_OK){
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
            String fileDest = sd.format(new Date())+".png";
            File photoName = new File(fileDest);
            String fullPhotoPath = rootPhotoDirectory+File.separator+photoName.toString();
            final_path_foto = fullPhotoPath;
            Bitmap resizedimage=Bitmap.createScaledBitmap(photo,(int)(photo.getWidth()*0.5), (int)(photo.getHeight()*0.5), true);
            savebitmap(photo, fullPhotoPath);
        }else if(requestCode == SELECT_PICTURE && resultCode==RESULT_OK && data!=null){
            Uri selectedImage = data.getData();
            String path = getPath(selectedImage);
            final_path_foto = path;
        }
        storeImage();
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
            return photo;
        }catch (Exception e) {
            return null;
        }
    }
    public void storeImage(){
        final ProgressDialog  dialog = ProgressDialog.show(ImageGallery.this, null, "Processing...", true);
        RequestRest req = new RequestRest(ImageGallery.this, new IConnectionResponseHandler(){
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
                    boolean status=obj.getBoolean("status");
                    dialog.dismiss();
                    if (status){

                        Toast.makeText(ImageGallery.this, "Success add image", Toast.LENGTH_LONG).show();
                        Intent i;
                        i = new Intent(ImageGallery.this, ImageGallery.class);
                        i.putExtra("id_program",id_program);
                        startActivity(i);
                        finish();
                        return;
                    }else {
                        Toast.makeText(ImageGallery.this, "Failed please try again", Toast.LENGTH_LONG).show();
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


        req.storeImageToGallery(final_id_program, final_id_user, final_path_foto);
    }


    public void getImage(String id_program){
        pDialog = ProgressDialog.show(ImageGallery.this, null, "Loading Image.. Please Wait..");

        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(ApplicationConstants.API_GET_IMAGE_GALLERY_BY_ID_PROGRAM+id_program,
                new AsyncHttpResponseHandler() {
                    // When the response returned by REST has Http
                    // response code '200'
                    @Override
                    public void onSuccess(String response) {


                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean status = jObj.getBoolean("status");
                            if (status) {
                                JSONArray berita= jObj.getJSONArray("image");

                                images.clear();
                                for (int i=0;i<berita.length();i++){
                                    JSONObject abc=berita.getJSONObject(i);
                                    Image image = new Image();
                                    image.set_idimage(abc.getInt("id_image"));
                                    image.set_idprogram(abc.getInt("id_program"));
                                    image.set_iduser(abc.getInt("id_user"));
                                    image.set_namaimage(abc.getString("nama_image"));
                                    image.set_pathimage(abc.getString("path_image"));
                                    image.set_namauser(abc.getString("nama_lengkap"));
                                    String[] splited = abc.getString("created_at").split("\\s+");
                                    image.set_createdat(splited[0]);
                                    images.add(image);
                                }

                                mAdapter.notifyDataSetChanged();
                                pDialog.dismiss();
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

}
