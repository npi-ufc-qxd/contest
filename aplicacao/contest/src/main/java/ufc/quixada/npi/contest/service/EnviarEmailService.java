package ufc.quixada.npi.contest.service;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import ufc.quixada.npi.contest.model.Email;

@Service
public class EnviarEmailService {

	private Email email;
	private String emailCONTEST = "naoresponda@contest.com";
	private SimpleMailMessage templateMessage;
	private JavaMailSender javaMailSender;
	
	public EnviarEmailService() {}

	public EnviarEmailService(Email email) {
		this.email = email;
		this.javaMailSender = new JavaMailSenderImpl();
		this.templateMessage = new SimpleMailMessage();
	}

	public boolean enviarEmail(String titulo, String texto) {
		SimpleMailMessage msg = new SimpleMailMessage(templateMessage);

		this.email.setTitulo(titulo);
		this.email.setTexto(texto);
        msg.setTo(email.getEnderecoDestinatario());
        msg.setText(email.getTexto());
        msg.setSubject(email.getTitulo());
        msg.setFrom(emailCONTEST);
		
		try {
			javaMailSender.send(msg);
			return true;

		} catch (MailException e) {
			e.printStackTrace();
			return false;
		}
		
	}
}