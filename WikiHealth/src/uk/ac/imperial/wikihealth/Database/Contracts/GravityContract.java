package uk.ac.imperial.wikihealth.Database.Contracts;


import android.provider.BaseColumns;

public class GravityContract implements BaseColumns,Contract {
    public static final String TABLE_NAME = "gravity";
    public static final String COLUMN_ID = "entryid";
    public static final String COLUMN_VALUEX = "value_x";
    public static final String COLUMN_VALUEY = "value_y";
    public static final String COLUMN_VALUEZ = "value_z";
    public static final String COLUMN_TIME = "timestamp";

    public static final String DATABASE_CREATE = "create table "
    	      + GravityContract.TABLE_NAME + "(" + GravityContract.COLUMN_ID
    	      + " INTEGER primary key autoincrement, " + GravityContract.COLUMN_TIME
    	      + " TEXT, "+ GravityContract.COLUMN_VALUEX
    	      + " REAL, "+ GravityContract.COLUMN_VALUEY
    	      + " REAL, "+ GravityContract.COLUMN_VALUEZ
    	      + " REAL);";
    
    public static final  String[] projection = {
    	GravityContract.COLUMN_ID,
    	GravityContract.COLUMN_TIME,
    	GravityContract.COLUMN_VALUEX,
    	GravityContract.COLUMN_VALUEY,
    	GravityContract.COLUMN_VALUEZ
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