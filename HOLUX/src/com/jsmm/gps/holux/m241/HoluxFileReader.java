package com.jsmm.gps.holux.m241;

import java.io.*; 

import com.jsmm.gps.gpx.GPXTrack;
import com.jsmm.gps.gpx.GPXTrackPoint;

public class HoluxFileReader {

	public static void main(String f[]) {
		String pathBase="C:\\Documents and Settings\\jsmm\\workspace\\HOLUX\\HOLUX\\";
		//String prefijoFichero="jsmm_M-241_Start_20091226-173600_Finish_20091226-195656";
		String prefijoFichero="prueba";
		readHoluxTRLFile(pathBase+prefijoFichero+".bin",pathBase+prefijoFichero+".gpx");
	}

	public static void readHoluxTRLFile(String fileNameIn,String fileNameOut) {
		DataInputStream dis;
		OutputStreamWriter dos=null;

		long tiempoInicial=System.currentTimeMillis();

		int ok=0;
		int err=0;
		int i=0	;

		try {
			dis = new DataInputStream( new FileInputStream(fileNameIn) );
			dos = new OutputStreamWriter(new FileOutputStream(fileNameOut), "UTF-8");
			System.out.println( "file: " + fileNameIn ); 
			System.out.println("Fichero Abierto, tardando "+(System.currentTimeMillis()-tiempoInicial)+"ms");
			dos.write(GPXTrack.getHeader("prueba", "ejemplo de prueba"));
			
			HoluxData holuxData=null;
			byte [] bytes=new byte[20];
			int resul=0;
			while ( true ) {
				resul=dis.read(bytes, 0, 20);
				i++;
				
				if (resul==20) {
					holuxData = new HoluxData(bytes);
					if (holuxData.isDataValid()) {
						ok++;
						System.out.println("leer dato: "+i+", con resultado: "+holuxData);
						dos.write(GPXTrackPoint.getTrkPoint(holuxData));
					}
					else {
						err++;
						System.err.println("dato INVALIDO: "+i+", con resultado: "+holuxData);
					}
				}
				else if (resul==-1) {
					System.err.println("FIN FICHERO");
					break;
				}
				else {
					System.err.println("solo se han leído "+resul+" bytes.");
				}
			} 
		} catch (EOFException eof) {
			System.out.println( "EOF reached " );
		} 
		catch (IOException ioe) {
			System.out.println( "IO error: " + ioe );
		} 
		finally {
			try {
				dos.write(GPXTrack.getFooter());
				dos.flush();
				dos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Finalizado, tardando "+(System.currentTimeMillis()-tiempoInicial)+"ms");
		System.err.println("Finalizado TOTAL="+(--i)+". OK="+ok+". ERROR="+err);
	}
}