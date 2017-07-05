package com.example.quadros.alpha;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class MenuActivity extends Activity {
	
	static final int DIALOG_DIFFICULTY_ID = 0;
	static final int DIALOG_OPTION_ID = 1;
	
	private SharedPreferences mPrefs;
	
	private boolean mSfx = true;
	private boolean mMusic = true;
	private boolean[] checked = {mMusic, mSfx};
	
	private MediaPlayer mediaPlayer;
	
	//0 = easy; 1 = medium; 2 = hard;
	private int mDifficulty = 0;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_view);
        
		Log.d("MENU", "in onCreate Menu");
		
		mediaPlayer = MediaPlayer.create(this, R.raw.menumusic);
		mediaPlayer.setLooping(true);
		mediaPlayer.setVolume(0.2f, 0.2f);
        mPrefs = getSharedPreferences("prefs", MODE_PRIVATE);
        mMusic = mPrefs.getBoolean("mMusic", true);
		mSfx = mPrefs.getBoolean("mSfx", true);
		mDifficulty = mPrefs.getInt("mDifficulty", 0);
    }
    
    
    @Override
	protected void onPause() {
    	super.onPause();
		Log.d("MENU", "in onPause Menu");
    	SharedPreferences.Editor ed = mPrefs.edit();
        ed.putBoolean("mSfx", mSfx);
        ed.putBoolean("mMusic", mMusic);
        ed.putInt("mDifficulty", mDifficulty);
        ed.commit();
        
		mediaPlayer.pause();
    }
    
    @Override
	protected void onStop() {
    	super.onStop();
		Log.d("MENU", "in onStop Menu");
    	SharedPreferences.Editor ed = mPrefs.edit();
        ed.putBoolean("mSfx", mSfx);
        ed.putBoolean("mMusic", mMusic);
        ed.putInt("mDifficulty", mDifficulty);
        ed.commit();
    }
    
    @Override
	protected void onResume() {
    	super.onResume();
		Log.d("MENU", "in onResume Menu");
        mPrefs = getSharedPreferences("prefs", MODE_PRIVATE);
        mMusic = mPrefs.getBoolean("mMusic", true);
		mSfx = mPrefs.getBoolean("mSfx", true);
		checked[0] = mMusic;
		checked[1] = mSfx;
		Log.d("menu mSfx", mSfx+"");
		Log.d("menu mMusic", mMusic+"");
		mDifficulty = mPrefs.getInt("mDifficulty", 0);
		
		if (mMusic) {
			mediaPlayer.start();
		}
    }
    
    /*@Override
	protected void onStart() {
    	super.onStart();
		Log.d("MENU", "in onStart Menu");
        mPrefs = getSharedPreferences("prefs", MODE_PRIVATE);
        mMusic = mPrefs.getBoolean("mMusic", true);
		mSfx = mPrefs.getBoolean("mSfx", true);
		mDifficulty = mPrefs.getInt("mDifficulty", 0);
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		switch(id) {
			case DIALOG_DIFFICULTY_ID:
				dialog = createDifficultyDialog(builder);
				break;
			case DIALOG_OPTION_ID:
				dialog = createOptionDialog(builder);
				break;
		}

		if(dialog == null)
			Log.d("Dialog", "Uh oh! Dialog is null");
		else
			Log.d("Dialog", "Dialog created: " + id + ", dialog: " + dialog);
		return dialog;        
	}
    
    /* ==================== */
    /*      Activities      */
    /* ==================== */
    
    // button redirects to main gameplay screenDIALOG_QUIT_ID
	public void playAction(View v) {
		//mSounds.play(mSoundIDMap.get(R.raw.bgmusic), (float)0.1, (float)0.1, 1, 0, 1);
		
		new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                // Create an Intent that will start the main activity.
                Intent intent = new Intent(MenuActivity.this, PlayActivity.class);
                MenuActivity.this.startActivity(intent);
                
                //Apply splash exit (fade out) and main entry (fade in) animation transitions.
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                
            }
        }, 0);
	}
	
	// button redirects to progress chart
	public void progressAction(View v) {
		new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                // Create an Intent that will start the main activity.
                Intent intent = new Intent(MenuActivity.this, ProgressActivity.class);
                MenuActivity.this.startActivity(intent);
                
                //Apply splash exit (fade out) and main entry (fade in) animation transitions.
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                
            }
        }, 0);
	}

	// button redirects to how to play
	public void howtoAction(View v) {
		
		new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                // Create an Intent that will start the main activity.
                Intent intent = new Intent(MenuActivity.this, HowToActivity.class);
                MenuActivity.this.startActivity(intent);
                
                //Apply splash exit (fade out) and main entry (fade in) animation transitions.
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                
            }
        }, 0);
	}
	
	
    /* ======================= */
    /*      Partial Views      */
    /* ======================= */
	
	public void modesAction(View v) {
		showDialog(DIALOG_DIFFICULTY_ID);
	}
	
	public void optionsAction(View v) {
		showDialog(DIALOG_OPTION_ID);
	}
	
    /* ======================= */
    /*      Dialog Handlers    */
    /* ======================= */
    
	private Dialog createDifficultyDialog(AlertDialog.Builder builder) {
		builder.setTitle(R.string.difficulty_choose);

		final CharSequence[] levels = {
				getResources().getString(R.string.difficulty_easy),
				getResources().getString(R.string.difficulty_medium), 
				getResources().getString(R.string.difficulty_hard)};

		//final int selected = mGame.getDifficultyLevel().ordinal();
		builder.setItems(levels, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				dialog.dismiss();   // Close dialog

				//mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.values()[item]);

				mDifficulty = item;
				Log.d("mDifficulty is ", ""+mDifficulty);
				// Display the selected difficulty level
				Toast.makeText(getApplicationContext(), "Difficulty changed to: " + levels[item], 
						Toast.LENGTH_SHORT).show();   
			}
		});
		return builder.create();
	}
	
	private Dialog createOptionDialog(AlertDialog.Builder builder) {
		builder.setTitle(R.string.options);

		final CharSequence[] levels = {
				getResources().getString(R.string.music),
				getResources().getString(R.string.sfx)};
		
		checked[0] = mMusic;
		checked[1] = mSfx;
 		builder.setMultiChoiceItems(levels, checked, 
				new DialogInterface.OnMultiChoiceClickListener() {
					
					//@Override
					public void onClick(DialogInterface dialog, int which, boolean isChecked) {
						checked[which] = isChecked;
						Log.d("SETTING THE MUSIC AND SOUNDS", which + " : " + isChecked);

						if (which == 0) {
							mMusic = isChecked;
							if (mMusic){
								mediaPlayer.start();
							}
							else
								mediaPlayer.pause();
						} else
							mSfx = isChecked;
						
						Log.d("MMUSIC", "" + mMusic);
						Log.d("MSFX", "" + mSfx);
					}
				});
		builder.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				dialog.dismiss();   // Close dialog   	    
			}
		});
		return builder.create();
	}
	
}
