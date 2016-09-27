package ufc.quixada.npi.contest.service;

import java.util.Date;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import ufc.quixada.npi.contest.model.Email;

@Service
public class ConvidaPessoaEmailService {

	
	private JavaMailSender javaMailSender;
	private SimpleMailMessage templateMessage;
	private Email email;
	
	public ConvidaPessoaEmailService() {
		
	}

	public ConvidaPessoaEmailService(Email email) {
		this.email = email;
	}

	private void editarCorpoEmail() {
		email.setTitulo("Convite CONTEST para evento " + this.email.getNomeEvento());
		email.setTexto("Olá "+ this.email.getNomeConvidado() + ", você foi convidado para participar do evento "
				+ this.email.getNomeEvento());
	}

	public boolean send() {
		//TODO change this to Autowired 
		this.javaMailSender = new JavaMailSenderImpl();
		this.templateMessage = new SimpleMailMessage();
		
		editarCorpoEmail();

		SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);
        msg.setTo(this.email.getEnderecoDestinatario());
        msg.setText(this.email.getTexto());
        msg.setSubject(this.email.getTitulo());
		try {
			this.javaMailSender.send(msg);
			return true;

		} catch (MailException e) {
			e.printStackTrace();
			return false;
		}
	}
}