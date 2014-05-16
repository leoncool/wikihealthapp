package uk.ac.imperial.wikihealth.Database.Contracts;


import android.provider.BaseColumns;

public class OrientationContract implements BaseColumns,Contract {
    public static final String TABLE_NAME = "orientation";
    public static final String COLUMN_ID = "entryid";
    public static final String COLUMN_VALUEX = "value_x";
    public static final String COLUMN_VALUEY = "value_y";
    public static final String COLUMN_VALUEZ = "value_z";
    public static final String COLUMN_TIME = "timestamp";

    
    public static final String DATABASE_CREATE = "create table "
    	      + OrientationContract.TABLE_NAME + "(" + OrientationContract.COLUMN_ID
    	      + " INTEGER primary key autoincrement, " + OrientationContract.COLUMN_TIME
    	      + " TEXT, "+ OrientationContract.COLUMN_VALUEX
    	      + " REAL, "+ OrientationContract.COLUMN_VALUEY
    	      + " REAL, "+ OrientationContract.COLUMN_VALUEZ
    	      + " REAL);";
    
    public static final  String[] projection = {
    	OrientationContract.COLUMN_ID,
    	OrientationContract.COLUMN_TIME,
    	OrientationContract.COLUMN_VALUEX,
    	OrientationContract.COLUMN_VALUEY,
    	OrientationContract.COLUMN_VALUEZ,
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