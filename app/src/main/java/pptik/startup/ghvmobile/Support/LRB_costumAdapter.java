package pptik.startup.ghvmobile.Support;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import pptik.startup.ghvmobile.User_Admin.ApprovalRelawan;
import pptik.startup.ghvmobile.User_Admin.ApprovalRelawanDetail;
import pptik.startup.ghvmobile.R;

/**
 * Created by GIGABYTE on 17/06/2016.
 */
public class LRB_costumAdapter extends BaseAdapter {

    ArrayList<DataUser2> listDataUser;
    Context context;
    private static LayoutInflater inflater=null;
    public LRB_costumAdapter(ApprovalRelawan ar, ArrayList<DataUser2> data) {
        listDataUser = new ArrayList<DataUser2>();
        listDataUser = data;
        context= ar;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return listDataUser.size();
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
            view = inflater.inflate(R.layout.lv_list_relawan, parent, false);
        }
        TextView joindate = (TextView) view.findViewById(R.id.lv_relawan_join_date);
        joindate.setText("Join Date :"+ listDataUser.get(_position).get_joindate());

        TextView nama = (TextView) view.findViewById(R.id.lv_relawn_name);
        nama.setText(listDataUser.get(_position).getNamaLengkap());

        TextView email = ((TextView) view.findViewById(R.id.lv_relawan_email));
        email.setText(listDataUser.get(_position).getEmail());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ApprovalRelawanDetail.class);
                intent.putExtra("detail", listDataUser.get(_position));
                intent.putExtra("status",2);
                context.startActivity(intent);

            }
        });

        return view;
    }
}
