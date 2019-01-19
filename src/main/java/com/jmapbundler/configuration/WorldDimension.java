package com.jmapbundler.configuration;

public class WorldDimension {

	private boolean isStarted = false;

	private int minX;
	private int maxX;
	private int minY;
	private int maxY;

	public void addPoint(int x, int y) {
		if (isStarted) {
			this.minX = Math.min(x, this.minX);
			this.maxX = Math.max(x, this.maxX);
			this.minY = Math.min(y, this.minY);
			this.maxY = Math.max(y, this.maxY);
		} else {
			this.minX = x;
			this.maxX = x;
			this.minY = y;
			this.maxY = y;
			this.isStarted = true;
		}
	}

	public int getMinX() {
		return this.minX;
	}

	public int getMaxX() {
		return this.maxX;
	}

	public int getMinY() {
		return this.minY;
	}

	public int getMaxY() {
		return this.maxY;
	}

}
