package uk.ac.imperial.wikihealth.SensorFramework;

import java.util.ArrayList;
import java.util.Calendar;

import uk.ac.imperial.wikihealth.Database.Contracts.AccelerometerContract;
import uk.ac.imperial.wikihealth.Database.Contracts.Contract;
import uk.ac.imperial.wikihealth.Database.Contracts.GravityContract;
import uk.ac.imperial.wikihealth.Database.Contracts.GyroscopeContract;
import uk.ac.imperial.wikihealth.Database.Contracts.HumidityContract;
import uk.ac.imperial.wikihealth.Database.Contracts.LightContract;
import uk.ac.imperial.wikihealth.Database.Contracts.MagneticContract;
import uk.ac.imperial.wikihealth.Database.Contracts.OrientationContract;
import uk.ac.imperial.wikihealth.Database.Contracts.PressureContract;
import uk.ac.imperial.wikihealth.Database.Contracts.ProximityContract;
import uk.ac.imperial.wikihealth.Database.Contracts.RotationContract;
import uk.ac.imperial.wikihealth.Database.Contracts.TemperatureContract;
import uk.ac.imperial.wikihealth.UI.SensorFragments.AccelerometerFragment;
import uk.ac.imperial.wikihealth.UI.SensorFragments.GravityFragment;
import uk.ac.imperial.wikihealth.UI.SensorFragments.GyroscopeFragment;
import uk.ac.imperial.wikihealth.UI.SensorFragments.LightFragment;
import uk.ac.imperial.wikihealth.UI.SensorFragments.MagneticFragment;
import uk.ac.imperial.wikihealth.UI.SensorFragments.OrientationFragment;
import uk.ac.imperial.wikihealth.UI.SensorFragments.PressureFragment;
import uk.ac.imperial.wikihealth.UI.SensorFragments.ProximityFragment;
import uk.ac.imperial.wikihealth.UI.SensorFragments.RotationFragment;
import uk.ac.imperial.wikihealth.UI.SensorFragments.TemperatureFragment;
import android.content.ContentValues;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;



/**
 * Provides utilities for available sensors in various object types
 * @author Panayiotis Kritiotis (pkritiotis@gmail.com)
 *
 */
public class SensorUtilities {
	
	// Variables to distinguish between sensors
	// ID mapping acquired for android documentation
	private final static int ACCELEROMETER =1;
	private final static int MAGNETIC =2;
	private final static int ORIENTATION =3;
	private final static int GYROSCOPE =4;
	private final static int LIGHT =5;
	private final static int PRESSURE =6;
	private final static int TEMPERATURE =7;
	private final static int PROXIMITY =8;
	private final static int GRAVITY =9;
	private final static int LINEAR_ACCELEROMETER =10;
	private final static int ROTATION =11;
	private final static int HUMIDITY = 12;
	
	//Sensor Manager which provides information about the available sensors
	private SensorManager mSensorManager;
	
	//Lists containing sensor/string objects of the sensors
	private static ArrayList<Sensor> mSensorList =null;
	private ArrayList<String> mSensorNames=null;
	
	/**
	 * Constructor initializing sensors lists 
	 * @param activity
	 */
	public SensorUtilities(Context activity){

		mSensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
		mSensorList =new ArrayList<Sensor>( mSensorManager.getSensorList(Sensor.TYPE_ALL));
		mapSensorTypes();
	}
	
	/**
	 * Maps  Available sensor types to their Human Readable names
	 */
	private void mapSensorTypes(){
		int [] types = new int[mSensorList.size()];
		
		mSensorNames=new ArrayList<String>();
		for(int i=0;i<mSensorList.size();i++){
			if(types[i]==0){
				types[i]++;
				switch(mSensorList.get(i).getType()){
					case ACCELEROMETER:	mSensorNames.add("Accelerometer");
										break;
					case MAGNETIC:	mSensorNames.add("Magnetic");
										break;
					case ORIENTATION:	mSensorNames.add("Orientation");
										break;
					case GYROSCOPE:	mSensorNames.add("Gyroscope");
										break;
					case LIGHT:	mSensorNames.add("Light");
										break;
					case PRESSURE:	mSensorNames.add("Pressure");
										break;
					case HUMIDITY:	mSensorNames.add("Humidity");
										break;					
					case TEMPERATURE:	mSensorNames.add("Temperature");
										break;
					case PROXIMITY:	mSensorNames.add("Proximity");
										break;
					case GRAVITY:	mSensorNames.add("Gravity");
										break;
					case LINEAR_ACCELEROMETER:	mSensorList.remove(i--);
										break;
					case ROTATION:	mSensorNames.add("Rotation");
										break;
					default: mSensorList.remove(i--);
							break;
				}
			}
			else
				mSensorList.remove(i--);
		}
		
		Log.e("sensorlist fetched", mSensorNames.toString());
		
	}
	
	/**
	 * Returns Sensor Names
	 * @return
	 */
	public ArrayList <String> getAvailableSensorNames(){
		return(mSensorNames);
	}
	
	/**
	 * Returns Sensor Objects
	 * @return
	 */
	public ArrayList <Sensor> getAvailableSensors(){
		return(mSensorList);
	}
	
	/**
	 * Returns the contract of the given sensor
	 * @param sensor
	 * @return
	 */
	public static Contract getContract(Sensor sensor){
		
		Contract contract = null;
		switch(sensor.getType()){
		
		case ACCELEROMETER:	contract=new AccelerometerContract();
							break;
		case MAGNETIC:	contract=new MagneticContract();
							break;
		case ORIENTATION:	contract=new OrientationContract();
							break;
		case GYROSCOPE:	contract=new GyroscopeContract();
							break;
		case LIGHT:	contract=new LightContract();
							break;
		case PRESSURE:	contract=new PressureContract();
							break;
		case TEMPERATURE:	contract=new TemperatureContract();
							break;
		case PROXIMITY:	contract= new ProximityContract();
							break;
		case GRAVITY:	contract=new GravityContract();
							break;
		case ROTATION:	contract=new RotationContract();
							break;
		case HUMIDITY:	contract=new HumidityContract();
				break;
	}
		
		return contract;
		
	}

	/**
	 * Returns the class of the given sensor type
	 * @param sensor
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Class getClassOfSensor(Sensor sensor) {
		
		switch(sensor.getType()){
			case ACCELEROMETER:	return AccelerometerFragment.class;
			case MAGNETIC:	return MagneticFragment.class;
			case ORIENTATION:	return OrientationFragment.class;
			case GYROSCOPE:	return GyroscopeFragment.class;
			case LIGHT:	return LightFragment.class;
			case PRESSURE:	return PressureFragment.class;
			case TEMPERATURE:	return TemperatureFragment.class;
			case PROXIMITY:	return ProximityFragment.class;
			case GRAVITY:	return GravityFragment.class;
			case ROTATION:	return RotationFragment.class;
		}
		return null;
	}
	
	/**
	 * Class to help return the column name of the sensor
	 * @param value_index
	 * @param size
	 * @return
	 */
	private static String getSensorColumnName(int value_index, int size) {
		if(size>=3){
			if(value_index==0)
				return AccelerometerContract.COLUMN_VALUEX;
			else if(value_index==1)
				return AccelerometerContract.COLUMN_VALUEY;
			else if(value_index==2) return AccelerometerContract.COLUMN_VALUEZ;
			else return null;
		}
		
		else
			return TemperatureContract.COLUMN_VALUE;
	}
		
	/**
	 * Returns the values according to the type of sensor
	 * Each event returned from event listener contains 3 values but depending on the sensor some may be null
	 * @param input_values
	 * @param type
	 * @return
	 */
	public static ContentValues getSensorValues( float[] input_values, int type) {
		
		int length=0;
		ContentValues values=new ContentValues();
		
		values.put(AccelerometerContract.COLUMN_TIME, Calendar.getInstance().getTimeInMillis());
		switch(type){
			case ACCELEROMETER:	length=3;
								break;
			case MAGNETIC:		length=3;
								break;
			case ORIENTATION:	length=3;
								break;
			case GYROSCOPE:		length=3;
								break;
			case LIGHT:			length=1;
								break;
			case PRESSURE:		length=1;
								break;
			case TEMPERATURE:			length=1;
								break;
			case PROXIMITY:		length=1;
								break;
			case GRAVITY:		length=3;
								break;
			case LINEAR_ACCELEROMETER:	length=3;
								break;
			case ROTATION:		length=3;
								break;
			case HUMIDITY:		length=1;
								break;		
	}		
			for(int i=0;i<length;i++)
				values.put(getSensorColumnName(i, length), input_values[i]);
			System.out.println(input_values+" type: "+type);
			return values;
	}

	public static  Sensor getObject(int object){
		return mSensorList.get(object);
	}
}
