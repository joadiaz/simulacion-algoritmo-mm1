package ar.edu.utn.frro.simulacion.algoritmoMM1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static Logger LOG = LoggerFactory.getLogger(Main.class);
    private static final String REPORTE = "%s,%s,%s,%s,%s,%s,%s\n";

	public static void main(String[] args) {

        final List<String> resultados = new ArrayList<String>();
        resultados.add("Tiempo promedio entre arribos,Tiempo promedio en cola,Tiempo de uso del servidor (%),Tiempo promedio en Servicio,Máximo de clientes en cola,Promedio elementos en cola,Promedio elementos en Sistema\n");

		for (int i = 0; i < 1000; i++) {
			final Reporte reporte = new Simulacion(100000).iniciarSimulacion();
			resultados.add(String.format(REPORTE,
                    reporte.getTiempoPromedioEntreArribos(),
                    reporte.getTiempoPromedioEnCola(),
                    reporte.getTiempoUsoServidor(),
                    reporte.getTiempoPromedioEnServicio(),
                    reporte.getMaximoClientesEnCola(),
                    reporte.getPromedioElementosEnCola(),
                    reporte.getPromedioElementosEnSistema()));
		}

        try
        {
            FileWriter writer = new FileWriter("resultados.csv");

            for (String resultado : resultados) {
                writer.append(resultado);
            }

            writer.flush();
            writer.close();

        } catch(Exception e) {
            LOG.warn("Error generando archivo CSV de reporte de simulacion", e);
        }
    }

}
