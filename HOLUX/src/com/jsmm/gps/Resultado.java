package com.jsmm.gps;

public class Resultado {

	private boolean ok;
	private String texto;
	public boolean isOk() {
		return ok;
	}
	public void setOk(boolean ok) {
		this.ok = ok;
	}
	public String getTexto() {
		return texto;
	}
	public void setTexto(String texto) {
		this.texto = texto;
	}
	@Override
	public String toString() {
		return "Resultado [ok=" + ok + ", texto=" + texto + "]";
	}
	public Resultado(boolean ok, String texto) {
		super();
		this.ok = ok;
		this.texto = texto;
	}
	
}
