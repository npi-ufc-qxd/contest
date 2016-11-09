$(document).ready(function(){
	//Parallax BG
	$('.parallax').parallax();

	//sideNav
	$(".button-collapse").sideNav();

	//Tooltips
	$('.tooltipped').tooltip({delay: 50});

	//select_initializer
	$('select').material_select();

	//select_initializer
	$('select').material_select();

	$(".listaRevisores" ).each(function(index){
		$(this).scroll(function(){});
	});

	trabalho_atual = function(e){
		$('.collection-item').attr('trabalho',e.id);
	};

	$('.modal-trigger').leanModal({
	    dismissible: false,
	});

	add = function(e,modal) {//vai receber o Objeto vindo da linkRevisor

		var seletor_lista = "#"+modal +" div.listaEscolhidos.collection";

		var listaescolhidos = $(seletor_lista);

	    var trabalhoId = $(e).attr('trabalho');

	    var seletor = $(e).attr('id');

	    var seletorRevisorEscolhido = "#esc_"+ seletor;

	    if (listaescolhidos.find(seletorRevisorEscolhido).length > 0){
	    	Materialize.toast('O mesmo organizador não pode ser alocado mais de uma vez no mesmo evento', 4000)
	    }else{
	      var a = $(e).clone(true);
	      a.attr('onclick',"remover(this,'"+modal+"');");
	      a.attr("id", "esc_"+seletor);

	      var token = $("meta[name='_csrf']").attr("content");

	      var data = {
		      		  	'revisorId': 	seletor.substr(seletor.lastIndexOf("_") + 1),
		    		  	'trabalhoId': 	trabalhoId,
		    		  };

	      $.ajax({
	    	  contentType: 'application/json;charset=UTF-8',
	    	  url: '../trabalho/revisor',
	    	  dataType: 'json',
	    	  headers: {"X-CSRF-TOKEN":token},
	    	  type: 'POST',
	    	  cache: false,
	    	  processData: false,
	    	  data: JSON.stringify(data),
	    	  success: function (data) {
	    		  		  listaescolhidos.append(a);
	        			},
	          error: function (data, error) {
	        	}
	    	});
	    }
	  }

	remover = function(e){
		var idRevisor = $(e).attr('id');
		idRevisor = idRevisor.substr(idRevisor.lastIndexOf("_") + 1);

		var idTrabalho = $(e).attr('trabalho');
		var token = $("meta[name='_csrf']").attr("content");

		var data = {
	    	'revisorId': 	idRevisor,
			'trabalhoId': 	idTrabalho,
		};



		$.ajax({
	  	  contentType: 'application/json;charset=UTF-8',
	  	  url: '../trabalho/removerRevisor',
	  	  dataType: 'json',
	  	  headers: {"X-CSRF-TOKEN":token},
	  	  type: 'POST',
	  	  cache: false,
	  	  processData: false,
	  	  data: JSON.stringify(data),
	  	  success: function (data) {
	  		$(e).remove();
	      },
	      error: function (data, error) {
	      }
	  	});
	}

	//Inicialização do Jpages para realizar a paginação
    $("div.holder").jPages({
    	containerID : "itemContainer",
    	perPage: 6 //Quantidade de elementos exibidos por página.
    });
});