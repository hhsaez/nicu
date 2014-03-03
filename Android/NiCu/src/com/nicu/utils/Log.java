package com.nicu.utils;

public class Log {
	
	private static final String TAG = "NiCu";
	
	public static void info(String message)
	{
		android.util.Log.i(TAG, message);
	}
	
	public static void debug(String message)
	{
		android.util.Log.d(TAG, message);
	}
	
	public static void error(String message)
	{
		android.util.Log.d(TAG, message);
	}

}
