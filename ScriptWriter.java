import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;

public class ScriptWriter extends FileExpert {
	private String scriptName;
	
	public ScriptWriter(String scriptName) {
		this.scriptName = scriptName;
	}
	
	public void writeScriptFile() {
		FileWriter kirjuri = null;
		File js = new File(scriptName);
		try {
			super.removeFile(js);
			
			kirjuri = new FileWriter(js);
			kirjoitaToggleGrid(kirjuri);
			kirjoitaToggleNames(kirjuri);
			kirjoitaMene(kirjuri, "Origo");
			kirjoitaMene(kirjuri, "Base");
			kirjoitaPrioriteetit(kirjuri);
			kirjoitaReload(kirjuri);
			kirjoitaMousePosition(kirjuri);
		} catch (Exception e) {
			System.out.println("FileWriter failed while writing mapFunctions.js");
		} finally {
			try {
				kirjuri.close();
				js.setReadable(true, false);
			} catch (Exception e) {}
		}
	}
	
	private void kirjoitaToggleGrid(FileWriter kirjuri) throws Exception {
		kirjuri.append("function ToggleGrid(){\n");
		kirjuri.append("\tvar sheet=document.getElementsByTagName(\"table\")[0];\n");
		kirjuri.append("\tif (sheet.style.borderCollapse == \"collapse\") {\n");
		kirjuri.append("\t\tsheet.style.borderCollapse = \"initial\";\n");
		kirjuri.append("\t}\n");
		kirjuri.append("\telse {\n");
		kirjuri.append("\t\tsheet.style.borderCollapse = \"collapse\";\n");
		kirjuri.append("\t}\n");
		kirjuri.append("}\n\n");
	}
	
	private void kirjoitaToggleNames(FileWriter kirjuri) throws Exception {
		kirjuri.append("var showPlaces=true;\n\n");
		
		kirjuri.append("function ToggleNames(){\n");
		kirjuri.append("\tvar nameTexts = document.getElementsByClassName(\"paikka\");\n");
		kirjuri.append("\tif(!showPlaces){\n");
		kirjuri.append("\t\tfor(i=0; i<nameTexts.length; i++){\n");
		kirjuri.append("\t\t\tnameTexts[i].style.display = \"block\";\n");
		kirjuri.append("\t\t}\n");
		kirjuri.append("\t\tshowPlaces = true;\n");
		kirjuri.append("\t}\n");
		kirjuri.append("\telse{\n");
		kirjuri.append("\t\tfor(i=0; i<nameTexts.length; i++){\n");
		kirjuri.append("\t\t\tnameTexts[i].style.display = \"none\";\n");
		kirjuri.append("\t\t}\n");
		kirjuri.append("\t\tshowPlaces = false;\n");
		kirjuri.append("\t}\n");
		kirjuri.append("}\n\n");
	}
	
	private void kirjoitaMene(FileWriter kirjuri, String anchor) throws Exception {
		kirjuri.append("function " + anchor + "(){" + "\n");
		kirjuri.append("\twindow.location.replace(window.location.pathname + \"#" + anchor.toLowerCase() + "\");" + "\n");
		kirjuri.append("}" + "\n\n");
	}
	
	private void kirjoitaPrioriteetit(FileWriter kirjuri) throws Exception {
		kirjoitaPrioAlustus(kirjuri);
		kirjoitaPrioToggle(kirjuri);
		kirjoitaPrioSet(kirjuri);
		kirjoitaPrioHaku(kirjuri);
	}
	
	private void kirjoitaPrioAlustus(FileWriter kirjuri) throws Exception {
		kirjuri.append("var prioVisibility = 1;\n");
		kirjuri.append("var prioMax = 3;\n\n");
		
		kirjuri.append("function maxVisibility(){\n");
		kirjuri.append("\tprioMax = 1;\n");
		kirjuri.append("\tvar paikat = document.getElementsByClassName(\"paikka\");\n");
		kirjuri.append("\tfor (i=0; i<paikat.length; i++){\n");
		kirjuri.append("\t\tvar prioriteetti = etsiPrioriteetti(paikat[i]);\n");
		kirjuri.append("\t\tif (prioriteetti > prioMax){\n");
		kirjuri.append("\t\t\tprioMax = prioriteetti;\n");
		kirjuri.append("\t\t}\n");
		kirjuri.append("\t}\n\n");
		
		kirjuri.append("\tsetVisibility();\n");
		kirjuri.append("}\n\n");
	}
	
	private void kirjoitaPrioToggle(FileWriter kirjuri) throws Exception {
		kirjuri.append("function toggleVisibility(){\n");
		kirjuri.append("\tprioVisibility++;\n");
		kirjuri.append("\tif (prioVisibility > prioMax){\n");
		kirjuri.append("\t\tprioVisibility = 1;\n");
		kirjuri.append("\t}\n");
		kirjuri.append("\tsetVisibility();\n");
		kirjuri.append("}\n\n");
	}
	
	private void kirjoitaPrioSet(FileWriter kirjuri) throws Exception {
		kirjuri.append("function setVisibility(){\n");
		kirjuri.append("\tvar paikat = document.getElementsByClassName(\"paikka\");\n");
		kirjuri.append("\tfor(i=0; i<paikat.length; i++){\n");
		kirjuri.append("\t\tvar prioriteetti = etsiPrioriteetti(paikat[i]);\n");
		kirjuri.append("\t\tif (prioriteetti <= prioVisibility){\n");
		kirjuri.append("\t\t\tpaikat[i].style.display = 'block';\n");
		kirjuri.append("\t\t} else {\n");
		kirjuri.append("\t\t\tpaikat[i].style.display = 'none';\n");
		kirjuri.append("\t\t}\n");
		kirjuri.append("\t}\n");
		kirjuri.append("}\n\n");
	}
	
	private void kirjoitaPrioHaku(FileWriter kirjuri) throws Exception {
		kirjuri.append("function etsiPrioriteetti(paikka){\n");
		kirjuri.append("\tvar classLista = paikka.classList;\n");
		kirjuri.append("\tfor(j=0; j<classLista.length; j++){\n");
		kirjuri.append("\t\tif (classLista[j].indexOf(\"prio\") == 0) {\n");
		kirjuri.append("\t\t\tfoundPrio = parseInt(classLista[j].replace(\"prio\",\"\"));\n");
		kirjuri.append("\t\t\tif (isNaN(foundPrio) == false) {\n");
		kirjuri.append("\t\t\t\treturn foundPrio;\n");
		kirjuri.append("\t\t\t}\n");
		kirjuri.append("\t\t}\n");
		kirjuri.append("\t}\n");
		kirjuri.append("\treturn -1;\n");
		kirjuri.append("}\n\n");
	}
	
	private void kirjoitaReload(FileWriter kirjuri) throws Exception {
		kirjuri.append("function forceReload(){" + "\n");
		kirjuri.append("\tlocation.reload(true);" + "\n");
		kirjuri.append("}" + "\n\n");
	}
	
	private void kirjoitaMousePosition(FileWriter kirjuri) throws Exception {
		kirjuri.append("function getPos(e, div, grid){" + "\n");
		kirjuri.append("\tx=e.clientX;" + "\n");
		kirjuri.append("\ty=e.clientY;" + "\n");
		kirjuri.append("\tscrollX = document.body.getBoundingClientRect().left;" + "\n");
		kirjuri.append("\tscrollY = document.body.getBoundingClientRect().top;" + "\n");
		kirjuri.append("\tdivX = div.offsetLeft;" + "\n");
		kirjuri.append("\tdivY = div.offsetTop;" + "\n");
		kirjuri.append("\tcursor=grid + \" (\" + Math.round( -scrollX + x - divX ) + \", \" + Math.round( -scrollY + y - divY ) + \")\";" + "\n");
		kirjuri.append("\tdocument.getElementById(\"cursor\").innerHTML=cursor;" + "\n");
		kirjuri.append("}" + "\n\n");

		kirjuri.append("function stopTracking(){" + "\n");
		kirjuri.append("\tdocument.getElementById(\"cursor\").innerHTML=\"\";" + "\n");
		kirjuri.append("}" + "\n\n");
	}
}

/*
function ToggleGrid(){
	var sheet=document.getElementsByTagName("table")[0];
	if (sheet.style.borderCollapse == "collapse") {
		sheet.style.borderCollapse = "initial";
	}
	else {
		sheet.style.borderCollapse = "collapse";
	}
}

var showPlaces=true;

function ToggleNames(){
	var nameTexts = document.getElementsByClassName("paikka");
	if(!showPlaces){
		for(i=0; i<nameTexts.length; i++){
			nameTexts[i].style.display = "block";
		}
		showPlaces = true;
	}
	else{
		for(i=0; i<nameTexts.length; i++){
			nameTexts[i].style.display = "none";
		}
		showPlaces = false;
	}
}

var prioVisibility = 1;
var prioMax = 3;

function maxVisibility(){
	prioMax = 1;
	var paikat = document.getElementsByClassName("paikka");
	for (i=0; i<paikat.length; i++){
		var prioriteetti = etsiPrioriteetti(paikat[i]);
		if (prioriteetti > prioMax){
			prioMax = prioriteetti;
		}
	}
	
	setVisibility();
}

function toggleVisibility(){
	prioVisibility++;
	if (prioVisibility > prioMax){
		prioVisibility = 1;
	}
	setVisibility();
}

function setVisibility(){
	var paikat = document.getElementsByClassName("paikka");
	for(i=0; i<paikat.length; i++){
		var prioriteetti = etsiPrioriteetti(paikat[i]);
		if (prioriteetti <= prioVisibility){
			paikat[i].style.display = 'block';
		} else {
			paikat[i].style.display = 'none';
		}
	}
}

function etsiPrioriteetti(paikka){
	var classLista = paikka.classList;
	for(j=0; j<classLista.length; j++){
		if (classLista[j].indexOf("prio") == 0) {
			foundPrio = parseInt(classLista[j].replace("prio",""));
			if (isNaN(foundPrio) == false) {
				return foundPrio;
			}
		}
	}
	return -1;
}

function Origo(){
	window.location.replace(window.location.pathname + "#origo");
}

function Base(){
	window.location.replace(window.location.pathname + "#base");
}

*/