/* DBClient.java: class for interacting with series and episode database.
 *
 * Tim Farrell, tmf@bu.edu
 * 150417
 */

package libs;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    /* TEST STRUCTS */
    final static SeriesData the_following = new SeriesData() {
        @Override
        public void fill() {
            put("seriesname", "The Following");
            put("network", "Fox");
            put("nextairdate", "Mon Apr 20 09:00:00 EDT 2015");
        }
    };
    final static SeriesData orange_is_the_new_black = new SeriesData() {
        @Override
        public void fill() {
            put("seriesname", "Orange is the New Black");
            put("network", "Netflix");
            put("nextairdate", "Fri Jun 12 00:00:00 EDT 2015");
        }
    };
    final static SeriesData game_of_thrones = new SeriesData() {
        @Override
        public void fill() {
            put("seriesname", "Game of Thrones");
            put("network", "HBO");
            put("nextairdate", "Sun Apr 19 09:00:00 EDT 2015");
        }
    };
    final static SeriesData marco_polo = new SeriesData() {
        @Override
        public void fill() {
            put("seriesname", "Marco Polo (2014)");
            put("network", "Netflix");
            put("nextairdate", "None available.");
        }
    };

    public final static SeriesData[] TEST = {the_following,
                                             orange_is_the_new_black,
                                             game_of_thrones,
                                             marco_polo};

    /* SERIES TABLE */
    final static String SERIES_TABLE_NAME = "Series";
    final static String SERIES_ID   = "seriesid";
    final static String SERIES_NAME = "seriesname";
    final static String GENRE       = "genre";
    final static String OVERVIEW    = "overview";
    final static String STATUS      = "status";
    final static String DAYOFWEEK   = "dayofweek";
    final static String TIME        = "time";
    final static String NETWORK     = "network";
    final static String RUNTIME     = "runtime";
    final static String RATING      = "rating";
    final static String RATINGCOUNT   = "ratingcount";
    final static String ACTORS        = "actors";
    final static String CONTENTRATING = "contentrating";
    final static String FIRSTAIRED    = "firstaired";
    final static String NEXTAIRDATE   = "nextairdate";

    final static String[] series_col = { SERIES_ID, SERIES_NAME, GENRE, OVERVIEW, STATUS, DAYOFWEEK,
                                         TIME, NETWORK, RUNTIME, RATING, RATINGCOUNT, ACTORS,
                                         CONTENTRATING, FIRSTAIRED, NEXTAIRDATE                   };

    final private static String CREATE_SERIES_CMD =

        "CREATE TABLE series ("+ SERIES_ID + " TEXT PRIMARY KEY, "+ SERIES_NAME + " TEXT NOT NULL, "
                               + GENRE + " TEXT, "+ OVERVIEW + " TEXT, "+ STATUS + " TEXT, "
                               + DAYOFWEEK + " TEXT, "+ TIME + " TEXT, "+ NETWORK + " TEXT, "
                               + RUNTIME + " TEXT, "+ RATING + " TEXT, "+ RATINGCOUNT + " TEXT, "
                               + ACTORS + " TEXT, "+ CONTENTRATING + " TEXT, "
                               + FIRSTAIRED + " TEXT, "+ NEXTAIRDATE + " TEXT);";

    /* EPISODE TABLE *//*
    final static String EPISODE_TABLE_NAME = "episode";
    final static String EPISODE_ID   = "episodeid";
    final static String EPISODE_NAME = "episodename";
    final static String DATE         = "date";
    final static String[] episode_col = { EPISODE_ID, SERIES_ID, EPISODE_NAME, OVERVIEW, DATE };

    final private static String CREATE_EPISODE_CMD =

        "CREATE TABLE series ("+ EPISODE_ID + " TEXT PRIMARY KEY, "
                               + SERIES_ID + "TEXT FOREIGN KEY, "
                               + OVERVIEW + "TEXT, "+ DATE + "TEXT);";*/

    final private static String NAME = "user_favorites_db";
    final private static Integer VERSION = 1;
    final private Context mContext;

    public DBHelper(Context context) {
        super(context, NAME, null, VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SERIES_CMD);
        //db.execSQL(CREATE_EPISODE_CMD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // N/A
    }

    void deleteDatabase() {
        mContext.deleteDatabase(NAME);
    }
}