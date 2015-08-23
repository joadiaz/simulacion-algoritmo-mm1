package ar.edu.utn.frro.simulacion.algoritmoMM1;

public class Evento {

    public enum Tipo {
        PARTIDA, ARRIBO;
    }

    private Tipo tipo;
    private long tiempoDeOcurrencia;
    private boolean atendido;

    public Evento(Tipo tipo) {
        this.tipo = tipo;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public long getTiempoDeOcurrencia() {
        return tiempoDeOcurrencia;
    }

    public void setTiempoDeOcurrencia(long tiempoDeOcurrencia) {
        this.tiempoDeOcurrencia = tiempoDeOcurrencia;
    }

    public boolean isAtendido() {
        return atendido;
    }

    public void setAtendido(boolean atendido) {
        this.atendido = atendido;
    }
}
