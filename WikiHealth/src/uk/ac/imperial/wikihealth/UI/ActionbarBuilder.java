package uk.ac.imperial.wikihealth.UI;

import uk.ac.imperial.wikihealth.R;
import uk.ac.imperial.wikihealth.Utils.ConnectivityUtil;
import android.content.Context;
import android.content.Intent;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;

/**
 * Class which adds the items on the action bar with respect to the calling activity
 * @author Panayiotis Kritiotis (pkritiotis@gmail.com)
 *
 */
public class ActionbarBuilder {

	//Calling activity identifiers
	private final int SENSORS_ACTIVITY =1;
//	private final int SETTINGS_ACTIVITY =2;
	private final int MAP_ACTIVITY =3;
	private final int STATUS_ACTIVITY = 4;
	private final int ACTIVITIES_ACTIVITY = 5;
	private final int HEARTRATE_ACTIVITY = 6;

	private int mCallingActivity=-1;
	
	/**
	 * Initialize instance to the respective activity identifier
	 * @param activity
	 */
	public ActionbarBuilder(String activity){
		if(activity.equals("Status"))
			mCallingActivity=STATUS_ACTIVITY;
		else if(activity.equals("Sensors"))
			mCallingActivity=SENSORS_ACTIVITY;
//		if(activity.equals("Settings"))
//			mCallingActivity=SETTINGS_ACTIVITY;
		if(activity.equals("Map"))
			mCallingActivity=MAP_ACTIVITY;
		if(activity.equals("Activities"))
			mCallingActivity=ACTIVITIES_ACTIVITY;
		if(activity.equals("HeartRate"))
			mCallingActivity=HEARTRATE_ACTIVITY;
	}
	
	/**
	 * Adds the menu items on the screen and adds extra buttons for some specific activities
	 * @param menu
	 * @return
	 */
    public  Menu addMenuItems(Menu menu) {
    	
    	if(mCallingActivity==HEARTRATE_ACTIVITY){
        	menu.add("").setTitle("Connect to Heart-Rate Monitor").setIcon(R.drawable.ic_refresh).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        	menu.add("").setTitle("Activate Bluetooth").setIcon(R.drawable.bluetooth_icon_small).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
    	
    	//Add Items to submenu with their unique IDs
		SubMenu subMenu1 = menu.addSubMenu("Menu");
		subMenu1.add(0,4,Menu.NONE,"Status");
        subMenu1.add(0,1,Menu.NONE,"Sensors");
        //subMenu1.add(0,2,Menu.NONE,"Settings");
        subMenu1.add(0,3,Menu.NONE,"Location");
        subMenu1.add(0,5,Menu.NONE,"Activities");
        subMenu1.add(0,6,Menu.NONE,"Heart-rate");

        //Remove the menu item of the current activity
        subMenu1.removeItem(mCallingActivity);
        MenuItem subMenu1Item = subMenu1.getItem();
        subMenu1Item.setIcon(R.drawable.menu_icon_small);
        subMenu1Item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        
        return menu;
     }
    
   
    /**
     * Handles clicks on the menu and starts the respective activities depending on the action of the user
     * @param item
     * @param callingActivity
     * @return
     */
    public boolean handleMenuClicks(MenuItem item, Context callingActivity) {
    	if(item.getItemId()==mCallingActivity)
    		return false;
    	switch (item.getItemId()) {
        	case STATUS_ACTIVITY:
            // app icon in action bar clicked; go to status screen
            Intent intent_home = new Intent(callingActivity, StatusScreen.class);
            intent_home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            callingActivity.startActivity(intent_home);
            
            return true;
            case SENSORS_ACTIVITY:
                // app icon in action bar clicked; go to sensors
                Intent intent_sensors = new Intent(callingActivity, SensorsScreen.class);
                callingActivity.startActivity(intent_sensors);
               
                return true;
//            case SETTINGS_ACTIVITY:
//                // app icon in action bar clicked; go to settings
//                Intent intent_settings = new Intent(callingActivity, SettingsScreen.class);
//                callingActivity.startActivity(intent_settings);
//                return true;
            case MAP_ACTIVITY:
                // app icon in action bar clicked; go to map screen
            	if(!ConnectivityUtil.isNetworkAvailable(callingActivity)){
            		ConnectivityUtil.buildAlertMessageNoInternet(callingActivity);
            		return false;
            	}
            	else if(!ConnectivityUtil.isGPSAvailable(callingActivity)){
            		ConnectivityUtil.buildAlertMessageNoGps(callingActivity);
            		System.out.println("false");
            		return false;
            	}
            	else{
	                Intent intent_map = new Intent(callingActivity, MapScreen.class);
	                callingActivity.startActivity(intent_map);
	                return true;
            	}
            case ACTIVITIES_ACTIVITY:
                // app icon in action bar clicked; go to activities screen
                Intent intent_activities = new Intent(callingActivity, ActivitiesScreen.class);
                callingActivity.startActivity(intent_activities);
                return true;
            case HEARTRATE_ACTIVITY:
                // app icon in action bar clicked; go to help screen
            	if(HeartRateScreen.getInstance()!=null){
            		HeartRateScreen.getInstance().fin();
            	}
                Intent intent_heart = new Intent(callingActivity, HeartRateScreen.class);
                callingActivity.startActivity(intent_heart);
            	
            	
                return true;
            default:
        		if(item.getTitle().toString().equals("Activate Bluetooth")){
            		//Toast.makeText(callingActivity, "Bluetooth Clicked", Toast.LENGTH_SHORT).show();
            		Intent intentBluetooth = new Intent();
            	    intentBluetooth.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
            	    callingActivity.startActivity(intentBluetooth); 
            		return false;
        		}
            		
                return false;
        }
    }
    
}
