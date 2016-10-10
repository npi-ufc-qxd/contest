package ufc.quixada.npi.contest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import ufc.quixada.npi.contest.model.Email;
import ufc.quixada.npi.contest.util.Constants;

@Service
public class ConvidaPessoaEmailService {

	private Email email;
	private String emailCONTEST = "naoresponda@gmail.com";
	private static final String TITULO_EMAIL_ORGANIZADOR="Não Responder este email - Convite CONTEST para evento ";
	private static final String CORPO_EMAIL_ORGANIZADOR=", você foi convidado para participar do evento";
	private SimpleMailMessage templateMessage;
	private JavaMailSender javaMailSender;
	
	public ConvidaPessoaEmailService() {}

	public ConvidaPessoaEmailService(Email email) {
		this.email = email;
		this.javaMailSender = new JavaMailSenderImpl();
		this.templateMessage = new SimpleMailMessage();
	}
	public boolean send(String formato) {
		if(validadorFormato(formato))
			return editarCorpoEmail(formato);
		return false;

	}
	private boolean validadorFormato(String formato){
		if(Constants.FORMATO_EMAIL_ORGANIZADOR.equals(formato))
			return true;
		return false;
	}
	private boolean editarCorpoEmail(String formato) {
		SimpleMailMessage msg = new SimpleMailMessage(templateMessage);
		switch (formato) {
		case Constants.FORMATO_EMAIL_ORGANIZADOR :
				this.email.setTitulo(TITULO_EMAIL_ORGANIZADOR +  email.getNomeEvento());
				this.email.setTexto(email.getNomeConvidado() + CORPO_EMAIL_ORGANIZADOR + email.getNomeEvento());
		        msg.setTo(email.getEnderecoDestinatario());
		        msg.setText(email.getTexto());
		        msg.setSubject(email.getTitulo());
		        msg.setFrom(emailCONTEST);
			break;
	
		}
		try {
			javaMailSender.send(msg);
			return true;

		} catch (MailException e) {
			e.printStackTrace();
			return false;
		}
	}
}