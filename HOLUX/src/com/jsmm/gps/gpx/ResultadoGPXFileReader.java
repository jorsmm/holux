package com.jsmm.gps.gpx;

import java.util.ArrayList;

public class ResultadoGPXFileReader {
	private int numViajes;
	private int numPuntosOK;
	private int numPuntosERROR;
	private ArrayList nombreViajes;
	private ArrayList primerosPuntos;
	
	public ArrayList getPrimerosPuntos() {
		return primerosPuntos;
	}
	public void setPrimerosPuntos(ArrayList primerosPuntos) {
		this.primerosPuntos = primerosPuntos;
	}
	public ArrayList getNombreViajes() {
		return nombreViajes;
	}
	public void setNombreViajes(ArrayList nombreViajes) {
		this.nombreViajes = nombreViajes;
	}
	public int getNumViajes() {
		return numViajes;
	}
	public void setNumViajes(int numViajes) {
		this.numViajes = numViajes;
	}
	public int getNumPuntosOK() {
		return numPuntosOK;
	}
	public void setNumPuntosOK(int numPuntosOK) {
		this.numPuntosOK = numPuntosOK;
	}
	public int getNumPuntosERROR() {
		return numPuntosERROR;
	}
	public void setNumPuntosERROR(int numPuntosERROR) {
		this.numPuntosERROR = numPuntosERROR;
	}
	public ResultadoGPXFileReader(ArrayList primerosPuntos, ArrayList nombreViajes, int numViajes, int numPuntosOK,
			int numPuntosERROR) {
		super();
		this.primerosPuntos=primerosPuntos;
		this.nombreViajes = nombreViajes;
		this.numViajes = numViajes;
		this.numPuntosOK = numPuntosOK;
		this.numPuntosERROR = numPuntosERROR;
	}
	@Override
	public String toString() {
		return "ResultadoGPXFileReader [numPuntosERROR=" + numPuntosERROR
				+ ", numPuntosOK=" + numPuntosOK + ", numViajes="
				+ numViajes + "]";
	}	
}