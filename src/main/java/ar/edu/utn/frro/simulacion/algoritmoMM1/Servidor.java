package ar.edu.utn.frro.simulacion.algoritmoMM1;

import java.util.ArrayList;

public class Servidor {
	
	public enum ESTADO{
		OCUPADO, LIBRE
	}
	
	public ESTADO estado; 
	public int reloj; 
	public int tiempoDeOcupacion;
	public int tiempoDeInicioOcupacion;
	
	public ArrayList<Evento> listaDeEventos = new ArrayList<Evento>();
	public ArrayList<Cliente> cola = new ArrayList<Cliente>(); 
	public ArrayList<Cliente> clientesAtendidos = new ArrayList<Cliente>();
	
	private Evento proximoEvento = null;

	public Evento goToProximoEvento(){
		int diferencia = Integer.MAX_VALUE; 
		int i = 0;
		int eventoActivo = 0;
		for (Evento evento : listaDeEventos){
			
			//Si hay solo un evento devuelvo ese
			if (listaDeEventos.size()==1){ 
				eventoActivo = 0;
			} else {
				
				//Checkeo si el evento ya fue atendido
				if (evento.atendido==false){
					int nuevaDiferencia = evento.tiempoDeOcurrencia - reloj;
					if (nuevaDiferencia < diferencia){
						diferencia = nuevaDiferencia;
						eventoActivo = i; 
					}else if (nuevaDiferencia == diferencia){
						//Si son eventos simultaneos priorizo la partida
						if (listaDeEventos.get(eventoActivo).tipo==Evento.TIPO.ARRIBO && evento.tipo==Evento.TIPO.PARTIDA){
							diferencia = nuevaDiferencia;
							eventoActivo = i;
						}
					}
				}
			}
			
			i++;
		}
						
		proximoEvento = listaDeEventos.get(eventoActivo);
		reloj = proximoEvento.tiempoDeOcurrencia;
		listaDeEventos.get(eventoActivo).atendido = true;	

		return proximoEvento; //devuelvo el proximo evento 
	}
	
}
