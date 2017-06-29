package pptik.startup.ghvmobile;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.ionicons_typeface_library.Ionicons;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pptik.startup.ghvmobile.Connection.IConnectionResponseHandler;
import pptik.startup.ghvmobile.Connection.RequestRest;
import pptik.startup.ghvmobile.Setup.ApplicationConstants;
import pptik.startup.ghvmobile.Utilities.Image;
import pptik.startup.ghvmobile.Utilities.PictureFormatTransform;

/**
 * Created by GIGABYTE on 06/10/2016.
 */

public class SlideshowDialogFragment extends DialogFragment {
    private String TAG = SlideshowDialogFragment.class.getSimpleName();
    private ArrayList<Image> images;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private TextView lblCount, lblnama, lblDate;
    private FloatingActionButton fabdel;
    private int selectedPosition = 0;
    private String theRole;
    private SharedPreferences prefs;
    private int id_user;
    static SlideshowDialogFragment newInstance() {
        SlideshowDialogFragment f = new SlideshowDialogFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_slider, container, false);
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        lblCount = (TextView) v.findViewById(R.id.lbl_count);
        lblnama = (TextView) v.findViewById(R.id.lbl_nama_user);
        lblDate = (TextView) v.findViewById(R.id.date);
        fabdel=(FloatingActionButton) v.findViewById(R.id.fabDelImage) ;
        images = (ArrayList<Image>) getArguments().getSerializable("images");
        selectedPosition = getArguments().getInt("position");
        prefs = SlideshowDialogFragment.this.getContext().getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);
        theRole = prefs.getString(ApplicationConstants.LEVEL_ID, "");
        id_user=prefs.getInt(ApplicationConstants.USER_ID,0);
        fabdel.setImageBitmap(PictureFormatTransform.drawableToBitmap(new IconicsDrawable(SlideshowDialogFragment.this.getContext())
                .icon(Ionicons.Icon.ion_android_delete
                )
                .color(this.getResources().getColor(R.color.red))
                .sizeDp(60)));
        Log.e(TAG, "position: " + selectedPosition);
        Log.e(TAG, "images size: " + images.size());

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);


        setCurrentItem(selectedPosition);

        return v;
    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
        displayMetaInfo(selectedPosition);
    }

    //  page change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            displayMetaInfo(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void displayMetaInfo(int position) {
        lblCount.setText((position + 1) + " of " + images.size());

        final Image image = images.get(position);
        lblnama.setText("Image by : "+image.get_namauser());
        lblDate.setText(image.get_createdat());
        fabdel.setVisibility(View.INVISIBLE);
        if (theRole=="1" || id_user==image.get_iduser() ){
            fabdel.setVisibility(View.VISIBLE);
        }
        fabdel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deleteImageFromGallery(image.get_idimage(),image.get_namaimage(),image.get_idprogram());

            }
        });
    }

    private void deleteImageFromGallery(int  id_image, String image_name, final int id_program){

        final ProgressDialog dialog = ProgressDialog.show(SlideshowDialogFragment.this.getContext(), null, "Processing...", true);
        RequestRest req = new RequestRest(SlideshowDialogFragment.this.getContext(), new IConnectionResponseHandler(){
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

                        Toast.makeText(SlideshowDialogFragment.this.getContext(), "Delete Image Success", Toast.LENGTH_LONG).show();
                        Intent i;
                        i = new Intent(SlideshowDialogFragment.this.getContext(), ImageGallery.class);
                        i.putExtra("id_program",id_program);
                        startActivity(i);
                        SlideshowDialogFragment.this.getActivity().finish();
                        return;
                    }else {
                        Toast.makeText(SlideshowDialogFragment.this.getContext(), "Failed please try again", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e){
                    Toast.makeText(SlideshowDialogFragment.this.getContext(), e.toString(), Toast.LENGTH_LONG).show();
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


        req.deleteImageFromGallery(id_image, image_name);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    //  adapter
    public class MyViewPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.image_fullscreen_preview, container, false);

            ImageView imageViewPreview = (ImageView) view.findViewById(R.id.image_preview);

            Image image = images.get(position);

            Glide.with(getActivity()).load(image.get_pathimage())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageViewPreview);

            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == ((View) obj);
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}