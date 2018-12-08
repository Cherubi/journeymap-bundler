package com.jmapbundler;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.TreeMap;
import javax.imageio.ImageIO;

public class MapMerger {
	
	
	public MapMerger() {
		varmistaKansionOlemassaolo("MapMerge/");
	}
	
	private void varmistaKansionOlemassaolo(String kansionNimi) {
		File mergeKansio = new File(kansionNimi);
		if (!mergeKansio.exists()) {
			mergeKansio.mkdir();
		}
	}
	
	public void liimaaKartatYhteen(String maailmanNimi, ArrayList<String> kansioidenNimet, int x_min, int x_max, int y_min, int y_max) {
		varmistaKansionOlemassaolo("MapMerge/" + maailmanNimi + "/");
		teeTarvittaessaBackUpKansiot(maailmanNimi, kansioidenNimet);
		if (maailmanNimi.endsWith("_nosnow")) {
			kansioidenNimet = luoLumettomatKuvat(maailmanNimi, kansioidenNimet, x_min, x_max, y_min, y_max);
		}
		
		for (int y = y_min; y <= y_max; y++) {
			for (int x = x_min; x <= x_max; x++) {
				TreeMap<Long, BufferedImage> karttakuvat = noudaKarttakuvat(kansioidenNimet, x, y);
				if (karttakuvat.size() == 0 || uusiaKuviaEiOle(karttakuvat, maailmanNimi, x, y)) continue;
				BufferedImage karttakooste = yhdistaKuvat(karttakuvat, x, y);
				paivitaKartta(karttakooste, maailmanNimi, kansioidenNimet, x, y);
				if (maailmanNimi.endsWith("_nosnow")) {
					karttakooste = liitaAlleTalvikartta(karttakooste, maailmanNimi, x, y);
				}
				tallennaKartta(karttakooste, maailmanNimi, x, y);
			}
		}
		
		teeUudetVarmuuskopiot(maailmanNimi, kansioidenNimet);
	}
	
	private void teeTarvittaessaBackUpKansiot(String maailmanNimi, ArrayList<String> kansioidenNimet) {
		varmistaKansionOlemassaolo("MapMerge/" + maailmanNimi + "/MergeMap/");
		varmistaKansionOlemassaolo("MapMerge/" + maailmanNimi + "/UpdateMap/");
		int i = 1;
		for (String kansionNimi : kansioidenNimet) {
			String[] polku = kansionNimi.split("/");
			String nimi = polku[ polku.length-1 ];
			varmistaKansionOlemassaolo("MapMerge/" + maailmanNimi + "/" + nimi + i + "/");
			i++;
		}
	}
	
	private ArrayList<String> luoLumettomatKuvat(String maailmanNimi, ArrayList<String> kansioidenNimet, int x_min, int x_max, int y_min, int y_max) {
		ArrayList<String> uudetNimet = luoLumettomatKansiot(maailmanNimi, kansioidenNimet);
		
		for (int x = x_min; x <= x_max; x++) {
			for (int y = y_min; y <= y_max; y++) {
				for (String kansionNimi : kansioidenNimet) {
					File lumiKuva = new File(kansionNimi + x + "," + y + ".png");
					String[] polku = kansionNimi.split("/");
					String nimi = polku[ polku.length-1 ];
					File kesaKuva = new File("MapMerge/" + maailmanNimi + "/new/" + nimi + "/" + x + "," + y + ".png");
					if (lumiKuva.exists()) {
						if (!kesaKuva.exists() || lumiKuva.lastModified() > kesaKuva.lastModified()) {
							try {
								luoLumetonKuva(lumiKuva, kesaKuva);
							} catch (Exception e) {
								System.out.println("Lumen poistaminen kuvasta epäonnistui: " + kansionNimi + " (" + x + ", " + y + ")");
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		
		return uudetNimet;
	}
	
	private ArrayList<String> luoLumettomatKansiot(String maailmanNimi, ArrayList<String> kansioidenNimet) {
		ArrayList<String> uudetNimet = new ArrayList<String>();
		varmistaKansionOlemassaolo("MapMerge/" + maailmanNimi + "/new/");
		
		for (String kansionimi : kansioidenNimet) {
			String[] polku = kansionimi.split("/");
			String nimi = polku[ polku.length-1 ];
			
			varmistaKansionOlemassaolo("MapMerge/" + maailmanNimi + "/new/" + nimi + "/");
			uudetNimet.add( "MapMerge/" + maailmanNimi + "/new/" + nimi + "/" );
		}
		return uudetNimet;
	}
	
	private void luoLumetonKuva(File lumitiedosto, File kesatiedosto) throws Exception {
		BufferedImage kuva = ImageIO.read( lumitiedosto );
		int leveys = kuva.getWidth();
		int korkeus = kuva.getHeight();
		int lapinakyva = (new Color(0, 0, 0, 0)).getRGB();
		
		for (int y=0; y<korkeus; y += 16) {
			for (int x=0; x<leveys; x += 16) {
				boolean talviChunkissa = chunkSisaltaaTalvea(kuva, x, y);
				
				if (talviChunkissa) {
					for (int j=0; j<16; j++) {
						for (int i=0; i<16; i++) {
							kuva.setRGB(x+i, y+j, lapinakyva);
						}
					}
				}
			}
		}
		
		ImageIO.write(kuva, "PNG", kesatiedosto);
	}
	
	private boolean chunkSisaltaaTalvea(BufferedImage kuva, int x, int y) {
		for (int j=0; j<16; j++) {
			for (int i=0; i<16; i++) {
				int rgb = kuva.getRGB(x+i, y+j);
				Color vari = new Color(rgb, true);
				if (vari.getAlpha() == 255 && (onLunta(vari) || onJaata(vari)) ) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean onLunta(Color vari) {
		return vari.getRed() == 255 && vari.getGreen() == 255 && vari.getBlue() == 255;
	}
	
	private boolean onJaata(Color vari) {
		int r = vari.getRed();
		int g = vari.getGreen();
		int b = vari.getBlue();
		
		if (r==121 && g==161 && b==218) return true;
		if (r==109 && g==150 && b==211) return true;
		if (r==204 && g==206 && b==211) return true;
		return false;
	}
	
	private TreeMap<Long, BufferedImage> noudaKarttakuvat(ArrayList<String> kansioidenNimet, int x, int y) {
		TreeMap<Long, BufferedImage> karttaMap = new TreeMap<Long, BufferedImage>();
		for (String kansionNimi: kansioidenNimet) {
			File karttakuva = new File(kansionNimi + x + "." + y + ".png");
			if (karttakuva.exists()) {
				lisaaKuvaMappiin(karttaMap, karttakuva);
			}
			else {
				karttakuva = new File(kansionNimi + x + "," + y + ".png");
				if (karttakuva.exists()) {
					lisaaKuvaMappiin(karttaMap, karttakuva);
				}
			}
		}
		
		return karttaMap;
	}
	
	private void lisaaKuvaMappiin(TreeMap<Long, BufferedImage> map, File kuva) {
		try {
			map.put( kuva.lastModified(), ImageIO.read(kuva) );
		} catch (Exception e) {
			System.out.println("Kuva: " + kuva.getName() + " ei ollut luettavissa (eikä lisättävissä kuvakollaasiin)");
			e.printStackTrace();
		}
	}
	
	private boolean uusiaKuviaEiOle(TreeMap<Long, BufferedImage> pelaajienKuvat, String maailmanNimi, int x, int y) {
		File viimeKooste = new File("MapMerge/" + maailmanNimi + "/MergeMap/" + x + "," + y + ".png");
		long viimePaivitys = viimeKooste.lastModified();
		for (long aikaleima : pelaajienKuvat.keySet()) {
			if (aikaleima > viimePaivitys) {
				return false;
			}
		}
		return true;
	}
	
	private BufferedImage yhdistaKuvat(TreeMap<Long, BufferedImage> karttakuvat, int x, int y) {
		int width = karttakuvat.firstEntry().getValue().getWidth();
		int height = karttakuvat.firstEntry().getValue().getHeight();
		BufferedImage kooste = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		Graphics g = kooste.getGraphics();
		for (BufferedImage kartta : karttakuvat.values()) {
			g.drawImage(kartta, 0, 0, null);
		}
		
		return kooste;
	}
	
	private void paivitaKartta(BufferedImage kartta, String maailmanNimi, ArrayList<String> kansioidenNimet, int x, int y) {
		BufferedImage paivitys = new BufferedImage(kartta.getWidth(), kartta.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = paivitys.getGraphics();
		int paivityksia = 0;
		
		try {
			paivityksia += haeVanhaPaivitysJosOn(paivitys, "MapMerge/" + maailmanNimi + "/UpdateMap/" + x + "," + y + ".png", g);
			int i = 1;
			for (String kansio : kansioidenNimet) {
				String[] polku = kansio.split("/");
				String kansionNimi = polku[ polku.length-1 ];
				if (onkoVanhempiaVarmuuskopioita("MapMerge/" + maailmanNimi + "/" + kansionNimi + i + "/" + x + "," + y + ".png", kansio + x + "," + y + ".png")) {
					paivityksia += tarkistaMuuttuneetPikselit("MapMerge/" + maailmanNimi + "/" + kansionNimi + i + "/" + x + "," + y + ".png", kansio + x + "," + y + ".png", g);
				}
				i++;
			}
			if (paivityksia > 0) {
				ImageIO.write(paivitys, "PNG", new File("MapMerge/" + maailmanNimi + "/UpdateMap/" + x + "," + y + ".png"));
				Graphics gKartta = kartta.getGraphics();
				gKartta.drawImage(paivitys, 0, 0, null);
			}
		} catch (Exception e) {
			System.out.println("Jokin meni vikaan päivityksiä tarkastellessa.");
			e.printStackTrace();
		}
	}
	
	private int haeVanhaPaivitysJosOn(BufferedImage paivitys, String tiedostonimi, Graphics g) throws Exception {
		File vanhaPaivitys = new File(tiedostonimi);
		if (vanhaPaivitys.exists()) {
			BufferedImage vanhaPaivityskuva = ImageIO.read(vanhaPaivitys);
			g.drawImage(vanhaPaivityskuva, 0, 0, null);
			return 1;
		}
		else {
			return 0;
		}
	}
	
	private boolean onkoVanhempiaVarmuuskopioita(String polku, String uusiPolku) {
		File tiedosto = new File(polku);
		if (!tiedosto.exists()) {
			return false;
		}
		File uusiTiedosto = new File(uusiPolku);
		if (tiedosto.lastModified() < uusiTiedosto.lastModified()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	private int tarkistaMuuttuneetPikselit(String backupSijainti, String uusiSijainti, Graphics g) throws Exception {
		int muutokset = 0;
		BufferedImage vanha = ImageIO.read(new File(backupSijainti));
		BufferedImage uusi = ImageIO.read(new File(uusiSijainti));
		int width = uusi.getWidth();
		int height = uusi.getHeight();
		
		for (int y=0; y<height; y++) {
			for (int x=0; x<width; x++) {
				int rgbVanha = vanha.getRGB(x, y);
				int rgbUusi = uusi.getRGB(x, y);
				if (rgbVanha != rgbUusi) {
					Color variVanha = new Color(rgbVanha, true);
					Color variUusi = new Color(rgbUusi, true);
					if (variUusi.getAlpha() == 0) continue;
					
					if (Math.abs(variVanha.getAlpha()-variUusi.getAlpha())>20 || Math.abs(variVanha.getRed()-variUusi.getRed())>20 || Math.abs(variVanha.getGreen()-variUusi.getGreen())>20 || Math.abs(variVanha.getBlue()-variUusi.getBlue())>20) {
						g.setColor(variUusi);
						g.fillRect(x, y, 1, 1);
						muutokset++;
					}
				}
			}
		}
		
		return muutokset;
	}
	
	private BufferedImage liitaAlleTalvikartta(BufferedImage kesakuva, String maailmanNimi, int x, int y) {
		int width = kesakuva.getWidth();
		int height = kesakuva.getHeight();
		BufferedImage yhdistelma = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = yhdistelma.getGraphics();
		
		try {
			File talvikooste = new File("MapMerge/" + maailmanNimi.substring(0, maailmanNimi.length() - "_nosnow".length()) + "/MergeMap/" + x + "," + y + ".png");
			BufferedImage talvikuva = ImageIO.read(talvikooste);
			
			g.drawImage(talvikuva, 0, 0, null);
			g.drawImage(kesakuva, 0, 0, null);
		} catch (Exception e) {
			System.out.println("Lumet sisältävää koostekuvaa ei saatu ladattua. (" + x + "," + y + ")");
			e.printStackTrace();
		}
		
		return yhdistelma;
	}
	
	private void tallennaKartta(BufferedImage karttakooste, String maailmanNimi, int x, int y) {
		try {
			File karttatiedosto = new File("MapMerge/" + maailmanNimi + "/MergeMap/" + x + "," + y + ".png");
			ImageIO.write(karttakooste, "PNG", karttatiedosto);
		} catch (Exception e) {
			System.out.println("Koostekuvaa ei onnistuttu luomaan");
			e.printStackTrace();
		}
	}
	
	//aina x,y.png -notaatiolla, ei x.y.png
	private void teeUudetVarmuuskopiot(String maailmanNimi, ArrayList<String> kansioidenNimet) {
		try {
			int n = 1;
			for (String kansionimi : kansioidenNimet) {
				String[] polku = kansionimi.split("/");
				String lyhytKansionimi = polku[ polku.length-1 ];
				File kansio = new File(kansionimi);
				if (kansio.exists() && kansio.isDirectory()) {
					
					String[] tiedostot = kansio.list();
					for (int i=0; i<tiedostot.length; i++) {
						if (tiedostot[i].endsWith(".png")) {
							BufferedImage kuva = ImageIO.read( new File(kansionimi + tiedostot[i]));
							tiedostot[i] = tiedostot[i].replace('.', ',');
							tiedostot[i] = tiedostot[i].replace(",png", ".png");
							ImageIO.write(kuva, "PNG", new File("MapMerge/" + maailmanNimi + "/" + lyhytKansionimi + n + "/" + tiedostot[i]));
						}
					}
					
				}
				n++;
			}
		} catch (Exception e) {
			System.out.println("Varmuuskopioita tehdessä sattui virhe.");
			e.printStackTrace();
		}
	}
}