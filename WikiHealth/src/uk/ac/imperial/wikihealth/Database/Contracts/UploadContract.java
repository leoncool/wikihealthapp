package uk.ac.imperial.wikihealth.Database.Contracts;

import android.provider.BaseColumns;

public class UploadContract implements BaseColumns,Contract {
    public static final String TABLE_NAME = "uploads";
    public static final String COLUMN_ID = "entryid";
    public static final String COLUMN_TIME = "timestamp";

    public static final String DATABASE_CREATE = "create table "
  	      + UploadContract.TABLE_NAME + "(" + UploadContract.COLUMN_ID
  	      + " INTEGER primary key autoincrement, " + UploadContract.COLUMN_TIME
  	      + " TEXT);";
    
    public static final  String[] projection = {
    	UploadContract.COLUMN_ID,
    	UploadContract.COLUMN_TIME
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