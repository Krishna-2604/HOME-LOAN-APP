package com.example.loancalculator;

import android.app.AlarmManager;
import android.app.Notification ;
import android.app.NotificationChannel ;
import android.app.NotificationManager ;
import android.app.PendingIntent;
import android.content.BroadcastReceiver ;
import android.content.Context ;
import android.content.Intent ;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import com.example.loancalculator.MainActivity.*;

import static androidx.core.content.ContextCompat.getSystemService;
import static com.example.loancalculator.MainActivity.MyPREFERENCES;
import static  com.example.loancalculator.MainActivity. NOTIFICATION_CHANNEL_ID ;

public class MyNotificationPublisher extends BroadcastReceiver {
    public static String NOTIFICATION_ID = "notification-id" ;
    public static String NOTIFICATION = "notification" ;
    public static int month=0;
    SharedPreferences sharedpreferences;
    public void onReceive (Context context , Intent intent) {
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context. NOTIFICATION_SERVICE ) ;
        Notification notification = intent.getParcelableExtra( NOTIFICATION ) ;
        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            int importance = NotificationManager. IMPORTANCE_HIGH ;
            NotificationChannel notificationChannel = new NotificationChannel( NOTIFICATION_CHANNEL_ID , "NOTIFICATION_CHANNEL_NAME" , importance) ;
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel) ;
            Log.e("test","OnRecieve");
        }
        int id = intent.getIntExtra( NOTIFICATION_ID , 0 ) ;
        assert notificationManager != null;
        notificationManager.notify(id , notification) ;
        Log.e("test","OnRecieve else Part");
        month=month+1;
        Log.e("test","Month"+month);

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("month-cur", String.valueOf(month));
        editor.commit();
        String channel = (sharedpreferences.getString("month-cur",""));
        Log.e("test","cur month"+channel);

    }
}