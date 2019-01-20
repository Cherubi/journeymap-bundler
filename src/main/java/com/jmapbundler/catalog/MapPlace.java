package com.jmapbundler.catalog;

import com.jmapbundler.catalog.json.*;
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
		return new JsonObject()
				.addField("world", this.mapName)
				.addField("majorX", this.place.getMajorX())
				.addField("majorY", this.place.getMajorY())
				.addField("minorX", this.place.getMinorX())
				.addField("minorY", this.place.getMinorY())
				.addField("priority", this.place.getPriority())
				.addField("name", this.place.getName())
				.toJSON();
	}

}
