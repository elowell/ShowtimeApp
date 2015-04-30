package nul1.showtimenotifier;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import libs.DBOperations;
import libs.SeriesData;

public class NotificationSettings extends ActionBarActivity {
    public static int CHECKCHECK;
    //private static int DELAY_IN_MILLISECONDS = 5000;
    private NotificationSettings THIS = this;
    protected int mNotificatonID = 1;
    private CheckBox chkWeek, chkDay, chkHour;
    private Button SaveSettings;
    protected int daycheck=0;
    protected int weekcheck=0;
    protected int hourcheck=0;
    //public int CHECKCHECK;
    public static int NUMBEROFSHOWS;
    public int DELAY;
    final Context mContext = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);

        Button search_home_button = (Button)findViewById(R.id.button);
        search_home_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotificationSettings.this,Home_Screen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        final List<SeriesData> listOfSeries = getDataForListView();
        NUMBEROFSHOWS= listOfSeries.size();


        chkWeek   = (CheckBox) findViewById(R.id.radioButtonWeek);
        chkDay     = (CheckBox) findViewById(R.id.radioButtonDay);
        chkHour    = (CheckBox) findViewById(R.id.radioButtonHour);
        SaveSettings = (Button) findViewById(R.id.button6);



        SaveSettings.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                //Save settings to DB on click

                //Send user back to homepage
                Intent intent = new Intent(NotificationSettings.this,Home_Screen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                //PUT LOOP through number of shows here.
                for(int i=0; i<NUMBEROFSHOWS; i++) {
                    //gets timedifference between next episode and now
                    int timediff=daysTillShow(listOfSeries.get(i));
                    //checks if day box is checked
                    if (chkDay.isChecked()) {
                        DELAY=86400000;
                        chkDay.setChecked(chkDay.isChecked());
                        daycheck = 1;//allows notification to be created
                        CHECKCHECK=daycheck;
                        showNotificationScheduler(listOfSeries.get(i),timediff-DELAY,daycheck);
                    } else {
                        daycheck = 0;
                        CHECKCHECK=daycheck;
                    showNotificationScheduler(listOfSeries.get(i),timediff-DELAY,daycheck);
                    }

                    //checks if week box is checked
                    if (chkWeek.isChecked()) {
                        DELAY=604800000;
                        chkWeek.setChecked(chkWeek.isChecked());
                        weekcheck = 1;//allows notification to be created
                        CHECKCHECK=weekcheck;
                        showNotificationScheduler(listOfSeries.get(i),timediff-DELAY,weekcheck);

                    } else {
                        weekcheck = 0;
                        CHECKCHECK=weekcheck;
                        showNotificationScheduler(listOfSeries.get(i),timediff-DELAY,weekcheck);
                    }

                    //checks if hour box is checked
                    if (chkHour.isChecked()) {
                        Toast toast = Toast.makeText(getApplicationContext(), "HOURCHECK", Toast.LENGTH_LONG);
                        toast.show();
                        DELAY=3600000;
                        chkHour.setChecked(chkHour.isChecked());
                        hourcheck = 1; //allows notification to be created
                        CHECKCHECK=hourcheck;
                        showNotificationScheduler(listOfSeries.get(i),timediff-DELAY,hourcheck);
                    } else {
                        hourcheck = 0;
                        CHECKCHECK=hourcheck;
                        showNotificationScheduler(listOfSeries.get(i),timediff-DELAY,hourcheck);
                    }

                }
            }
        });

    }
    public List<SeriesData> getDataForListView() {
        List<SeriesData> listOfSeries = new ArrayList<SeriesData>();

        DBOperations db = new DBOperations(mContext);
        listOfSeries = db.getDataForListView();

        return listOfSeries;
    }
public void showNotificationScheduler(SeriesData series, int delay, int killer) {
    //for parsing strings with dates of specified format
    SimpleDateFormat date_parser = new SimpleDateFormat("EEE MMM dd hh:mm:ss z yyyy");

    mNotificatonID =Integer.parseInt(series.get("seriesid"));
    Notification newNotification = null;
    try {
        newNotification = THIS.getNotification("New Episode at: " + date_parser.parse(series.get("nextairdate")), series.get("seriesname"));
    } catch (ParseException e) {
        e.printStackTrace();
    }
    //schedule the notification
    THIS.scheduleNotification(newNotification, delay, killer);
}
    public int daysTillShow(SeriesData series) {

        if(series.get("status").equals("Ended")) {
            return -1;
        }
        //int daysBetween = 0;
        Calendar today = Calendar.getInstance();        //get today's date
        //Set seconds and milliseconds to 00:00:00
        //today.set(Calendar.SECOND, 0);
        //today.set(Calendar.MILLISECOND, 0);
        Calendar episode_date = Calendar.getInstance(); //to store episode date
        //Calendar episode_time = Calendar.getInstance(); //to store episode time
        //for parsing strings with dates of specified format
        SimpleDateFormat date_parser = new SimpleDateFormat("EEE MMM dd hh:mm:ss z yyyy");
        try {
            episode_date.setTime(date_parser.parse(series.get("nextairdate")));
        } catch (Exception e) {
            //TODO Auto-Generated catch block  ;
        }
        //Set episode date and time to 00:00:00
        //episode_date.set(Calendar.SECOND, 0);
        //episode_date.set(Calendar.MILLISECOND, 0);
        int timediffms= (int) (episode_date.getTimeInMillis()-today.getTimeInMillis());
        return timediffms;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notification_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Notification getNotification(String content, String showTitle)
    {
        //creates a notification with the given text
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("New Episode of "+ showTitle);
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.tvsnlogo);
        return builder.build();
    }

    public void scheduleNotification(Notification notification, int delay, int killer)
    {
        //create an intent that will be used to call the notification publisher
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        //add the notification id to the intent
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, mNotificatonID);
        //if we keep notification ID the same it will update the old notification with that id
        //mNotificatonID++;
        //add the notification object we're going to present
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        //wrap the intent in a pending intent so it can be used by a different application
        //notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_KILLER, killer);
        //if killer==1 then notification is made
        if(killer==1) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

            //figure out when the intent should be used
            long futureInMillis = SystemClock.elapsedRealtime() + delay;
            //get the alarm manager
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            //give the pending intent to the alarm manager and tell it to use it at the given time
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
        }
    }
}
