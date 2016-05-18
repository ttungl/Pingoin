package com.example.pingoin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class Home extends FragmentActivity implements OnMapClickListener, OnMarkerClickListener, OnInfoWindowClickListener {

    public static LatLng myCurrentPOILatLng;
    public static HashMap<LatLng, String> myCurrentPOIList = new HashMap<LatLng, String>(); // all currentPOI
    public static HashMap<LatLng, String> myInsiderPOIList = new HashMap<LatLng, String>(); // all poi insider
    public static LatLng editGivenLatLng;
    public static int State = 0; // If zero, call Add else call Update
    public static String FileName = "PingoInDataFile";
    
    // proximity alert
    private static final long MINIMUM_DISTANCECHANGE_FOR_UPDATE = 1; // in meter
    private static final long MINIMUM_TIME_BETWEEN_UPDATE = 5000; // in Milliseconds
    private static final double CURRENT_LOCATION_RADIUS = 1000; // in Meters
    
    // proximity alert - end
	
    protected static boolean Entry; 	// Manage the signin process
    protected static boolean enabled1; 	// Manage the network 
    
    // Hashing
    private String SHAHash;
	public static int NO_OPTIONS=0;

	// Timer
	public int time = 0;
	
    GoogleMap mMap;	
	Marker mMarker;
	
	FileOutputStream fos;
	FileInputStream fis;
	
	Button btnSignIn, btnSignUp;
	LoginDataBaseAdapter loginDataBaseAdapter;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
                
		 // create a instance of SQLite Database
	     loginDataBaseAdapter=new LoginDataBaseAdapter(this);
	     loginDataBaseAdapter=loginDataBaseAdapter.open();
	     
	     // Get The Reference Of Buttons
	     btnSignIn=(Button)findViewById(R.id.buttonSignIN);
	     btnSignUp=(Button)findViewById(R.id.buttonSignUP);
		 	     	

	    // SignUp button 
	    btnSignUp.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				/// Create Intent for SignUpActivity  and Start The Activity
				Intent intentSignUP = new Intent(getApplicationContext(),SigningUpActivity.class);
				startActivity(intentSignUP);
			}
		});
	    
	    // Logout button
	    Button LogoutButton = (Button)findViewById(R.id.LogOut);
	   	LogoutButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				Entry = false;
				// close the database - Tung			
				loginDataBaseAdapter.close();
								
//				Home.myCurrentPOIList.clear();  // Is tested - don't use this function, it could probably hurt the POIList file. Tung
			
				// Return to the home activity
				Intent intent = new Intent(getApplicationContext(), Home.class);
				startActivity(intent);
					
			}
		});
	    
	   	
	   	//test2
	    if(!Entry){
	    	signIn(btnSignIn);
	    }
	    //test2-end
	   	
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
	            .findFragmentById(R.id.map);
	    
		if(Entry){ // listeners enabled only if Entry true
			
			btnSignIn.setVisibility(View.GONE); // Hide button - romas james
			btnSignUp.setVisibility(View.GONE); 
			
			// Set up the map - Tung
			mMap = mapFragment.getMap();
		    mMap.setMyLocationEnabled(true);
		    
		    // Listen your touching on screen
		    mMap.setOnMapClickListener(this);
		    mMap.setOnMarkerClickListener(this); // romas james
		    		    
		    mMap.setOnInfoWindowClickListener(this); // romas james
		   
		    		    
		    ////////////////////////////////
		    //// Check network available////
		    ////////////////////////////////		    
		    
		    // Tung: Check GPS and network status for loading the google map
		    
		    LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
		    LocationListener ll = new LocationListener() {	 
    	    	
		        @Override
		        public void onStatusChanged(String provider, int status,
		                Bundle extras) {
		            // TODO Auto-generated method stub
	
		        }
		        @Override
		        public void onProviderEnabled(String provider) {
		            // TODO Auto-generated method stub
	
		        }
		        @Override
		        public void onProviderDisabled(String provider) {
		            // TODO Auto-generated method stub
	
		        }
						    	
		        @Override		        
		        public void onLocationChanged(Location location) {
		            // Change different views: change 10.0f ; 15.0f; etc. 
		            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
		        			location.getLatitude(), location.getLongitude()), 10.0f));
		            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
		                    location.getLatitude(), location.getLongitude()), 10.0f));
	
		        }

		    };
		    
		    // Get GPS status 
	        boolean enabled = service
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
			
			// Get network status
	        boolean NetworkEnabled = service
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	        
////	        ========/ popup test
//	        if(!Home.myInsiderPOIList.isEmpty()){
//	        	
//	        	Iterator<LatLng> myInsiderIterator = Home.myInsiderPOIList.keySet().iterator();
//		    
//	        
//			    // Iteration of reading friendlist
//			    while(myInsiderIterator.hasNext()){
//					
//			    	
//			    	LatLng key = (LatLng) myInsiderIterator.next(); 
//					Toast.makeText(getBaseContext(), key.toString() + "<-LatLng Insider LOL friendlist@", Toast.LENGTH_LONG).show();
//					
//					// Get the information of POI
//					String val = (String) Home.myInsiderPOIList.get(key);
//					
//					Marker friendInfo = mMap.addMarker(new MarkerOptions()					
//					.position(key)
//					.title(val)
//					.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_boys))
//					);
//					
//					friendInfo.showInfoWindow();
//			    }
//	        }
////	        ======= popup test-end
	        
	        if(!enabled && !NetworkEnabled){	  
	        	Toast.makeText(getBaseContext(), "GPS and Network services are not available around here!", Toast.LENGTH_LONG).show();
	        	
	        } else 
	        {	        	
	        	// if network is available
	        	if (NetworkEnabled){
	        		service.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MINIMUM_TIME_BETWEEN_UPDATE, MINIMUM_DISTANCECHANGE_FOR_UPDATE, ll);
	        		Log.d("Network", "Network Enabled");	        		
		        		if(service!=null){
		        			Location location = service.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		        			if(location !=null){
		        				enabled1 = false;
		        			}		        			
		        		}		
	        		}
	        	}

	        	// if GPS is available
	        	if(enabled){
	        		service.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINIMUM_TIME_BETWEEN_UPDATE, MINIMUM_DISTANCECHANGE_FOR_UPDATE, ll);
	        		Log.d("GPS", "GPS Enabled");
	        		if(service!=null){
	        			Location location = service.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	        			if(location !=null){
	        				enabled1 = true;
	        			}
	        		}
	        	}
	        	
	        	// Turn on GPS manually in settings
	        	if(!enabled){
	        		Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivity(intent);
	        		
	        	}
	        	
	        	// If GPS is available, else Network is available.
	        	if(enabled1){
	        		service.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINIMUM_TIME_BETWEEN_UPDATE, MINIMUM_DISTANCECHANGE_FOR_UPDATE, ll);
	        		Location location = service.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	        		
	        		// Get update LatLng
	        		double latitude = location.getLatitude();
				    double longitude = location.getLongitude();
				    Toast.makeText(getBaseContext(), "CurrLat Check 1 @" + latitude + "CurrLng Check 1 @" + longitude, Toast.LENGTH_LONG).show();
				    
				    // load data from database
				    readDataFromFile();
				    
				    // Calculate distance				    
				    // Check in myCurrentPOIList database of the user
				    Iterator<LatLng> myIterator = Home.myCurrentPOIList.keySet().iterator();
				    
				    // Iteration of reading friendlist
				    while(myIterator.hasNext()){
						
						// Check LatLng of the user's friendlist.
						LatLng key = (LatLng) myIterator.next(); 
						Toast.makeText(getBaseContext(), key.toString() + "<-LatLng friendlist@", Toast.LENGTH_LONG).show();
						
						// Get the information of POI
						String val = (String) Home.myCurrentPOIList.get(key); 
//						Toast.makeText(getApplicationContext(), key.toString() + "Testing@" + val, Toast.LENGTH_LONG).show();
						
						// Get the friendlist's location
						double friendLatitude = key.latitude;
						double friendLongtitude = key.longitude;									
						double returnDist = CalcDistance(latitude, longitude, friendLatitude, friendLongtitude);						
//						Toast.makeText(getBaseContext(), returnDist + "in meters <-Distance between return@", Toast.LENGTH_LONG).show();
		
//									if(returnDist < CURRENT_LOCATION_RADIUS){
//										// popup the message corresponded.
////										Toast.makeText(getBaseContext(), "This is within the range of current location!" + val, Toast.LENGTH_LONG).show();
//										
//										Marker friendInfo = mMap.addMarker(new MarkerOptions()					
//										.position(key)
//										.title(val)
//										.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_boys))
//										);
//										
//										friendInfo.showInfoWindow();
//									}
//									else{										
//										// clear the POI message corresponded.
////										Toast.makeText(getBaseContext(), "This is out-of-the-range of current location!" + val, Toast.LENGTH_LONG).show();
//										
//										Marker friendInfo = mMap.addMarker(new MarkerOptions()					
//												.position(key)
//												.title(val)
//												.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_boys))
//												);
//										
//										friendInfo.hideInfoWindow();
//									}		
									
											
						
						if(returnDist < CURRENT_LOCATION_RADIUS){							
							
							if(!myInsiderPOIList.containsKey(key)){
							
								String getPOIs = myInsiderPOIList.put(key, val);
								Toast.makeText(getBaseContext(), "This is inside of current location!" + getPOIs, Toast.LENGTH_LONG).show();
//						
							}
							
						}
						else{		
							
							if(myInsiderPOIList.containsKey(key)){
								
								myInsiderPOIList.remove(key);
//								
								Toast.makeText(getBaseContext(), "This POI has been removed from myInsiderList." , Toast.LENGTH_LONG).show();
//			
							}
						
						}
						
						//Declare the timer
				        Timer t = new Timer();
				        
				        //Set the schedule function and rate
//				        t.scheduleAtFixedRate(new TimerTask() {
//							@Override
//							public void run() {
//								runOnUiThread(new Runnable() {
//
//									@Override
//									public void run() {
//										time += 1;
//									}
//									
//								});
//							}
//				        	
//				        }, 0, 1000);
//						 ========/ popup test
									
							        if(!Home.myInsiderPOIList.isEmpty()){
							        	
							        	Iterator<LatLng> myInsiderIterator = Home.myInsiderPOIList.keySet().iterator();
							        	
									    // Iteration of reading friendlist
									    while(myInsiderIterator.hasNext()){
								    		
										    	LatLng key2 = (LatLng) myInsiderIterator.next(); 
												Toast.makeText(getBaseContext(), key2.toString() + "<-Insider LOL friendlist@", Toast.LENGTH_LONG).show();
												
												// Get the information of POI
												String val2 = (String) Home.myInsiderPOIList.get(key);
												
												Marker friendInfo = mMap.addMarker(new MarkerOptions()					
												.position(key2)
												.title(val2)
												.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_boys))
												);
												
												friendInfo.showInfoWindow();
												
												
//												delay , timer
										        t.scheduleAtFixedRate(new TimerTask() {
													@Override
													public void run() {
														runOnUiThread(new Runnable() {

															@Override
															public void run() {
																time += 1;
															}
															
														});
													}
										        	
										        }, 1, 1);

												
												
												
									    }
									    
									    
									    
									    
							        }
//							        ======= popup test-end
									
						
						
						
									
									
									
						}	        		
	        	}
	        	// Network is available.
	        	else{
	        		service.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MINIMUM_TIME_BETWEEN_UPDATE, MINIMUM_DISTANCECHANGE_FOR_UPDATE, ll);
	        		Location location = service.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);	        		
	        		
	        		// Check update LatLng
	        		double latitude = location.getLatitude();
				    double longitude = location.getLongitude();
				    Toast.makeText(getBaseContext(), "CurrLat Check 2 @" + latitude + "CurrLng Check 2 @" + longitude, Toast.LENGTH_LONG).show();
				    
				    // load data from database
				    readDataFromFile();
				    
				    // Calculate distance
				    
				    // Check in myCurrentPOIList database of the user
				    Iterator<LatLng> myIterator = Home.myCurrentPOIList.keySet().iterator();
				    
				    // Iteration of reading friendlist 
					while(myIterator.hasNext()){
						
						// check LatLng of the user's friendlist
						LatLng key = (LatLng) myIterator.next(); 
						Toast.makeText(getBaseContext(), key.toString() + "<-LatLng friendlist@", Toast.LENGTH_LONG).show();
						
						// Get the information of POI
						String val = (String) Home.myCurrentPOIList.get(key); 
//						Toast.makeText(getApplicationContext(), key.toString() + "Testing@" + val, Toast.LENGTH_LONG).show();
												
						double friendLatitude = key.latitude;
						double friendLongtitude = key.longitude;									
						double returnDist = CalcDistance(latitude, longitude, friendLatitude, friendLongtitude);	
						

//						String[] arrayPOIsInRadius = new String[20];		
//						Toast.makeText(getBaseContext(), arrayPOIsInRadius + "<-LatLng friendlist@", Toast.LENGTH_LONG).show();
						
						
//						Toast.makeText(getBaseContext(), returnDist + "in meters <-Distance between return@", Toast.LENGTH_LONG).show();
		
//									if(returnDist < CURRENT_LOCATION_RADIUS){
//										// popup the message corresponded.
//										Toast.makeText(getBaseContext(), "This is within the range of current location!" + val, Toast.LENGTH_LONG).show();
//										
//										Marker friendInfo = mMap.addMarker(new MarkerOptions()					
//										.position(key)
//										.title(val)
//										.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_boys))
//										);
//										
//										friendInfo.showInfoWindow();
//										
//									}
//									else{										
//										// clear the POI message corresponded.
//										Toast.makeText(getBaseContext(), "This is out-of-the-range of current location!" + val, Toast.LENGTH_LONG).show();
//										
//										Marker friendInfo = mMap.addMarker(new MarkerOptions()					
//												.position(key)
//												.title(val)
//												.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_boys))
//												);
//										
//										friendInfo.hideInfoWindow();
//									}	
								
									/////======
						
						if(returnDist < CURRENT_LOCATION_RADIUS){							
							
								if(!myInsiderPOIList.containsKey(key)){
									String getPOIs = myInsiderPOIList.put(key, val);
									Toast.makeText(getBaseContext(), "This is inside of current location!" + getPOIs, Toast.LENGTH_LONG).show();
									
								}
								
							}
						else{										
								if(myInsiderPOIList.containsKey(key)){
									
									myInsiderPOIList.remove(key);
	//								
									Toast.makeText(getBaseContext(), "This POI has been removed from myInsiderList." , Toast.LENGTH_LONG).show();
									
								}
							
							}	
							
						
									
						}
					
					 Timer t = new Timer();

//			        ========/ popup test
			        if(!Home.myInsiderPOIList.isEmpty()){
			        	
			        	Iterator<LatLng> myInsiderIterator = Home.myInsiderPOIList.keySet().iterator();
				    
			        
					    // Iteration of reading friendlist
					    while(myInsiderIterator.hasNext()){
							
					    	
					    	LatLng key = (LatLng) myInsiderIterator.next(); 
							Toast.makeText(getBaseContext(), key.toString() + "<-LatLng Insider LOL friendlist@", Toast.LENGTH_LONG).show();
							
							// Get the information of POI
							String val = (String) Home.myInsiderPOIList.get(key);
							
							Marker friendInfo = mMap.addMarker(new MarkerOptions()					
							.position(key)
							.title(val)
							.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_boys))
							);
							
							friendInfo.showInfoWindow();
							
							
//							delay , timer
					        t.scheduleAtFixedRate(new TimerTask() {
								@Override
								public void run() {
									runOnUiThread(new Runnable() {

										@Override
										public void run() {
											time += 1;
										}
										
									});
								}
					        	
					        }, 0, 1000);
							
							
					    }
			        }
//			        ======= popup test-end
					
					
//					Iterator<LatLng> myInsiderIterator = Home.myInsiderPOIList.keySet().iterator();
//				    
//				    // Iteration of reading friendlist
//				    while(myInsiderIterator.hasNext()){
//						
//				    	
//				    	LatLng key = (LatLng) myInsiderIterator.next(); 
//						Toast.makeText(getBaseContext(), key.toString() + "<-LatLng insider friendlist@", Toast.LENGTH_LONG).show();
//						
//						// Get the information of POI
//						String val = (String) Home.myInsiderPOIList.get(key);
//						
//						Marker friendInfo = mMap.addMarker(new MarkerOptions()					
//						.position(key)
//						.title(val)
//						.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_boys))
//						);
//						
//						friendInfo.showInfoWindow();
//				    }	
					
	        	} 	
	        	
	        	// Load user's data from database
//				readDataFromFile(); // Romas James
				
	        	
	      } // if(entry)-end (Note: background works should be inside of this entry)
		
		    
		}


	

	private String[] LatLng(LatLng key) {
		// TODO Auto-generated method stub
		return null;
	}




	// Tung: this function calculates the distance from the current location to friends' locations in the POI's list.
	public static double CalcDistance(double LatValBegin, double LongValBegin, double LatValEnd, double LongValEnd){
		
		try{
			
			double dist =0.0;
			double deltaLat = Math.toRadians(LatValEnd - LatValBegin);
			double deltaLng = Math.toRadians(LongValEnd - LongValBegin);
			
			// This converts in Radians metric
			LatValBegin = Math.toRadians(LatValBegin);
			LatValEnd 	= Math.toRadians(LatValEnd);
			LongValBegin = Math.toRadians(LongValBegin);
			LongValEnd 	= Math.toRadians(LongValEnd);
			
			// Earth radius from its center to the surface (consider as a sphere)
			double EarthRadius = 6371; // in Km
			
			double a = Math.sin(deltaLat/2)*Math.sin(deltaLat/2) 
					+ Math.cos(LatValBegin)*Math.cos(LatValEnd)
					* Math.sin(deltaLng/2)*Math.sin(deltaLng/2);
			
			double c = 2* Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
			
			dist = EarthRadius * c ; // return in Km
			dist = dist*1000;   // in meters
			
			return dist; 
			
		} catch(Exception e) {			
			return 0;
			
		}
	}
	// Calculate distance -end

    @Override
	public void onMapClick(LatLng position) {
//		mMap.setOnMapClickListener(this);
    	//Toast.makeText(getApplicationContext(), "Just added a marker on the map!", Toast.LENGTH_LONG).show();
    	mMarker = mMap.addMarker(new MarkerOptions()
	        .position(position)
	        .title("Romas James!")
	        .icon(BitmapDescriptorFactory
	                		.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
	    // Let User to Add More About the POI 
	    //Toast.makeText(this, mMarker.getPosition().toString(), Toast.LENGTH_LONG).show();
	    myCurrentPOILatLng = mMarker.getPosition();
	    DisplayOptionDialog();
		
    }
    
    public void startUpdatePage(){    	 
    	// Creates new activity on main screen
	    Intent startNewActivityOpen = new Intent(this, AddActivity.class);
		startActivityForResult(startNewActivityOpen, 0);
		
    }
    
    public void startDialogBox(){
    	// New activity
    	Intent myIntent = new Intent(this, DialogActivity.class);
        startActivity(myIntent);      
        finish();
	  
    }
   
    public void readDataFromFile(){
    	
    	String inputData = "";
    	File file = getBaseContext().getFileStreamPath(FileName);
    	if(file.exists()){
    		Toast.makeText(getBaseContext(), "The existing file is read.", Toast.LENGTH_LONG).show();
    		try{
     			fis = openFileInput(FileName);
     			byte[] dataArray = new byte[fis.available()];
     			
     			// Read the file.
     			while(fis.read(dataArray) != -1){
     				inputData = new String(dataArray);
     				//test = inputData.split("@");     				
     				Toast.makeText(getApplicationContext(), inputData, Toast.LENGTH_LONG).show();     				
     				//Toast.makeText(getApplicationContext(), test[Counter+1], Toast.LENGTH_LONG).show();
     				//Counter++ ;
     				
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
     		}
    		
    		// Data Extraction    		
    		if(inputData != ""){
    			String myPOIValue[] = inputData.split("@");
	     		//Toast.makeText(getApplicationContext(), Integer.toString(myPOIValue.length), Toast.LENGTH_SHORT).show();
	     		int Counters = 0;
	     		while(Counters < myPOIValue.length){
	     			// Lat and Long Positions
	     			//Toast.makeText(getApplicationContext(), myPOIValue[Counters], Toast.LENGTH_SHORT).show();
	     			//Toast.makeText(getApplicationContext(), myPOIValue[Counters + 1], Toast.LENGTH_SHORT).show();
	     			//Toast.makeText(getApplicationContext(), Integer.toString(Counters), Toast.LENGTH_SHORT).show();	     			
	     			double Lat = Double.parseDouble(myPOIValue[Counters].trim());
	     			double Long = Double.parseDouble(myPOIValue[Counters + 1].trim());
	     			
	     			String AllInfo =  myPOIValue[Counters + 2] + "@" + myPOIValue[Counters + 3] + "@" +  myPOIValue[Counters + 4] + "@" + myPOIValue[Counters + 5] + "@";
	     		    String TitleInfo = myPOIValue[Counters];
	     		    
	     		    myCurrentPOIList.put(new LatLng(Lat, Long), AllInfo); // Put all information of each POI	     			
	     		    MarkThePOI(new LatLng(Lat, Long), TitleInfo);	     		    
	     			Counters += 6 ;
	     		}	     		
    		}
    	}
    }	
    
    public void MarkThePOI(LatLng myPosition, String OtherInfo){
    	   	
    	Marker myCurrentMarker = mMap.addMarker(new MarkerOptions()
			.position(myPosition)
			.title(OtherInfo)
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_boys)));
			
//		myCurrentMarker.setDraggable(true);
		myCurrentMarker.setDraggable(false);
    
    }

    
	@Override
	public boolean onMarkerClick(Marker MyMarker) {
		String Title[] = Home.myCurrentPOIList.get(MyMarker.getPosition()).split("@");
		MyMarker.setTitle(Title[0]);
		//MyMarker.setSnippet("Romas and Shailendra is working hard in this project! What about you?); 
		// @both: Please don't count your efforts too early! Tung
		
		MyMarker.showInfoWindow();        
		return true;
    }

	public void DisplayUserData(){
		// Iterating through hashmap - romas james
		Iterator<LatLng> myIterator = Home.myCurrentPOIList.keySet().iterator();
		while(myIterator.hasNext()){
			LatLng key = (LatLng) myIterator.next(); 
			String val = (String) Home.myCurrentPOIList.get(key); 
			Toast.makeText(getApplicationContext(), key.toString() + "@" + val, Toast.LENGTH_LONG).show();
			
		}
	}


	@Override
	public void onInfoWindowClick(Marker MyMarker) {
		// TODO Auto-generated method stub
		editGivenLatLng = MyMarker.getPosition();
		//Toast.makeText(Home.this,editGivenLatLng.toString(), Toast.LENGTH_LONG).show();// display toast
		
		State = 1; // Update mode
		
		DisplayOptionDialog();
		
		//Intent j = new Intent(getBaseContext(), DialogActivity.class);    
		//startActivity(j);
       
	}
    
	public void DisplayOptionDialog(){
		
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
						//Toast.makeText(getApplicationContext(), "Cancel?", Toast.LENGTH_LONG).show();
						
						Intent k = new Intent(getBaseContext(), Home.class);    
						startActivity(k);
						break;
//						
				}
				// TODO Auto-generated method stub
			} 
		}); 
			
			final AlertDialog dialogs = builder.create();
			dialogs.show();
			
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
	    	
	    	Toast.makeText(getBaseContext(), "Your POI has successfully been removed!", Toast.LENGTH_LONG).show();
		}
		else{
			Toast.makeText(getBaseContext(), "Nothing is available to be removed!", Toast.LENGTH_LONG).show();
		}
	}
	
		
	// Methods to handleClick Event of Sign In Button
		public void signIn(View V)
		   {
				final Dialog dialog = new Dialog(this);
				dialog.setContentView(R.layout.login);
			    dialog.setTitle("Login");
			    			    			    
			    // get the References of views
			    final  EditText editTextUserName=(EditText)dialog.findViewById(R.id.editTextUserNameToLogin);
			    final  EditText editTextPassword=(EditText)dialog.findViewById(R.id.editTextPasswordToLogin);
			    
				Button btnSignIn=(Button)dialog.findViewById(R.id.buttonSignIn);
					
				// Set On ClickListener
				btnSignIn.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) {
						// get The User name and Password
						String userName=editTextUserName.getText().toString();
						String password=editTextPassword.getText().toString();
						
						// fetch the Password form database for respective user name
						String storedPassword=loginDataBaseAdapter.getSinlgeEntry(userName);						
//						Toast.makeText(Home.this, "Check hashing function in database storedPassword (sign in): " + storedPassword, Toast.LENGTH_LONG).show();
						
						// hashing password-input						
						String passHash1 = computeSHAHash(password);
						
//						Toast.makeText(Home.this, "Check hashing function: " + passHash1, Toast.LENGTH_LONG).show();
						
						
						// check if the Stored password matches with  Password entered by user

						if(passHash1.equals(storedPassword))
						{
							/// Create Intent for Pingoin activity
							Intent HomeCurrActivity=new Intent(getApplicationContext(), Home.class);
							startActivity(HomeCurrActivity);
							
							// hashing password-storage
//							computeSHAHash(password);
							
							Toast.makeText(Home.this, "Congratulations! You have successfully logged into the Pingoin.", Toast.LENGTH_LONG).show();
							dialog.dismiss();
							FileName = userName + "_" + FileName; // for every user different file name
							Entry = true;
												
							
						}
						else
						{
							
							Toast.makeText(Home.this, "Your User Name or Password does not match", Toast.LENGTH_LONG).show();
							Entry = false;
						}
					}
				});
				
				dialog.show();
		}

		
		// Tung: Convert to HEX
		 private static String convertToHex(byte[] data) throws java.io.IOException 
		 {
		        	        
		        StringBuffer sb = new StringBuffer();
		        String hex=null;
		        
		        hex=Base64.encodeToString(data, 0, data.length, NO_OPTIONS);
		        
		        sb.append(hex);
		        	        
		        return sb.toString();
		    }
		
//		Tung: this function is used to hash the username and password; 
//			  return the result to compare with the password in the database.
		public String computeSHAHash(String password)
		  {
			  MessageDigest mdSha1 = null;
		        try 
		        {
		          mdSha1 = MessageDigest.getInstance("SHA-1");
		        } catch (NoSuchAlgorithmException e1) {
		          Log.e("Pingoin", "Error initializing SHA1 message digest");
		        }
		        try {
					mdSha1.update(password.getBytes("ASCII"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        byte[] data = mdSha1.digest();
		        try {
					SHAHash = convertToHex(data);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        
//				Toast.makeText(Home.this, "SHA1 generated in signing in is: " + SHAHash, Toast.LENGTH_LONG).show();
				
				return SHAHash;
								
		    }

		
		
		@Override
		protected void onDestroy() {
			super.onDestroy();
		    // Close The Database
			loginDataBaseAdapter.close();
		}
}
	
