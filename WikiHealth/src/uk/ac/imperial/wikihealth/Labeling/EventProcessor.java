package uk.ac.imperial.wikihealth.Labeling;

import java.util.Calendar;
import java.util.Date;

import uk.ac.imperial.wikihealth.Database.SQLiteReaderWriter;
import uk.ac.imperial.wikihealth.Database.Contracts.ActivityContract;
import android.content.ContentValues;
import android.content.Context;

/**
 * Class responsible for saving the inserted events in the local database and returning the timestamp of a given string 
 * @author Panayiotis Kritiotis (pkritiotis@gmail.com)
 *
 */
public class EventProcessor {

	private String mDate;
	private String mTimeFrom;
	private String mTimeTo;
	private String mActivity;
	private Context mContext;
	
	/**
	 * Constructor initializing fields
	 * @param date
	 * @param time_from
	 * @param time_to
	 * @param activity
	 * @param context
	 */
	public EventProcessor(String date,String time_from, String time_to, String activity, Context context) {
		mDate=date;
		mTimeFrom=time_from;
		mTimeTo=time_to;
		mActivity=activity;
		mContext=context;
		}
	
	/**
	 * Saves given event in the local database
	 * @return
	 */
	public boolean SaveEvent(){
		   
	        	SQLiteReaderWriter db = new SQLiteReaderWriter(mContext,new ActivityContract());
			
				ContentValues values=new ContentValues();
				values.put(ActivityContract.COLUMN_TIME, Calendar.getInstance().getTimeInMillis());
				values.put(ActivityContract.COLUMN_DATE, mDate);
				values.put(ActivityContract.COLUMN_FROM_TIME, timestampOf(mTimeFrom));
				values.put(ActivityContract.COLUMN_TO_TIME, timestampOf(mTimeTo));
				values.put(ActivityContract.COLUMN_ACTIVITY, mActivity);

				System.out.println("EventWriting: "+values.toString());
				return db.writeToDatabase(values);
	        }

	
	/**
	 * Returns timestamp of a given string
	 * @param time
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public String timestampOf(String time) {
			Date kdate=new Date();
			
			System.out.println("date input: "+mDate);
			
			String [] days=mDate.split("/");
			
			kdate.setDate(Integer.parseInt(days[0]));
			kdate.setMonth(Integer.parseInt(days[1])-1);
			kdate.setYear(Integer.parseInt(days[2])-1900);
		
			String []times = time.split(":");
			kdate.setHours(Integer.parseInt(times[0]));
			kdate.setMinutes(Integer.parseInt(times[1]));
			
			kdate.setSeconds(0);
			return String.valueOf(kdate.getTime());
	
	
	}
}
