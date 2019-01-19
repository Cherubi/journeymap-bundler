package com.jmapbundler.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WorldConfiguration {

	private String base;

	private final String name;
	private final List<String> directories = new ArrayList<>();
	private final List<WorldPlace> places = new ArrayList<>();

	public WorldConfiguration(String worldName) {
		this.name = worldName;
	}

	public String getBase() {
		return this.base;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public String getName() {
		return this.name;
	}

	public List<String> getDirectories() {
		return this.directories;
	}

	public void addText(int majorX, int majorY, int minorX, int minorY, String name, int priority) {
		this.places.add(new WorldPlace(majorX, majorY, minorX, minorY, name, priority));
	}

	public List<WorldPlace> getTexts(int majorX, int majorY) {
		return this.places.stream()
				.filter((place) -> place.isInMajor(majorX, majorY))
				.collect(Collectors.toList());
	}

}
