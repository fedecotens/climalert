package ar.edu.utn.ba.ddsi.fedecotens.climalert.services;

import ar.edu.utn.ba.ddsi.fedecotens.climalert.entities.Clima;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

  private final JavaMailSender mailSender;

  public EmailService(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  public void enviarCorreoAlerta(String[] destinatarios, Clima clima) {
    SimpleMailMessage message = new SimpleMailMessage();

    message.setTo(destinatarios);
    message.setSubject("ALERTA: Condiciones climáticas peligrosas o inusuales");

    String cuerpoMensaje = String.format(
        "Se ha generado una alerta automática debido a parámetros climáticos fuera de los límites seguros.\n\n" +
            "Detalle:\n" +
            "• Temperatura:    %.2f °C\n" +
            "• Humedad:        %.2f %%\n" +
            "• Fecha y Hora:   %s\n" +
        clima.getId(),
        clima.getTemperatura(),
        clima.getHumedad(),
        clima.getFechaHora()
    );

    message.setText(cuerpoMensaje);

    mailSender.send(message);
  }
}