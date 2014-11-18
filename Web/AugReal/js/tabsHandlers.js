// Main
function activateMainTab(pageId) {
	document.getElementById('t1h').style.background="#eee";
	document.getElementById('t2h').style.background="#eee";
	document.getElementById('t3h').style.background="#eee";
	document.getElementById(pageId+'h').style.background="#ccc";
	
	var tabMainCtrl = document.getElementById('tabMainCtrl');
	var MainpageToActivate = document.getElementById(pageId);
	for (var i = 0; i < tabMainCtrl.childNodes.length; i++) {
		var nodeMain = tabMainCtrl.childNodes[i];
		if (nodeMain.nodeType == 1)
			nodeMain.style.display = (nodeMain == MainpageToActivate) ? '' : 'none';
	}
}

// 3D Models
function activate3dModelsTab(pageId) {
	document.getElementById('m1h').style.background="#eee";
	document.getElementById('m2h').style.background="#eee";
	document.getElementById('m3h').style.background="#eee";
	document.getElementById(pageId+'h').style.background="#bbb";
	
	var tabCtrl = document.getElementById('tab3dModelsCtrl');
	var pageToActivate = document.getElementById(pageId);
	for (var i = 0; i < tabCtrl.childNodes.length; i++) {
		var node = tabCtrl.childNodes[i];
		if (node.nodeType == 1)
			node.style.display = (node == pageToActivate) ? '' : 'none';
	}
}

// VisualRecognition
function activateVisRecTab(pageId) {
	document.getElementById('v1h').style.background="#eee";
	document.getElementById('v2h').style.background="#eee";
	document.getElementById(pageId+'h').style.background="#bbb";
	
	var tabCtrl = document.getElementById('tabVisRecCtrl');
	var pageToActivate = document.getElementById(pageId);
	for (var i = 0; i < tabCtrl.childNodes.length; i++) {
		var node = tabCtrl.childNodes[i];
		if (node.nodeType == 1)
			node.style.display = (node == pageToActivate) ? '' : 'none';
	}
}



// Classification
function activateClassificationTab(pageId) {
	document.getElementById('c1h').style.background="#eee";
	document.getElementById('c2h').style.background="#eee";
	document.getElementById(pageId+'h').style.background="#aaa";

	var tabMainCtrl = document.getElementById('tabClassificationCtrl');
	var MainpageToActivate = document.getElementById(pageId);
	for (var i = 0; i < tabMainCtrl.childNodes.length; i++) {
		var nodeMain = tabMainCtrl.childNodes[i];
		if (nodeMain.nodeType == 1)  
			nodeMain.style.display = (nodeMain == MainpageToActivate) ? '' : 'none';
	}
}
