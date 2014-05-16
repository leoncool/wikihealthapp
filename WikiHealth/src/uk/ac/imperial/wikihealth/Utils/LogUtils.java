package uk.ac.imperial.wikihealth.Utils;

import android.util.Log;

/**
 * Utility to help developer with debugging
 * Can select to exclude certain debugging messages
 * @author Panayiotis Kritiotis (pkritiotis@gmail.com)
 *
 */
public class LogUtils{
	
	//Variable to select which class to debug
	private final static String mShowOnly=null;
	
	
	/**
	 * Information Logs
	 * @param className
	 * @param tag
	 * @param msg
	 */
	public static void info(String className, String tag, String msg){
		if(mShowOnly==null || className.equals(mShowOnly))
				return;
		Log.i(tag, msg);
	}
	
	/**
	 * Warning Logs
	 * @param className
	 * @param tag
	 * @param msg
	 */
	public static void warn(String className, String tag, String msg){
		if(mShowOnly==null || className.equals(mShowOnly))
			return;
		Log.w(tag, msg);
	}
	
	/**
	 * Error Logs
	 * @param className
	 * @param tag
	 * @param msg
	 */
	public static void err(String className, String tag, String msg){
		if(mShowOnly==null || className.equals(mShowOnly))
			return;
		Log.e(tag, msg);
	}
	
}
