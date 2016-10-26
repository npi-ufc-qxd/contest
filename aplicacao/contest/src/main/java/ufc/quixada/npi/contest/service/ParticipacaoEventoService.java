package ufc.quixada.npi.contest.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Papel;
import ufc.quixada.npi.contest.model.ParticipacaoEvento;
import ufc.quixada.npi.contest.repository.ParticipacaoEventoRepository;

@Service
public class ParticipacaoEventoService {
	
	@Autowired
	private ParticipacaoEventoRepository participacaoEventoRepository;
	
	public ParticipacaoEvento findParticipacaoEventoPorId(Long id){
		return participacaoEventoRepository.findOne(id);
	}
	
	public boolean adicionarOuEditarParticipacaoEvento(ParticipacaoEvento participacaoEvento){
		if(participacaoEvento != null){
			participacaoEventoRepository.save(participacaoEvento);
			return true;
		}
		return false;
	}
	
	public void removerParticipacaoEvento(Evento evento){
		if(evento != null && evento.getEstado().equals(EstadoEvento.INATIVO)){
			ParticipacaoEvento participacaoEvento = findByEventoId(evento.getId());
			participacaoEventoRepository.delete(participacaoEvento);
		}else{
			throw new IllegalArgumentException("So é possivel remover eventos inativos");
		}
	}
	
	public ParticipacaoEvento findByEventoId(Long idEvento){
		if(idEvento != null)
			return participacaoEventoRepository.findByEventoId(idEvento);
		return null;
	}
	
	public List<ParticipacaoEvento> getEventosDoOrganizador(EstadoEvento estado, Long id){
		List<ParticipacaoEvento> listaParticipacaoEventos = new ArrayList<>();
		listaParticipacaoEventos = participacaoEventoRepository.findByEventoEstadoAndPapelAndPessoaId(estado, Papel.ORGANIZADOR, id);
		return listaParticipacaoEventos;
	}
	
	public List<ParticipacaoEvento> getParticipacaoComoRevisorPorEvento(Long idEvento){
		return participacaoEventoRepository.findByEventoIdAndPapel(idEvento, Papel.REVISOR);
	}
	
	public int buscarQuantidadeRevisoresPorEvento(Long idEvento){
		return getParticipacaoComoRevisorPorEvento(idEvento).size();
	}
}