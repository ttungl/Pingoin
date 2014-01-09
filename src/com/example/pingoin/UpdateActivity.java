package com.example.pingoin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
//import android.widget.Toast;

import android.widget.Toast;

import com.example.pingoin.util.SystemUiHider;
import com.google.android.gms.maps.model.LatLng;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class UpdateActivity extends Activity implements android.view.View.OnClickListener {
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 30000;

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

		setContentView(R.layout.activity_update);

		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		final View contentView = findViewById(R.id.fullscreen_content);

		
		
		
		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		// test1
//		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
//				HIDER_FLAGS);
//		mSystemUiHider.setup();
//		mSystemUiHider
//				.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
//					// Cached values.
//					int mControlsHeight;
//					int mShortAnimTime;
//
//					@Override
//					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
//					public void onVisibilityChange(boolean visible) {
//						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
//							// If the ViewPropertyAnimator API is available
//							// (Honeycomb MR2 and later), use it to animate the
//							// in-layout UI controls at the bottom of the
//							// screen.
//							if (mControlsHeight == 0) {
//								mControlsHeight = controlsView.getHeight();
//							}
//							if (mShortAnimTime == 0) {
//								mShortAnimTime = getResources().getInteger(
//										android.R.integer.config_shortAnimTime);
//							}
//							controlsView
//									.animate()
//									.translationY(visible ? 0 : mControlsHeight)
//									.setDuration(mShortAnimTime);
//						} else {
//							// If the ViewPropertyAnimator APIs aren't
//							// available, simply show or hide the in-layout UI
//							// controls.
//							controlsView.setVisibility(visible ? View.VISIBLE
//									: View.GONE);
//						}
//
//						if (visible && AUTO_HIDE) {
//							// Schedule a hide().
//							delayedHide(AUTO_HIDE_DELAY_MILLIS);
//						}
//					}
//				});
//		
//		// At First I forgot to put this, and the button was not responding. - Romas James
//		Button ourButton = (Button) findViewById(R.id.UpdateButton);
//		ourButton.setOnClickListener(this);
//		// Set up the user interaction to manually show or hide the system UI.
//		contentView.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				if (TOGGLE_ON_CLICK) {
//					mSystemUiHider.toggle();
//				} else {
//					mSystemUiHider.show();
//				}
//			}
//		});
//
//		// Upon interacting with UI controls, delay any scheduled hide()
//		// operations to prevent the jarring behavior of controls going away
//		// while interacting with the UI.
////		findViewById(R.id.UpdateButton).setOnTouchListener(
////				mDelayHideTouchListener);
////		
//		DisplayInfo();
//		
		//test1-end
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
	
	public void DisplayInfo(){
		
		//Toast.makeText(getApplicationContext(), "I am Updating!", Toast.LENGTH_LONG).show();
		
		
		String InfotoBeUpdated = Home.myCurrentPOIList.get(Home.editGivenLatLng);
		String Information[] = InfotoBeUpdated.split("@");
		
		TextView Title = (TextView) findViewById(R.id.editTitleUpdate);
		Title.setText(Information[0]);
		
		TextView Description =  (TextView) findViewById(R.id.editDescriptionUpdate);
		Description.setText(Information[1]);
		
		TextView PrivateNotes = (TextView) findViewById(R.id.editPrivateNotesUpdate);
		PrivateNotes.setText(Information[2]);
		
		TextView PublicNotes = (TextView) findViewById(R.id.editPublicNotesUpdate);
		PublicNotes.setText(Information[3]);
	}
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int targetId = v.getId();
		
		if(targetId == R.id.UpdateButton){
			
			// to get Title from the user
			TextView Title = (TextView) findViewById(R.id.editTitleUpdate);
			String myTitle = Title.getText().toString().trim();
			
			// to get Description from the user
			TextView Description = (TextView) findViewById(R.id.editDescriptionUpdate);
			String myDescription = Description.getText().toString().trim();
			
			// to get Private Notes from the user
			TextView PrivateNotes = (TextView) findViewById(R.id.editPrivateNotesUpdate);
			String myPrivateNotes = PrivateNotes.getText().toString().trim();
			
			// to get Public Notes from the user
			TextView PublicNotes = (TextView) findViewById(R.id.editPublicNotesUpdate);
			String myPublicNotes = PublicNotes.getText().toString().trim();
			
			//String myLat = Double.toString(Home.editGivenLatLng.latitude);	
			//String myLong = Double.toString(Home.editGivenLatLng.longitude);	
			
			
			if(!myTitle.equals("") && !myDescription.equals("")){
				
				Home.myCurrentPOIList.remove(Home.editGivenLatLng);// remove and replace by new
			 	Home.myCurrentPOIList.put(Home.editGivenLatLng, myTitle + "@" + myDescription + "@" + myPrivateNotes + "@" + myPublicNotes + "@");
			}
			
			File file = getBaseContext().getFileStreamPath(Home.FileName);
	    	if(file.exists()){
				// write into the file
				try {
					
					FileOutputStream fos = openFileOutput(Home.FileName, Context.MODE_PRIVATE);
					//fos.write(System.getProperty("line.separator").getBytes());
					Iterator<LatLng> myIterator = Home.myCurrentPOIList.keySet().iterator();
					while(myIterator.hasNext()){
						LatLng key = (LatLng) myIterator.next(); 
						String val = (String) Home.myCurrentPOIList.get(key); 
						
						String Info[] = val.split("@");
						//Toast.makeText(getApplicationContext(), key.toString() + "@" + Info[0] + " " + Info[1] + " " + Info[2] + " " + Info[3] , Toast.LENGTH_LONG).show();
						fos.write((Double.toString(key.latitude) + "@" + Double.toString(key.longitude) + "@" + Info[0]  + "@" + Info[1] + "@" + Info[2] + "@" + Info[3] + "@").getBytes());	
						
						
					}
					
					
					fos.close();	
				} catch (IOException e) {
		
					e.printStackTrace();
				}
		
	    	}
	    	
	    	
	    	Intent k = new Intent(getBaseContext(), Home.class);    
			startActivity(k);
			
			Toast.makeText(getApplicationContext(), "Congratulations!!! POI Successfully Updated!", Toast.LENGTH_LONG).show();
	  	
		}
	}
	
}
