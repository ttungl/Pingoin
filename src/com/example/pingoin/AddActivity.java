package com.example.pingoin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
//import android.widget.Toast;

import android.widget.Toast;

import com.example.pingoin.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class AddActivity extends Activity implements OnClickListener {
	
	FileOutputStream fos;
	FileInputStream fis;
	
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 30000; // romas james

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_add);
		setupActionBar();

		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		final View contentView = findViewById(R.id.fullscreen_content);

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
				HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider
				.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
					// Cached values.
					int mControlsHeight;
					int mShortAnimTime;

					@Override
					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
					public void onVisibilityChange(boolean visible) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
							// If the ViewPropertyAnimator API is available
							// (Honeycomb MR2 and later), use it to animate the
							// in-layout UI controls at the bottom of the
							// screen.
							if (mControlsHeight == 0) {
								mControlsHeight = controlsView.getHeight();
							}
							if (mShortAnimTime == 0) {
								mShortAnimTime = getResources().getInteger(
										android.R.integer.config_shortAnimTime);
							}
							controlsView
									.animate()
									.translationY(visible ? 0 : mControlsHeight)
									.setDuration(mShortAnimTime);
						} else {
							// If the ViewPropertyAnimator APIs aren't
							// available, simply show or hide the in-layout UI
							// controls.
							controlsView.setVisibility(visible ? View.VISIBLE
									: View.GONE);
						}

						if (visible && AUTO_HIDE) {
							// Schedule a hide().
							delayedHide(AUTO_HIDE_DELAY_MILLIS); // Romas James
						}
					}
				});

		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		
		// At First I forgot to put this, and the button was not responding. - Romas James
		Button myButton = (Button) findViewById(R.id.AddButton);
		myButton.setOnClickListener(this);
		// Set up the user interaction to manually show or hide the system UI.
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});
		
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			// TODO: If Settings has multiple levels, Up should navigate up
			// that hierarchy.
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int targetId = v.getId();
				
		if(targetId == R.id.AddButton){
			
			// to get Title from the user
			TextView Title = (TextView) findViewById(R.id.editTitleAdd);
			String myTitle = Title.getText().toString().trim();
			
			// to get Description from the user
			TextView Description = (TextView) findViewById(R.id.editDescriptionAdd);
			String myDescription = Description.getText().toString().trim();
			
			// to get Private Notes from the user
			TextView PrivateNotes = (TextView) findViewById(R.id.editPrivateNotesAdd);
			String myPrivateNotes = PrivateNotes.getText().toString().trim();
			
			// to get Public Notes from the user
			TextView PublicNotes = (TextView) findViewById(R.id.editPublicNotesAdd);
			String myPublicNotes = PublicNotes.getText().toString().trim();
			
			double myCurrentLatitude = Home.myCurrentPOILatLng.latitude;
			double myCurrentLongitude = Home.myCurrentPOILatLng.longitude;
			
					
			// -- prevent the empty input in adding the information - Tung
			
			if(myTitle.equals("")||myDescription.equals("")||myPrivateNotes.equals("")||myPublicNotes.equals(""))
			{
					Toast.makeText(getApplicationContext(), "Please fillout full information!", Toast.LENGTH_LONG).show();
					return;
			}
			
			
			// - Tung
					
			// create a file
			if(!myTitle.equals("") && !myDescription.equals("") && Home.myCurrentPOILatLng != null){
				String myLat = Double.toString(myCurrentLatitude);
				String myLong = Double.toString(myCurrentLongitude);	
			
			 	if(!Home.myCurrentPOIList.containsKey(Home.myCurrentPOILatLng)){ // Prevent Duplicate keys
			 		Home.myCurrentPOIList.put(Home.myCurrentPOILatLng, myTitle + "@" + myDescription + "@" + myPrivateNotes + "@" + myPublicNotes  + "@");
			 	}
				
			 	// Creates file --> This was the point which wasted my 2 hours :(
			 	File file = getBaseContext().getFileStreamPath(Home.FileName);	
			 	if(!file.exists()){
			 		try {
			 			FileOutputStream fout  = new FileOutputStream(new File(Home.FileName));
						fout.close();
						finish();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			 	}
			 	
				try {
					 
					fos = openFileOutput(Home.FileName, Context.MODE_APPEND);
					//fos.write(System.getProperty("line.separator").getBytes());
					fos.write((myLat + "@" + myLong + "@" + myTitle + "@" + myDescription + "@" + myPrivateNotes + "@" + myPublicNotes + "@").getBytes());	
					
					
					fos.close();	
				} catch (IOException e) {
		
					e.printStackTrace();
				}
			}		
			
			/*
			String inputData = "Nothing";
			try {
				fis = openFileInput(Home.FileName);
				byte[] dataArray = new byte[fis.available()];
				while(fis.read(dataArray) != -1){
					inputData = new String(dataArray);
					
					
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally{
				try {
					fis.close();
					//Toast.makeText(getApplicationContext(), inputData, Toast.LENGTH_LONG).show();
				} catch (IOException e) {
					e.printStackTrace();
				}
			
			

			
			}*/
			
			
			
			
			// to prevent duplicate update 
			Title.setText("");
			Description.setText("");
			PrivateNotes.setText("");
			PublicNotes.setText("");
			
			Intent i = new Intent(getBaseContext(), Home.class);    
			startActivity(i);
			
			Toast.makeText(getApplicationContext(), "Congratulations!!! POI Successfully Added!", Toast.LENGTH_LONG).show();
			
		}
	}
}
