package final_project.mobile.lecture.ma02_20141095;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LocationBR extends BroadcastReceiver {
    Appoint sData;

    @Override
    public void onReceive(Context context, Intent intent) {

        sData = (Appoint)intent.getSerializableExtra("sData");

        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(context);

        builder.setSmallIcon(R.drawable.ic_alarm_black_24dp).setTicker("It's time to go!").setWhen(System.currentTimeMillis())
                .setNumber(1).setContentTitle(sData.getTitle()).setContentText(sData.getContent())
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingIntent).setAutoCancel(true);

        notificationmanager.notify(1, builder.build());


    }
}
