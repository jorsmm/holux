package com.jsmm.gps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import tid.util.*;

public class GPSBabelControl {

//	private static final int ERROR = 1;
	private static final String AMBITO_LOG = "control";
	private static final String AMBITO_PROPERTIES = "control";
	
	
	////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * gpsbabel -t -r -w -i m241 -f com3: -o kml -F prueba.kml
	 * gpsbabel -t -r -w -i m241 -f com3: -o gpx -F prueba.gpx
	 * mtk_logger: This is not a MTK based GPS ! (or is it turned off ?)
	 * 
	 * @return
	 */
	public static Resultado leerEnGPXDatosHolux241(String ficheroDestino) {
		Process child = null;
		Resultado resultado = new Resultado(true,"Leidos los datos y copiados al fichero: "+ficheroDestino);
		String command = null;
		String opciones = null;
		String textoError = null;

        command = PropertyHandler.getProperty(AMBITO_LOG,"comando","resources\\GPSBABEL\\gpsbabel.exe");
        opciones = PropertyHandler.getProperty(AMBITO_LOG,"opciones","-t -r -w -i m241 -f com"+getPuerto()+": -o gpx -F ");

        String cadena=command+" "+opciones+" \""+ficheroDestino+"\"";

        try{
            //Logger.log(AMBITO_LOG,ERROR,"VAMOS A EJECUTAR "+cadena);
            System.out.println("===> VAMOS A EJECUTAR "+cadena);

            // ejecutamos el comando
            Runtime rt = Runtime.getRuntime();
            child = rt.exec( cadena );
            
            // miramos si hay errores
            if( (textoError = getErroresProceso(child)) != null ){
            	//Logger.log(AMBITO_LOG,ERROR,"Error de comando. Miramos si es conocido... : "+textoError);

            	if( textoError.indexOf(PropertyHandler.getProperty(AMBITO_PROPERTIES,"textoErrorNecesitaReconectar","mtk_logger: This is not a MTK based GPS ! (or is it turned off ?)")) != -1 ){
            		//Logger.log(AMBITO_LOG,ERROR,"Error conocido SIGNIFICA QUE HAY QUE DESENCHUFAR Y VOLVER A ENCHUFAR POR USB EL HOLUX!!!!");
            		System.err.println("¡¡¡HAY QUE DESENCHUFAR Y VOLVER A ENCHUFAR POR USB EL HOLUX!!!! "+textoError);
            		resultado.setTexto("¡¡¡HAY QUE DESENCHUFAR Y VOLVER A ENCHUFAR POR USB EL HOLUX!!!!");
            		resultado.setOk(false);
            	}
            	else if (textoError.indexOf(PropertyHandler.getProperty(AMBITO_PROPERTIES,"textoErrorSinConectar","mtk_logger: Can't initialise port "))!=-1) {
            		//Logger.log(AMBITO_LOG,ERROR,"Error conocido SIGNIFICA QUE HAY QUE ENCHUFAR POR USB EL HOLUX!!!!");
            		System.err.println("¡¡¡HAY QUE ENCHUFAR POR USB EL HOLUX, en el puerto (com"+getPuerto()+" por ejemplo) adecuado!!!!"+textoError);
            		resultado.setTexto("¡¡¡HAY QUE ENCHUFAR POR USB EL HOLUX, en el puerto (com"+getPuerto()+" por ejemplo) adecuado!!!!");
            		resultado.setOk(false);
            	}else if (textoError.indexOf(PropertyHandler.getProperty(AMBITO_PROPERTIES,"textoErrorCorte"," error (-3)"))!=-1) {
            		//Logger.log(AMBITO_LOG,ERROR,"Error conocido SIGNIFICA QUE HAS DESCONECTADO EL CABLE MIENTRAS ESTABA LEYENDO!!!!");
            		System.err.println("¡¡¡ATENCIÓN!!! ¡¡¡HAS DESCONECTADO EL CABLE MIENTRAS ESTABA LEYENDO!!!! "+textoError);
            		resultado.setTexto("¡¡¡ATENCIÓN!!! ¡¡¡HAS DESCONECTADO EL CABLE MIENTRAS ESTABA LEYENDO!!!!");
            		resultado.setOk(false);
            	}
            	else {
            		resultado.setTexto("Se ha producido un error desconocido: "+textoError);
            		resultado.setOk(false);
            	}
            }
        }catch(Exception ex){
        	//Logger.log(AMBITO_LOG,ERROR,"ERROR al intentar obtener datos ejecutando: "+cadena+"). Ex: "+ex);
        	resultado.setTexto("ERROR al intentar obtener datos ejecutando: "+cadena+"). Ex: "+ex);
        	resultado.setOk(false);
        } finally {
        	if (child != null) {
        		try {
        			child.getInputStream().close();
        			child.getErrorStream().close();
        			child.getOutputStream().close();
        		} catch (Exception ex) {
                	//Logger.log(AMBITO_LOG,ERROR,"ERROR2 al intentar obtener datos en fichero: "+ficheroDestino+", command:"+command+", opciones: "+opciones+"). Ex: "+ex);
                	resultado.setOk(false);
        		}
        	}
        	
        	java.io.File fich=new java.io.File(ficheroDestino);
        	if (fich.exists()&&fich.length()>0) {
        		//Logger.log(AMBITO_LOG, 2, "Fichero resultado: "+ficheroDestino+" creado con éxito, tamaño="+fich.length());
        		System.out.println("Fichero resultado: "+ficheroDestino+" creado con éxito, tamaño="+fich.length());
        		resultado.setTexto("Fichero resultado: "+ficheroDestino+" creado con éxito, tamaño="+fich.length());
        		resultado.setOk(true);
        	}
        	else {
        		//Logger.log(AMBITO_LOG, 1, "Fichero resultado: "+ficheroDestino+" NO creado correctamente.");
        		System.err.println("Fichero resultado: "+ficheroDestino+" NO creado correctamente.");
        		resultado.setOk(false);
        	}
        }
        
        return resultado;
	} 
	
	/**
	 * gpsbabel -i gpx -f file.gpx -o kml -F two.kml
	 * 
	 * @return
	 */
	public static Resultado convertirGPXaKML(String ficheroOrigen, String ficheroDestino) {
		Process child = null;
		Resultado resultado = new Resultado(true,"Convertidos los datos al fichero: "+ficheroDestino);
		String command = null;
		String opcionesIN = null;
		String opcionesOUT = null;
		String textoError = null;

        command = PropertyHandler.getProperty(AMBITO_PROPERTIES,"comando","resources\\GPSBABEL\\gpsbabel.exe");
        opcionesIN = PropertyHandler.getProperty(AMBITO_PROPERTIES,"opcionesIN_GPX","-i gpx -f");
        opcionesOUT = PropertyHandler.getProperty(AMBITO_PROPERTIES,"opcionesOUT_GPX","-o kml,units=m,points=1,floating=0,trackdata=1,extrude=1,trackdirection=1 -F");

        String cadena=command+" "+opcionesIN+" \""+ficheroOrigen+"\""+" "+opcionesOUT+" \""+ficheroDestino+"\"";

        try{
            //Logger.log(AMBITO_LOG,ERROR,"VAMOS A EJECUTAR "+cadena);
            System.out.println("===> VAMOS A EJECUTAR "+cadena);

            // ejecutamos el comando
            Runtime rt = Runtime.getRuntime();
            child = rt.exec( cadena );
            
            // miramos si hay errores
            if( (textoError = getErroresProceso(child)) != null ){
            	System.out.println(textoError);
            	//Logger.log(AMBITO_LOG,ERROR,"Error de comando. Miramos si es conocido... : "+textoError);
            }
        }catch(Exception ex){
        	//Logger.log(AMBITO_LOG,ERROR,"ERROR al intentar obtener datos ejecutando: "+cadena+"). Ex: "+ex);
        	resultado.setTexto("ERROR al intentar obtener datos ejecutando: "+cadena+"). Ex: "+ex);
        	resultado.setOk(false);
        } finally {
        	if (child != null) {
        		try {
        			child.getInputStream().close();
        			child.getErrorStream().close();
        			child.getOutputStream().close();
        		} catch (Exception ex) {
        			resultado.setTexto("ERROR2 al ejecutar:"+cadena+"). Ex: "+ex);
        			resultado.setOk(false);
        		}
        	}
        	
        	java.io.File fich=new java.io.File(ficheroDestino);
        	if (fich.exists()&&fich.length()>0) {
        		//Logger.log(AMBITO_LOG, 2, "Fichero resultado: "+ficheroDestino+" creado con éxito, tamaño="+fich.length());
        		resultado.setTexto("Fichero resultado: "+ficheroDestino+" creado con éxito, tamaño="+fich.length());
        		resultado.setOk(true);
        	}
        	else {
        		//Logger.log(AMBITO_LOG, 1, "Fichero resultado: "+ficheroDestino+" NO creado correctamente.");
        		resultado.setTexto("Fichero resultado: "+ficheroDestino+" NO creado correctamente.");
        		resultado.setOk(false);
        	}
        }
        
        return resultado;
	} 

	////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * obtiene los errores de de la ejecución de un proceso
	 *
	 * @param process proceso del que se obtienen los errores
	 */
	private static String getErroresProceso(Process process){
        String currentLine = null; 
        String textoError = null;

        try {
        	BufferedReader in = new BufferedReader(  new InputStreamReader ( process.getErrorStream (  )  )  ) ;
			while(  (currentLine = in.readLine()) != null ){
				textoError += currentLine;
			}
		}catch(IOException e){
			//Logger.log(AMBITO_LOG,ERROR,"Error al obtener los errores de un proceso. Ex: "+e);
		}
        return textoError;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////

	public static int getPuerto() {
		return PropertyHandler.getIntProperty(AMBITO_PROPERTIES, "puerto", 3);
	}
	
	public static boolean setPuerto(int puerto) {
		PropertyHandler.setProperty(AMBITO_PROPERTIES, "puerto", Integer.toString(puerto));
		try {
			PropertyHandler.save(AMBITO_PROPERTIES);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static long dameContador() {
		return PropertyHandler.getLongProperty(AMBITO_PROPERTIES, "contador", 0);
	}
	public static void incrementarContador () {
		PropertyHandler.setProperty(AMBITO_PROPERTIES, "contador", Long.toString(dameContador()+1));
		try {
			PropertyHandler.save(AMBITO_PROPERTIES);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}