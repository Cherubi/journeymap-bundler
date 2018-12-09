package com.jmapbundler.catalog;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ImageCatalog {

	private final List<MapImage> mapImages = new ArrayList<>();

	public void addImage(String worldName, int x, int y) {
		this.mapImages.add(new MapImage(worldName, x, y));
	}

	public String toJSON() {
		final String list = this.mapImages.stream()
				.map(MapImage::toJSON)
				.collect(Collectors.joining(","));

		return new StringBuilder().append('[').append(list).append(']').toString();
	}

}
