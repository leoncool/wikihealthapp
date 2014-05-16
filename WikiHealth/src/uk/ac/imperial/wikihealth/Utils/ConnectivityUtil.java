package uk.ac.imperial.wikihealth.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Utils for checking connectivity of the device and prompting user with dialog boxes for activating the needed services
 * @author Panayiotis Kritiotis (pkritiotis@gmail.com)
 *
 */
public class ConnectivityUtil {
	
	
	/**
	 * Builds an alert message for GPS activation
	 * @param activity
	 */
	public static void buildAlertMessageNoGps(final Context activity) {
  	    final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
  	    builder.setMessage("This option needs GPS to proceed. Do you wish to activate GPS?")
  	           .setCancelable(false)
  	           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
  	               public void onClick(final DialogInterface dialog, final int id) {
  	                   activity.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));// ACTION_LOCATION_SOURCE_SETTINGS));
  	               }
  	           })
  	           .setNegativeButton("No", new DialogInterface.OnClickListener() {
  	               public void onClick(final DialogInterface dialog, final int id) {
  	                    dialog.cancel();
  	               }
  	           });
  	    final AlertDialog alert = builder.create();
  	    alert.show();
  	}

	/**
	 * Builds an alert mesage for Internet connection activation
	 * @param activity
	 */
    public static void buildAlertMessageNoInternet(final Context activity) {
	  	    final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
	  	    builder.setMessage("This option needs Internet connection to proceed. Do you wish to activate WiFi/Mobile Internet? ")
	  	           .setCancelable(false)
	  	           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	  	               public void onClick(final DialogInterface dialog, final int id) {
	  	            	 activity.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS ));
	  	               }
	  	           })
	  	           .setNegativeButton("No", new DialogInterface.OnClickListener() {
	  	               public void onClick(final DialogInterface dialog, final int id) {
	  	                    dialog.cancel();
	  	               }
	  	           });
	  	    final AlertDialog alert = builder.create();
	  	    alert.show();
	  	}  
    
    /**
     * Checks if a network is available
     * @param activity
     * @return
     */
	public static boolean isNetworkAvailable(Context activity) {
	    ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	/**
	 * Checks if GPS is activated
	 * @param activity
	 * @return
	 */
	public static boolean isGPSAvailable(Context activity) {
		final LocationManager manager = (LocationManager) activity.getSystemService( Context.LOCATION_SERVICE );
		if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ){
			return false;
		}
		else return true;
	}
}
