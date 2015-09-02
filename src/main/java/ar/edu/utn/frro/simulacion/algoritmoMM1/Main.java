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
        double acumTiempoUsoServicio=0;
        float acumTiempoPromedioEntreArribo=0, acumTiempoPromedioEnCola=0,acumTiempoPromedioEnServicio=0, acumMaximoClientesEnCola=0, acumPromedioElementosEnCola=0, acumPromedioElementosEnSistema=0;
        resultados.add("Tiempo promedio entre arribos,Tiempo promedio en cola,Tiempo de uso del servidor (%),Tiempo promedio en Servicio,Máximo de clientes en cola,Promedio elementos en cola,Promedio elementos en Sistema\n");
        int incremento=1;
		for (int i = 0; i < 100; i++) {
			
			for (int j = 0; j < incremento; j++) {
				final Reporte reporte = new Simulacion(1).iniciarSimulacion();
				
				acumTiempoPromedioEntreArribo=reporte.getTiempoPromedioEntreArribos()+acumTiempoPromedioEntreArribo;
				acumTiempoPromedioEnCola=reporte.getTiempoPromedioEnCola()+acumTiempoPromedioEnCola;
				acumTiempoUsoServicio=reporte.getTiempoUsoServidor()+acumTiempoUsoServicio;
				acumTiempoPromedioEnServicio=reporte.getTiempoPromedioEnServicio()+acumTiempoPromedioEnServicio;
				acumMaximoClientesEnCola=reporte.getMaximoClientesEnCola()+acumMaximoClientesEnCola;
				acumPromedioElementosEnCola=reporte.getPromedioElementosEnCola()+acumPromedioElementosEnCola;
				acumPromedioElementosEnSistema=reporte.getPromedioElementosEnSistema()+acumPromedioElementosEnSistema;
			}
			incremento=incremento+1;
			resultados.add(String.format(REPORTE,
					acumTiempoPromedioEntreArribo/incremento,
					acumTiempoPromedioEnCola/incremento,
					acumTiempoUsoServicio/incremento,
					acumTiempoPromedioEnServicio/incremento,
					acumMaximoClientesEnCola/incremento,
					acumPromedioElementosEnCola/incremento,
					acumPromedioElementosEnSistema/incremento));
					
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
