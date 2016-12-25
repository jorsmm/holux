package com.jsmm.gps.xml;

import java.io.*;
import java.util.*;
import tid.util.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

/**
 * <pre>
 * Clase abstracta que extiende de <code>DefaultHandler</code>.
 * Se encarga de procesar, obtener y almacenar toda la información contenida en
 * el XML.
 *
 * Todas las clases que extiendan a parser han de implementar los métodos
 * creaInformacion, addElemento, addAtributo y addCaracteres.
 * Se añade un método endElemento que no es necesario implementar.
 *
 * @author $Author: jsmm $. División de Servicios Avanzados de Red Móvil. TID.
 * @date $Date: 2007/10/05 11:44:26 $
 * @version $Revision: 1.1.1.1 $
 * </pre>
*/

public abstract class Parser extends DefaultHandler {

  public static final String FICHERO="parser";

  /**
   * Pila (lista FIFO) que contiene el nombre de los elementos que aún no se han
   * terminado de procesar. El elemento en el top será el elemento que se esté
   * procesando en cada momento.
   */
  public Stack<String> pilaElementos;

  /**
   * Pila (lista FIFO) que contiene las cadenas de caracteres de los distintos elementos.
   * La cadena en el top será del elemento que se esté procesando en cada momento.
   */
  public Stack<String> pilaCaracteres;

  /**
   * Indica si se hara trim en los caracteres recogidos por addCaracteres.
   * Por defecto es true (siempre se eliminan blancos).
   */
  private boolean bTrim=true;

  /**
   * Indica si se hará parseo validante usando la DTD incluida en el XML.
   * Por defecto es false (compatibilidad hacia atrás).
   */
  private boolean bValidarConDTD=false;

  /**
   * Indica si se parará el parseo cuando se detecte un error recuperaple cuando
   * se haga el parseo validante usando la DTD incluida en el XML.
   * Por defecto es true.
   * Hay tres tipos de eventos de error que se pueden producir cuando se usa
   * validacion con DTD:
   *    Error no recuperable (fatalError) ==> se para el parseo
   *    Error recuperable (error) ==> se para o no en función de este parámetro
   *    Aviso (warning) ==> no se para el parseo
   */
  private boolean bPararParseoSiErrorEnValidacion=true;

  /**
   * Inicializa la estructura donde se van a almacenar todos los datos a extraer.
   */
  public abstract void creaInformacion();

  /**
   * Añade a la estructura de información un nuevo elemento.
   *
   * @param nombreElemento nombre del elemento a añadir
   * @param nombreElementoPadre nombre del elemento padre de este elemento
   *
   * @throws SAXException
   */
  public abstract void addElemento(String nombreElemento, String nombreElementoPadre) throws SAXException;

  /**
   * Añade a la estructura de información un nuevo atributo "nombreAtributo" al
   * elemento "nombreElemento" con el valor "valorAtributo".
   *
   * @param nombreElemento nombre del elemento.
   * @param nombreElementoPadre nombre del elemento padre de este elemento
   * @param nombreAtributo nombre del atributo.
   * @param valorAtributo valor del atributo.
   *
   * @throws SAXException
   */
  public abstract void addAtributo(String nombreElemento, String nombreElementoPadre, String nombreAtributo, String valorAtributo) throws SAXException;

  /**
   * Añade a la estructura de información la cadena "caracteres" al
   * elemento "nombreElemento".
   * Cuando se llama a este método, se le pasa la cadena completa de caracteres
   * que conforma el texto del elemento. Internamente se encarga de agrupar los
   * distintos segmentos de texto en una sola cadena de "caracteres".
   *
   * @param nombreElemento nombre del elemento.
   * @param nombreElementoPadre nombre del elemento padre de este elemento
   * @param caracteres cadena completa de caracteres (todo el contenido del text)
   *
   * @throws SAXException
   */
  public abstract void addCaracteres(String nombreElemento, String nombreElementoPadre, String caracteres) throws SAXException;

  /**
   * Se llama cuando se cierra el elemento.
   *
   * @param nombreElemento nombre del elemento cerrado.
   * @param nombreElementoPadre nombre del elemento padre de este elemento
   *
   * @throws SAXException
   */
  public void endElemento(String nombreElemento, String nombreElementoPadre) throws SAXException {}

  /****************************************************************************/
  /****************************************************************************/

  /**
   * Procesa con el parser xml la información "informacion", y devuelve sus
   * resultados en una estructura, para que el contenido que llamó a este método
   * pueda recuperarlos. Se obtiene la información del fichero "nombreFichero"
   * Con bTrim se indica si se desea, cada eliminar los blancos (blancos, tabulados, retornos de carro...)
   * que se encuentre al parsear (no elimina en principio los blancos entre palabras).
   *
   * @param nombreFichero Nombre del Fichero con la información en XML.
   * @param bTrim Indica si se debe eliminar blancos en los textos recibidos o no.
   * @param bValidarConDTD Indica
   *
   * @return true si se ha parseado correctamente. false en caso contrario.
   */
  public boolean getInformacionFichero (String nombreFichero, boolean bTrim, boolean bValidarConDTD) {
    this.bValidarConDTD=bValidarConDTD;
    return (getInformacionFichero (nombreFichero, bTrim));
  }

  /**
   * Procesa con el parser xml la información "informacion", y devuelve sus
   * resultados en una estructura, para que el contenido que llamó a este método
   * pueda recuperarlos. Se obtiene la información del fichero "nombreFichero"
   * Con bTrim se indica si se desea, cada eliminar los blancos (blancos, tabulados, retornos de carro...)
   * que se encuentre al parsear (no elimina en principio los blancos entre palabras).
   *
   * @param nombreFichero Nombre del Fichero con la información en XML.
   * @param bTrim Indica si se debe eliminar blancos en los textos recibidos o no.
   *
   * @return true si se ha parseado correctamente. false en caso contrario.
   */
  public boolean getInformacionFichero (String nombreFichero, boolean bTrim) {
    this.bTrim = bTrim;
    return (getInformacionFichero (nombreFichero));
  }

  /**
   * Procesa con el parser xml la información "informacion", y devuelve sus
   * resultados en una estructura, para que el contenido que llamó a este método
   * pueda recuperarlos. Se obtiene la información del fichero "nombreFichero"
   *
   * @param nombreFichero Nombre del Fichero con la información en XML.
   *
   * @return true si se ha parseado correctamente. false en caso contrario.
   */
  public boolean getInformacionFichero (String nombreFichero) {
    try {
      return (getInformacionStream(new BufferedInputStream(new FileInputStream(nombreFichero))));
    }
    catch (IOException ex){
      //Logger.log(FICHERO,1,"getInformacionFichero: Excepción en acceso al fichero: "+ex);
      return (false);
    }
  }

  /**
   * Procesa con el parser xml la información "informacion" a partir de un canal de
   * entrada, y devuelve sus resultados en una estructura, para que el contenido que
   * llamó a este método pueda recuperarlos.
   * Con bTrim se indica si se desea, cada eliminar los blancos (blancos, tabulados, retornos de carro...)
   * que se encuentre al parsear (no elimina en principio los blancos entre palabras).
   *
   * @param informacion stream con la información del XML.
   * @param bTrim Indica si se debe eliminar blancos en los textos recibidos o no.
   * @param bValidarConDTD true indica que se haga parseo validante usando la DTD que indique el propio XML
   *
   * @return true si se ha parseado correctamente. false en caso contrario.
   */
  public boolean getInformacionStream (InputStream informacion, boolean bTrim, boolean bValidarConDTD) {
    this.bValidarConDTD=bValidarConDTD;
    return (getInformacionStream (informacion, bTrim));
  }

  /**
   * Procesa con el parser xml la información "informacion" a partir de un canal de
   * entrada, y devuelve sus resultados en una estructura, para que el contenido que
   * llamó a este método pueda recuperarlos.
   * Con bTrim se indica si se desea, cada eliminar los blancos (blancos, tabulados, retornos de carro...)
   * que se encuentre al parsear (no elimina en principio los blancos entre palabras).
   *
   * @param informacion stream con la información del XML.
   * @param bTrim Indica si se debe eliminar blancos en los textos recibidos o no.
   *
   * @return true si se ha parseado correctamente. false en caso contrario.
   */
  public boolean getInformacionStream (InputStream informacion, boolean bTrim) {
    this.bTrim = bTrim;
    return (getInformacionStream (informacion));
  }

  /**
   * Procesa con el parser xml la información "informacion" a partir de un canal de
   * entrada, y devuelve sus resultados en una estructura, para que el contenido que
   * llamó a este método pueda recuperarlos.
   *
   * @param informacion stream con la información del XML.
   *
   * @return true si se ha parseado correctamente. false en caso contrario.
   */
  public boolean getInformacionStream (InputStream informacion) {
    boolean bResult=false;

    try {
      PushbackInputStream informacionNew=new PushbackInputStream(informacion);
      bPararParseoSiErrorEnValidacion=PropertyHandler.getBooleanProperty(FICHERO,FICHERO+".pararParseoSiErrorEnValidacion",true);

      // mostrar traza de si se hace validación usando DTD
      if (this.bValidarConDTD) {
        //Logger.log(FICHERO,3,"##### PARSEADO: Validar usando DTD del XML a parsear");
        // parar en caso de error si hay validación usando DTD
        if (this.bPararParseoSiErrorEnValidacion) {
          //Logger.log(FICHERO,3,"##### PARSEADO: Parar en caso de error recuperable");
        }
        else {
          //Logger.log(FICHERO,3,"##### PARSEADO: NO Parar en caso de error recuperable");
        }
      }
      else {
        //Logger.log(FICHERO,3,"##### PARSEADO: NO Validar usando DTD del XML a parsear");
      }

      // mostrar traza de si se hace trim de los caracteres
      if (this.bTrim) {
        //Logger.log(FICHERO,3,"##### PARSEADO: Borrar espacios en blanco sobrantes al principio y fin de los textos del XML a parsear");
      }
      else {
        //Logger.log(FICHERO,3,"##### PARSEADO: No Borrar espacios en blanco sobrantes al principio y fin de los textos del XML a parsear");
      }


	  int readChar=0;
	  try {
	        while((readChar=informacionNew.read())!=-1 && readChar!='<');
	        informacionNew.unread('<');
	  }
	  catch (IOException e)	{
	        throw new IOException("ERROR EN LA ELIMINACION DE ESPACIOS EN EL PREPROCESADO DEL XML: "+e);
	  }

      
      // obtener un nuevo parser y parsear el documento
      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setValidating(bValidarConDTD);
      SAXParser saxParser = factory.newSAXParser();
      saxParser.parse(informacionNew,this);
      bResult = true;
    }
    catch (SAXException e) {
      //Logger.log(FICHERO,1,"Error en Parseado: "+e);
      bResult = false;
    }
    catch (IOException ex){
      //Logger.log(FICHERO,1,"Error en acceso a página: "+ex);
      bResult = false;
    }
    catch (ParserConfigurationException exc) {
      //Logger.log(FICHERO,1,"Error en creación de parser: "+exc);
      bResult = false;
    }
    catch (Exception e) {
      StringBuffer sbResul = new StringBuffer();
      StackTraceElement stackTrace[] = e.getStackTrace();
      for (int i=0; i< stackTrace.length; i++) {
        sbResul.append("\n\t").append(stackTrace[i].toString());
      }
      //Logger.log(FICHERO,1,"Excepción no capturada: "+e+sbResul.toString());
      bResult = false;
    }
    return (bResult);
  }

  /****************************************************************************/
  /****************************************************************************/

  /**
   * Método heredado de DefaultHandler.
   *
   * Se llama cuando se encuentra caracteres dentro de un elemento
   *
   * @param buf buffer donde se encuentran almacenados los caracteres
   * @param offset posición inicial donde se encuentran los caracteres
   * @param len tamaño de los caracteres almacenados.
   *
   * @throws SAXException
   */
  public void characters (char buf [], int offset, int len) throws SAXException {
    String s = new String(buf, offset, len);
    String sTrim = s;
    if (bTrim) {
      sTrim = s.trim();
    }
    // si sTrim (cadena s tras hacerle un trim) es distinto de vacía, se añade la cadena s original
    if (sTrim.length()>0) {
      try {
        s = (String) pilaCaracteres.pop() + s;
        // meter los caracteres a la pila
        pilaCaracteres.push(s);
      }
      // capturar excepción de pila vacia.
      // sirve solo para el primer elmento, cuando la pila está vacia.
      // no tiene padre por ser el primero, por eso genera excepción
      catch (EmptyStackException e) {
      }
    }
  }

  /**
   * Método heredado de DefaultHandler.
   *
   * Se llama cuando se encuentra el fin del documento.
   *
   * @throws SAXException
   */
  public void endDocument () throws SAXException {
    pilaElementos = null;
    pilaCaracteres = null;
    //Logger.log(FICHERO,3,"##### PARSEADO: Fin de Documento.");
  }

  /**
   * Método heredado de DefaultHandler.
   *
   * Se llama cuando se encuentra el fin de un elemento
   *
   * @param uri URI del namespace.
   * @param localName The local name (without prefix), or the empty string if Namespace processing is not being performed.
   * @param qName nombre del elemento (The qualified name (with prefix), or the empty string if qualified names are not available).
   *
   * @throws SAXException
   */
  public void endElement (String uri, String localName, String qName) throws SAXException {
	String nombreElemento = null;
    String nombreElementoPadre = null;
    // borrar el elemento de la pila de caracteres
    String s = (String)pilaCaracteres.pop();
    try {
      // sacar el nombre del elemento y de su padre
      nombreElemento = (String)pilaElementos.pop();
      if (!pilaElementos.empty()) {
        nombreElementoPadre = (String)pilaElementos.peek();
      }
      // si hay caracteres, se pasan a addcaracteres
      if (!s.equals("")) {
        // pasar caractres (texto) al elemento "name" o "nombreElemento"
        addCaracteres (nombreElemento,nombreElementoPadre,s);
      }
    }
    // capturar excepción de pila vacia.
    // sirve solo para el primer elmento, cuando la pila está vacia.
    // no tiene padre por ser el primero, por eso genera excepción
    catch (EmptyStackException e) {
    }
    endElemento (nombreElemento,nombreElementoPadre);
  }

  /**
   * Método heredado de DefaultHandler.
   *
   * Se llama cuando se encuentra el inicio del documento.
   *
   * @throws SAXException
   */
  public void startDocument () throws SAXException {
    //Logger.log(FICHERO,3,"##### PARSEADO: Comienzo de Documento.");
    // inicializa la estructura de información
    creaInformacion();
    // crear pila de elementos.
    pilaElementos = new Stack<String>();
    // crear pila de caracteres.
    pilaCaracteres = new Stack<String>();
  }

  /**
   * Método heredado de DefaultHandler.
   *
   * Se llama cuando se encuentra el inicio de un elemento.
   *
   * @param uri URI del namespace.
   * @param localName The local name (without prefix), or the empty string if Namespace processing is not being performed.
   * @param qName nombre del elemento (The qualified name (with prefix), or the empty string if qualified names are not available).
   * @param attrs lista de atributos del elemento.
   *
   * @throws SAXException
   */
  public void startElement (String uri, String localName, String qName, Attributes attrs) throws SAXException {
    String nombreElementoPadre=null;

    try {
      nombreElementoPadre = (String)pilaElementos.peek();
    }
    // capturar excepción de pila vacia.
    // sirve solo para el primer elmento, cuando la pila está vacia.
    // no tiene padre por ser el primero, por eso genera excepción
    catch (EmptyStackException e) {
    }

    // añadir un nuevo elemento (se pasa el nombre del padre tambien)
    addElemento (qName, nombreElementoPadre);
    // añadir el elemento a la pila de elementos
    pilaElementos.push(qName);
    // añadir Cadena de caracteres vacia
    pilaCaracteres.push("");

    // si tiene atributos
    if (attrs != null) {
      for (int i = 0; i < attrs.getLength (); i++) {
        // añadir un nuevo atributo al elemento.
        addAtributo (qName,nombreElementoPadre,attrs.getQName(i),attrs.getValue (i));
      }
    }
  }

  /****************************************************************************/
  /****************************************************************************/

  /**
   * Método heredado de DefaultHandler.
   *
   * Se llama cuando se encuentra un error no recuperable de validación respecto a la DTD del XML.
   *
   * @param e Exepción que contiene el error producido
   *
   * @throws SAXException
   */
  public void fatalError (SAXParseException e) throws SAXException {
    //Logger.log(FICHERO,2,"ERROR no recuperable detectado en parseado validando con la DTD: ("+e+"), lanzada SAXParseException");
    //Logger.log(FICHERO,4,"ERROR detectado en linea: "+e.getLineNumber()+". columna: "+e.getColumnNumber());
    throw e;
  }

  /**
   * Método heredado de DefaultHandler.
   *
   * Se llama cuando se encuentra un error de validación respecto a la DTD del XML.
   *
   * @param e Exepción que contiene el error producido
   *
   * @throws SAXException
   */
  public void error (SAXParseException e) throws SAXException {
    //Logger.log(FICHERO,2,"ERROR detectado en parseado validando con la DTD: ("+e+")");
    //Logger.log(FICHERO,4,"ERROR detectado en linea: "+e.getLineNumber()+". columna: "+e.getColumnNumber());
    if (this.bPararParseoSiErrorEnValidacion) {
      //Logger.log(FICHERO,3,"lanzada SAXParseException");
      throw e;
    }
    else {
      //Logger.log(FICHERO,3,"Se continua parseo sin lanzar SAXParseException");
    }
  }

  /**
   *
   * @param e Exepción que contiene el warning producido
   *
   * @throws SAXException
   */
  public void warning (SAXParseException e) throws SAXException {
    //Logger.log(FICHERO,2,"WARNING detectado en parseado validando con la DTD: ("+e+"), se continua el parsing del documento");
    //Logger.log(FICHERO,4,"WARNING detectado en linea: "+e.getLineNumber()+". columna: "+e.getColumnNumber());
  }
}