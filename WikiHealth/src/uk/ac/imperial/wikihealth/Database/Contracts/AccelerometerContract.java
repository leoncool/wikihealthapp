package uk.ac.imperial.wikihealth.Database.Contracts;


import android.provider.BaseColumns;

/**
 * Database Template for Accelerometer Entries
 * 
 * @author Panayiotis Kritiotis (pkritiotis@gmail.com)
 *
 */
public class AccelerometerContract implements BaseColumns,Contract {
    public static final String TABLE_NAME = "accelerometer";
    public static final String COLUMN_ID = "entryid";
    public static final String COLUMN_VALUEX = "value_x";
    public static final String COLUMN_VALUEY = "value_y";
    public static final String COLUMN_VALUEZ = "value_z";
    public static final String COLUMN_TIME = "timestamp";
    
    public static final String DATABASE_CREATE = "create table "
  	      + AccelerometerContract.TABLE_NAME + "(" + AccelerometerContract.COLUMN_ID
  	      + " INTEGER primary key autoincrement, " + AccelerometerContract.COLUMN_TIME
  	      + " TEXT, "+ AccelerometerContract.COLUMN_VALUEX
  	      + " REAL, "+ AccelerometerContract.COLUMN_VALUEY
  	      + " REAL, "+ AccelerometerContract.COLUMN_VALUEZ
  	      + " REAL);";
    
    public static final  String[] projection = {
	    AccelerometerContract.COLUMN_ID,
	    AccelerometerContract.COLUMN_TIME,
	    AccelerometerContract.COLUMN_VALUEX,
	    AccelerometerContract.COLUMN_VALUEY,
	    AccelerometerContract.COLUMN_VALUEZ
	    };
	public static boolean sMonitor=true;
	public static int sPeriod=5000;
    
	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	@Override
	public String getDBCreate() {
		return DATABASE_CREATE;
	}

	@Override
	public String getNullColumn() {
		return COLUMN_ID;
	}

    public String [] getProjection(){
    	return projection;
    }

	@Override
	public boolean getPermission() {
		return sMonitor;
	}

	@Override
	public int getPeriod() {
		// TODO Auto-generated method stub
		return sPeriod;
	}
  

}