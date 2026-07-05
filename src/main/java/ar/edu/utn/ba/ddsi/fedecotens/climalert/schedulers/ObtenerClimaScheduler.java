package ar.edu.utn.ba.ddsi.fedecotens.climalert.schedulers;

import ar.edu.utn.ba.ddsi.fedecotens.climalert.entities.Clima;
import ar.edu.utn.ba.ddsi.fedecotens.climalert.repositories.ClimaRepository;
import ar.edu.utn.ba.ddsi.fedecotens.climalert.services.WeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ObtenerClimaScheduler {

  private static final Logger log = LoggerFactory.getLogger(ObtenerClimaScheduler.class);

  private final WeatherService weatherService;
  private final ClimaRepository climaRepository;

  public ObtenerClimaScheduler(WeatherService weatherService, ClimaRepository climaRepository) {
    this.weatherService = weatherService;
    this.climaRepository = climaRepository;
  }

  // 300000 ms = 5 minutos
  @Scheduled(fixedRate = 300000)
  public void ejecutarObtencionClima() {
    log.info("Scheduler: Obtención de datos climáticos");

    try {
      String ciudad = "Buenos Aires";
      Clima climaActual = weatherService.getCurrentWeather(ciudad);
      Clima climaGuardado = climaRepository.save(climaActual);

      log.info("Clima guardado con éxito -> ID: {}, Temp: {}°C, Humedad: {}%, Fecha/Hora: {}",
          climaGuardado.getId(),
          climaGuardado.getTemperatura(),
          climaGuardado.getHumedad(),
          climaGuardado.getFechaHora());

    } catch (Exception e) {
      log.error("Error crítico durante la ejecución del Scheduler de clima: {}", e.getMessage());
    }
  }
}