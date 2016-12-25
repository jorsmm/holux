package com.jsmm.gps.holux.m241;

import com.jsmm.gps.GPSData;

public class HoluxData extends GPSData{

	/////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////

	/**
	 * Constructor
	 * 
	 * 4 (UTC time) + 4 latitude + 4 longitude + 3 height + 4 speed + 1 checksum
	 */
	public HoluxData(byte[] bytes) {
		this.UTCtime=arr2int(bytes, 0)*(long)1000;
		this.latitude=arr2float(bytes, 4);
		this.longitude=arr2float(bytes, 8);
		this.height=arr2float(bytes, 12, 3);
		this.speed=arr2float(bytes, 15);
	}
	
	//////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////
	
	public static int arr2int (byte[] arr, int start) {
		return arr2int (arr,start,4);
	}
	
	public static int arr2int (byte[] arr, int start, int len) {
		int i = 0;
		int cnt = 0;
		byte[] tmp = new byte[len];
		for (i = start; i < (start + len); i++) {
			tmp[cnt] = arr[i];
			cnt++;
		}
		int accum = 0;
		i = 0;
		for ( int shiftBy = 0; shiftBy < (8*len); shiftBy += 8 ) {
			accum |= ( (long)( tmp[i] & 0xff ) ) << shiftBy;
			i++;
		}
		return accum;
	}

	public static float arr2float (byte[] arr, int start) {
		return arr2float(arr,start,4);
	}
	
	public static float arr2float (byte[] arr, int start, int len) {
		int i = 0;
		int cnt = 0;

		byte[] tmp = new byte[4];
		
		for (cnt=0; cnt+len<4; cnt++) {
			tmp[cnt]=0x00;
		}
			
		for (i = start; i < (start + len); i++) {
			tmp[cnt] = arr[i];
			cnt++;
		}
		int accum = 0;
		i = 0;
		for ( int shiftBy = 0; shiftBy < 32; shiftBy += 8 ) {
			accum |= ( (long)( tmp[i] & 0xff ) ) << shiftBy;
			i++;
		}
		return Float.intBitsToFloat(accum);
	}
}