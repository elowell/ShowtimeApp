package nul1.showtimenotifier;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationPublisher  extends BroadcastReceiver 
{
	//keys for the intent extras
    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";

    public void onReceive(Context context, Intent intent) 
    {
    	//get the system notification manager
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        //get the notification stored in the intent
        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        //get the notification id
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        //push the notification to the notification manager
        notificationManager.notify(id, notification);
        /*if(nul1.showtimenotifier.NotificationSettings.CHECKCHECK==0)
        {
            notificationManager.cancel(Integer.parseInt(NOTIFICATION_ID));
        }*/
    }
}
