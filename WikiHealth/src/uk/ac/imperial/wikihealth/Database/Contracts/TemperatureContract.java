package uk.ac.imperial.wikihealth.Database.Contracts;


import android.provider.BaseColumns;

public class TemperatureContract implements BaseColumns,Contract {
    public static final String TABLE_NAME = "temperature";
    public static final String COLUMN_ID = "entryid";
    public static final String COLUMN_VALUE = "value";
    public static final String COLUMN_TIME = "timestamp";

    public static final String DATABASE_CREATE = "create table "
  	      + TemperatureContract.TABLE_NAME + "(" + TemperatureContract.COLUMN_ID
  	      + " INTEGER primary key autoincrement, " + TemperatureContract.COLUMN_TIME
  	      + " TEXT, "+ TemperatureContract.COLUMN_VALUE
  	      + " REAL);";
    
    public static final  String[] projection = {
    	TemperatureContract.COLUMN_ID,
    	TemperatureContract.COLUMN_TIME,
    	TemperatureContract.COLUMN_VALUE
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