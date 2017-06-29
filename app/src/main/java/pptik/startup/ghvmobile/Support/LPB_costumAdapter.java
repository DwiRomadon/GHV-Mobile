package pptik.startup.ghvmobile.Support;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.content.Intent;
import java.util.ArrayList;

import pptik.startup.ghvmobile.User_Admin.ApprovalProgram;
import pptik.startup.ghvmobile.User_Admin.ApprovalProgramDetail;
import pptik.startup.ghvmobile.R;

/**
 * Created by GIGABYTE on 17/06/2016.
 */
public class LPB_costumAdapter extends BaseAdapter {

    ArrayList<DataProgram> listDataProgram;
    Context context;
    private static LayoutInflater inflater=null;
    public LPB_costumAdapter(ApprovalProgram ap, ArrayList<DataProgram> data) {
        listDataProgram = new ArrayList<DataProgram>();
        listDataProgram = data;
        context= ap;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return listDataProgram.size();
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
        tanggal.setText(listDataProgram.get(_position).get_tanggal());

        TextView supervisor=(TextView)view.findViewById(R.id.fragment_program_supervisor);
        supervisor.setText(listDataProgram.get(_position).get_supervisor());

        TextView nama = (TextView) view.findViewById(R.id.lv_program_name);
        nama.setText(listDataProgram.get(_position).getNamaProgram());

        TextView email = ((TextView) view.findViewById(R.id.lv_program_lokasi));
        email.setText(listDataProgram.get(_position).getLokasiProgram());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ApprovalProgramDetail.class);
                intent.putExtra("detail", listDataProgram.get(_position));
                //belum di approve jadi harus di approve
                intent.putExtra("status",2);
                context.startActivity(intent);
            }
        });

        return view;
    }
}
