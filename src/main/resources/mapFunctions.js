function ToggleGrid() {
	var sheet = document.getElementsByTagName("table")[0];
	if (sheet.style.borderCollapse == "collapse") {
		sheet.style.borderCollapse = "initial";
	}
	else {
		sheet.style.borderCollapse = "collapse";
	}
}

var showPlaces = true;

function ToggleNames() {
	var nameTexts = document.getElementsByClassName("paikka");
	if (!showPlaces) {
		for (i = 0; i < nameTexts.length; i++) {
			nameTexts[i].style.display = "block";
		}
		showPlaces = true;
	}
	else {
		for (i = 0; i < nameTexts.length; i++) {
			nameTexts[i].style.display = "none";
		}
		showPlaces = false;
	}
}

function Origo() {
	window.location.replace(window.location.pathname + "#origo");
}

function Base() {
	window.location.replace(window.location.pathname + "#base");
}

var prioVisibility = 1;
var prioMax = 3;

function maxVisibility() {
	prioMax = 1;
	var paikat = document.getElementsByClassName("paikka");
	for (i = 0; i < paikat.length; i++) {
		var prioriteetti = etsiPrioriteetti(paikat[i]);
		if (prioriteetti > prioMax) {
			prioMax = prioriteetti;
		}
	}

	setVisibility();
}

function toggleVisibility() {
	prioVisibility++;
	if (prioVisibility > prioMax) {
		prioVisibility = 1;
	}
	setVisibility();
}

function setVisibility() {
	var paikat = document.getElementsByClassName("paikka");
	for (i = 0; i < paikat.length; i++) {
		var prioriteetti = etsiPrioriteetti(paikat[i]);
		if (prioriteetti <= prioVisibility) {
			paikat[i].style.display = 'block';
		} else {
			paikat[i].style.display = 'none';
		}
	}
}

function etsiPrioriteetti(paikka) {
	var classLista = paikka.classList;
	for (j = 0; j < classLista.length; j++) {
		if (classLista[j].indexOf("prio") == 0) {
			foundPrio = parseInt(classLista[j].replace("prio", ""));
			if (isNaN(foundPrio) == false) {
				return foundPrio;
			}
		}
	}
	return -1;
}

function forceReload() {
	location.reload(true);
}

function getPos(e, div, grid) {
	x = e.clientX;
	y = e.clientY;
	scrollX = document.body.getBoundingClientRect().left;
	scrollY = document.body.getBoundingClientRect().top;
	divX = div.offsetLeft;
	divY = div.offsetTop;
	cursor = grid + " (" + Math.round(-scrollX + x - divX) + ", " + Math.round(-scrollY + y - divY) + ")";
	document.getElementById("cursor").innerHTML = cursor;
}

function stopTracking() {
	document.getElementById("cursor").innerHTML = "";
}
