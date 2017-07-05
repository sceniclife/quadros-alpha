package com.example.quadros.alpha;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;


public class TitleActivity extends Activity
{
    // Set the display time, in milliseconds (or extract it out as a configurable parameter)
    private final int SPLASH_DISPLAY_LENGTH = 3000;
 
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.title_view);
    }
 
    @Override
    protected void onResume()
    {
        super.onResume();
        new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    // Create an Intent that will start the main activity.
                    Intent intent = new Intent(TitleActivity.this, MenuActivity.class);
                    TitleActivity.this.startActivity(intent);
                    
                    //Finish the splash activity so it can't be returned to.
                    TitleActivity.this.finish();
                    
                    //Apply splash exit (fade out) and main entry (fade in) animation transitions.
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                }
            }, SPLASH_DISPLAY_LENGTH);
    }
}