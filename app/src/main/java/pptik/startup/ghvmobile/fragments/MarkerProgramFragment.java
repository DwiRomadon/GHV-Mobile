package pptik.startup.ghvmobile.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

import pptik.startup.ghvmobile.Detailmateri;
import pptik.startup.ghvmobile.R;
import pptik.startup.ghvmobile.Support.DataProgram;
import pptik.startup.ghvmobile.Support.Program;
import pptik.startup.ghvmobile.User_Admin.ApprovalProgramDetail;

/**
 * Created by GIGABYTE on 01/09/2016.
 */
public class MarkerProgramFragment extends Fragment implements View.OnClickListener {
    private JSONObject data;
    private TextView program_start,program_name,program_supervisor,program_status;
    private Button viewdetail;
    ArrayList<Program> listProgram;
    ArrayList<DataProgram> listDataProgram;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_program_detail, container, false);


        program_start=(TextView) view.findViewById(R.id.fragment_program_start);
        program_name=(TextView) view.findViewById(R.id.fragment_program_name);
        program_supervisor=(TextView) view.findViewById(R.id.fragment_program_supervisor);
        program_status=(TextView) view.findViewById(R.id.fragment_program_verified);
        program_start.setText(data.optString("mulai"));
        program_name.setText(data.optString("nama_program"));
        program_supervisor.setText(data.optString("supervisor"));
        program_status.setText("Verified");
        viewdetail=(Button)view.findViewById(R.id.fragment_program_BTN_detail);
        if (data.optString("levelaksesuser").contains("1"))
        {
            viewdetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listDataProgram = new ArrayList<DataProgram>();
                    DataProgram p=new DataProgram();
                    p.setIdProgram(data.optInt("id_program"));
                    p.setNamaProgram(data.optString("nama_program"));
                    p.setLokasiProgram(data.optString("lokasi_program"));
                    listDataProgram.add(p);
                    Intent intent = new Intent(getActivity().getApplicationContext(), ApprovalProgramDetail.class);
                    intent.putExtra("detail", listDataProgram.get(0));
                    intent.putExtra("status",data.optInt("status"));
                    startActivity(intent);
                }
            });

        }else {
            viewdetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listProgram = new ArrayList<Program>();
                    Program p=new Program();
                    p.setIdProgram(data.optInt("id_program"));
                    p.setNamaProgram(data.optString("nama_program"));
                    p.setLokasiProgram(data.optString("lokasi_program"));
                    p.setMulai(data.optString("mulai"));
                    p.setAkhir(data.optString("akhir"));
                    p.setSupervisor(data.optString("supervisor"));
                    p.setDeskripsi(data.optString("deskripsi"));
                    p.setKeterangan(data.optString("keterangan"));
                    p.setLatitude(data.optString("latitude"));
                    p.setLongitude(data.optString("longitude"));
                    p.setStatus(data.optInt("status"));
                    listProgram.add(p);
                    Intent intent = new Intent(getActivity().getApplicationContext(), Detailmateri.class);
                    intent.putExtra("program", listProgram.get(0));
                    startActivity(intent);
                }
            });
        }



        return view;
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
