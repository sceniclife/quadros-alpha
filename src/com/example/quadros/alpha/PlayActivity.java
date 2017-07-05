package com.example.quadros.alpha;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class PlayActivity extends Activity {
	static final int DIALOG_NEW_ID = 0;
	static final int DIALOG_RETRY_ID = 1;
	static final int DIALOG_QUIT_ID = 2;
	static final int DIALOG_START_ID = 3;
	static final int DIALOG_OPTION_ID = 4;
	static final int DIALOG_RESET_ID = 5;

	private static final String TAG = "PlayActivity";
	private TextView mScoreTextView;
	
	private int mDifficulty;
	static final int EASY = 0;
	static final int MEDIUM = 1;
	static final int HARD = 2;
	
	private int score;
	private int life;
	
	private int tier;
	private int level;
	
	private boolean isPerfect;
	private int highScore;

	private QuadrosGame mGame;
	//private Button mBoardButtons[];
	private ImageView mHearts[];
	private boolean isGameOver;
	private BoardView mBoardView;
	// for all the sounds  we play
	private SoundPool mSounds;
	private MediaPlayer mediaPlayer;
	private HashMap<Integer, Integer> mSoundIDMap;
	
	private SharedPreferences mPrefs;
	private SharedPreferences mProgress;
	private boolean mMusic;
	private boolean mSfx;
	private boolean[] checked = {mMusic, mSfx};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "in onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play_view);

		// set initial game state
		isGameOver = false;
		isPerfect = true;
		
		score = 0;
		highScore = 0;
		life = 4;
		tier = level = 1;
		
		// get preferences
		mPrefs = getSharedPreferences("prefs", MODE_PRIVATE);
		mProgress = getSharedPreferences("progress", MODE_PRIVATE);
		mMusic = mPrefs.getBoolean("mMusic", false);
		mSfx = mPrefs.getBoolean("mSfx", false);
		
		mDifficulty = mPrefs.getInt("mDifficulty", 0);
		// set textview
		mScoreTextView = (TextView) findViewById(R.id.score);
		mGame = new QuadrosGame(tier, level);
		mBoardView = (BoardView) findViewById(R.id.board);
		mBoardView.setGame(mGame);
		
		// Sounds
		mediaPlayer = MediaPlayer.create(this, R.raw.bgmusic);
		mediaPlayer.setLooping(true);
		mediaPlayer.setVolume(0.2f, 0.2f);

		mHearts = new ImageView[4];
		mHearts[0] = (ImageView) findViewById(R.id.heart1);
		mHearts[1] = (ImageView) findViewById(R.id.heart2);
		mHearts[2] = (ImageView) findViewById(R.id.heart3);
		mHearts[3] = (ImageView) findViewById(R.id.heart4);
		
		// Listen for touches on the board
		mBoardView.setOnTouchListener(mTouchListener);
		
		showAnswer();
		
		new Handler().postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				clearAnswer();			//for (int i = 0; i < mBoardButtons.length; i++) {
				//mBoardButtons[i].setEnabled(false);		 		   
			//}
			}
		}, 3000);
		displayScore();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		Log.d(TAG, "in onPause");
		
    	SharedPreferences.Editor ed = mPrefs.edit();
        ed.putBoolean("mSfx", mSfx);
        ed.putBoolean("mMusic", mMusic);
        ed.putInt("mDifficulty", mDifficulty);
        ed.commit();
        
		Log.d("play mSfx", mSfx+"");
		Log.d("play mMusic", mMusic+"");

		if(mSounds != null) {
			mSounds.release();
			mSounds = null;
		}
		
		// set high score
		
		mediaPlayer.pause();
	}
	
    @Override
	protected void onStop() {
    	super.onStop();
    	
    	SharedPreferences.Editor ed = mPrefs.edit();
        ed.putBoolean("mSfx", mSfx);
        ed.putBoolean("mMusic", mMusic);
        ed.putInt("mDifficulty", mDifficulty);
        ed.commit();
    }
	
	@Override
	protected void onResume() {		
		super.onResume();
		Log.d(TAG, "in onResume");
		createSoundPool();
        mPrefs = getSharedPreferences("prefs", MODE_PRIVATE);
        mMusic = mPrefs.getBoolean("mMusic", false);
		mSfx = mPrefs.getBoolean("mSfx", false);
		mDifficulty = mPrefs.getInt("mDifficulty", 0);
		checked[0] = mMusic;
		checked[1] = mSfx;
		if (mMusic) {
			mediaPlayer.start();
		}
	}


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
		case DIALOG_NEW_ID:
			dialog = createNewDialog(builder);
			break;
		case DIALOG_RETRY_ID:
			dialog = createRetryDialog(builder);
			break;
		case DIALOG_RESET_ID:
			dialog = createResetDialog(builder);
			break;
		case DIALOG_QUIT_ID:
			dialog = createQuitDialog(builder);
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
	
	/* ======================= */
	/*      Music & Sound      */
	/* ======================= */
	
	private void createSoundPool() {
		int[] soundIds = {R.raw.correctbeep, R.raw.incorrectbeef};
		mSoundIDMap = new HashMap<Integer, Integer>();
		mSounds = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
		for(int id : soundIds) 
			mSoundIDMap.put(id, mSounds.load(this, id, 1));
	}


	/* ========================== */
	/*      Gameplay Actions      */
	/* ========================== */
	
	public void nextLevel() {
		
		if (mDifficulty == EASY) {
			life = 4;
			for (int i = 0; i < 4; i++)
				mHearts[i].setVisibility(View.VISIBLE);
		}
		
		isPerfect = true;
		isGameOver = false;
		level++;
		if (level < 4)
			tier = 2;
		else if (level < 7)
			tier = 3;
		else if (level < 11)
			tier = 4;
		else 
			tier = 5;
		
		mGame.level = level;
		mGame.rows = tier + 2;
		mGame.cols = tier + 2;
		mGame.board = new boolean[mGame.rows * mGame.cols];
		mGame.boardSize = mGame.rows * mGame.cols;
		mGame.generateCells(level);
		//this.mGame = new QuadrosGame(tier, level);
		mBoardView.setBoard(tier + 2);
		
		showAnswer();
		
		new Handler().postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				clearAnswer();			//for (int i = 0; i < mBoardButtons.length; i++) {
				//mBoardButtons[i].setEnabled(false);		 		   
			//}
			}
		}, 1000*tier-500);
		displayScore();
		mBoardView.setOnTouchListener(mTouchListener);
		mBoardView.invalidate();
		
	}

	// button click represents correct user guess
	public void correctAction(View v) {
		Log.d(TAG, "in correctAction");
		
		if (mSfx)
			mSounds.play(mSoundIDMap.get(R.raw.correctbeep), 1, 1, 1, 0, 1);
		
		score += 10;
		displayScore();

		// concurrency problem (potential)
		checkAnswer();
		if (isGameOver) {
			
			if (mDifficulty == MEDIUM && isPerfect) {
				if (life < 4) {
					life ++;
					mHearts[life-1].setVisibility(View.VISIBLE);
					// SOUND FOR REGEN
				}
			}
			
			newGameAction(v);
		}

	}

	private void displayScore() {
		Log.d(TAG, "in displayScore");
		mScoreTextView.setText(Integer.toString(score));
	}

	// button click represents incorrect user guess
	public void incorrectAction(View v) {
		
		if (mSfx)
			mSounds.play(mSoundIDMap.get(R.raw.incorrectbeef), 1, 1, 1, 0, 1);
		
		life--;
		isPerfect = false;
		
		if(life > 0) {
			mHearts[life].setVisibility(View.INVISIBLE);
		}

		else {
			mHearts[life].setVisibility(View.INVISIBLE);
				//for (int i = 0; i < mBoardButtons.length; i++) {
					//mBoardButtons[i].setEnabled(false);		 		   
				//}private int score;
				isGameOver = true;
				highScore = score;
				
				int size = mProgress.getInt("size", 0);
				
				SharedPreferences.Editor ed = mProgress.edit();
		        ed.putInt("size", size+1);
		        ed.putInt("data_"+size, highScore);
		        ed.commit();
				
				showAnswer();
				retryGameAction(v);
		}
	}

	public void checkAnswer() {
		boolean check = true;
		for (int s : mGame.getSelectedCells()) {
			check &= mGame.getBoardLocation(s);
		}
		
		isGameOver = check;
	}

	public void showAnswer() {
		for (int s : mGame.getSelectedCells()) {
			//mBoardButtons[s].setBackgroundColor(Color.GREEN);
			mGame.setBoard(s, true);
		}
		mBoardView.invalidate();	
	}

	public void clearAnswer() {
		for (int i = 0; i < mGame.getBoardSize(); i++) {
			//mBoardButtons[i].setBackgroundResource(android.R.drawable.btn_default);
			mGame.setBoard(i, false);
		}
		mBoardView.invalidate();
	}

	/* ======================= */
	/*      Partial Views      */
	/* ======================= */


	public void newGameAction(View v) {
		showDialog(DIALOG_NEW_ID);
	}

	public void retryGameAction(View v) {
		showDialog(DIALOG_RETRY_ID);
	}
	
	public void resetGameAction(View v) {
		showDialog(DIALOG_RESET_ID);
	}

	public void quitGameAction(View v) {
		showDialog(DIALOG_QUIT_ID);
	}
	
	public void optionsAction(View v) {
		showDialog(DIALOG_OPTION_ID);
	}
	
    /* ==================== */
    /*      Activities      */
    /* ==================== */

	// button redirects to how to play
	public void howtoAction(View v) {
		Intent intent = new Intent(this, HowToActivity.class);
		startActivityForResult(intent, 0);
	}
	
	/* ======================= */
	/*      Dialog Handlers    */
	/* ======================= */

	private Dialog createNewDialog(AlertDialog.Builder builder) {
		builder.setTitle(R.string.newgame);

		builder.setPositiveButton(R.string.next, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				dialog.dismiss();   // Close dialog  
				//Finish the splash activity so it can't be returned to.
				nextLevel();
			}
		});
		return builder.create();
	}

	private Dialog createRetryDialog(AlertDialog.Builder builder) {
		builder.setTitle(R.string.retry);

		final CharSequence[] levels = {
				getResources().getString(R.string.yes),
				getResources().getString(R.string.no)};

		//final int selected = mGame.getDifficultyLevel().ordinal();
		builder.setItems(levels, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				dialog.dismiss();   // Close dialog	    
				if (item == 0){
					new Handler().postDelayed(new Runnable()
			        {
			            @Override
			            public void run()
			            {
			                // Create an Intent that will start the main activity.
							//Finish the splash activity so it can't be returned to.
							PlayActivity.this.finish();
							Intent intent = new Intent(PlayActivity.this, PlayActivity.class);
							PlayActivity.this.startActivity(intent);
			                //Apply splash exit (fade out) and main entry (fade in) animation transitions.
			                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
			                
			            }
			        }, 0);
					
				}
				if (item == 1){
					new Handler().postDelayed(new Runnable()
			        {
			            @Override
			            public void run()
			            {
			                // Create an Intent that will start the main activity.
							//Finish the splash activity so it can't be returned to.
							PlayActivity.this.finish();
							Intent intent = new Intent(PlayActivity.this, MenuActivity.class);
							PlayActivity.this.startActivity(intent);
			                //Apply splash exit (fade out) and main entry (fade in) animation transitions.
			                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
			                
			            }
			        }, 0);
					
				}
			}
		});
		return builder.create();
	}

	
	private Dialog createResetDialog(AlertDialog.Builder builder) {
		builder.setTitle(R.string.reset);

		final CharSequence[] levels = {
				getResources().getString(R.string.yes),
				getResources().getString(R.string.no)};

		//final int selected = mGame.getDifficultyLevel().ordinal();
		builder.setItems(levels, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				dialog.dismiss();   // Close dialog	    
				if (item == 0){
					new Handler().postDelayed(new Runnable()
			        {
			            @Override
			            public void run()
			            {
			                // Create an Intent that will start the main activity.
							//Finish the splash activity so it can't be returned to.
							PlayActivity.this.finish();
							Intent intent = new Intent(PlayActivity.this, PlayActivity.class);
							PlayActivity.this.startActivity(intent);
			                //Apply splash exit (fade out) and main entry (fade in) animation transitions.
			                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
			                
			            }
			        }, 0);
					
				}
			}
		});
		return builder.create();
	}
	private Dialog createQuitDialog(AlertDialog.Builder builder) {
		builder.setTitle(R.string.quit);

		final CharSequence[] levels = {
				getResources().getString(R.string.yes),
				getResources().getString(R.string.no)};

		//final int selected = mGame.getDifficultyLevel().ordinal();
		builder.setItems(levels, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				dialog.dismiss();   // Close dialog	    
				if (item == 0){
					new Handler().postDelayed(new Runnable()
			        {
			            @Override
			            public void run()
			            {
			                // Create an Intent that will start the main activity.
							//Finish the splash activity so it can't be returned to.
							PlayActivity.this.finish();
							Intent intent = new Intent(PlayActivity.this, MenuActivity.class);
							PlayActivity.this.startActivity(intent);
			                //Apply splash exit (fade out) and main entry (fade in) animation transitions.
			                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
			                
			            }
			        }, 0);
					
				}
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
	
	
    // Listen for touches on the board
    private OnTouchListener mTouchListener = new OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {

        	// Determine which cell was touched	    	
	    	int col = (int) event.getX() / mBoardView.getBoardCellWidth();
	    	int row = (int) event.getY() / mBoardView.getBoardCellHeight();
	    	int pos = row * mBoardView.getBoardSize() + col;

	    	View view = null;
			if (!isGameOver) {
				if(mGame.setMove(pos)) {
					
					correctAction(view);
					mBoardView.invalidate();
				}
				else if (!mGame.getBoardLocation(pos)){
					incorrectAction(view);
					mBoardView.invalidate();
				}

			}
	    	
	    	// So we aren't notified of continued events when finger is moved
	    	return false;
        } 
    };

}
