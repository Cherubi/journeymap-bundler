package com.jmapbundler.catalog;

import com.jmapbundler.configuration.WorldPlace;

public class MapPlace implements Jsonable {

	private final String mapName;
	private final WorldPlace place;

	public MapPlace(final String mapName, final WorldPlace place) {
		this.mapName = mapName;
		this.place = place;
	}

	@Override
	public String toJSON() {
		return "{world:'" + this.mapName + "',"
				+ "majorX:" + this.place.getMajorX() + ","
				+ "majorY:" + this.place.getMajorY() + ","
				+ "minorX:" + this.place.getMinorX() + ","
				+ "minorY:" + this.place.getMinorY() + ","
				+ "priority:" + this.place.getPriority() + ","
				+ "name:'" + this.place.getName() + "'}";
	}

}
