package ar.edu.utn.frro.simulacion.algoritmoMM1;

public class Evento {

	public enum TIPO{
		PARTIDA, ARRIBO;
	}
	
	public TIPO tipo; 
	public int tiempoDeOcurrencia; 
	public boolean atendido; 
	
}
