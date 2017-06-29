package pptik.startup.ghvmobile.Service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import pptik.startup.ghvmobile.R;
import pptik.startup.ghvmobile.User_Admin.Admin;
import pptik.startup.ghvmobile.User_Admin.ApprovalRelawan;
import pptik.startup.ghvmobile.User_Relawan.RelawanMenu;
import pptik.startup.ghvmobile.Setup.ApplicationConstants;

public class GCMNotificationIntentService extends IntentService {
    // Sets an ID for the notification, so it can be updated
    public static final int notifyID = 9001;
    NotificationCompat.Builder builder;
    SharedPreferences prefs;

    private  String theRole;
    public GCMNotificationIntentService() {
        super("GcmIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        prefs = getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);
        theRole = prefs.getString(ApplicationConstants.LEVEL_ID, "1");
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
                    .equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                    .equals(messageType)) {
                sendNotification("Deleted messages on server: "
                        + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
                    .equals(messageType)) {
                if(theRole.contains("2")) {

                    sendNotification("" + extras.get(ApplicationConstants.MSG_KEY));
                } else if (theRole.contains("1")){
                    sendNotification2("" + extras.get(ApplicationConstants.MSG_KEY));
                }
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg) {
        try {
            JSONObject jObj = new JSONObject(msg);
            String buat=jObj.getString("for");
            String judul="Wellcome";
            String content;
            if (buat.contains("1")){
                content="Your Volunteer status Approved";
                notifToRelawan(msg,1,judul,content);
            }else if (buat.contains("2"))  {
                content="there's New Program or Volunteer";
                notifToRelawan(msg,2,judul,content);
            }else if (buat.contains("3"))  {
                content="New Volunteer submited";
                notifToRelawan(msg,3,judul,content);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void notifToRelawan(String msg,int i,String judul,String content){
         if (i==1){
            Intent resultIntent = new Intent(this, RelawanMenu.class);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                    resultIntent, PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Builder mNotifyBuilder;
            NotificationManager mNotificationManager;

            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotifyBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle(judul)
                    .setContentText(content)
                    .setSmallIcon(R.drawable.logo_ic);
            // Set pending intent
            mNotifyBuilder.setContentIntent(resultPendingIntent);
            int defaults = 0;
            defaults = defaults | Notification.DEFAULT_LIGHTS;
            defaults = defaults | Notification.DEFAULT_VIBRATE;
            defaults = defaults | Notification.DEFAULT_SOUND;

            mNotifyBuilder.setDefaults(defaults);
            // Set the content for Notification
            mNotifyBuilder.setContentText(content);
            // Set autocancel
            mNotifyBuilder.setAutoCancel(true);
            // Post a notification
            Random random = new Random();
            int m = random.nextInt(9999 - 1000) + 1000;
            mNotificationManager.notify(notifyID, mNotifyBuilder.build());
        }else if (i==2){
            Intent resultIntent = new Intent(this, Admin.class);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                    resultIntent, PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Builder mNotifyBuilder;
            NotificationManager mNotificationManager;

            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotifyBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle(judul)
                    .setContentText(content)
                    .setSmallIcon(R.drawable.logo_ic);
            // Set pending intent
            mNotifyBuilder.setContentIntent(resultPendingIntent);
            int defaults = 0;
            defaults = defaults | Notification.DEFAULT_LIGHTS;
            defaults = defaults | Notification.DEFAULT_VIBRATE;
            defaults = defaults | Notification.DEFAULT_SOUND;

            mNotifyBuilder.setDefaults(defaults);
            // Set the content for Notification
            mNotifyBuilder.setContentText(content);
            // Set autocancel
            mNotifyBuilder.setAutoCancel(true);
            // Post a notification
            Random random = new Random();
            int m = random.nextInt(9999 - 1000) + 1000;
            mNotificationManager.notify(notifyID, mNotifyBuilder.build());
        }



        // Set Vibrate, Sound and Light

    }

    private void sendNotification2(String msg) {


        try {
            JSONObject jObj = new JSONObject(msg);
            String buat=jObj.getString("for");
            String judul=jObj.getString("judul");
            String content;
            if (buat.contains("1")){
                content="Ada Relawan Baru Mendaftar";
                notifToApproveRelawan(msg,1,judul,content);
            }else if (buat.contains("2"))  {
                content="Ada Relawan atau Program Baru disubmit";
                notifToApproveRelawan(msg,2,judul,content);
            }else if (buat.contains("3"))  {
                content="Ada Relawan Baru Mendaftar";
                notifToApproveRelawan(msg,3,judul,content);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void notifToApproveRelawan(String msg,int i,String judul,String content){


        if (i==1){
          Intent resultIntent = new Intent(this, ApprovalRelawan.class);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                    resultIntent, PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Builder mNotifyBuilder;
            NotificationManager mNotificationManager;

            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotifyBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle(judul)
                    .setContentText(content)
                    .setSmallIcon(R.drawable.logo_act);
            // Set pending intent
            mNotifyBuilder.setContentIntent(resultPendingIntent);
            int defaults = 0;
            defaults = defaults | Notification.DEFAULT_LIGHTS;
            defaults = defaults | Notification.DEFAULT_VIBRATE;
            defaults = defaults | Notification.DEFAULT_SOUND;

            mNotifyBuilder.setDefaults(defaults);
            // Set the content for Notification
            mNotifyBuilder.setContentText(content);
            // Set autocancel
            mNotifyBuilder.setAutoCancel(true);
            // Post a notification
            Random random = new Random();
            int m = random.nextInt(9999 - 1000) + 1000;
            mNotificationManager.notify(notifyID, mNotifyBuilder.build());
        }else if (i==2){
           Intent resultIntent = new Intent(this, Admin.class);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                    resultIntent, PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Builder mNotifyBuilder;
            NotificationManager mNotificationManager;

            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotifyBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle(judul)
                    .setContentText(content)
                    .setSmallIcon(R.drawable.logo_act);
            // Set pending intent
            mNotifyBuilder.setContentIntent(resultPendingIntent);
            int defaults = 0;
            defaults = defaults | Notification.DEFAULT_LIGHTS;
            defaults = defaults | Notification.DEFAULT_VIBRATE;
            defaults = defaults | Notification.DEFAULT_SOUND;

            mNotifyBuilder.setDefaults(defaults);
            // Set the content for Notification
            mNotifyBuilder.setContentText(content);
            // Set autocancel
            mNotifyBuilder.setAutoCancel(true);
            // Post a notification
            Random random = new Random();
            int m = random.nextInt(9999 - 1000) + 1000;
            mNotificationManager.notify(notifyID, mNotifyBuilder.build());
        }



        // Set Vibrate, Sound and Light

    }




}
