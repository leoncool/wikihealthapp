package uk.ac.imperial.wikihealth.Database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import uk.ac.imperial.wikihealth.Database.Contracts.ActivityContract;
import uk.ac.imperial.wikihealth.Database.Contracts.Contract;
import uk.ac.imperial.wikihealth.Database.Contracts.UploadContract;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

/**
 * SQLite Util to perform read and write operations
 * 
 * @author Panayiotis Kritiotis (pkritiotis@gmail.com)
 *
 */
@SuppressLint("SimpleDateFormat")
public class SQLiteReaderWriter {

	private MySQLiteHelper mDbHelper=null;
	private Contract mContract;
	
	/**
	 * Constructor initializing fields
	 * @param context
	 * @param contract
	 */
	public SQLiteReaderWriter(Context context,Contract contract){
		mDbHelper=MySQLiteHelper.getHelper(context);
		this.mContract=contract;
	}
	
	/**
	 * Writes given Content Values in the database
	 * @param values
	 * @return
	 */
	public boolean writeToDatabase(ContentValues values){
		 // Gets the data repository in write mode
	    SQLiteDatabase db = mDbHelper.getWritableDatabase();

	    System.out.println(values.toString());
	    // Create a new map of values, where column names are the keys
	  	db.insert(mContract.getTableName(),mContract.getNullColumn(),values);
	  	 try {
	  		File sdCard = Environment.getExternalStorageDirectory();
	  		String folderPath=sdCard.getAbsoluteFile()+"/wiki-health/";
	  		File dir=new File(folderPath);
	  		if(!dir.exists())
	  		{
	  			dir.mkdirs();
	  		}
	  		File logFile=new File(folderPath+mContract.getTableName()+".txt");
	         if(!logFile.exists())
	        	 logFile.createNewFile();
	         FileWriter logWriter=new FileWriter(logFile,true);
	         logWriter.append(values+"\r\n");
	         logWriter.flush();
	         logWriter.close();
	        } 
	        catch (Exception e) 
	        {
	            e.printStackTrace();
	            
	        }
	  	
	    return true;
	}
	
	/**
	 * Returns all data that exist in the database for the specific table of the instance
	 * @return
	 */
	public ArrayList<HashMap<String, String>> readFromDatabase(){
		
		 // Gets the data repository in read mode
	    SQLiteDatabase db = mDbHelper.getReadableDatabase();
	    String[] projection= mContract.getProjection();
	    
	    Cursor cursor = db.query(
	    	    mContract.getTableName(),  // The table to query
	    	    projection,                               // The columns to return
	    	    null,                                // The columns for the WHERE clause
	    	    null,                            // The values for the WHERE clause
	    	    null,                                     // don't group the rows
	    	    null,                                     // don't filter by row groups
	    	    null                                 // The sort order
	    	    );
	    // Create a new map of values, where column names are the keys
	    cursor.moveToFirst();
        ArrayList< HashMap<String,String> > entries = new ArrayList<HashMap<String,String>>();
        
        System.out.println(mContract.getTableName()+" Records: "+cursor.getCount());
	    while (!cursor.isAfterLast()) {
	    	HashMap<String,String> values = new HashMap<String,String>();

	    	for(int i=0 ; i< cursor.getColumnCount();i++){
	    	
	    		if(cursor.getColumnName(i).equals("timestamp")){
	    			long milliSeconds= cursor.getLong(i);
	    			Calendar calendar = Calendar.getInstance();
	    			calendar.setTimeInMillis(milliSeconds);
	    			values.put(cursor.getColumnName(i),String.valueOf(milliSeconds));
	    		}
	    		else{
	    		values.put(cursor.getColumnName(i),cursor.getString(i));
	    		}
	    		
	    	}
	    	
	    	entries.add(values);
	    	cursor.moveToNext();
	      }
	    cursor.close();
	    db.close();
		return entries;
	    
	}
	
	/**
	 * Returns an array of strings containing the time and description of events that happened in the given date
	 * @param currentDate
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public String[] getActivityEventsFromDatabase(long currentDate) {
		 // Gets the data repository in read mode
	    SQLiteDatabase db = mDbHelper.getReadableDatabase();
	    String[] projection= mContract.getProjection();
	    Date current_Date= new Date(currentDate);
    	DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    	String [] dateFormatted = {formatter.format(current_Date)};
    	
	    Cursor cursor = db.query(
	    	    mContract.getTableName(),  // The table to query
	    	    projection,                               // The columns to return
	    	   ActivityContract.COLUMN_DATE+"=?",                                // The columns for the WHERE clause
	    	    dateFormatted,                            // The values for the WHERE clause
	    	    null,                                     // don't group the rows
	    	    null,                                     // don't filter by row groups
	    	    null                                 // The sort order
	    	    );
	    ArrayList< HashMap<String,String> > entries = new ArrayList<HashMap<String,String>>();
        
	    if(cursor.getCount()==0)
        	return null;
	    
	    // read data
        cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	    	HashMap<String,String> values = new HashMap<String,String>();
	    	for(int j=0 ; j< cursor.getColumnCount();j++){
	    		System.out.println(j);
	    		values.put(cursor.getColumnName(j),cursor.getString(j));
	    		
	    	}
	    	
	    	entries.add(values);
	    	cursor.moveToNext();
	      }
	    cursor.close();
	    db.close();
	    
	    //format and store data in a sting array
		int r=0;
	    String [] results = new String[cursor.getCount()];
	    for(HashMap<String,String> h : entries){
	    	Date from= new Date(Long.valueOf(h.get(ActivityContract.COLUMN_FROM_TIME)));
	    	Date to=new Date(Long.valueOf(h.get(ActivityContract.COLUMN_TO_TIME)));
	    	String prefix_from="";
	    	String prefix_to="";

	    	if(from.getMinutes()<10)
	    		prefix_from="0";
	    	if(to.getMinutes()<10)
	    		prefix_to="0";
	    	results[r++]=String.valueOf(from.getHours())+":"+prefix_from+String.valueOf(from.getMinutes())+"-"+String.valueOf(to.getHours())+":"+prefix_to+String.valueOf(to.getMinutes())+" : "+h.get(ActivityContract.COLUMN_ACTIVITY);
	    }
	    
		return results;
	}
	
	
	/**
	 * Returns an array of strings containing the formatted strings(date and time) of all uploads 
	 * @return
	 */
	public String[] getUploadListFromDatabase() {
		 // Gets the data repository in read mode
	    SQLiteDatabase db = mDbHelper.getReadableDatabase();
	    Log.i("sqlite ", "readable db fetched");
	    //System.out.println("in database: "+mContract.getTableName());
	    String[] projection= mContract.getProjection();
	   
	    Cursor cursor = db.query(
	    	    mContract.getTableName(),  // The table to query
	    	    projection,                               // The columns to return
	    	    null,                                // The columns for the WHERE clause
	    	    null,                            // The values for the WHERE clause
	    	    null,                                     // don't group the rows
	    	    null,                                     // don't filter by row groups
	    	    UploadContract.COLUMN_TIME+" DESC"                                 // The sort order
	    	    );
	    System.out.println(cursor.getCount() + " " +cursor.getColumnCount());
	    ArrayList< HashMap<String,String> > entries = new ArrayList<HashMap<String,String>>();
       
	    if(cursor.getCount()==0)
	    	return null;
       
	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	    	HashMap<String,String> values = new HashMap<String,String>();
	    	for(int j=0 ; j< cursor.getColumnCount();j++){
	    		values.put(cursor.getColumnName(j),cursor.getString(j));
	    		
	    	}
	    	entries.add(values);
	    	cursor.moveToNext();
	      }
	    cursor.close();
	    db.close();
	    
		int r=0;
	    String [] results = new String[cursor.getCount()];
	    for(HashMap<String,String> h : entries){
	    	Date date= new Date(Long.valueOf(h.get(UploadContract.COLUMN_TIME)));
        	DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
        	String dateFormatted = formatter.format(date);
	    	results[r++]=dateFormatted;
	    }
	    
		return results;
	}

	/**
	 * Returns the number of rows of each table.
	 * @return
	 */
	public int getRows() {
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
	    String[] projection= {"entryid"};
	    
	    Cursor cursor = db.query(
	    	    mContract.getTableName(),  // The table to query
	    	    projection,                               // The columns to return
	    	    null,                                // The columns for the WHERE clause
	    	    null,                            // The values for the WHERE clause
	    	    null,                                     // don't group the rows
	    	    null,                                     // don't filter by row groups
	    	    null                                 // The sort order
	    	    );
	    return cursor.getCount();
	}
	
}

