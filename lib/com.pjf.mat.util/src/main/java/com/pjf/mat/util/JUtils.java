package com.pjf.mat.util;

public class JUtils {

	/**
	 * Check if a class exists
	 * 
	 * @param className
	 * @return true if it exists
	 */
	public static boolean classExists(String className)
	{
	    try
	    {
	        Class.forName(className);
	        return true;
	    }
	    catch(ClassNotFoundException ex)
	    {
	        return false;
	    }
	}

}
