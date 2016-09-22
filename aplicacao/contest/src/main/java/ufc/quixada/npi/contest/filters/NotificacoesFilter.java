package ufc.quixada.npi.contest.filters;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ModelAttribute;

import ufc.quixada.npi.contest.model.Notificacao;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.service.NotificacaoService;
import ufc.quixada.npi.contest.service.PessoaService;



@WebFilter("/*")
public class NotificacoesFilter implements Filter {

	private static final String NOTIFICACOES = "notificacoes";

	@Autowired
	private PessoaService pessoaService;
	
	@Autowired
	private NotificacaoService notificacaoService;
	
	@ModelAttribute("pessoas")
	public List<Pessoa> listaPossiveisOrganizadores() {
		return pessoaService.getPossiveisOrganizadores();
	}
	
	
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
	}
	

	@Override
	public void doFilter(ServletRequest request, 
	        ServletResponse response, FilterChain chain)
	        throws IOException, ServletException {
	                
	    try {
	    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			String cpf = auth.getName();
			Pessoa autorLogado = pessoaService.getByCpf(cpf);

			List<Notificacao> listaNotificacao = notificacaoService.listaNotificacaoDePessoa(autorLogado);
			//model.addAttribute(NOTIFICACOES,listaNotificacao);
	    	
	    } catch (Exception e) {
	    	//
	    }
	}

}
