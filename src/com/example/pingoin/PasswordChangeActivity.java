package com.example.pingoin;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PasswordChangeActivity extends Activity {

	EditText editTextUserName1,editTextPassword1,editTextNewPassword;
	Button btnPasswChange;
		
    // Hashing
    private String SHAHash;
	public static int NO_OPTIONS=0;
	
	
	LoginDataBaseAdapter loginDataBaseAdapter;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_password_change);
		
		// get Instance  of Database Adapter
		loginDataBaseAdapter=new LoginDataBaseAdapter(this);
		loginDataBaseAdapter=loginDataBaseAdapter.open();
		
		// Get References of Views
		editTextUserName1=(EditText)findViewById(R.id.editTextUserName1);
		editTextPassword1=(EditText)findViewById(R.id.editTextPassword1);
		editTextNewPassword=(EditText)findViewById(R.id.editTextNewPassword);
		
		btnPasswChange=(Button)findViewById(R.id.buttonPasswChange);
		btnPasswChange.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				String userName=editTextUserName1.getText().toString();
				String password=editTextPassword1.getText().toString();
				String newPassword=editTextNewPassword.getText().toString();
				
				String UserNameChecker= loginDataBaseAdapter.getUsernameEntry(userName);
								
				// check if any of the fields are not available.
				if(userName.equals("")||password.equals("")||newPassword.equals(""))
				{
						Toast.makeText(getApplicationContext(), "The field is empty! Please input!", Toast.LENGTH_LONG).show();
						return;
				}
											
				// Check if the username is already in use in the database or not
				if(UserNameChecker.equals(userName)){
					
					String storedPassword=loginDataBaseAdapter.getSinlgeEntry(userName);					
//					Toast.makeText(PasswordChangeActivity.this, "storedPassword (password change session)" + storedPassword, Toast.LENGTH_LONG).show();
					
					// hashing pass
					String passHash1 = computeSHAHash(password);					
//					Toast.makeText(PasswordChangeActivity.this, "passHash1 in newpassword in (password change session) " + passHash1, Toast.LENGTH_LONG).show();
										
					// Check if the stored password is matched with the password entered by user or not.
					if(passHash1.equals(storedPassword))
					{						

						// hashing pass
						String passHash2 = computeSHAHash(newPassword);
						
						// Save data in database - hashing new password
						loginDataBaseAdapter.updateEntry(userName, passHash2);
						Toast.makeText(getApplicationContext(), "Your password has been updated successfully!", Toast.LENGTH_LONG).show();
																		
						// Close the database before changing to a new activity
					    loginDataBaseAdapter.close();
						
//						 /// Create Intent for HomeActivity - cannot call the mainActivity here. Tung
					    Intent HomeActivity = new Intent(getApplicationContext(),Home.class);
						startActivity(HomeActivity);
						
					}
					else{
						
						Toast.makeText(getApplicationContext(), "Your password does not match! Please input again!", Toast.LENGTH_LONG).show();
					}
					
				}
				else{
					Toast.makeText(getApplicationContext(), "Your username does not match! Please input again!", Toast.LENGTH_LONG).show();
					
				}
				
			}
			
			
		});
		
		
		
		
		
	}

// 			 Tung: Convert to HEX
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
			        	        
//					Toast.makeText(PasswordChangeActivity.this, "SHA1 generated in changing password is: " + SHAHash, Toast.LENGTH_LONG).show();
					
					return SHAHash;
					
			    }
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		loginDataBaseAdapter.close();
	}
	
}
