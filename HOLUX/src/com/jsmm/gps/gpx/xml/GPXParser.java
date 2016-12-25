package com.jsmm.gps.gpx.xml;

import java.io.*;
import tid.util.*;

import com.jsmm.gps.gpx.GPXTrack;
import com.jsmm.gps.gpx.GPXTrackPoint;
import com.jsmm.gps.xml.*;

import org.xml.sax.SAXException;

/**
 * <pre>
 * Clase que hereda de <code>Parser</code>.
 * Se encarga de procesar, obtener y almacenar toda la información contenida en
 * el XML referente a la respuesta a una compra de la plataforma de RBT.
 *
 * Implementar los métodos
 * creaInformacion, addElemento, addAtributo y addCaracteres.
 *
 * </pre>
 */

public class GPXParser extends Parser {
  
  public static final String E_TRACK="trk";
  public static final String E_TRACKPOINT="trkpt";
  public static final String E_ELE="ele";
  public static final String E_TIME="time";
  public static final String E_SPEED="speed";
  public static final String A_LAT="lat";
  public static final String A_LON="lon";
  private static final Object E_NAME = "name";
  private static final Object E_DESCRIPTION = "desc";
  
  /*
   * <trkpt lat="-0.000000000" lon="0.000000000">
   *   <ele>0.000000</ele>
   *   <time>1987-01-30T03:38:08Z</time>
   *   <speed>-0.001591</speed>
   *   <name>TP000001</name>
   * </trkpt>
   */
  
  
  // valores de entrada
  private InputStream isInput;
  private GPXParserListener gpxParserListener;

  private GPXTrack gpxTrack;
  private GPXTrackPoint gpxTrackPoint;
  private boolean bPrimero=true; 
  
  /**
   * Constructor.
   *
   * Recibe un InputStream que ha de parsear.
   * El objeto resultado contendra toda la información, no solo la propia de la
   * interfaz seleccionada.
   *
   * @param isInput Objeto de InputStream (o uno que hereda de él) que contiene la información a parsear.
   */
  public GPXParser(InputStream isInput, GPXParserListener gpxParserListener) {
	  this.isInput=isInput;
	  this.gpxParserListener=gpxParserListener;
   }

  /**
   * Devuelve tras el parseado la estructura con la informacion necesaria.
   *
   * @return ResultadoPeticionPlataforma objeto ResultadoPeticionPlataforma con toda la información obtenida del parseo del documento XML.
   *
   */

  public void getInfo() {
    boolean bHacerTrim = PropertyHandler.getBooleanProperty (FICHERO,FICHERO+".PeticionParser.hacerTrim", true);
    boolean bValidarConDTD = PropertyHandler.getBooleanProperty (FICHERO,FICHERO+".PeticionParser.validarConDTD", false);
//    //Logger.log(FICHERO,2,"[" + idLog + "] " + "########### INICIO PeticionParser.getInfo() ##########");
    // procesa la información.
    if (!getInformacionStream(this.isInput, bHacerTrim, bValidarConDTD)) {
//      //Logger.log(FICHERO,2,"[" + idLog + "] " + "########### FIN CON ERROR PeticionParser.getInfo() #########");
      }
    else {
//      //Logger.log(FICHERO,2,"[" + idLog + "] " + "########### FIN CORRECTO PeticionParser.getInfo() ##########");
    }
  }

  /****************************************************************************/
  /****************************************************************************/
  /************** MÉTODOS A IMPLEMENTAR DE LA CLASE Parser ********************/
  /****************************************************************************/
  /****************************************************************************/

  /**
  * Inicializa la estructura donde se van a almacenar todos los datos a extraer.
  */
  public void creaInformacion() {
	  this.gpxTrack=new GPXTrack();
	  this.gpxTrackPoint=new GPXTrackPoint();
	  this.gpxParserListener.startDocument();
  }

  /**
  * Añade a la estructura de información un nuevo atributo "sNombreAtributo" al
  * elemento "sNombreElemento" con el valor "sValorAtributo".
  * Solo se usa para comprobar si es de tipo baja la petición, y por tanto
  *
  * @param sNombreElemento nombre del nodo actual.
  * @param sNombreElementoPadre nombre del nodo padre del nodo actual
  * @param sNombreAtributo nombre del atributo a añadir en el nodo actual.
  * @param sValorAtributo valor del atributo a añadir en el nodo actual.
  *
  * @throws SAXException lanza excepción si no tiene formato esperado
  */
  public void addAtributo(String sNombreElemento, String sNombreElementoPadre, String sNombreAtributo, String sValorAtributo) throws SAXException {
//    //Logger.log(FICHERO,6,"[" + idLog + "] " + "addAtributo("+sNombreElemento+", "+sNombreElementoPadre+", "+sNombreAtributo+", "+sValorAtributo+")");
    if (sNombreAtributo.equalsIgnoreCase(A_LAT)) {
    	this.gpxTrackPoint.setLatitude(Float.parseFloat(sValorAtributo));
    }
    else if (sNombreAtributo.equalsIgnoreCase(A_LON)) {
    	this.gpxTrackPoint.setLongitude(Float.parseFloat(sValorAtributo));
    }
  }

  /**
  * Añade a la estructura de información un nuevo elemento.
  *
  * @param sNombreElemento nombre del elemento a añadir
  * @param sNombreElementoPadre nombre del elemento padre si lo tiene, sino será null
  *
  * @throws SAXException lanza excepción si no tiene formato esperado
  *
  */
  public void addElemento(String sNombreElemento, String sNombreElementoPadre) throws SAXException {
//    //Logger.log(FICHERO,6,"[" + idLog + "] " + "addElemento("+sNombreElemento+", "+sNombreElementoPadre+")");
    if (sNombreElemento.equalsIgnoreCase(E_TRACK)) {
    	this.gpxTrack=new GPXTrack();
    }
    else if (sNombreElemento.equalsIgnoreCase(E_TRACKPOINT)) {
    	this.gpxTrackPoint = new GPXTrackPoint();
    }
  }

  /**
  * Se llama cuando se cierra el elemento.
  *
  * @param sNombreElemento nombre del elemento cerrado.
  * @param sNombreElementoPadre nombre del elemento padre de este elemento
  *
  * @throws SAXException lanza excepción si no tiene formato esperado
  */
  public void endElemento(String sNombreElemento, String sNombreElementoPadre) throws SAXException {
//    //Logger.log(FICHERO,6,"[" + idLog + "] " + "endElemento("+sNombreElemento+", "+sNombreElementoPadre+")");
    if (sNombreElemento.equals(E_TRACKPOINT)) {
    	if (bPrimero) {
    		bPrimero=false;
    		this.gpxParserListener.newTrack(gpxTrack);
    	}
    	this.gpxParserListener.newTrackPoint(this.gpxTrackPoint);
    }
    else if (sNombreElemento.equals(E_TRACK)) {
    	this.gpxParserListener.endDocument();
    }
  }

  /**
  * Añade a la estructura de información la cadena "caracteres" al
  * elemento "sNombreElemento".
  * Comprueba el "elementoPadre" que debe ser: ELEMENTO_TIPO_PETICION
  *
  * @param sNombreElemento nombre del nodo actual.
  * @param sNombreElementoPadre nombre del nodo padre del nodo actual
  * @param caracteres valor del nuevo nodo.
  *
  * @throws SAXException lanza excepción si no tiene formato esperado
  */
  public void addCaracteres(String sNombreElemento, String sNombreElementoPadre, String caracteres) throws SAXException {
	  try{
//	    //Logger.log(FICHERO,5,"[" + idLog + "] " + "addCaracteres("+sNombreElemento+", "+sNombreElementoPadre+", "+caracteres+")");
	    
	    	if ( sNombreElemento.equals(E_ELE)){
	    		this.gpxTrackPoint.setHeight(Float.parseFloat(caracteres));
	    	} else if ( sNombreElemento.equals(E_SPEED)){
	    		this.gpxTrackPoint.setSpeed(Float.parseFloat(caracteres));
	    	} else if ( sNombreElemento.equals(E_TIME)){
	    		this.gpxTrackPoint.setUTCtime(caracteres);
	    	} else if ( sNombreElemento.equals(E_NAME)){
	    		if (sNombreElementoPadre.equals(E_TRACK)) {
	    			this.gpxTrack.setName(caracteres);
	    		}
	    	} else if ( sNombreElemento.equals(E_DESCRIPTION)) {
	    		this.gpxTrack.setDescription(caracteres);
	    	}
	  } catch ( Exception ex){
			//Logger.log(FICHERO,2,"[" + idLog + "]  Error : " + ex.getMessage() +"." +sNombreElemento + "-"+sNombreElementoPadre +"-"+caracteres);
	  }
   }  
}