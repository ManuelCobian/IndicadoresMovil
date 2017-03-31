package com.example.joseantonio.indicadoresmovil;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Jose Antonio on 30/03/2017.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService  {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d( "From: " , remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d( "Message data payload: " , String.valueOf(remoteMessage.getData()));
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
           // Log.d("mensaje",remoteMessage.getNotification().getBody());
            showNotification(remoteMessage.getData().get("message"), remoteMessage.getData().get("id_indica"));
            //showNotification(remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void showNotification(String message,String id_indica) {
        long[] pattern = new long[]{1000, 500, 1000};
        NotificationCompat.Builder builder;
        // Sonido por defecto de notificaciones, podemos usar otro
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent i = new Intent(this,Scrolling_indicador.class);
        i.putExtra("id",Integer.parseInt(id_indica));
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT < 20) {
            builder = new NotificationCompat.Builder(this)
                    .setAutoCancel(true)
                    .setContentTitle("Indicadores")
                    .setContentText(message)
                    .setSmallIcon(R.mipmap.gob)
                    .setContentIntent(pendingIntent)
                    .setNumber(Integer.parseInt(id_indica));
        }

        else {

            builder = new NotificationCompat.Builder(this)
                    .setAutoCancel(true)
                    .setContentTitle("Indicadores")
                    .setContentText(message)
                    .setSmallIcon(R.drawable.grafica)
                    .setContentIntent(pendingIntent).setVibrate(pattern)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setNumber(Integer.parseInt(id_indica));

        }


        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(0,builder.build());
    }
}
