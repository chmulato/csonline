jQuery(function(){
	jQuery('input[id*=valor]').maskMoney({symbol:'R$ ', showSymbol:true, thousands:'.', decimal:',', symbolStay: true});

	jQuery('input[id*=banco]').mask("999",{placeholder:"0"});
	jQuery('input[id*=agencia]').mask("9999",{placeholder:"0"});
	jQuery('input[id*=cheque]').mask("999999",{placeholder:"0"});
	
	jQuery('input[id*=dataEmissao_input]').mask("99/99/9999",{placeholder:" "});
	jQuery('input[id*=dataVencimento_input]').mask("99/99/9999",{placeholder:" "});
	
	jQuery('input[id*=searchPeriodoInicio_input]').mask("99/99/9999",{placeholder:" "});
	jQuery('input[id*=searchPeriodoFinal_input]').mask("99/99/9999",{placeholder:" "});
});

PrimeFaces.locales['pt_BR'] = {
	    closeText: 'Fechar',
	    prevText: 'Anterior',
	    nextText: 'Próximo',
	    currentText: 'Começo',
	    monthNames: ['Janeiro','Fevereiro','Março','Abril','Maio','Junho','Julho','Agosto','Setembro','Outubro','Novembro','Dezembro'],
	    monthNamesShort: ['Jan','Fev','Mar','Abr','Mai','Jun', 'Jul','Ago','Set','Out','Nov','Dez'],
	    dayNames: ['Domingo','Segunda','Terça','Quarta','Quinta','Sexta','Sábado'],
	    dayNamesShort: ['Dom','Seg','Ter','Qua','Qui','Sex','Sáb'],
	    dayNamesMin: ['D','S','T','Q','Q','S','S'],
	    weekHeader: 'Semana',
	    firstDay: 1,
	    isRTL: false,
	    showMonthAfterYear: false,
	    yearSuffix: '',
	    timeOnlyTitle: 'Só Horas',
	    timeText: 'Tempo',
	    hourText: 'Hora',
	    minuteText: 'Minuto',
	    secondText: 'Segundo',
	    ampm: false,
	    month: 'Mês',
	    week: 'Semana',
	    day: 'Dia',
	    allDayText : 'Todo Dia'
	};
