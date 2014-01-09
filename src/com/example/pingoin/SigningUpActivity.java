package com.example.pingoin;



import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.os.Bundle;
import android.app.Activity;
//import android.view.Menu;
//import android.app.Activity;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class SigningUpActivity extends Activity {

	EditText editTextUserName,editTextPassword,editTextConfirmPassword;
	Button btnCreateAccount, btnPasswordChange;
	
	// Hashing
    private String SHAHash;
	public static int NO_OPTIONS=0;
	
	LoginDataBaseAdapter loginDataBaseAdapter;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signing_up);
		
				// get Instance  of Database Adapter
				loginDataBaseAdapter=new LoginDataBaseAdapter(this);
				loginDataBaseAdapter=loginDataBaseAdapter.open();
				
				// Get References of Views
				editTextUserName=(EditText)findViewById(R.id.editTextUserName);
				editTextPassword=(EditText)findViewById(R.id.editTextPassword);
				editTextConfirmPassword=(EditText)findViewById(R.id.editTextNewPassword);
				
				btnCreateAccount=(Button)findViewById(R.id.buttonCreateAccount);
				btnCreateAccount.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					String userName=editTextUserName.getText().toString();
					String password=editTextPassword.getText().toString();
					String confirmPassword=editTextConfirmPassword.getText().toString();
					
					
					// fetch the UserName from database to check the input if coincidence. Tung
					String UserNameChecker= loginDataBaseAdapter.getUsernameEntry(userName);	
					
					// check if any of the fields are vacant
					if(userName.equals("")||password.equals("")||confirmPassword.equals(""))
					{
							Toast.makeText(getApplicationContext(), "The field is empty! Please input!", Toast.LENGTH_LONG).show();
							return;
					}
					
										
					// check if the users are equal				
					if(UserNameChecker.equals(userName)){
						Toast.makeText(getApplicationContext(), "Username is already in use. Please input another one!", Toast.LENGTH_LONG).show();
						return;						
					}
					
					// check if the user is named "Admin"-> not allowed
					if(userName.equals("admin")||userName.equals("Admin")||userName.equals("ADmin")||
							userName.equals("AdMin")||userName.equals("AdmIn")||userName.equals("AdmiN")||
							userName.equals("ADmin")||userName.equals("ADMin")||userName.equals("ADMIn")||
							userName.equals("ADMIN")||
							userName.equals("administrator")||userName.equals("Administrator")||userName.equals("ADministrator")||
							userName.equals("AdMinistrator")||userName.equals("AdmInistrator")||userName.equals("AdmiNistrator")||
							userName.equals("ADministrator")||userName.equals("ADMinistrator")||userName.equals("ADMInistrator")||
							userName.equals("ADMINISTRATOR")){
						Toast.makeText(getApplicationContext(), "Admin name is not allowed!", Toast.LENGTH_LONG).show();
						return;
						
					}
					
						
					// check if both passwords are matched or not
					if(!password.equals(confirmPassword))
					{
						Toast.makeText(getApplicationContext(), "Your passwords do not match!", Toast.LENGTH_LONG).show();
						return;
					}
					
					else
					{
						
						// hashing password
						String passHash1 = computeSHAHash(password);
//						Toast.makeText(getApplicationContext(), "Check hashing signing up:" + passHash1, Toast.LENGTH_LONG).show();
	    
					    // Save the Datab in Database- new hashing
					    loginDataBaseAdapter.insertEntry(userName, passHash1);
					    Toast.makeText(getApplicationContext(), "Your account has been created successfully!", Toast.LENGTH_LONG).show();
					    
					    					    
					    // Close the database before changing to a new activity
					    loginDataBaseAdapter.close();
					    
					    // Create Intent for HomeActivity - cannot call the mainActivity here. Tung
					    Intent HomeActivity = new Intent(getApplicationContext(),Home.class);
						startActivity(HomeActivity);
												
					}
				}
			});			
				
				
				
			// Tung: Password change	
				btnPasswordChange=(Button)findViewById(R.id.buttonChangePassword);
				btnPasswordChange.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						
						// Close the database before changing to a new activity
					    loginDataBaseAdapter.close();
					    
//						Toast.makeText(getApplicationContext(), "Change Password Testing! ", Toast.LENGTH_LONG).show();
						Intent intentPasswordChange =new Intent(getApplicationContext(),PasswordChangeActivity.class);
						startActivity(intentPasswordChange);
					
					}
				});		
				
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
			
//			Tung: this function is used to hash the username and password
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
			        
//					Toast.makeText(SigningUpActivity.this, "SHA1 generated in signing up process is: " + SHAHash, Toast.LENGTH_LONG).show();
					
					return SHAHash;
					
			    }
	
	
	
	
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		loginDataBaseAdapter.close();
	}


}
