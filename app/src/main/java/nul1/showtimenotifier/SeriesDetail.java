package nul1.showtimenotifier;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

import libs.SeriesData;

/* created by nickhall 4/23/15 */

public class SeriesDetail extends ActionBarActivity {

    //Buttons
    Button btnClose;
    Button btnDelete;

    //Testing purposes to see if string can be passed.
    TextView textviewname;

    //TextView object for displaying all of the relevant series data
    TextView details_TextView;

    //Initialize a SeriesData class object to get and display all of the show information.
    SeriesData seriesData = new SeriesData();
    //Create dummy linkedhashmapobject
    HashMap<String, String> testData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series_detail);

        btnClose = (Button) findViewById(R.id.buttoncloseshowdetail);
        btnDelete = (Button) findViewById(R.id.deleteshowbutton);
        textviewname = (TextView) findViewById(R.id.detailtextview1);
        details_TextView = (TextView) findViewById(R.id.detail_AllDetails);

        //Button to return to saved shows list
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SeriesDetail.this, SavedShows.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                //.putExtra("keywords",actv.getText().toString()));
                //actv.setText("");
            }
        });

        //attempting to pass the name of the item clicked
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if (b != null) {
            String seriesNameStr = (String) b.get("SERIESNAME");
            testData = (HashMap<String, String>) b.get("SERIESDATA");
            textviewname.setText(seriesNameStr);
        }

        //Display all of the series data from the seriesData object
        details_TextView.setText(testData.toString());


        //Do something when delete button is clicked on
        /*
        btnDelete.setOnClickListener(this);

        Intent intent = getIntent();
        */
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_series_detail, menu);
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
