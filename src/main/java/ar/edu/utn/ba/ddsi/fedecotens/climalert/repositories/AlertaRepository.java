package ar.edu.utn.ba.ddsi.fedecotens.climalert.repositories;

import ar.edu.utn.ba.ddsi.fedecotens.climalert.entities.Alerta;
import ar.edu.utn.ba.ddsi.fedecotens.climalert.entities.Clima;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Repository;

@Repository
public class AlertaRepository {

  private final ConcurrentHashMap<Integer, Alerta> almacen = new ConcurrentHashMap<>();
  private final AtomicInteger contadorId = new AtomicInteger(1);

  public Alerta save(Alerta alerta) {
    if (alerta.getId() == -1) {
      alerta.setId(contadorId.getAndIncrement());
    }
    almacen.put(alerta.getId(), alerta);
    return alerta;
  }

  public Optional<Alerta> findByClimaId(int climaId) {
    return almacen.values().stream()
        .filter(alerta -> alerta.getClima() != null && alerta.getClima().getId() == climaId)
        .findFirst();
  }
}
