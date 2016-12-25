package com.jsmm.gps;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;

import com.jsmm.gps.gpx.GPXFileReaderXML;
import com.jsmm.gps.gpx.ResultadoGPXFileReader;
import com.jsmm.gps.kml.KMLFileReaderText;

public class Ventana extends JFrame implements ActionListener{

	private static final String SUFIJO_ORIGINAL = "_original_extraido_el_";

	private static final String FOTO_HOLUX = "resources//holux-m-241.jpg";

	private static final long serialVersionUID = 37894713859723L;

	private static final String EXTENSION_KML = ".kml";

	private static final String EXTENSION_GPX = ".gpx";

	private static final String SUFIJO_VIAJES = "_VIAJE_";

	private static final String FICHERO_TEMPORAL = "data.bin";

	private static final String PATH_BASE_RESULTADOS="RESULTADOS\\";

	private static final SimpleDateFormat FORMATO_FECHA_FICHERO = new java.text.SimpleDateFormat("ddMMyyyy-HHmmss");
	private static final Color COLOR_HOLUX = new Color(255,209,72);

	///////////////////////////////////////////////////////////////////////////////////////

	JLabel labelImagen;
	JComboBox comboPuerto;
	JTextField fieldResultado;
	JLabel labelPuerto;
	JLabel labelResultado;
	JButton botonEjecutar;
	JLabel labelBarraProgreso;
	JProgressBar barraProgreso;
	private boolean esPrincipio=true;
	private int contadorTimer=0;
	
	int puerto;
	
	Timer timer;

	///////////////////////////////////////////////////////////////////////////////////////

	public Ventana(int puerto){
		super("Holux M-241 por USB en COM"+puerto);
		this.puerto=puerto;
		
		comboPuerto = new JComboBox();
		for (int i=0; i<20; i++) {
			comboPuerto.addItem("COM"+i);
		}
		comboPuerto.setSelectedIndex(puerto);
		
		fieldResultado = new JTextField();
		fieldResultado.setEditable(false);
		fieldResultado.setEnabled(false);
		fieldResultado.setHorizontalAlignment(JTextField.CENTER);
		
		labelPuerto = new JLabel("Puerto");
		labelResultado = new JLabel("Información");
		labelResultado.setEnabled(false);

		labelBarraProgreso = new JLabel("Progreso: ");
		labelBarraProgreso.setEnabled(false);
		barraProgreso = new JProgressBar(0, 100);
		barraProgreso.setString("0%");
		barraProgreso.setEnabled(false);
		
		botonEjecutar = new JButton("");
		botonEjecutar.addActionListener(this);
		botonEjecutar.setFocusable(true);
				
		labelImagen = new JLabel();
		labelImagen.setIcon(new ImageIcon(FOTO_HOLUX));
		
		JPanel panel= new JPanel();
		panel.add(labelPuerto);
		panel.add(comboPuerto);
		panel.add(labelResultado);
		panel.add(fieldResultado);
		panel.add(labelBarraProgreso);
		panel.add(barraProgreso);
		panel.add(botonEjecutar);

		comboPuerto.setFocusable(false);
		barraProgreso.setFocusable(false);
		barraProgreso.setStringPainted(true);
		fieldResultado.setFocusable(false);

		botonEjecutar.requestFocus();
		botonEjecutar.setFocusCycleRoot(true);
		botonEjecutar.setFocusPainted(true);
		
		JPanel panelCompleto = new JPanel();
		BoxLayout layoutCompleto = new BoxLayout(panelCompleto,0);
		panelCompleto.setLayout(layoutCompleto);
		panelCompleto.setBackground(Color.white);
		panelCompleto.add(labelImagen);
		panelCompleto.add(panel);
		
		GridBagLayout gbLayout=new GridBagLayout();
		panel.setLayout(gbLayout);
		ponGriBagConstraints(gbLayout,labelPuerto       , 0, 0, 1, 1, 0,1,GridBagConstraints.NONE,GridBagConstraints.EAST);
		ponGriBagConstraints(gbLayout,comboPuerto       , 0, 1, 1, 1, 0,19,GridBagConstraints.NONE,GridBagConstraints.WEST);
		ponGriBagConstraints(gbLayout,labelResultado    , 1, 0, 1, 1, 0,0,GridBagConstraints.NONE,GridBagConstraints.EAST);
		ponGriBagConstraints(gbLayout,fieldResultado    , 1, 1, 1, 1, 0,0,GridBagConstraints.HORIZONTAL,GridBagConstraints.WEST);
		ponGriBagConstraints(gbLayout,labelBarraProgreso, 2, 0, 1, 1, 0,0,GridBagConstraints.NONE,GridBagConstraints.EAST);
		ponGriBagConstraints(gbLayout,barraProgreso     , 2, 1, 1, 1, 0,0,GridBagConstraints.BOTH,GridBagConstraints.WEST);
		ponGriBagConstraints(gbLayout,botonEjecutar     , 3, 0, 1, 2, 0,0,GridBagConstraints.NONE,GridBagConstraints.CENTER);

		panel.setBackground(Color.white);
		panel.setBorder(BorderFactory.createMatteBorder(5,5,5,5,COLOR_HOLUX));
		setContentPane(panelCompleto);

		WindowListener l =
			new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.err.println("ventana cerrada");
				System.exit(0);
			}
		};
		addWindowListener(l);
		
		resetearContador();
	}

	///////////////////////////////////////////////////////////////////////////////////////

	private static void ponGriBagConstraints(GridBagLayout gbLayout, Component comp, int fila, int columna, int numfilas, int numcolumnas, int tamanoRelativoFila, int tamanoRelativoColumna, int fill, int anchor) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = columna;
		gbc.gridy = fila;
		gbc.gridwidth = numcolumnas;
		gbc.gridheight = numfilas;
		gbc.weightx = tamanoRelativoColumna;
		gbc.weighty = tamanoRelativoFila; 
		gbc.fill = fill; 
		gbc.anchor = anchor; 

		gbc.insets=new Insets(5, 5, 5, 5);
		gbc.ipady=25;

		gbLayout.setConstraints(comp, gbc);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////

	public void actionPerformed(ActionEvent evento) {
		Object fuente=evento.getSource();
		if(fuente == this.botonEjecutar){
			this.pulsadoBotonEjecutar();
		}
		else {
			this.contadorTimer++;
			if (this.esPrincipio) {
				this.esPrincipio=false;
				new Thread(new Runnable() {
			        public void run() {
			        	ejecuta();
			        }
				}).start();
			}
			else {
				this.barraProgreso.setValue(this.contadorTimer%100);
				long tam=getTamanoTemporal();
				this.barraProgreso.setString("Creando el fichero temporal: "+FICHERO_TEMPORAL+"="+tam+" KB.");
			}
			
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	public void setResultado(Resultado resultado) {
		this.fieldResultado.setText(resultado.getTexto());
		if (resultado.isOk()) {
			this.fieldResultado.setBackground(Color.green);
		}
		else {
			this.fieldResultado.setBackground(Color.red);
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	
	private void resetearContador() {
		this.esPrincipio=true;
		this.contadorTimer=0;
		this.barraProgreso.setValue(0);
		this.fieldResultado.setBackground(Color.white);
	}

	private void pulsadoBotonEjecutar() {
		this.resetearContador();

		this.setTitle("Holux M-241 por USB en COM"+puerto+". Extracción #"+(GPSBabelControl.dameContador()+1));
		// importante no olvidarlo por si han cambiado el puerto.
		this.puerto=this.comboPuerto.getSelectedIndex();
		
		this.botonEjecutar.setEnabled(false);
		this.botonEjecutar.setText("Espera a que Finalice...");
		this.barraProgreso.setEnabled(true);
		this.labelPuerto.setEnabled(false);
		this.labelBarraProgreso.setEnabled(false);
		this.comboPuerto.setEnabled(false);
		this.labelBarraProgreso.setEnabled(true);
		this.labelResultado.setEnabled(true);
		this.fieldResultado.setEnabled(true);
		this.fieldResultado.setText("|                                             Leyendo por USB en el puerto "+(String)this.comboPuerto.getSelectedItem()+"                                             |");
		
		// ejecuta todo lo necesario
		this.timer = new Timer(100,this);
		this.timer.start();
	}

	private void permitirNuevoIntento() {
		this.timer.stop();
		this.comboPuerto.setEditable(true);
		this.comboPuerto.setEnabled(true);
		this.botonEjecutar.setEnabled(true);
		this.botonEjecutar.setText("PULSAME PARA VOLVER A INTENTARLO");
		this.borrarTemporal();
	}

	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	private long getTamanoTemporal() {
		File file= new File(FICHERO_TEMPORAL);
		if (file.exists()) {
			return file.length()/1024;
		}
		else {
			return 0;
		}
	}

	private void borrarTemporal() {
		File fichABorrar = new File (FICHERO_TEMPORAL);
		if (fichABorrar.exists()) {
			fichABorrar.delete();
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Ejectuta todos los procesos y va informando a la Ventana de loque debe mostrar
	 */
	private void ejecuta() {
		long ini=System.currentTimeMillis();

		// guardamos lo último elegido en properties
		GPSBabelControl.setPuerto(this.puerto);

		this.borrarTemporal();
		GPSBabelControl.incrementarContador();
		
		File dir = new File(PATH_BASE_RESULTADOS);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		if (!dir.canWrite()) {
			System.out.println("===> NO PUEDO ESCRIBIR EN EL DIRECTORIO: "+dir);
		}
		String prefijoNombreFichero=GPSBabelControl.dameContador()+"_HOLUX_241";
		String nombreFicheroOrigen=prefijoNombreFichero+SUFIJO_ORIGINAL+FORMATO_FECHA_FICHERO.format(new Date());
		
		System.err.println("===> ################################################################################");
		System.err.println("===> ######                 VAMOS A LEER POR USB EL HOLUX M241                 ######");
		System.err.println("===> ################################################################################");
		Resultado resultado=GPSBabelControl.leerEnGPXDatosHolux241(PATH_BASE_RESULTADOS+nombreFicheroOrigen+EXTENSION_GPX);
		if (!resultado.isOk()) {
			System.err.println("===> Finalizamos con error tras haber intentado usar GPSBABEL para leer los datos del Holux por USB.");
			this.setResultado(resultado);
		}
		else {
			this.fieldResultado.setText("Convirtiendo el GPX obtenido a KML");
			resultado=GPSBabelControl.convertirGPXaKML(PATH_BASE_RESULTADOS+nombreFicheroOrigen+EXTENSION_GPX,PATH_BASE_RESULTADOS+nombreFicheroOrigen+EXTENSION_KML);

			System.err.println("===> ################################################################################");
			System.err.println("===> ######              VAMOS A REVISAR Y LIMPIAR EL GPX CREADO               ######");
			System.err.println("===> ################################################################################");
			this.fieldResultado.setText("Limpiando el GPX obtenido para quitar errores");
			
			GPXFileReaderXML gpxFileReader = new GPXFileReaderXML(PATH_BASE_RESULTADOS+prefijoNombreFichero+SUFIJO_VIAJES);
			ResultadoGPXFileReader resul=gpxFileReader.readGPXFile(PATH_BASE_RESULTADOS+nombreFicheroOrigen+EXTENSION_GPX);
			int numViajes=resul.getNumViajes();
			for (int i=1; i<=numViajes; i++) {
				this.fieldResultado.setText("Convirtiendo el GPX limpio del viaje: "+i+" a KML");
				String prefijoNombreViaje=((String)resul.getNombreViajes().get(i-1));
				prefijoNombreViaje=prefijoNombreViaje.substring(0,prefijoNombreViaje.lastIndexOf(EXTENSION_GPX));
				resultado=GPSBabelControl.convertirGPXaKML(prefijoNombreViaje+EXTENSION_GPX,prefijoNombreViaje+EXTENSION_KML);
				
				KMLFileReaderText.modifyNameAndDesc(prefijoNombreViaje+EXTENSION_KML, (GPSData)resul.getPrimerosPuntos().get(i-1));
				
				this.setResultado(resultado);
				if (resultado.isOk()) {
				}
				else {
					System.err.println("===> No se ha podido convertir el gpx del VIAJE "+i+" a kml");
				}
			}
			System.out.flush();
			System.out.flush();
			System.err.println("===> ################################################################################");
			System.err.println("===> ###### FIN DE PROCESADO CORRECTO!. YA PUEDES DESCONECTAR EL HOLUX DEL USB ######");
			System.err.println("===> ################################################################################");
			this.setResultado(new Resultado(true,"¡FIN OK!. YA PUEDES DESCONECTAR EL HOLUX DEL USB"));

			this.barraProgreso.setValue(0);
			this.barraProgreso.setString("Se han generado "+resul.getNumViajes()+" viajes. "+resul.getNumPuntosOK()+" puntos. Eliminados "+resul.getNumPuntosERROR()+" puntos erróneos.");
		}
		this.permitirNuevoIntento();
		
		System.err.println("===> Tiempo total empleado: "+((System.currentTimeMillis()-ini)/1000f)+" segundos.");
		System.err.println("===> ################################################################################");
	}

	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	public static void main(String[] args){
		Ventana nueva = new Ventana(GPSBabelControl.getPuerto());
		nueva.setVisible(true);
		nueva.setSize(800,280);
		nueva.setResizable(false);
		nueva.pulsadoBotonEjecutar();
	}
}