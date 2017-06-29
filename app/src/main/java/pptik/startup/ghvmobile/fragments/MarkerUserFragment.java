package pptik.startup.ghvmobile.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

import pptik.startup.ghvmobile.R;
import pptik.startup.ghvmobile.Support.DataUser;
import pptik.startup.ghvmobile.Support.DataUser2;
import pptik.startup.ghvmobile.User_Admin.ApprovalRelawanDetail;
import pptik.startup.ghvmobile.Setup.ApplicationConstants;

/**
 * Created by hynra on 8/18/16.
 */

public class MarkerUserFragment extends Fragment implements View.OnClickListener {
    private JSONObject data;
    private TextView nama,hp;
    private Button BTN_detail;
    private ImageView thumbnail;
    private String pathfoto=null;
    private ArrayList<DataUser> listDataUser;
    private ArrayList<DataUser2> listDataUser2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences prefs;
        View view = inflater.inflate(R.layout.fragments_user_detail, container, false);
        nama=(TextView)view.findViewById(R.id.fragment_user_name);
        hp=(TextView)view.findViewById(R.id.fragment_user_hp);
        thumbnail=(ImageView) view.findViewById(R.id.fragment_user_thumb);
        BTN_detail=(Button)view.findViewById(R.id.fragment_user_BTN_detail);
        nama.setText(data.optString("nama_lengkap"));
        hp.setText(data.optString("hp"));
        pathfoto=data.optString("path_foto");
        if (pathfoto !=null || !pathfoto.isEmpty()){
            new DownloadImageTask(thumbnail)
                    .execute(pathfoto);
        }
        prefs = getActivity().getApplicationContext().getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);
        String role=prefs.getString(ApplicationConstants.LEVEL_ID,"0");
        if (Integer.parseInt(role) !=1){
            BTN_detail.setVisibility(View.INVISIBLE);
        }
        BTN_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if (data.optInt("persetujuan")==1 && data.optInt("level")==2 ){
                    listDataUser = new ArrayList<DataUser>();
                    DataUser p=new DataUser();
                    p.setIdUser(data.optInt("id_user"));
                    p.setEmail(data.optString("email"));
                    p.setNamaLengkap(data.optString("nama_lengkap"));
                    listDataUser.add(p);
                    intent = new Intent(getActivity().getApplicationContext(), ApprovalRelawanDetail.class);
                    intent.putExtra("detail", listDataUser.get(0));
                    intent.putExtra("status",1);
                    startActivity(intent);
                }else {
                    listDataUser2 = new ArrayList<DataUser2>();
                    DataUser2 p=new DataUser2();
                    p.setIdUser(data.optInt("id_user"));
                    p.setEmail(data.optString("email"));
                    p.setNamaLengkap(data.optString("nama_lengkap"));
                    listDataUser2.add(p);
                    intent = new Intent(getActivity().getApplicationContext(), ApprovalRelawanDetail.class);
                    intent.putExtra("detail", listDataUser2.get(0));
                    intent.putExtra("status",2);
                    startActivity(intent);
                }



            }
        });
        return view;
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
            }
            thumbnail.setImageBitmap(result);

        }
    }
    public void setData(JSONObject data) {
        this.data = data;
    }

    public JSONObject getData() {
        return data;
    }
    @Override
    public void onClick(View v) {

    }
}
