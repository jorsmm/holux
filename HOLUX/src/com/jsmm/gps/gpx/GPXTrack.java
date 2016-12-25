package com.jsmm.gps.gpx;

import java.text.MessageFormat;

public class GPXTrack {

	private static final String XML_BASE_HEADER="\n\t<trk>\n\t\t<name>{0}</name>\n\t\t<desc>{1}</desc>\n\t\t<trkseg>";
	private static final String XML_BASE_FOOTER="\n\t\t</trkseg>\n\t</trk>";
	
	//////////////////////////////////////////////////////////////////////////

	private String name;
	private String description;
	
	//////////////////////////////////////////////////////////////////////////

	public GPXTrack(String name, String description) {
		this.name=name;
		this.description=description;
	}
	
	public GPXTrack() {
		super();
	}

	//////////////////////////////////////////////////////////////////////////

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	//////////////////////////////////////////////////////////////////////////

	public String getHeader() {
		return getHeader(name, description);
	}
	
	//////////////////////////////////////////////////////////////////////////

	public static String getHeader (String name, String description) {
		return (MessageFormat.format(XML_BASE_HEADER,name,description));
	}

	public static String getFooter () {
		return XML_BASE_FOOTER;
	}
	
	//////////////////////////////////////////////////////////////////////////

	public String mostrar (String sSeparador) {		
		StringBuffer sb = new StringBuffer();
		sb.append("name=").append(name).append(sSeparador);
		sb.append("description=").append(description);
		return sb.toString();
	}
	
	public String toString () {
		return mostrar("|");
	}
}