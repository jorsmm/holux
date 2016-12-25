package com.jsmm.gps;

import java.util.*;

public class GPSData {

	public static final float MINLAT =   -90.0f;
	public static final float MAXLAT =  90.0f;
	public static final float MINLON =  -180.0f;
	public static final float MAXLON = 180.0f;
	protected static final float MINSPEED = 0.0f;
	protected static final float MAXSPEED = 950.0f;
	protected static final float MINHEIGHT = 0.0f;
	protected static final float MAXHEIGHT = 15500.0f;
	
	//1/1/2009
	protected static final long MINTIME = 1230764400000l;
		
	//////////////////////////////////////////////////////////////////////////

	protected static final java.text.SimpleDateFormat FORMATO_FECHA_IMPRIMIR = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	//////////////////////////////////////////////////////////////////////////

	protected long UTCtime;
	protected float latitude;
	protected float longitude;
	protected float height;
	protected float speed;

	//////////////////////////////////////////////////////////////////////////	

    public long getUTCtime() {
		return UTCtime;
	}

	public void setUTCtime(long uTCtime) {
		UTCtime = uTCtime;
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	//////////////////////////////////////////////////////////////////////////

    protected String fechaToString(java.util.Date date) {
        return FORMATO_FECHA_IMPRIMIR.format(date);
    }

	//////////////////////////////////////////////////////////////////////////

    public boolean isDataValid() {
    	return !(this.UTCtime<MINTIME ||
    			this.latitude<MINLAT || this.latitude>MAXLAT ||
    			this.longitude<MINLON || this.longitude>MAXLON ||
    			this.height<MINHEIGHT || this.height>MAXHEIGHT ||
    			this.speed<MINSPEED || this.speed>MAXSPEED);
    }

	//////////////////////////////////////////////////////////////////////////

	public String mostrar (String sSeparador) {		
		StringBuffer sb = new StringBuffer();
		sb.append("UTCtime=").append(fechaToString(new Date(this.UTCtime))).append(sSeparador);
		sb.append("latitude=").append(String.format(Locale.US, "%.6f", this.latitude)).append(sSeparador);
		sb.append("longitude=").append(String.format(Locale.US, "%.6f", this.longitude)).append(sSeparador);
		sb.append("height=").append(String.format(Locale.US, "%.2f",this.height)).append(sSeparador);
		sb.append("speed=").append(String.format(Locale.US, "%.1f",this.speed));
		return sb.toString();
	}
	
	public String toString () {
		return mostrar("|");
	}
}