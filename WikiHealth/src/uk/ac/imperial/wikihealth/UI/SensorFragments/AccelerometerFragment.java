package uk.ac.imperial.wikihealth.UI.SensorFragments;

import java.text.DecimalFormat;

import uk.ac.imperial.wikihealth.R;
import uk.ac.imperial.wikihealth.Database.Contracts.AccelerometerContract;
import uk.ac.imperial.wikihealth.Database.Contracts.Contract;
import uk.ac.imperial.wikihealth.Utils.PreferenceUtils;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Fragment containing data about the Accelerometer measurements
 * It also implements a listener to show the data of the sensors when they are changed
 * @author Panayiotis Kritiotis (pkritiotis@gmail.com)
 *
 */
public class AccelerometerFragment extends Fragment implements SensorEventListener {
		private SensorManager mSensorManager;
		private Sensor mAccelerometer;
	    private View mView;
	    private TextView mValueXtext,mValueYtext,mValueZtext;
	    private TextView mDescription;
	    private CheckBox mCheck;
	    private EditText mPeriodText;
	    private Button mSave;
	    /**
	     * Called when activity is first created
	     */
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			
			//Show the new view of the fragment
			mView= inflater.inflate(R.layout.general_sensor_layout, container, false);

			//Initialize UI variables
			intializeUiElements();

		    return mView;
		}

		/**
		 * initializes UI elements
		 */
		private void intializeUiElements() {
			mSensorManager = (SensorManager) this.getActivity().getSystemService(Context.SENSOR_SERVICE);
		    mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			mSensorManager.registerListener(this,mAccelerometer, Integer.MAX_VALUE);
			
			mValueXtext=(TextView)mView.findViewById(R.id.valuex);
			mValueYtext=(TextView)mView.findViewById(R.id.valuey);
			mValueZtext=(TextView)mView.findViewById(R.id.valuez);
			
			
			((TextView)mView.findViewById(R.id.title)).setText("Acceleration");
			((TextView)mView.findViewById(R.id.labelx)).setText("X-axis");
			((TextView)mView.findViewById(R.id.labely)).setText("Y-axis");
			((TextView)mView.findViewById(R.id.labelz)).setText("Z-axis");			
			
			
			
			mDescription=(TextView)mView.findViewById(R.id.sensorsDescription);
			mDescription.setText(getResources().getText(R.string.accelerometer_description));
			mCheck=(CheckBox)mView.findViewById(R.id.sensorCheck);
			
			final Contract c=new AccelerometerContract();
			mCheck.setChecked(PreferenceUtils.isChecked(mView.getContext(), c));
			mCheck.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(mCheck.isChecked()){
						AccelerometerContract.sMonitor=true;
						PreferenceUtils.updateMonitoringPreference(mView.getContext(),c, true);

					}
					else{
						AccelerometerContract.sMonitor=false;
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
								Toast.makeText(mView.getContext(), "Invalid period value",Toast.LENGTH_LONG).show();
								//mPeriodText.setText("5000");
						}
						else{
							AccelerometerContract.sPeriod=Integer.parseInt(mPeriodText.getText().toString());
							PreferenceUtils.updatePeriodPreference(mView.getContext(),c,Integer.parseInt(mPeriodText.getText().toString()));
							Toast.makeText(getActivity(), "New period value updated",Toast.LENGTH_LONG).show();
						}
						
					}
				});
		}
		
		
		/**
		 * Called when the activity is created
		 * Commits the transaction for the fragment to be shown on the screen
		 */
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
		    super.onActivityCreated(savedInstanceState);
		    FragmentManager fm = getFragmentManager();
		    fm.beginTransaction().commit();
		}

		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			
		}


		@Override
		public void onSensorChanged(SensorEvent event) {
			mValueXtext.setText(String.valueOf(new DecimalFormat("#.##").format(event.values[0])));
			mValueYtext.setText(String.valueOf(new DecimalFormat("#.##").format(event.values[1])));
			mValueZtext.setText(String.valueOf(new DecimalFormat("#.##").format(event.values[2])));

				
		}
		
		@Override
		public void onDestroy(){
			System.out.println("Destroyed");
			mSensorManager.unregisterListener(this);
	        super.onDestroy();

		}
	
	
}
