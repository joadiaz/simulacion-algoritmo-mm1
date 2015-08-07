package ar.edu.utn.frro.simulacion.algoritmoMM1;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Simulacion {

	private static Logger LOG = LoggerFactory.getLogger(Simulacion.class);
	private static final DecimalFormat DF = new DecimalFormat("0.0");

	/**
	 * Condicion de fin de la simulación.
	 */
	private static final int CONDICION_FIN = 50;

	/**
	 * Valor de ALFA utilizado para la generación de numeros de una distribución exponencial.
	 */
	private static final double GENERADOR_EXPONENCIAL_ALFA = 10;

	// Definicion de variables del sistema
	private int reloj;
	private boolean servidorDesocupado;
	private int n;
	private int clientesAtendidos;
	private int deltaQ;
	private int deltaD;
	private int deltaB;
	private int tiempoArribo;
	private int tiempoUltimoEvento;
	private int inicioOcupacionServidor;
	private Map<Evento, Integer> listaEventos;

	/**
	 * Algoritmo Principal
	 */
	public void iniciarSimulacion() {
		LOG.info("Iniciando simulacion --> Condicion de fin de simulacion: {}", CONDICION_FIN);
		inicializar();

		while (reloj < CONDICION_FIN) {
			Evento proximoEvento = calcularTiempos();
			LOG.info("Reloj: {} --> Procesando {}", reloj, proximoEvento);

			if (proximoEvento.equals(Evento.ARRIBO)) {
				procesarArribo();
			} else {
				procesarPartida();
			}
		}

		imprimirReporte();
	}

	/**
	 * Algoritmo de Inicialización
	 */
	private void inicializar() {
		reloj = 0;
		servidorDesocupado = Boolean.TRUE;
		n = 0;
		clientesAtendidos = 0;
		deltaQ = 0;
		deltaD = 0;
		deltaB = 0;
		tiempoArribo = 0;
		tiempoUltimoEvento = 0;
		listaEventos = new HashMap<Evento, Integer>();
		listaEventos.put(Evento.ARRIBO, 0);
		listaEventos.put(Evento.PARTIDA, Integer.MAX_VALUE); // MaxValue = Infinito
		inicioOcupacionServidor = 0;
	}

	/**
	 * Algoritmo Tiempos
	 * 
	 * @return tipo de {@link Evento evento}
	 */
	private Evento calcularTiempos() {
		int tiempoProximoArrivo = listaEventos.get(Evento.ARRIBO);
		int tiempoProximaPartida = listaEventos.get(Evento.PARTIDA);

		// Si un arribo y una partida tienen el mismo tiempo, se prioriza la partida
		if (tiempoProximoArrivo < tiempoProximaPartida) {
			reloj = tiempoProximoArrivo;
			return Evento.ARRIBO;
		} else {
			reloj = tiempoProximaPartida;
			return Evento.PARTIDA;
		}
	}

	/**
	 * Algoritmo Arribo
	 */
	private void procesarArribo() {
		if (servidorDesocupado) {
			servidorDesocupado = Boolean.FALSE;
			LOG.info("El servidor se encuentra ahora OCUPADO");

			int tiempoServicio = generarRandomExponencial(40);
			listaEventos.put(Evento.PARTIDA, reloj + tiempoServicio);
			LOG.info("La partida del cliente del servidor sera en {}", listaEventos.get(Evento.PARTIDA));

			clientesAtendidos++;
			inicioOcupacionServidor = reloj;

		} else {
			LOG.info("No se puede atender al cliente. El servidor aun se encuentra ocupado.");
			deltaQ += (reloj - tiempoUltimoEvento) * n;
			n++;
			tiempoArribo = reloj;
		}

		int tiempoProximoArribo = generarRandomExponencial(10);
		listaEventos.put(Evento.ARRIBO, reloj + tiempoProximoArribo);
		LOG.info("El proximo arribo se producira en {}", listaEventos.get(Evento.ARRIBO));

		tiempoUltimoEvento = reloj;
	}

	/**
	 * Algoritmo Partida
	 */
	private void procesarPartida() {
		if (n > 0) {

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

		tiempoUltimoEvento = reloj;
	}

	/**
	 * Algoritmo Reporte
	 */
	private void imprimirReporte() {
		BigDecimal q = BigDecimal.valueOf(deltaQ).divide(BigDecimal.valueOf(reloj), RoundingMode.HALF_UP);
		BigDecimal b = BigDecimal.valueOf(deltaB).divide(BigDecimal.valueOf(reloj), RoundingMode.HALF_UP);
		BigDecimal d = BigDecimal.valueOf(deltaD).divide(BigDecimal.valueOf(clientesAtendidos), RoundingMode.HALF_UP);

		System.out.println("Q = " + q);
		System.out.println("B = " + b);
		System.out.println("D = " + d);
	}

	/**
	 * Algoritmo Generador Nro Exponencial
	 * 
	 * @return numero entero generado para una distribución exponencial
	 */
	private int generarRandomExponencial(double lambda) {
		int generatedNumber = 0;

		// Nos aseguramos que el numero generado no sea 0
		while (generatedNumber == 0) {
			double random = Math.random();
			double x = ((-1.0 / lambda) * Math.log(random));
			generatedNumber = Integer.parseInt(DF.format(x).replace("0.", ""));
		}

		return generatedNumber;
	}

}
