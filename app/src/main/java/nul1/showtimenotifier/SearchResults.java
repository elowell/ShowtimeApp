package nul1.showtimenotifier;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import libs.SeriesData;


public class SearchResults extends ActionBarActivity {

    //Handler for all posts--here implements a delay for save button
    private Handler mHandler = new Handler();


    //save context for use in anonymous class
    final Context mContext = this;

    //init data struct to store full series data record
    SeriesData seriesData = new SeriesData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //required
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        //Create database for storing the shows when app is started
        //final DBHelper showdb = new this;

        //setup UI elements
        Button search_home_button = (Button)findViewById(R.id.backbutton);
        final TextView resultsView = (TextView) findViewById(R.id.results_view);
        Button getFullRecord = (Button) findViewById(R.id.get_full_record);
        final Button saveToFavs    = (Button) findViewById(R.id.save_to_favs);

        //make textview scrollable
        resultsView.setMovementMethod(new ScrollingMovementMethod());

        //get series data, which was sent as extra
        Bundle extras = this.getIntent().getExtras();
        String seriesDataStr  = (String) extras.get("seriesData");
        final String seriesID = (String) extras.get("seriesID");

        //display in textview
        resultsView.setText(seriesDataStr);


        //Check if seriesID is present. If seriesID not present, then no results were returned
        //If no results were returned, deactivate save button.
        if(seriesID==null)
        {
            saveToFavs.setEnabled(false);
            saveToFavs.getBackground().setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY);
            getFullRecord.setEnabled(false);
            getFullRecord.getBackground().setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY);
        }

        //set listener for back
        search_home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchResults.this,Home_Screen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                //.putExtra("keywords",actv.getText().toString()));
                //actv.setText("");
            }
        });

        //set listener for get_full_record
        getFullRecord.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //init requestqueue
                RequestQueue queue = Volley.newRequestQueue(mContext);

                //construct url
                String url = "http://thetvdb.com/api/" + seriesData.TVDB_API_KEY
                                                       + "/series/" + seriesID + "/all/";

                //init string request
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {           //if no error occurs
                            @Override
                            public void onResponse(String response) {   //when response is returned
                                //insert XML tokens into series data struct
                                seriesData.insertSelectXML(response.split(">"));

                                //reset text view
                                resultsView.setText(seriesData.toString());
                            }
                        }, new Response.ErrorListener() {               //if error occurs
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error: ", error.toString());             //log error
                    }
                });

                //add request to requestqueue
                queue.add(stringRequest);
            }
        });

        //set listener for save
        saveToFavs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save full series data to db
                //saveToFavsDB(seriesData);
                //showdb.AddShow(seriesData);
                saveToFavs.setEnabled(false);
                saveToFavs.getBackground().setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY);
                mHandler.postDelayed(mLaunchTask,1000);
            }

            //Launch activity after delay
            private Runnable mLaunchTask = new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SearchResults.this,Home_Screen.class);
                    startActivity(intent);
                }
            };
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_results, menu);
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
}
