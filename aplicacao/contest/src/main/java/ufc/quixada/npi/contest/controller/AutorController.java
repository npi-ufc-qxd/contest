package ufc.quixada.npi.contest.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Papel;
import ufc.quixada.npi.contest.model.ParticipacaoEvento;
import ufc.quixada.npi.contest.model.ParticipacaoTrabalho;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.Submissao;
import ufc.quixada.npi.contest.model.TipoSubmissao;
import ufc.quixada.npi.contest.model.Trabalho;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.MessageService;
import ufc.quixada.npi.contest.service.ParticipacaoEventoService;
import ufc.quixada.npi.contest.service.ParticipacaoTrabalhoService;
import ufc.quixada.npi.contest.service.PessoaService;
import ufc.quixada.npi.contest.service.RevisaoService;
import ufc.quixada.npi.contest.service.StorageService;
import ufc.quixada.npi.contest.service.SubmissaoService;
import ufc.quixada.npi.contest.util.Constants;
import ufc.quixada.npi.contest.validator.TrabalhoValidator;


@Controller
@RequestMapping("/autor")
public class AutorController {

	private static final String FORMATO_ARQUIVO_INVALIDO = "FORMATO_ARQUIVO_INVALIDO";
	private static final String NAO_HA_TRABALHOS = "NAO_HA_TRABALHOS";
	private static final String ENVIARTRABALHO = "ENVIARTRABALHO";
	private static final String SUBMISSAO = "SUBMISSAO";
	private static final String REVISAO = "REVISAO";
	private static final String PARTICAPAR_EVENTO_SUCESSO = "PARTICAPAR_EVENTO_SUCESSO";
	private static final String PARTICAPACAO_EVENTO_SUCESSO = "particapacaoEventoSucesso";
	private static final String EVENTO_NAO_EXISTE = "EVENTO_NAO_EXISTE";
	private static final String EVENTO_INEXISTENTE_ERROR = "eventoInexistenteError";
	private static final String PARTICIPAR_EVENTO_INATIVO = "PARTICIPAR_EVENTO_INATIVO";
	private static final String ID_EVENTO_VAZIO_ERROR = "ID_EVENTO_VAZIO_ERROR";
	private static final String EVENTO_VAZIO_ERROR = "eventoVazioError";
	private static final String PARTICIPAR_EVENTO_INATIVO_ERROR = "participarEventoInativoError";

	@Autowired
	private ParticipacaoEventoService participacaoEventoService;
	
	@Autowired
	private EventoService eventoService;
	
	@Autowired
	private MessageService messageService;
	
	@Autowired
	private PessoaService pessoaService;
	
	@Autowired
	private RevisaoService revisaoService;
	
	@Autowired
	private SubmissaoService submissaoService;
	
	@Autowired
	private TrabalhoValidator trabalhoValidator;
	
	@Autowired
	private ParticipacaoTrabalhoService participacaoTrabalhoService;
	
	private final StorageService storageService;

    @Autowired
    public AutorController(StorageService storageService) {
        this.storageService = storageService;
    }
	
	@RequestMapping
	public String index(Model model){
		Pessoa autorLogado = getAutorLogado();
		model.addAttribute("eventosParaParticipar", eventoService.eventosParaParticipar(autorLogado.getId()));
		model.addAttribute("eventoParticipando", eventoService.buscarEventosParticapacaoAutor(autorLogado.getId()));
		return Constants.TEMPLATE_INDEX_AUTOR;
	}
	
	@RequestMapping(value="/participarEvento", method = RequestMethod.GET)
	public String eventosAtivos(Model model){
		Pessoa autorLogado = getAutorLogado();
		model.addAttribute("eventosParaParticipar", eventoService.eventosParaParticipar(autorLogado.getId()));
		model.addAttribute("eventoParticipando", eventoService.buscarEventosParticapacaoAutor(autorLogado.getId()));
		return Constants.TEMPLATE_INDEX_AUTOR;
	}
		
	@RequestMapping(value="/participarEvento", method = RequestMethod.POST)
	public String participarEvento(@RequestParam String idEvento, Model model, RedirectAttributes redirect){
		if(!eventoService.existeEvento(Long.parseLong(idEvento))){
			redirect.addFlashAttribute(EVENTO_VAZIO_ERROR, messageService.getMessage(ID_EVENTO_VAZIO_ERROR));
			return "redirect:/autor/participarEvento";
		}
		
		Pessoa autorLogado = getAutorLogado();
		Evento evento = eventoService.buscarEventoPorId(Long.parseLong(idEvento));
		
		if(evento != null){
			if(evento.getEstado() == EstadoEvento.ATIVO){
				ParticipacaoEvento participacaoEvento = new ParticipacaoEvento();
				participacaoEvento.setEvento(evento);
				participacaoEvento.setPessoa(autorLogado);
				participacaoEvento.setPapel(Papel.AUTOR);
				
				participacaoEventoService.adicionarOuEditarParticipacaoEvento(participacaoEvento);
				redirect.addFlashAttribute(PARTICAPACAO_EVENTO_SUCESSO, messageService.getMessage(PARTICAPAR_EVENTO_SUCESSO));
			}else{
				redirect.addFlashAttribute(PARTICIPAR_EVENTO_INATIVO_ERROR, messageService.getMessage(PARTICIPAR_EVENTO_INATIVO));
			}
		}else{
			redirect.addFlashAttribute(EVENTO_INEXISTENTE_ERROR, messageService.getMessage(EVENTO_NAO_EXISTE));
			return "redirect:/autor";
		}
		return "redirect:/autor";
	}
	
	@RequestMapping(value = "/meusTrabalhos", method = RequestMethod.GET)
	public String listarEventosInativos(Model model) {
		Pessoa autorLogado = getAutorLogado();
		List<Evento> eventos =  eventoService.buscarEventosParticapacaoAutor(autorLogado.getId());
		if(eventos != null){
			List<String> trabalhosEventos = new ArrayList<>();
			
			for(Evento evento : eventos){
				if(submissaoService.existeTrabalhoNesseEvento(evento.getId())){
					if(revisaoService.existeTrabalhoNesseEvento(evento.getId())){
						trabalhosEventos.add(REVISAO);
					}else{
						trabalhosEventos.add(SUBMISSAO);
					}
				}else{
					trabalhosEventos.add(ENVIARTRABALHO);
				}
			}
			
			model.addAttribute("eventos", eventos);
			model.addAttribute("trabalhosEvento", trabalhosEventos);
		}else{
			model.addAttribute("naoHaTrabalhos", messageService.getMessage(NAO_HA_TRABALHOS));
		}
		return Constants.TEMPLATE_MEUS_TRABALHOS_AUTOR;
	}
	
//	@RequestMapping(value = "/enviarTrabalho/{id}", method = RequestMethod.GET)
//	public String enviarTrabalho(@PathVariable String id, Model model, RedirectAttributes redirect){
//		if(!eventoService.existeEvento(Long.parseLong(id))){
//			redirect.addFlashAttribute(EVENTO_VAZIO_ERROR, messageService.getMessage(ID_EVENTO_VAZIO_ERROR));
//			return "redirect:/autor/meusTrabalhos";
//		}
//		Evento evento = eventoService.buscarEventoPorId(Long.parseLong(id));
//		model.addAttribute("evento", evento);
//
//		return Constants.TEMPLATE_ENVIAR_TRABALHO_AUTOR;
//	}

	
	@RequestMapping(value = "/enviarTrabalhoForm/{id}", method = RequestMethod.GET)
	public String enviarTrabalhoForm(@PathVariable String id, Model model){
		model.addAttribute("trabalho", new Trabalho());
		model.addAttribute("eventoId", id);
		return Constants.TEMPLATE_ENVIAR_TRABALHO_FORM_AUTOR;
	}
	
	@RequestMapping(value = "/enviarTrabalhoForm", method = RequestMethod.POST)
	public String enviarTrabalhoForm(@Valid Trabalho trabalho, BindingResult result, @RequestParam("file") MultipartFile file, @RequestParam("eventoId") String eventoId,
			@RequestParam("nomeOrientador") String nomeOrientador, @RequestParam("emailOrientador") String emailOrientador, RedirectAttributes redirect){
       
		Evento evento = eventoService.buscarEventoPorId(Long.parseLong(eventoId));
		trabalho.setEvento(evento);
		
		trabalhoValidator.validate(trabalho, result);

		if(result.hasErrors()){
			return Constants.TEMPLATE_ENVIAR_TRABALHO_FORM_AUTOR;
		}else{
			if(validarArquivo(file)){
				Pessoa pessoaOrientador = pessoaService.getByEmail(emailOrientador);
				Pessoa pessoaAutor = getAutorLogado();
				if(pessoaOrientador != null){
					adicionarTrabalho(trabalho, pessoaOrientador, pessoaAutor, eventoId, file);
					return "redirect:/autor/meusTrabalhos";
				}else{
					Pessoa orientador = new Pessoa();
					orientador.setEmail(emailOrientador);
					orientador.setNome(nomeOrientador);
					orientador.setCpf("111.111.111-11");
					pessoaService.addOrUpdate(orientador);
					adicionarTrabalho(trabalho, orientador, pessoaAutor, eventoId, file);
					return "redirect:/autor/meusTrabalhos";
				}
			}else{
				redirect.addFlashAttribute("erro", messageService.getMessage(FORMATO_ARQUIVO_INVALIDO));
				return "redirect:/autor/enviarTrabalhoForm/"+ eventoId;
			}
		}
	}
	
	public Pessoa getAutorLogado(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String cpf = auth.getName();
		Pessoa autorLogado = pessoaService.getByCpf(cpf);
		return autorLogado;
	}
	public boolean validarArquivo(MultipartFile file){
		String fileExtentions = ".pdf";
		String fileName = file.getOriginalFilename();
		int lastIndex = fileName.lastIndexOf('.');
		String substring = fileName.substring(lastIndex, fileName.length());
		if(fileExtentions.contains(substring)){
			return true;
		}
		return false;
	}
	
	public void adicionarTrabalho(Trabalho trabalho, Pessoa pessoaOrientador, Pessoa pessoaAutor, String eventoId, MultipartFile file){
		ParticipacaoTrabalho participacaoTrabalhoOrientador = new ParticipacaoTrabalho();
		ParticipacaoTrabalho participacaoTrabalhoAutor = new ParticipacaoTrabalho();
		
		participacaoTrabalhoOrientador.setPessoa(pessoaOrientador);
		participacaoTrabalhoOrientador.setTrabalho(trabalho);
		participacaoTrabalhoOrientador.setPapel(Papel.ORIENTADOR);
		
		participacaoTrabalhoAutor.setPessoa(pessoaAutor);
		participacaoTrabalhoAutor.setTrabalho(trabalho);
		participacaoTrabalhoAutor.setPapel(Papel.AUTOR);
		
		Submissao submissao = new Submissao();
		
		Date data = new Date(System.currentTimeMillis());  

		submissao.setTipoSubmissao(TipoSubmissao.PARCIAL);
		submissao.setTrabalho(trabalho);
		submissao.setDataSubmissao(data);
		
		submissaoService.adicionarOuEditar(submissao);
		participacaoTrabalhoService.adicionarOuEditar(participacaoTrabalhoOrientador);
		participacaoTrabalhoService.adicionarOuEditar(participacaoTrabalhoAutor);
		storageService.store(file);
	}
}