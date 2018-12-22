package com.jmapbundler;

import java.io.File;
import java.io.PrintStream;

import com.jmapbundler.catalog.ImageList;
import com.jmapbundler.catalog.PlaceList;
import com.jmapbundler.configuration.*;
import com.jmapbundler.console.Console;

public class Map {

	public static void main(String[] args) {
		final Console console = new Console();
		PrintStream ps = console.getPrintStream();
		System.setOut(ps);
		System.setErr(ps);
		try {
			generateMaps();
		} catch (Exception e) {
			e.printStackTrace(ps);
		}
	}

	private static void generateMaps() {
		MapMerger karttapiirturi = new MapMerger();

		ConfigurationManager configuration = new ConfigurationManager("Basics.txt").etsiPohjatiedot();

		ImageList images = new ImageList();
		PlaceList places = new PlaceList();

		for (WorldConfiguration world : configuration.getWorlds()) {
			String maailma = world.getName();

			WorldDimension dimension = etsiUlottuvuudet(world);
			karttapiirturi.liimaaKartatYhteen(maailma, world.getDirectories(), dimension.getMinX(), dimension.getMaxX(), dimension.getMinY(), dimension.getMaxY());
			annaKatseluoikeuksia("MapMerge/" + maailma + "/MergeMap/");

			crawlWorld(world, dimension, images, places);
		}

		final String imageData = "var images = " + images.toJSON() + ";\n";
		new ResourceWriter("images.js", imageData).writeFile();

		final String placeData = "var places = " + places.toJSON() + ";\n";
		new ResourceWriter("places.js", placeData).writeFile();

		copyResourceFiles("map.css", "mapFunctions.js", "viewer.html");
	}

	private static void copyResourceFiles(String ...fileNames) {
		for (final String fileName : fileNames) {
			new ResourceWriter(fileName).writeFile();
		}
	}

	private static void crawlWorld(WorldConfiguration world, WorldDimension dimension, ImageList images, PlaceList places) {
		String worldName = world.getName();
		for (int x = dimension.getMinX(); x <= dimension.getMaxX(); x += 1) {
			for (int y = dimension.getMinY(); y <= dimension.getMaxY(); y += 1) {
				if ((new File("MapMerge/" + worldName + "/MergeMap/" + x + "," + y + ".png")).isFile()) {
					images.addImage(worldName, x, y);
				}
				places.add(worldName, world.getTexts(x, y));
			}
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

}
