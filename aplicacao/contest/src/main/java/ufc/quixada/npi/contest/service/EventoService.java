package ufc.quixada.npi.contest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Papel;
import ufc.quixada.npi.contest.repository.EventoRepository;

@Service
public class EventoService {

	@Autowired
	private EventoRepository eventoRepository;

	public boolean adicionarOuAtualizarEvento(Evento evento) {
		if(evento.getPrazoSubmissaoInicial() != null && evento.getPrazoSubmissaoFinal() != null &&
           evento.getPrazoRevisaoInicial() != null && evento.getPrazoRevisaoFinal() != null){
			if(!evento.getEstado().equals(EstadoEvento.FINALIZADO) && evento.getPrazoSubmissaoInicial().before(evento.getPrazoSubmissaoFinal()) &&
			   evento.getPrazoRevisaoInicial().after(evento.getPrazoSubmissaoInicial()) &&
			   evento.getPrazoRevisaoInicial().before(evento.getPrazoRevisaoFinal()) &&
			   evento.getPrazoRevisaoFinal().before(evento.getPrazoSubmissaoFinal())){
				
				eventoRepository.save(evento);
				return true;
			}
			return false;
		}else{
			eventoRepository.save(evento);
			return true;
		}
	}

	public boolean removerEvento(Long id) {
		if (eventoRepository.findOne(id) != null) {
			eventoRepository.delete(id);
			return true;
		}

		return false;
	}

	public Evento buscarEventoPorId(Long id) {
		return eventoRepository.findOne(id);
	}

	public List<Evento> buscarEventos() {
		return (List<Evento>) eventoRepository.findAll();
	}

	public Boolean existeEvento(Long id){
		if(id == null || id.toString().isEmpty()){
			return false;
		}else{
			return eventoRepository.exists(id);
		}
	}
	
	public List<Evento> buscarEventoPorEstado(EstadoEvento estado){
		return eventoRepository.findByEstadoEquals(estado);
	}
	
	public List<Evento> buscarEventosAtivosEPublicos(){
		return eventoRepository.findEventosAtivosEPublicos();
	}

	public List<Evento> eventosParaParticipar(Long idPessoa) {
		return eventoRepository.eventosParaParticipar(idPessoa);
	}
	
	public List<Evento> buscarEventosParticapacaoAutor(Long idAutor){
		return eventoRepository.findEventosDoAutor(idAutor);
	}
	
	public List<Evento> buscarEventosParticapacaoRevisor(Long idRevisor, Papel papel){
		return eventoRepository.findEventosDoRevisor(idRevisor,papel.toString());
	}
	
	public List<Evento> getEventosByEstado(EstadoEvento estado){
		return eventoRepository.findEventoByEstado(estado);
	}
	
}