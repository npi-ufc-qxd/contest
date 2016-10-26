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
	
	$( "#listaRevisores" ).scroll(function(){});
	
	trabalho_atual = function(e){
		$('.collection-item').attr('trabalho',e.id);
	};
	
	$('.modal-trigger').leanModal({
	    dismissible: false,
	});
	
	add = function(e,modal) {//vai receber o Objeto vindo da linkRevisor
	    var listaescolhidos = $("#listaEscolhidos");
	    
	    var trabalhoId = $(e).attr('trabalho');
	    
	    var seletor = '#'+modal+'_'+$(e).attr('id');
	    
	    if ($(seletor).length>0){
	    	Materialize.toast('O mesmo organizador não pode ser alocado mais de uma vez no mesmo evento', 4000)
	    }else{
	      var a = $(e).clone(true);              
	      a.attr('onclick',"remover(this,'"+modal+"');");
	      a.attr("id", modal+'_'+$(e).attr('id'));
	      
	      
	      var token = $("meta[name='_csrf']").attr("content");
	      
	      var data = {
		      		  	'revisorId': 	$(e).attr('id'),
		    		  	'trabalhoId': 	trabalhoId,
		    		  };
	
	      $.ajax({
	    	  contentType: 'application/json;charset=UTF-8',
	    	  url: '/eventoOrganizador/evento/trabalho/revisor',
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
	
	remover = function(e,modal){
		modal = modal+'_';
		var idRevisor = $(e).attr('id').replace(modal,'');
		var idTrabalho = $(e).attr('trabalho');
		var token = $("meta[name='_csrf']").attr("content");
		
		var data = {
	    	'revisorId': 	idRevisor,
			'trabalhoId': 	idTrabalho,
		};
		
		$(e).remove();
		
		$.ajax({
	  	  contentType: 'application/json;charset=UTF-8',
	  	  url: '/eventoOrganizador/evento/trabalho/removerRevisor',
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
});