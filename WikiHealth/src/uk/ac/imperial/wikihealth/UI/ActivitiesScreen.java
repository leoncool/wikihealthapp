package uk.ac.imperial.wikihealth.UI;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import net.simonvt.calendarview.CalendarView;
import net.simonvt.calendarview.CalendarView.OnDateChangeListener;
import uk.ac.imperial.wikihealth.R;
import uk.ac.imperial.wikihealth.Database.SQLiteReaderWriter;
import uk.ac.imperial.wikihealth.Database.Contracts.ActivityContract;
import uk.ac.imperial.wikihealth.Labeling.EventsDialog;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * Activity responsible for entering the activities of the user on a specific date and time duration
 * This specific screen shows and handles a calendar with its events and shows the events for the selected date
 * @author Panayiotis Kritiotis (pkritiotis@gmail.com)
 *
 */
public class ActivitiesScreen extends SherlockActivity {
	
	private ActionbarBuilder mActionbarBuilder=new ActionbarBuilder("Activities");
	private CalendarView mCalendar;
	private Button mAddEventButton;
	private ListView mEventsList;
	private final Context mContext=this;
	private long mCurrentDate;
	final private int EDIT_ACTION=9;
	
	/**
	 * Called when activity is created
	 * Displays calendar view and initializes UI elements 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		
		//show calendar view
		setContentView(R.layout.calendar);
		
		//Initialize UI elements and handlers
		initUiElements();
	}
	
	/**
	 * Initilizes View Elements of the screen
	 */
	private void initUiElements() {
		//Initialize and handle Calendar Events
		mCalendar =(CalendarView)findViewById(R.id.calendar);
		mAddEventButton = (Button)findViewById(R.id.add_button);
		mEventsList = (ListView)findViewById(R.id.eventsList);
		mCurrentDate=mCalendar.getDate();
		
		//Updates Event list
		updateEventList();
		
		//sets up click listener for date changing --  shows events for selected day
		mCalendar.setOnDateChangeListener(new OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,int dayOfMonth) {
        		mCurrentDate=mCalendar.getDate();
        		updateEventList();
            }
        });

		//Initialize and Handle "Add Event" button clicks
		mAddEventButton.setOnClickListener(new View.OnClickListener() {
			
			@SuppressLint("SimpleDateFormat")
			@Override
			public void onClick(View v) {
				//Start EventsDialog passing the date as a parameter
				Intent addEvent = new Intent(mContext, EventsDialog.class);
            	Date currentDate= new Date(mCurrentDate);
            	DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            	String dateFormatted = formatter.format(currentDate);
            	addEvent.putExtra("date",dateFormatted);
            	addEvent.putExtra("date_in_milliseconds", String.valueOf(mCurrentDate));
            	((Activity) mContext).startActivityForResult(addEvent, EDIT_ACTION);  
			}
		});		
	}


	//Updates EventList by fetching data from the local database for the selected date
	private void updateEventList() {
		SQLiteReaderWriter db = new SQLiteReaderWriter(mContext, new ActivityContract());
		String[] values =db.getActivityEventsFromDatabase(mCurrentDate);
		if(values ==null){
			mEventsList.setAdapter(null);
			return;
		}
		ArrayList<String> list = new ArrayList<String>();
	    for (int i = 0; i < values.length; ++i) {
	    	list.add(values[i]);
	    }
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.listitem,R.id.list_content, list);
	    mEventsList.setAdapter(adapter);		
	}


	/**
	 * Handles Results returned from the EventDialog to report to the user whether the result of the event addition was successful or not
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                    Toast.makeText(getApplicationContext(), "Event added Succesfully",Toast.LENGTH_LONG).show();
                    updateEventList();
                break;
            default:
            	Toast.makeText(getApplicationContext(), "Event was not saved",Toast.LENGTH_LONG).show();
                break;
        }
    }
	
	/**
	 * Initializes ActionBar
	 */
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		//Add items on action bar
		menu=mActionbarBuilder.addMenuItems(menu);
    	return super.onCreateOptionsMenu(menu);
    }
	
	/**
	 * Handles ActionBar Events
	 */
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
	 	//Handles clicks on menu sub-items
    	if(mActionbarBuilder.handleMenuClicks(item, this)){
    		finish();
    		return true;
    	}
    	return super.onOptionsItemSelected(item);
    }
}
