$(document).ready(function(){
	selecionarTodos = function (){
		$("[name=trabalhosIds]").prop("checked", true);
	}
   
	removerTodos = function(){
		$("[name=trabalhosIds]").prop("checked",false);
	}
});