package pptik.startup.ghvmobile.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by hynraon 6/13/16.
 */
public class WarningDialog {
    private Context context;
    public WarningDialog(Context _context){
        context = _context;
    }

    public void showWarning(String title, String msg, final boolean closeActivity){
        final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setCancelable(false);
        alertDialog.setMessage(msg);
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