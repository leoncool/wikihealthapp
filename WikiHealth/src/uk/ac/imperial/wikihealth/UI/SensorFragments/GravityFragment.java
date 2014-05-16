package uk.ac.imperial.wikihealth.UI.SensorFragments;

import java.text.DecimalFormat;

import uk.ac.imperial.wikihealth.R;
import uk.ac.imperial.wikihealth.Database.Contracts.GravityContract;
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
  * Fragment for gravity sensor which displays real time measurements of the gravity sensor of the device
  * @author Panayiotis Kritiotis (pkritiotis@gmail.com)
  *
  */
public class GravityFragment extends Fragment implements SensorEventListener {
	private SensorManager mSensorManager;
	private Sensor mGravity;
    private View mView;
    private TextView mValueXtext;
	private TextView mDescription;
    private CheckBox mCheck;
    private EditText mPeriodText;
    private Button mSave;
    	/**
    	 * Called when the view is created
    	 */
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			
			//Show the new view of the fragment
			mView= inflater.inflate(R.layout.general_sensor_layout, container, false);
			//InitializeViews
			initializeUIelements();
			
		    return mView;
		}

		/**
		 * Initializes the UI elements
		 */
		private void initializeUIelements() {
			mSensorManager = (SensorManager) this.getActivity().getSystemService(Context.SENSOR_SERVICE);
		    mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
			mSensorManager.registerListener(this,mGravity, Integer.MAX_VALUE);
			mValueXtext=(TextView)mView.findViewById(R.id.valuex);
			((TextView)mView.findViewById(R.id.title)).setText("Magnitude:");
			((TextView)mView.findViewById(R.id.labelx)).setText("");
			
			mDescription=(TextView)mView.findViewById(R.id.sensorsDescription);
			mDescription.setText(getResources().getText(R.string.gravity_description));
			mCheck=(CheckBox)mView.findViewById(R.id.sensorCheck);
			
			final Contract c=new GravityContract();
			mCheck.setChecked(PreferenceUtils.isChecked(mView.getContext(), c));
			mCheck.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(mCheck.isChecked()){
						GravityContract.sMonitor=true;
						PreferenceUtils.updateMonitoringPreference(mView.getContext(),c, true);

					}
					else{
						GravityContract.sMonitor=false;
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
							GravityContract.sPeriod=Integer.parseInt(mPeriodText.getText().toString());
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

		/**
		 * Called when an accuracy change event is detected
		 */
		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		/**
		 * called upon the appearance of a change in the measurments of the event
		 */
		@Override
		public void onSensorChanged(SensorEvent event) {
			mValueXtext.setText(String.valueOf(new DecimalFormat("#.##").format(event.values[0])));
		}
		@Override
		public void onDestroy(){
			mSensorManager.unregisterListener(this);
	        super.onDestroy();

		}
	
	
}
