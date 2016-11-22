package ufc.quixada.npi.contest.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import ufc.quixada.npi.contest.model.Email;
import ufc.quixada.npi.contest.util.Constants;

@Service
public class EnviarEmailService {
	
	private JavaMailSender javaMailSender;

	@Autowired
	public EnviarEmailService(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}

	public boolean enviarEmail(Email email) {
//
//		this.javaMailSender.setHost("200.129.38.134");
//	
//		
//		Properties props = new Properties();
//		props.put("mail.smtp.starttls.enable","true");
//		props.put( "mail.smtp.auth", "true" );
//		props.put("mail.smtp.port","587");
//		
//		this.javaMailSender.setPassword("");
//		this.javaMailSender.setPort(587);
//		this.javaMailSender.setProtocol("smtp");
//		Session session = Session.getDefaultInstance(props,null); 
//		this.javaMailSender.setSession(session);
	
		
		Map<String, String> destinatariosHash;
		  int i = 0;
		 destinatariosHash = email.getEnderecosDestinatarios();
	     int qtdDestinatario = destinatariosHash.size();
	     String[] enderecos = new String[qtdDestinatario];
	   
	     for (String key : destinatariosHash.keySet()) {
	           enderecos[i] = key;
	     }

	    SimpleMailMessage msg = new SimpleMailMessage();
        msg.setText(email.getCorpo());
        msg.setSubject(email.getAssunto());
        msg.setFrom(Constants.ENDERECO_EMAIL_CONTEST);
        msg.setTo(enderecos);
        
		try {
			javaMailSender.send(msg);
		} catch (MailException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}