package com.example.pingoin;



//import android.app.Dialog;
//import android.content.Intent;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
import android.os.Bundle;
//import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
//import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
//import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;




// for signin and signup buttons
import com.example.pingoin.R;
//import com.example.pingoin.SignUPActivity;
// for database adapter
//import com.example.pingoin.LoginDataBaseAdapter;
//import com.example.pingoin.MainActivity;

public class MainActivity extends FragmentActivity {

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
			     
	   		 
	    // Set OnClick Listener on SignUp button 
	    btnSignUp.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
//			/// Create Intent for SignUpActivity  and Start The Activity
			Intent intentSignUP=new Intent(getApplicationContext(),SigningUpActivity.class);
			startActivity(intentSignUP);

			
			}
		});
	}
	
	
	// Methods to handleClick Event of Sign In Button
	public void signIn(View V)
	   {
			final Dialog dialog = new Dialog(MainActivity.this);
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
					
					// fetch the Password from database for respective user name
					String storedPassword=loginDataBaseAdapter.getSinlgeEntry(userName);
					
					// check if the Stored password matches with  Password entered by user
					if(password.equals(storedPassword))
					{
												
						Toast.makeText(MainActivity.this, "Congratulations! You have successfully logged into the Pingoin.", Toast.LENGTH_LONG).show();
						dialog.dismiss();
						
						/// Create Intent for Pingoin activity
						Intent HomeActivity = new Intent(getApplicationContext(),Home.class);
						startActivity(HomeActivity);
						
					}
					else
					{
						Toast.makeText(MainActivity.this, "User Name or Password does not match.", Toast.LENGTH_LONG).show();
					}
				}
			});
			
			dialog.show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	    // Close The Database
		loginDataBaseAdapter.close();
	}

}





