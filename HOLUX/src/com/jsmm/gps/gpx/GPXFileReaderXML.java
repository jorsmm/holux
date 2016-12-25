package com.jsmm.gps.gpx;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//import tid.util.Logger;

import com.jsmm.gps.gpx.xml.GPXParser;
import com.jsmm.gps.gpx.xml.GPXParserListener;

public class GPXFileReaderXML implements GPXParserListener {

	private static final String SEPARADOR_VIAJE_Y_FECHA = "_";

	private static final SimpleDateFormat FORMATO_FECHA_FICHERO = new java.text.SimpleDateFormat("ddMMyyyy-HHmmss");

	/////////////////////////////////////////////////////////////////////////////

	OutputStreamWriter dos=null;
	InputStream is;
	private String fileNameOutPrefix;
	private GPXBounds gpxBounds;
	
	int numViajes=0;
	
	int ok=0;
	int err=0;
	int i=0	;

	private long currentTime;

	private GPXTrack currentGpxTrack;
	private ArrayList nombreViajes;

	private ArrayList primerosPuntos;

	/////////////////////////////////////////////////////////////////////////////
	
	public GPXFileReaderXML(String fileNameOutPrefix) {
		this.fileNameOutPrefix=fileNameOutPrefix;
		this.nombreViajes=new ArrayList();
		this.primerosPuntos=new ArrayList();
	}

	/////////////////////////////////////////////////////////////////////////////
	
	@Override
	public boolean startDocument() {
		System.err.println("===> INICIO de lectura de documento.");
		return true;
//		return nuevoFicheroViaje();
	}

	/**
	 * 
	 */
	private boolean nuevoFicheroViaje(GPXTrackPoint punto) {
		this.gpxBounds=new GPXBounds();
		this.numViajes++;
		//Logger.log(AMBITO_LOG,2,"escribeCurrentTrack "+this.numViajes);
		String sFich=this.fileNameOutPrefix+this.numViajes+SEPARADOR_VIAJE_Y_FECHA+FORMATO_FECHA_FICHERO.format(new Date(punto.getUTCtime()))+".gpx";
		this.nombreViajes.add(sFich);
		this.primerosPuntos.add(punto);
		System.out.println("===> Nuevo viaje a crear "+sFich);
		try {
			dos = new OutputStreamWriter(new FileOutputStream(sFich), "UTF-8");
			dos.write(GPXDocument.getHeader());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean newTrack(GPXTrack gpxTrack) {
		//Logger.log(AMBITO_LOG, 3,"newTrack("+gpxTrack+")");
		this.currentGpxTrack=gpxTrack;
		return true;
	}

	/**
	 * @return
	 */
	private boolean escribeCurrentTrack() {
		//Logger.log(AMBITO_LOG,2,"escribeCurrentTrack "+this.numViajes);
		if (this.currentGpxTrack.getDescription()==null) {
			this.currentGpxTrack.setDescription("Revisado GPXFileReader ["+new Date()+"]");
		}
		else {
			this.currentGpxTrack.setDescription(this.currentGpxTrack.getDescription()+ " (Revisado GPXFileReader ["+new Date()+"])");
		}
		try {
			dos.write(this.currentGpxTrack.getHeader());
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean newTrackPoint(GPXTrackPoint gpxTrackPoint) {
		i++;
		if (!gpxTrackPoint.isDataValid()) {
			err++;
			//Logger.log(AMBITO_LOG, 1,"###ERROR###newTrackPoint("+gpxTrackPoint+")");
		}
		else {
			ok++;
//			//Logger.log(AMBITO_LOG, 4,"newTrackPoint("+gpxTrackPoint+")");
			
			if (this.currentTime+3600000 < gpxTrackPoint.getUTCtime()) {
				System.err.println("===> Se ha encontrado una diferencia de tiempos mayor a 1 hora, asi que se crea un nuevo fichero de viaje GPX distinto.");
				//Logger.log(AMBITO_LOG,2,"Se ha encontrado una diferencia de tiempos mayor a 1 hora, asi que se crea un nuevo fichero de viaje GPX distinto.");
				this.cierraFicheroViaje();
				this.nuevoFicheroViaje(gpxTrackPoint);
				this.escribeCurrentTrack();
			}
			this.currentTime=gpxTrackPoint.getUTCtime();
			
			this.gpxBounds.updateBounds(gpxTrackPoint);
			try {
				dos.write(GPXTrackPoint.getTrkPoint(gpxTrackPoint));
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean endDocument() {
        System.err.println("===> FIN de lectura de documento.");
        try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cierraFicheroViaje();
	}

	/**
	 * @return
	 */
	private boolean cierraFicheroViaje() {
		//Logger.log(AMBITO_LOG,2,"cierraFicheroViaje "+this.numViajes);
		if (dos==null) {
			return true;
		}
		try {
			dos.write(GPXTrack.getFooter());
			dos.write(this.gpxBounds.getBounds());
			dos.write(GPXDocument.getFooter());
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				dos.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		
		return true;
	}

	////////////////////////////////////////////////////////////////////////////////////
	
	public ResultadoGPXFileReader readGPXFile(String fileNameIn) {
		long tiempoInicial=System.currentTimeMillis();
		try {
			is =  new FileInputStream(fileNameIn);
			System.out.println("===> Fichero GPX Abierto para leer datos: "+fileNameIn);
			
			GPXParser gpxParser = new GPXParser(is, this);
			gpxParser.getInfo();
			is.close();
		} catch (IOException e) {
				e.printStackTrace();
		}
		System.out.println("===> Finalizado Lectura de GPX, tardando "+(System.currentTimeMillis()-tiempoInicial)+"ms");
		System.out.flush();
		System.err.println("===> Número de VIAJES = "+this.numViajes+". Puntos de GPS totales="+i+". Consideramos de ellos OK="+ok+". y por tanto incorrectos="+err);
		return new ResultadoGPXFileReader(primerosPuntos,nombreViajes,numViajes, ok, err);
	}
}