package uk.ac.imperial.wikihealth.UI;


import uk.ac.imperial.wikihealth.R;
import uk.ac.imperial.wikihealth.Utils.LogUtils;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;


/**
 * Splash Screen at the execution of the Application with duration of <SPLASH_DURATION> seconds
 * @author pkritiotis
 *
 */
public class SplashScreen extends Activity {
	
	private final String CLASS_NAME="SplashScreen";
	private final int SPLASH_DURATION =500;//ms
	private boolean mIsBackButtonPressed = false;
	
	
	/**
	 * Called when the activity is first created
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtils.info(CLASS_NAME, CLASS_NAME, "Activity Created");
		
		//show splash screen
		setContentView(R.layout.activity_splash_screen);
		LogUtils.info(CLASS_NAME, CLASS_NAME, "Layout shown succesfully");

		//start a thread in some time for transition to the next screen
		LogUtils.info(CLASS_NAME, CLASS_NAME, "Starting handler to run in "+SPLASH_DURATION+" milliseconds");
		Handler handler = new Handler();
	    handler.postDelayed(new Runnable() {
	            @Override
	            public void run() {	 
	                if (!mIsBackButtonPressed) {
	                	
	                	//Check if this is the first execution of the application
	                	SharedPreferences settings = getSharedPreferences("first_time", 0);

	                	if(settings.getBoolean("first_time", true)){ //first time
	                		LogUtils.info(CLASS_NAME, CLASS_NAME, "First execution of the applicationn on the device");
		                	Intent intent = new Intent(SplashScreen.this, EnterScreen.class);
		            		LogUtils.info(CLASS_NAME, CLASS_NAME, "Starting Registration/Login Activity");
			                finish();
			                LogUtils.info(CLASS_NAME, CLASS_NAME, "Activity Finished");
		                    SplashScreen.this.startActivity(intent);
	                	}
	                	else{	//second time
	                		LogUtils.info(CLASS_NAME, CLASS_NAME, "The application has already executed in the past");
	                		Intent intent = new Intent(SplashScreen.this, StatusScreen.class);
		            		LogUtils.info(CLASS_NAME, CLASS_NAME, "Starting Status Screen Activity");
			                finish();
			                LogUtils.info(CLASS_NAME, CLASS_NAME, "Activity Finished");
		                    SplashScreen.this.startActivity(intent);
	                	}
	                		
	               }
	                 
	            }
	 
	        }, SPLASH_DURATION); 
	    
	    }
	
	/**
	 * When back button is pressed set flag to avoid proceeding in the next screen
	 */
	public void onBackPressed() {
        LogUtils.info(CLASS_NAME, CLASS_NAME, "Backbutton Pressed: Exiting Application");
		mIsBackButtonPressed = true;
        super.onBackPressed();
 
    }	
	

}
