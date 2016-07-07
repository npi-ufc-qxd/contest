package ufc.quixada.npi.contest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
	
	@Autowired
	private MessageSource messages;
	
	public String getMessage(String identificador) throws NoSuchMessageException{
		return messages.getMessage(identificador, null, null);
	}
}
