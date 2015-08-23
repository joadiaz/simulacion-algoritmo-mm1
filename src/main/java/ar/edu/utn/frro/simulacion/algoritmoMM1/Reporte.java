package ar.edu.utn.frro.simulacion.algoritmoMM1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Fernando on 8/23/2015.
 */
public class Reporte {

    private static Logger LOG = LoggerFactory.getLogger(Reporte.class);

    private List<CronologiaEvento> cronologiaEventos;
    private List<CronologiaClienteAtendido> cronologiaClientesAtendidos;
    private long tiempoPromedioEntreArribos;
    private long tiempoPromedioEnCola;
    private double tiempoUsoServidor;
    private long tiempoPromedioEnServicio;
    private long maximoClientesEnCola;
    private long promedioElementosEnCola;
    private long promedioElementosEnSistema;

    public boolean addNewCronologiaEvento(CronologiaEvento cronologiaEvento) {
        if(cronologiaEventos == null) {
            cronologiaEventos = new ArrayList<CronologiaEvento>();
        }
        return cronologiaEventos.add(cronologiaEvento);
    }

    public boolean addNewCronologiaClienteAtendido(CronologiaClienteAtendido cronologiaClienteAtendido) {
        if(cronologiaClientesAtendidos == null) {
            cronologiaClientesAtendidos = new ArrayList<CronologiaClienteAtendido>();
        }
        return cronologiaClientesAtendidos.add(cronologiaClienteAtendido);
    }

    public void setTiempoPromedioEntreArribos(long tiempoPromedioEntreArribos) {
        this.tiempoPromedioEntreArribos = tiempoPromedioEntreArribos;
    }

    public void setTiempoPromedioEnCola(long tiempoPromedioEnCola) {
        this.tiempoPromedioEnCola = tiempoPromedioEnCola;
    }

    public void setTiempoUsoServidor(double tiempoUsoServidor) {
        this.tiempoUsoServidor = tiempoUsoServidor;
    }

    public void setTiempoPromedioEnServicio(long tiempoPromedioEnServicio) {
        this.tiempoPromedioEnServicio = tiempoPromedioEnServicio;
    }

    public void setMaximoClientesEnCola(long maximoClientesEnCola) {
        this.maximoClientesEnCola = maximoClientesEnCola;
    }

    public void setPromedioElementosEnCola(long promedioElementosEnCola) {
        this.promedioElementosEnCola = promedioElementosEnCola;
    }

    public void setPromedioElementosEnSistema(long promedioElementosEnSistema) {
        this.promedioElementosEnSistema = promedioElementosEnSistema;
    }

    @Override
    public String toString() {

        return
                "Tiempo promedio entre arribos," + tiempoPromedioEntreArribos + "\n" +
                "Tiempo promedio en cola," + tiempoPromedioEnCola + "\n" +
                "Tiempo de uso del servidor," + tiempoUsoServidor + "%\n" +
                "Tiempo promedio en Servicio," + tiempoPromedioEnServicio + "\n" +
                "Máximo de clientes en cola," + maximoClientesEnCola + "\n" +
                "Promedio elementos en cola," + promedioElementosEnCola + "\n" +
                "Promedio elementos en Sistema," + promedioElementosEnSistema + "\n";
    }

    /**
     *
     */
    public final class CronologiaEvento {
        private Evento.Tipo tipoEvento;
        private long tiempoOcurrencia;
        private boolean atendido;

        public CronologiaEvento(Evento.Tipo tipoEvento, long tiempoOcurrencia, boolean atendido) {
            this.tipoEvento = tipoEvento;
            this.tiempoOcurrencia = tiempoOcurrencia;
            this.atendido = atendido;
        }

        @Override
        public String toString() {
            return tipoEvento + "," + tiempoOcurrencia + "," + atendido + "\n";
        }
    }

    /**
     *
     */
    public final class CronologiaClienteAtendido {
        private long tiempoArribo;
        private long tiempoEntradaAlServidor;
        private long tiempoPartida;
        private long tiempoEnCola;

        public CronologiaClienteAtendido(long tiempoArribo, long tiempoEntradaAlServidor, long tiempoPartida, long tiempoEnCola) {
            this.tiempoArribo = tiempoArribo;
            this.tiempoEntradaAlServidor = tiempoEntradaAlServidor;
            this.tiempoPartida = tiempoPartida;
            this.tiempoEnCola = tiempoEnCola;
        }

        @Override
        public String toString() {
            return tiempoArribo + "," + tiempoEntradaAlServidor + "," + tiempoPartida + "," + tiempoEnCola  + "\n";
        }
    }

    public void printReportToCSVFile(String fileName)
    {
        try
        {
            FileWriter writer = new FileWriter(fileName);
            writer.append(this.toString());

            StringBuilder sbCronologiaEvento = new StringBuilder("Tipo de Evento,Tiempo de Ocurrencia,Atendido\n");
            for (CronologiaEvento cronologiaEvento : cronologiaEventos) {
                sbCronologiaEvento.append(cronologiaEvento.toString());
            }
            writer.append(sbCronologiaEvento.toString());

            StringBuilder sbCronologiaClienteAtendido = new StringBuilder("Tiempo arribo,Tiempo entrada al Servidor,Tiempo de Partida,Tiempo en cola\n");
            for (CronologiaClienteAtendido cronologiaClientesAtendido : cronologiaClientesAtendidos) {
                sbCronologiaClienteAtendido.append(cronologiaClientesAtendido.toString());
            }
            writer.append(sbCronologiaClienteAtendido.toString());

            writer.flush();
            writer.close();

        } catch(Exception e) {
            LOG.warn("Error generando archivo CSV de reporte de simulacion", e);
        }
    }
}