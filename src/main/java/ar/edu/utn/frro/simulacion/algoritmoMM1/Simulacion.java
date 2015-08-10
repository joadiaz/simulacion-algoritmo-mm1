package ar.edu.utn.frro.simulacion.algoritmoMM1;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Simulacion {

	private static Logger LOG = LoggerFactory.getLogger(Simulacion.class);
	private static final DecimalFormat DF = new DecimalFormat("0.0");

	/**
	 * Condicion de fin de la simulaci√≥n.
	 */
	private static final int CONDICION_FIN = 100;

	/**
	 * Valor de ALFA utilizado para la generaci√≥n de numeros de una distribuci√≥n exponencial.
	 */
	private static final double lAMBDA_ARRIBOS = 5;
	private static final double lAMBDA_PARTIDAS = 7;

	// Definicion de variables del sistema
	private Servidor servidor = new Servidor();
	private int numeroMaximoEnCola = 0;
	
	/**
	 * Algoritmo Principal
	 */
	public void iniciarSimulacion() {
		LOG.info("Iniciando simulacion --> Condicion de fin de simulacion: {}", CONDICION_FIN);
		inicializar();

		while (servidor.reloj < CONDICION_FIN) {
			Evento proximoEvento = servidor.goToProximoEvento();
			LOG.info("Reloj: {} --> Procesando {}", servidor.reloj, proximoEvento.tipo);

			if (proximoEvento.tipo.equals(Evento.TIPO.ARRIBO)) {
				procesarArribo();
			} else {
				procesarPartida();
			}
		}

		imprimirReporte();
	}

	/**
	 * Algoritmo de Inicializaci√≥n
	 */
	private void inicializar() {
		
		//Inicializo el servidor
		servidor.reloj = 0;
		servidor.estado = Servidor.ESTADO.LIBRE;
		servidor.tiempoDeOcupacion = 0;
		
		//Creo el primer arribo
		Evento nuevoEvento = new Evento();
		nuevoEvento.tipo = Evento.TIPO.ARRIBO;
		nuevoEvento.tiempoDeOcurrencia = generarRandomExponencial(lAMBDA_ARRIBOS);
		
		//Lo agrego a la lista de eventos
		servidor.listaDeEventos.add(nuevoEvento);
		
	}

	/**
	 * Algoritmo Arribo
	 */
	private void procesarArribo() {
		
		if(servidor.estado == Servidor.ESTADO.OCUPADO){ 
			//El cliente entra a la cola
			Cliente clienteEnCola = new Cliente();
			clienteEnCola.tiempoArribo = servidor.reloj;			
			servidor.cola.add(clienteEnCola);
			
			LOG.info("Cliente agregado a la cola; clientes en cola {}", servidor.cola.size());
			
			if(servidor.cola.size()>numeroMaximoEnCola){
				numeroMaximoEnCola = servidor.cola.size();
			}
			
			
		} else{		
			
			//El servidor se ocupa
			servidor.estado = Servidor.ESTADO.OCUPADO;
			
			//El cliente entra al servidor
			Cliente clienteEnServicio = new Cliente();
			clienteEnServicio.tiempoArribo = servidor.reloj;
			clienteEnServicio.tiempoEntradaAlServidor = servidor.reloj;
			clienteEnServicio.tiempoEnCola = 0;
			
			//Genero el evento de partida
			Evento partida = new Evento();
			partida.tipo = Evento.TIPO.PARTIDA;
			partida.tiempoDeOcurrencia = generarRandomExponencial(lAMBDA_PARTIDAS) + servidor.reloj;
			clienteEnServicio.tiempoPartida = partida.tiempoDeOcurrencia;
			
			//Agrego la informaciÛn al servidor
			servidor.tiempoDeInicioOcupacion = servidor.reloj;
			servidor.clientesAtendidos.add(clienteEnServicio);
			servidor.listaDeEventos.add(partida);
			
			LOG.info("Un cliente entrÛ al servidor, la partid· ser· en {}", partida.tiempoDeOcurrencia);
		}
		
		//Genero el proximo arribo
		Evento arribo = new Evento();
		arribo.tipo = Evento.TIPO.ARRIBO;
		arribo.tiempoDeOcurrencia = generarRandomExponencial(lAMBDA_ARRIBOS) + servidor.reloj;
		servidor.listaDeEventos.add(arribo);
		
		LOG.info("Genero el proximo arribo, ser· en {}", arribo.tiempoDeOcurrencia);
		
			
		/*if (servidorDesocupado) { //SERVIDOR DESOCUPADO
			servidorDesocupado = Boolean.FALSE;
			LOG.info("El servidor se encuentra ahora OCUPADO");

			int tiempoServicio = generarRandomExponencial(lAMBDA_PARTIDAS);
			LOG.info("Tiempo de servicio generado: {}", tiempoServicio);
			listaEventos.put(Evento.PARTIDA, reloj + tiempoServicio);
			LOG.info("La partida del cliente del servidor sera en {}", listaEventos.get(Evento.PARTIDA));

			clientesAtendidos++;
			inicioOcupacionServidor = reloj;
			
			if( n!=0){
				n--;
			}

		} else { //SERVIDOR OCUPADO
			LOG.info("No se puede atender al cliente. El servidor aun se encuentra ocupado.");
			deltaQ += (reloj - tiempoUltimoEvento) * n;
			n++;
			tiempoArribo = reloj;
		}

		int tiempoProximoArribo = generarRandomExponencial(lAMBDA_ARRIBOS);
		LOG.info("Tiempo de arribo generado: {}", tiempoArribo);
		listaEventos.put(Evento.ARRIBO, reloj + tiempoProximoArribo);
		LOG.info("El proximo arribo se producira en {}", listaEventos.get(Evento.ARRIBO));

		tiempoUltimoEvento = reloj;*/
	}

	/**
	 * Algoritmo Partida
	 */
	private void procesarPartida() {
		
		if (servidor.cola.size()==0){
			
			//El servidor se desocupa
			servidor.estado = Servidor.ESTADO.LIBRE;
			
			//Acumulo la ocupaciÛn del servidor
			servidor.tiempoDeOcupacion += (servidor.reloj - servidor.tiempoDeInicioOcupacion);
			
			LOG.info("El servidor se desocupa");
			
		} else {
			
			//Tomo el primer item de la cola y lo agrego al servidor 
			Cliente clienteEnServicio = servidor.cola.get(0);
			clienteEnServicio.tiempoEntradaAlServidor = servidor.reloj;
			clienteEnServicio.tiempoEnCola = clienteEnServicio.tiempoArribo - clienteEnServicio.tiempoEntradaAlServidor;
			servidor.cola.remove(0);
			
			//Genero el evento de partida
			Evento partida = new Evento();
			partida.tipo = Evento.TIPO.PARTIDA;
			partida.tiempoDeOcurrencia = generarRandomExponencial(lAMBDA_PARTIDAS) + servidor.reloj;
			clienteEnServicio.tiempoPartida = partida.tiempoDeOcurrencia;
			servidor.clientesAtendidos.add(clienteEnServicio);
			servidor.listaDeEventos.add(partida);
			
			LOG.info("El cliente que entrÛ en {} se mueve a la cola. La partida ser· en {}", clienteEnServicio.tiempoArribo, partida.tiempoDeOcurrencia);
			
		}
		
		servidor.tiempoDeInicioOcupacion = servidor.reloj;
		
		/*if (n == 0) {

			int tiempoServicio = generarRandomExponencial(40);
			listaEventos.put(Evento.PARTIDA, reloj + tiempoServicio);
			LOG.info("La partida del cliente del servidor sera en {}", listaEventos.get(Evento.PARTIDA));

			clientesAtendidos++;
			deltaD += reloj + tiempoArribo;
			deltaQ += (reloj - tiempoUltimoEvento) * n;
			n--;

		} else {
			LOG.info("No hay clientes en cola. El servidor se desocupara.");
			servidorDesocupado = Boolean.TRUE;
			deltaB += reloj - inicioOcupacionServidor;
			listaEventos.put(Evento.PARTIDA, Integer.MAX_VALUE); // MaxValue = Infinito
		}

		tiempoUltimoEvento = reloj;*/
	}

	/**
	 * Algoritmo Reporte
	 */
	private void imprimirReporte() {

		int tiempoEnColaTotal = 0;
		int tiempoTotalEntreArribos = 0;
		
		LOG.info("EVENTO    |   TIEMPO DE OCURRENCIA    |    estado");
		int ultimoArribo = 0;
		for (Evento evento : servidor.listaDeEventos){
			LOG.info(evento.tipo + " | " + evento.tiempoDeOcurrencia + "  |  " + evento.atendido);
			//Calculo el tiempo total entre arribos
			if(evento.tipo==Evento.TIPO.ARRIBO){
				if (ultimoArribo == 0){
					ultimoArribo = evento.tiempoDeOcurrencia;
					tiempoTotalEntreArribos = ultimoArribo;
				}else{
					tiempoTotalEntreArribos += (evento.tiempoDeOcurrencia-ultimoArribo);
					ultimoArribo = evento.tiempoDeOcurrencia;
				}
			}
		}
		
		LOG.info("Tiempo arribo | Tiempo entrada al Servidor |  Tiempo de Partida  |  Tiempo en cola");
		servidor.tiempoDeOcupacion = 0;
		
		for (Cliente cliente : servidor.clientesAtendidos){
			LOG.info(cliente.tiempoArribo + " | " + cliente.tiempoEntradaAlServidor + " | " + cliente.tiempoPartida + " | " + (cliente.tiempoEntradaAlServidor-cliente.tiempoArribo));
			tiempoEnColaTotal += cliente.tiempoEntradaAlServidor-cliente.tiempoArribo;
			servidor.tiempoDeOcupacion += (cliente.tiempoPartida - cliente.tiempoEntradaAlServidor);
		}
		
		//Ignoro el uso de servidor despues de CONDICION_FIN
		if(servidor.tiempoDeOcupacion>CONDICION_FIN){
			servidor.tiempoDeOcupacion -= (servidor.tiempoDeOcupacion-CONDICION_FIN);
		}
		
		int totalClientes = servidor.clientesAtendidos.size();
		double utilizacionDelServidor = servidor.tiempoDeOcupacion/(double)CONDICION_FIN*100;
		LOG.info("Tiempo promedio entre arribos --> {} ", tiempoTotalEntreArribos/totalClientes);
		LOG.info("Tiempo promedio en cola: --> {} ", tiempoEnColaTotal/totalClientes);
		LOG.info("Tiempo de uso del servidor --> {} %",utilizacionDelServidor);
		LOG.info("Tiempo promedio en servicio --> {} ", servidor.tiempoDeOcupacion/totalClientes);
		LOG.info("M·ximo de clientes en cola --> {} ", numeroMaximoEnCola);			
	}

	/**
	 * Algoritmo Generador Nro Exponencial
	 * 
	 * @return numero entero generado para una distribuci√≥n exponencial
	 */
	
	private int generarRandomExponencial(double lambda) {
		int generatedNumber = 0;

		
		// Nos aseguramos que el numero generado no sea 0
		while (generatedNumber == 0) {
			double random = Math.random();
			double x = -Math.log(1.0 - random) * lambda;
			generatedNumber = (int)x;
		}

		return generatedNumber;
	}

}
