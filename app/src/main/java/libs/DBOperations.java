package libs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by nickhall on 4/26/15.
 */
public class DBOperations {

    //All database functions, including write, retrieve, update, delete

    private DBHelper dbHelper;

    //Context for DBHelper class
    public DBOperations(Context context) {   dbHelper = new DBHelper(context);   }

    //Write (save) show to database.
    public int saveShow(SeriesData series) {
        //Get writeable database
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //Get values to be stored in database from saved show.
        ContentValues values = new ContentValues();

        Iterator entries = series.entrySet().iterator();
        while(entries.hasNext()) {
            LinkedHashMap.Entry entry = (LinkedHashMap.Entry) entries.next();

            if(series.fields.contains(entry.getKey().toString()))
                values.put(entry.getKey().toString(), entry.getValue().toString());
        }

        Log.d("DBHelper.saveShow: ", values.toString());

        //Inserting a row into the database table.
        long show_Id = db.insert("Series", null, values);
        db.close();
        return (int) show_Id;
    }


    public void deleteShow(String series_name) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("Series", "seriesname=?", new String[] {String.valueOf(series_name)});
        db.close();


    }




    //gets all saved series and returns in datalistview format
    public List<SeriesData> getDataForListView() {
        //List for storing the data
        List<SeriesData> listOfSeries = new ArrayList<SeriesData>();

        //open db in read mode
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //Create a selection query
        String selectQuery = "SELECT seriesid, seriesname, genre, overview, status, "
                    + "dayofweek, time, network, runtime, rating, ratingcount, actors, "
                    + "contentrating, firstaired, nextairdate FROM Series;";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                SeriesData series = new SeriesData();
                series.put("seriesid",cursor.getString(0));
                series.put("seriesname",cursor.getString(1));
                series.put("genre",cursor.getString(2));
                series.put("overview",cursor.getString(3));
                series.put("status",cursor.getString(4));
                series.put("dayofweek",cursor.getString(5));
                series.put("time",cursor.getString(6));
                series.put("network",cursor.getString(7));
                series.put("runtime",cursor.getString(8));
                series.put("rating",cursor.getString(9));
                series.put("ratingcount",cursor.getString(10));
                series.put("actors",cursor.getString(11));
                series.put("contentrating",cursor.getString(12));
                series.put("firstaired",cursor.getString(13));
                series.put("nextairdate",cursor.getString(14));
                listOfSeries.add(series);
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return listOfSeries;
    }




}
