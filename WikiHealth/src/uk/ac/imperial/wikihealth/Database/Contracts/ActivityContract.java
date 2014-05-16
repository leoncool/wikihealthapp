package uk.ac.imperial.wikihealth.Database.Contracts;

import android.provider.BaseColumns;

public class ActivityContract implements BaseColumns,Contract {
    public static final String TABLE_NAME = "activities";
    public static final String COLUMN_ID = "entryid";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_FROM_TIME = "from_time";
    public static final String COLUMN_TO_TIME = "to_time";
    public static final String COLUMN_ACTIVITY = "activity";
    public static final String COLUMN_TIME = "timestamp";

    public static final String DATABASE_CREATE = "create table "
  	      + ActivityContract.TABLE_NAME + "(" + ActivityContract.COLUMN_ID
  	      + " INTEGER primary key autoincrement, " + ActivityContract.COLUMN_TIME
  	      + " TEXT, "+ ActivityContract.COLUMN_DATE
  	      + " STRING, "+ ActivityContract.COLUMN_FROM_TIME
  	      + " LONG, "+ ActivityContract.COLUMN_TO_TIME
  	      + " LONG, "+ ActivityContract.COLUMN_ACTIVITY
  	      + " INTEGER);";
    

    public static final  String[] projection = {
    	ActivityContract.COLUMN_ID,
    	ActivityContract.COLUMN_TIME,
    	ActivityContract.COLUMN_DATE,
    	ActivityContract.COLUMN_FROM_TIME,
    	ActivityContract.COLUMN_TO_TIME,
    	ActivityContract.COLUMN_ACTIVITY,
	    };
    public static final  String[] events = {
    	ActivityContract.COLUMN_FROM_TIME,
    	ActivityContract.COLUMN_TO_TIME,
    	ActivityContract.COLUMN_ACTIVITY,
	    };
	public static  boolean sMonitor=true;
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
    
    public String [] getEvents(){
    	return events;
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