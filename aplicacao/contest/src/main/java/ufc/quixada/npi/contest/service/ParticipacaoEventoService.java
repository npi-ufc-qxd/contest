package ufc.quixada.npi.contest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Papel.Tipo;
import ufc.quixada.npi.contest.model.ParticipacaoEvento;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.repository.ParticipacaoEventoRepository;

@Service
public class ParticipacaoEventoService {
	
	@Autowired
	private ParticipacaoEventoRepository participacaoEventoRepository;
	
	public List<ParticipacaoEvento> findParticipacaoEventoPorPessoa(Long id){
		return participacaoEventoRepository.findParticipacaoEventoByPessoaId(id);
	}
	
	public ParticipacaoEvento findParticipacaoEventoPorId(Long id){
		return participacaoEventoRepository.findOne(id);
	}
	public void remover(ParticipacaoEvento participacaoEvento){
		participacaoEventoRepository.delete(participacaoEvento);
	}
	
	public boolean adicionarOuEditarParticipacaoEvento(ParticipacaoEvento participacao){
		try{
			List<ParticipacaoEvento> participacoes = participacaoEventoRepository.findByEventoAndPessoa(participacao.getEvento(), participacao.getPessoa());
			if(participacoes.isEmpty()){
				participacaoEventoRepository.save(participacao);
			} else {		
			
				boolean save = false;
				
				for(ParticipacaoEvento participacaoTemp : participacoes){
					if(!participacaoTemp.getPapel().equals(participacao.getPapel())){
						save = true;
					}
				}
				
				if(save){
					participacaoEventoRepository.save(participacao);
				}
			}
			
			return true;
			
		}catch (Exception e) {
			return false;
		}
	}
	
	public void removerParticipacaoEventoDoOrganizador(Evento evento, Pessoa pessoa){
		if(evento != null && evento.getEstado().equals(EstadoEvento.INATIVO)){
			ParticipacaoEvento participacaoEvento = buscarOrganizadorPorPessoaEEvento(evento, pessoa);
			participacaoEventoRepository.delete(participacaoEvento);
		}else{
			throw new IllegalArgumentException("So é possivel remover eventos inativos");
		}
	}
	
	public List<ParticipacaoEvento> getEventosDoOrganizador(EstadoEvento estado, Long id){
		return participacaoEventoRepository.findByEventoEstadoAndPapelAndPessoaId(estado, Tipo.ORGANIZADOR, id);
	}
	
	public List<ParticipacaoEvento> getEventosDoRevisor(EstadoEvento estado, Long id){
		return participacaoEventoRepository.findByEventoEstadoAndPapelAndPessoaId(estado, Tipo.REVISOR, id);
	}
	
	public List<ParticipacaoEvento> getEventosDoAutor(EstadoEvento estado, Long id){
		return participacaoEventoRepository.findByEventoEstadoAndPapelAndPessoaId(estado, Tipo.AUTOR, id);
	}
	
	public List<ParticipacaoEvento> getParticipacaoComoRevisorPorEvento(Long idEvento){
		return participacaoEventoRepository.findByEventoIdAndPapel(idEvento, Tipo.REVISOR);
	}
	
	public int buscarQuantidadeRevisoresPorEvento(Long idEvento){
		return getParticipacaoComoRevisorPorEvento(idEvento).size();
	}

	public List<ParticipacaoEvento> getRevisoresNoEvento(Long id){
		return participacaoEventoRepository.findByEventoIdAndPapel(id, Tipo.REVISOR);
	}
	
	public List<ParticipacaoEvento> getOrganizadoresNoEvento(Long id){
		return participacaoEventoRepository.findByEventoIdAndPapel(id, Tipo.ORGANIZADOR);
	}
	
	public boolean isOrganizadorDoEvento(Pessoa organizador, Long idEvento){
		if(organizador.getParticipacoesEvento() != null){
			for(ParticipacaoEvento participacaoEvento : organizador.getParticipacoesEvento()){
				if(participacaoEvento.getEvento().getId() == idEvento && 
						participacaoEvento.getPapel() == Tipo.ORGANIZADOR) return true;
			}
		}
		
		return false;
	}
	
	public ParticipacaoEvento buscarOrganizadorPorPessoaEEvento(Evento evento, Pessoa pessoa){
		return participacaoEventoRepository.findOneByEventoAndPessoaAndPapel(evento, pessoa, Tipo.ORGANIZADOR);
	}
	
	public ParticipacaoEvento buscarRevisorPorPessoaEEvento(Evento evento, Pessoa pessoa){
		return participacaoEventoRepository.findOneByEventoAndPessoaAndPapel(evento, pessoa, Tipo.REVISOR);
	}
}