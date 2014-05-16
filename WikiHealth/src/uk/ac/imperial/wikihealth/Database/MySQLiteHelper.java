package uk.ac.imperial.wikihealth.Database;


import uk.ac.imperial.wikihealth.Database.Contracts.ActivityContract;
import uk.ac.imperial.wikihealth.Database.Contracts.GPScontract;
import uk.ac.imperial.wikihealth.Database.Contracts.Heartratecontract;
import uk.ac.imperial.wikihealth.Database.Contracts.UploadContract;
import uk.ac.imperial.wikihealth.SensorFramework.SensorUtilities;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.hardware.Sensor;
import android.util.Log;

/**
 * Class containing the helper for creating/reading writing and updating the local database
 * @author Panayiotis Kritiotis (pkritiotis@gmail.com)
 *
 */
public class MySQLiteHelper extends SQLiteOpenHelper {
	  private final static int DATABASE_VERSION=1;
	  private static MySQLiteHelper mInstance;
	  private final static String DB_NAME="Sensors.db";
	  private static Context mContext;
	  
	  /**
	   * singleton
	   * @param _context
	   * @return
	   */
		public static synchronized MySQLiteHelper getHelper(Context _context){
		    if (mInstance == null){
				mContext=_context;
		        mInstance = new MySQLiteHelper(mContext);
		    }
		    return mInstance;
		}

	/**
	 * creates the instance of the class when it is first called
	 * @param context
	 */
	  public MySQLiteHelper(Context context) {
		  super(context,  DB_NAME, null, DATABASE_VERSION);
	  }
	
	  /**
	   * Initializes the database by creating the tables if they do not exist or updating them to a new version
	   */
	  @Override
	  public void onCreate(SQLiteDatabase database) {
		  if(database==null)
		  System.out.println("database  : "+ database);
		  SensorUtilities si= new SensorUtilities(mContext);
		  for(Sensor s : si.getAvailableSensors()){
			  Log.i("helper: ",s.getName());
			if(SensorUtilities.getContract(s)!=null){
				database.execSQL(SensorUtilities.getContract(s).getDBCreate());
			}
		  }		  
		  database.execSQL(new GPScontract().getDBCreate());
		  database.execSQL(new Heartratecontract().getDBCreate());
		  database.execSQL(new ActivityContract().getDBCreate());
		  database.execSQL(new UploadContract().getDBCreate());
	  }
	  
	  @Override
	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	  }
}