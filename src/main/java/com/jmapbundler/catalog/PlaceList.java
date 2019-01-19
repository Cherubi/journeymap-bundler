package com.jmapbundler.catalog;

import java.util.List;

import com.jmapbundler.configuration.WorldPlace;

public class PlaceList extends JsonList implements Jsonable {

	public void add(String worldName, WorldPlace place) {
		this.add(new MapPlace(worldName, place));
	}

	public void add(String worldName, List<WorldPlace> places) {
		for (WorldPlace place : places) {
			this.add(worldName, place);
		}
	}

}
