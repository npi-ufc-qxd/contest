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
   
    public boolean isOrganizadorInEvento(Long eventoId){
    	Pessoa pessoa = (Pessoa) this.getPrincipal();
    	for(ParticipacaoEvento participacacao : pessoa.getParticipacoesEvento()){
    		if(participacacao.getEvento().getId()==eventoId && participacacao.getPapel()== Tipo.ORGANIZADOR){
    			return true;
    		}
    	}
    	return false;
    }
    
    public boolean isOrganizador(){
    	Pessoa pessoa = (Pessoa) this.getPrincipal();
    	System.out.println(pessoa.getCpf());
    	//for(ParticipacaoEvento participacao : pessoa.getParticipacoesEvento()){
    	//	if(participacao.equals(Tipo.ORGANIZADOR)){
    	//		return true;
    	//	}
    	//}
    	return true;
    }
    
    public boolean isRevisorInEvento(Long eventoId){
    	Pessoa pessoa = (Pessoa) this.getPrincipal();
    	for(ParticipacaoEvento participacacao : pessoa.getParticipacoesEvento()){
    		if(participacacao.getEvento().getId()==eventoId && participacacao.getPapel()== Tipo.REVISOR){
    			return true;
    		}
    	}
    	return false;
    }
    
    public boolean isAutorInEvento(Long eventoId){
    	Pessoa pessoa = (Pessoa) this.getPrincipal();
    	for(ParticipacaoEvento participacacao : pessoa.getParticipacoesEvento()){
    		if(participacacao.getEvento().getId()==eventoId && participacacao.getPapel()== Tipo.AUTOR){
    			return true;
    		}
    	}
    	return false;
    }
    
    public boolean isAutorInTrabalho(Long trabalhoId){
    	Pessoa pessoa = (Pessoa) this.getPrincipal();
    	for(ParticipacaoTrabalho participacacao : pessoa.getParticipacoesTrabalho()){
    		if(participacacao.getTrabalho().getId() == trabalhoId && participacacao.getPapel()== Tipo.AUTOR){
    			return true;
    		}
    	}
    	return false;
    }
    
    public boolean isCoautorInTrabalho(Long trabalhoId){
    	Pessoa pessoa = (Pessoa) this.getPrincipal();
    	for(ParticipacaoTrabalho participacacao : pessoa.getParticipacoesTrabalho()){
    		if(participacacao.getTrabalho().getId() == trabalhoId && participacacao.getPapel()== Tipo.COAUTOR){
    			return true;
    		}
    	}
    	return false;
    }

    
    public boolean isOrientadorInTrabalho(Long trabalhoId){
    	Pessoa pessoa = (Pessoa) this.getPrincipal();
    	for(ParticipacaoTrabalho participacacao : pessoa.getParticipacoesTrabalho()){
    		if(participacacao.getTrabalho().getId() == trabalhoId && participacacao.getPapel()== Tipo.ORIENTADOR){
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
