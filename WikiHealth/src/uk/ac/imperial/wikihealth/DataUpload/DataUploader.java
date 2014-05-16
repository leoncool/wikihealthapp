package uk.ac.imperial.wikihealth.DataUpload;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import uk.ac.imperial.wikihealth.Database.DBUtils;
import uk.ac.imperial.wikihealth.Database.SQLiteReaderWriter;
import uk.ac.imperial.wikihealth.Database.Contracts.Contract;
import uk.ac.imperial.wikihealth.Database.Contracts.UploadContract;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Class responsible for uploading the collected data to the remote API
 * @author Panayiotis Kritiotis (pkritiotis@gmail.com)
 *
 */
public class DataUploader {
	private String USERNAME="";
	private String PASSWORD="";
	private Context mContext = null;
	public static boolean sInstance=false;
	//private final String mClassName="DataUploader";
	/**
	 * Constructor initializing fields
	 * @param _context
	 */
	public DataUploader(Context _context){
		mContext=_context;
		sInstance=true;
		prepareData();
	}
	
	/**
	 * Get data from local database
	 */
	public void prepareData(){
		Log.i("DataUploader","Beginning uploading");
	}

	/**
	 * Gets the credentials that were saved when the user logged in or registered to the service
	 * @return
	 */
	public boolean initCredentials(){
		SharedPreferences settings = mContext.getSharedPreferences("id_credentials", 0);
		USERNAME=settings.getString("username","NOUSER");
		PASSWORD=settings.getString("password","NOPASS");
		
		if(USERNAME.equals("NOUSER") || PASSWORD.equals("NOPASS")){
			Log.i("credentials", "not found");
			return false;
		}
			
		return true;
	}
	/**
	 * Uploads data to the remote API
	 */
	public void upload()  {
		DBUtils db_utils=new DBUtils(mContext);
		
			
		ArrayList<Contract> contracts=db_utils.getAllContracts();
		
		if(!initCredentials()){
			//Toast.makeText(mContext, "Credentials not found. Something went really wrong. Please reinstall the application", Toast.LENGTH_LONG).show();
			return;
		}
		
		JSONUtils json_util=new JSONUtils(USERNAME,PASSWORD);
		String datastream_name=null;
		String [] columns=null;
		json_util.initConnection();
    	boolean datastreams_created=isDatastreamsCreated();
//    	DBUtils dbutils=new DBUtils(mContext);
//		
//		ArrayList<ArrayList<HashMap<String,String>>> db_contents= new ArrayList<ArrayList<HashMap<String,String>>>();
    	int row=0;
    	
    	//for every table upload the data
		for(Contract c:contracts){
			System.out.println("Contract: "+c.getTableName());
			SQLiteReaderWriter db = new SQLiteReaderWriter(mContext,c);
		
		ArrayList<HashMap<String,String>> table=db.readFromDatabase();
			datastream_name=contracts.get(row).getTableName();
			columns=contracts.get(row).getProjection();
			//If the datastream does not exist create it
			if(!datastreams_created){
				json_util.createHealthDatastream(datastream_name,columns);
			}
			//send datapoints to the API
			json_util.sendDatapointsToHealthStream(datastream_name,columns,table);
			row++;
		
		}
		SharedPreferences settings = mContext.getSharedPreferences("first_time", 0);
        settings.edit().putBoolean("first_time", false).commit();
		saveUploadAction();
		db_utils.dbClean();
		sInstance=false;
	}

	/**
	 * Checks if the datastreams are already created
	 * @return
	 */
	private boolean isDatastreamsCreated() {
		SharedPreferences settings = mContext.getSharedPreferences("datastreams_created", 0);

		if(settings.getBoolean("datastreams_created", false)){
        	settings.edit().putBoolean("datastreams_created", true).commit();
        	return true;
    	}
		return false;
	}

	/**
	 * Saves the upload time and date in the local db
	 * @return
	 */
	public boolean saveUploadAction(){
    	SQLiteReaderWriter db = new SQLiteReaderWriter(mContext,new UploadContract());
		ContentValues values=new ContentValues();
		values.put(UploadContract.COLUMN_TIME, Calendar.getInstance().getTimeInMillis());
		return db.writeToDatabase(values);
    }
}
