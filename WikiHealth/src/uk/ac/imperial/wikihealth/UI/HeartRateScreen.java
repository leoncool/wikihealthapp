package uk.ac.imperial.wikihealth.UI;

import java.util.Calendar;
import java.util.Set;

import uk.ac.imperial.wikihealth.R;
import uk.ac.imperial.wikihealth.Database.SQLiteReaderWriter;
import uk.ac.imperial.wikihealth.Database.Contracts.Contract;
import uk.ac.imperial.wikihealth.Database.Contracts.Heartratecontract;
import uk.ac.imperial.wikihealth.hxmMonitor.HxmService;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;


/**
 * 
 * Activity containing the heart-rate Zephyr HxM Monitor Functionality and presentation
 * Code used from the open-source project: www.zephyrtexhnology.com and modified in our needs
 * @author Panayiotis Kritiotis (pkritiotis@gmail.com)
 *
 */
public class HeartRateScreen extends SherlockActivity {
	ActionbarBuilder m=new ActionbarBuilder("HeartRate");

	//  TAG for Debugging Log
    private static final String TAG = "hxmDemo";
        
    private static TextView mStatus;
    private static String mHxMName = null;
    private static String mHxMAddress = null;
    private static BluetoothAdapter mBluetoothAdapter = null;
    private static HxmService mHxmService = null;
	@SuppressWarnings("unused")
	private static String mState;
    public static HeartRateScreen instance;

    public static HeartRateScreen getInstance() {
        return instance;
    }
    
    public void fin(){
    	//finish();
    }
    
    /*
     * connectToHxm() sets up our service loops and starts the connection
     * logic to manage the HxM device data stream 
     */
    private void connectToHxm() {

    	mStatus.setText(R.string.connecting);
      	mState=getResources().getString(R.string.connecting);

	    if (mHxmService == null) 
	    	setupHrm();
	    
	    if ( getFirstConnectedHxm() ) {
	    	BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mHxMAddress);
	    	mHxmService.connect(device); 	// Attempt to connect to the device
	    } else {
	      	mStatus.setText(R.string.nonePaired);	    	
          	mState=getResources().getString(R.string.nonePaired);

	    }
    
    }
    
    
    /*
     * Loop through all the connected bluetooth devices, the first one that 
     * starts with HXM will be assumed to be our Zephyr HxM Heart Rate Monitor,
     * and this is the device we will connect to
     * 
     * returns true if a HxM is found and the global device address has been set 
     */
    private boolean getFirstConnectedHxm() {

    	mHxMAddress = null;    	
    	mHxMName = null;
	    
    	//Get the local Bluetooth adapter
	    BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();

	    /*
	     *  Get a set of currently paired devices to cycle through, the Zephyr HxM must
	     *  be paired to this Android device, and the bluetooth adapter must be enabled
	     */
	    Set<BluetoothDevice> bondedDevices = mBtAdapter.getBondedDevices();

	    /*
	     * For each device check to see if it starts with HXM, if it does assume it
	     * is the Zephyr HxM device we want to pair with      
	     */
	    if (bondedDevices.size() > 0) {
	        for (BluetoothDevice device : bondedDevices) {
	        	String deviceName = device.getName();
	        	if ( deviceName.startsWith("HXM") ) {
	        	
	        		mHxMAddress = device.getAddress();
	        		mHxMName = device.getName();
	        		Log.d(TAG,"getFirstConnectedHxm() found a device whose name starts with 'HXM', its name is "+mHxMName+" and its address is ++mHxMAddress");
	        		break;
	        	}
	        }
	    }
    
	    return (mHxMAddress != null);
   }

    
        
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        instance=this;
        if(mHxmService==null){
	        setContentView(R.layout.rawdata);
	
	        mStatus = (TextView) findViewById(R.id.status);
		    
	       	mStatus.setText(R.string.initializing);
          	mState=getResources().getString(R.string.initializing);

	        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	
	        /*
	         *  If the adapter is null, then Bluetooth is not supported
	         */
	        if (mBluetoothAdapter == null) {
		        /*
		         * Bluetoooth needs to be available on this device, and also enabled.  
		         */
	            Toast.makeText(this, "Bluetooth is not available or not enabled", Toast.LENGTH_LONG).show();
	           	mStatus.setText(R.string.noBluetooth);
	          	mState=getResources().getString(R.string.noBluetooth);

	         } else {
	
	        	 if (!mBluetoothAdapter.isEnabled()) {
		          	mStatus.setText(R.string.btNotEnabled);
		          	mState=getResources().getString(R.string.btNotEnabled);

		        	Log.d(TAG, "onStart: Blueooth adapter detected, but it's not enabled");
		        } else {
		          	mStatus.setText(R.string.connecting);
		          	mState=getResources().getString(R.string.connecting);

			        connectToHxm();
		        }
	         }
        }
        else
        {
	        //mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        	setContentView(R.layout.rawdata);
           	instance.onResume();
          	mState=instance.getResources().getString(R.string.connectedTo)+mHxMName;
        	//BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mHxMAddress);
	    	//mHxmService.connect(device); 	// Attempt to connect to the device
        }
    }
    

    
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");

        if (mBluetoothAdapter != null ) {
	        // If BT is not on, request that it be enabled.
	        // setupChat() will then be called during onActivityResult     
	        if (!mBluetoothAdapter.isEnabled()) {
	          	mStatus.setText(R.string.btNotEnabled);
	          	mState=getResources().getString(R.string.btNotEnabled);
	        }
        } else {
          	mStatus.setText(R.string.noBluetooth);
          	mState=getResources().getString(R.string.noBluetooth);

        }
	    
    }
    
    @Override
    public synchronized void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mHxmService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mHxmService.getState() == R.string.HXM_SERVICE_RESTING) {
              // Start the Bluetooth scale services
            	mStatus.setText(instance.getResources().getString(R.string.connectedTo)+mHxMName);
              mHxmService.start();
            }
        }
    }

    private void setupHrm() {
        // Initialize the service to perform bluetooth connections
        mHxmService = new HxmService(this, mHandler);
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    
    @Override
    public void onDestroy() {
    	if(!mStatus.getText().toString().equals(instance.getResources().getString(R.string.connectedTo)+mHxMName)){
    		instance=null;
    		mHxmService=null;
    		if (mHxmService != null) mHxmService.stop();
    		finish();
    	}
    		
        super.onDestroy();
        // Stop the Bluetooth chat services
//        if (mHxmService != null) mHxmService.stop();
    }

     /**
     * Sends a message.
     * @param message  A string of text to send.
     */
    // The Handler that gets information back from the hrm service
    @SuppressLint("HandlerLeak")
	private static final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case R.string.HXM_SERVICE_MSG_STATE: 
                Log.d(TAG, "handleMessage():  MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
	                case R.string.HXM_SERVICE_CONNECTED:
	                	if ((mStatus != null) && (mHxMName != null)) {
	                		mStatus.setText(R.string.connectedTo);
	                		mStatus.append(mHxMName);
	        	          	mState=instance.getResources().getString(R.string.connectedTo)+mHxMName;

	                	}
	                    break;

	                case R.string.HXM_SERVICE_CONNECTING:
	                    mStatus.setText(R.string.connecting);
	    	          	mState=instance.getResources().getString(R.string.connecting);

	                    break;
	                    
	                case R.string.HXM_SERVICE_RESTING:
	                	if (mStatus != null ) {
	                		mStatus.setText(R.string.notConnected);
	        	          	mState=instance.getResources().getString(R.string.notConnected);

	                	}
	                    break;
                }
                break;

            case R.string.HXM_SERVICE_MSG_READ: {
            	/*
            	 * MESSAGE_READ will have the byte buffer in tow, we take it, build an instance
            	 * of a HrmReading object from the bytes, and then display it into our view
            	 */
                byte[] readBuf = (byte[]) msg.obj;
                HrmReading hrm = new HrmReading( readBuf );
                hrm.displayRaw();
                hrm.writeToDatabase();
                break;
            }
                
            case R.string.HXM_SERVICE_MSG_TOAST:
                Toast.makeText(instance.getApplicationContext(), msg.getData().getString(null),Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		//Add items on action bar
		menu=m.addMenuItems(menu);
    	return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	 	//Handles clicks on menu sub-items
    	if(m.handleMenuClicks(item, this)){
    		finish();    	
    		return true;
    	}
    	else
    		if(item.getTitle().toString().equals("Connect to Heart-Rate Monitor")){
        		 connectToHxm(); 
        		 System.out.println("refreshed!");
    		}
    	System.out.println("false");
    	return super.onOptionsItemSelected(item);
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + requestCode);
        
        switch(requestCode)
        {  
        
	        case 1:
	       	    break;

		}             
    }
    
    /*
     * HrmReading
     * 
     * This class holds the information corresponding to a single message from 
     * the Zephyr HxM Heart Rate Monitor
     * 
     * The constructor HrmReading(byte[]) will fill the member fields from the bytes presumably 
     * read from a connected Zephyr HxM Heart Rate Monitor.  Because Java does not support 
     * signed/unsigned variants of numbers, we sometimes put the fields extracted from the 
     * HxM message into fields larger than is necessary.
     * 
     *
     *  
     */
    public static class HrmReading {
        public final int STX = 0x02;
        public final int MSGID = 0x26;
        public final int DLC = 55;
        public final int ETX = 0x03;
    	
    	private static final String TAG = "HrmReading";

    	int serial;
        byte stx;
        byte msgId;
        byte dlc;
        int firmwareId;
        int firmwareVersion;
        int hardWareId;
        int hardwareVersion;
        int batteryIndicator;
        int heartRate;
        int heartBeatNumber;
        long hbTime1;
        long hbTime2;
        long hbTime3;
        long hbTime4;
        long hbTime5;
        long hbTime6;
        long hbTime7;
        long hbTime8;
        long hbTime9;
        long hbTime10;
        long hbTime11;
        long hbTime12;
        long hbTime13;
        long hbTime14;
        long hbTime15;
        long reserved1;
        long reserved2;
        long reserved3;
        long distance;
        long speed;
        byte strides;    
        byte reserved4;
        long reserved5;
        byte crc;
        byte etx;

        public HrmReading (byte[] buffer) {
        	int bufferIndex = 0;

        	Log.d ( TAG, "HrmReading being built from byte buffer");
        	
            try {
    			stx 				= buffer[bufferIndex++];
    			msgId 				= buffer[bufferIndex++];
    			dlc 				= buffer[bufferIndex++];
    			firmwareId 			= (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
    			firmwareVersion 	= (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
    			hardWareId 			= (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
    			hardwareVersion		= (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
    			batteryIndicator  	= (int)(0x000000FF & (int)(buffer[bufferIndex++]));
    			heartRate  			= (int)(0x000000FF & (int)(buffer[bufferIndex++]));
    			heartBeatNumber  	= (int)(0x000000FF & (int)(buffer[bufferIndex++]));
    			hbTime1				= (long) (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
    			hbTime2				= (long) (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
    			hbTime3				= (long) (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
    			hbTime4				= (long) (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
    			hbTime5				= (long) (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
    			hbTime6				= (long) (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
    			hbTime7				= (long) (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
    			hbTime8				= (long) (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
    			hbTime9				= (long) (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
    			hbTime10			= (long) (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
    			hbTime11			= (long) (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
    			hbTime12			= (long) (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
    			hbTime13			= (long) (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
    			hbTime14			= (long) (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
    			hbTime15			= (long) (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
    			reserved1			= (long) (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
    			reserved2			= (long) (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
    			reserved3			= (long) (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
    			distance			= (long) (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
    			speed				= (long) (int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
    			strides 			= buffer[bufferIndex++];    
    			reserved4 			= buffer[bufferIndex++];
    			reserved5 			= (long)(int)((0x000000FF & (int)buffer[bufferIndex++]) | (int)(0x000000FF & (int)buffer[bufferIndex++])<< 8);
    			crc 				= buffer[bufferIndex++];
    			etx 				= buffer[bufferIndex];
    		} catch (Exception e) {
    			/*
    			 * An exception should only happen if the buffer is too short and we walk off the end of the bytes,
    			 * because of the way we read the bytes from the device this should never happen, but just in case
    			 * we'll catch the exception
    			 */
    	        Log.d(TAG, "Failure building HrmReading from byte buffer, probably an incopmplete or corrupted buffer");
    		}
                
    		
            Log.d(TAG, "Building HrmReading from byte buffer complete, consumed " + bufferIndex + " bytes in the process");
            
            /*
             * One simple check to see if we parsed the bytes properly is to check if the ETX 
             * character was found where we expected it,  a more robust implementation would be
             * to calculate the CRC from the message contents and compare it to the CRC from 
             * the packet.  
             */
            if ( etx != ETX )
            	 Log.e(TAG,"...ETX mismatch!  The HxM message was not parsed properly");
            
            /*
             * log the contents of the HrmReading, use logcat to watch the data as it arrives  
             */
            dump();
            }

        
        public void writeToDatabase() {
        	SQLiteReaderWriter db = null;
     		
	    	Contract contract= new Heartratecontract();
 		
	 		if(contract!=null){
	 			db=new SQLiteReaderWriter(instance.getBaseContext(), contract);
	 			
	 			ContentValues values=new ContentValues();
	 			values.put(Heartratecontract.COLUMN_TIME, System.currentTimeMillis());
	 			values.put(Heartratecontract.COLUMN_DISTANCE, distance);
	 			values.put(Heartratecontract.COLUMN_HEART_BEAT_NUMBER, (int)heartBeatNumber);
	 			values.put(Heartratecontract.COLUMN_SPEED, speed);
	 			values.put(Heartratecontract.COLUMN_STRIDES, (int)strides);
	 			values.put(Heartratecontract.COLUMN_HEART_RATE, (int)heartRate);
				System.out.println(values.toString());
				db.writeToDatabase(values);
			}
        }

			/*
         * Display the HRM reading into the layout     
         */
            private void displayRaw() {  	  
            	display ( R.id.batteryChargeIndicator,  (int)batteryIndicator );
            	display ( R.id.heartRate, (int)heartRate );
            	display ( R.id.heartBeatNumber,  (int)heartBeatNumber );
            	display ( R.id.distance,   distance );
            	display ( R.id.speed,   speed );
            	display ( R.id.strides,  (int)strides );
            }    
        
        /*
         * dump() sends the contents of the HrmReading object to the log, use 'logcat' to view
         */    
        public void dump() {
        		Log.d(TAG,"HrmReading Dump");
        		Log.d(TAG,"...serial "+ ( serial ));
        		Log.d(TAG,"...stx "+ ( stx ));
        		Log.d(TAG,"...msgId "+( msgId ));
        		Log.d(TAG,"...dlc "+ ( dlc ));
        		Log.d(TAG,"...firmwareId "+ ( firmwareId ));
        		Log.d(TAG,"...sfirmwareVersiontx "+ (  firmwareVersion ));
        		Log.d(TAG,"...hardWareId "+ (  hardWareId ));
        		Log.d(TAG,"...hardwareVersion "+ (  hardwareVersion ));
        		Log.d(TAG,"...batteryIndicator "+ ( batteryIndicator ));
        		Log.d(TAG,"...heartRate "+ ( heartRate ));
        		Log.d(TAG,"...heartBeatNumber "+ ( heartBeatNumber ));
        		Log.d(TAG,"...shbTime1tx "+ (  hbTime1 ));
        		Log.d(TAG,"...hbTime2 "+ (  hbTime2 ));
        		Log.d(TAG,"...hbTime3 "+ (  hbTime3 ));
        		Log.d(TAG,"...hbTime4 "+ (  hbTime4 ));
        		Log.d(TAG,"...hbTime4 "+ (  hbTime5 ));
        		Log.d(TAG,"...hbTime6 "+ (  hbTime6 ));
        		Log.d(TAG,"...hbTime7 "+ (  hbTime7 ));
        		Log.d(TAG,"...hbTime8 "+ (  hbTime8 ));
        		Log.d(TAG,"...hbTime9 "+ (  hbTime9 ));
        		Log.d(TAG,"...hbTime10 "+ (  hbTime10 ));
        		Log.d(TAG,"...hbTime11 "+ (  hbTime11 ));
        		Log.d(TAG,"...hbTime12 "+ (  hbTime12 ));
        		Log.d(TAG,"...hbTime13 "+ (  hbTime13 ));
        		Log.d(TAG,"...hbTime14 "+ (  hbTime14 ));
        		Log.d(TAG,"...hbTime15 "+ (  hbTime15 ));
        		Log.d(TAG,"...reserved1 "+ (  reserved1 ));
        		Log.d(TAG,"...reserved2 "+ (  reserved2 ));
        		Log.d(TAG,"...reserved3 "+ (  reserved3 ));
        		Log.d(TAG,"...distance "+ (  distance ));
        		Log.d(TAG,"...speed "+ (  speed ));
        		Log.d(TAG,"...strides "+ ( strides ));
        		Log.d(TAG,"...reserved4 "+ ( reserved4 ));
        		Log.d(TAG,"...reserved5 "+ ( reserved5 ));
        		Log.d(TAG,"...crc "+ ( crc ));
        		Log.d(TAG,"...etx "+ ( etx ));    	    	    	
        }    

        
        
/****************************************************************************
 * Some utility functions to control the formatting of HxM fields into the 
 * activity's view
 ****************************************************************************/
        
        
        /*
         * display a byte value
         */
    	@SuppressWarnings("unused")
		private void display  ( int nField, byte d ) {   
    		String INT_FORMAT = "%x";
    		
    		String s = String.format(INT_FORMAT, d);

    		display( nField, s  );
    	}

    	/*
    	 * display an integer value
    	 */
    	private void display  ( int nField, int d ) {   
    		String INT_FORMAT = "%d";
    		
    		String s = String.format(INT_FORMAT, d);

    		display( nField, s  );
    	}

    	/*
    	 * display a long integer value
    	 */
    	private void display  ( int nField, long d ) {   
    		String INT_FORMAT = "%d";
    		
    		String s = String.format(INT_FORMAT, d);

    		display( nField, s  );
    	}

    	/*
    	 * display a character string
    	 */
    	private void display ( int nField, CharSequence  str  ) {
        	TextView tvw = (TextView) instance.findViewById(nField);
        	if ( tvw != null )
        		tvw.setText(str);
        }
    }    	   
}