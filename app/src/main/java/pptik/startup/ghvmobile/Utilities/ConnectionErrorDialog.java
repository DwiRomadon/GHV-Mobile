package pptik.startup.ghvmobile.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by hynraon 6/13/16.
 */
public class ConnectionErrorDialog {
    private Context context;
    public ConnectionErrorDialog(Context _context){
        context = _context;
    }

    public void showWarning(final boolean closeActivity){
        final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(context);
        alertDialog.setTitle("Gagal memuat permintaan");
        alertDialog.setCancelable(false);
        alertDialog.setMessage("Koneksi Anda bermasalah, cek kembali koneksi Internet Anda dan coba kembali");
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(closeActivity){
                    dialog.dismiss();
                    ((Activity)context).finish();
                }else {
                    dialog.dismiss();
                }
            }
        });
        alertDialog.show();
    }
}