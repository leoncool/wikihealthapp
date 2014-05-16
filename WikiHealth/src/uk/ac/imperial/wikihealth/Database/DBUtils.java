package uk.ac.imperial.wikihealth.Database;

import java.text.DecimalFormat;
import java.util.ArrayList;

import uk.ac.imperial.wikihealth.Database.Contracts.ActivityContract;
import uk.ac.imperial.wikihealth.Database.Contracts.Contract;
import uk.ac.imperial.wikihealth.Database.Contracts.GPScontract;
import uk.ac.imperial.wikihealth.Database.Contracts.Heartratecontract;
import uk.ac.imperial.wikihealth.SensorFramework.SensorUtilities;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;

/**
 * Util for returning all contracts of available sensors and some database statistics
 * @author Panayiotis Kritiotis (pkritiotis@gmail.com)
 *
 */
public class DBUtils {
	SensorUtilities s;
	Context mContext;
	public DBUtils(Context _context){
		mContext=_context;
		s= new SensorUtilities(_context);

	}
	
	/**
	 * Returns all contracts
	 * @return
	 */
	public  ArrayList<Contract> getAllContracts(){
		
		ArrayList<Contract> contracts = new ArrayList<Contract>();
		for(Sensor sensor: s.getAvailableSensors() ){
			contracts.add(SensorUtilities.getContract(sensor));
		}
		contracts.add(new GPScontract());
		contracts.add(new Heartratecontract());
		contracts.add(new ActivityContract());
		return contracts;
		
	}

	/**
	 * Cleans the database. Deleting every table except activities
	 */
	public void dbClean() {
		MySQLiteHelper mDbHelper=MySQLiteHelper.getHelper(mContext);
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		
		SensorUtilities si= new SensorUtilities(mContext);
		  for(Sensor s : si.getAvailableSensors()){
			if(SensorUtilities.getContract(s)!=null){
				db.delete(SensorUtilities.getContract(s).getTableName(),null,null);
			}
		  }		  
		  db.delete(new GPScontract().getTableName(),null,null);
		  db.delete(new Heartratecontract().getTableName(),null,null);
		  db.delete(new ActivityContract().getTableName(),null,null);
	}
	
	/**
	 * Checks if database is full
	 * @return
	 */
	public boolean isFull(){
//		final int limit=15000;
		final int limit=10000000;
//		System.out.println("i'm here");
		ArrayList<Contract> contracts = this.getAllContracts();
//		System.out.println(contracts.size());
		int sum=0;
		for(Contract c:contracts){
			SQLiteReaderWriter db = new SQLiteReaderWriter(mContext,c);
//			System.out.println("rows of contract: "+db.getRows());
			sum+=db.getRows();
			if(sum>=limit)
				return true;
		}
		return false;
	}
	
	/**
	 * Returns the capacity of the database. How full it is
	 * @return
	 */
	public double getCapacityPercentage(){
//		final int limit=15000;
		final int limit=10000000;
		int sum=0;
		//System.out.println("i'm here");
		ArrayList<Contract> contracts = this.getAllContracts();
//		System.out.println(contracts.size());
		for(Contract c:contracts){
			SQLiteReaderWriter db = new SQLiteReaderWriter(mContext,c);
			System.out.println("rows of contract: "+db.getRows());
			sum+=db.getRows();
		}
//		
//		System.out.println("max "+sum +" all : "+sum/(limit/100));
		return sum;
		/*if((sum/(limit/100.0)>100))
			return 100;
		return Double.parseDouble((new DecimalFormat("#.#######").format((sum/((limit/100.0))))));*/
	}
}
