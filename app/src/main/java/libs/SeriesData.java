/* SeriesData.java: dictionary data structure to act as in-between for app Activities/ Services
 *                  and database.
 *
 * Tim Farrell, tmf@bu.edu
 * 150417
 */

package libs;

import android.util.Log;

import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Calendar;

import java.text.SimpleDateFormat;


public class SeriesData extends LinkedHashMap<String, String> {

    /* Fields */
    //tag labels of interest to be extracted from TVDB XML
    ArrayList<String> fields = new ArrayList<String>();

    public final static String TVDB_API_KEY = "531FC560FB062CB3";

    /*
    //Trying to add some data fields to see if this will make database work.
    public static final String TABLE = "series";
    public static final String SERIES_NAME = "series_name";
    public static final String SERIES_ID = "series_id";

    public String series_name;
    public String series_id;*/

    /* Constructors */
    //no arg constructor: adds default tag labels
    public SeriesData() {
        fields.add("seriesname");
        fields.add("seriesid");
        fields.add("genre");
        fields.add("network");
        fields.add("overview");
        fields.add("status");
        fields.add("nextairdate");
        fields.add("runtime");
        fields.add("rating");
        fields.add("ratingcount");
        fields.add("actors");
        fields.add("contentrating");
        fields.add("firstaired");
        fields.add("dayofweek");
        fields.add("time");
    }

    public SeriesData(String[] _fields) {
        this();                         //add default fields
        for (String field : _fields)     //then add custom fields
            fields.add(field);
    }


    /* Methods */
    //method to be customized by anonymous class declaration
    public void fill() { }

    //inserts XML from a theTVDB XML string
    public void insertSelectXML(String[] strs) {

        String key, value, key_dummy;                   //dummy vars to hold tokens

        for(int i = 1; i < strs.length - 1; i += 2) {   //foreach token pair

            //clean tokens and add to dummies
            key   = strs[i].toLowerCase().replaceAll("\\W*", "");
            value = strs[i+1].replaceAll("</[a-zA-Z]*", "");

            //process dummies further
            if(key.contains("_"))
                key = key.substring(key.indexOf('_') + 1);
            if(value.contains("_"))
                value = value.substring(0, value.indexOf('_'));

            //if key token is in tag labels and if value of key not yet init
            if(fields.contains(key) && this.get(key) == null)
                this.put(key, value);

            else if (key.contains("episodenumber")) {
                key = key + " " + value;

                i = i + 2;

                key_dummy   = strs[i].toLowerCase().replaceAll("\\W*", "");
                value       = strs[i+1].replaceAll("</[a-zA-Z]*", "");

                if(key_dummy.contains("firstaired"))
                    this.put(key, value);
            }
        }

        //put next air date, if episode information avail
        this.putNextAirDate();
    }

    //inserts XML from a theTVDB XML string
    public void insertAllXML(String[] strs) {
        String key, value;                              //dummy vars to hold tokens

        for(int i = 1; i < strs.length - 1; i += 2) {   //foreach token pair

            //clean tokens and add to dummies
            key   = strs[i].toLowerCase().replaceAll("\\W*", "");
            value = strs[i+1].replaceAll("</[a-zA-Z]*", "");

            //insert into dict
            this.put(key, value);
        }
    }

    //parses episode data and fills the nextairdate field, if there is a next episode
    private void putNextAirDate() {

        final String TAG = "putNextAirDate: ";
        String debug = "";

        Calendar today = Calendar.getInstance();        //get today's date
        Calendar episode_date = Calendar.getInstance(); //to store episode date
        Calendar episode_time = Calendar.getInstance(); //to store episode time

        //for parsing strings with dates of specified format
        SimpleDateFormat date_parser = new SimpleDateFormat("yyyy-MM-dd");
        //for parsing strings with times of specified format
        SimpleDateFormat time_parser = new SimpleDateFormat("hh:mm a");

        Iterator entries = this.entrySet().iterator(); //loop thru entries
        while(entries.hasNext()) {
            LinkedHashMap.Entry entry = (LinkedHashMap.Entry) entries.next();

            //fill episode date
            if (entry.getKey().toString().contains("episode")) {
                try {
                    episode_date.setTime(date_parser.parse(entry.getValue().toString()));
                } catch (Exception e) {
                    Log.v(TAG, "Could not parse date.");
                }

                //if episode after today, then episode_date is the nextairdate
                if (today.before(episode_date))
                    break;
            }

            //get air time from time field
            if (entry.getKey().toString().contains("time")) {
                try {
                    episode_time.setTime(time_parser.parse(entry.getValue().toString()));
                } catch (Exception e) {
                    Log.v(TAG, "Could not parse time.");
                }
            }
        }

        //if episode_date after today and was changed from initial init
        if ( (today.before(episode_date) )
                && (today.get(Calendar.DATE)     != episode_date.get(Calendar.DATE)
                    || today.get(Calendar.MONTH) != episode_date.get(Calendar.MONTH)
                    || today.get(Calendar.YEAR)  != episode_date.get(Calendar.YEAR)) ) {

            //add episode time to date and append to data struct
            episode_date.add(Calendar.HOUR, episode_time.get(Calendar.HOUR));
            this.put("nextairdate", episode_date.getTime().toString());
        }
        else if ( today.get(Calendar.DATE)     != episode_date.get(Calendar.DATE)
                    || today.get(Calendar.MONTH) != episode_date.get(Calendar.MONTH)
                    || today.get(Calendar.YEAR)  != episode_date.get(Calendar.YEAR)) {
            this.put("nextairdate", "none available");
        }
    }


    //prints entries in clean format
    @Override
    public String toString() {
        String str = "";

        for (String field : fields) {
            if (this.get(field) != null)
                str += field + ": " + this.get(field) + "\n\n";
        }

        Iterator entries = this.entrySet().iterator();
        while(entries.hasNext()) {
            LinkedHashMap.Entry entry = (LinkedHashMap.Entry) entries.next();

            if (!fields.contains(entry.getKey().toString()))
                str += entry.getKey().toString() + ": " + entry.getValue().toString() + "\n\n";
        }

        if(str != "")   return str;
        else            return "\nThere were no results for your search.";
    }

    //prints entries in fancy format
    public String toStringFancy() {
        String str = "";

        for (String field : fields) {
            if (this.get(field) != null) {
                if (field != "seriesid")
                    str += this.get(field) + "\n\n";
            }
        }

        Iterator entries = this.entrySet().iterator();
        while(entries.hasNext()) {
            LinkedHashMap.Entry entry = (LinkedHashMap.Entry) entries.next();

            if (!fields.contains(entry.getKey().toString()))
                str += entry.getKey().toString() + ": " + entry.getValue().toString() + "\n\n";
        }

        if(str != "")   return str;
        else            return "\nThere were no results for your search.";
    }
}

