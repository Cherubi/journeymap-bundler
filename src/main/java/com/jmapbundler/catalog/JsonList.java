package com.jmapbundler.catalog;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class JsonList implements Jsonable {

	private List<Jsonable> items = new ArrayList<>();

	protected void add(Jsonable ...items) {
		for (Jsonable item : items) {
			this.items.add(item);
		}
	}

	@Override
	public String toJSON() {
		final String list = this.items.stream()
				.map(Jsonable::toJSON)
				.collect(Collectors.joining(","));

		return new StringBuilder().append('[').append(list).append(']').toString();
	}

}
