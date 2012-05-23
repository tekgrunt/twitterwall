package twitterwall.core;

/**
 * Some static variables used across the threads. The queue of Tweets and
 * I can't remember what FIRST was for.
 * 
 * @author christopherluft
 *
 */
public class Shared 
{
	public static long FIRST = -1;
	public static boolean LOCAL_DB_ENABLED = false;

	public static final int Width = 1024;
	public static final int Height = 768;
	
	public static final String ImageFolder = "images/";
	public static final String IconFolder = "icons/";
	public static final String BackgroundsFolder = "backgrounds/";
	
	public static final int IconSize = 48;
	public static final int BackgroundSwitchAmount = 10;
	
	public static boolean WEBSERVICE_ENABLED = false;
	
	public static String ADMIN_USER = "yaletown2012";
	public static String ADMIN_TAG = "#admintwm2o";
	public static int ADMIN_CYCLE = 2;
	
}
