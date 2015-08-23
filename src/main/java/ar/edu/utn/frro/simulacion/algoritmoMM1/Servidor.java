package ar.edu.utn.frro.simulacion.algoritmoMM1;

import java.util.ArrayList;
import java.util.List;

public class Servidor {

    public enum Estado {
        OCUPADO, LIBRE
    }

    private Estado estado;
    private long reloj;
    private long tiempoDeOcupacion;
    private long tiempoDeInicioOcupacion;
    private long elementosEnCola;
    private long elementosEnSistema;
    private List<Evento> listaDeEventos = new ArrayList<Evento>();
    private List<Cliente> cola = new ArrayList<Cliente>();
    private List<Cliente> clientesAtendidos = new ArrayList<Cliente>();
    private Evento proximoEvento = null;

    public Servidor(Evento eventoInicial) {
        this.reloj = 0;
        this.estado = Servidor.Estado.LIBRE;
        this.tiempoDeOcupacion = 0;
        this.addEvento(eventoInicial);
    }

    public Evento runProximoEvento() {
        long diferencia = Integer.MAX_VALUE;
        int i = 0;
        int eventoActivo = 0;
        for (Evento evento : getListaDeEventos()) {

            //Si hay solo un evento devuelvo ese
            if (getListaDeEventos().size() == 1) {
                eventoActivo = 0;
            } else {

                //Checkeo si el evento ya fue atendido
                if (!evento.isAtendido()) {
                    long nuevaDiferencia = evento.getTiempoDeOcurrencia() - getReloj();
                    if (nuevaDiferencia < diferencia) {
                        diferencia = nuevaDiferencia;
                        eventoActivo = i;
                    } else if (nuevaDiferencia == diferencia) {
                        //Si son eventos simultaneos priorizo la partida
                        if (getListaDeEventos().get(eventoActivo).getTipo().equals(Evento.Tipo.ARRIBO) && evento.getTipo().equals(Evento.Tipo.PARTIDA)) {
                            diferencia = nuevaDiferencia;
                            eventoActivo = i;
                        }
                    }
                }
            }

            i++;
        }

        setProximoEvento(getListaDeEventos().get(eventoActivo));
        setElementosEnCola(getElementosEnCola() + (getProximoEvento().getTiempoDeOcurrencia() - getReloj()) * getCola().size());
        setElementosEnSistema(getElementosEnSistema() + (getProximoEvento().getTiempoDeOcurrencia() - getReloj()) * getCola().size() + (getEstado().equals(Estado.OCUPADO) ? 1 : 0));
        setReloj(getProximoEvento().getTiempoDeOcurrencia());
        getListaDeEventos().get(eventoActivo).setAtendido(true);

        return getProximoEvento(); //devuelvo el proximo evento
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public long getReloj() {
        return reloj;
    }

    public void setReloj(long reloj) {
        this.reloj = reloj;
    }

    public long getTiempoDeOcupacion() {
        return tiempoDeOcupacion;
    }

    public void setTiempoDeOcupacion(long tiempoDeOcupacion) {
        this.tiempoDeOcupacion = tiempoDeOcupacion;
    }

    public long getTiempoDeInicioOcupacion() {
        return tiempoDeInicioOcupacion;
    }

    public void setTiempoDeInicioOcupacion(long tiempoDeInicioOcupacion) {
        this.tiempoDeInicioOcupacion = tiempoDeInicioOcupacion;
    }

    public long getElementosEnCola() {
        return elementosEnCola;
    }

    public void setElementosEnCola(long elementosEnCola) {
        this.elementosEnCola = elementosEnCola;
    }

    public long getElementosEnSistema() {
        return elementosEnSistema;
    }

    public void setElementosEnSistema(long elementosEnSistema) {
        this.elementosEnSistema = elementosEnSistema;
    }

    public List<Evento> getListaDeEventos() {
        return listaDeEventos;
    }

    public boolean addEvento(Evento evento) {
        return getListaDeEventos().add(evento);
    }

    public List<Cliente> getCola() {
        return cola;
    }

    public boolean addClienteEnCola(Cliente clienteEnCola) {
        return getCola().add(clienteEnCola);
    }

    public List<Cliente> getClientesAtendidos() {
        return clientesAtendidos;
    }

    public boolean addClienteAtendido(Cliente clienteAtendido) {
        return getClientesAtendidos().add(clienteAtendido);
    }

    public Evento getProximoEvento() {
        return proximoEvento;
    }

    public void setProximoEvento(Evento proximoEvento) {
        this.proximoEvento = proximoEvento;
    }
}
