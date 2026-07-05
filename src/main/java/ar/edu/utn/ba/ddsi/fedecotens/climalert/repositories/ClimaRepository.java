package ar.edu.utn.ba.ddsi.fedecotens.climalert.repositories;

import ar.edu.utn.ba.ddsi.fedecotens.climalert.entities.Clima;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Repository;

@Repository
public class ClimaRepository {

    private final ConcurrentHashMap<Integer, Clima> almacen = new ConcurrentHashMap<>();
    private final AtomicInteger contadorId = new AtomicInteger(1);

    public Clima save(Clima clima) {
        if (clima.getId() == -1) {
            clima.setId(contadorId.getAndIncrement());
        }
        almacen.put(clima.getId(), clima);
        return clima;
    }

    public Optional<Clima> findLatest() {
        return almacen.values().stream()
            .max(Comparator.comparingInt(Clima::getId));
    }
}
