package ufc.quixada.npi.contest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Papel.Tipo;
import ufc.quixada.npi.contest.model.ParticipacaoEvento;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.VisibilidadeEvento;
import ufc.quixada.npi.contest.repository.EventoRepository;

@Service
public class EventoService {

	private static final String TITULO_EMAIL_ORGANIZADOR = "TITULO_EMAIL_CONVITE_ORGANIZADOR";
	private static final String TEXTO_EMAIL_ORGANIZADOR = "TEXTO_EMAIL_CONVITE_ORGANIZADOR";
	
	@Autowired
	private EventoRepository eventoRepository;

	@Autowired
	private PessoaService pessoaService;

	@Autowired
	private MessageService messageService;

	@Autowired
	private EnviarEmailService emailService;

	@Autowired
	private ParticipacaoEventoService participacaoEventoService;

	private boolean adicionarPessoa(String email, Evento evento, String nome, Tipo papel, String url) {

		Pessoa pessoa = pessoaService.getByEmail(email);

		String assunto = messageService.getMessage(TITULO_EMAIL_ORGANIZADOR) + " " + evento.getNome();
		String corpo = nome + messageService.getMessage(TEXTO_EMAIL_ORGANIZADOR) + " " + evento.getNome() + " como "+ papel.getNome();
		String titulo = "[CONTEST] Convite para o Evento: " + evento.getNome();
		
		if (pessoa == null) {
			pessoa = new Pessoa(nome, email);
			corpo = corpo + ". Você não está cadastrado na nossa base de dados. Acesse: " + url + " e termine o seu cadastro";
		}
		

		
		if (!emailService.enviarEmail(titulo, assunto, email, corpo)) {
			return false;
		}
		pessoaService.addOrUpdate(pessoa);
		ParticipacaoEvento participacao = new ParticipacaoEvento(papel, pessoa, evento);
		participacaoEventoService.adicionarOuEditarParticipacaoEvento(participacao);
		return true;
	}

	public boolean adicionarOrganizador(String email, Evento evento, String nome, String url) {
		Tipo papel = Tipo.ORGANIZADOR;
		return adicionarPessoa(email, evento, nome, papel, url);
	}

	public boolean adicionarRevisor(String email, Evento evento, String nome, String url) {
		Tipo papel = Tipo.REVISOR;
		return adicionarPessoa(email, evento, nome, papel, url);
	}

	public boolean adicionarAutor(String email, Evento evento, String nome, String url) {
		Tipo papel = Tipo.AUTOR;
		return adicionarPessoa(email, evento, nome, papel, url);
	}

	public boolean adicionarOuAtualizarEvento(Evento evento) {
		if (evento.getPrazoSubmissaoInicial() != null && evento.getPrazoSubmissaoFinal() != null
				&& evento.getPrazoRevisaoInicial() != null && evento.getPrazoRevisaoFinal() != null) {
			if (!evento.getEstado().equals(EstadoEvento.FINALIZADO)
					&& evento.getPrazoSubmissaoInicial().before(evento.getPrazoSubmissaoFinal())
					&& evento.getPrazoRevisaoInicial().after(evento.getPrazoSubmissaoInicial())
					&& evento.getPrazoRevisaoInicial().before(evento.getPrazoRevisaoFinal())
					&& evento.getPrazoRevisaoFinal().before(evento.getPrazoSubmissaoFinal())) {

				eventoRepository.save(evento);
				return true;
			}
			return false;
		} else {
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

	public Boolean existeEvento(Long id) {
		if (id == null || id.toString().isEmpty()) {
			return false;
		} else {
			return eventoRepository.exists(id);
		}
	}

	public List<Evento> buscarEventoPorEstado(EstadoEvento estado) {
		return eventoRepository.findEventoByEstado(estado);
	}

	public List<Evento> buscarEventosAtivosEPublicos() {
		return eventoRepository.findEventosAtivosEPublicos();
	}

	public List<Evento> eventosParaParticipar(Long idPessoa) {
		return eventoRepository.eventosParaParticipar(idPessoa);
	}

	public List<Evento> buscarMeusEventos(Long id) {
		return eventoRepository.findDistinctEventoByParticipacoesPessoaIdAndVisibilidade(id,
				VisibilidadeEvento.PUBLICO);
	}

	public List<Evento> buscarEventosParticapacaoAutor(Long idAutor) {
		return eventoRepository.findEventoByParticipacoesPessoaIdAndParticipacoesPapelAndVisibilidadeAndEstado(idAutor,
				Tipo.AUTOR, VisibilidadeEvento.PUBLICO, EstadoEvento.ATIVO);
	}

	public List<Evento> buscarEventosParticapacaoRevisor(Long idRevisor) {
		return eventoRepository.findEventoByParticipacoesPessoaIdAndParticipacoesPapelAndVisibilidadeAndEstado(
				idRevisor, Tipo.REVISOR, VisibilidadeEvento.PUBLICO, EstadoEvento.ATIVO);
	}

	public List<Evento> buscarEventosParticapacaoOrganizador(Long idOrganizador) {
		return eventoRepository.findEventoByParticipacoesPessoaIdAndParticipacoesPapelAndVisibilidadeAndEstado(
				idOrganizador, Tipo.ORGANIZADOR, VisibilidadeEvento.PUBLICO, EstadoEvento.ATIVO);
	}

	public List<Evento> buscarEventosInativosQueOrganizo(Long idOrganizador) {
		return eventoRepository.findEventoByParticipacoesPessoaIdAndParticipacoesPapelAndEstado(idOrganizador,
				Tipo.ORGANIZADOR, EstadoEvento.INATIVO);
	}

	public List<Evento> getEventosByEstadoEVisibilidadePublica(EstadoEvento estado) {
		return eventoRepository.findEventoByEstadoAndVisibilidade(estado, VisibilidadeEvento.PUBLICO);
	}

}