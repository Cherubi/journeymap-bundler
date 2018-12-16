package com.jmapbundler.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class ConfigurationManager {

	private final List<WorldConfiguration> worlds = new ArrayList<>();
	private final String configurationFilename;

	public ConfigurationManager() {
		this("Basics.txt");
	}

	public ConfigurationManager(String filename) {
		this.configurationFilename = filename;
	}

	public List<WorldConfiguration> getWorlds() {
		return this.worlds;
	}

	public ConfigurationManager etsiPohjatiedot() {
		Scanner lukija = null;

		try {
			lukija = new Scanner(new File(this.configurationFilename));
			luePohjatiedot(lukija);
		} catch (Exception e) {
			e.printStackTrace();
			if (worlds.size() == 0) {
				alustaTiedostotiedot();
			}
		} finally {
			try {
				lukija.close();
			} catch(Exception e) {}
		}

		for (WorldConfiguration world : this.worlds) {
			this.initializeTexts(world);
		}

		return this;
	}

	private void luePohjatiedot(Scanner lukija) {
		while (lukija.hasNextLine()) {
			String rivi = lukija.nextLine();
			if (rivi.length()==0)
				break;
			WorldConfiguration world = new WorldConfiguration(rivi);
			worlds.add(world);
			List<String> maailmaKansiot = world.getDirectories();
			while (lukija.hasNextLine()) {
				rivi = lukija.nextLine();
				if (rivi.length() == 0) {
					break;
				}
				if (rivi.startsWith("(")) {
					world.setBase(rivi);
					continue;
				}
				maailmaKansiot.add(teeKansiopolku(rivi));
			}
		}
	}

	private String teeKansiopolku(String annettuOsoite) {
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

	private void alustaTiedostotiedot() {
		if (JOptionPane.showConfirmDialog(null, "Do you want to input your world files?", "Minecraft Maps", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			String worldName = JOptionPane.showInputDialog("Decide world name (map id):");
			WorldConfiguration world = new WorldConfiguration(worldName);
			this.worlds.add(world);
			world.getDirectories().add(JOptionPane.showInputDialog("Write whole path to z1 pictures:"));

			luoPerustiedosto(world.getName(), world.getDirectories().get(0));
		}
	}

	private void poistaTiedosto(File tiedosto) {
		if (tiedosto.exists()) {
			tiedosto.delete();
		}
	}

	private void luoPerustiedosto(String maailmanNimi, String osoite) {
		File tiedosto = new File(this.configurationFilename);
		poistaTiedosto(tiedosto);
		FileWriter kirjuri = null;
		try {
			kirjuri = new FileWriter(tiedosto);
			kirjuri.append(maailmanNimi + "\n");
			kirjuri.append(osoite + "\n\n");
			kirjoitaOhjeet(kirjuri);
		} catch (Exception e) {
			System.out.println("Creating " + this.configurationFilename + " failed.");
			e.printStackTrace();
		} finally {
			try {
				kirjuri.close();
			} catch (Exception e) {}
		}
	}

	private void kirjoitaOhjeet(FileWriter kirjuri) throws Exception {
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

	private void initializeTexts(WorldConfiguration world) {
		int x = 0;
		int y = 0;
		Scanner lukija = null;
		int priority = 1;
		try {
			lukija = new Scanner(new File(world.getName() + ".txt"));
			while (lukija.hasNextLine()) {
				String rivi = lukija.nextLine();
				String[] osat = rivi.split(" ");
				if (osat.length == 2) {
					x = Integer.parseInt(osat[0]);
					y = Integer.parseInt(osat[1]);
					priority = 1;
				}
				else if (rivi.matches("\\d+")) {
					priority = Integer.parseInt(rivi);
				}
				else {
					world.addText(x, y, Integer.parseInt(osat[0]), Integer.parseInt(osat[1]), osat[2], priority);
				}
			}
		} catch (FileNotFoundException ffe) {
			//System.out.println("File could not be found:\n" + maailma + ".txt");
		} catch (Exception e) {
			System.out.println("The names and coordinates of places could not be read in file: " + world.getName() + ".txt");
			e.printStackTrace();
		} finally {
			try {
				lukija.close();
			} catch (Exception e) {}
		}
	}

}
