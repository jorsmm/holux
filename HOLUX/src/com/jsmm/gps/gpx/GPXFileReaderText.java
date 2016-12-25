package com.jsmm.gps.gpx;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class GPXFileReaderText {

	private static int MAX_FILAS_A_RECORRER=10;
	
	/**
	 * debe aparece algo como: <trkpt lat="40.461338" lon="-3.591761"><ele>639.609375</ele><time>2010-01-08T07:48:40Z</time></trkpt>
	 * si está en distintas filas no funciona
	 *  
	 * @param sFileName
	 * @return devuelve un long leyendo de algo como 2010-01-08T07:48:40Z, o -1 si no se ha encontrado
	 */
	public static long getFirstTime(String sFileName) {
		String sCadena;
		BufferedReader bf=null;
		int i=1;
		try {
			bf = new BufferedReader(new FileReader(sFileName));
			while ((sCadena = bf.readLine())!=null || (++i > MAX_FILAS_A_RECORRER)) {
				int inicio=sCadena.indexOf("<time>");
				if (inicio>0) {
					int fin = sCadena.indexOf("</time>",inicio);
					if (fin > inicio) {
						return GPXTrackPoint.getMillisUTCTime(sCadena.substring(inicio,fin));
					}
				}
			} 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				bf.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return -1;
	}
}