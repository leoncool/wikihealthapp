package uk.ac.imperial.wikihealth.UI;



import java.util.ArrayList;

import uk.ac.imperial.wikihealth.SensorFramework.SensorUtilities;
import uk.ac.imperial.wikihealth.UI.SensorFragments.LocationFragment;
import android.hardware.Sensor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * Activity presenting all available sensors in swipe-able tabs
 * Presents each sensor data in separate fragments
 * @author Panayiotis Kritiotis (pkritiotis@gmail.com)
 *
 */
public class SensorsScreen extends TabSwipeActivity{
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

        //Use SensorUtilities to get the Available Sensors of the device and display them as tabs
        SensorUtilities si=new SensorUtilities(this.getApplicationContext());
        ArrayList<String> sensors_names = si.getAvailableSensorNames();
        ArrayList<Sensor> sensors = si.getAvailableSensors();
        //Add a tab for each sensor
        for (int i = 0; i < sensors.size(); i++) {
        		addTab( sensors_names.get(i), SensorUtilities.getClassOfSensor(sensors.get(i)), new Bundle() );
        }
        addTab( "Location", LocationFragment.class, new Bundle() );

	
	}

	/**
	 * Initialize ActionBar
	 */
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		//Add items to the menu
    	menu=mActionbar.addMenuItems(menu);
    	return super.onCreateOptionsMenu(menu);
    }
	
	/**
	 * Handle ActionBar Events
	 */
	 @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	    	if(mActionbar.handleMenuClicks(item, this)){
	    		finish();
	    		return true;
	    	}
	    	//else
	    	//	finish();
	    	return super.onOptionsItemSelected(item);
	    }





}
