package uk.ac.imperial.wikihealth.UI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import uk.ac.imperial.wikihealth.R;
import uk.ac.imperial.wikihealth.DataUpload.DataUploader;
import uk.ac.imperial.wikihealth.Database.DBUtils;
import uk.ac.imperial.wikihealth.Database.SQLiteReaderWriter;
import uk.ac.imperial.wikihealth.Database.Contracts.UploadContract;
import uk.ac.imperial.wikihealth.Monitoring.BackgroundMonitoring;
import uk.ac.imperial.wikihealth.Utils.ConnectivityUtil;
import uk.ac.imperial.wikihealth.Utils.LogUtils;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * Screen shown when the app is executed after the splash screen.
 * Contains an action bar which provides access to the menu.
 * 
 * Provides user with the option to start the monitoring operation by clicking an ImageButton
 * @author Panayiotis Kritiotis (pkritiotis@gmail.com)
 *
 */
@SuppressLint("ShowToast")
public class StatusScreen extends SherlockActivity {
	
	// UI elements
	private ActionbarBuilder mActionBarBuilder=new ActionbarBuilder("Status"); // Action bar object
	private Button mMonitoringButton; // Button view handler for UI interaction
	private TextView mStatusText;
	private static Button mUploadButton;
	private ListView mUploadList;
	private static ProgressBar mUploadProgress;
	private static TextView mUploadText;
	// Variables for handling background sensor monitoring
	//private static BackgroundListener sBackgroundListener;
	private static boolean sIsListening =true;
	private Context mContext=this;
	private final String CLASS_NAME="StatusScreen";
	private static TextView mDBText;
	private static Intent mIntent;

	//UI flags
	public final int UPLOAD_UPDATE=0;
	public final int NORMAL_UPDATE=1;

	/**
	 * Called when the activity is first created
	 * Initializes variables for future handling and backups database for debugging purposes
	 * @param savedInstanceState
	 */
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=StatusScreen.this;

        LogUtils.info(CLASS_NAME, CLASS_NAME, "Activity Started");
        sIsListening=isMyServiceRunning();
        //Show view of status screen
        setContentView(R.layout.activity_status_screen);
        LogUtils.info(CLASS_NAME, CLASS_NAME, "Layout shown succesfully");
        
        //Initialize UI variables and handlers
        LogUtils.info(CLASS_NAME, CLASS_NAME, "Initializing view references");
        initializeViews();	
        LogUtils.info(CLASS_NAME, CLASS_NAME, "Views initialized succesfully");

        //Backups existing database to sd card
        LogUtils.info(CLASS_NAME, CLASS_NAME, "Backing up database to sdcard");
        backupDatabase();
        LogUtils.info(CLASS_NAME, CLASS_NAME, "Backup Succesful");
       
    }
	 @SuppressLint("SdCardPath")
	public static void backupDatabase(){
		    //Open local db as the input stream
		    final String inFileName = "/data/data/uk.ac.imperial.wikihealth/databases/Sensors.db";
		    File dbFile = new File(inFileName);
		    FileInputStream fis;
			try {
				fis = new FileInputStream(dbFile);
			    String outFileName = Environment.getExternalStorageDirectory()+"/database.sqlite";
			    //Open the empty db as the output stream
			    OutputStream output = new FileOutputStream(outFileName);
			    //transfer bytes from the inputfile to the outputfile
			    byte[] buffer = new byte[1024];
			    int length;
			    while ((length = fis.read(buffer))>0){
			        output.write(buffer, 0, length);
			    }
			    //Close the streams
			    output.flush();
			    output.close();
			    fis.close();
			} catch (IOException e) {
				LogUtils.err("StatusScreen","StatusScreen", "Backup Database could not be created");
			}
		}
	 
	/**
	 * Initialize screen UI elements after execution
	 */
    private void initializeViews() {
    	 // Initialize Screen Views
        mMonitoringButton = (Button) findViewById(R.id.imageb);
        mStatusText=(TextView)findViewById(R.id.statustxt);
        mUploadButton=(Button) findViewById(R.id.uploadData);
        mUploadList = (ListView)findViewById(R.id.uploadList);
		mUploadProgress=(ProgressBar)findViewById(R.id.uploadProgress);
		mUploadText=(TextView)findViewById(R.id.uploadText);
		mDBText=(TextView)findViewById(R.id.dbstatustext);
		
		updateDBstatus(NORMAL_UPDATE);
					

		
	    // Set Status button click listener
        mMonitoringButton.setOnClickListener(new View.OnClickListener() {
        		@Override
        			public void onClick(View v) {

        			
        			DBUtils db=new DBUtils(mContext);
        			
        			if(db!= null && db.isFull()){
	                        	Toast.makeText(StatusScreen.this, "Too much data are concentrated in the database. Please upload them and try again",Toast.LENGTH_LONG).show();
	                        }
        			else
        				if(mUploadProgress.getVisibility()==ProgressBar.VISIBLE){
        	            	Toast.makeText(StatusScreen.this, "Uploading progress needs to be completed before start monitoring",Toast.LENGTH_LONG).show();
                    	}
        				else{
        					monitoringButtonClicked();
        				}
        			}
	    		});
        
        
        if(DataUploader.sInstance)
        	showUploadingProgress(true);

        //Set Upload Button click listener
        mUploadButton.setOnClickListener(new View.OnClickListener() {
			DBUtils db=new DBUtils(mContext);
			@Override
			public void onClick(View v) {
				
				//check for internet connection
            	if(!ConnectivityUtil.isNetworkAvailable(mContext))
            		ConnectivityUtil.buildAlertMessageNoInternet(mContext);
            	else if(sIsListening){
	            	Toast.makeText(StatusScreen.this, "You should stop the monitoring process before uploading your data ",Toast.LENGTH_LONG).show();
            	}
            	else if(db.getCapacityPercentage()<=0){
	            	Toast.makeText(StatusScreen.this, "Local Database is empty. There is nothing to upload.",Toast.LENGTH_LONG).show();
            	}
            	else{
                	showUploadingProgress(true);

	            	Toast.makeText(StatusScreen.this, "Uploading Data",Toast.LENGTH_LONG).show();
	
					 new Thread(new Runnable() {
							public void run() {
					        	DataUploader data_uploader = new DataUploader(mContext);
					        	try{
					        	data_uploader.upload();
					        	}catch(Exception e){
					        		runOnUiThread(new Runnable() {
				                        @Override
				                        public void run() {
				                    		Toast.makeText(mContext, "Data uploading interrupted due to network problem.", Toast.LENGTH_LONG).show();
				                        	new UpdateUploadList().execute();
//				                        	updateUploadList();
				                        	showUploadingProgress(false);
				                        }
					        	 });
					        	}
					        	 runOnUiThread(new Runnable() {
				                        @Override
				                        public void run() {
				                    		Toast.makeText(mContext, "Data uploaded succesfully", Toast.LENGTH_LONG).show();
				                        	new UpdateUploadList().execute();
//				                        	updateUploadList();
				                        	updateDBstatus(UPLOAD_UPDATE);
				                        	showUploadingProgress(false);
				                        }
					        	 });
					        }
					    }).start(); 
					
	            	}
				}
		});
        
        
        //Set appropriate icons/labels
        updateMonitorViews();
    	new UpdateUploadList().execute();

//        updateUploadList();

        		
	}
    
    private void updateDBstatus(int type){
    	double percentage;
    	
    	if(type==UPLOAD_UPDATE){
    		percentage=0;
    	}
    	else{
    	DBUtils db=new DBUtils(mContext);
    	percentage=db.getCapacityPercentage();
//    	percentage=percentage/10000000;
    	}
//    	ProgressBar db_prog=(ProgressBar)findViewById(R.id.db_progress);
    	
    	if(percentage>100){
    		percentage=100;
    	}
//    	db_prog.setProgress((int)percentage);
//		final String db_string=String.valueOf(percentage)+"%";
    	final String db_string=String.valueOf(percentage)+"rows";
		mDBText.setText(db_string);
		if(percentage>20000000){
			mDBText.setTextColor(getResources().getColor(R.color.db_status_full));
		}
		else
		if(percentage>10000000){
			mDBText.setTextColor(getResources().getColor(R.color.db_status_medium));
		}
		else
			mDBText.setTextColor(getResources().getColor(R.color.db_status_ok));

    }
    
    
    private boolean isMyServiceRunning() {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (BackgroundMonitoring.class.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}

    /**
     * 
     * Update UI elements according to the state of monitoring
     * 
     */
	private void updateMonitorViews() {
		LogUtils.info(CLASS_NAME, CLASS_NAME, "Updating views");
		 if(!sIsListening || !isMyServiceRunning()){
	        	mMonitoringButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.off));
	        	mStatusText.setText(getResources().getString(R.string.status_not_monitoring));
	        	mStatusText.setTextColor(getResources().getColor(R.color.not_monitoring));
	        }
	        else{
	        	mMonitoringButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.on));
	        	mStatusText.setText(getResources().getString(R.string.status_monitoring));
	        	mStatusText.setTextColor(getResources().getColor(R.color.monitoring));

	        }		
		 updateDBstatus(NORMAL_UPDATE);
		 LogUtils.info(CLASS_NAME, CLASS_NAME, "Views updated");
	}
	
	public void stopMyService(){
		  ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
		        if (BackgroundMonitoring.class.getName().equals(service.service.getClassName())) {
		         stopService(new Intent(this,service.service.getClass()));
		            
		        }
		    }
	}
	
	/**
	 * Handles view updates for progress ui elements while uploading
	 * @param show
	 */
	private void showUploadingProgress(boolean show) {
		
		if(show){
			LogUtils.info(CLASS_NAME, CLASS_NAME, "Updating views");
			mUploadProgress.setVisibility(ProgressBar.VISIBLE);
	   		mUploadButton.setVisibility(Button.GONE);
	   		mUploadText.setVisibility(TextView.VISIBLE);
		}
		else{
			LogUtils.info(CLASS_NAME, CLASS_NAME, "Updating views");
			mUploadProgress.setVisibility(ProgressBar.GONE);
	   		mUploadButton.setVisibility(Button.VISIBLE);
	   		mUploadText.setVisibility(TextView.GONE);
		}
	}

	@Override
	public void onResume(){
		super.onResume();		registerReceiver(onBroadcast, new IntentFilter("mymessage"));

	}
	@Override
	public void onPause(){
		super.onPause();
		unregisterReceiver(onBroadcast);

	}

	/**
     * Click handler executed when button is clicked
     * Starts monitoring sensors when activated, otherwise it is stopped
     */
	private BroadcastReceiver onBroadcast = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context ctxt, Intent i) {
	    	System.out.println("br received");
	    	if(i.getExtras().getBoolean("wikihealth_stopped")==true){
	    	mIntent=null;
	        sIsListening=false;
	        updateMonitorViews();
	        Toast.makeText(mContext, "Background Monitoring stopped because local database is full. You need to upload the data",Toast.LENGTH_LONG).show();
	    }
	    }
	};
	
	private void monitoringButtonClicked() {
		
		
		if(!sIsListening || BackgroundMonitoring.isServiceActive!=true){
			LogUtils.info(CLASS_NAME, CLASS_NAME, "Calling Background Listener to start Monitoring");
			//sBackgroundListener=BackgroundListener.getBackgroundListener(this);
			mIntent=new Intent(mContext,BackgroundMonitoring.class);
			mContext.startService(mIntent);
			sIsListening=true;
			updateMonitorViews();
		  }
	  	else{
	  	  LogUtils.info(CLASS_NAME, CLASS_NAME, "Stoping Background Listener");
		  //sBackgroundListener.stopListening();
		  mIntent=null;
		  stopService(new Intent(BackgroundMonitoring.MY_SERVICE));
		  sIsListening=false;
		  updateMonitorViews();
	  	}		
	}
	
	/**
	 * Show action bar and add the appropriate menu items to it
	 */
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		// Add menu items to action bar
		menu=mActionBarBuilder.addMenuItems(menu);
    	return super.onCreateOptionsMenu(menu);
    }
	
	/**
	 * Handles Action bar's events
	 */
	 @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
		 // Handle action bar clicks
		 if(mActionBarBuilder.handleMenuClicks(item, this))
	    		return true;
	    	return super.onOptionsItemSelected(item);
	    }
	 
	 private class UpdateUploadList extends AsyncTask<String, Void, String[]> {

         @Override
         protected String[] doInBackground(String... params) {
              
        	Log.i(CLASS_NAME, "Updating upload records");
     		SQLiteReaderWriter db = new SQLiteReaderWriter(mContext, new UploadContract());
     		Log.i(CLASS_NAME,"SQLite initialized");
     		String []values=db.getUploadListFromDatabase();
     		Log.i(CLASS_NAME,"List fetched");

        	 

               return values;
         }      

         @Override
         protected void onPostExecute(String [] values) {
        	 if(values ==null){
     			mUploadList.setAdapter(null);
     			return;
     		}
     		ArrayList<String> list = new ArrayList<String>();
     	    for (int i = 0; i < values.length; ++i) {
     	    	list.add(values[i]);
     	    }
     		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,R.layout.listitem,R.id.list_content, list);
     	    mUploadList.setAdapter(adapter);	
     		LogUtils.info(CLASS_NAME, CLASS_NAME, "Records update succesfully");
     		if(mUploadProgress.getVisibility()==ProgressBar.VISIBLE){
     			//Toast.makeText(StatusScreen.this, "Data uploaded succesfully on remote Database",Toast.LENGTH_LONG).show();
     		}

         }

         @Override
         protected void onPreExecute() {
         }

         @Override
         protected void onProgressUpdate(Void... values) {
         }
   }   
}