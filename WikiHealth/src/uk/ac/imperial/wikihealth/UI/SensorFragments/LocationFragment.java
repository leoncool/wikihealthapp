package uk.ac.imperial.wikihealth.UI.SensorFragments;

import uk.ac.imperial.wikihealth.R;
import uk.ac.imperial.wikihealth.Database.Contracts.Contract;
import uk.ac.imperial.wikihealth.Database.Contracts.GPScontract;
import uk.ac.imperial.wikihealth.Database.Contracts.LightContract;
import uk.ac.imperial.wikihealth.Utils.PreferenceUtils;
import android.content.Context;
import android.hardware.Sensor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class LocationFragment extends Fragment{
    private View mView;
    private TextView valuex,valuey,valuez,labelx,labely;
    
	private TextView mDescription;
    private CheckBox mCheck;
    private RadioButton mPeriodRadioButton,mChangeRadioButton;
    private EditText mPeriodText;
    private Button mSave;
    public static Bundle createBundle( String title ) {
        Bundle bundle = new Bundle();
        return bundle;
    }
 

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			
			mView= inflater.inflate(R.layout.location_sensor_layout, container, false);

			 valuex=(TextView)mView.findViewById(R.id.valuex);
			 valuey=(TextView)mView.findViewById(R.id.valuey);
			 labelx=(TextView)mView.findViewById(R.id.labelx);
			 labely=(TextView)mView.findViewById(R.id.labely);
			 
			 mPeriodRadioButton=(RadioButton)mView.findViewById(R.id.periodRadio);
			 mChangeRadioButton=(RadioButton)mView.findViewById(R.id.locationchangeRadio);
			 
			 boolean periodic=PreferenceUtils.isPeriodic(mView.getContext(), new GPScontract());
			 
			 mPeriodRadioButton.setChecked(periodic);
			 mChangeRadioButton.setChecked(!periodic);
			 
			 mPeriodRadioButton.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
							mPeriodRadioButton.setChecked(true);
							mChangeRadioButton.setChecked(false);
							
							PreferenceUtils.updateLocationMonitoringPreference(mView.getContext(),true);
					}
				});
			 
			 mChangeRadioButton.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
	
							mPeriodRadioButton.setChecked(false);
							mChangeRadioButton.setChecked(true);
							PreferenceUtils.updateLocationMonitoringPreference(mView.getContext(),false);
						
					}
				});
			 
			 mDescription=(TextView)mView.findViewById(R.id.sensorsDescription);
			 mDescription.setText("GPS receiver provides the longitude and the latitude of the current location of the device");
			 labelx.setText("Latitude:");
			 labely.setText("Longitude:");
			init_listener(mView.getContext());
			 ((TextView)mView.findViewById(R.id.title)).setText("Coordinates");
			 
				mCheck=(CheckBox)mView.findViewById(R.id.sensorCheck);
				
				final Contract c=new GPScontract();
				mCheck.setChecked(PreferenceUtils.isChecked(mView.getContext(), c));
				mCheck.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(mCheck.isChecked()){
							LightContract.sMonitor=true;
							PreferenceUtils.updateMonitoringPreference(mView.getContext(),c, true);

						}
						else{
							LightContract.sMonitor=false;
							PreferenceUtils.updateMonitoringPreference(mView.getContext(),c, false);
						}
					}
				});
				mPeriodText=(EditText)mView.findViewById(R.id.sensorsPeriod);
				mPeriodText.setText(String.valueOf(PreferenceUtils.getPeriod(mView.getContext(), c)));
				 mSave=(Button)mView.findViewById(R.id.sensorsSave);
				 
				 mSave.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							if(mPeriodText.getText()==null || mPeriodText.getText().toString().equals("")||  (mPeriodText.getText().toString().length()>6) || Integer.parseInt(mPeriodText.getText().toString())<1000 ){
									Toast.makeText(getActivity(), "Invalid period value",Toast.LENGTH_LONG).show();
							}
							else{
								LightContract.sPeriod=Integer.parseInt(mPeriodText.getText().toString());
								PreferenceUtils.updatePeriodPreference(mView.getContext(),c,Integer.parseInt(mPeriodText.getText().toString()));

								Toast.makeText(getActivity(), "New period value updated",Toast.LENGTH_LONG).show();
							}
							
						}
					});
				 
		    return mView;
		}


		Context mContext;
		private static LocationManager mLocationManager;
		/**
		 * location listener which stores the location in the remote database if changed
		 */
		LocationListener mLocationListener = new LocationListener() {
		    public void onLocationChanged(final Location location) {


				new Thread(new Runnable() {
			        public void run() {
			 		
			        	getActivity().runOnUiThread(new Runnable() {
	                        @Override
	                        public void run() {
	                        	valuex.setText(String.valueOf(location.getLatitude()));
								valuey.setText(String.valueOf(location.getLongitude()));
	                        }
		        	 });
							
	 		
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
		public void init_listener(Context _context){
			try{
			mContext=_context;
			mLocationManager= (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
			Criteria crit = new Criteria();
			crit.setAccuracy(Criteria.ACCURACY_FINE);
			mLocationManager.requestLocationUpdates(mLocationManager.getBestProvider(crit, false), 0, 1, mLocationListener);
			mLocationManager.requestLocationUpdates(mLocationManager.NETWORK_PROVIDER, 0, 1, mLocationListener);
			mLocationManager.requestLocationUpdates(mLocationManager.GPS_PROVIDER, 0, 1, mLocationListener);
			//mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 0, mLocationListener);
			System.out.println("fragments Location Listener succesfully initialized");
			}catch(Exception ex)
			{
				ex.printStackTrace();
				Log.e("sensor missing locations", "network, gps, or others");
			}
			}
	
		@Override
		public void onDestroy(){
			mLocationManager.removeUpdates(mLocationListener);
	        super.onDestroy();
		}
}
