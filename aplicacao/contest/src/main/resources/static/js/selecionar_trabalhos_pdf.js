$(document).ready(function(){
	selecionarTodos = function (){
		$("[type=checkbox]").prop("checked", true);
	}
   
	removerTodos = function(){
		$("[type=checkbox]").prop("checked",false);
	}
});