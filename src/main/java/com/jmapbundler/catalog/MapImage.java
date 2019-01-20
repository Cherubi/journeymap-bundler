package com.jmapbundler.catalog;

import com.jmapbundler.catalog.json.*;

public class MapImage implements Jsonable {

	private final String mapName;
	private final Integer x;
	private final Integer y;

	public MapImage(String mapName, int x, int y) {
		this.mapName = mapName;
		this.x = x;
		this.y = y;
	}

	@Override
	public String toJSON() {
		return new JsonObject()
				.addField("name", this.mapName)
				.addField("x", this.x)
				.addField("y", this.y)
				.toJSON();
	}

}
