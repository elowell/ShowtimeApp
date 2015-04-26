package nul1.showtimenotifier;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.net.URLEncoder;

import libs.SeriesData;

public class Home_Screen extends ActionBarActivity {
    //save context for use in anonymous classes
    final Context mContext = this;

    //data struct to store series data
    final SeriesData seriesData = new SeriesData();

    LinearLayout mLinearLayout;


    //onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //required
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home__screen);

        //String[] shows=getResources().getStringArray(R.array.list_of_shows);
        //ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);

        //actv=(AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
          //      actv.setadapter(adapter);

        //init search textview and button
        final Button search_show_button = (Button) findViewById(R.id.searchbutton);
        final AutoCompleteTextView actv = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);

        actv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                search_show_button.callOnClick();
                return false;
            }
        });


        //set button to send request to TVDB
        search_show_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //init requestqueue
                RequestQueue queue = Volley.newRequestQueue(mContext);

                //get string from edittext and encode
                String query = "";
                try {
                    query = URLEncoder.encode(actv.getText().toString(), "utf-8");
                } catch (Exception e)
                {  Log.d("Error: ", e.toString()); }

                //add query to url
                String url = "http://thetvdb.com/api/GetSeries.php?seriesname=" + query;

                //init string request
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {           //if no error occurs
                            @Override
                            public void onResponse(String response) {   //when response is returned
                                //insert XML tokens into series data struct
                                seriesData.insertSelectXML(response.split(">"));

                                //set intent to open results screen
                                Intent intent = new Intent(Home_Screen.this, SearchResults.class);

                                //give intent series data string and seriesid
                                intent.putExtra("seriesData", seriesData.toString());
                                intent.putExtra("seriesID", seriesData.get("seriesid"));

                                //open search results activity
                                startActivity(intent);
                            }
                        }, new Response.ErrorListener() {               //if error occurs
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("Error: ", error.toString());             //log error
                    }
                });

                //add request to requestqueue
                queue.add(stringRequest);

                //.putExtra("keywords",actv.getText().toString()));
                //actv.setText("");
            }
        });

        Button notification_settings_button = (Button)findViewById(R.id.notificationsettingsbutton);
        notification_settings_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home_Screen.this,NotificationSettings.class);
                startActivity(intent);
                //.putExtra("keywords",actv.getText().toString()));
                //actv.setText("");
            }
        });

        Button shows_list_button = (Button)findViewById(R.id.showslistbutton);
        shows_list_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home_Screen.this,SavedShows.class);
                startActivity(intent);
                //.putExtra("keywords",actv.getText().toString()));
                //actv.setText("");
            }
        });

        //Adds logo image
        ImageView i = new ImageView(this);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home__screen, menu);
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



//    public void onSearchButtonClicked(View view){
//        Intent i = new Intent(this,SearchResults.class);
//        startActivity(i.putExtra("keywords",actv.getText().toString()));
//        actv.setText("");
//    }
//
}
