package ufc.quixada.npi.contest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.repository.EventoRepository;

@Service
public class EventoService {
	
	@Autowired
	private EventoRepository eventoRepository;
	
	public void adicionarOuAtualizarEvento(Evento evento){
		eventoRepository.save(evento);
	}
	
	public void removerEvento(Integer id){
		if(eventoRepository.findOne(id) != null)
			eventoRepository.delete(id);
	}
	
	public List<Evento> buscarEventos(){
		return (List<Evento>) eventoRepository.findAll();
	}
	
	public Evento buscarEventoPorId(Long long1){
		return eventoRepository.findOne(long1);
	}
	
	public Boolean existeEvento(Long long1){
		return eventoRepository.exists(long1);
	}
	
}