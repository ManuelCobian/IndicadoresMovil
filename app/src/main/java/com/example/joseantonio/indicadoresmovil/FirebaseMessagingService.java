package com.example.joseantonio.indicadoresmovil;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jose Antonio on 30/03/2017.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService  {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String mensaje;
        String nombre = null;
        Log.d("id",remoteMessage.getData().get("MyKey1"));
        Log.d("mensaje",remoteMessage.getData().get("message"));



        try {
            JSONObject o = new JSONObject(remoteMessage.getData().get("MyKey1"));
            mensaje= o.getString("id_indica");
            nombre=o.getString("titulo");


        } catch (JSONException e) {
            e.printStackTrace();
        }

        showNotification(remoteMessage.getData().get("message"),remoteMessage.getData().get("MyKey1"));

    }

    private void showNotification(String message,String titulo) {
        long[] pattern = new long[]{1000, 500, 1000};
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String mensaje="", indicador="",id_indica="";
        Intent i = new Intent();
        //i.putExtra("id",Integer.parseInt(id_indica));
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setClass(this,UpdatesActivity.class);



        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);



        try {
            JSONObject o = new JSONObject(message);
            mensaje= o.getString("message");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject o = new JSONObject(titulo);
            indicador= o.getString("titulo");

        } catch (JSONException e) {
            e.printStackTrace();
        }



        if (Build.VERSION.SDK_INT < 20) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setAutoCancel(true)
                    .setContentTitle("Indicadores Moviles")
                    .setContentText(mensaje)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.iconito))
                    .setSmallIcon(R.drawable.grafica)
                    .setContentIntent(pendingIntent)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setVibrate(pattern).addAction(android.R.drawable.ic_menu_info_details, "Detalles",pendingIntent);
        }else{

            Notification.Builder builder=new Notification.Builder(this)
                    .setAutoCancel(true)
                    .setContentTitle("Indicadores Moviles")
                    //  .setContentText(mensaje)
                   .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.iconito))
                    .setSmallIcon(R.drawable.grafica)
                    .setContentIntent(pendingIntent)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setOngoing(true)
                    .setLights(Color.RED, 1, 0);


            Notification notification = new Notification.InboxStyle(builder)
                    .addLine(indicador)
                    .setBigContentTitle("ultima actualizacion")
                    .setSummaryText("ver mas")
                    .build();

            NotificationManager notificationManager = (NotificationManager)getSystemService(this.NOTIFICATION_SERVICE);
            notificationManager.notify(121,notification);
        }



    }
}
