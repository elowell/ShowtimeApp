package nul1.showtimenotifier;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import libs.DBOperations;
import libs.SeriesData;


public class SavedShows extends ActionBarActivity {

    final Context mContext = this;

    //Trying to get intent passing to work

    //init adapter for the listview
    SeriesDataListAdapter seriesDataAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_shows);

        //Assign adapter for list data
        seriesDataAdapter = new SeriesDataListAdapter();

        //init layout
        final ListView seriesDataView = (ListView)findViewById(R.id.listview1);
        seriesDataView.setAdapter(seriesDataAdapter);

        seriesDataView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                SeriesData series = seriesDataAdapter.getSeriesDataItem(arg2);
                Intent objIntent = new Intent(getApplicationContext(), SeriesDetail.class);
                String seriesName = series.get("seriesname");

                //Pass the name of the show to the activity
                objIntent.putExtra("SERIESNAME", seriesName);
                objIntent.putExtra("SERIESDATA", series);

                startActivity(objIntent);
            }
        });


        //get seriesdata for user from DB
        //SeriesData[] user_favs = DBHelper.getSeriesData();
        /*                         ^ not implemented yet, use test structs in DBHelper */

        Button home_button = (Button)findViewById(R.id.home_button);
        home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SavedShows.this,Home_Screen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

    }


    public class SeriesDataListAdapter extends BaseAdapter {

        //Will eventually call getdata function from DBHelper class
        //Currently will create data based on DBHelper.TEST
        //
        //List<seriesList> seriesDataList = getAllSeries();

        //Create List object of class SeriesData
        //This list is what stores values extracted from the database
        //The adapter then passes this list to the custom listview object for display purposes
        //The function getDataForListView() assembles the list from the database.
        List<SeriesData> listOfSeries = getDataForListView();

        @Override
        public int getCount() {
            //TODO Auto-generated method stub
            return listOfSeries.size();
        }

        @Override
        public SeriesData getItem(int arg0) {
            //TODO Auto-generated method stub
            return listOfSeries.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        //Create a view associated with each object in the list. This view gets passed to
        //the listview object. Data fields for the view are extracted from the list.
        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {
            LayoutInflater inflater;
            inflater = (LayoutInflater) SavedShows.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.series_data_list_view, arg2, false);
            if(arg1==null) {
                inflater = (LayoutInflater) SavedShows.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                arg1 = inflater.inflate(R.layout.series_data_list_view, arg2, false);
            }
            ImageView seriesImage = (ImageView)arg1.findViewById(R.id.imageview1);
            TextView seriesName = (TextView)arg1.findViewById(R.id.seriesnametextview);
            TextView daysUntil = (TextView)arg1.findViewById(R.id.datecountdowntextview);
            SeriesData series = listOfSeries.get(arg0);

            //Series Photo
            seriesImage.setImageResource(R.mipmap.ic_launcher);

            //Set the name of the series
            seriesName.setText(series.get("seriesname"));

            //If-else statements for displaying right number for days.
            long days = daysUntilNextShowing(series);
            if (days == 0) {
                daysUntil.setText("NA");
            }
            else if (days == 1) {
                daysUntil.setText("" + days + " Day");
            }
            else {
                daysUntil.setText("" + days + " Days");
            }
            return arg1;
        }

        //This code returns location in the list, essentially allowing the ID of the item
        //clicked on to be passed to the detail function.
        public SeriesData getSeriesDataItem(int position) {
            return listOfSeries.get(position);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_saved_shows, menu);
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


    //This function generates a list object from the SQLite database
    //At this point, it generates it from the TEST structure.
    //This is passed to the view creating function above.
    public List<SeriesData> getDataForListView() {
        List<SeriesData> listOfSeries = new ArrayList<SeriesData>();

        DBOperations db = new DBOperations(mContext);
        listOfSeries = db.getDataForListView();

        return listOfSeries;
    }


    //This function calculates how many days remain until a show starts
    //This number is then passed to the datecountdown textobject.
    //This allows quick visualization of when the next episode is in listview
    //Pass it a series variable with a next showing field, and it will calculate.
    public long daysUntilNextShowing(SeriesData series) {
        long daysBetween = 0;
        Calendar today = Calendar.getInstance();        //get today's date
        //Set date back to time 00:00:00

        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        Calendar episode_date = Calendar.getInstance(); //to store episode date
        //Calendar episode_time = Calendar.getInstance(); //to store episode time

        //for parsing strings with dates of specified format
        SimpleDateFormat date_parser = new SimpleDateFormat("EEE MMM dd hh:mm:ss z yyyy");

        try {
            episode_date.setTime(date_parser.parse(series.get("nextairdate")));
        } catch (Exception e) {
          //TODO Auto-Generated catch block  ;
        }

        //Increment today's date and counter as long as it is still before the episode date
        while(today.before(episode_date))
        {
            today.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }
        return daysBetween;
    }

}

