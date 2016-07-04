package ufc.quixada.npi.contest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.PapelEvento;
import ufc.quixada.npi.contest.model.ParticipacaoEvento;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.repository.ParticipacaoEventoRepository;

@Service
public class ParticipacaoEventoService {
	
	@Autowired
	private ParticipacaoEventoRepository participacaoEventoRepository;
	
	public ParticipacaoEvento findParticipacaoEventoPorId(Long id){
		return participacaoEventoRepository.findOne(id);
	}
	
	public void adicionarOuEditarParticipacaoEvento(Evento evento, Pessoa pessoa, PapelEvento papelEvento){
		ParticipacaoEvento participacaoEvento = new ParticipacaoEvento();
		participacaoEvento.setEvento(evento);
		participacaoEvento.setPessoa(pessoa);
		participacaoEvento.setPapel(papelEvento);
		participacaoEventoRepository.save(participacaoEvento);
	}
	
	public void removerParticipacaoEvento(Evento evento){
		if(evento != null && evento.getEstado().equals(EstadoEvento.ATIVO)){
			ParticipacaoEvento participacaoEvento = findByEvento(evento);
			participacaoEventoRepository.delete(participacaoEvento);
		}else{
			throw new IllegalArgumentException("So é possivel remover eventos inativos");
		}
	}
	
	public ParticipacaoEvento findByEvento(Evento evento){
		if(evento != null)
			return participacaoEventoRepository.findByEvento(evento);
		return null;
	}
}