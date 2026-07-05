package ar.edu.utn.ba.ddsi.fedecotens.climalert.entities;

import lombok.Getter;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class Alerta {
  private Clima clima;
  private Set<String> enviados;
  private int id;

  public Alerta(Clima clima, Set<String> enviados) {
    this.clima = clima;
    this.enviados = enviados != null ? enviados : new HashSet<>();
    this.id = -1;
  }
}
