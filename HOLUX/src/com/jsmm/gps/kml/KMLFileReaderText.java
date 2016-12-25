package com.jsmm.gps.kml;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.jsmm.gps.GPSData;

public class KMLFileReaderText {


	private static int MAX_FILAS_A_RECORRER=10;

	private static final SimpleDateFormat FORMATO_FECHA = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	/**
	 * debe aparece algo como: <name>GPS device</name> y <Snippet> antes de las 10 primeras líneas.
	 * si está en distintas filas no funciona
	 *  
	 * @param sFileName
	 * @return devuelve un long leyendo de algo como 2010-01-08T07:48:40Z, o -1 si no se ha encontrado
	 */
	public static boolean modifyNameAndDesc(String sFileName, GPSData primerPunto) {
		String sCadena;
		BufferedReader bfin=null;
		OutputStreamWriter bfout=null;
		
		File file = new File(sFileName);
		if (!file.exists()) {
			return (false);
		}
		else {
			file.renameTo(new File(sFileName+"_tmp"));
		}
		
		int i=1;
		try {
			bfin = new BufferedReader(new FileReader(sFileName+"_tmp"));
			bfout = new OutputStreamWriter(new FileOutputStream(sFileName), "UTF-8");
			
			while ((sCadena = bfin.readLine())!=null || (++i > MAX_FILAS_A_RECORRER)) {
				if (sCadena.indexOf("<name>GPS device</name>")!=-1) {
					bfout.write("<name>Holux M-241 (");
					bfout.write(FORMATO_FECHA.format(new Date(primerPunto.getUTCtime())));
					bfout.write(")</name>\r\n");
				}
				else if (sCadena.indexOf("<Snippet>")!=-1) {
					bfout.write("<Snippet>Viaje iniciado el ");
					bfout.write(FORMATO_FECHA.format(new Date(primerPunto.getUTCtime())));
					bfout.write("</Snippet>\r\n");
				}
				else {
					bfout.write(sCadena+"\r\n");
				}
			}
			while ((sCadena = bfin.readLine())!=null) {
				bfout.write(sCadena+"\r\n");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				bfout.flush();
				bfout.close();
				bfin.close();

				file = new File(sFileName+"_tmp");
				file.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}
}