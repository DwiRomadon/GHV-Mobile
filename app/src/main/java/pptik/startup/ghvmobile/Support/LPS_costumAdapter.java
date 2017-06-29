package pptik.startup.ghvmobile.Support;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import pptik.startup.ghvmobile.User_Admin.ApprovalProgramDetail;
import pptik.startup.ghvmobile.User_Admin.DisapprovProgram;
import pptik.startup.ghvmobile.R;

/**
 * Created by GIGABYTE on 17/06/2016.
 */
public class LPS_costumAdapter extends BaseAdapter {

    ArrayList<DataProgram> listDataProgram2;
    Context context;
    private static LayoutInflater inflater=null;
    public LPS_costumAdapter(DisapprovProgram ap, ArrayList<DataProgram> data) {
        listDataProgram2 = new ArrayList<DataProgram>();
        listDataProgram2 = data;
        context= ap;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return listDataProgram2.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int _position, View convertView, ViewGroup parent) {
        View view = convertView;
        // There are three cases here
        if (view == null) {
            // 1) The view has not yet been created - we need to initialize the YouTubeThumbnailView.
            view = inflater.inflate(R.layout.lv_list_program, parent, false);
        }
        TextView tanggal=(TextView)view.findViewById(R.id.fragment_program_start);
        tanggal.setText(listDataProgram2.get(_position).get_tanggal());

        TextView supervisor=(TextView)view.findViewById(R.id.fragment_program_supervisor);
        supervisor.setText(listDataProgram2.get(_position).get_supervisor());

        TextView nama = (TextView) view.findViewById(R.id.lv_program_name);
        nama.setText(listDataProgram2.get(_position).getNamaProgram());

        TextView email = ((TextView) view.findViewById(R.id.lv_program_lokasi));
        email.setText(listDataProgram2.get(_position).getLokasiProgram());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ApprovalProgramDetail.class);
                intent.putExtra("detail", listDataProgram2.get(_position));
                //sudah di approve jadi di dissaporve
                intent.putExtra("status",1);
                context.startActivity(intent);
            }
        });

        return view;
    }
}
