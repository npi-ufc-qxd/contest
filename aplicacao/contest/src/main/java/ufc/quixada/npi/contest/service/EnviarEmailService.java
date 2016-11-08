package ufc.quixada.npi.contest.service;

import java.util.Map;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import ufc.quixada.npi.contest.model.Email;
import ufc.quixada.npi.contest.util.Constants;

@Service
public class EnviarEmailService {

	private Email email;
	private SimpleMailMessage templateMessage;
	private JavaMailSender javaMailSender;
	
	public EnviarEmailService() {}

	public EnviarEmailService(Email email) {
		this.email = email;
		this.javaMailSender = new JavaMailSenderImpl();
		this.templateMessage = new SimpleMailMessage();
	}

	public boolean enviarEmail() {
		SimpleMailMessage msg = new SimpleMailMessage(templateMessage);
		Map<String, String> destinatariosHash;
		  int i = 0;
		 destinatariosHash = this.email.getEnderecosDestinatarios();
	     int qtdDestinatario = destinatariosHash.size();
	     String[] enderecos = new String[qtdDestinatario];
	   
	     for (String key : destinatariosHash.keySet()) {
	           enderecos[i] = key;
	     }

        msg.setText(email.getCorpo());
        msg.setSubject(email.getAssunto());
        msg.setFrom(Constants.ENDERECO_EMAIL_CONTEST);
        msg.setTo(enderecos);
        
		try {
			javaMailSender.send(msg);
			return true;

		} catch (MailException e) {
			e.printStackTrace();
			return false;
		}
		
	}
}