function text_msg() {
		var elem				= document.getElementById("form:customer").value;
	    var selecting 			= elem.options[elem.options.selectedIndex];
	    var txtCustomerValue	= selecting.text;
		var txtContactValue		= document.getElementById('form:contact').value;
		var txtStartValue		= document.getElementById('form:start').value;
		var txtDestinationValue	= document.getElementById('form:destination').value;
		var txtDescriptionValue	= document.getElementById('form:description').value;
		var txtKmValue			= document.getElementById('form:km').value;
		var result = "Cliente: " 	+ txtCustomerValue 		+ " Contato: "	+  txtContactValue		+
					" Base: " 	+ txtStartValue 		+ " Dest: "	+ txtDestinationValue	+
					" Obs: "	+ txtDescriptionValue	+ " KM: " 	+ txtKmValue;
		document.getElementById('form:editor').value = result;
}
