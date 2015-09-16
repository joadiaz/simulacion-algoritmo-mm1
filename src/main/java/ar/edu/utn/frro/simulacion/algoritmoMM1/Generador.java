package ar.edu.utn.frro.simulacion.algoritmoMM1;

/**
 * Created by Fernando on 8/23/2015.
 */
public class Generador {

    /**
     * Valor de ALFA utilizado para la generacion de numeros de una distribucion exponencial.
     */
    private static final double lAMBDA_ARRIBOS = 5;
    private static final double lAMBDA_PARTIDAS = 10;

    public static int nextArribo() {
        return generarRandomExponencial(lAMBDA_ARRIBOS);
    }

    public static int nextPartida() {
        return generarRandomExponencial(lAMBDA_PARTIDAS);
    }

    /**
     * Algoritmo Generador Nro Exponencial
     *
     * @return numero entero generado para una distribucion exponencial
     */
    private static int generarRandomExponencial(double lambda) {
        int generatedNumber = 0;

        // Nos aseguramos que el numero generado no sea 0
        while (generatedNumber == 0) {
            double random = Math.random();
            double x = -Math.log(1.0 - random) * lambda;
            generatedNumber = (int) (Math.round(x));
        }

        return generatedNumber;
    }

}
