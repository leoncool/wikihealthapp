package uk.ac.imperial.wikihealth.Database.Contracts;


import android.provider.BaseColumns;

public class ProximityContract implements BaseColumns,Contract {
    public static final String TABLE_NAME = "proximity";
    public static final String COLUMN_ID = "entryid";
    public static final String COLUMN_VALUE = "value";
    public static final String COLUMN_TIME = "timestamp";

    public static final String DATABASE_CREATE = "create table "
  	      + ProximityContract.TABLE_NAME + "(" + ProximityContract.COLUMN_ID
  	      + " INTEGER primary key autoincrement, " + ProximityContract.COLUMN_TIME
  	      + " TEXT, "+ ProximityContract.COLUMN_VALUE
  	      + " REAL);";
    
    public static final  String[] projection = {
    	ProximityContract.COLUMN_ID,
    	ProximityContract.COLUMN_TIME,
    	ProximityContract.COLUMN_VALUE
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