package uk.ac.imperial.wikihealth.Labeling;

import java.util.Calendar;

import uk.ac.imperial.wikihealth.R;
import uk.ac.imperial.wikihealth.Utils.ConnectivityUtil;
import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * Activity containing a dialog for entering event details for a given Date
 * @author Panayiotis Kritiotis (pkritiotis@gmail.com)
 *
 */
public class EventsDialog extends Activity {

   private EditText mTimeDisplayFrom,mTimeDisplayTo,mActivityText;
   private Button mPickTimeFrom,mPickTimeTo,mAddEvent;
   
   private int mHour;
   private int mMinute;
   private boolean mIsFrom=false;
   private boolean mIsTo=true;

   
   private static final int TIME_DIALOG_ID = 2;

   private final Context context=this;
  

   /**
    * Initializes Views
    */
   @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_events);
       
       initViews();
       
       initClickListeners();
       
       setCurrentDate();

       // display the current time
       displayTime();
   }

   /**
    * sets curent data for the calendar
    */
   private void setCurrentDate() {
	   // get the current date and time
       final Calendar c = Calendar.getInstance();
       mHour = c.get(Calendar.HOUR_OF_DAY);
       mMinute = c.get(Calendar.MINUTE);	
   }

   /**
    * initializes click listeners on the calendar view
    */
   private void initClickListeners() {
	   // add a click listener to the FROM button
       mPickTimeFrom.setOnClickListener(new View.OnClickListener() {
           @SuppressWarnings("deprecation")
		public void onClick(View v) {
        	   mIsFrom=true;
               showDialog(TIME_DIALOG_ID);
           }
       });
       
       // add a click listener to the button
       mPickTimeTo.setOnClickListener(new View.OnClickListener() {
           @SuppressWarnings("deprecation")
		public void onClick(View v) {
               mIsTo=true;
        	   showDialog(TIME_DIALOG_ID);
               
           }
       });
       
       //add a click listener to add event button

       mAddEvent.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			
			if(!ConnectivityUtil.isNetworkAvailable(EventsDialog.this))
        		ConnectivityUtil.buildAlertMessageNoInternet(EventsDialog.this);
        	else{
        		String date=getIntent().getExtras().getString("date");
				String time_from=mTimeDisplayFrom.getText().toString();
				String time_to=mTimeDisplayTo.getText().toString();
				String activity_text=mActivityText.getText().toString();
				EventProcessor ep=new EventProcessor(date,time_from,time_to,activity_text,context);
				
		
				if(time_to.equals("") || time_from.equals("") || (time_to.length()!=5) || (time_from.length()!=5)|| activity_text.equals("") || Long.valueOf(ep.timestampOf(time_from))>=Long.valueOf(ep.timestampOf(time_to)) 
				|| !(time_to.charAt(2) ==':') || !(time_from.charAt(2) ==':')  || !isInteger((time_to.substring(0, 2)+time_to.substring(3, 5))) 
				|| !isInteger((time_from.substring(0, 2)+time_from.substring(3, 5))) || (Integer.parseInt(time_to.substring(3, 5))>59) ||(Integer.parseInt(time_from.substring(3, 5))>59) )
					Toast.makeText(EventsDialog.this, "Invalid Input data. Make sure that all fields are completed and times are valid. Time must be in HH:MM format", Toast.LENGTH_LONG).show();
				else{
					
					boolean res=ep.SaveEvent();
					if(res==true)
						setResult(RESULT_OK,new Intent());
					else
						setResult(RESULT_CANCELED);
					finish();
				}
        	}
		}
       });	
   }
   public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    // only got here if we didn't return false
	    return true;
	}

/**
    * Initializes views
    */
   private void initViews() {
	// capture our View elements
       mTimeDisplayFrom = (EditText) findViewById(R.id.editText1);
       mPickTimeFrom = (Button) findViewById(R.id.button1);
       mTimeDisplayTo = (EditText) findViewById(R.id.editText2);
       mPickTimeTo = (Button) findViewById(R.id.Button01);
       mAddEvent = (Button) findViewById(R.id.add_event_button);
       mActivityText=(EditText) findViewById(R.id.activity);
       
       TextView title = (TextView) findViewById(R.id.title_event);
       String s = getIntent().getExtras().getString("date");
       title.setText(s);	
}


/**
    *  updates the time we display in the EditText   private static final int TIME_DIALOG_ID = 2;

    */
   private void displayTime() {
	   if(mIsFrom){
		   mTimeDisplayFrom.setText(
               new StringBuilder()
               .append(pad(mHour)).append(":")
               .append(pad(mMinute)));
		   mIsFrom=false;
	   }
	   else if(mIsTo){
		   mTimeDisplayTo.setText(
	               new StringBuilder()
	               .append(pad(mHour)).append(":")
	               .append(pad(mMinute)));
		   mIsTo=false;
	   }
   }

   /**
    * Padding utility for inserting zero if time <10
    * @param c
    * @return
    */
   private String pad(int c) {
       if (c >= 10)
           return String.valueOf(c);
       else
           return "0" + String.valueOf(c);
   }

   /**
    *  the callback received when the user "sets" the time in the dialog
    */
   private TimePickerDialog.OnTimeSetListener mTimeSetListener =
       new TimePickerDialog.OnTimeSetListener() {
       public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
           mHour = hourOfDay;
           mMinute = minute;
           displayTime();
       }
   };
   /**
    * Executed when the dialog is created
    */
   @Override
   protected Dialog onCreateDialog(int id) {
       switch (id) {
       case TIME_DIALOG_ID:
           return new TimePickerDialog(this,
                   mTimeSetListener, mHour, mMinute, false);

       }
       return null;
   }  
   
}