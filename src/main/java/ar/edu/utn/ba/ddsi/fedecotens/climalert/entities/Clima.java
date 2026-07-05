package ar.edu.utn.ba.ddsi.fedecotens.climalert.entities;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class Clima {

    private int id;
    private double temperatura;
    private double humedad;
    private LocalDateTime fechaHora;

    public Clima(double temperatura, double humedad, LocalDateTime fechaHora) {
        this.temperatura = temperatura;
        this.humedad = humedad;
        this.fechaHora = fechaHora;
        this.id = -1;
    }

}
