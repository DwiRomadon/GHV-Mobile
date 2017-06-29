package pptik.startup.ghvmobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.ionicons_typeface_library.Ionicons;
import com.squareup.picasso.Picasso;

import pptik.startup.ghvmobile.Support.Program;
import pptik.startup.ghvmobile.Utilities.PictureFormatTransform;

/**
 * Created by edo on 6/12/2016.
 */
public class Detailmateri  extends AppCompatActivity {
    private Program p;
    private TextView nama,awal,akhir,status,supervisor,lokasi,keterangan,deskripsi;
    private ImageView mainimage;
    private FloatingActionButton fabGallery;
    private int id_program;

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_program_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        prefs = getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);
        mainimage=(ImageView)findViewById(R.id.detailmateri_mainimage);
        nama = (TextView) findViewById(R.id.detail_program_nama);
        awal = (TextView) findViewById(R.id.detail_program_awal);
        akhir=(TextView) findViewById(R.id.detail_program_akhir);
        status=(TextView) findViewById(R.id.detail_program_status);
        supervisor=(TextView) findViewById(R.id.detail_program_supervisor);
        lokasi=(TextView) findViewById(R.id.detail_program_lokasi);
        keterangan=(TextView) findViewById(R.id.detail_program_keterangan);
        deskripsi=(TextView) findViewById(R.id.detail_program_deskripsi);
        fabGallery=(FloatingActionButton)findViewById(R.id.fabGallery);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            p= (Program) extras.getSerializable("program");
            id_program=p.getIdProgram();
            nama.setText(p.getNamaProgram());
            awal.setText(p.getMulai());
            akhir.setText(p.getAkhir());
            if (p.getStatus()==1){
                status.setText("Verified");
            }else {
                status.setText("Unverified");
            }
            supervisor.setText(p.getSupervisor());
            lokasi.setText(p.getLokasiProgram());
            keterangan.setText(p.getKeterangan());
            deskripsi.setText(p.getDeskripsi());
            getSupportActionBar().setTitle(p.getNamaProgram());
            if (p.getPathfoto()!="" || p.getPathfoto().isEmpty()){
                Picasso.with(this)
                        .load(p.getPathfoto())
                        .fit()
                        .into(mainimage);
            }

            fabGallery.setImageBitmap(PictureFormatTransform.drawableToBitmap(new IconicsDrawable(this)
                    .icon(Ionicons.Icon.ion_images)
                    .color(this.getResources().getColor(R.color.white))
                    .sizeDp(60)));
            fabGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i;
                    i = new Intent(Detailmateri.this, ImageGallery.class);
                    i.putExtra("id_program",id_program);
                    startActivity(i);
                    finish();
                }
            });
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
