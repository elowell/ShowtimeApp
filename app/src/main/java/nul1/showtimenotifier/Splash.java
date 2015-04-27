package nul1.showtimenotifier;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by jklein on 4/22/15.  This particular page derived from
 * http://stackoverflow.com/questions/5486789/how-do-i-make-a-splash-screen
 */
public class Splash extends Activity {
    protected boolean _active = true;
    protected int _splashTime = 4000; // time to display the splash screen in ms



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //put update here

        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try
                {
                    int waited = 0;
                    while (_active && (waited < _splashTime))
                    {
                        sleep(100);
                        if (_active)
                        {
                            waited += 100;
                        }
                    }
                }
                catch (Exception e)
                {

                }
                finally
                {
                    startActivity(new Intent(Splash.this, Home_Screen.class));
                    finish();
                }
            };
        };
        splashTread.start();
    }
}