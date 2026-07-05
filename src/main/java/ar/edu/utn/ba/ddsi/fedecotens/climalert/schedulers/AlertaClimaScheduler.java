package ar.edu.utn.ba.ddsi.fedecotens.climalert.schedulers;

import ar.edu.utn.ba.ddsi.fedecotens.climalert.entities.Alerta;
import ar.edu.utn.ba.ddsi.fedecotens.climalert.entities.Clima;
import ar.edu.utn.ba.ddsi.fedecotens.climalert.repositories.AlertaRepository;
import ar.edu.utn.ba.ddsi.fedecotens.climalert.repositories.ClimaRepository;
import ar.edu.utn.ba.ddsi.fedecotens.climalert.services.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class AlertaClimaScheduler {

  private static final Logger log = LoggerFactory.getLogger(AlertaClimaScheduler.class);

  private final ClimaRepository climaRepository;
  private final EmailService emailService;
  private final AlertaRepository alertaRepository;

  private final String[] destinatarios = {
      "admin@clima.com",
      "emergencias@clima.com",
      "meteorologia@clima.com"
  };

  public AlertaClimaScheduler(ClimaRepository climaRepository, EmailService emailService, AlertaRepository alertaRepository) {
    this.climaRepository = climaRepository;
    this.emailService = emailService;
    this.alertaRepository = alertaRepository;
  }

  // fixedRate = 60000 ms (1 minuto)
  @Scheduled(fixedRate = 60000, initialDelay = 15000)
  public void procesarYVerificarAlertas() {
    log.info("Scheduler: monitoreo de alertas climáticas");

    Optional<Clima> ultimoClimaOpt = climaRepository.findLatest();

    if (ultimoClimaOpt.isEmpty()) {
      log.info("No se encontraron registros climáticos en el repositorio para evaluar.");
      return;
    }

    Clima ultimoClima = ultimoClimaOpt.get();

    boolean esTemperaturaCritica = ultimoClima.getTemperatura() > 2.0;
    boolean esHumedadCritica = ultimoClima.getHumedad() > 60.0;

    if (esTemperaturaCritica && esHumedadCritica) {
      log.warn("ALERTA DETECTADA - Procediendo al envío de notificaciones.");

      Optional<Alerta> alertaExistenteOpt = alertaRepository.findByClimaId(ultimoClima.getId());
      Alerta alerta;
      Set<String> aNotificar = new HashSet<>();

      if (alertaExistenteOpt.isEmpty()) {
        // No existe la alerta, se prepara para enviar a todos
        log.info("No existe alerta previa para el Clima ID: {}.", ultimoClima.getId());
        alerta = new Alerta(ultimoClima, new HashSet<>());
        aNotificar.addAll(Arrays.asList(destinatarios));
      } else {
        // Ya existe, enviamos a los que no habian recibido todavia
        alerta = alertaExistenteOpt.get();
        log.info("Alerta encontrada para el Clima ID: {}. Filtrando nuevos destinatarios.", ultimoClima.getId());

        for (String dest : destinatarios) {
          if (!alerta.getEnviados().contains(dest)) {
            aNotificar.add(dest);
          }
        }
      }

      if (!aNotificar.isEmpty()) {
        try {
          String[] destinatariosArray = aNotificar.toArray(new String[0]);
          emailService.enviarCorreoAlerta(destinatariosArray, ultimoClima);

          alerta.getEnviados().addAll(aNotificar);
          alertaRepository.save(alerta);

          log.info("Correos de alerta enviados exitosamente a: {}", aNotificar);
        } catch (Exception e) {
          log.error("Error al intentar enviar los correos de alerta: {}", e.getMessage());
        }
      } else {
        log.info("Todos los destinatarios ya fueron notificados previamente para este clima.");
      }
    } else {
      log.info("Monitoreo finalizado. No se necesita emitir alerta. (Temp: {}°C, Hum: {}%).",
          ultimoClima.getTemperatura(), ultimoClima.getHumedad());
    }
  }
}