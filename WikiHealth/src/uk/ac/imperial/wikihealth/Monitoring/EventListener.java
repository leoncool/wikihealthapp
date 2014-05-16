package uk.ac.imperial.wikihealth.Monitoring;

import java.util.Calendar;

import uk.ac.imperial.wikihealth.Database.SQLiteReaderWriter;
import uk.ac.imperial.wikihealth.Database.Contracts.Contract;
import uk.ac.imperial.wikihealth.Database.Contracts.GPScontract;
import uk.ac.imperial.wikihealth.SensorFramework.SensorUtilities;
import android.content.ContentValues;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

/**
 * EventListener class which is instantiated for every selected sensor
 * @author Panayiotis Kritiotis (pkritiotis@gmail.com)
 *
 */
public class EventListener implements SensorEventListener{
	
	
	private Context mContext;
	private Handler mHandler;
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private int mInterval=5000;
	private EventListener mEventListener=this;
	private static LocationManager mLocationManager;

	/**
	 * Constructor initiating the handler responsible for getting the measurements
	 * @param _context
	 * @param s
	 * @param interval
	 */
	public EventListener(Context _context, Sensor sensor, int interval) {
		this.mContext=_context;
		this.mSensor=sensor;
		this.mInterval=interval;
		
		mSensorManager=(SensorManager) mContext.getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
		mHandler = new Handler();
		monitorValues.run();
	}
	
	/**
	 * 
	 * @param _context
	 * @param sensor
	 * @param interval
	 */
	public EventListener(Context _context, int interval) {
		this.mContext=_context;
		this.mInterval=interval;
		
		mHandler = new Handler();
	   	 mLocationManager= (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

		monitorLocation.run();
   	 System.out.println("Periodic Location Updates run");
		
	}
	
	/**
	 * Stops handler from listening for changes
	 */
	void stopRepeatingTask()
	{
		
	    mHandler.removeCallbacks(monitorValues);
	    mHandler.removeCallbacks(monitorLocation);
	}
	
	/**
	 * Method called when accuracy on the sensors is changed
	 */
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * New thread responsible for registering the listeners to start getting values from the sensors
	 */
	Runnable monitorValues = new Runnable()
	{
	     @Override 
	     public void run() {		
	    	 
	    	 mSensorManager.registerListener(mEventListener, mSensor,Integer.MAX_VALUE);		
	          mHandler.postDelayed(monitorValues, mInterval);
	     }
	};
	
	/**
	 * New thread responsible for registering the listeners to start getting values from the gps receiver
	 */
	Runnable monitorLocation = new Runnable()
	{
		private final LocationListener mLocationListener = new LocationListener() {
 		    @Override
 		    public void onLocationChanged(final Location location) {
 		    	new Thread(new Runnable() {
 			        public void run() {
 			        	System.out.println("@@@@LocationChanged");

 				 		SQLiteReaderWriter db = null;
 				 		
 				    	Contract contract= new GPScontract();
 			 		
 				 		if(contract!=null){
 				 			db=new SQLiteReaderWriter(mContext, contract);
 				 		//	Location location=mLocationManager.getLastKnownLocation(mLocationManager.getBestProvider(crit, false));
 				 			
 				 			ContentValues values=new ContentValues();
 				 			values.put(GPScontract.COLUMN_TIME, System.currentTimeMillis());
 							values.put(GPScontract.COLUMN_LATITUDE,location.getLatitude());
 							values.put(GPScontract.COLUMN_LONGITUTDE,location.getLongitude());
 							System.out.println(values.toString());
 							db.writeToDatabase(values);
 				 		}
 				 		mLocationManager.removeUpdates(mLocationListener);
 				 		 mHandler.postDelayed(monitorLocation, mInterval);

 			        }
 			    }).start(); 
 		    }

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub
				
			}
 		};
 		
	     @Override 
	     public void run() {		
		   	 mLocationManager= (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

	    	 Criteria crit = new Criteria();
			crit.setAccuracy(Criteria.ACCURACY_FINE);
	    	 mLocationManager.requestLocationUpdates(mLocationManager.getBestProvider(crit, false), 0, 1, mLocationListener);
			mLocationManager.requestLocationUpdates(mLocationManager.NETWORK_PROVIDER, 0, 1, mLocationListener);
			mLocationManager.requestLocationUpdates(mLocationManager.GPS_PROVIDER, 0, 1, mLocationListener);
	     }

	 		
	};
	
	
	/**
	 * Method called when a sensor's values are changed
	 */
	@Override
	public void onSensorChanged(final SensorEvent event) {
		
		// thread writing new values in the database
		new Thread(new Runnable() {
	        public void run() {
	            
	        	SQLiteReaderWriter db = null;
	        	Sensor s = event.sensor;
	        	System.out.println("Sensor: " + s.getType() +"\t"+ event.values.length);
	        	Contract contract= SensorUtilities.getContract(s);
	        	if(contract!=null){
		 			db=new SQLiteReaderWriter(mContext, contract);
		 			ContentValues values=SensorUtilities.getSensorValues(event.values,event.sensor.getType());
		 			db.writeToDatabase(values);
	        	}

	        }
	    }).start(); 
		mSensorManager.unregisterListener(this);
	}
	
	
}
