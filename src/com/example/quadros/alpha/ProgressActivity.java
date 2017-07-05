package com.example.quadros.alpha;

import java.util.ArrayList;
import java.util.Arrays;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ProgressActivity extends Activity {

	static final int DIALOG_CLEAR_ID = 0;
	private SharedPreferences mProgress;
	private SharedPreferences.Editor mProgressEd;
	private int[] dataArray;
	private int size;
	
	// chart container & variables
	private LinearLayout layout;
	private TimeSeries series;
	private XYMultipleSeriesDataset dataset;
	private XYSeriesRenderer renderer;
	private XYMultipleSeriesRenderer mRenderer;
	private GraphicalView mChartView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.progress_view);
		
		// grab data from progress.xml file
		layout = (LinearLayout) findViewById(R.id.chart);
		mProgress = getSharedPreferences("progress", MODE_PRIVATE);
		mProgressEd = mProgress.edit();
		
		size = mProgress.getInt("size", 0) + 1;
		Log.d("SIZE IS"+size, size+"");
		
		// size will always have (0, 0) hence offset by 1
		dataArray = new int[size];
		dataArray[0] = 0;
		
		for (int i=1; i < size; i++) {
			int datum = mProgress.getInt("data_"+(i-1), 0); 
			dataArray[i] = datum;
			Log.d("THIS IS DATA_"+(i-1), datum+"");
		}
		
		
		/*===================*/
		/* DISPLAY THE GRAPH */
		/*===================*/
		Log.d("displayProgress SIZE IS"+size, size+"");
		
		// x values
		int[] x = new int[size];
		for (int i=0; i < size; i++) {
			x[i] = i;
		}
		
		Log.d("displayProgress X VALS", Arrays.toString(x));
		
		// dataset y values
		series = new TimeSeries("Line1"); 
		for( int i = 0; i < size; i++)
		{
			series.add(x[i], dataArray[i]);
		}
		
		Log.d("displayProgress SIZE IS"+size, Arrays.toString(dataArray));

		dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(series);

		mRenderer = new XYMultipleSeriesRenderer(); // Holds a collection of XYSeriesRenderer and customizes the graph
		renderer = new XYSeriesRenderer(); // This will be used to customize line 1
		mRenderer.addSeriesRenderer(renderer);

		// Customization time for line 1!
		renderer.setColor(Color.BLACK);
		renderer.setPointStyle(PointStyle.SQUARE);
		renderer.setFillPoints(true);
		
		mChartView = ChartFactory.getLineChartView(this, dataset, mRenderer);
		layout.addView(mChartView);
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
		case DIALOG_CLEAR_ID:
			dialog = this.createClearDialog(builder);
			break;
		}

		if(dialog == null)
			Log.d("Dialog", "Uh oh! Dialog is null");
		else
			Log.d("Dialog", "Dialog created: " + id + ", dialog: " + dialog);
		return dialog;        
	}

	/* ======================= */
	/*      Partial Views      */
	/* ======================= */

	public void clearDataAction(View v) {
		showDialog(DIALOG_CLEAR_ID);
	}

	/* ======================= */
	/*      Dialog Handlers    */
	/* ======================= */

	private Dialog createClearDialog(AlertDialog.Builder builder) {
		builder.setTitle(R.string.sure);

		final CharSequence[] levels = {
				getResources().getString(R.string.yes),
				getResources().getString(R.string.no), };

		builder.setItems(levels, 
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				dialog.dismiss();   // Close dialog

				if (item == 0) {
					mProgressEd.clear();
					mProgressEd.commit();
				}
				

				new Handler().postDelayed(new Runnable()
		        {
		            @Override
		            public void run()
		            {
		                // Create an Intent that will start the main activity.
						//Finish the splash activity so it can't be returned to.
						ProgressActivity.this.finish();
						Intent intent = new Intent(ProgressActivity.this, ProgressActivity.class);
						ProgressActivity.this.startActivity(intent);
		                //Apply splash exit (fade out) and main entry (fade in) animation transitions.
		                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		                
		            }
		        }, 0);
			}
		});
		return builder.create();
	}

}
