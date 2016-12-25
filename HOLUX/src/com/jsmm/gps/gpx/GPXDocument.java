package com.jsmm.gps.gpx;

public class GPXDocument {

	private static final String XML_BASE_HEADER="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" xmlns:xalan=\"http://xml.apache.org/xalan\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" creator=\"Jorge SMM\" version=\"1.1\">";
	private static final String XML_BASE_FOOTER="\n</gpx>";
	
	//////////////////////////////////////////////////////////////////////////

	public static String getHeader () {
		return XML_BASE_HEADER;
	}

	public static String getFooter () {
		return XML_BASE_FOOTER;
	}
}
