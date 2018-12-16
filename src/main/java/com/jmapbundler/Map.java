package com.jmapbundler;

import java.util.List;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.jmapbundler.catalog.ImageCatalog;
import com.jmapbundler.configuration.*;

public class Map {

	public static void main(String[] args) {
		MapMerger karttapiirturi = new MapMerger();

		ConfigurationManager configuration = new ConfigurationManager("Basics.txt").etsiPohjatiedot();

		ImageCatalog catalog = new ImageCatalog();

		for (WorldConfiguration world : configuration.getWorlds()) {
			String maailma = world.getName();

			File tiedosto = new File(maailma + ".html");
			poistaTiedosto(tiedosto);

			WorldDimension dimension = etsiUlottuvuudet(world);
			karttapiirturi.liimaaKartatYhteen(maailma, world.getDirectories(), dimension.getMinX(), dimension.getMaxX(), dimension.getMinY(), dimension.getMaxY());
			annaKatseluoikeuksia("MapMerge/" + maailma + "/MergeMap/");

			FileWriter kirjuri = null;
			try {
				kirjuri = new FileWriter(tiedosto);
				kirjoitaNettisivu(kirjuri, world, dimension, catalog);
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
		if (configuration.getWorlds().size() > 1) {
			luoKoostesivu(configuration.getWorlds());
			luoIndexsivu();
		}
		luoKatselija();
		luoKatalogi(catalog);
	}

	private static void poistaTiedosto(File tiedosto) {
		if (tiedosto.exists()) {
			tiedosto.delete();
		}
	}

	private static WorldDimension etsiUlottuvuudet(WorldConfiguration world) {
		WorldDimension dimension = new WorldDimension();

		for (String worldDirectory : world.getDirectories()) {
			File kansio = new File(worldDirectory);
			etsiUlottuvuudetTiedostosta(kansio, dimension);
		}

		return dimension;
	}

	private static WorldDimension etsiUlottuvuudetTiedostosta(File kansio, WorldDimension dimension) {
		if (dimension == null) {
			dimension = new WorldDimension();
		}
		if (!kansio.isDirectory()) {
			System.out.println("Folder could not be found:\n" + kansio.getAbsolutePath() + "/");
			return dimension;
		}
		for (File tiedosto : kansio.listFiles()) {
			if (tiedosto.getName().matches("-?\\d+\\.-?\\d+\\.png") || tiedosto.getName().matches("-?\\d+\\,-?\\d+\\.png")) {
				String[] osat = tiedosto.getName().split("[\\.\\,]");

				dimension.addPoint(Integer.parseInt(osat[0]), Integer.parseInt(osat[1]));
			}
		}

		return dimension;
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

	private static void kirjoitaNettisivu(FileWriter kirjuri, WorldConfiguration world, WorldDimension dimension, ImageCatalog catalog) throws Exception {
		kirjoitaTiedostonAlku(kirjuri, world);

		kirjoitaKarttaTaulukko(kirjuri, world, dimension, catalog);

		kirjoitaTiedostonLoppu(kirjuri);
	}

	private static void kirjoitaTiedostonAlku(FileWriter kirjuri, WorldConfiguration world) throws Exception {
		kirjuri.append("<html>" + "\n\n");

		kirjuri.append("<head>" + "\n");
		kirjuri.append("<title>" + world.getName() + " Map</title>" + "\n");
		kirjuri.append("<META charset=utf-8>" + "\n");
		kirjuri.append("<link rel=\"stylesheet\" href=\"map.css\" type=\"text/css\" media=\"screen, print\" />" + "\n");
		// <meta http-equiv="refresh" content="0; url=http://example.com/" />
		kirjuri.append("<script type=\"text/javascript\" src=\"mapFunctions.js\"></script>\n");
		kirjuri.append("</head>" + "\n\n");

		kirjuri.append("<body onload=\"maxVisibility()\">" + "\n\n");

		kirjoitaToolsContainer(kirjuri, world);

		kirjuri.append("<table>" + "\n");
	}

	private static void kirjoitaToolsContainer(FileWriter kirjuri, WorldConfiguration world) throws Exception {
		kirjuri.append("<div id=\"toolsContainer\">\n");

		kirjoitaButton(kirjuri, "Toggle grid", "ToggleGrid");

		kirjuri.append("<br />" + "\n");
		kirjoitaButton(kirjuri, "Go to origo", "Origo");

		if (world.getBase() != null) {
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

	private static void kirjoitaKarttaTaulukko(FileWriter kirjuri, WorldConfiguration world, WorldDimension dimension, ImageCatalog catalog) throws Exception {
		String worldName = world.getName();
		for (int y = dimension.getMinY(); y <= dimension.getMaxY(); y++) {
			kirjuri.append("<tr>\n");
			for (int x = dimension.getMinX(); x <= dimension.getMaxX(); x++) {
				kirjuri.append("<td ");
				if ((new File("MapMerge/" + worldName + "/MergeMap/" + x + "," + y + ".png")).isFile()) {
					catalog.addImage(worldName, x, y);
					kirjuri.append("background=\"" + "MapMerge/" + worldName + "/MergeMap/" + x + "," + y + ".png\" ");
				}
				kirjuri.append( "style=\"background-repeat:no-repeat;" + "background-position: center center\">" + "\n");

				kirjuri.append("<div class=\"palanen\" onmousemove=\"getPos(event,this,' " + x + " " + y + "')\" onmouseout=\"stopTracking()\">" + "\n");

				kirjoitaAnchorit(kirjuri, world, x, y);
				//kirjoitaPaallekaisetKuvat(kirjuri, world, x, y, ".");
				//kirjoitaPaallekaisetKuvat(kirjuri, world, x, y, ",");
				kirjoitaPaikat(kirjuri, world, x, y);

				kirjuri.append("</div>" + "\n");
				kirjuri.append("</td>" + "\n");
			}
			kirjuri.append("</tr>" + "\n");
		}
	}

	private static void kirjoitaAnchorit(FileWriter kirjuri, WorldConfiguration world, int x, int y) throws Exception {
		if (x==0 && y==0) {
			kirjuri.append("<a id=\"origo\" />" + "\n");
		}

		if (world.getBase() != null) {
			String baseKoordinaatti = world.getBase();
			if (baseKoordinaatti.replace( " ", "" ).equals("("+x+","+y+")")) {
				kirjuri.append("<a id=\"base\" />" + "\n");
			}
		}
	}

	private static void kirjoitaPaikat(FileWriter kirjuri, WorldConfiguration world, int x, int y) throws Exception {
		kirjuri.append("<p class=\"paikka prio 1\" style=\"left:0;top:0\">[" + x + "," + y + "]</p>" + "\n");

		for (WorldPlace place : world.getTexts(x, y)) {
			kirjuri.append(teePaikka(place.getMinorX(), place.getMinorY(), place.getName(), place.getPriority()));
		}
	}

	private static String teePaikka(int x, int y, String nimi, int tarkeysaste) {
		return "<p class=\"paikka prio" + tarkeysaste + "\" style=\"left:" + x + ";top:" + y + "\">" + nimi + "</p>" + "\n";
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

	private static void luoKatalogi(ImageCatalog imageCatalog) {
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

	private static void luoKoostesivu(List<WorldConfiguration> worlds) {
		FileWriter kirjuri = null;
		File koostesivu = new File("WorldPage.html");
		try {
			poistaTiedosto(koostesivu);

			kirjuri = new FileWriter(koostesivu);
			kirjoitaKoostesivu(kirjuri, worlds);
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

	private static void kirjoitaKoostesivu(FileWriter kirjuri, List<WorldConfiguration> worlds) throws Exception {
		kirjuri.append("<html>" + "\n");
		kirjuri.append("<head>" + "\n");
		kirjuri.append("<title>Minecraft Maps</title>" + "\n");
		kirjuri.append("<META charset=utf-8>" + "\n");
		kirjuri.append("<link rel=\"stylesheet\" href=\"map.css\" type=\"text/css\" media=\"screen, print\" />" + "\n");
		kirjuri.append("<base target=f2>");
		kirjuri.append("</head>" + "\n\n");

		kirjuri.append("<body>" + "\n");
		kirjuri.append("<h2>Minecraft Maps</h2>" + "\n");
		for (WorldConfiguration world : worlds) {
			String maailma = world.getName();
			kirjuri.append("<p><a href=\"" + maailma + ".html\">" + maailma + "</a>");
			kirjuri.append(" <a href=\"" + maailma + ".html#origo\" class=mini>[origo]</a>");
			if (world.getBase() != null) {
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
