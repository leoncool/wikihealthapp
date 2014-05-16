package uk.ac.imperial.wikihealth.Database.Contracts;

import android.provider.BaseColumns;

public class Heartratecontract implements BaseColumns,Contract {
    public static final String TABLE_NAME = "heartrate";
    public static final String COLUMN_ID = "entryid";
    public static final String COLUMN_HEART_RATE = "heartrate";
    public static final String COLUMN_SPEED = "speed";
    public static final String COLUMN_DISTANCE = "distance";
    public static final String COLUMN_HEART_BEAT_NUMBER = "heartbeatnumber";
    public static final String COLUMN_STRIDES = "strides";
    
    public static final String COLUMN_TIME = "timestamp";

    public static final String DATABASE_CREATE = "create table "
  	      + Heartratecontract.TABLE_NAME + "(" + Heartratecontract.COLUMN_ID
  	      + " INTEGER primary key autoincrement, " + Heartratecontract.COLUMN_TIME
  	      + " TEXT, "+ Heartratecontract.COLUMN_HEART_RATE
  	      + " INT, "+ Heartratecontract.COLUMN_SPEED
  	      + " LONG, "+ Heartratecontract.COLUMN_DISTANCE
  	      + " LONG, "+ Heartratecontract.COLUMN_HEART_BEAT_NUMBER
  	      + " INTEGER, "+ Heartratecontract.COLUMN_STRIDES
  	      + " INT);";
    
    public static final  String[] projection = {
    	Heartratecontract.COLUMN_ID,
    	Heartratecontract.COLUMN_TIME,
    	Heartratecontract.COLUMN_HEART_RATE,
    	Heartratecontract.COLUMN_HEART_BEAT_NUMBER,
    	Heartratecontract.COLUMN_SPEED,
    	Heartratecontract.COLUMN_DISTANCE,
    	Heartratecontract.COLUMN_STRIDES
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