package com.jmapbundler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileNotFoundException;

import javax.swing.JOptionPane;

import com.jmapbundler.catalog.ImageCatalog;

public class Map {
	private static ArrayList<String> maailmat = new ArrayList<String>();
	private static HashMap<String, String> maailmaBaset = new HashMap<String, String>();
	private static ArrayList<ArrayList<String>> minecraftKansiot = new ArrayList<ArrayList<String>>();
	
	private static ArrayList<String> maailmaKansiot = new ArrayList<String>();
	private static String maailma;
	
	//Mac:
	//private static String minecraftKansio = "/Applications/FTB/Voxel/minecraft/saves/mapwriter_mp_worlds/130_234_179_149_25565/images/z1/";
	
	private static int x0=0, x1=0, y0=0, y1=0;
	private static HashMap<String, ArrayList<String>> paikat;

	private final static ImageCatalog imageCatalog = new ImageCatalog();
	
	public static void main(String[] args) {
		MapMerger karttapiirturi = new MapMerger();
		etsiPohjatiedot();
		
		for (int i=0; i<maailmat.size(); i++) {
			maailma = maailmat.get(i);
			maailmaKansiot = minecraftKansiot.get(i);
			
			File tiedosto = new File(maailma + ".html");
			poistaTiedosto(tiedosto);
			
			etsiUlottuvuudet();
			karttapiirturi.liimaaKartatYhteen(maailma, maailmaKansiot, x0, x1, y0, y1);
			annaKatseluoikeuksia("MapMerge/" + maailma + "/MergeMap/");
			luoTekstitValmiiksi();
			
			FileWriter kirjuri = null;
			try {
				kirjuri = new FileWriter(tiedosto);
				kirjoitaNettisivu(kirjuri);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("FileWriter kaatui.");
			} finally {
				try {
					kirjuri.close();
					tiedosto.setReadable(true, false);
				} catch(Exception e) {}
			}
		}
		
		luoCSS();
		luoJavaskripti();
		if (maailmat.size() > 1) {
			luoKoostesivu();
			luoIndexsivu();
		}
		luoKatselija();
		luoKatalogi();
	}
	
	private static void etsiPohjatiedot() {
		Scanner lukija = null;
		
		File basics = new File("Basics.txt");
		
		try {
			lukija = new Scanner(basics);
			luePohjatiedot(lukija);
		} catch (Exception e) {
			e.printStackTrace();
			if (maailmat.size() == 0) {
				alustaTiedostotiedot();
			}
		} finally {
			try {
				lukija.close();
			} catch(Exception e) {}
		}
	}
	
	private static void luePohjatiedot(Scanner lukija) {
		while (lukija.hasNextLine()) {
			String rivi = lukija.nextLine();
			if (rivi.length()==0)
				break;
			maailmat.add(rivi);
			maailmaKansiot = new ArrayList<String>();
			while (lukija.hasNextLine()) {
				rivi = lukija.nextLine();
				if (rivi.length()==0) {
					break; //break
				}
				if (rivi.startsWith("(")) {
					maailmaBaset.put(maailmat.get( maailmat.size()-1), rivi);
					continue;
				}
				maailmaKansiot.add(teeKansiopolku(rivi));
			}
			minecraftKansiot.add(maailmaKansiot);
		}
	}
	
	private static String teeKansiopolku(String annettuOsoite) {
		if (annettuOsoite.contains("\\")) {
			if (!annettuOsoite.endsWith("\\")) {
				return annettuOsoite + "\\";
			}
			return annettuOsoite;
		}
		else {
			if (!annettuOsoite.endsWith("/")) {
				return annettuOsoite + "/";
			}
			return annettuOsoite;
		}
	}
	
	private static void alustaTiedostotiedot() {
		if (JOptionPane.showConfirmDialog(null, "Do you want to input your world files?", "Minecraft Maps", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			maailmat.add( JOptionPane.showInputDialog("Decide world name (map id):") );
			maailmaKansiot = new ArrayList<String>();
			maailmaKansiot.add( JOptionPane.showInputDialog("Write whole path to z1 pictures:") );
			minecraftKansiot.add(maailmaKansiot);
			
			luoPerustiedosto(maailmat.get(maailmat.size()-1), maailmaKansiot.get(0));
		}
		else {
			maailmat.add("Locations");
			//Windows:
			maailmaKansiot = new ArrayList<String>();
			//TODO
			//maailmaKansiot.add( "/Users/Shikkoku/Games/Voxel/minecraft/saves/mapwriter_mp_worlds/130_234_179_149_25565/images/z1/");
			minecraftKansiot.add(maailmaKansiot);
		}
	}
	
	private static void poistaTiedosto(File tiedosto) {
		if (tiedosto.exists()) {
			tiedosto.delete();
		}
	}
	
	private static void luoPerustiedosto(String maailmanNimi, String osoite) {
		File tiedosto = new File("Basics.txt");
		poistaTiedosto(tiedosto);
		FileWriter kirjuri = null;
		try {
			kirjuri = new FileWriter(tiedosto);
			kirjuri.append(maailmanNimi + "\n");
			kirjuri.append(osoite + "\n\n");
			kirjoitaOhjeet(kirjuri);
		} catch (Exception e) {
			System.out.println("Creating Basics.txt failed.");
			e.printStackTrace();
		} finally {
			try {
				kirjuri.close();
			} catch (Exception e) {}
		}
	}
	
	private static void kirjoitaOhjeet(FileWriter kirjuri) throws Exception {
		kirjuri.append("\nInstructions:\n");
		kirjuri.append("-different worlds must be separated by one empty line" + "\n");
		kirjuri.append("-one world can have many locations for pictures that are listed on consequent lines" + "\n");
		kirjuri.append("-comments must be at the end with at least two empty lines separating" + "\n\n");
		
		kirjuri.append("Text over map:\n");
		kirjuri.append("Each WorldName.txt should contain:\n");
		kirjuri.append("1) lines of: x-coordinate y-coordinate text" + "\n");
		kirjuri.append("2) definition for which picture the previous lines of text are: x-coordinate y-coordinate" + "\n");
		kirjuri.append("-append 1) and 2) for the next map grid image after 2) for the previous set" + "\n");
	}
	
	private static void etsiUlottuvuudet() {
		boolean ulottuvuudenMaaritysAloitettu = false;
		x0=0;
		x1=0;
		y0=0;
		y1=0;
		
		for (String minecraftKansio : maailmaKansiot) {
			File kansio = new File(minecraftKansio);
			ulottuvuudenMaaritysAloitettu = etsiUlottuvuudetTiedostosta(kansio, ulottuvuudenMaaritysAloitettu);
		}
	}
	
	private static boolean etsiUlottuvuudetTiedostosta(File kansio, boolean ulottuvuudenMaaritysAloitettu) {
		if (!kansio.isDirectory()) {
			System.out.println("Folder could not be found:\n" + kansio.getAbsolutePath() + "/");
			return false;
		}
		for (File tiedosto : kansio.listFiles()) {
			if (tiedosto.getName().matches("-?\\d+\\.-?\\d+\\.png") || tiedosto.getName().matches("-?\\d+\\,-?\\d+\\.png")) {
				String[] osat = tiedosto.getName().split("[\\.\\,]");
				laajennaUlottuvuuksia(Integer.parseInt(osat[0]), Integer.parseInt(osat[1]), ulottuvuudenMaaritysAloitettu);
				ulottuvuudenMaaritysAloitettu = true;
			}
		}
		
		return ulottuvuudenMaaritysAloitettu;
	}
	
	private static void laajennaUlottuvuuksia(int x, int y, boolean ulottuvuudenMaaritysAloitettu) {
		if (ulottuvuudenMaaritysAloitettu == false) {
			x0 = x;
			x1 = x;
			y0 = y;
			y1 = y;
		}
		else {
			if (x < x0)
				x0 = x;
			if (x > x1)
				x1 = x;
			if (y < y0)
				y0 = y;
			if (y > y1)
				y1 = y;
		}
	}
	
	private static void annaKatseluoikeuksia(String kansiopolku) {
		File kansio = new File(kansiopolku);
		//kansio.setExecutable(true, false);
		for (File tiedosto : kansio.listFiles()) {
			if (tiedosto.getName().matches("-?\\d+\\.-?\\d+\\.png") || tiedosto.getName().matches("-?\\d+\\,-?\\d+\\.png")) {
				tiedosto.setReadable(true, false);
			}
		}
	}
	
	private static void luoTekstitValmiiksi() {
		paikat = new HashMap<String, ArrayList<String>>();
		
		Scanner lukija = null;
		int tarkeysaste = 1;
		try {
			lukija = new Scanner(new File(maailma + ".txt"));
			ArrayList<String> lista = new ArrayList<String>();
			while (lukija.hasNextLine()) {
				String rivi = lukija.nextLine();
				String[] osat = rivi.split(" ");
				if (osat.length == 2) {
					paikat.put(rivi, lista);
					lista = new ArrayList<String>();
					tarkeysaste = 1;
				}
				else if (rivi.matches("\\d+")) {
					tarkeysaste = Integer.parseInt(rivi);
				}
				else {
					lista.add(teePaikka( Integer.parseInt(osat[0]), Integer.parseInt(osat[1]), haePaikanNimi(rivi), tarkeysaste));
				}
			}
		} catch (FileNotFoundException ffe) {
			//System.out.println("File could not be found:\n" + maailma + ".txt");
		} catch (Exception e) {
			System.out.println("The names and coordinates of places could not be read in file: " + maailma + ".txt");
			e.printStackTrace();
		} finally {
			try {
				lukija.close();
			} catch (Exception e) {}
		}
	}
	
	private static String haePaikanNimi(String rivi) {
		int tokaVali = rivi.indexOf(' ', rivi.indexOf(' ')+1);
		return rivi.substring(tokaVali+1, rivi.length());
	}
	
	private static String teePaikka(int x, int y, String nimi, int tarkeysaste) {
		return "<p class=\"paikka prio" + tarkeysaste + "\" style=\"left:" + x + ";top:" + y + "\">" + nimi + "</p>" + "\n";
	}
	
	private static void kirjoitaNettisivu(FileWriter kirjuri) throws Exception {
		kirjoitaTiedostonAlku(kirjuri);
		
		kirjoitaKarttaTaulukko(kirjuri);
		
		kirjoitaTiedostonLoppu(kirjuri);
	}
	
	private static void kirjoitaTiedostonAlku(FileWriter kirjuri) throws Exception {
		kirjuri.append("<html>" + "\n\n");
		
		kirjuri.append("<head>" + "\n");
		kirjuri.append("<title>" + maailma + " Map</title>" + "\n");
		kirjuri.append("<META charset=utf-8>" + "\n");
		kirjuri.append("<link rel=\"stylesheet\" href=\"map.css\" type=\"text/css\" media=\"screen, print\" />" + "\n");
		// <meta http-equiv="refresh" content="0; url=http://example.com/" />
		kirjuri.append("<script type=\"text/javascript\" src=\"mapFunctions.js\"></script>\n");
		kirjuri.append("</head>" + "\n\n");
		
		kirjuri.append("<body onload=\"maxVisibility()\">" + "\n\n");
		
		kirjoitaToolsContainer(kirjuri);
		
		kirjuri.append("<table>" + "\n");
	}
	
	private static void kirjoitaToolsContainer(FileWriter kirjuri) throws Exception {
		kirjuri.append("<div id=\"toolsContainer\">\n");
		
		kirjoitaButton(kirjuri, "Toggle grid", "ToggleGrid");
		
		kirjuri.append("<br />" + "\n");
		kirjoitaButton(kirjuri, "Go to origo", "Origo");
		
		if (maailmaBaset.containsKey(maailma)) {
			kirjuri.append("<br />" + "\n");
			kirjoitaButton(kirjuri, "Go to base", "Base");
		}
		
		kirjuri.append("<br />" + "\n");
		kirjoitaButton(kirjuri, "Toggle prio", "toggleVisibility");
		
		kirjuri.append("<br />" + "\n");
		kirjoitaButton(kirjuri, "Reload", "forceReload");
		
		kirjuri.append("<p id=\"cursor\" style=\"font-size:10px; color:white\"></p>");
		kirjuri.append("</div>\n\n");
	}
	
	private static void kirjoitaButton(FileWriter kirjuri, String teksti, String metodi) throws Exception {
		kirjuri.append("<input type=\"button\" value=\"" + teksti + "\" onclick=\"" + metodi + "()\"/>" + "\n");
	}
	
	private static void kirjoitaKarttaTaulukko(FileWriter kirjuri) throws Exception {
		for (int y=y0; y<=y1; y++) {
			kirjuri.append("<tr>\n");
			for (int x=x0; x<=x1; x++) {
				kirjuri.append("<td ");
				if ((new File("MapMerge/" + maailma + "/MergeMap/" + x + "," + y + ".png")).isFile()) {
					imageCatalog.addImage(maailma, x, y);
					kirjuri.append("background=\"" + "MapMerge/" + maailma + "/MergeMap/" + x + "," + y + ".png\" ");
				}
				kirjuri.append( "style=\"background-repeat:no-repeat;" + "background-position: center center\">" + "\n");
				
				kirjuri.append("<div class=\"palanen\" onmousemove=\"getPos(event,this,' " + x + " " + y + "')\" onmouseout=\"stopTracking()\">" + "\n");
				
				kirjoitaAnchorit(kirjuri, x, y);
				//kirjoitaPaallekaisetKuvat(kirjuri, x, y, ".");
				//kirjoitaPaallekaisetKuvat(kirjuri, x, y, ",");
				kirjoitaPaikat(kirjuri, x, y);
				
				kirjuri.append("</div>" + "\n");
				kirjuri.append("</td>" + "\n");
			}
			kirjuri.append("</tr>" + "\n");
		}
	}
	
	private static void kirjoitaAnchorit(FileWriter kirjuri, int x, int y) throws Exception {
		if (x==0 && y==0) {
			kirjuri.append("<a id=\"origo\" />" + "\n");
		}
		
		if (maailmaBaset.containsKey(maailma)) {
			String baseKoordinaatti = maailmaBaset.get(maailma);
			if (baseKoordinaatti.replace( " ", "" ).equals("("+x+","+y+")")) {
				kirjuri.append("<a id=\"base\" />" + "\n");
			}
		}
	}
	
	private static void kirjoitaPaallekaisetKuvat(FileWriter kirjuri, int x, int y, String erottaja) throws Exception {
		for (String minecraftKansio : maailmaKansiot) {
			if (!(new File(minecraftKansio + x + erottaja + y + ".png")).exists()) {
				continue;
			}
			kirjuri.append("<img class=\"kuva\" src=");
			kirjuri.append("\"" + minecraftKansio);
			kirjuri.append(x + erottaja + y + ".png\">" + "\n");
		}
	}
	
	private static void kirjoitaPaikat(FileWriter kirjuri, int x, int y) throws Exception {
		kirjuri.append("<p class=\"paikka prio 1\" style=\"left:0;top:0\">[" + x + "," + y + "]</p>" + "\n");
		
		if (!paikat.containsKey(x + " " + y)) {
			return;
		}
		ArrayList<String> avainpaikat = paikat.get(x + " " + y);
		for (String avainpaikka : avainpaikat) {
			kirjuri.append(avainpaikka);
		}
	}
	
	private static void kirjoitaTiedostonLoppu(FileWriter kirjuri) throws Exception {
		kirjuri.append("</table>" + "\n");
		kirjuri.append("</body>" + "\n\n");
		
		kirjuri.append("</html>");
	}
	
	private static void luoCSS() {
		new ResourceWriter("map.css").copyResourceFile();
	}

	private static void luoJavaskripti() {
		new ResourceWriter("mapFunctions.js").copyResourceFile();
	}

	private static void luoKatselija() {
		new ResourceWriter("viewer.html").copyResourceFile();
	}

	private static void luoKatalogi() {
		final String images = imageCatalog.toJSON();

		FileWriter kirjuri = null;
		File katalogisivu = new File("images.js");
		try {
			poistaTiedosto(katalogisivu);

			kirjuri = new FileWriter(katalogisivu);
			kirjuri.append("var images = " + images + ";\n");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				kirjuri.close();
				katalogisivu.setReadable(true, false);
			} catch (Exception e) {}
		}
	}

	private static void luoKoostesivu() {
		FileWriter kirjuri = null;
		File koostesivu = new File("WorldPage.html");
		try {
			poistaTiedosto(koostesivu);
			
			kirjuri = new FileWriter(koostesivu);
			kirjoitaKoostesivu(kirjuri);
		} catch (Exception e) {
			System.out.println("FileWriter failed while writing WorldPage.html");
			e.printStackTrace();
		} finally {
			try {
				kirjuri.close();
				koostesivu.setReadable(true, false);
			} catch (Exception e) {}
		}
	}
	
	private static void kirjoitaKoostesivu(FileWriter kirjuri) throws Exception {
		kirjuri.append("<html>" + "\n");
		kirjuri.append("<head>" + "\n");
		kirjuri.append("<title>Minecraft Maps</title>" + "\n");
		kirjuri.append("<META charset=utf-8>" + "\n");
		kirjuri.append("<link rel=\"stylesheet\" href=\"map.css\" type=\"text/css\" media=\"screen, print\" />" + "\n");
		kirjuri.append("<base target=f2>");
		kirjuri.append("</head>" + "\n\n");
		
		kirjuri.append("<body>" + "\n");
		kirjuri.append("<h2>Minecraft Maps</h2>" + "\n");
		for (String maailma : maailmat) {
			kirjuri.append("<p><a href=\"" + maailma + ".html\">" + maailma + "</a>");
			kirjuri.append(" <a href=\"" + maailma + ".html#origo\" class=mini>[origo]</a>");
			if (maailmaBaset.containsKey(maailma)) {
				kirjuri.append(" <a href=\"" + maailma + ".html#base\" class=mini>[base]</a>");
			}
			kirjuri.append("</p>" + "\n");
		}
		
		kirjuri.append("</body>" + "\n\n");
		kirjuri.append("</html>");
	}
	
	private static void luoIndexsivu() {
		FileWriter kirjuri = null;
		File indexsivu = new File("index.html");
		try {
			poistaTiedosto(indexsivu);
			
			kirjuri = new FileWriter(indexsivu);
			kirjoitaIndexsivu(kirjuri);
		} catch (Exception e) {
			System.out.println("FileWriter failed while writing index.html");
			e.printStackTrace();
		} finally {
			try {
				kirjuri.close();
				indexsivu.setReadable(true, false);
			} catch (Exception e) {}
		}
	}
	
	private static void kirjoitaIndexsivu(FileWriter kirjuri) throws Exception {
		kirjuri.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Frameset//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd\">"+ "\n");
		kirjuri.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">" + "\n");
		kirjuri.append("<head>" + "\n");
		kirjuri.append("<title>Minecraft Maps</title>" + "\n");
		kirjuri.append("<META charset=\"utf-8\">" + "\n");
		kirjuri.append("</head>" + "\n\n");
		
		kirjuri.append("<frameset cols=\"20%,80%\">" + "\n");
		kirjuri.append("<frame src=\"WorldPage.html\" NAME=f1>" + "\n");
		kirjuri.append("<frame src=\"javascript:parent.blank()\" NAME=f2>" + "\n");
		kirjuri.append("<noframes>Sorry, your browser doesn't handle frames.</noframes>" + "\n");
		kirjuri.append("</frameset>" + "\n");
		
		kirjuri.append("<body />" + "\n\n");
		kirjuri.append("</html>");
	}
}
