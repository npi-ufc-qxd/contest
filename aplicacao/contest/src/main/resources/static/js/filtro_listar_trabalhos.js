$(document).ready(function(){
	
	var selectFiltro = $("#filtro").on('change', function(){
		var opcaoDeFiltro = $(this).val();
		
		if(opcaoDeFiltro == "nao_revisado"){
			$(".card.horizontal").hide();
			$("[revisado=false]").show();
		} else if(opcaoDeFiltro == "todos"){
			$(".card.horizontal").show();
		} else if(opcaoDeFiltro == "best"){
			$(".card.horizontal").hide();
			$("[best=true]").show();
		} else {
			$(".card.horizontal").hide();
			var mostrar = ".card.horizontal." + opcaoDeFiltro;
			$(mostrar).show();
		}
		
	});
	
	
	
});