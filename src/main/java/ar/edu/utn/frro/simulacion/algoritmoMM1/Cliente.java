package ar.edu.utn.frro.simulacion.algoritmoMM1;

public class Cliente {

	private long tiempoArribo;
	private long tiempoEnCola;
	private long tiempoEntradaAlServidor;
	private long tiempoPartida;

	public long getTiempoArribo() {
		return tiempoArribo;
	}

	public void setTiempoArribo(long tiempoArribo) {
		this.tiempoArribo = tiempoArribo;
	}

	public long getTiempoEnCola() {
		return tiempoEnCola;
	}

	public void setTiempoEnCola(long tiempoEnCola) {
		this.tiempoEnCola = tiempoEnCola;
	}

	public long getTiempoEntradaAlServidor() {
		return tiempoEntradaAlServidor;
	}

	public void setTiempoEntradaAlServidor(long tiempoEntradaAlServidor) {
		this.tiempoEntradaAlServidor = tiempoEntradaAlServidor;
	}

	public long getTiempoPartida() {
		return tiempoPartida;
	}

	public void setTiempoPartida(long tiempoPartida) {
		this.tiempoPartida = tiempoPartida;
	}
}
