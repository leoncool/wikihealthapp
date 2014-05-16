package uk.ac.imperial.wikihealth.Utils;

import uk.ac.imperial.wikihealth.Database.Contracts.Contract;
import uk.ac.imperial.wikihealth.Database.Contracts.GPScontract;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

public class PreferenceUtils {
	public static String getPeriod(Context context,Contract c) {
		SharedPreferences settings = context.getSharedPreferences("sensors",0 );
		return String.valueOf(settings.getInt(c.getTableName()+"_period",5000));
	}

	public static void updateMonitoringPreference(Context context,Contract c,boolean checked){
		SharedPreferences settings = context.getSharedPreferences("sensors",0);
		settings.edit().putBoolean(c.getTableName(), checked).commit();
	}
	
	public static void updatePeriodPreference(Context context,Contract c,int period){
		SharedPreferences settings = context.getSharedPreferences("sensors",0 );
		settings.edit().putInt(c.getTableName()+"_period", period).commit();
	}

	public static boolean isChecked(Context context, Contract contract) {
		SharedPreferences settings = context.getSharedPreferences("sensors",0 );
		return settings.getBoolean(contract.getTableName(),false);
	}

	public static boolean isPeriodic(Context context, GPScontract gpScontract) {
		SharedPreferences settings = context.getSharedPreferences("sensors",0 );
		return settings.getBoolean(gpScontract.getTableName()+"_type",false);
	}

	public static void updateLocationMonitoringPreference(Context context, boolean checked) {
		SharedPreferences settings = context.getSharedPreferences("sensors",0);
		settings.edit().putBoolean((new GPScontract().getTableName())+"_type", checked).commit();
		
	}
}
