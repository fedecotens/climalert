package ar.edu.utn.ba.ddsi.fedecotens.climalert.services;

import ar.edu.utn.ba.ddsi.fedecotens.climalert.dtos.WeatherApiResponse;
import ar.edu.utn.ba.ddsi.fedecotens.climalert.entities.Clima;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.time.LocalDateTime;

@Component
public class WeatherService {

  private final RestClient restClient;

  @Value("${weather.api.key}")
  private String apiKey;

  public WeatherService() {
    this.restClient = RestClient.builder()
        .baseUrl("https://api.weatherapi.com/v1")
        .build();
  }

  public Clima getCurrentWeather(String city) {
    WeatherApiResponse response = this.restClient.get()
        .uri(uriBuilder -> uriBuilder
            .path("/current.json")
            .queryParam("key", apiKey)
            .queryParam("q", city)
            .build())
        .retrieve()
        .body(WeatherApiResponse.class);

    if (response != null && response.current() != null) {
      return new Clima(
          response.current().temp_c(),
          response.current().humidity(),
          LocalDateTime.now()
      );
    }
    throw new RuntimeException("Error al obtener datos de WeatherAPI para " + city);
  }
}