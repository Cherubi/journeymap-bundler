package com.jmapbundler.catalog;

public class MapImage {

	private final String mapName;
	private final Integer x;
	private final Integer y;

	public MapImage(String mapName, int x, int y) {
		this.mapName = mapName;
		this.x = x;
		this.y = y;
	}

	public String toJSON() {
		return "{name:'" + this.mapName + "',x:" + this.x + ",y:" + this.y + "}";
	}

}
