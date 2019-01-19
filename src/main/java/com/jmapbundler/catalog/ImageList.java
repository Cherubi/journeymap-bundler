package com.jmapbundler.catalog;

public class ImageList extends JsonList implements Jsonable {

	public void addImage(String worldName, int x, int y) {
		this.add(new MapImage(worldName, x, y));
	}

}
