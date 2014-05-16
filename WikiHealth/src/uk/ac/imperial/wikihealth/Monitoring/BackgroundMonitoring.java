package uk.ac.imperial.wikihealth.Monitoring;


import java.util.ArrayList;

import uk.ac.imperial.wikihealth.R;
import uk.ac.imperial.wikihealth.Database.DBUtils;
import uk.ac.imperial.wikihealth.Database.Contracts.GPScontract;
import uk.ac.imperial.wikihealth.SensorFramework.SensorUtilities;
import uk.ac.imperial.wikihealth.UI.StatusScreen;
import uk.ac.imperial.wikihealth.Utils.PreferenceUtils;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;

/**
 * The service that implements the background monitoring
 * set's up a listener for every sensor and handles the process
 * @author Panayiotis Kritiotis (pkritiotis@gmail.com)
 *
 */
public class BackgroundMonitoring extends Service{

	private static Context mContext;
	private static ArrayList<EventListener> mEventListeners; //contains a list of all currently active event listeners
	//private static BackgroundListener mSingletonInstance;	//Singleton instance
	private static LocationMonitoring mLocationManager;	//Location Monitoring reference
	public static boolean mIsInstanceStopped = true;
	private SensorManager mSensorManager=null;
	public static boolean isServiceActive;
    private NotificationManager mNM;
	private Handler mHandler;
    public static final String MY_SERVICE = "uk.ac.imperial.wikihealth.Monitoring.MY_SERVICE";
    private DBUtils db;
	
	@Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification();
    }
	
	 
	/**
	 * shows an icon in the notification bar
	 */
	@SuppressWarnings("deprecation")
	private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = "Monitoring Active";

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.ic_launcher, text,
        System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, StatusScreen.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, "Wikihealth",text, contentIntent);
        notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

        // Send the notification.
        mNM.notify("local service wiki", 0, notification);
    }
    @Override

    /**
     * when the service starts
     */
	public int onStartCommand(Intent intent, int flags, int startId) {
        isServiceActive=true;
		mContext=this.getApplicationContext();
		SharedPreferences settings = mContext.getApplicationContext().getSharedPreferences("service_status", 0);
		settings.edit().putBoolean("service_runs", true).commit(); 
		mSensorManager = (SensorManager) mContext.getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
   	 	SensorUtilities si=new SensorUtilities(mContext.getApplicationContext());

        ArrayList<Sensor> sensors = si.getAvailableSensors();
        mEventListeners = new ArrayList<EventListener>();
   	 	for(Sensor s: sensors){
   	 			boolean monitor=PreferenceUtils.isChecked(mContext,SensorUtilities.getContract(s));
   	 			int period = Integer.valueOf(PreferenceUtils.getPeriod(mContext,SensorUtilities.getContract(s)));
   	 			if(monitor){
   	 				EventListener el=new EventListener(mContext, s,period);
   	 				mEventListeners.add(el);
   	 				mSensorManager.registerListener(el,s, Integer.MAX_VALUE);
   	 			}
       }
		if(PreferenceUtils.isChecked(mContext,new GPScontract())){
			if(!PreferenceUtils.isPeriodic(mContext,new GPScontract())){
				if(mLocationManager==null){
					mLocationManager=new LocationMonitoring(mContext);
				}
			}
			else{
	   	 			int period = Integer.valueOf(PreferenceUtils.getPeriod(mContext,new GPScontract()));

					EventListener el=new EventListener(mContext,period);
   	 				mEventListeners.add(el);
				}
		}
		mHandler = new Handler();
		checkSize.run();
		//db=new DBUtils(mContext);
        return START_STICKY;	

	}
    /**
     * stops monitoring
     */
    void stopRepeatingTask()
	{
	    mHandler.removeCallbacks(checkSize);
	}
	
	
	/**
	 * New thread responsible for registering the listeners to start getting values from the sensors
	 */
	Runnable checkSize = new Runnable()
	{
	     @Override 
	     public void run() {
	    	 db=new DBUtils(mContext);
	    	 if(db!=null && db.isFull()){
	    		 stopRepeatingTask();


	    		 onDestroy();
 
//	    		
	    		 return;
	    	 }
	    	 else
	    	 mHandler.postDelayed(checkSize, 5000);
	     }
	};
	
		
	@Override
	public void onDestroy(){
		stopListening();
		isServiceActive=false;
		 stopRepeatingTask();

	System.out.println("destroyed");
	super.onDestroy();
	}
	
	 public void releaseBind(){
		    unbindService((ServiceConnection) this);
		  }
	 
		public void stopListening() {
			for(EventListener e:mEventListeners)
				e.stopRepeatingTask();
			if(mLocationManager!=null)
				mLocationManager.close();
			mIsInstanceStopped=true;
			if(mNM!=null)
				mNM.cancelAll();
			stopSelf();
		}
		
		@Override
		public IBinder onBind(Intent intent) {
			// TODO Auto-generated method stub
			return null;
		}
		
}