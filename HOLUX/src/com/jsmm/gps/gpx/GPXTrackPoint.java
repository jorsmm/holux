package com.jsmm.gps.gpx;

import java.util.*;
import java.text.*;
import com.jsmm.gps.GPSData;

public class GPXTrackPoint extends GPSData {

	//////////////////////////////////////////////////////////////////////////

	private static final String XML_BASE = "<trkpt lat=\"%1$.6f\" lon=\"%2$.6f\"><ele>%3$.6f</ele><time>%4$s</time></trkpt>";
	public static final SimpleDateFormat FORMATO_FECHA = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	private static final String PREFIX_TRKPOINT_XML="\n\t\t\t";

	//////////////////////////////////////////////////////////////////////////

	public void setUTCtime(String UTCtime) {
		 this.UTCtime=getMillisUTCTime(UTCtime);
	}
	
	public static long getMillisUTCTime (String UTCtime) {
		Date fecha = null;
		 try {
		     fecha = FORMATO_FECHA.parse(UTCtime);
		 } catch (ParseException ex) {
		     ex.printStackTrace();
		 }
		 return fecha.getTime();
	}
	//////////////////////////////////////////////////////////////////////////

	/**
	 * Esta es la pinta de un trkpt de GPX:
	 * 
	 * <trkpt lat="40.448787" lon="-3.639742">
     *   <ele>743.958362</ele>
     *   <time>2009-12-01T17:14:45Z</time>
     * </trkpt>
	 */
	public static String format (GPSData gpsData) {
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb,Locale.US);
		return formatter.format(XML_BASE, gpsData.getLatitude(),gpsData.getLongitude(),gpsData.getHeight(),otraFechaToString(new Date(gpsData.getUTCtime()))).toString();
	}
	
	public static String getTrkPoint(GPSData gpsData) {
		return PREFIX_TRKPOINT_XML+GPXTrackPoint.format(gpsData);
	}

	private static String otraFechaToString(java.util.Date date) {
	    return FORMATO_FECHA.format(date);
	}
}