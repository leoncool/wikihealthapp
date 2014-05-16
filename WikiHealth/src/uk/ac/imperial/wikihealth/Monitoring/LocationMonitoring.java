package uk.ac.imperial.wikihealth.Monitoring;

import java.util.Calendar;

import uk.ac.imperial.wikihealth.Database.SQLiteReaderWriter;
import uk.ac.imperial.wikihealth.Database.Contracts.Contract;
import uk.ac.imperial.wikihealth.Database.Contracts.GPScontract;
import android.content.ContentValues;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Class responsible for Location Monitoring
 * @author Panayiotis Kritiotis (pkritiotis@gmail.com)
 *
 */
public class LocationMonitoring {

	Context mContext;
	private static LocationManager mLocationManager;
	/**
	 * location listener which stores the location in the remote database if changed
	 */
	public LocationListener mLocationListener = new LocationListener() {
	    public void onLocationChanged(final Location location) {


			new Thread(new Runnable() {
		        public void run() {
			    	SQLiteReaderWriter db = null;
		 		
			    	Contract contract= new GPScontract();
		 		
			 		if(contract!=null){
			 			db=new SQLiteReaderWriter(mContext, contract);
			 			
			 			ContentValues values=new ContentValues();
			 			values.put(GPScontract.COLUMN_TIME, System.currentTimeMillis());
						values.put(GPScontract.COLUMN_LATITUDE,location.getLatitude());
						values.put(GPScontract.COLUMN_LONGITUTDE,location.getLongitude());
						System.out.println(values.toString());
						db.writeToDatabase(values);
			 		}
 		
		        }
		    }).start(); 
	    }

		@Override
		public void onProviderDisabled(String provider) {
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}
	};
	
	/**
	 * Constructor initiating listening for location updates
	 * @param _context
	 */
	public LocationMonitoring(Context _context){
		mContext=_context;
		mLocationManager= (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		Criteria crit = new Criteria();
		crit.setAccuracy(Criteria.ACCURACY_FINE);

		mLocationManager.requestLocationUpdates(mLocationManager.getBestProvider(crit, false), 0, 1, mLocationListener);
		mLocationManager.requestLocationUpdates(mLocationManager.NETWORK_PROVIDER, 0, 1, mLocationListener);
		mLocationManager.requestLocationUpdates(mLocationManager.GPS_PROVIDER, 0, 1, mLocationListener);
		System.out.println("background Location Listener succesfully initialized");
	}
	
	/**
	 * stops listening for updates
	 */
	public void close() {
		mLocationManager.removeUpdates(mLocationListener);
	}
	
}
