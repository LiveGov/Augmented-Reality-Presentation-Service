

function updateTextInput(val) {
  document.getElementById('trackImRot').value=val; 
}


//--------------------------------------------------
function UrlExists(url){
	var http = new XMLHttpRequest();
	http.open('HEAD', url, false);
	http.send();
	return http.status!=404;
}

//--------------------------------------------------
function toggle_visibility(id_div, id_bt) {
	var e = document.getElementById(id_div);
	var b = document.getElementById(id_bt);

	if(e.style.display == ''){
		e.style.display = 'none';
		b.className ='toggler off';
	} else {
		e.style.display = '';
		b.className ='toggler';
	}
}
 

//--------------------------------------------------
function toggle_yesno(id_bt) {
	var b = document.getElementById(id_bt);
	if(b.value =='yes'){
		b.style.backgroundColor ='#f00';
		b.value  = 'no';
	} else {
		b.style.backgroundColor ='#0f0';
		b.value  = 'yes';
	}
}
//--------------------------------------------------