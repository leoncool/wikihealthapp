package uk.ac.imperial.wikihealth.Database.Contracts;


import android.provider.BaseColumns;

public  class MagneticContract implements BaseColumns,Contract {
    public static final String TABLE_NAME = "magnetic";
    public static final String COLUMN_ID = "entryid";
    public static final String COLUMN_VALUEX = "value_x";
    public static final String COLUMN_VALUEY = "value_y";
    public static final String COLUMN_VALUEZ = "value_z";
    public static final String COLUMN_TIME = "timestamp";

    
    public static final String DATABASE_CREATE = "create table "
    	      + MagneticContract.TABLE_NAME + "(" + MagneticContract.COLUMN_ID
    	      + " INTEGER primary key autoincrement, " + MagneticContract.COLUMN_TIME
    	      + " TEXT, "+ MagneticContract.COLUMN_VALUEX
    	      + " REAL, "+ MagneticContract.COLUMN_VALUEY
    	      + " REAL, "+ MagneticContract.COLUMN_VALUEZ
    	      + " REAL);";
    
    public static final  String[] projection = {
    	MagneticContract.COLUMN_ID,
    	MagneticContract.COLUMN_TIME,
    	MagneticContract.COLUMN_VALUEX,
    	MagneticContract.COLUMN_VALUEY,
    	MagneticContract.COLUMN_VALUEZ,
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