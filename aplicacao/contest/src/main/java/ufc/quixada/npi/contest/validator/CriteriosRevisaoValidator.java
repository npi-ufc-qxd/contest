package ufc.quixada.npi.contest.validator;

public class CriteriosRevisaoValidator {

	public boolean validate(String ... campos){
		for(String campo : campos){
			if(campo == null) return false;
		}
		return true;
	}
}