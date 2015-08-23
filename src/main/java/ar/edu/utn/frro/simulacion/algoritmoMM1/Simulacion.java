package ar.edu.utn.frro.simulacion.algoritmoMM1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Simulacion {

    private static Logger LOG = LoggerFactory.getLogger(Simulacion.class);

    // Definicion de variables del sistema
    private int condicionFin;
    private Servidor servidor;
    private int numeroMaximoEnCola;

    /**
     * Algoritmo de Inicializacion
     */
    public Simulacion(int condicionFin) {
        //LOG.info("Iniciando simulacion --> Condicion de fin de simulacion: {}", CONDICION_FIN);
        if(condicionFin < 0) {
            throw new IllegalStateException("La condicion de fin de la simulacion debe ser mayor a 0");
        }

        this.condicionFin = condicionFin;
        this.numeroMaximoEnCola = 0;

        //Creo el primer arribo
        Evento eventoInicial = new Evento(Evento.Tipo.ARRIBO);
        eventoInicial.setTiempoDeOcurrencia(Generador.nextArribo());

        //Inicializo el servidor
        this.servidor = new Servidor(eventoInicial);
    }

    /**
     * Algoritmo Principal
     */
    public Reporte iniciarSimulacion() {

        while (servidor.getReloj() < condicionFin) {
            Evento proximoEvento = servidor.runProximoEvento();
            //LOG.info("Reloj: {} --> Procesando {}", servidor.reloj, proximoEvento.tipo);

            if (proximoEvento.getTipo().equals(Evento.Tipo.ARRIBO)) {
                procesarArribo();
            } else {
                procesarPartida();
            }
        }

        Reporte reporte = generarReporte();
        LOG.info("=======================\n" + reporte.toString());

        return reporte;
    }

    /**
     * Algoritmo Arribo
     */
    private void procesarArribo() {

        // Arriba un nuevo cliente
        Cliente nuevoCliente = new Cliente();
        nuevoCliente.setTiempoArribo(servidor.getReloj());

        if (servidor.getEstado().equals(Servidor.Estado.OCUPADO)) {
            //El cliente entra a la cola
            servidor.addClienteEnCola(nuevoCliente);

            //LOG.info("Cliente agregado a la cola; clientes en cola {}", servidor.cola.size());

            if (servidor.getCola().size() > numeroMaximoEnCola) {
                numeroMaximoEnCola = servidor.getCola().size();
            }


        } else {

            //El servidor se ocupa
            servidor.setEstado(Servidor.Estado.OCUPADO);

            //Genero el evento de partida
            Evento partida = new Evento(Evento.Tipo.PARTIDA);
            partida.setTiempoDeOcurrencia(Generador.nextPartida() + servidor.getReloj());

            //El cliente entra al servidor
            nuevoCliente.setTiempoEntradaAlServidor(servidor.getReloj());
            nuevoCliente.setTiempoEnCola(0);
            nuevoCliente.setTiempoPartida(partida.getTiempoDeOcurrencia());

            //Agrego la información al servidor
            servidor.setTiempoDeInicioOcupacion(servidor.getReloj());
            servidor.addClienteAtendido(nuevoCliente);
            servidor.addEvento(partida);

            //LOG.info("Un cliente entró al servidor, la partidá será en {}", partida.tiempoDeOcurrencia);
        }

        //Genero el proximo arribo
        Evento arribo = new Evento(Evento.Tipo.ARRIBO);
        arribo.setTiempoDeOcurrencia(Generador.nextArribo() + servidor.getReloj());
        servidor.addEvento(arribo);

        //LOG.info("Genero el proximo arribo, será en {}", arribo.tiempoDeOcurrencia);
    }

    /**
     * Algoritmo Partida
     */
    private void procesarPartida() {

        if (servidor.getCola().isEmpty()) {

            //El servidor se desocupa
            servidor.setEstado(Servidor.Estado.LIBRE);

            //Acumulo la ocupación del servidor
            servidor.setTiempoDeOcupacion(servidor.getTiempoDeOcupacion() + (servidor.getReloj() - servidor.getTiempoDeInicioOcupacion()));

            //LOG.info("El servidor se desocupa");

        } else {

            //Tomo el primer item de la cola y lo agrego al servidor
            Cliente clienteEnServicio = servidor.getCola().get(0);
            clienteEnServicio.setTiempoEntradaAlServidor(servidor.getReloj());
            clienteEnServicio.setTiempoEnCola(clienteEnServicio.getTiempoArribo() - clienteEnServicio.getTiempoEntradaAlServidor());
            servidor.getCola().remove(0);

            //Genero el evento de partida
            Evento partida = new Evento(Evento.Tipo.PARTIDA);
            partida.setTiempoDeOcurrencia(Generador.nextPartida() + servidor.getReloj());
            clienteEnServicio.setTiempoPartida(partida.getTiempoDeOcurrencia());

            servidor.addClienteAtendido(clienteEnServicio);
            servidor.addEvento(partida);

            //LOG.info("El cliente que entró en {} se mueve a la cola. La partida será en {}", clienteEnServicio.tiempoArribo, partida.tiempoDeOcurrencia);

        }

        servidor.setTiempoDeInicioOcupacion(servidor.getReloj());
    }

    /**
     * Algoritmo Reporte
     */
    private Reporte generarReporte() {
        Reporte reporte = new Reporte();

        long tiempoEnColaTotal = 0;
        long tiempoTotalEntreArribos = 0;
        long ultimoArribo = 0;

        for (Evento evento : servidor.getListaDeEventos()) {
            reporte.addNewCronologiaEvento(reporte.new CronologiaEvento(evento.getTipo(), evento.getTiempoDeOcurrencia(), evento.isAtendido()));

            //Calculo el tiempo total entre arribos
            if (evento.getTipo().equals(Evento.Tipo.ARRIBO)) {
                if (ultimoArribo == 0) {
                    ultimoArribo = evento.getTiempoDeOcurrencia();
                    tiempoTotalEntreArribos = ultimoArribo;

                } else {
                    tiempoTotalEntreArribos += (evento.getTiempoDeOcurrencia() - ultimoArribo);
                    ultimoArribo = evento.getTiempoDeOcurrencia();
                }
            }
        }

        servidor.setTiempoDeOcupacion(0);

        for (Cliente cliente : servidor.getClientesAtendidos()) {
            reporte.addNewCronologiaClienteAtendido(reporte.new CronologiaClienteAtendido(
                    cliente.getTiempoArribo(),
                    cliente.getTiempoEntradaAlServidor(),
                    cliente.getTiempoPartida(),
                    (cliente.getTiempoEntradaAlServidor() - cliente.getTiempoArribo())));

            tiempoEnColaTotal += cliente.getTiempoEntradaAlServidor() - cliente.getTiempoArribo();
            servidor.setTiempoDeOcupacion(servidor.getTiempoDeOcupacion() + (cliente.getTiempoPartida() - cliente.getTiempoEntradaAlServidor()));
        }

        //Ignoro el uso de servidor despues de CONDICION_FIN
        if (servidor.getTiempoDeOcupacion() > condicionFin) {
            servidor.setTiempoDeOcupacion(servidor.getTiempoDeOcupacion() - (servidor.getTiempoDeOcupacion() - condicionFin));
        }

        int totalClientes = servidor.getClientesAtendidos().size();
        double utilizacionDelServidor = servidor.getTiempoDeOcupacion() / (double) condicionFin * 100;

        reporte.setTiempoPromedioEntreArribos(tiempoTotalEntreArribos / totalClientes);
        reporte.setTiempoPromedioEnCola(tiempoEnColaTotal / totalClientes);
        reporte.setTiempoUsoServidor(utilizacionDelServidor);
        reporte.setTiempoPromedioEnServicio(servidor.getTiempoDeOcupacion() / totalClientes);
        reporte.setMaximoClientesEnCola(numeroMaximoEnCola);
        reporte.setPromedioElementosEnCola(servidor.getElementosEnCola() / servidor.getReloj());
        reporte.setPromedioElementosEnSistema(servidor.getElementosEnSistema() / servidor.getReloj());

        return reporte;
    }

}
