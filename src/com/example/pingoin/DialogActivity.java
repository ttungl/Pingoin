package com.example.pingoin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import com.google.android.gms.maps.model.LatLng;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class DialogActivity extends Activity {
	private Button button;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dialog);
		// Show the Up button in the action bar.
		setupActionBar();
		
		setContentView(R.layout.optionmenu); 
		final String[] option = new String[] { "Update POI", "Remove POI", "Get Tweets", "Cancel" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, option);
		
		final AlertDialog.Builder builder = new AlertDialog.Builder(this); builder.setTitle("Select Option"); 
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() { 
			public void onClick(DialogInterface dialog, int which) {
				switch(which){
					case 0: // Update POI
						//Toast.makeText(getApplicationContext(), "I am Update POI! ", Toast.LENGTH_LONG).show();
						if(Home.State == 0){
							Intent i = new Intent(getBaseContext(), AddActivity.class);    
							startActivity(i);
						}
						else{
							Intent k = new Intent(getBaseContext(), UpdateActivity.class);    
							startActivity(k);
							Home.State = 0;
						}
							
						//finish();
						
						break;
					case 1: 
						removePOI();
						Intent j = new Intent(getBaseContext(), Home.class);    
						startActivity(j);
						//Toast.makeText(getApplicationContext(), "Get More Information?", Toast.LENGTH_LONG).show();
						break;
					case 2: 
						//Toast.makeText(getApplicationContext(), "Delete POI?", Toast.LENGTH_LONG).show();
						break;
					case 3:
						//Intent GoBack = new Intent(MainActivity.class);
						//Toast.makeText(getApplicationContext(), "Cacel?", Toast.LENGTH_LONG).show();
						Intent k = new Intent(getBaseContext(), Home.class);    
						startActivity(k);
						break;
						
					
				
				}
				// TODO Auto-generated method stub
			} }); 
			
			final AlertDialog dialogs = builder.create();
			button = (Button) findViewById(R.id.DialogButton); 
			button.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) { 
					dialogs.show(); 
					
				} 
			}); 
	} 
	

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dialog, menu);
		return true;
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
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void removePOI(){
		if(Home.myCurrentPOIList.containsKey(Home.editGivenLatLng)){
			Home.myCurrentPOIList.remove(Home.editGivenLatLng);
			File file = getBaseContext().getFileStreamPath(Home.FileName);
		   	if(file.exists()){
				// write into the file
				try {
					//file.delete();
					FileOutputStream fos = openFileOutput(Home.FileName, Context.MODE_PRIVATE);
					//fos.write(System.getProperty("line.separator").getBytes());
					if(Home.myCurrentPOIList.size() != 0){
						Iterator<LatLng> myIterator = Home.myCurrentPOIList.keySet().iterator();
						while(myIterator.hasNext()){
							LatLng key = (LatLng) myIterator.next(); 
							String val = (String) Home.myCurrentPOIList.get(key); 
							
							String Info[] = val.split("@");
							Toast.makeText(getApplicationContext(), key.toString() + "@" + Info[0] + " " + Info[1] + " " + Info[2] + " " + Info[3] , Toast.LENGTH_LONG).show();
							fos.write((Double.toString(key.latitude) + "@" + Double.toString(key.longitude) + "@" + Info[0]  + "@" + Info[1] + "@" + Info[2] + "@" + Info[3] + "@").getBytes());	
							
							
						}
						fos.close();
					}	
					else{
						file.delete();
						
					}
				} catch (IOException e) {
		
					e.printStackTrace();
				}
		
	    	}
	    	
	    	Toast.makeText(getBaseContext(), "POI Successfully Removed!", Toast.LENGTH_LONG).show();
		}
		else{
			Toast.makeText(getBaseContext(), "Nothing is there to be removed!", Toast.LENGTH_LONG).show();
		}
	}

}
