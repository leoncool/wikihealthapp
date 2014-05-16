package uk.ac.imperial.wikihealth.UI;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * Screen which appears in the first execution of the application and asks the user to register or login
 * Contains two tabs: one for login and one for registration
 * @author Panayiotis Kritiotis (pkritiotis@gmail.com)
 *
 */
public class EnterScreen extends TabSwipeActivity{

	//View variables
	ActionbarBuilder mActionbar=new ActionbarBuilder("Sensors");
	ViewPager mViewPager;

    /**
     * Called when activity is created
     */
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set navigation type to swipe tabs
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        //add login and registration tabs to the navigation bar
        addTab("Login",LoginScreen.class, new Bundle() );
        addTab("Register",RegisterScreen.class, new Bundle() );

      //  addTab("Register", RegisterScreen.class, new Bundle() );

	
	}

	/**
	 * Initialize ActionBar
	 */
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		//Add items to the menu
    	return super.onCreateOptionsMenu(menu);
    }
	
	/**
	 * Handle ActionBar Events
	 */
	 @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
		 	return super.onOptionsItemSelected(item);
	    }





}