package com.jmapbundler.catalog.json;

import java.util.HashMap;
import java.util.stream.Collectors;

public class JsonObject implements Jsonable {

	private final HashMap<String, Jsonable> fields = new HashMap<>();

	public JsonObject addField(final String name, final Jsonable any) {
		this.fields.put(name, any);
		return this;
	}

	public JsonObject addField(final String name, final String string) {
		this.fields.put(name, new JsonPrimitive(string));
		return this;
	}

	public JsonObject addField(final String name, final Number number) {
		this.fields.put(name, new JsonPrimitive(number));
		return this;
	}

	public JsonObject addField(final String name, final Boolean bool) {
		this.fields.put(name, new JsonPrimitive(bool));
		return this;
	}

	@Override
	public String toJSON() {
		final String content = this.fields
				.entrySet()
				.stream()
				.map((pair) -> pair.getKey() + ":" + pair.getValue().toJSON())
				.collect(Collectors.joining(","));
		return "{" + content + "}";
	}

	private class JsonPrimitive implements Jsonable {

		private final String data;

		public JsonPrimitive(String string) {
			this.data = "'" + string.replace("'", "\\'") + "'";
		}

		public JsonPrimitive(Number number) {
			this.data = number.toString();
		}

		public JsonPrimitive(Boolean bool) {
			this.data = bool ? "true" : "false";
		}

		@Override
		public String toJSON() {
			return this.data;
		}

	}

}
