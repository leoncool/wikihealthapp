package uk.ac.imperial.wikihealth.Database.Contracts;

import android.provider.BaseColumns;

public class GPScontract implements BaseColumns,Contract {
    public static final String TABLE_NAME = "location";
    public static final String COLUMN_ID = "entryid";
    public static final String COLUMN_LONGITUTDE = "latitude";
    public static final String COLUMN_LATITUDE = "longitude";
    public static final String COLUMN_TIME = "timestamp";

    public static boolean sMonitor=true;
    public static int sPeriod=5000;
    public static final String DATABASE_CREATE = "create table "
  	      + GPScontract.TABLE_NAME + "(" + GPScontract.COLUMN_ID
  	      + " INTEGER primary key autoincrement, " + GPScontract.COLUMN_TIME
  	      + " TEXT, "+ GPScontract.COLUMN_LATITUDE
  	      + " REAL, "+ GPScontract.COLUMN_LONGITUTDE
  	      + " REAL);";
    

    public static final  String[] projection = {
	    GPScontract.COLUMN_ID,
	    GPScontract.COLUMN_TIME,
	    GPScontract.COLUMN_LATITUDE,
	    GPScontract.COLUMN_LONGITUTDE
	    };
    
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