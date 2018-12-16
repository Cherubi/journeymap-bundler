package com.jmapbundler.configuration;

public class WorldPlace {

	private final int majorX;
	private final int majorY;

	private final int minorX;
	private final int minorY;

	private final String name;
	private final int priority;

	public WorldPlace(int majorX, int majorY, int minorX, int minorY, String name, int priority) {
		this.majorX = majorX;
		this.majorY = majorY;
		this.minorX = minorX;
		this.minorY = minorY;
		this.name = name;
		this.priority = priority;
	}

	public int getMinorX() {
		return this.minorX;
	}

	public int getMinorY() {
		return this.minorY;
	}

	public String getName() {
		return this.name;
	}

	public int getPriority() {
		return this.priority;
	}

	public boolean isInMajor(int x, int y) {
		return this.majorX == x && this.majorY == y;
	}

}
