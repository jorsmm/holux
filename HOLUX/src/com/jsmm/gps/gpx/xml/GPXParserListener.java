package com.jsmm.gps.gpx.xml;

import com.jsmm.gps.gpx.GPXTrack;
import com.jsmm.gps.gpx.GPXTrackPoint;

public interface GPXParserListener {

	public boolean startDocument();
	public boolean endDocument();
	public boolean newTrack(GPXTrack gpxTrack);
	public boolean newTrackPoint(GPXTrackPoint gpxTrackPoint);
}
