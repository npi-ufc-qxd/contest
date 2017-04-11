package ufc.quixada.npi.contest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import br.ufc.quixada.npi.model.Email;
import br.ufc.quixada.npi.model.Email.EmailBuilder;
import br.ufc.quixada.npi.service.SendEmailService;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.util.Constants;

@Service
public class EnviarEmailService {
	@Autowired
	SendEmailService service;
	
	public boolean enviarEmail(Evento evento,String assunto, String emailDestinatario, String corpo) {
		
		String titulo = "[CONTEST] Convite Evento "+evento.getNome();
		//EmailBuilder emailBuilder = new EmailBuilder(titulo,Constants.ENDERECO_EMAIL_CONTEST,assunto,emailDestinatario,corpo);
		EmailBuilder emailBuilder = new EmailBuilder(titulo,Constants.ENDERECO_EMAIL_CONTEST,assunto,"carlos_matheus95@hotmail.com",corpo);
		Email email = new Email(emailBuilder);
		
        
		try {
			service.sendEmail(email);
		} catch (MailException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}