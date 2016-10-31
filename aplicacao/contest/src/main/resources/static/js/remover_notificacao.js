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
	
	removerNotificacao = function(e){
		var idNotificacao = $(e).attr('id');
		var token = $("meta[name='_csrf']").attr("content");
		
		var data = {
	    	'notificacaoId': 	idNotificacao,
		};
		
		$(e).remove();
		
		$.ajax({
	  	  contentType: 'application/json;charset=UTF-8',
	  	  url: '/notificacao/removerNotificacao',
	  	  dataType: 'json',
	  	  headers: {"X-CSRF-TOKEN":token},
	  	  type: 'POST',
	  	  cache: false, 
	  	  processData: false, 
	  	  data: JSON.stringify(data),
	  	  success: function (data) {
	  		location.reload();	
	      			},
	      error: function (data, error) {
	    	  alert();
	      }
	      
	  	});
		
	}
});