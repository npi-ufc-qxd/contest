package ufc.quixada.npi.contest.config.security;

import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

import ufc.quixada.npi.contest.model.Papel.Tipo;
import ufc.quixada.npi.contest.model.ParticipacaoEvento;
import ufc.quixada.npi.contest.model.ParticipacaoTrabalho;
import ufc.quixada.npi.contest.model.Pessoa;

public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations{
	
	public CustomMethodSecurityExpressionRoot(Authentication authentication) {
        super(authentication);
    }
   //RESTRIÇÕES PARA ORGANIZADOR
    public boolean isOrganizadorInEvento(Long eventoId){
    	Pessoa pessoa = (Pessoa) this.getPrincipal();
    	
    	for(ParticipacaoEvento participacao : pessoa.getParticipacoesEvento()){
    		if(participacao.getEvento().getId().equals(eventoId) && participacao.getPapel()== Tipo.ORGANIZADOR){
    			return true;
    		}
    	}
    	return false;
    }
    
    //RESTRIÇÕES PARA REVISOR
    
    public boolean isRevisorInTrabalho(Long trabalhoId){
    	Pessoa pessoa = (Pessoa)getPrincipal();
    	
    	for(ParticipacaoTrabalho participacao : pessoa.getParticipacoesTrabalho()){
    		if(participacao.getTrabalho().getId().equals(trabalhoId) && participacao.getPapel() == Tipo.REVISOR){
    			return true;
    		}
    	}
    	return false;
    }
   
	@Override
	public void setFilterObject(Object filterObject) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getFilterObject() {
		return null;
	}

	@Override
	public void setReturnObject(Object returnObject) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getReturnObject() {
		return null;
	}

	@Override
	public Object getThis() {
		return null;
	}
}
