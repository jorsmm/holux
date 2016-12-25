package com.jsmm.gps.gpx;

import java.util.Formatter;
import java.util.Locale;

import com.jsmm.gps.GPSData;

public class GPXBounds {

	private float minlat=GPSData.MAXLAT;
	private float maxlat=GPSData.MINLAT;
	private float minlon=GPSData.MAXLON;
	private float maxlon=GPSData.MINLON;

	//////////////////////////////////////////////////////////////////////////
	
	//<bounds minlat="-0.000000000" minlon="-3.658511877" maxlat="12718204.000000000" maxlon="40.535045624"/>
	private static final String XML_BASE = "<bounds minlat=\"%1$.6f\" minlon=\"%2$.6f\" maxlat=\"%3$.6f\" maxlon=\"%4$.6f\"/>";
	private static final String PREFIX_BOUNDS_XML="\n\t";

	//////////////////////////////////////////////////////////////////////////

	public void updateBounds (GPSData gpsData) {
		if (this.minlat>gpsData.getLatitude()) {
			this.minlat=gpsData.getLatitude();
		}
		if (this.minlon>gpsData.getLongitude()) {
			this.minlon=gpsData.getLongitude();
		}
		if (this.maxlat<gpsData.getLatitude()) {
			this.maxlat=gpsData.getLatitude();
		}
		if (this.maxlon<gpsData.getLongitude()) {
			this.maxlon=gpsData.getLongitude();
		}
	}
	
	/**
	 * Esta es la pinta de un trkpt de GPX:
	 * 
	 * <bounds minlat="-0.000000000" minlon="-3.658511877" maxlat="12718204.000000000" maxlon="40.535045624"/>
	 */
	public String format () {
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb,Locale.US);
		return formatter.format(XML_BASE, this.minlat, this.minlon, this.maxlat, this.maxlon).toString();
	}
	public String getBounds() {
		return PREFIX_BOUNDS_XML+this.format();
	}
}
