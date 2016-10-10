package ufc.quixada.npi.contest.util;

import org.json.JSONObject;

public class RevisaoJSON {

	public String toJson(String formatacao, String originalidade, String merito, 
			String clareza, String qualidade, String relevancia, String auto_avaliacao,
			String avaliacao_geral, String comentarios, String indicar){
		
		JSONObject json = new JSONObject();
		json.put("formatacao", formatacao);
		json.put("originalidade", originalidade);
		json.put("merito", merito);
		json.put("clareza", clareza);
		json.put("qualidade", qualidade);
		json.put("relevancia", auto_avaliacao);
		json.put("avaliacao_geral", avaliacao_geral);
		json.put("comentarios", comentarios);
		json.put("indicacao", indicar);
		
		return json.toString();
	}
	
}
