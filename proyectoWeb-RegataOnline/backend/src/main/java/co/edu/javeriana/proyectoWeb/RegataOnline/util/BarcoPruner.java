package co.edu.javeriana.proyectoWeb.RegataOnline.util;

import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import co.edu.javeriana.proyectoWeb.RegataOnline.dto.BarcoDTO;
import co.edu.javeriana.proyectoWeb.RegataOnline.services.BarcoServicio;

/**
 * One-shot utility that prunes barcos leaving only a fixed number.
 * Activate with the Spring profile "prune" to run at application startup.
 *
 * WARNING: This will permanently delete Barco records. Use with care.
 */
@Component
@Profile("prune")
public class BarcoPruner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(BarcoPruner.class);

    @Autowired
    private BarcoServicio barcoServicio;

    // How many barcos to keep
    private final int keep = 9;

    @Override
    public void run(String... args) throws Exception {
        try {
            List<BarcoDTO> barcos = barcoServicio.listarBarcos();
            int total = barcos.size();
            log.info("BarcoPruner: encontrados {} barcos", total);

            if (total <= keep) {
                log.info("BarcoPruner: no se requiere borrado ({} <= {})", total, keep);
                return;
            }

            // Ordenar por id descendente (eliminar los de id más alto primero)
            barcos.sort(Comparator.comparing(BarcoDTO::getId, Comparator.nullsLast(Comparator.naturalOrder())).reversed());

            int deleted = 0;
            for (int i = keep; i < barcos.size(); i++) {
                BarcoDTO b = barcos.get(i);
                if (b != null && b.getId() != null) {
                    long id = b.getId();
                    try {
                        barcoServicio.borrarBarco(id);
                        deleted++;
                        log.info("BarcoPruner: eliminado barco id={} nombre={}", id, b.getNombre());
                    } catch (Exception e) {
                        log.error("BarcoPruner: error eliminando barco id={}", id, e);
                    }
                }
            }

            int remaining = barcoServicio.listarBarcos().size();
            log.info("BarcoPruner: borrados {} barcos; quedan {} barcos", deleted, remaining);
        } catch (Exception e) {
            log.error("BarcoPruner: error ejecutando prune", e);
        }
    }
}
